package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VitalSigns {
	private int temperature;
	private int systolicPresure;
	private int diastolicPressure;
	private int checkInId;
	private int medicalStaffId;

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getSystolicPresure() {
		return systolicPresure;
	}

	public void setSystolicPresure(int systolicPresure) {
		this.systolicPresure = systolicPresure;
	}

	public int getDiastolicPressure() {
		return diastolicPressure;
	}

	public void setDiastolicPressure(int diastolicPressure) {
		this.diastolicPressure = diastolicPressure;
	}

	public int getCheckInID() {
		return checkInId;
	}

	public void setCheckInID(int checkInID) {
		this.checkInId = checkInID;
	}

	public int getMedicalStaffID() {
		return medicalStaffId;
	}

	public void setMedicalStaffID(int medicalStaffID) {
		this.medicalStaffId = medicalStaffID;
	}

	public void load(ResultSet rs) throws SQLException {
		checkInId = rs.getInt("check_in_id");
		temperature = rs.getInt("temperature");
		systolicPresure = rs.getInt("systolicPresure");
		diastolicPressure = rs.getInt("diastolicPressure");
		medicalStaffId = rs.getInt("medical_staff_id");
	}
}