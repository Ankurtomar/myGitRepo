package org.anks2089.spring.app.component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.anks2089.spring.app.bean.IPerson;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MySchool {

	@Setter
	private IPerson person1;
	@Setter
	private IPerson person2;
	
//	@Autowired
//	@Qualifier("teacher")
//	private IPerson person3;

	public MySchool() {
		log.trace("Myschool default: {}", this.hashCode());
	}

	public MySchool(final IPerson person1, final IPerson person2) {
		log.trace("Myschool: {}", this.hashCode());
		this.person1 = person1;
		this.person2 = person2;
	}

	public void run() {
		try {
			person1.setName("Ankur");
			log.debug(person1.greet());
			log.debug(person1.toString());
			log.trace("1 - {}", person1.hashCode());
			log.trace("clone1 {}", person1.getClone().hashCode());

			person2.setName("Girija");
			log.debug(person2.greet());
			log.debug(person2.toString());
			log.trace("2 - {}", person2.hashCode());
			
//			person3.setName("Garima");
//			log.debug(person3.greet());
//			log.debug(person3.toString());
//			log.trace("3 - {}", person3.hashCode());
//			log.trace("clone2 {}", person3.getClone().hashCode());
			
		} catch (CloneNotSupportedException cnse) {
			log.error("", cnse);
		}
		log.info("done");

	}

}
