package beans;

public class ReferralReason {
	
    private int referralReasonId;
    private int reasonCode;
    private String description;
    private int referralId;
    
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
    
    

}
