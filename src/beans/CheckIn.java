package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CheckIn {
	private int checkInId;
	private Timestamp startTime;
	private Timestamp endTime;
	private char priority;
	private int patientId;
	private int facilityId;

	private VitalSigns vitalSigns;
	private Treatment treatment;
	private SymptomMetadata metadata;

	public int getCheckInId() {
		return checkInId;
	}

	public void setCheckInId(int checkInId) {
		this.checkInId = checkInId;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public char getPriority() {
		return priority;
	}

	public void setPriority(char priority) {
		this.priority = priority;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public void load(ResultSet rs) throws SQLException {
		checkInId = rs.getInt("check_in_id");
		patientId = rs.getInt("patient_id");
		facilityId = rs.getInt("facility_id");
		priority = rs.getString("priority").charAt(0);
		startTime = rs.getTimestamp("start_time");
		endTime = rs.getTimestamp("end_time");

		vitalSigns = new VitalSigns();
		vitalSigns.load(rs);

		treatment = new Treatment();
		treatment.load(rs);
	}

	public void save(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		if (checkInId == 0) {
			String sql = "INSERT INTO check_in(start_time,end_time,priority,patient_id,facility_id) VALUES (?,?,?,?,?)";
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} else {
			String sql = "UPDATE check_in SET start_time=?,end_time=?,priority=?,patient_id=?,facility_id=? WHERE check_in_id=?";
			ps = conn.prepareStatement(sql);
			ps.setInt(6, checkInId);
		}
		ps.setTimestamp(1, startTime);
		ps.setTimestamp(2, endTime);
		ps.setInt(3, priority);
		ps.setInt(4, patientId);
		ps.setInt(5, facilityId);
		ps.executeUpdate();
		if (checkInId == 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				checkInId = rs.getInt(1);
			}
		}
	}
}