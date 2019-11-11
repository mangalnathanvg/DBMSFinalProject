package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {

	private int addressId;
	private int addNumber;
	private String state;
	private String city;
	private String streetName;
	private String country;

	public int getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(int addNumber) {
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
}
