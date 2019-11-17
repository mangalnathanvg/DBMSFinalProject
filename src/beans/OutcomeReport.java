package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public void setReferralStatus(ReferralStatus referralStatus) {
		this.referralStatus = referralStatus;

	}

	public ReferralStatus getReferralStatus(Connection conn) throws SQLException {
		if (referralStatus == null) {
			String sql = "SELECT * FROM referral_status WHERE referral_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, referralId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				referralStatus = new ReferralStatus();
				referralStatus.load(rs);
			}
		}
		return referralStatus;
	}

	public int getPatientConfirmation() {
		return patientConfirmation;
	}

	public void setPatientConfirmation(int patientConfirmation) {
		this.patientConfirmation = patientConfirmation;
	}

	public NegativeExperience getNegativeExperience(Connection conn) throws SQLException {
		if (experience == null) {
			String sql = "SELECT * FROM referral_status WHERE referral_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, referralId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				experience = new NegativeExperience();
				experience.load(rs);
			}
		}
		return experience;
	}

	public void load(ResultSet rs) throws SQLException {
		reportId = rs.getInt("report_id");
		referralId = rs.getInt("referral_id");
		feedbackId = rs.getInt("feedbacIId");
		checkInId = rs.getInt("check_in_id");
		treatmentDescription = rs.getString("treatment_description");
		dischargeStatus = rs.getString("discharge_status").charAt(0);
		generationTime = rs.getTimestamp("generation_time");
	}

	public void save(Connection conn) throws Exception {
		PreparedStatement ps = null;
		if (reportId == 0) {
			String sql = "INSERT INTO outcome_report(discharge_status,treatment_description, patient_confirmation,generation_time,referral_id,feedback_id) "
					+ "VALUES (to_char(?),?,?,?,?,?)";
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} else {
			String sql = "UPDATE outcome_report SET discharge_status=?,treatment_description=?,"
					+ "patient_confirmation=?,generation_time=?,referral_id=?,feedback_id=? WHERE report_id=?";
			ps = conn.prepareStatement(sql);
			ps.setInt(7, reportId);
		}
		ps.setInt(1, dischargeStatus);
		ps.setString(2, treatmentDescription);
		ps.setInt(3, patientConfirmation);
		ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		ps.setInt(5, referralId);
		ps.setInt(6, feedbackId);
		ps.executeUpdate();
		if (reportId == 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				reportId = rs.getInt(1);
			}
		}
	}

	public void setNegativeExperience(NegativeExperience negativeExperience) {
		this.experience = negativeExperience;
	}
}
