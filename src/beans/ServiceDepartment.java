package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceDepartment {

	private String departmentCode;
	private String name;
	private int directorId;
	private int facilityId;
	// Don't think we need to know if it is medical or non-medical

	private ArrayList<String> bodyParts;

	public ServiceDepartment() {
		bodyParts = new ArrayList<String>();
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDirectorId() {
		return directorId;
	}

	public void setDirectorId(int directorId) {
		this.directorId = directorId;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public void load(ResultSet rs) throws SQLException {
		directorId = rs.getInt("director_id");
		facilityId = rs.getInt("facility_id");
		departmentCode = rs.getString("department_code");
		name = rs.getString("name");
		String bodyPartCode = rs.getString("body_part_code");
		if (bodyPartCode != null && !bodyPartCode.isEmpty()) {
			bodyParts.add(bodyPartCode);
		}
	}
}
