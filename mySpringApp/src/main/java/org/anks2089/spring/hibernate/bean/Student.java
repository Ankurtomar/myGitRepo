package org.anks2089.spring.hibernate.bean;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Slf4j
@Data
@Entity
@Table(name = "Student")
//@DiscriminatorColumn(name = "Student")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Student implements IPerson {

	@Id @Generated(GenerationTime.INSERT)
	@PrimaryKeyJoinColumn
	@Column(unique = true, nullable = false)
	private String enrol;

	// private boolean isRegistered;
	private String name;
	private int age;

	@OneToMany(targetEntity = Teacher.class)
	private Set<IPerson> relation = new HashSet<IPerson>();

	Student() {
		log.trace("Student bean : {}", this.hashCode());
	}

	public void addRelation(final IPerson object) {
		relation.add(object);
	}

	@Override
	public String greet() {
		return "Hello " + name;
	}

	@Override
	public Object getClone() throws CloneNotSupportedException {
		return clone();
	}

}
