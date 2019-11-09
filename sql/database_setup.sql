--DEEPALI
CREATE TABLE patient(
    patient_id NUMBER(10) PRIMARY KEY,
    first_name VARCHAR2(20) NOT NULL,
    last_name VARCHAR2(20),
    date_of_birth DATE,
    phone_number NUMBER(11),
    address_id NUMBER(10));

CREATE TABLE medical_facility(
    facility_id NUMBER(10) PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    capacity NUMBER(10),
    classification NUMBER(1),
    address_id NUMBER(10));

CREATE TABLE address(
    address_id NUMBER(10) PRIMARY KEY,
    add_number NUMBER(10),
    state VARCHAR2(50),
    city VARCHAR2(50),
    street_name VARCHAR2(50),
    country VARCHAR2(50));

CREATE TABLE certification(
    acronym VARCHAR2(20) PRIMARY KEY,
    name VARCHAR2(50),
    certification_date DATE,
    expiration_date DATE);

CREATE TABLE facility_certification(
    acronym VARCHAR2(20),
    facility_id NUMBER(10),
    PRIMARY KEY(acronym, facility_id));

CREATE TABLE service_department(
    department_code VARCHAR2(5) PRIMARY KEY,
    name VARCHAR2(50),
    director_id NUMBER(10),
    facility_id NUMBER(10)
);

CREATE TABLE department_speciality(
    department_code VARCHAR2(5),
    body_part_code VARCHAR2(20),
    PRIMARY KEY(department_code, body_part_code)
);

CREATE TABLE service(
    service_code VARCHAR2(10) PRIMARY KEY,
    name VARCHAR2(50),
    equipment VARCHAR2(50),
    facility_id NUMBER(10)
);

CREATE TABLE medical_service_department(
    department_code VARCHAR2(5) PRIMARY KEY
);

CREATE TABLE non_medical_service_department(
    department_code VARCHAR2(5) PRIMARY KEY
);

CREATE TABLE services_offered(
    service_code VARCHAR2(10),
    department_code VARCHAR2(5),
    PRIMARY KEY(service_code, department_code)
);

CREATE TABLE staff(
    staff_id NUMBER(10) PRIMARY KEY,
    name VARCHAR2(50),
    designation VARCHAR2(50),
    hire_date DATE,
    facility_id NUMBER(10)
);

CREATE TABLE non_medical_staff(
    non_medical_staff_id NUMBER(10) PRIMARY KEY,
    primary_department_code VARCHAR2(5)
);

CREATE TABLE secondary_medical_department(
    medical_staff_id NUMBER(10),
    medical_service_dept_code VARCHAR2(5),
    PRIMARY KEY(medical_staff_id, medical_service_dept_code)
);

CREATE TABLE secondary_service_department(
    non_medical_staff_id NUMBER(10),
    department_code VARCHAR2(5),
    PRIMARY KEY (non_medical_staff_id, department_code)
);

CREATE TABLE body_part(
    body_part_code VARCHAR2(20) PRIMARY KEY,
    name VARCHAR2(50)
);

CREATE TABLE check_in(
    check_in_id VARCHAR2(10) PRIMARY KEY,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    priority char(1),
    patient_id NUMBER(10)
);


CREATE TABLE symptom(
    symptom_code VARCHAR2(20) PRIMARY KEY,
    name VARCHAR2(50),
    severity_scale_id NUMBER(10),
    body_part_code VARCHAR2(20)
);

CREATE TABLE medical_staff(
    medical_staff_id NUMBER(10) PRIMARY KEY,
    primary_department_code VARCHAR2(5)
);

CREATE TABLE symptom_metadata(
    check_in_id VARCHAR2(20),
    symptom_code VARCHAR2(20),
    body_part_code VARCHAR2(20),
    duration_days NUMBER(3),
    severity_scale_value NUMBER(10),
    first_occurrence NUMBER(1),
    cause VARCHAR2(4000),
    PRIMARY KEY(check_in_id, symptom_code, body_part_code)
);

CREATE TABLE severity_scale(
    severity_scale_id NUMBER(20) PRIMARY KEY,
    name VARCHAR2(50)
);

CREATE TABLE severity_scale_value(
    severity_value_id NUMBER(10) PRIMARY KEY,
    scale_value VARCHAR2(50),
    severity_scale_id NUMBER(10),
    order NUMBER(10)
);

CREATE TABLE vital_signs(
    temperature NUMBER(3),
    systolic_pressure NUMBER(3),
    diastolic_pressure NUMBER(3),
    check_in_id NUMBER(10) PRIMARY KEY,
    medical_staff_id NUMBER(10)
);

