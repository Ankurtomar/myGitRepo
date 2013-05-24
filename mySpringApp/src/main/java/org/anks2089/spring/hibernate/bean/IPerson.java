package org.anks2089.spring.hibernate.bean;

import java.util.Set;

public interface IPerson extends Cloneable{

	String getEnrol();
	String getName();
	int getAge();
	
	void setEnrol(final String enrol);
	void setName(final String name);
	void setAge(final int age);
	void addRelation(final IPerson object);
	
	String greet();
	String toString();
	
	Object getClone() throws CloneNotSupportedException;

}
