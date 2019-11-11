package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient {

	private int patientId;
	private String firstName;
	private String lastName;
	private java.sql.Date dateOfBirth;
	private long phoneNumber;

	private Address address;

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public java.sql.Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(java.sql.Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void load(ResultSet rs) throws SQLException {
		patientId = rs.getInt("patient_id");
		firstName = rs.getString("first_name");
		lastName = rs.getString("last_name");
		dateOfBirth = rs.getDate("date_of_birth");
		phoneNumber = rs.getLong("phone_number");
		address = new Address();
		address.load(rs);
	}

}