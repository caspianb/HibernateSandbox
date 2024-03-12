import entity.Child;
import entity.Parent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class HibernateDefectTests {

    private final EntityManager em;

    HibernateDefectTests() {
        this.em = HibernateTools.getEntityManager();
        log.info("****************************** TEST START ******************************");
    }

    @AfterEach
    void testEnd() {
        log.info("Flushing changes before rollback...");
        em.flush();
        log.info("******************************* TEST END *******************************");
        em.getTransaction().rollback();
        em.close();
        HibernateTools.getEntityManagerFactory().close();
    }

    /**
     * 5.2.12 - PASS
     * 5.3.17 - PASS
     * 5.4.15 - PASS
     * 5.4.20 - PASS
     */
    @Test
    void testManyToOneEagerMapping() {
        Set<Integer> parentIds = createTestData(1, 5);
        int parentId = parentIds.iterator().next();

        // Retrieve parent from session cache and refresh prior to clear - we'll see only 5 children
        Parent parent = em.find(Parent.class, parentId);
        em.refresh(parent);
        Assertions.assertEquals(5, parent.getChildrenEager().size());

        // However, after clearing and forcing a reload... things go wonky.
        // This only occurs if batch fetching is enabled!
        em.clear();
        parent = em.find(Parent.class, parentId);
        Assertions.assertEquals(5, parent.getChildrenEager().size());
    }

    /**
     * https://hibernate.atlassian.net/browse/HHH-12268
     * 5.2.12 - FAIL
     * 5.3.17 - FAIL
     * 5.4.15 - FAIL
     * 5.4.20 - PASS
     */
    @Test
    void testLazyCollectionAfterBatchFetchRefreshLock() {

        // Create some test data
        Set<Integer> parentIds = createTestData(5, 2);
        int parentId = parentIds.iterator().next();

        // Clear the session
        em.clear();

        // Retrieve one of the parents into the session.
        Parent parent = em.find(Parent.class, parentId);
        Assertions.assertNotNull(parent);

        // Retrieve children but keep their parents lazy!
        // This allows batch fetching to do its thing when we refresh below.
        em.createQuery("FROM Child").getResultList();

        em.refresh(parent, LockModeType.PESSIMISTIC_WRITE);

        // TODO Another interesting thing to note - em.getLockMode returns an incorrect value after the above refresh
        Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(parent));

        // Just something to force delazification of children on parent entity
        // The parent is obviously attached to the session (we just refreshed it!)
        parent.getChildrenLazy().size();
    }

    /**
     * https://hibernate.atlassian.net/browse/HHH-13270
     * 5.2.12 - FAIL
     * 5.3.17 - PASS
     * 5.4.15 - PASS
     * 5.4.20 - PASS
     */
    @Test
    void testLockModeAfterRefresh() {
        int parentId = 0;

        {
            // Create a record in DB
            Parent parent = createParent(parentId, "Parent Name");
            em.clear();
        }

        {
            // Retrieve record with lock
            Parent customer = em.find(Parent.class, parentId, LockModeType.PESSIMISTIC_WRITE);
            Assertions.assertNotNull(customer);
            Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(customer));

            em.refresh(customer);
            Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(customer));
        }
    }

    /**
     * https://hibernate.atlassian.net/browse/HHH-14008
     * 5.3.17 - FAIL
     * 5.4.15 - PASS
     * 5.4.20 - PASS
     */
    @Test
    void testSharedCollectionExceptionAfterLockRefreshAndFlush() {
        // Create some test data and clear the session
        createTestData(3, 2);
        em.clear();

        // Read in parent0 first.
        Parent parent0 = em.find(Parent.class, 0);

        // Read in some a random children record NOT for parent0 (id > 2), but don't delazify it
        log.info("READING IN CHILD WITH LAZY PARENT SET ON IT.");
        Child otherChild = em.find(Child.class, 5);

        // Lock and refresh parent0; batch fetching will kick in and cause duplication of parent0 in session cache
        log.info("LOCKING AND REFRESHING PARENT-0; BATCH FETCH FOR PARENT2 SHOULD KICK IN");
        em.lock(parent0, LockModeType.PESSIMISTIC_WRITE);
        em.refresh(parent0);

        // Read in a child which points to parent0; then refresh parent0
        // The problem here, is child will get a reference to the *new* duplicate entity in the cache above
        Child child = em.find(Child.class, 0);

        // Refresh the original parent, reconnecting it to the session
        em.refresh(parent0);

        // And blow up with completely misleading error message
        em.flush();
    }

    // ************************************************************************
    // Helpers to create test data defined below
    // ************************************************************************

    Set<Integer> createTestData(int numParents, int childrenPerParent) {
        log.info("******************** CREATE TEST DATA ********************");
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

        log.info("******************** DONE TEST DATA ********************");
        return parentIds;
    }

    Parent createParent(int parentId, String name) {
        Parent parent = new Parent();
        parent.setParentId(parentId);
        parent.setName(name);

        em.persist(parent);
        em.flush();
        return parent;
    }

    Child createChild(int childId, String name, int age, Parent parent) {
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
