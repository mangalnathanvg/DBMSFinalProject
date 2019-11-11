package beans;

public class Treatment {
    
	private int checkInId;
    private java.sql.Timestamp treatmentTime;
    private int medicalStaffId;
  
	public int getCheckInId() {
		return checkInId;
	}
	public void setCheckInId(int checkInId) {
		this.checkInId = checkInId;
	}
	public java.sql.Timestamp getTreatmentTime() {
		return treatmentTime;
	}
	public void setTreatmentTime(java.sql.Timestamp treatmentTime) {
		this.treatmentTime = treatmentTime;
	}
	public int getMedicalStaffId() {
		return medicalStaffId;
	}
	public void setMedicalStaffId(int medicalStaffId) {
		this.medicalStaffId = medicalStaffId;
	}
    
}
