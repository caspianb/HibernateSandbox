package com.logicalbias.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.logicalbias.jpa.entity.util.generator.OrderedUuidGenerator;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@DynamicUpdate
@Table(name = "test_entity")
public class TestEntity {

    @Id
    @OrderedUuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column
    @EqualsAndHashCode.Include
    private UUID id;
}
