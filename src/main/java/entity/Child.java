package entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@Table(name = "child")
public class Child {

    @Id
    @Column(name = "child_id")
    private Integer childId;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Column(name = "age")
    private int age;

    public Child() {
    }

    public Child(Child other) {
        this.childId = other.childId;
        this.name = other.name;
        this.parent = other.parent;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Child)) return false;
        Child child = (Child) o;
        return age == child.age &&
                Objects.equals(childId, child.childId) &&
                Objects.equals(name, child.name) &&
                Objects.equals(parent, child.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childId, name, parent, age);
    }

    @Override
    public String toString() {
        return String.format("Child [%s]: %s", childId, name);
    }

}
