package org.anks2089.spring.app;

import lombok.extern.slf4j.Slf4j;

import org.anks2089.spring.app.component.MySchool;
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
		
		context = new ClassPathXmlApplicationContext("app/Spring-module.xml");
		MySchool myschool1 = (MySchool) context.getBean("schoolApp");
		myschool1.run();

//		MySchool myschool2 = (MySchool) context.getBean("schoolApp");
//		myschool2.run();
	}

}
