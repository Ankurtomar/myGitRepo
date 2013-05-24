drop table testTable;
drop table testTable2;

create table testTable (
	ID int,
	name varchar(30)
);

create table testTable2(
	ID int,
	name varchar(30) 
);

insert into testTable values (1,'anks');
insert into testTable values (2,'tom');

insert into testTable2 values (1,'anks');
insert into testTable2 values (2,'tom');

select * from testTable;

select * from testTable2;

