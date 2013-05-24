drop procedure mySchool.readStudentData;

create procedure mySchool.readStudentData()
begin
	select enrollment, name, age from STUDENT;
end