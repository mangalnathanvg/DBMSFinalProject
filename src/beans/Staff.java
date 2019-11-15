package beans;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM service_department sd LEFT JOIN department_speciality ds ON sd.department_code = ds.department_code WHERE ds.department_code = "
							+ primaryDeptCode);

			while (rs.next()) {
				primaryDepartment = new ServiceDepartment();
				primaryDepartment.load(rs);
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

}
