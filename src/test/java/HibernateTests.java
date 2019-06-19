import entity.Child;
import entity.Customer;
import entity.Gender;
import entity.Parent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateTests {

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
    public void testEnumConversion() {
        Set<Integer> parentIds = createTestData(2, 2);
        Integer parentId = parentIds.iterator().next();
        em.clear();

        em.createNativeQuery("update parent SET gender = 'm' where 1=1").executeUpdate();

        Parent parent = em.find(Parent.class, parentId);
        Assert.assertEquals(Gender.MALE, parent.getGender());
    }

    @Test
    public void testHibernateMerge() {
        // Put some test data into the database and clear the session
        Set<Integer> parentIds = createTestData(1, 3);
        em.clear();

        int parentId = parentIds.iterator().next();
        Parent parent = em.find(Parent.class, parentId);
        Set<Child> children = parent.getChildrenLazy();

        // Create new unattached children objects with different names
        List<Child> copies = children.stream()
                .map(child -> {
                    Child copy = new Child(child);
                    copy.setName("John" + child.getChildId());
                    return copy;
                })
                .collect(Collectors.toList());

        log.info("Merging copies into session...");
        copies.forEach(copy -> {
            em.merge(copy);
        });

        log.info("Merge complete.");
    }

    // ************************************************************************
    // Helpers to create test data defined below
    // ************************************************************************

    protected Set<Integer> createTestData(int numParents, int childrenPerParent) {

        Set<Integer> parentIds = new LinkedHashSet<>();

        //
        // Prep some data in the DB
        //
        for (int i = 0; i < numParents; i++) {
            Parent parent = createParent("Parent_" + i);
            int parentId = parent.getParentId();
            parentIds.add(parentId);

            // Create some children for each parent...
            for (int j = 0; j < childrenPerParent; j++) {
                createChild("Child_" + i + "_" + j, 15, parent);
            }
        }

        return parentIds;
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
