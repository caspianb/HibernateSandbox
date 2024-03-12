package com.logicalbias.jpa.entity.util.generator;

import java.util.EnumSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;

import com.github.f4b6a3.uuid.factory.rfc4122.TimeOrderedEpochFactory;

/**
 * Define a custom Hibernate UUID Generation Strategy that utilizes the monotonic type-7 UUID format.
 */
public class TimeOrderedUuidGenerator implements BeforeExecutionGenerator {

    private final TimeOrderedEpochFactory uuidGenerator = new TimeOrderedEpochFactory();

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EventTypeSets.INSERT_ONLY;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        return currentValue != null ? currentValue : uuidGenerator.create();
    }
}

