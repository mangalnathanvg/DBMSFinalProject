package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CheckIn {
	private int checkInId;
	private Timestamp startTime;
	private Timestamp endTime;
	private char priority = '?';
	private int patientId;
	private int facilityId;

	private VitalSigns vitalSigns;
	private Treatment treatment;
	private ArrayList<SymptomMetadata> metadata;
	private Patient patient;

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

	public Patient getPatient() {
		return patient;
	}

	public ArrayList<SymptomMetadata> getSymptomMetadata(Connection conn) throws SQLException {
		if (metadata == null) {
			String sql = "SELECT * from symptom_metadata where check_in_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, checkInId);
			ps.executeQuery();
			ResultSet rs = ps.getResultSet();
			while (rs.next()) {
				SymptomMetadata symptomMetadata = new SymptomMetadata();
				symptomMetadata.load(rs);
			}
			rs.close();
			ps.close();
		}
		return metadata;
	}

	public void load(ResultSet rs, boolean loadPatient) throws SQLException {
		checkInId = rs.getInt("check_in_id");
		patientId = rs.getInt("patient_id");
		facilityId = rs.getInt("facility_id");
		String pri = rs.getString("priority");
		if (pri != null) {
			priority = pri.charAt(0);
		}
		startTime = rs.getTimestamp("start_time");
		endTime = rs.getTimestamp("end_time");

		vitalSigns = new VitalSigns();
		vitalSigns.load(rs);

		treatment = new Treatment();
		treatment.load(rs);

		if (loadPatient) {
			patient = new Patient();
			patient.load(rs, false);
		}
	}

	public void save(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		if (checkInId == 0) {
			String sql = "INSERT INTO check_in(start_time,end_time,priority,patient_id,facility_id) VALUES (?,?,?,?,?)";
			String[] primaryKey = { "check_in_id" };
			ps = conn.prepareStatement(sql, primaryKey);
		} else {
			String sql = "UPDATE check_in SET start_time=?,end_time=?,priority=?,patient_id=?,facility_id=? WHERE check_in_id=?";
			ps = conn.prepareStatement(sql);
			ps.setInt(6, checkInId);
		}
		ps.setTimestamp(1, startTime);
		ps.setTimestamp(2, endTime);
		ps.setString(3, priority == '?' ? null : "" + priority);
		ps.setInt(4, patientId == 0 ? null : patientId);
		ps.setInt(5, facilityId == 0 ? null : facilityId);
		ps.executeUpdate();
		if (checkInId == 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				checkInId = rs.getInt(1);
			}
		}
	}
}