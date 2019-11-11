package beans;

public class SymptomMetadata {
	private int checkInID;
	private String symptomCode;
	private String bodyPartCode;
	private int durationDays;
	private int severityScaleValue;
	private int firstOccurrence;
	private String cause;
	private String description;
	
	public int getCheckInID() {
		return checkInID;
	}
	
	public void setCheckInID(int checkInID) {
		this.checkInID = checkInID;
	}
	
	public String getSymptomCode() {
		return symptomCode;
	}
	
	public void setSymptomCode(String symptomCode) {
		this.symptomCode = symptomCode;
	}
	
	public String getBodyPartCode() {
		return bodyPartCode;
	}
	
	public void setBodyPartCode(String bodyPartCode) {
		this.bodyPartCode = bodyPartCode;
	}
	
	public int getDurationDays() {
		return durationDays;
	}
	
	public void setDurationDays(int durationDays) {
		this.durationDays = durationDays;
	}
	
	public int getSeverityScaleValue() {
		return severityScaleValue;
	}
	
	public void setSeverityScaleValue(int severityScaleValue) {
		this.severityScaleValue = severityScaleValue;
	}
	
	public int getFirstOccurrence() {
		return firstOccurrence;
	}
	
	public void setFirstOccurrence(int firstOccurrence) {
		this.firstOccurrence = firstOccurrence;
	}
	
	public String getCause() {
		return cause;
	}
	
	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}