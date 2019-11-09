package beans;

public class CheckIn {
	private int checkInID;
	private java.sql.Timestamp startTime;
	private java.sql.Timestamp endTime;
	private char priority;
	private int patientID;

	public int getCheckInID() {
		return checkInID;
	}
	public void setCheckInID(int checkInID) {
		this.checkInID = checkInID;
	}
	public java.sql.Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(java.sql.Timestamp startTime) {
		this.startTime = startTime;
	}
	public java.sql.Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(java.sql.Timestamp endTime) {
		this.endTime = endTime;
	}
	public char getPriority() {
		return priority;
	}
	public void setPriority(char priority) {
		this.priority = priority;
	}
	public int getPatientID() {
		return patientID;
	}
	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}
	
}