package entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@Table(name = "parent")
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "parent_id")
    private int parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    @Convert(converter = Gender.Converter.class)
    private Gender gender;

    @OneToMany(mappedBy = "parent")
    private Set<Child> childrenLazy = new LinkedHashSet<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
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
    public String toString() {
        return "Parent: " + name;
    }

}