CREATE TABLE rule(
    rule_id NUMBER(10) PRIMARY KEY,
    priority CHAR(1)
);

CREATE TABLE rule_symptom(
    rule_symptom_id NUMBER(10) PRIMARY KEY,
    comparison_symbol CHAR(1),
    symptom_code VARCHAR2(20),
    body_part_code VARCHAR2(20),
    scale_value_id NUMBER(10)
);

CREATE TABLE rule_consists(
    rule_id NUMBER(10),
    rule_symptom_id NUMBER(10),
    PRIMARY KEY(rule_id, rule_symptom_id)
);

CREATE TABLE treatment(
    check_in_id NUMBER(10) PRIMARY KEY,
    treatment_time TIMESTAMP,
    medical_staff_id NUMBER(10)
);

CREATE TABLE outcome_report(
    report_id NUMBER(10) PRIMARY KEY,
    discharge_status CHAR(1) NOT NULL,
    treatment_description VARCHAR2(4000),
    generation_time TIMESTAMP,
    patient_confirmation NUMBER(1),
    referral_id NUMBER(10),
    feedback_id NUMBER(10)
);

CREATE TABLE feedback(
    feedback_id NUMBER(10) PRIMARY KEY,
    description VARCHAR2(4000)
);

CREATE TABLE referral_status(
    referral_id NUMBER(10) PRIMARY KEY,
    facility_id NUMBER(10),
    medical_staff_id NUMBER(10)
);

CREATE TABLE referral_reason(
    referral_reason_id NUMBER(10) PRIMARY KEY,
    reason_code NUMBER(1),
    description VARCHAR2(4000),
    referral_id NUMBER(10)
);

CREATE TABLE negative_experience(
    report_id NUMBER(10) PRIMARY KEY,
    experience_code NUMBER(1),
    description VARCHAR2(4000)
);

ALTER TABLE medical_facility ADD(CONSTRAINT medical_facility_classification CHECK(classification IN(1, 2, 3)));

ALTER TABLE medical_facility ADD FOREIGN KEY(address_id) REFERENCES address(address_id);

ALTER TABLE patient ADD FOREIGN KEY(address_id) REFERENCES address(address_id);

ALTER TABLE facility_certification ADD FOREIGN KEY(facility_id) REFERENCES medical_facility(facility_id);

ALTER TABLE facility_certification ADD FOREIGN KEY(acronym) REFERENCES certification(acronym);

CREATE SEQUENCE patient_seq;

CREATE OR REPLACE TRIGGER patient_trigger
BEFORE INSERT ON patient
FOR EACH ROW
BEGIN
SELECT patient_seq.nextval
INTO: new.patient_id
FROM dual;
END;

CREATE SEQUENCE medical_facility_seq;

CREATE OR REPLACE TRIGGER medical_facility_trigger
BEFORE INSERT ON medical_facility
FOR EACH ROW
BEGIN
SELECT medical_facility_seq.nextval
INTO: new.facility_id
FROM dual;
END;

CREATE SEQUENCE address_seq;

CREATE OR REPLACE TRIGGER address_trigger
BEFORE INSERT ON address
FOR EACH ROW
BEGIN
SELECT address_seq.nextval
INTO: new.address_id
FROM dual;
END;

--MANGAL
--auto increment



ALTER TABLE service_department
ADD CONSTRAINT FK_DepartmentDirector
FOREIGN KEY(director_id) REFERENCES medical_staff(medical_staff_id)

ALTER TABLE service_department
ADD CONSTRAINT FK_DepartmentFacility
FOREIGN KEY(facility_id) REFERENCES medical_facility(facility_id);


ALTER TABLE department_speciality
ADD CONSTRAINT FK_Department
FOREIGN KEY(department_code) REFERENCES service_department(department_code);


ALTER TABLE department_speciality
ADD CONSTRAINT FK_BodyPartAssociation
FOREIGN KEY(body_part_code) REFERENCES body_part(body_part_code);



ALTER TABLE medical_service_department
ADD CONSTRAINT FK_MedServiceDepartment
FOREIGN KEY(department_code) REFERENCES service_department(department_code);




ALTER TABLE non_medical_service_department
ADD CONSTRAINT FK_NonMedServiceDepartment
FOREIGN KEY(department_code) REFERENCES service_department(department_code);



ALTER TABLE service
ADD CONSTRAINT FK_ServiceFacility
FOREIGN KEY(facility_id) REFERENCES medical_facility(facility_id);



ALTER TABLE services_offered
ADD CONSTRAINT FK_ServicesOfferedServices
FOREIGN KEY(service_code) REFERENCES service(service_code);


