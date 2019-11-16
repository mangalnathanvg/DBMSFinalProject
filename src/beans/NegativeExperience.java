package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NegativeExperience {

	private int reportId;
	private int experienceCode;
	private String description;

	public NegativeExperience(int experienceCode, String description) {
		this.experienceCode = experienceCode;
		this.description = description;
	}

	public NegativeExperience() {
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getExperienceCode() {
		return experienceCode;
	}

	public void setExperienceCode(int experienceCode) {
		this.experienceCode = experienceCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExperienceCodeName() {
		if (experienceCode == 1) {
			return "Misdiagnosis";
		} else if (experienceCode == 2) {
			return "Patient acquired an infection during hospital stay";
		}
		return "";
	}

	public void load(ResultSet rs) throws SQLException {
		reportId = rs.getInt("report_id");
		experienceCode = rs.getInt("experience_code");
		description = rs.getString("description");
	}
}
