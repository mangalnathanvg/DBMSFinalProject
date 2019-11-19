package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReferralReason {

	private int referralReasonId;
	private int reasonCode;
	private String description;
	private int referralId;
	private String serviceName;

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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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
		serviceName = rs.getString("service_name");
	}

	public void insert(Connection conn) throws SQLException {
		String sql = "INSERT INTO referral_reason " + "(reason_code, description, referral_id, service_name)"
				+ "values(?, ?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, reasonCode);
		stmt.setString(2, description);
		stmt.setInt(3, referralId);
		stmt.setString(4, serviceName);
		stmt.executeUpdate();

		stmt.close();
	}
}
