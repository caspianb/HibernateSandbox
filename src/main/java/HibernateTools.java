import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;

public class HibernateTools {

    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        return em;
    }

    public synchronized static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            Map<String, Object> properties = buildProperties();

            // Load persistence unit from src/META-INF/persistence.xml
            emf = Persistence.createEntityManagerFactory("test", properties);
        }
        return emf;
    }

    private static Map<String, Object> buildProperties() {
        Map<String, Object> properties = new HashMap<>();

        // Enable automatic entity discovery (classpath scanning))
        properties.put(AvailableSettings.SCANNER_DISCOVERY, "class, hbm");

        // Configure hibernate specific properties
        properties.put(AvailableSettings.SHOW_SQL, true);
        properties.put(AvailableSettings.FORMAT_SQL, false);
        properties.put(AvailableSettings.USE_SQL_COMMENTS, true);
        //properties.put(AvailableSettings.HBM2DDL_AUTO, Action.CREATE_DROP);
        properties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");

        properties.put(AvailableSettings.FLUSH_MODE, FlushModeType.AUTO);
        properties.put(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, 8);

        properties.put(AvailableSettings.JPA_JDBC_DRIVER, "org.hsqldb.jdbcDriver");
        properties.put(AvailableSettings.JPA_JDBC_URL, "jdbc:hsqldb:mem:hibtest");
        properties.put(AvailableSettings.JPA_JDBC_USER, "");
        properties.put(AvailableSettings.JPA_JDBC_PASSWORD, "");

        return properties;
    }

}
