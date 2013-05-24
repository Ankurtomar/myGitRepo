package org.anks2089.spring.jdbc.bean;

public interface IPerson {

	void setName(final String name);
	String getName();
		
	void setAge(final int age);
	int getAge();
	
	String greet();

	String toString();
}
