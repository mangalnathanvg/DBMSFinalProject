package beans;

public class OutcomeReport {
	
    private int reportId;
    private char dischargeStatus;
    private String treatmentDescription;
    private java.sql.Timestamp generationTime;
    private int referralId;
    private int feedbackId;
    private int patientConfirmation;
    
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	public char getDischargeStatus() {
		return dischargeStatus;
	}
	public void setDischargeStatus(char dischargeStatus) {
		this.dischargeStatus = dischargeStatus;
	}
	public String getTreatmentDescription() {
		return treatmentDescription;
	}
	public void setTreatmentDescription(String treatmentDescription) {
		this.treatmentDescription = treatmentDescription;
	}
	public java.sql.Timestamp getGenerationTime() {
		return generationTime;
	}
	public void setGenerationTime(java.sql.Timestamp generationTime) {
		this.generationTime = generationTime;
	}
	public int getReferralId() {
		return referralId;
	}
	public void setReferralId(int referralId) {
		this.referralId = referralId;
	}
	public int getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}
	public int getPatientConfirmation() {
		return patientConfirmation;
	}
	public void setPatientConfirmation(int patientConfirmation) {
		this.patientConfirmation = patientConfirmation;
	}
    
    

}
