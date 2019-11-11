package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SeverityScaleValue {
	private int severityValueId;
	private int severityScaleId;
	private String scaleValue;
	private int sort;

	public int getSeverityValueId() {
		return severityValueId;
	}

	public void setSeverityValueId(int severityValueId) {
		this.severityValueId = severityValueId;
	}

	public int getSeverityScaleId() {
		return severityScaleId;
	}

	public void setSeverityScaleId(int severityScaleId) {
		this.severityScaleId = severityScaleId;
	}

	public String getScaleValue() {
		return scaleValue;
	}

	public void setScaleValue(String scaleValue) {
		this.scaleValue = scaleValue;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public void load(ResultSet rs) throws SQLException {
		severityValueId = rs.getInt("severity_value_id");
		severityScaleId = rs.getInt("severity_scale_id");
		sort = rs.getInt("sort");
		scaleValue = rs.getString("scale_value");
	}
}