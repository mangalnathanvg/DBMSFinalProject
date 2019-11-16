package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SymptomMetadata {
	private int checkInId;
	private String symptomCode;
	private String bodyPartCode;
	private int durationDays;
	private int severityScaleValueId;
	private int firstOccurrence;
	private String cause;
	private String description;

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

	public int getCheckInId() {
		return checkInId;
	}

	public void setCheckInId(int checkInId) {
		this.checkInId = checkInId;
	}

	public int getSeverityScaleValueId() {
		return severityScaleValueId;
	}

	public void setSeverityScaleValueId(int severityScaleValueId) {
		this.severityScaleValueId = severityScaleValueId;
	}

	public void save(Connection conn) throws SQLException {
		String sql = "INSERT INTO symptom_metadata(check_in_id,symptom_code,body_part_code,duration_days,severity_scale_value,"
				+ "first_occurrence,cause,description) VALUES (?,?,?,?,?,?,?,?);";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, checkInId);
		ps.setString(2, symptomCode);
		ps.setString(3, bodyPartCode);
		ps.setInt(4, durationDays);
		ps.setInt(5, severityScaleValueId);
		ps.setInt(6, firstOccurrence);
		ps.setString(7, cause);
		ps.setString(8, description);
		ps.executeUpdate();
	}
}