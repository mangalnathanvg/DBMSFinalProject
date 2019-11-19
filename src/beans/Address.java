package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {

	private int addressId;
	private long addNumber;
	private String streetName;
	private String city;
	private String state;
	private String country;

	public Address(long addNumber, String streetName, String city, String state, String country) {
		this.addNumber = addNumber;
		this.streetName = streetName;
		this.city = city;
		this.state = state;
		this.country = country;
	}

	public Address() {
	}

	public long getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(long addNumber) {
		this.addNumber = addNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void load(ResultSet rs) throws SQLException {
		addressId = rs.getInt("address_id");
		addNumber = rs.getInt("add_number");
		state = rs.getString("state");
		city = rs.getString("city");
		streetName = rs.getString("street_name");
		country = rs.getString("country");
	}

	public void save(Connection conn) throws SQLException {
		String[] primaryKey = { "address_id" };
		String sql = "INSERT INTO address(add_number,street_name,city,state,country) VALUES (?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql, primaryKey);
		ps.setLong(1, addNumber);
		ps.setString(2, streetName);
		ps.setString(3, city);
		ps.setString(4, state);
		ps.setString(5, country);

		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next()) {
			addressId = rs.getInt(1);
		}
	}
}
