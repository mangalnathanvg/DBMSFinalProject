import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Application {

	static final String jdbcURL = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	static Connection conn = null;

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
			System.out.println("Error occured: " + e);
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
		PreparedStatement ps = null;
		String sql;

		while (choice != 2) {
			sb = new StringBuilder();
			sb.append("\n1. Sign-in\n");
			sb.append("2. Go back");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {
				String facilityId, dob, city, lname, patient;
				System.out.println("\nPlease enter the following information:\nFacility ID");
				facilityId = br.readLine();
				System.out.println("Last Name");
				lname = br.readLine();
				System.out.println("Date of birth");
				dob = br.readLine();
				System.out.println("City of address");
				city = br.readLine();
				System.out.println("Patient? (y/n)");
				patient = br.readLine();

				boolean isPatient = patient.equalsIgnoreCase("y");
				if (isPatient) {
					// not the right query
					sql = "SELECT * FROM patient p INNER JOIN address a ON p.address_id = a.address_id WHERE p.last_name = ? AND p.date_of_birth = ? AND a.city = ?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, lname);
					ps.setString(2, dob);
					ps.setString(3, city);
				} else {
					// handle for staff
				}

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					// set global variable patient/staff or pass in the object (to set context)
					if (isPatient) {
						displayPatientRouting();
					} else {
						// need to check if medical staff
						displayStaffMenu();
					}
				} else {
					System.out.println("Sign-in incorrect");
				}

			}
		}
	}

	private static void displayStaffMenu() {
		// TODO Auto-generated method stub

	}

	private static void displayPatientRouting() {
		// TODO Auto-generated method stub

	}
}
