package org.anks2089.spring.hibernate.bean;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Entity
@Table(name="Teacher")
public class Teacher implements IPerson {

	@Id
	@PrimaryKeyJoinColumn
	@Column(unique = true, nullable = false)
	private String enrol;
	
	private String name;
	private int age;
	
	@OneToMany(targetEntity = Student.class)
	private Set<IPerson> relation = new HashSet<IPerson>();
	
	Teacher() {
		log.trace("Teacher bean : {}", this.hashCode());
	}

	@Override
	public String greet() {
		return "Hello Teacher: " + getName();
	}

	@Override
	public Object getClone() throws CloneNotSupportedException {
		return clone();
	}

	@Override
	public void addRelation(IPerson object) {
		relation.add(object);
	}

}
