package com.logicalbias.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;

public class HibernateTools {

    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.setFlushMode(FlushModeType.AUTO);

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

        // Enable automatic com.logicalbias.jpa.entity discovery (classpath scanning))
        properties.put(AvailableSettings.SCANNER_DISCOVERY, "class, hbm");

        // Configure hibernate specific properties
        properties.put(AvailableSettings.SHOW_SQL, false);
        properties.put(AvailableSettings.FORMAT_SQL, false);
        properties.put(AvailableSettings.USE_SQL_COMMENTS, false);
        properties.put(AvailableSettings.HBM2DDL_AUTO, Action.CREATE_DROP.getExternalHbm2ddlName());

        properties.put(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, 8);

        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.hsqldb.jdbcDriver");
        properties.put(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:hsqldb:mem:hibtest");
        properties.put(AvailableSettings.JAKARTA_JDBC_USER, "");
        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, "");

        return properties;
    }

}
