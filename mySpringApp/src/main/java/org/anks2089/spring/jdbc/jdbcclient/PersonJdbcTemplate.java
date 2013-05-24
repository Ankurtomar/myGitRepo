package org.anks2089.spring.jdbc.jdbcclient;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.anks2089.spring.jdbc.bean.IPerson;
import org.anks2089.spring.jdbc.bean.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

public class PersonJdbcTemplate {
	private DataSource dataSource;
	private SimpleJdbcCall jdbcCall;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcCall = new SimpleJdbcCall(dataSource)
				.withProcedureName("getRecord");
	}

	private JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

	public List<Student> listStudents() {
		String SQL = "select * from Student";
		List<Student> students = getJdbcTemplate().query(SQL,
				new StudentMapper());
		return students;
	}

	public void create(final IPerson[] student) {

		String[] name = new String[student.length];
		int[] age = new int[student.length];

		for (int i = 0; i < student.length; i++) {
			name[i] = student[i].getName();
			age[i] = student[i].getAge();
		}

		String SQL = "insert into Student (name, age) values (?, ?)";
		getJdbcTemplate().update(SQL, name, age);
		System.out.println("Created Record Name = " + name + " Age = " + age);
		return;
	}

	public Student getStudent(final Integer id) {
		SqlParameterSource in = new MapSqlParameterSource().addValue("in_id",
				id);
		Map<String, Object> out = jdbcCall.execute(in);
		Student student = new Student();
		// student.setEnrol(id);
		student.setName((String) out.get("out_name"));
		student.setAge((Integer) out.get("out_age"));
		return student;
	}

}
