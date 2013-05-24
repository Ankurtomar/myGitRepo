create schema mySchool;

drop table mySchool.Student;
drop table mySchool.Teacher;

create table mySchool.Student(
	ENROL VARCHAR(20) NOT NULL,
	NAME VARCHAR(20) NOT NULL,
	AGE INT NOT NULL,
	PRIMARY KEY (ENROL)
);

create table mySchool.Teacher(
	ENROL VARCHAR(20) NOT NULL,
	NAME VARCHAR(20) NOT NULL,
	AGE INT NOT NULL,
	PRIMARY KEY (ENROL),
	CONSTRAINT FKENROLL FOREIGN KEY(ENROL) REFERENCES myschool.student(enrol)
);

select * from mySchool.Student;
select * from mySchool.Teacher;
