package org.anks2089.spring.annotation.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Teacher implements IPerson {

	private String enrol;
	private boolean isRegistered;
	private String name;
	private int age;

	Teacher() {
		log.trace("Teacher bean : {}", this.hashCode());
	}

	@Override
	public String greet() {
		return "Hello Teacher: " + name;
	}

	@Override
	public Object getClone() throws CloneNotSupportedException {
		return clone();
	}

}
