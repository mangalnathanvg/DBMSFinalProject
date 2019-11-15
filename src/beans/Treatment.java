package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Treatment {

	private int checkInId;
	private Timestamp treatmentTime;
	private int medicalStaffId;

	public int getCheckInId() {
		return checkInId;
	}

	public void setCheckInId(int checkInId) {
		this.checkInId = checkInId;
	}

	public Timestamp getTreatmentTime() {
		return treatmentTime;
	}

	public void setTreatmentTime(Timestamp treatmentTime) {
		this.treatmentTime = treatmentTime;
	}

	public int getMedicalStaffId() {
		return medicalStaffId;
	}

	public void setMedicalStaffId(int medicalStaffId) {
		this.medicalStaffId = medicalStaffId;
	}

	public void load(ResultSet rs) throws SQLException {
		checkInId = rs.getInt("check_in_id");
		medicalStaffId = rs.getInt("medical_staff_id");
		treatmentTime = rs.getTimestamp("treatment_time");
	}

}
