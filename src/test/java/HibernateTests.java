import entity.Child;
import entity.Gender;
import entity.Parent;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        try {
            log.info("Flushing changes before rollback...");
            em.flush();
        }
        catch (Exception e) {
            log.error("Exception detected on flush: ", e);
        }
        log.info("******************************* TEST END *******************************");
        em.clear();
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

        parent.setGender(Gender.UNKNOWN);
        em.flush();
        em.clear();

        parent = em.find(Parent.class, parentId);
        Assert.assertEquals(Gender.UNKNOWN, parent.getGender());
    }

    @Test
    public void testReKeyChildrenCollectionViaDelete() {
        Set<Integer> parentIds = createTestData(1, 5);
        em.clear();

        int parentId = parentIds.iterator().next();
        Parent parent = em.find(Parent.class, parentId);
        List<Child> children = new ArrayList<>(parent.getChildrenEager());

        int firstChildId = children.get(0).getChildId();

        log.info("DELETING ENTITIES");
        children.forEach(em::remove);

        // Required to force deletes to sync to DB now
        // otherwise persists below might encounter duplicate ID exceptions
        em.flush();

        log.info("CHANGING KEYS AND REINSERTING");
        // Update IDs of all children objects and re-merge into session
        children.stream()
                .peek(child -> child.setChildId(child.getChildId() + 1))
                .forEach(em::persist);

        log.info("CREATING NEW ELEMENT AT FRONT OF LIST");
        // Add new child at front of list
        Child newChild = new Child();
        newChild.setChildId(firstChildId);
        newChild.setName("First");
        newChild.setAge(23);
        newChild.setParent(parent);

        em.persist(newChild);
        children.add(0, newChild);

        // Force persist inserts to database prior to refreshing parent entity
        em.flush();
        em.refresh(parent);

        // Our lists shold contain equal elements still
        Assert.assertEquals(children, parent.getChildrenEager());

        // Not only that, if we kept them in sync they should be the EXACT SAME entity objects
        for (int i = 0; i < children.size(); i++) {
            Assert.assertSame(children.get(i), parent.getChildrenEager().get(i));
        }
    }

    @Test
    public void testReKeyChildrenCollectionViaEvict() {
        Set<Integer> parentIds = createTestData(1, 5);
        em.clear();

        int parentId = parentIds.iterator().next();
        Parent parent = em.find(Parent.class, parentId);
        List<Child> children = new ArrayList<>(parent.getChildrenEager());

        int firstChildId = children.get(0).getChildId();

        log.info("DETACHING ENTITIES");
        children.forEach(em::detach);

        log.info("CHANGING KEYS AND MERGING");
        // Update IDs of all children objects and re-merge into session
        List<Child> mergedList = children.stream()
                .peek(child -> child.setChildId(child.getChildId() + 1))
                .map(em::merge)
                .collect(Collectors.toList());

        log.info("CREATING NEW ELEMENT AT FRONT OF LIST");
        // Add new child at front of list
        Child newChild = new Child();
        newChild.setChildId(firstChildId);
        newChild.setName("First");
        newChild.setAge(23);
        newChild.setParent(parent);

        children.add(0, newChild);

        Child mergedChild = em.merge(newChild);
        mergedList.add(0, mergedChild);

        // Force updates/inserts to database prior to refreshing parent entity
        em.flush();
        em.refresh(parent);

        // Both lists should contain "equal" elements still
        Assert.assertEquals(children, parent.getChildrenEager());
        Assert.assertEquals(mergedList, parent.getChildrenEager());

        // However, the entities we started with are NOT the same entities after the merge!
        // This is because merge would have created new persistent entities for us.
        for (int i = 0; i < children.size(); i++) {
            Assert.assertNotSame(children.get(i), parent.getChildrenEager().get(i));
        }

        // This is why our MERGED LIST is the same objects we get off the parent entity.
        for (int i = 0; i < mergedList.size(); i++) {
            Assert.assertSame(mergedList.get(i), parent.getChildrenEager().get(i));
        }
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

        parent.getChildrenEager().stream().map(Child::toString).forEach(log::info);

        log.info("Merging copies into session...");
        copies.forEach(copy -> {
            em.merge(copy);
        });

        log.info("Merge complete.");
        parent.getChildrenEager().stream().map(Child::toString).forEach(log::info);
    }

    // ************************************************************************
    // Helpers to create test data defined below
    // ************************************************************************

    protected Set<Integer> createTestData(int numParents, int childrenPerParent) {
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