ALTER TABLE services_offered
ADD CONSTRAINT FK_ServicesOfferedDepartment
FOREIGN KEY(department_code) REFERENCES service_department(department_code);



CREATE SEQUENCE staff_seq;

CREATE OR REPLACE TRIGGER staff_id_trigger
BEFORE INSERT ON staff
FOR EACH ROW
BEGIN
SELECT staff_seq.nextval
INTO: new.staff_id
FROM dual;
END;

ALTER TABLE staff
ADD CONSTRAINT FK_StaffFacility
FOREIGN KEY(facility_id) REFERENCES medical_facility(facility_id);


ALTER TABLE medical_staff
ADD CONSTRAINT FK_MedicalStaff
FOREIGN KEY(medical_staff_id) REFERENCES staff(staff_id);

ALTER TABLE medical_staff
ADD CONSTRAINT FK_MedPrimaryDepartment
FOREIGN KEY(primary_department_code) REFERENCES medical_service_department(department_code)



ALTER TABLE non_medical_staff
ADD CONSTRAINT FK_NonMedicalStaff
FOREIGN KEY(non_medical_staff_id) REFERENCES staff(staff_id);

ALTER TABLE non_medical_staff
ADD CONSTRAINT FK_NonMedPrimaryDepartment
FOREIGN KEY(primary_department_code) REFERENCES non_medical_service_department(department_code)



ALTER TABLE secondary_medical_department
ADD CONSTRAINT FK_MedicalStaffSecondary
FOREIGN KEY(medical_staff_id) REFERENCES medical_staff(medical_staff_id);

ALTER TABLE secondary_medical_department
ADD CONSTRAINT FK_MedicalDepartmentSecondary
FOREIGN KEY(medical_service_department_code) REFERENCES medical_service_department(department_code);




ALTER TABLE secondary_service_department
ADD CONSTRAINT FK_ServiceStaffSecondary
FOREIGN KEY(non_medical_staff_id) REFERENCES non_medical_staff(non_medical_staff_id);

ALTER TABLE secondary_service_department
ADD CONSTRAINT FK_ServiceDepartmentSecondary
FOREIGN KEY(department_code) REFERENCES service_department(department_code);

--GOD
--auto increment



ALTER TABLE check_in
ADD CONSTRAINT FK_CheckinPatient
FOREIGN KEY(patient_id) REFERENCES Patient(patient_id);

ALTER TABLE check_in ADD(CONSTRAINT check_in_priority CHECK(classification IN('H', 'N', 'Q')));

--add constraint
for SYM prefix

ALTER TABLE symptom
ADD CONSTRAINT FK_SymptomSSID
FOREIGN KEY(severity_scale_id) REFERENCES severity_scale(severity_scale_id);

ALTER TABLE symptom
ADD CONSTRAINT FK_SymptomBodyPart
FOREIGN KEY(body_part_code) REFERENCES body_part(body_part_code);


ALTER TABLE symptom_metadata ADD(CONSTRAINT symptom_metadata_first_occurence CHECK(first_occurrence IN(0, 1)));

ALTER TABLE symptom_metadata
ADD CONSTRAINT FK_SymptomMDCheckin
FOREIGN KEY(check_in_id) REFERENCES check_in(check_in_id);

ALTER TABLE symptom_metadata
ADD CONSTRAINT FK_SymptomMDSymptom
FOREIGN KEY(symptom_code) REFERENCES symptom(symptom_code);

ALTER TABLE symptom_metadata
ADD CONSTRAINT FK_SymptomBodyPart
FOREIGN KEY(body_part_code) REFERENCES body_part(body_part_code);

ALTER TABLE symptom_metadata
ADD CONSTRAINT FK_SymptomSevValueScale
FOREIGN KEY(severity_scale_value) REFERENCES severity_scale_value(severity_value_id);


ALTER TABLE severity_scale_value
ADD CONSTRAINT FK_SSVSevScale
FOREIGN KEY(severity_scale_id) REFERENCES severity_scale(severity_scale_id);


ALTER TABLE vital_signs
ADD CONSTRAINT FK_VitalSignsCheckIn
FOREIGN KEY(check_in_id) REFERENCES check_in(check_in_id);

ALTER TABLE vital_signs
ADD CONSTRAINT FK_VitalSignsMedStaff
FOREIGN KEY(medical_staff_id) REFERENCES medical_Staff(medical_staff_id);

CREATE SEQUENCE check_in_id_sequence;

