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

}
