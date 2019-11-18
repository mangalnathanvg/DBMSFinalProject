insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ("John","Smith",1994-01-01,9007004567,1);
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ("Jane","Doe",2000-02-29,9192453245,2);
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ("Rock","Star",1970-08-31,5403127893,3);
insert into Patient (first_name,last_name,date_of_birth,phone_number,address_id) values ("Sheldon","Cooper",1984-05-26,6184628437,4);


insert into Address (add_number, state, city, street_name, country) values (100,"North Carolina","Raleigh","Avent Ferry Road","US");
insert into Address (add_number, state, city, street_name, country) values (1016,"New York","New York","Lixington Road","US");
insert into Address (add_number, state, city, street_name, country) values (1022,"California","Mountain View","Amphitheatre Parkway","US");
insert into Address (add_number, state, city, street_name, country) values (1210,"California","Santa Cruz","Sacramento","US");
insert into Address (add_number, state, city, street_name, country) values (2650,"North Carolina","Raleigh","Wolf Village","US");
insert into Address (add_number, state, city, street_name, country) values (2500,"California","Santa Cruz","Sacramento","US");
insert into Address (add_number, state, city, street_name, country) values (489,"New York","New York","First Avenue","US");
insert into Address (add_number, state, city, street_name, country) values (83,"NJ","Scotch Plains","Vernon St.","US");
insert into Address (add_number, state, city, street_name, country) values (69,"VA","Blacksburg","Holly drive","US");
insert into Address (add_number, state, city, street_name, country) values (7540, "NH" , "Derry", "plymouth Court", "US");
insert into Address (add_number, state, city, street_name, country) values (8196,"MD","Lutherville Timonium","Big Rock Cove Road","US");
insert into Address (add_number, state, city, street_name, country) values (697, "NJ" , "Teaneck" , "Lawrence Ave." , "US");
insert into Address (add_number, state, city, street_name, country) values (685, "CT" , "Branford" , "South Chapel Lane", "US");
insert into Address (add_number, state, city, street_name, country) values (7056,"GA","Macon","W. Piper Dr.","US");
insert into Address (add_number, state, city, street_name, country) values (40,"NY","Sunnyside","N. Peachtree Drive","US");
insert into Address (add_number, state, city, street_name, country) values (22, "MD", "Laurel", "Sutor st.","US");


insert into body_part (body_part_code,name) values ("DUM000","Dummy body Part");
insert into body_part (body_part_code,name) values ("ARM000","Left Arm");
insert into body_part (body_part_code,name) values ("ARM001","Right Arm");
insert into body_part (body_part_code,name) values ("ABD000","Abdominal");
insert into body_part (body_part_code,name) values ("EYE000","Eye");
insert into body_part (body_part_code,name) values ("HRT000","Heart");
insert into body_part (body_part_code,name) values ("CST000","Chest");
insert into body_part (body_part_code,name) values ("HED000","Head");


insert into severity_scale (name) values ("Numbers");
insert into severity_scale (name) values ("Symbol1");
insert into severity_scale (name) values ("Symbol2");
insert into severity_scale (name) values ("Symbol3");
insert into severity_scale (name) values ("Exists");

insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("1",1,1);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("2",1,2);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("3",1,3);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("4",1,4);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("5",1,5);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("6",1,6);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("7",1,7);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("8",1,8);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("9",1,9);
insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("10",1,10);

insert into severity_scale_value (scale_value, severity_scale_id, order)  values ("Low",2,1);
insert into severity_scale_value (scale_value, severity_scale_id, order) values ("High",2,2);

insert into severity_scale_value (scale_value, severity_scale_id, order) values ("Normal",3,1);
insert into severity_scale_value (scale_value, severity_scale_id, order) values ("Severe",3,2);

insert into severity_scale_value (scale_value, severity_scale_id, order) values ("Normal",4,1);
insert into severity_scale_value (scale_value, severity_scale_id, order) values ("Premium",4,2);

insert into severity_scale_value (scale_value, severity_scale_id, order) values ("Present",5,2);
insert into severity_scale_value (scale_value, severity_scale_id, order) values ("Absent",5,1);

insert into symptom (name,severity_scale_id) values ("Pain",2);
insert into symptom (name,severity_scale_id, body_part_code) values ("Diarrhea",1, "ABD000");
insert into symptom (name,severity_scale_id, body_part_code) values ("Fever",2, "DUM000");
insert into symptom (name,severity_scale_id) values ("Physical Exam",2);
insert into symptom (name,severity_scale_id, body_part_code) values ("Lightheadedness",1, "HED000");
insert into symptom (name,severity_scale_id, body_part_code) values ("Blurred Vision",2, "EYE000");
insert into symptom (name,severity_scale_id) values ("Bleeding",2);


insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id, first_occurrence,cause, description) values ("SYM3", "DUM000" , 1, 12, 0 ,"Unknown", "Unknown");
insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id,first_occurrence,cause, description) values ("SYM1", "ARM000" , 3, 5, 1 ,"Fell off bike", "Fell off bike");
insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id,first_occurrence,cause, description) values ("SYM2", "DUM000" , 1, 14, 0 ,"Pepper challenge", "Pepper challenge");
insert into symptom_metadata (symptom_code, body_part_code, duration_days, severity_value_id,first_occurrence,cause, description) values ("SYM6", "EYE000" , 1, 13, 0 ,"Unknown", "Unknown");


insert into certification (acronym, name, certification_date, expiration_date) values ("CER001","Comprehensive Stroke certification",1990-11-12, 2025-11-11);
insert into certification (acronym, name, certification_date, expiration_date) values ("CER002","ISO certification",2011-05-09, 2024-02-08);
insert into certification (acronym, name, certification_date, expiration_date) values ("CER003","Primary Stroke certification",2018-01-01, 2028-12-31);


insert into medical_facility (name, capacity, classification, address_id) values ("Wolf Hospital",300,3,5);
insert into medical_facility (name, capacity, classification, address_id) values ("California Health Care",150,2,6);
insert into medical_facility (name, capacity, classification, address_id) values ("Suny Medical",10,1,7);


insert into facility_certification (acronym, facility_id) values ("CER001",1);
insert into facility_certification (acronym, facility_id) values ("CER002",2);
insert into facility_certification (acronym, facility_id) values ("CER003",3);


insert into service_department (department_code,name, director_id, facility_id) values ("ER000","Emergency room", 93001 , 1001);
insert into service_department (department_code,name, director_id, facility_id) values ("GP000","General practice department", 67001, 1000);
insert into service_department (department_code,name, director_id, facility_id) values ("GP001","General practice department", 91001 , 1001);
insert into service_department (department_code,name, director_id, facility_id) values ("OP000","Optometry", 89001 , 1000);
insert into service_department (department_code,name, director_id, facility_id) values ("SE000","Security", 89002 , 1000);
insert into service_department (department_code,name, director_id, facility_id) values ("ER001","Emergency room", 67002 , 1002);

insert into department_speciality (department_code, body_part_code) values ("ER000","DUM000");
insert into department_speciality (department_code, body_part_code) values ("GP000","DUM000");
insert into department_speciality (department_code, body_part_code) values ("GP001","DUM000");
insert into department_speciality (department_code, body_part_code) values ("OP000","EYE000");
insert into department_speciality (department_code, body_part_code) values ("SE000","DUM000");
insert into department_speciality (department_code, body_part_code) values ("ER001","DUM000");


insert into service (service_code, name, equipment,facility_id) values ("SER01","Emergency","ER combo rack");
insert into service (service_code, name, equipment,facility_id) values ("SGP01","General practice","Blood pressure monitor / thermometer");
insert into service (service_code, name, equipment,facility_id) values ("VIS01","Vision","Vision Screener");


insert into services_offered (service_code, department_code) values ("SER01", "ER000");
insert into services_offered (service_code, department_code) values ("SGP01", "GP000");
insert into services_offered (service_code, department_code) values ("SGP01", "GP001");
insert into services_offered (service_code, department_code) values ("VIS01", "OP000");


insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Medical Robot", "Director", 2019-06-21 ,1989-04-19, 8);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Musical Robert", "Director",2018-08-29 , 1993-01-29 , 9);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Muscular Rob", "Director",1993-10-12 , 1967-12-09 , 10);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Mechanical Roboto", "General", 2019-06-21, 1988-05-18, 11);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Millennium Roberten", "Director",2018-09-20, 1991-06-28 , 12);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Missionary Robinson", "General", 1993-10-01, 1966-07-08 , 13);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Miscellaneous Robotor", "Director",1919-08-14,1989-04-19, 14);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Musician Root", "General",2017-10-18, 1993-01-29, 15);
insert into staff (name, designation, hire_date, date_of_birth, facility_id) values ("Messaging Robin", "Director",1990-12-10,1967-12-09, 16);


insert into medical_staff (medical_staff_id, primary_department_code) values (1,"OP000");
insert into medical_staff (medical_staff_id, primary_department_code) values (2,"ER000");
insert into medical_staff (medical_staff_id, primary_department_code) values (3,"GP000");
insert into medical_staff (medical_staff_id, primary_department_code) values (4,"GP000");
insert into medical_staff (medical_staff_id, primary_department_code) values (5,"GP001");
insert into medical_staff (medical_staff_id, primary_department_code) values (6,"ER000");
insert into medical_staff (medical_staff_id, primary_department_code) values (8,"ER001");

insert into non_medical_staff (non_medical_staff_id, primary_department_code) values (7,"SE000");
insert into non_medical_staff (non_medical_staff_id, primary_department_code) values (9,"SE000");

insert into medical_service_department (department_code) values(1);
insert into medical_service_department (department_code) values(2);
insert into medical_service_department (department_code) values(3);
insert into medical_service_department (department_code) values(4);
insert into medical_service_department (department_code) values(6);

insert into non_medical_service_department (department_code) values(5);

insert into secondary_medical_department () values ();

insert into secondary_service_department (service_code,department_code) values(4,"OP000");












