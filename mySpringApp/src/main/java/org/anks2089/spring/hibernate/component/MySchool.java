package org.anks2089.spring.hibernate.component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.anks2089.spring.hibernate.bean.IPerson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "schoolAppHibernate")
public class MySchool {

	@Setter
	private IPerson person1;
	@Setter
	private IPerson person2;

	@Setter
	private SessionFactory sessionFactory;

	// @Autowired
	// @Qualifier("teacher")
	// private IPerson person3;

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

			sessionFactory = myHibernateUtil.getSessionFactory();
			Session session = sessionFactory.openSession();
			session.beginTransaction();

			try {
				person1.setName("Ankur");
				person1.setEnrol("2");
				person1.addRelation(person2);
				// log.debug(person1.greet());

				person2.setName("Ankur");
				person2.setEnrol("1");
				// log.debug(person2.greet());

				log.debug(person1.toString());
				log.debug(person2.toString());

				session.saveOrUpdate(person1);
				// session.saveOrUpdate(person2);
			} catch (Exception e) {
				log.error("", e);
			}

			// final List<Student> studentList = (List<Student>)
			// session.createQuery(
			// "from Student").list();
			// log.debug("{}", studentList.size());
			// for (Student stu : studentList) {
			// log.debug("{}", stu.greet());
			// }

			session.getTransaction().commit();
			session.close();

		} catch (Exception cnse) {
			log.error("", cnse);
		}
		log.info("done");

	}

}
