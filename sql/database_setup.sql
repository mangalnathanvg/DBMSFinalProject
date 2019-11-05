CREATE TABLE rule(
    rule_id NUMBER PRIMARY KEY,
    priority CHAR(1),
);


CREATE SEQUENCE rule_sequence;


CREATE OR REPLACE TRIGGER rule_on_insert
  BEFORE INSERT ON rule
  FOR EACH ROW
BEGIN
  SELECT rule_sequence.nextval
  INTO :new.rule_id
  FROM dual;
END;


CREATE TABLE rule_symptom(
    rule_symptom_id NUMBER PRIMARY KEY,
    comparison_symbol CHAR(1),
    symptom_code VARCHAR2(4000),
    body_part_code VARCHAR2(4000),
    scale_value_id NUMBER
);


CREATE SEQUENCE rule_symptom_sequence;


CREATE OR REPLACE TRIGGER rule_symptom_on_insert
  BEFORE INSERT ON rule
  FOR EACH ROW
BEGIN
  SELECT rule_symptom_sequence.nextval
  INTO :new.rule_symptom_id
  FROM dual;
END;


ALTER TABLE rule_symptom
ADD CONSTRAINT fk_rule_symptom_scale_value
FOREIGN KEY(scale_value_id)
REFERENCES rule_symptom(scale_value_id);


CREATE TABLE rule_consists(
    rule_id NUMBER,
    rule_symptom_id NUMBER,
    PRIMARY KEY(rule_id, rule_symptom_id)
);


ALTER TABLE rule_consists
ADD CONSTRAINT fk_rule_consists_rule
FOREIGN KEY(rule_id)
REFERENCES rule(rule_id);


ALTER TABLE rule_consists
ADD CONSTRAINT fk_rule_consists_rule_symptom
FOREIGN KEY(rule_symptom_id)
REFERENCES rule_symptom(rule_symptom_id);


CREATE TABLE treatment(
    check_in_id NUMBER PRIMARY KEY,
    time DATE,
    medical_staff_id NUMBER
);


ALTER TABLE treatment
ADD CONSTRAINT fk_treatment_medical_staff
FOREIGN KEY(medical_staff_id)
REFERENCES medical_staff(medical_staff_id);


CREATE TABLE outcome_report(
    report_id NUMBER PRIMARY KEY,
    discharge_status CHAR(1) NOT NULL,
    treatment_description VARCHAR2(4000),
    generation_time DATE,
    patient_confirmation NUMBER,
    referral_id NUMBER,
    negative_exp_id NUMBER,
    feedback_id NUMBER
);


CREATE SEQUENCE outcome_report_sequence;


CREATE OR REPLACE TRIGGER outcome_report_on_insert
  BEFORE INSERT ON outcome_report
  FOR EACH ROW
BEGIN
  SELECT outcome_report_sequence.nextval
  INTO :new.report_id
  FROM dual;
END;


ALTER TABLE outcome_report
ADD CONSTRAINT fk_outcome_report_referral_status
FOREIGN KEY(referral_id)
REFERENCES referral_status(referral_id);

--something wrong here
ALTER TABLE outcome_report
ADD CONSTRAINT fk_outcome_report_negative_experience
FOREIGN KEY(negative_exp_id)
REFERENCES negative_experience(report_id);


ALTER TABLE outcome_report
ADD CONSTRAINT fk_outcome_report_feedback
FOREIGN KEY(feedback_id)
REFERENCES feedback(feedback_id);


CREATE TABLE feedback(
    feedback_id NUMBER PRIMARY KEY,
    description VARCHAR2(4000)
);


CREATE SEQUENCE feedback_sequence;


CREATE OR REPLACE TRIGGER feedback_on_insert
  BEFORE INSERT ON feedback
  FOR EACH ROW
BEGIN
  SELECT feedback_sequence.nextval
  INTO :new.feedback_id
  FROM dual;
END;


CREATE TABLE referral_status(
    referral_id NUMBER PRIMARY KEY,
    facility_id NUMBER,
    medical_staff_id NUMBER
);


CREATE SEQUENCE referral_status_sequence;


CREATE OR REPLACE TRIGGER referral_status_on_insert
  BEFORE INSERT ON referral_status
  FOR EACH ROW
BEGIN
  SELECT referral_status_sequence.nextval
  INTO :new.referral_id
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


CREATE TABLE referral_reason(
    referral_reason_id NUMBER PRIMARY KEY,
    reason_code NUMBER,
    description VARCHAR2(4000),
    referral_id NUMBER
);


CREATE SEQUENCE referral_reason_sequence;


CREATE OR REPLACE TRIGGER referral_reason_on_insert
  BEFORE INSERT ON referral_reason
  FOR EACH ROW
BEGIN
  SELECT referral_reason_sequence.nextval
  INTO :new.referral_reason_id
  FROM dual;
END;


ALTER TABLE referral_reason
ADD CONSTRAINT fk_referral_reason_referral_status
FOREIGN KEY(referral_id)
REFERENCES referral_status(referral_id);


CREATE TABLE negative_experience(
    report_id NUMBER PRIMARY KEY,
    experience_code NUMBER,
    description VARCHAR2(4000)
);


ALTER TABLE negative_experience
ADD CONSTRAINT fk_negative_experience_outcome_report
FOREIGN KEY(report_id)
REFERENCES outcome_report(report_id);