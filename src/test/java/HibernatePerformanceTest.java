import entity.LargeEntity;
import jakarta.persistence.EntityManager;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernatePerformanceTest {

    private Logger log = LoggerFactory.getLogger(HibernateTests.class);

    private EntityManager em;

    @Before
    public void testSetup() {
        em = HibernateTools.getEntityManager();
        log.info("****************************** TEST START ******************************");
    }

    @Test
    public void testPerformance() {
        var numEntities = 100000;

        log.info("PERSISTING ALL ENTITIES");
        var entities = IntStream.range(0, numEntities)
                .mapToObj(this::generateEntity)
                .toList();
        entities.forEach(em::persist);
        em.flush();
        log.info("DONE PERSISTING");

        entities.clear();
        em.clear();

        log.info("READING ALL ENTITIES");
        var allEntities = em.createQuery("SELECT e FROM LargeEntity e", LargeEntity.class)
                .getResultList();
        log.info("DONE READING {}", allEntities.size());
    }

    private LargeEntity generateEntity(int id) {
        try {
            var entity = new LargeEntity();
            Field[] fields = entity.getClass().getDeclaredFields();
            Field.setAccessible(fields, true);

            for (var field : fields) {
                if (String.class.isAssignableFrom(field.getType())) {
                    field.set(entity, UUID.randomUUID().toString());
                }

                else if (Integer.class.isAssignableFrom(field.getType())) {
                    field.set(entity, ThreadLocalRandom.current().nextInt());
                }
            }

            entity.setId(id);
            return entity;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
