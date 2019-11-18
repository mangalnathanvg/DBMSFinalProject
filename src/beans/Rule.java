package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Rule {

	private int ruleId;
	private char priority;

	private ArrayList<RuleSymptom> ruleSymptoms;

	public Rule() {
		ruleSymptoms = new ArrayList<>();
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public char getPriority() {
		return priority;
	}

	public void setPriority(char priority) {
		this.priority = priority;
	}

	public void addRuleSymptom(RuleSymptom ruleSymptom) {
		ruleSymptoms.add(ruleSymptom);
	}

	public ArrayList<RuleSymptom> getRuleSymptoms() {
		return ruleSymptoms;
	}

	public void load(ResultSet rs, HashMap<String, BodyPart> bodyParts, HashMap<String, Symptom> symptoms,
			HashMap<Integer, SeverityScaleValue> severityScaleValues) throws SQLException {
		ruleId = rs.getInt("rule_id");
		priority = rs.getString("priority").charAt(0);
		RuleSymptom ruleSymptom = new RuleSymptom();
		ruleSymptom.load(rs, bodyParts, symptoms, severityScaleValues);
		ruleSymptoms.add(ruleSymptom);
	}

}
