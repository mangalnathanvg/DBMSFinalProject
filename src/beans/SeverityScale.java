package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SeverityScale {

	private int severityScaleId;
	private String name;

	private ArrayList<SeverityScaleValue> severityScaleValues;

	public SeverityScale() {
		severityScaleValues = new ArrayList<>();
	}

	public ArrayList<SeverityScaleValue> getSeverityScaleValues() {
		return severityScaleValues;
	}

	public void setSeverityScaleValues(ArrayList<SeverityScaleValue> severityScaleValues) {
		this.severityScaleValues = severityScaleValues;
	}

	public int getSeverityScaleId() {
		return severityScaleId;
	}

	public void setSeverityScaleId(int severityScaleId) {
		this.severityScaleId = severityScaleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void load(ResultSet rs) throws SQLException {
		severityScaleId = rs.getInt("severity_scale_id");
		name = rs.getString("name");
		SeverityScaleValue scaleValue = new SeverityScaleValue();
		scaleValue.load(rs);
		severityScaleValues.add(scaleValue);
	}

}