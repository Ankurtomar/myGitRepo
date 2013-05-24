package org.anks2089.spring.jdbc.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Student implements IPerson{

	private String enrol;
	private boolean isRegistered;
	private String name;
	private int age;	

	public Student(){}
	
	@Override
	public String greet() {
		return "Hello " + name;
	}
	
}
