import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.AvailableSettings;

public class HibernateTools {

    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        return em;
    }

    private synchronized static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            Map<String, Object> properties = new HashMap<>();

            // Configure hibernate specific properties
            properties.put(AvailableSettings.SHOW_SQL, true);
            properties.put(AvailableSettings.FORMAT_SQL, false);
            properties.put(AvailableSettings.USE_SQL_COMMENTS, true);
            //properties.put(AvailableSettings.HBM2DDL_AUTO, Action.CREATE_DROP);
            properties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");

            properties.put(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, 8);

            properties.put(AvailableSettings.JPA_JDBC_DRIVER, "org.hsqldb.jdbcDriver");
            properties.put(AvailableSettings.JPA_JDBC_URL, "jdbc:hsqldb:mem:hibtest");
            properties.put(AvailableSettings.JPA_JDBC_USER, "");
            properties.put(AvailableSettings.JPA_JDBC_PASSWORD, "");

            // Load persistence unit from src/META-INF/persistence.xml
            emf = Persistence.createEntityManagerFactory("test", properties);
        }
        return emf;
    }

}
