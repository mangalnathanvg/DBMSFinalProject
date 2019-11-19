package beans;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Staff {

	private int staffId;
	private String name;
	private char designation;
	private Date hireDate;
	private Date dateOfBirth;
	private String primaryDeptCode;

	private ServiceDepartment primaryDepartment = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getPrimaryDeptCode() {
		return primaryDeptCode;
	}

	public void setPrimaryDeptCode(String primaryDeptCode) {
		this.primaryDeptCode = primaryDeptCode;
	}

	public char getDesignation() {
		return designation;
	}

	public void setDesignation(char designation) {
		this.designation = designation;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public ServiceDepartment getPrimaryDepartment(Connection conn) throws SQLException {
		if (primaryDepartment == null) {
			StringBuilder sb = new StringBuilder();
			String tablename = "";
			sb.append("SELECT * FROM service_department sd ");
			if (designation == 'M') {
				tablename = "medical";
			} else if (designation == 'N') {
				tablename = "non_medical";
			}

			sb.append(
					"INNER JOIN " + tablename + "_service_department msd on msd.department_code = sd.department_code ");
			sb.append("INNER JOIN " + tablename + "_staff s on s.primary_department_code = msd.department_code ");
			sb.append("WHERE s." + tablename + "_staff_id = ?");
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			stmt.setInt(1, staffId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				if (primaryDepartment == null) {
					primaryDepartment = new ServiceDepartment();
				}
				primaryDepartment.load(rs, false);
			}
		}
		return primaryDepartment;
	}

	public boolean isMedical() {
		return (designation == 'M');
	}

	public void load(ResultSet rs) throws SQLException {
		staffId = rs.getInt("staff_id");
		name = rs.getString("name");
		designation = rs.getString("designation").charAt(0);
		dateOfBirth = rs.getDate("date_of_birth");
		hireDate = rs.getDate("hire_date");
	}

	public ArrayList<String> getTreatableBodyParts(Connection conn) throws SQLException {
		ArrayList<String> bodyPartCodes = new ArrayList<String>();
		String sql = "SELECT body_part_code FROM department_speciality ds INNER JOIN service_department sd on sd.department_code = ds.department_code "
				+ "INNER JOIN medical_staff ms on ms.primary_department_code = sd.department_code WHERE ms.medical_staff_id = ? "
				+ "UNION SELECT body_part_code FROM department_speciality ds INNER JOIN service_department sd on sd.department_code = ds.department_code "
				+ "INNER JOIN secondary_medical_department smd ON smd.medical_service_dept_code = ds.department_code WHERE smd.medical_staff_id = ?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, staffId);
		ps.setInt(2, staffId);
		ps.executeQuery();
		ResultSet rs = ps.getResultSet();
		while (rs.next()) {
			bodyPartCodes.add(rs.getString("body_part_code"));
		}
		return bodyPartCodes;
	}
}
