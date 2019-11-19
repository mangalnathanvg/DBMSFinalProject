package beans;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient {

	private int patientId;
	private String firstName;
	private String lastName;
	private java.sql.Date dateOfBirth;
	private long phoneNumber;

	private Address address;

	public Patient() {
	}

	public Patient(String firstName, String lastName, Date dateOfBirth, long phoneNumber, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

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

	public void load(ResultSet rs, boolean loadAddress) throws SQLException {
		patientId = rs.getInt("patient_id");
		firstName = rs.getString("first_name");
		lastName = rs.getString("last_name");
		dateOfBirth = rs.getDate("date_of_birth");
		phoneNumber = rs.getLong("phone_number");
		if (loadAddress) {
			address = new Address();
			address.load(rs);
		}
	}

	public void save(Connection conn) throws SQLException {
		String sql = "INSERT INTO patient(first_name,last_name,date_of_birth,phone_number,address_id) VALUES (?,?,?,?,?)";
		String[] primaryKey = { "patient_id" };
		PreparedStatement ps = conn.prepareStatement(sql, primaryKey);
		ps.setString(1, firstName);
		ps.setString(2, lastName);
		ps.setDate(3, dateOfBirth);
		ps.setLong(4, phoneNumber);
		ps.setInt(5, address.getAddressId());
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next()) {
			patientId = rs.getInt(1);
		}
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

}