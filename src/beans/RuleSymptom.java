package beans;

public class RuleSymptom {
    private int ruleSymptomId;
    private char comparisonSymbol;
    private String symptomCode;
    private String bodyPartCode;
    private int scaleValueId;

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
	public String getSymptomCode() {
		return symptomCode;
	}
	public void setSymptomCode(String symptomCode) {
		this.symptomCode = symptomCode;
	}

	public String getBodyPartCode() {
		return bodyPartCode;
	}
	public void setBodyPartCode(String bodyPartCode) {
		this.bodyPartCode = bodyPartCode;
	}
	public int getScaleValueId() {
		return scaleValueId;
	}
	public void setScaleValueId(int scaleValueId) {
		this.scaleValueId = scaleValueId;
	}

    
}
