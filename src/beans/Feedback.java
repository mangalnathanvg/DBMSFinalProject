package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Feedback {

	private int feedbackId;
	private String description;

	public int getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void insert(Connection conn) throws SQLException {
		String[] primaryKey = { "feedback_id" };
		String sql = "INSERT INTO feedback(description) VALUES (?)";
		PreparedStatement ps = conn.prepareStatement(sql, primaryKey);
		ps.setString(1, description);
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next()) {
			feedbackId = rs.getInt(1);
		}
	}
}
