create schema mySchool;

drop table mySchool.Student;
drop table mySchool.Teacher;

create table mySchool.Student(
	id integer generated always as identity,
	ENROL VARCHAR(20) NOT NULL,
	NAME VARCHAR(20) NOT NULL,
	AGE INT NOT NULL,
	PRIMARY KEY (ENROL)
);

create table mySchool.Teacher(
	id integer 	generated always as identity,
	ENROL VARCHAR(20) NOT NULL,
	NAME VARCHAR(20) NOT NULL,
	AGE INT NOT NULL,
	PRIMARY KEY (ENROL),
	CONSTRAINT FKENROLL FOREIGN KEY(ENROL) REFERENCES myschool.student(enrol)
);

select * from Student;
select * from Teacher;
