package beans;

public class Symptom {
	private String symptomCode;
	private String name;
	private int severityScaleID;
	private String bodyPartCode;
	
	public String getSymptomCode() {
		return symptomCode;
	}
	
	public void setSymptomCode(String symptomCode) {
		this.symptomCode = symptomCode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getSeverityScaleID() {
		return severityScaleID;
	}
	
	public void setSeverityScaleID(int severityScaleID) {
		this.severityScaleID = severityScaleID;
	}
	
	public String getBodyPartCode() {
		return bodyPartCode;
	}
	
	public void setBodyPartCode(String bodyPartCode) {
		this.bodyPartCode = bodyPartCode;
	}
}