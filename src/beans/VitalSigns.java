package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VitalSigns {
	private int checkInId;
	private int medicalStaffId;
	private int temperature;
	private int systolicPresure;
	private int diastolicPressure;

	public VitalSigns(int checkInId, int medicalStaffId, int temperature, int systolicPresure, int diastolicPressure) {
		this.checkInId = checkInId;
		this.medicalStaffId = medicalStaffId;
		this.temperature = temperature;
		this.systolicPresure = systolicPresure;
		this.diastolicPressure = diastolicPressure;
	}

	public VitalSigns() {
	}

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
		systolicPresure = rs.getInt("SYSTOLIC_PRESSURE");
		diastolicPressure = rs.getInt("DIASTOLIC_PRESSURE");
		medicalStaffId = rs.getInt("medical_staff_id");
	}

	public void save(Connection conn) throws SQLException {
		String sql = "INSERT INTO vital_signs(check_in_id,medical_staff_id,temperature,systolic_pressure,diastolic_pressure) VALUES (?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, checkInId);
		ps.setInt(2, medicalStaffId);
		ps.setInt(3, temperature);
		ps.setInt(4, systolicPresure);
		ps.setInt(5, diastolicPressure);
		ps.executeUpdate();
	}
}