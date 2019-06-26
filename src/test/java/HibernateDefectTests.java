import entity.Child;
import entity.Customer;
import entity.Parent;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateDefectTests {

    private Logger log = LoggerFactory.getLogger(HibernateTests.class);

    private EntityManager em;

    @Before
    public void testSetup() {
        em = HibernateTools.getEntityManager();
        log.info("****************************** TEST START ******************************");
    }

    @After
    public void testEnd() {
        log.info("Flushing changes before rollback...");
        em.flush();
        log.info("******************************* TEST END *******************************");
        em.getTransaction().rollback();
        em.close();
        HibernateTools.getEntityManagerFactory().close();
    }

    @Test
    public void testManyToOneEagerMapping() {
        Set<Integer> parentIds = createTestData(1, 5);
        int parentId = parentIds.iterator().next();

        // Retrieve parent from session cache and refresh prior to clear - we'll see only 5 children
        Parent parent = em.find(Parent.class, parentId);
        em.refresh(parent);
        Assert.assertEquals(5, parent.getChildrenEager().size());

        // However, after clearing and forcing a reload... things go wonky.
        // This only occurs if batch fetching is enabled!
        em.clear();
        parent = em.find(Parent.class, parentId);
        Assert.assertEquals(5, parent.getChildrenEager().size());
    }

    @Test
    public void testLazyCollectionAfterBatchFetchRefreshLock() {

        // Create some test data
        Set<Integer> parentIds = createTestData(5, 2);
        int parentId = parentIds.iterator().next();

        // Clear the session
        em.clear();

        // Retrieve one of the parents into the session.
        Parent parent = em.find(Parent.class, parentId);
        Assert.assertNotNull(parent);

        // Retrieve children but keep their parents lazy!
        // This allows batch fetching to do its thing when we refresh below.
        em.createQuery("FROM Child").getResultList();

        em.refresh(parent, LockModeType.PESSIMISTIC_WRITE);

        // Another interesting thing to note - em.getLockMode returns an incorrect value after the above refresh
        Assert.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(parent));

        // Just something to force delazification of children on parent entity
        // The parent is obviously attached to the session (we just refreshed it!)
        parent.getChildrenLazy().size();
    }

    @Test
    public void testLockModeAfterRefresh() {
        int customerId = 0;

        {
            // Create a record in DB
            Customer customer = createCustomer(customerId, "John Doe");
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

    // ************************************************************************
    // Helpers to create test data defined below
    // ************************************************************************

    protected Set<Integer> createTestData(int numParents, int childrenPerParent) {

        Set<Integer> parentIds = new LinkedHashSet<>();

        //
        // Prep some data in the DB
        //
        for (int parentId = 0; parentId < numParents; parentId++) {
            Parent parent = createParent(parentId, "Parent_" + parentId);
            parentIds.add(parentId);

            // Create some children for each parent...
            for (int i = 0; i < childrenPerParent; i++) {
                int childId = parentId * childrenPerParent + i;
                createChild(childId, "Child_" + parentId + "_" + i, 15, parent);
            }
        }

        return parentIds;
    }

    protected Customer createCustomer(int customerId, String name) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setName("John Doe");

        em.persist(customer);
        em.flush();
        return customer;
    }

    protected Parent createParent(int parentId, String name) {
        Parent parent = new Parent();
        parent.setParentId(parentId);
        parent.setName(name);

        em.persist(parent);
        em.flush();
        return parent;
    }

    protected Child createChild(int childId, String name, int age, Parent parent) {
        Child child = new Child();
        child.setChildId(childId);
        child.setName(name);
        child.setAge(age);
        child.setParent(parent);

        em.persist(child);
        em.flush();
        return child;
    }

}
