package org.anks2089.spring.annotation.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Student implements IPerson{

	private String enrol;
	private boolean isRegistered;
	private String name;
	private int age;	

	Student(){
		log.trace("Student bean : {}", this.hashCode());
	}
	
	@Override
	public String greet() {
		return "Hello " + name;
	}

	@Override
	public Object getClone() throws CloneNotSupportedException{
		return clone();
	}
	
}
