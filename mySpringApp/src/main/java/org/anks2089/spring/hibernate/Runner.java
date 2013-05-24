package org.anks2089.spring.hibernate;

import lombok.extern.slf4j.Slf4j;

import org.anks2089.spring.hibernate.component.MySchool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Slf4j
public class Runner {
	
	private static ApplicationContext context;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("::: Spring test application :::");
		
		context = new ClassPathXmlApplicationContext("hibernate/Spring-hibernate-module.xml");
		MySchool myschool1 = (MySchool) context.getBean("schoolAppHibernate");
		myschool1.run();

//		MySchool myschool2 = (MySchool) context.getBean("schoolApp");
//		myschool2.run();
	}

}
