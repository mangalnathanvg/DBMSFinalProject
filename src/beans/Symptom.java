package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Symptom {

	private String symptomCode;
	private String name;

	private BodyPart bodyPart;
	private SeverityScale severityScale;

	public Symptom() {
		severityScale = new SeverityScale();
	}

	public String getSymptomCode() {
		return symptomCode;
	}

	public void setSymptomCode(String symptomCode) {
		this.symptomCode = symptomCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BodyPart getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(BodyPart bodyPart) {
		this.bodyPart = bodyPart;
	}

	public SeverityScale getSeverityScale() {
		return severityScale;
	}

	public void setSeverityScale(SeverityScale severityScale) {
		this.severityScale = severityScale;
	}

	public boolean hasBodyPart() {
		return bodyPart == null;
	}

	public void load(ResultSet rs, HashMap<String, BodyPart> bodyParts, HashMap<Integer, SeverityScale> severityScales)
			throws SQLException {
		symptomCode = rs.getString("symptom_code");
		name = rs.getString("name");
		severityScale = severityScales.get(rs.getInt("severity_scale_id"));
		bodyPart = bodyParts.get(rs.getString("body_part_code"));
	}
}