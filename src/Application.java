import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import beans.Patient;
import beans.Staff;

public class Application {

	static final String jdbcURL = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	static Connection conn = null;

	static Patient checkedInPatient = null;
	static Staff checkedInStaff = null;

	public static void main(String[] args) {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			System.out.println("Driver missing!");
		}

		String user = "zmathew";
		String passwd = "200251210";

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			Statement stmt = conn.createStatement();

			// demo read code
			ResultSet rs = stmt.executeQuery("select * from cat");

			// Demo for reading to a variable
			// Rule rule = new Rule();
			// rule.setRuleId(rs.getInt("rule_id"));
			// rule.setPriority(rs.getString("priority").charAt(0));

			while (rs.next()) {
				String name = rs.getString("name");
				System.out.println(name);
			}

			displayHome();

			System.out.println("");

			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
//			System.out.println("Error occured: " + e);
			e.printStackTrace();
		} finally {

		}
	}

	private static void displayHome() throws Exception {
		int choice = 0;
		StringBuilder sb = null;

		while (choice != 4) {
			sb = new StringBuilder();
			sb.append("\n1. Sign-in\n");
			sb.append("2. Sign-up (patient)\n");
			sb.append("3. Demo queries\n");
			sb.append("4. Exit");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {
				displaySignIn();
			} else if (choice == 2) {
				displaySignUp();
			} else if (choice == 3) {
				displayDemoQueries();
			}
		}

	}

	private static void displayDemoQueries() {

	}

	private static void displaySignUp() {

	}

	private static void displaySignIn() throws Exception {
		int choice = 0;
		StringBuilder sb = null;

		while (choice != 2) {

			String facilityId, dob, city = null, lname = null, patient;
			System.out.println("\nPlease enter the following information:\nFacility ID");
			facilityId = br.readLine();
			System.out.println("Patient? (y/n)");
			patient = br.readLine();
			boolean isPatient = patient.equalsIgnoreCase("y");
			if (isPatient) {
				System.out.println("Last Name");
			} else {
				System.out.println("Name");
			}
			lname = br.readLine();
			System.out.println("Date of birth (YYYY-MM-DD)");
			dob = br.readLine();
			System.out.println("City of address");
			city = br.readLine();

			Date dateOfBirth = Date.valueOf(dob);

			System.out.println("\nPlease choose from the below options:");
			sb = new StringBuilder();
			sb.append("1. Sign-in\n");
			sb.append("2. Go back\n");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {
				if (isPatient) {
					checkedInPatient = loadPatient(lname, dateOfBirth, city);
					if (checkedInPatient != null) {
						displayPatientRouting();
					}
				} else {
					checkedInStaff = loadStaff(lname, dateOfBirth, city);
					displayStaffMenu();
				}
			}
			if (checkedInPatient == null && checkedInStaff == null) {
				System.out.println("Sign-in incorrect\n");
			} else {
				break;
			}

		}
	}

	private static Staff loadStaff(String lname, Date dateOfBirth, String city) {
		return null;
	}

	private static Patient loadPatient(String lname, Date dateOfBirth, String city) throws Exception {
		Patient patient = null;
		String sql = "SELECT * FROM patient p INNER JOIN address a ON p.address_id = a.address_id "
				+ "WHERE upper(p.last_name) = upper(?) AND upper(a.city) = upper(?) AND to_char(p.date_of_birth, 'YYYY-MM-DD') = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, lname);
		ps.setString(2, city);
		ps.setString(3, dateOfBirth.toString());
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			patient = new Patient();
			patient.load(rs);
		}
		return patient;
	}

	private static void displayStaffMenu() {
		// TODO Auto-generated method stub

	}

	private static void displayPatientRouting() {
		System.out.println("Logged in");
	}
}
