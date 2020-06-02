package entity.util;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@Table(name = "ord")
public class Order {
}
