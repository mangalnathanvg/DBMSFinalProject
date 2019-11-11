package beans;

public class NonMedicalStaff extends Staff {

	private int nonMedicalStaff;
	private String primaryDepartmentCode;

	public int getNonMedicalStaff() {
		return nonMedicalStaff;
	}

	public void setNonMedicalStaff(int nonMedicalStaff) {
		this.nonMedicalStaff = nonMedicalStaff;
	}

	public String getPrimaryDepartmentCode() {
		return primaryDepartmentCode;
	}

	public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
		this.primaryDepartmentCode = primaryDepartmentCode;
	}

}
