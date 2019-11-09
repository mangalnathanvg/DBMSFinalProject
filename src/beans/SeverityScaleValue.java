package beans;

public class SeverityScaleValue {
	private int severityValueID;
	private String scaleValue;
	private int severityScaleID;
	private int sort;
	
	public int getSeverityValueID() {
		return severityValueID;
	}
	
	public void setSeverityValueID(int severityValueID) {
		this.severityValueID = severityValueID;
	}
	
	public String getScaleValue() {
		return scaleValue;
	}
	
	public void setScaleValue(String scaleValue) {
		this.scaleValue = scaleValue;
	}
	
	public int getSeverityScaleID() {
		return severityScaleID;
	}
	
	public void setSeverityScaleID(int severityScaleID) {
		this.severityScaleID = severityScaleID;
	}
	
	public int getSort() {
		return sort;
	}
	
	public void setSort(int sort) {
		this.sort = sort;
	}
}