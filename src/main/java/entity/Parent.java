package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name = "parent")
public class Parent {

    @Id
    @Column(name = "parent_id")
    private int parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    @Convert(converter = Gender.Converter.class)
    private Gender gender;

    @OneToMany(mappedBy = "parent")
    @OrderBy("childId ASC")
    private Set<Child> childrenLazy = new LinkedHashSet<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @OrderBy("childId ASC")
    private List<Child> childrenEager = new ArrayList<>();

    public Parent() {
    }

    public Parent(Parent other) {
        this.parentId = other.parentId;
        this.name = other.name;
        this.gender = other.gender;
        this.childrenLazy.addAll(other.childrenLazy);
        this.childrenEager.addAll(other.childrenEager);
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Set<Child> getChildrenLazy() {
        return childrenLazy;
    }

    public void setChildrenLazy(Set<Child> childrenLazy) {
        this.childrenLazy = childrenLazy;
    }

    public List<Child> getChildrenEager() {
        return childrenEager;
    }

    public void setChildrenEager(List<Child> childrenEager) {
        this.childrenEager = childrenEager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parent)) return false;
        Parent parent = (Parent) o;
        return parentId == parent.parentId &&
                Objects.equals(name, parent.name) &&
                gender == parent.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId, name, gender);
    }

    @Override
    public String toString() {
        return String.format("Parent [%s]: %s", parentId, name);
    }

}
