package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalFacility {

	private int facilityId;
	private String name;
	private int capacity;
	private int classification;

	private Address address;

	public MedicalFacility() {
		address = new Address();
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getClassification() {
		return classification;
	}

	public void setClassification(int classification) {
		this.classification = classification;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void load(ResultSet rs) throws SQLException {
		facilityId = rs.getInt("facility_id");
		name = rs.getString("name");
		capacity = rs.getInt("capacity");
		classification = rs.getInt("classification");
		address.load(rs);
	}
}