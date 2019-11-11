package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BodyPart {
	private String bodyPartCode;
	private String name;

	public String getBodyPartCode() {
		return bodyPartCode;
	}

	public void setBodyPartCode(String bodyPartCode) {
		this.bodyPartCode = bodyPartCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void load(ResultSet rs) throws SQLException {
		bodyPartCode = rs.getString("body_part_code");
		name = rs.getString("name");
	}
}