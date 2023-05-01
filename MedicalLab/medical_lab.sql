create database medical_lab;
use medical_lab;

create table Doctors(doctor_name varchar(50) not null,
doctor_id int not null auto_increment,
Specialty varchar(50) not null,
primary key (doctor_id)
);

alter table Doctors add discount double default 0;
alter table Doctors rename to Doctor;
alter table Doctor rename column Specialty to specialty;
alter table Doctor modify doctor_name varchar(50) after doctor_id;
alter table Doctor add email varchar(50) after specialty;

-- we can add an email to the patients that gets displayed in the show more menu
create table Patient(
patient_id int not null auto_increment,
date_of_birth date not null,
patient_name varchar(50) not null,
gender varchar(10) not null,
address varchar(50) not null,
email varchar(50),
hasInsurance boolean default false,
primary key (patient_id));


create table Patient_phones(patient_id int not null,
phone_number varchar(15),
primary key (phone_number),
foreign key (patient_id) references Patient(patient_id) on delete cascade on update cascade);

create table patient_doctor(doctor_id int not null,
patient_id int not null,
primary key (doctor_id,patient_id),
foreign key (doctor_id) references Doctor(doctor_id) on update cascade on delete cascade,
foreign key (patient_id) references Patient(patient_id) on update cascade on delete cascade
);


create table medical_tests(
TSN int auto_increment not null,
test_name varchar(50) unique not null,
result_range varchar(60) not null,
charge_price int not null,
process_time int not null,
primary key (TSN,test_name)
);

create table Branches(working_hours varchar(20) not null,
location varchar(20) not null,
branch_id int not null auto_increment,
branch_name varchar(50) not null,
primary key (branch_id) 
);

create table Employees(emp_id int auto_increment not null,
shift varchar(30) not null,
emp_name varchar(50) not null,
date_of_birth date not null,
gender varchar(10) not null,
address varchar(50) not null,
hire_date date not null,
job_description varchar(20) not null,
branch_id int not null,
primary key (emp_id) ,
foreign key (branch_id) references Branches(branch_id) on delete cascade on update cascade
);

alter table Employees add salary double;

create table emp_phone(emp_id int not null,
phone_number varchar(15),
primary key (phone_number),
foreign key (emp_id) references Employees(emp_id) on delete cascade on update cascade);

create table Visits(visit_id int not null auto_increment,
payment_method varchar(50) not null,
charge_price int not null,
branch_id int not null,
patient_id int not null,
doctor_id int,
emp_id int not null,
process_status boolean default false,
date_time datetime not null,
foreign key (branch_id) references Branches(branch_id) on delete cascade on update cascade,
foreign key (doctor_id) references Doctor(doctor_id) on delete cascade on update cascade,
foreign key (patient_id) references Patient(patient_id) on delete cascade on update cascade,
foreign key (emp_id) references Employees(emp_id) on delete cascade on update cascade,
primary key (visit_id,branch_id,patient_id)
);


create table visit_tests_results(visit_id int not null,
TSN int not null,
result varchar(30),
primary key (TSN,visit_id),
foreign key (TSN) references medical_tests(TSN) on delete cascade on update cascade,
foreign key (visit_id) references Visits(visit_id) on delete cascade
 );
 
show tables;

select * from Doctor;
select * from Patient;
select * from Patient_phones;
select * from medical_tests;
select * from visit_tests_results;
select * from Branches;
select * from Visits;
select * from Employees;

insert into Patient (date_of_birth, patient_name, gender, address) values ("2000-08-17", 'ali', "male", "nablus");
insert into Patient (date_of_birth, patient_name, gender, address, email)
values ("2006-06-09", "Omar", "Male", "chobar", "omar@hotmail.com");
insert into Patient (date_of_birth, patient_name, gender, address, email)
values ("2013-04-10", "Puppy", "Male", "Nablus", "doggo@hotmail.com");
insert into medical_tests (test_name,result_range ,charge_price,process_time ) values ("Vitamin D","20-40 ng/dl",30,2),("B12","118-701 pmol/l",20,3),("Red Blood Cells","4.7-6.1 cells/mcl",25,1),("T4","5-12 mg/dl",40,2);
insert into Branches (working_hours,location,branch_name) values ("8:00-18:00","Al-Bireh","Al-Bireh");
insert into Doctor (doctor_name,Specialty) values ("Lubber","Leg");
insert into Employees (shift,emp_name,date_of_birth,gender,address,hire_date,job_description,branch_id,salary) values ("Night","Ah Chong",'1998-2-10',"Male","Al-Bireh",'2021-9-6',"Reception",1,1000);
insert into Employees (shift,emp_name,date_of_birth,gender,address,hire_date,job_description,branch_id,salary) values ("Morning","Rupert",'1998-10-21',"Male","Ramallah",'2019-6-10',"Reception",1,3000);
insert into Employees (shift,emp_name,date_of_birth,gender,address,hire_date,job_description,branch_id,salary) values ("Morning","Xi Dong",'1982-12-6',"Female","Nablus",'2018-5-4',"Reception",1,1500);
insert into Employees (shift,emp_name,date_of_birth,gender,address,hire_date,job_description,branch_id,salary) values ("Night","Piao Piao",'2000-5-15',"Female","Beit-Lahm",'2023-2-4',"Lab",1,5000);


select count(*) from medical_tests mt where mt.TSN IN (select mt.TSN from medical_tests mt,visit_tests_results vt where mt.TSN=vt.TSN group by mt.TSN);
