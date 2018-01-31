import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RefreshTest {

    private EntityManager em;

    @Before
    public void testSetup() {
        em = HibernateTools.getEntityManager();
    }

    @After
    public void testEnd() {
        em.getTransaction().rollback();
        em.close();
        em.getEntityManagerFactory().close();
    }

    @Test
    public void testLazyCollectionAfterBatchFetchRefreshLock() {
        final int numParents = 5;
        final int childrenPerParent = 2;
        int parentId = 0;

        //
        // Prep some data in the DB
        //
        for (int i = 0; i < numParents; i++) {
            Parent parent = createParent("Parent_" + i);
            parentId = parent.getParentId();

            // Create some children for each parent...
            for (int j = 0; j < childrenPerParent; j++) {
                createChild("Child_" + i + "_" + j, 15, parent);
            }
        }

        // Clear the session
        em.clear();

        // Retrieve one of the parents into the session.
        Parent parent = em.find(Parent.class, parentId);
        Assert.assertNotNull(parent);

        // Retrieve children but keep their parents lazy!
        // This allows batch fetching to do its thing when we refresh below.
        em.createQuery("FROM Child").getResultList();

        em.refresh(parent, LockModeType.PESSIMISTIC_WRITE);

        // Just something to force delazification of children on parent entity
        // The parent is obviously attached to the session (we just refreshed it!)
        parent.getChildren().size();

        // Another interesting thing to note - em.getLockMode returns an incorrect value after the above refresh
        Assert.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(parent));
    }

    @Test
    public void testLockModeAfterRefresh() {
        Integer customerId = null;

        {
            // Create a record in DB
            Customer customer = createCustomer("John Doe");
            customerId = customer.getCustomerId();
            em.clear();
        }

        {
            // Retrieve record with lock
            Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);
            Assert.assertNotNull(customer);
            Assert.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(customer));

            em.refresh(customer);
            Assert.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(customer));
        }
    }

    protected Customer createCustomer(String name) {
        Customer customer = new Customer();
        customer.setName("John Doe");

        em.persist(customer);
        em.flush();
        return customer;
    }

    protected Parent createParent(String name) {
        Parent parent = new Parent();
        parent.setName(name);

        em.persist(parent);
        em.flush();
        return parent;
    }

    protected Child createChild(String name, int age, Parent parent) {
        Child child = new Child();
        child.setName(name);
        child.setAge(age);
        child.setParent(parent);

        em.persist(child);
        em.flush();
        return child;
    }

}
