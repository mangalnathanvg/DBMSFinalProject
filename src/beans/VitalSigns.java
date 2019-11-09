package beans;

/*CREATE TABLE vital_signs(
	    temperature NUMBER(3),
	    systolic_pressure NUMBER(3),
	    diastolic_pressure NUMBER(3),
	    check_in_id NUMBER(10) PRIMARY KEY,
	    medical_staff_id NUMBER(10)
	);*/

public class VitalSigns {
	private int temperature;
	private int systolicPresure;
	private int diastolicPressure;
	private int checkInID;
	private int medicalStaffID;
	
	public int getTemperature() {
		return temperature;
	}
	
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	public int getSystolicPresure() {
		return systolicPresure;
	}
	
	public void setSystolicPresure(int systolicPresure) {
		this.systolicPresure = systolicPresure;
	}
	
	public int getDiastolicPressure() {
		return diastolicPressure;
	}
	
	public void setDiastolicPressure(int diastolicPressure) {
		this.diastolicPressure = diastolicPressure;
	}
	
	public int getCheckInID() {
		return checkInID;
	}
	
	public void setCheckInID(int checkInID) {
		this.checkInID = checkInID;
	}
	
	public int getMedicalStaffID() {
		return medicalStaffID;
	}
	
	public void setMedicalStaffID(int medicalStaffID) {
		this.medicalStaffID = medicalStaffID;
	}
}