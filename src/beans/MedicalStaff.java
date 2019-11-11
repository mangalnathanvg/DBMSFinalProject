package beans;

public class MedicalStaff extends Staff {

	private int medicalStaffId;
	private String departmentCode;

	public int getMedicalStaffId() {
		return medicalStaffId;
	}

	public void setMedicalStaffId(int medicalStaffId) {
		this.medicalStaffId = medicalStaffId;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

}
