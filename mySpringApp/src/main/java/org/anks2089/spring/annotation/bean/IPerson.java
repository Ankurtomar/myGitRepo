package org.anks2089.spring.annotation.bean;

public interface IPerson extends Cloneable{

	void setName(final String name);
	void setAge(final int age);
	
	String greet();
	String toString();
	
	Object getClone() throws CloneNotSupportedException;

}
