package org.anks2089.spring.jdbc.component;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.anks2089.spring.jdbc.bean.IPerson;
import org.anks2089.spring.jdbc.jdbcclient.PersonJdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("mySchoolJDBC")
public class MySchool {

	@Setter
	private PersonJdbcTemplate personJdbcTemplate;

	@Setter
	private IPerson person1;
	@Setter
	private IPerson person2;

	public MySchool() {
		log.trace("Myschool default: {}", this.hashCode());
	}

	public MySchool(final IPerson person1, final IPerson person2) {
		log.trace("Myschool: {}", this.hashCode());
		this.person1 = person1;
		this.person2 = person2;
	}

	public void run() {

		List<IPerson> persons = new ArrayList<IPerson>();

		person1.setName("Ankur");
		log.debug(person1.greet());
		log.debug(person1.toString());
		persons.add(person1);

		person2.setName("Girija");
		log.debug(person2.greet());
		log.debug(person2.toString());
		persons.add(person2);

		personJdbcTemplate.create(persons.toArray(new IPerson[persons.size()]));

		log.info("done");

	}

}
