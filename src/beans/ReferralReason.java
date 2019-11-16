package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReferralReason {
	
    private int referralReasonId;
    private int reasonCode;
    private String description;
    private int referralId;
    private String name_of_service;
    
	public int getReferralReasonId() {
		return referralReasonId;
	}
	public void setReferralReasonId(int referralReasonId) {
		this.referralReasonId = referralReasonId;
	}
	public int getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getReferralId() {
		return referralId;
	}
	public void setReferralId(int referralId) {
		this.referralId = referralId;
	}
	
	public void load(ResultSet rs) throws SQLException {
		referralReasonId = rs.getInt("referral_reason_id");
		reasonCode = rs.getInt("reason_code");
		description = rs.getString("description");
		referralId = rs.getInt("referral_id");
		name_of_service = rs.getString(name_of_service);
	}
    
    

}

