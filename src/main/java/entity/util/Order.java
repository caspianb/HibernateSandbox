package entity.util;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@Table(name = "ord")
public class Order {
}
