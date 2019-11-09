package beans;
import java.sql.Timestamp;

public class CheckIn {
	private int checkInID;
	private Timestamp startTime;
	private Timestamp endTime;
	private char priority;
	private int patientID;
	
	public int getCheckInID() {
		return checkInID;
	}
	
	public void setCheckInID(int checkInID) {
		this.checkInID = checkInID;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}
	
	public char getPriority() {
		return priority;
	}
	
	public void setPriority(char priority) {
		this.priority = priority;
	}
	
	public number getPatientID() {
		return patientID;
	}
	
	public void setPatientID(number patientID) {
		this.patientID = patientID;
	}
}