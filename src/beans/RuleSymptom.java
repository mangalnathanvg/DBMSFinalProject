package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class RuleSymptom {

	private int ruleSymptomId;
	private char comparisonSymbol;

	private BodyPart bodyPart;
	private Symptom symptom;
	private SeverityScaleValue scaleValue;

	public int getRuleSymptomId() {
		return ruleSymptomId;
	}

	public void setRuleSymptomId(int ruleSymptomId) {
		this.ruleSymptomId = ruleSymptomId;
	}

	public char getComparisonSymbol() {
		return comparisonSymbol;
	}

	public void setComparisonSymbol(char comparisonSymbol) {
		this.comparisonSymbol = comparisonSymbol;
	}

	public SeverityScaleValue getScaleValue() {
		return scaleValue;
	}

	public void setScaleValue(SeverityScaleValue scaleValue) {
		this.scaleValue = scaleValue;
	}

	public BodyPart getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(BodyPart bodyPart) {
		this.bodyPart = bodyPart;
	}

	public Symptom getSymptom() {
		return symptom;
	}

	public void setSymptom(Symptom symptom) {
		this.symptom = symptom;
	}

	public void load(ResultSet rs, HashMap<String, BodyPart> bodyParts, HashMap<String, Symptom> symptoms,
			HashMap<Integer, SeverityScaleValue> severityScaleValues) throws SQLException {
		ruleSymptomId = rs.getInt("rule_symptom_id");
		comparisonSymbol = rs.getString("comparison_symbol").charAt(0);
		bodyPart = bodyParts.get(rs.getString("body_part_code"));
		symptom = symptoms.get(rs.getString("symptom_code"));
		scaleValue = severityScaleValues.get(rs.getInt("scale_value_id"));
	}

}
