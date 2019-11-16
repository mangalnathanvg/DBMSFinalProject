package beans;

import java.sql.Connection;
import java.sql.Timestamp;

public class OutcomeReport {

	private int reportId;
	private char dischargeStatus;
	private String treatmentDescription;
	private Timestamp generationTime;
	private int referralId;
	private int feedbackId;
	private int checkInId;
	private int patientConfirmation;

	private ReferralStatus referralStatus;
	private Treatment treatment;
	private NegativeExperience experience;

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

	public Timestamp getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(Timestamp generationTime) {
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

	public boolean isReferred() {
		return (dischargeStatus == 'R' || dischargeStatus == 'r');
	}

	public String getDischargeStatusName() {
		String name = "";
		switch (dischargeStatus) {
		case 'T':
		case 't':
			name = "Treated Successfully";
			break;
		case 'D':
		case 'd':
			name = "Deceased,";
			break;
		case 'R':
		case 'r':
			name = "Referred).";
			break;
		}
		return name;
	}

	public ReferralStatus getReferralStatus(Connection conn) {
		if (referralStatus == null) {

		}
		return referralStatus;
	}

	public int getPatientConfirmation() {
		return patientConfirmation;
	}

	public void setPatientConfirmation(int patientConfirmation) {
		this.patientConfirmation = patientConfirmation;
	}

}
