import entity.LargeEntity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

@Slf4j
public class HibernatePerformanceTest {

    private final EntityManager em;

    HibernatePerformanceTest() {
        this.em = HibernateTools.getEntityManager();
        log.info("****************************** TEST START ******************************");
    }

    @Test
    void testPerformance() {
        var numEntities = 100000;

        log.info("PERSISTING ALL ENTITIES");
        var entities = IntStream.range(0, numEntities)
                .mapToObj(this::generateEntity)
                .toList();
        entities.forEach(em::persist);

        em.flush();
        em.clear();
        log.info("DONE PERSISTING");

        log.info("READING ALL ENTITIES");
        var allEntities = em.createQuery("SELECT e FROM LargeEntity e", LargeEntity.class)
                .getResultList();
        log.info("DONE READING {}", allEntities.size());
    }

    LargeEntity generateEntity(int id) {
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
