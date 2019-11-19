-- Change Logs:
-- Changed all the double quotes to single quotes.
-- Changed date string to TO_DATE(date, format).
-- Updated the address_id of the patient insert queries since it starts from 21 in trigger.
-- Updated column name in insert queries of severity_scale_value table from order to sort. Also updated severity_scale_id values for the queries

--Done
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ('John','Smith',TO_DATE('1994-01-01','YYYY-MM-DD'), 9007004567,21);
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ('Jane','Doe', TO_DATE('2000-02-29','YYYY-MM-DD'),9192453245,22);
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ('Rock','Star',TO_DATE('1970-08-31','YYYY-MM-DD'),5403127893,23);
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ('Sheldon','Cooper',TO_DATE('1984-05-26','YYYY-MM-DD'),6184628437,24);

--Done
insert into Address (add_number, state, city, street_name, country) values (100,'North Carolina','Raleigh','Avent Ferry Road','US');
insert into Address (add_number, state, city, street_name, country) values (1016,'New York','New York','Lixington Road','US');
insert into Address (add_number, state, city, street_name, country) values (1022,'California','Mountain View','Amphitheatre Parkway','US');
insert into Address (add_number, state, city, street_name, country) values (1210,'California','Santa Cruz','Sacramento','US');
insert into Address (add_number, state, city, street_name, country) values (2650,'North Carolina','Raleigh','Wolf Village','US');
insert into Address (add_number, state, city, street_name, country) values (2500,'California','Santa Cruz','Sacramento','US');
insert into Address (add_number, state, city, street_name, country) values (489,'New York','New York','First Avenue','US');
insert into Address (add_number, state, city, street_name, country) values (83,'NJ','Scotch Plains','Vernon St.','US');
insert into Address (add_number, state, city, street_name, country) values (69,'VA','Blacksburg','Holly drive','US');
insert into Address (add_number, state, city, street_name, country) values (7540, 'NH' , 'Derry', 'plymouth Court', 'US');
insert into Address (add_number, state, city, street_name, country) values (8196,'MD','Lutherville Timonium','Big Rock Cove Road','US');
insert into Address (add_number, state, city, street_name, country) values (697, 'NJ' , 'Teaneck' , 'Lawrence Ave.' , 'US');
insert into Address (add_number, state, city, street_name, country) values (685, 'CT' , 'Branford' , 'South Chapel Lane', 'US');
insert into Address (add_number, state, city, street_name, country) values (7056,'GA','Macon','W. Piper Dr.','US');
insert into Address (add_number, state, city, street_name, country) values (40,'NY','Sunnyside','N. Peachtree Drive','US');
insert into Address (add_number, state, city, street_name, country) values (22, 'MD', 'Laurel', 'Sutor st.','US');


--Done
insert into body_part (body_part_code,name) values ('DUM000','Dummy Body Part');
insert into body_part (body_part_code,name) values ('ARM000','Left Arm');
insert into body_part (body_part_code,name) values ('ARM001','Right Arm');
insert into body_part (body_part_code,name) values ('ABD000','Abdominal');
insert into body_part (body_part_code,name) values ('EYE000','Eye');
insert into body_part (body_part_code,name) values ('HRT000','Heart');
insert into body_part (body_part_code,name) values ('CST000','Chest');
insert into body_part (body_part_code,name) values ('HED000','Head');

--Done
insert into severity_scale (name) values ('Numbers');
insert into severity_scale (name) values ('Symbol1');
insert into severity_scale (name) values ('Symbol2');
insert into severity_scale (name) values ('Symbol3');
insert into severity_scale (name) values ('Exists');

--Done
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('1',41,1);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('2',41,2);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('3',41,3);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('4',41,4);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('5',41,5);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('6',41,6);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('7',41,7);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('8',41,8);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('9',41,9);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('10',41,10);
insert into severity_scale_value (scale_value, severity_scale_id, sort)  values ('Low',42,1);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('High',42,2);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('Normal',43,1);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('Severe',43,2);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('Normal',44,1);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('Premium',44,2);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('Present',45,2);
insert into severity_scale_value (scale_value, severity_scale_id, sort) values ('Absent',45,1);


--Done
insert into symptom (name,severity_scale_id) values ('Pain',2);
insert into symptom (name,severity_scale_id, body_part_code) values ('Diarrhea',1, 'ABD000');
insert into symptom (name,severity_scale_id, body_part_code) values ('Fever',2, 'DUM000');
insert into symptom (name,severity_scale_id) values ('Physical Exam',2);
insert into symptom (name,severity_scale_id, body_part_code) values ('Lightheadedness',1, 'HED000');
insert into symptom (name,severity_scale_id, body_part_code) values ('Blurred Vision',2, 'EYE000');
insert into symptom (name,severity_scale_id) values ('Bleeding',2);

-- Can't do it before check-in data is entered. No Sample data given for both check-in table and symptom metadata
-- insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id, first_occurrence,cause, description) values ('SYM44', 'DUM000' , 1, 55, 0 ,'Unknown', 'Unknown');
-- insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id,first_occurrence,cause, description) values ('SYM47', 'ARM000' , 3, 55, 1 ,'Fell off bike', 'Fell off bike');
-- insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id,first_occurrence,cause, description) values ('SYM48', 'DUM000' , 1, 55, 0 ,'Pepper challenge', 'Pepper challenge');
-- insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id,first_occurrence,cause, description) values ('SYM46', 'EYE000' , 1, 55, 0 ,'Unknown', 'Unknown');