CREATE OR REPLACE TRIGGER check_in_on_insert
BEFORE INSERT ON check_in
FOR EACH ROW
BEGIN
SELECT check_in_id_sequence.nextval
INTO: new.check_in_id
FROM dual;
END;

CREATE SEQUENCE severity_scale_id_sequence;

CREATE OR REPLACE TRIGGER severity_scale_on_insert
BEFORE INSERT ON severity_scale
FOR EACH ROW
BEGIN
SELECT severity_scale_id_sequence.nextval
INTO: new.severity_scale_id
FROM dual;
END;

CREATE SEQUENCE severity_value_id_sequence;

CREATE OR REPLACE TRIGGER severity_scale_value_on_insert
BEFORE INSERT ON severity_scale_value
FOR EACH ROW
BEGIN
SELECT severity_value_id_sequence.nextval
INTO: new.severity_value_id

CREATE SEQUENCE rule_sequence;


CREATE OR REPLACE TRIGGER rule_on_insert
BEFORE INSERT ON rule
FOR EACH ROW
BEGIN
SELECT rule_sequence.nextval
INTO: new.rule_id
FROM dual;
END;

--constraint for symbol

CREATE SEQUENCE rule_symptom_sequence;


CREATE OR REPLACE TRIGGER rule_symptom_on_insert
BEFORE INSERT ON rule_symptom
FOR EACH ROW
BEGIN
SELECT rule_symptom_sequence.nextval
INTO: new.rule_symptom_id
FROM dual;
END;


ALTER TABLE rule_symptom
ADD CONSTRAINT fk_rule_symptom_scale_value
FOREIGN KEY(scale_value_id)
REFERENCES rule_symptom(scale_value_id);





ALTER TABLE rule_consists
ADD CONSTRAINT fk_rule_consists_rule
FOREIGN KEY(rule_id)
REFERENCES rule(rule_id);


ALTER TABLE rule_consists
ADD CONSTRAINT fk_rule_consists_rule_symptom
FOREIGN KEY(rule_symptom_id)
REFERENCES rule_symptom(rule_symptom_id);





ALTER TABLE treatment
ADD CONSTRAINT fk_treatment_medical_staff
FOREIGN KEY(medical_staff_id)
REFERENCES medical_staff(medical_staff_id);




--add constraint discharge status, confirmation

CREATE SEQUENCE outcome_report_sequence;


CREATE OR REPLACE TRIGGER outcome_report_on_insert
BEFORE INSERT ON outcome_report
FOR EACH ROW
BEGIN
SELECT outcome_report_sequence.nextval
INTO: new.report_id
FROM dual;
END;


ALTER TABLE outcome_report
ADD CONSTRAINT fk_outcome_report_referral_status
FOREIGN KEY(referral_id)
REFERENCES referral_status(referral_id);


ALTER TABLE outcome_report
ADD CONSTRAINT fk_outcome_report_feedback
FOREIGN KEY(feedback_id)
REFERENCES feedback(feedback_id);


CREATE SEQUENCE feedback_sequence;


CREATE OR REPLACE TRIGGER feedback_on_insert
BEFORE INSERT ON feedback
FOR EACH ROW
BEGIN
SELECT feedback_sequence.nextval
INTO: new.feedback_id
FROM dual;
END;


CREATE SEQUENCE referral_status_sequence;


CREATE OR REPLACE TRIGGER referral_status_on_insert
BEFORE INSERT ON referral_status
FOR EACH ROW
BEGIN
SELECT referral_status_sequence.nextval
INTO: new.referral_id
FROM dual;
END;


ALTER TABLE referral_status
ADD CONSTRAINT fk_referral_status_facility
FOREIGN KEY(facility_id)
REFERENCES medical_facility(facility_id);


ALTER TABLE referral_status
ADD CONSTRAINT fk_referral_status_medical_staff
FOREIGN KEY(medical_staff_id)
REFERENCES medical_staff(medical_staff_id);

--add constraint reason code

CREATE SEQUENCE referral_reason_sequence;


CREATE OR REPLACE TRIGGER referral_reason_on_insert
BEFORE INSERT ON referral_reason
FOR EACH ROW
BEGIN
SELECT referral_reason_sequence.nextval
INTO: new.referral_reason_id
FROM dual;
END;


ALTER TABLE referral_reason
ADD CONSTRAINT fk_referral_reason_referral_status
FOREIGN KEY(referral_id)
REFERENCES referral_status(referral_id);

--constraint for experience code

ALTER TABLE negative_experience
ADD CONSTRAINT fk_negative_experience_outcome_report
FOREIGN KEY(report_id)
REFERENCES outcome_report(report_id);