-- Done
insert into certification (acronym, name, certification_date, expiration_date) values ('CER001','Comprehensive Stroke certification',TO_DATE('1990-11-12','YYYY-MM-DD'), TO_DATE('2025-11-11','YYYY-MM-DD'));
insert into certification (acronym, name, certification_date, expiration_date) values ('CER002','ISO certification',TO_DATE('2011-05-09','YYYY-MM-DD'), TO_DATE('2024-02-08','YYYY-MM-DD'));
insert into certification (acronym, name, certification_date, expiration_date) values ('CER003','Primary Stroke certification',TO_DATE('2018-01-01','YYYY-MM-DD'), TO_DATE('2028-12-31','YYYY-MM-DD'));


--Done
insert into medical_facility (name, capacity, classification, address_id) values ('Wolf Hospital',300,3,25);
insert into medical_facility (name, capacity, classification, address_id) values ('California Health Care',150,2,26);
insert into medical_facility (name, capacity, classification, address_id) values ('Suny Medical',10,1,27);

--Done
insert into facility_certification (acronym, facility_id) values ('CER001',41);
insert into facility_certification (acronym, facility_id) values ('CER002',42);
insert into facility_certification (acronym, facility_id) values ('CER003',43);

--Done
insert into service_department (department_code,name, director_id, facility_id) values ('ER000','Emergency room', null , 41);
insert into service_department (department_code,name, director_id, facility_id) values ('GP000','General practice department', null, 42);
insert into service_department (department_code,name, director_id, facility_id) values ('GP001','General practice department', null , 43);
insert into service_department (department_code,name, director_id, facility_id) values ('OP000','Optometry', null , 41);
insert into service_department (department_code,name, director_id, facility_id) values ('SE000','Security', null , 42);
insert into service_department (department_code,name, director_id, facility_id) values ('ER001','Emergency room', null , 43);

--Done
insert into department_speciality (department_code, body_part_code) values ('ER000','DUM000');
insert into department_speciality (department_code, body_part_code) values ('GP000','DUM000');
insert into department_speciality (department_code, body_part_code) values ('GP001','DUM000');
insert into department_speciality (department_code, body_part_code) values ('OP000','EYE000');
insert into department_speciality (department_code, body_part_code) values ('SE000','DUM000');



--Done
insert into service (service_code, name, equipment,facility_id) values ('SER01','Emergency','ER combo rack',41);
insert into service (service_code, name, equipment,facility_id) values ('SGP01','General practice','Blood pressure monitor / thermometer',42);
insert into service (service_code, name, equipment,facility_id) values ('VIS01','Vision','Vision Screener', 43);

--Done
insert into services_offered (service_code, department_code) values ('SER01', 'ER000');
insert into services_offered (service_code, department_code) values ('SGP01', 'GP000');
insert into services_offered (service_code, department_code) values ('SGP01', 'GP001');
insert into services_offered (service_code, department_code) values ('VIS01', 'OP000');

--Done
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Medical Robot', 'Director', TO_DATE('2019-06-21','YYYY-MM-DD') ,TO_DATE('1989-04-19','YYYY-MM-DD'), 41);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Musical Robert', 'Director',TO_DATE('2018-08-29','YYYY-MM-DD') , TO_DATE('1993-01-29','YYYY-MM-DD') , 42);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Muscular Rob', 'Director',TO_DATE('1993-10-12','YYYY-MM-DD') , TO_DATE('1967-12-09','YYYY-MM-DD') , 43);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Mechanical Roboto', 'General', TO_DATE('2019-06-21','YYYY-MM-DD'), TO_DATE('1988-05-18','YYYY-MM-DD'),41);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Millennium Roberten', 'Director', TO_DATE('2018-09-20','YYYY-MM-DD'), TO_DATE('1991-06-28','YYYY-MM-DD') , 42);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Missionary Robinson', 'General', TO_DATE('1993-10-01','YYYY-MM-DD'), TO_DATE('1966-07-08','YYYY-MM-DD') , 43);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Miscellaneous Robotor', 'Director', TO_DATE('1919-08-14','YYYY-MM-DD'), TO_DATE('1989-04-19','YYYY-MM-DD'), 41);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Musician Root', 'General', TO_DATE('2017-10-18','YYYY-MM-DD'), TO_DATE('1993-01-29','YYYY-MM-DD'), 42);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ('Messaging Robin', 'Director',TO_DATE('1990-12-10','YYYY-MM-DD'),TO_DATE('1967-12-09','YYYY-MM-DD'), 43);

--Done
insert into medical_staff (medical_staff_id, primary_department_code) values (21,'ER000');
insert into medical_staff (medical_staff_id, primary_department_code) values (22,'GP000');
insert into medical_staff (medical_staff_id, primary_department_code) values (23,'GP000');
insert into medical_staff (medical_staff_id, primary_department_code) values (24,'GP001');
insert into medical_staff (medical_staff_id, primary_department_code) values (25,'ER000');

--Done
insert into non_medical_staff (non_medical_staff_id, primary_department_code) values (27,'SE000');
insert into non_medical_staff (non_medical_staff_id, primary_department_code) values (29,'SE000');

--Done
insert into medical_service_department (department_code) values('ER000');
insert into medical_service_department (department_code) values('GP000');
insert into medical_service_department (department_code) values('GP001');
insert into medical_service_department (department_code) values('OP000');

--Done
insert into non_medical_service_department (department_code) values('SE000');

insert into secondary_medical_department () values ();

--Done
insert into secondary_service_department (service_code,department_code) values(27,'OP000');












