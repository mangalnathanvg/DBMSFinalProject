import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import beans.BodyPart;
import beans.MedicalFacility;
import beans.Patient;
import beans.Rule;
import beans.SeverityScale;
import beans.SeverityScaleValue;
import beans.Staff;
import beans.Symptom;

public class Application {

	static final String jdbcURL = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	static Connection conn = null;

	static Patient checkedInPatient = null;
	static Staff checkedInStaff = null;

	static HashMap<String, BodyPart> bodyParts = new HashMap<String, BodyPart>();
	static HashMap<String, Symptom> symptoms = new HashMap<String, Symptom>();
	static HashMap<Integer, SeverityScale> severityScales = new HashMap<Integer, SeverityScale>();
	static HashMap<Integer, SeverityScaleValue> severityScaleValues = new HashMap<Integer, SeverityScaleValue>();
	static HashMap<Integer, Rule> rules = new HashMap<Integer, Rule>();
	static HashMap<Integer, MedicalFacility> facilities = new HashMap<Integer, MedicalFacility>();

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

			// SETUP GLOBALS HASHMAPS
			loadFacilities();
			loadSeverityScales();
			loadBodyParts();
			loadSymptoms();
			loadRules();

			displayHome();

			System.out.println("");

			conn.close();
		} catch (Exception e) {
//			System.out.println("Error occured: " + e);
			e.printStackTrace();
		} finally {

		}
	}

	private static void loadFacilities() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT * FROM medical_facility f INNER JOIN address a ON f.address_id = a.address_id");

		while (rs.next()) {
			MedicalFacility facility = new MedicalFacility();
			facility.load(rs);
			facilities.put(facility.getFacilityId(), facility);
		}
	}

	private static void loadSeverityScales() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM severity_scale ss INNER JOIN severity_scale_value ssv "
				+ "ON ss.severity_scale_id = ssv.severity_scale_id");

		while (rs.next()) {
			int id = rs.getInt("severity_scale_id");
			SeverityScale scale = severityScales.get(Integer.valueOf(id));
			if (scale == null) {
				scale = new SeverityScale();
			}
			scale.load(rs);
			severityScales.put(scale.getSeverityScaleId(), scale);
			// add to severity scale value map. we need this to load rule symptom and
			// symptom metadata
			SeverityScaleValue scaleValue = new SeverityScaleValue();
			scaleValue.load(rs);
			severityScaleValues.put(scaleValue.getSeverityValueId(), scaleValue);
		}
	}

	private static void loadSymptoms() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM symptom");
		while (rs.next()) {
			Symptom symptom = new Symptom();
			symptom.load(rs, bodyParts, severityScales);
			symptoms.put(symptom.getSymptomCode(), symptom);
		}
	}

	private static void loadBodyParts() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM body_part");

		while (rs.next()) {
			BodyPart bodyPart = new BodyPart();
			bodyPart.load(rs);
			bodyParts.put(bodyPart.getBodyPartCode(), bodyPart);
		}
	}

	private static void loadRules() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT r.*,rs.* FROM rule r INNER JOIN rule_consists rc ON r.rule_id = rc.rule_id "
						+ "INNER JOIN rule_symptom rs ON rc.rule_symptom_id = rs.rule_symptom_id");

		while (rs.next()) {
			int id = rs.getInt("rule_id");
			Rule rule = rules.get(Integer.valueOf(id));
			if (rule == null) {
				rule = new Rule();
			}
			rule.load(rs, bodyParts, symptoms, severityScaleValues);
			rules.put(rule.getRuleId(), rule);
		}

	}

	private static void displayHome() throws Exception {
		int choice = 0;
		StringBuilder sb = null;
		System.out.println("Medical Facility Performance Dashboard Application");
		while (true) {
			sb = new StringBuilder();
			sb.append("\nPlease choose from the below options:\n");
			sb.append("1. Sign-in\n");
			sb.append("2. Sign-up (patient)\n");
			sb.append("3. Demo queries\n");
			sb.append("5. Add Symptoms\n");
			sb.append("6. Add Severity Scale\n");
			sb.append("7. Exit\n");
			System.out.println(sb.toString());

//			while (true) {
//				choice = validateNumber(br.readLine(), 1, 7);
//				if (choice != -1) {
//					break;
//				}
//			}
			choice = readNumber(1, 7);

			if (choice == 1) {
				displaySignIn();
			} else if (choice == 2) {
				displaySignUp();
			} else if (choice == 3) {
				displayDemoQueries();
			} else if (choice == 5) {
				addSymptoms();
			} else if (choice == 6) {
				addSeverityScale();
			} else if (choice == 7) {
				break;
			}
		}

	}

	private static void displayDemoQueries() {

	}

	private static void displaySignUp() {

	}

	private static int readNumber(int min, int max) throws IOException {
		int choice = -1;
		while (true) {
			try {
				choice = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number:");
				choice = -1;
			}
			if (choice != -1 && (choice < min || choice > max)) {
				System.out.println("Please enter a valid choice:");
			} else if (choice != -1) {
				break;
			}
		}
		return choice;
	}

	private static char readChar(String[] options) throws IOException {
		String str;
		while (true) {
			String ch = br.readLine();
			boolean found = false;
			for (String option : options) {
				if (ch.equalsIgnoreCase(option)) {
					found = true;
				}
			}
			if (found) {
				str = ch;
				break;
			} else {
				System.out.println("Please enter valid option:");
				str = null;
			}
		}
		return str.charAt(0);
	}

	private static Date readDate() {
		Date dt = null;
		while (true) {
			try {
				dt = Date.valueOf(br.readLine());
			} catch (Exception e) {
				System.out.println("Please enter a valid date in the specified format:");
			}
			if (dt != null) {
				break;
			}
		}
		return dt;
	}

	private static void displaySignIn() throws Exception {
		int choice = 0;
		StringBuilder sb = null;

		// TODO: Check if already signed in

		while (true) {
			System.out.println("\n===| Sign-in |===\n");

			String city, name;
			System.out.println("Please enter the details as prompted");
			System.out.println("\nFacility (Select from below options): ");
			int idx = 1;
			ArrayList<MedicalFacility> facilityList = new ArrayList<MedicalFacility>();
			facilityList.addAll(facilities.values());
			for (MedicalFacility facility : facilityList) {
				System.out.println(idx++ + " - " + facility.getName());
			}
			int facilityIndex = 0;
//			while (true) {
//				facilityIndex = validateNumber(br.readLine(), 1, facilityList.size());
//				if (facilityIndex != -1) {
//					break;
//				}
//			}
			facilityIndex = readNumber(1, facilityList.size());
			int facilityId = facilityList.get(facilityIndex - 1).getFacilityId();

			System.out.println("Patient? (y/n):");
			String[] options = new String[] { "n", "y" };
			char patient = readChar(options);
			boolean isPatient = (patient == 'y' || patient == 'Y');
			if (isPatient) {
				System.out.println("Last Name:");
			} else {
				System.out.println("Name:");
			}
			name = br.readLine();

			System.out.println("Date of birth (YYYY-MM-DD):");
			Date dateOfBirth = readDate();

			System.out.println("City of address:");
			city = br.readLine();

			System.out.println("\nPlease choose from the below options:");
			sb = new StringBuilder();
			sb.append("1. Sign-in\n");
			sb.append("2. Go back\n");
			System.out.println(sb.toString());

			choice = readNumber(1, 2);
			if (choice == 1) {
				if (isPatient) {
					checkedInPatient = loadPatient(name, dateOfBirth, city);
					// TODO : load check in to see if returning to do feedback, else create new
					// check in with facilityId
					if (checkedInPatient != null) {
						System.out.println("\nLogged in successfully.\n");
						displayPatientRouting();

					}
				} else {
					System.out.println("\nLogged in successfully.\n");
					checkedInStaff = loadStaff(name, dateOfBirth, city, facilityId);
					displayStaffMenu();
				}
				if (checkedInPatient == null && checkedInStaff == null) {
					System.out.println("Sign-in incorrect\n");
				} else {
					break;
				}
			} else if (choice == 2) {
				break;
			}

		}
	}

	private static Staff loadStaff(String lname, Date dateOfBirth, String city, int facilityId) throws SQLException {
		Staff staff = null;
		String sql = "SELECT s.* FROM staff s INNER JOIN medical_facility f on s.facility_id = f.facility_id INNER JOIN address a ON f.address_id = a.address_id "
				+ "WHERE upper(s.name) = upper(?) AND upper(a.city) = upper(?) AND to_char(s.date_of_birth, 'YYYY-MM-DD') = ? and f.facility_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, lname);
		ps.setString(2, city);
		ps.setString(3, dateOfBirth.toString());
		ps.setInt(4, facilityId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			staff = new Staff();
			staff.load(rs);
		}
		return staff;
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

	// Devi - Method to display Staff Menu.
	private static void displayStaffMenu() throws Exception {
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = true;

		while (flag) {
			System.out.println("\n===| Staff Menu |===\n");

			System.out.println("\nPlease choose one of the following options:\n");
			sb = new StringBuilder();
			sb.append("1. Checked-in Patient List\n");
			sb.append("2. Treated Patient List\n");
			sb.append("3. Add symptoms\n");
			sb.append("4. Add severity scale\n");
			sb.append("5. Add assessment rule\n");
			sb.append("6. Go back\n");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {
				staffProcessPatient();
			} else if (choice == 2) {
				// generate outcome report
				staffCheckOutPatient();
			} else if (choice == 3) {
				addSymptoms();
			} else if (choice == 4) {
				addSeverityScale();
			} else if (choice == 5) {
				addAssessmentRule();
			} else if (choice == 6) {
				break;
			} else {
				System.out.println("Invalid option! Please choose from the available options.");
				continue;
			}
			flag = false;
		}
	}

	private static void staffProcessPatient() {
		System.out.println("Staff Process Patient Page");
	}

	private static void staffCheckOutPatient() {
		System.out.println("Staff Checkout Patient Page");
	}

	private static void displayPatientRouting() {
		System.out.println("\n===| P |===\n");
	}

	private static void addAssessmentRule() {
		System.out.println("Add Assessment Rule Page");
	}

	// Mangal - Method to add new symptom to the database.
	private static void addSymptoms() throws Exception {

		int choice, severityID = 0;
		String symptomName = "", bodyPartAssocCode = "", temp = "";
		boolean flag = true;

		System.out.println("\n===| Add Symptoms |===\n");

		// Taking symptom name as input.
		while (flag) {
			System.out.println("Please enter the details as prompted");
			System.out.println("\n Symptom Name: ");
			symptomName = br.readLine();

			// Validating the symptom name entered
			if (symptomName.isEmpty()) {
				System.out.println("Symptom Name cannot be empty!");
				continue;
			}
			/*
			 * Flag indicates whether the loop must continue or not. The loop will not
			 * continue if the correct symptom name is entered
			 */
			flag = false;
		}

		flag = true;

		// Choosing associated body part.
		while (flag) {
			System.out
					.println("\n Body Part associated (Select option and press enter or press enter to leave blank): ");
			int i = 1;

			// Body Parts options taken from master list bodyParts.
			ArrayList<String> bpoptions = new ArrayList<String>();
			for (String bpcode : bodyParts.keySet()) {
				bpoptions.add(bpcode);
				String value = bodyParts.get(bpcode).getName().toString();
				System.out.println(Integer.toString(i) + ") " + bpcode + " - " + value);
				i++;
			}
			System.out.print("Choice: ");
			temp = br.readLine();

			if (!temp.isEmpty()) {
				choice = -1;
				// Validating the choice entered.
				try {
					choice = Integer.parseInt(temp);
				} catch (NumberFormatException e) {
					System.out.println("Invalid option entered!");
					continue;
				}
				if (choice < 0 || choice > bpoptions.size()) {
					System.out.println("Invalid Choice entered!");
					continue;
				}
				// Getting the selected bodyPart object from the master list.
				bodyPartAssocCode = bodyParts.get(bpoptions.get(choice - 1)).getBodyPartCode().toString();
			} else if (temp.isEmpty()) {
				// If no body part is associated.
				bodyPartAssocCode = "No";
			}
			flag = false;
		}

		flag = true;

		// Choosing the severity scale for the symptom.
		while (flag) {
			System.out.println("Choose the severity scale below (Press Enter if you want to leave it blank)");

			int i = 1;
			// Displaying Severity scales from the master list.
			ArrayList<Integer> svcoptions = new ArrayList<Integer>();
			for (int sv : severityScales.keySet()) {
				svcoptions.add(sv);
				String value = severityScales.get(sv).getName().toString();
				System.out.println(Integer.toString(i) + ") " + sv + " - " + value);
				i++;
			}
			System.out.print("Choice: ");
			temp = br.readLine();

			// Validating the choice entered.
			if (!temp.isEmpty()) {
				choice = -1;
				try {
					choice = Integer.parseInt(temp);
				} catch (NumberFormatException e) {
					System.out.println("Invalid option entered!");
					continue;
				}
				if (choice <= 0 || choice > svcoptions.size()) {
					System.out.println("Invalid Choice entered!");
					continue;
				}
				// Getting the selected severity scale from the master list.
				severityID = svcoptions.get(choice - 1);
			} else {
				severityID = 0;
			}
			flag = false;
		}

		flag = true;
		// SymptomName , BodyPartAssocCode and SeverityID is associated successfully.
		// Entering these values to the database accordingly.
		while (flag) {
			System.out.println("1) Record");
			System.out.println("2) Go Back");
			System.out.print("\nChoice: ");
			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {

				PreparedStatement ps = null;
				String sql;

				if (bodyPartAssocCode.compareTo("No") == 0 && severityID == 0) {
					sql = "INSERT INTO SYMPTOM(NAME) values (?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, symptomName);
				} else if (bodyPartAssocCode.compareTo("No") == 0) {
					sql = "INSERT INTO SYMPTOM(NAME, SEVERITY_SCALE_ID) values (?, ?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, symptomName);
					ps.setString(2, Integer.toString(severityID));
				} else if (severityID == 0) {
					sql = "INSERT INTO SYMPTOM(NAME, BODY_PART_CODE) values (?, ?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, symptomName);
					ps.setString(2, bodyPartAssocCode);
				} else {
					sql = "INSERT INTO SYMPTOM(NAME, SEVERITY_SCALE_ID, BODY_PART_CODE) values ( ? , ? , ? )";
					ps = conn.prepareStatement(sql);
					ps.setString(1, symptomName);
					ps.setString(2, Integer.toString(severityID));
					ps.setString(3, bodyPartAssocCode);
				}

				ResultSet rs = ps.executeQuery();
				if (rs != null) {
					System.out.println("Recorded Symptoms Successfully");
				} else {
					System.out.println("Unable to record symptoms");
				}

			} else if (choice != 2) {
				System.out.println("Invalid Option selected!");
				continue;
			}
			flag = false;
		}

	}

	// Mangal - Method to add new severity scale.
	private static void addSeverityScale() throws Exception {
		String severityScaleName = "";
		int choice;
		boolean flag = true;

		System.out.println("\n===| Add Severity Scale |===\n");

		// Recording the name of the severity scale.
		while (flag) {
			System.out.println("Enter the name of the Severity scale to be added:");
			severityScaleName = br.readLine();
			// Validating the name of the severity scale.
			if (severityScaleName.isEmpty()) {
				System.out.println("Please enter a severity scale name");
				continue;
			}
			flag = false;
		}

		flag = true;
		ArrayList<String> scaleValues = new ArrayList<String>();
		ArrayList<String> order = new ArrayList<String>();
		// Displaying previously added scale values.
		while (flag) {
			if (!scaleValues.isEmpty()) {
				System.out.println("Current scale values with order for " + severityScaleName + ": ");
				Iterator<String> itr = scaleValues.iterator();
				Iterator<String> ito = order.iterator();
				while (itr.hasNext() && ito.hasNext()) {
					System.out.println("Rank " + ito.next() + " - " + itr.next());
				}
			}

			// Entering new scale and its order.
			StringBuilder sb = new StringBuilder();
			System.out.println("Select an option below:");
			sb.append("1. Add new scale value\n");

			if (!scaleValues.isEmpty()) {
				sb.append("2. No more scales to add\n");
			}
			System.out.println(sb.toString());
			choice = -1;

			try {
				System.out.print("\nChoice: ");
				choice = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid choice!");
				continue;
			}

			if (choice == 1) {

				boolean f = true;
				String scname = null, temp = null;
				while (f) {
					System.out.println("Enter scale value to be added (Numeric or Alphanumeric): ");
					temp = br.readLine();

					if (scaleValues.contains(temp)) {
						System.out.println("Scale Value already exists for this table! Please enter new scale value");
						continue;
					}
					scaleValues.add(temp);
					scname = temp;
					f = false;
				}

				f = true;

				while (f) {
					System.out.println("Enter the order of the scale value" + scname + " (Numeric):");
					temp = br.readLine();
					try {
						int a = Integer.parseInt(temp);
					} catch (NumberFormatException e) {
						System.out.println("Order must be a number!");
						continue;
					}

					if (order.contains(temp)) {
						System.out.println("Order already assigned to another scale value");
						continue;
					}

					f = false;
				}
				order.add(temp);
				continue;
			}

			if (choice > 2) {
				System.out.println("Invalid Choice!");
				continue;
			}
			flag = false;
		}

		// Validating if scale has scale values. Will not be recorded to the database if
		// scale has no values.
		if (!scaleValues.isEmpty()) {

			PreparedStatement ps = null;
			String sql;
			// Adding severity scale details
			sql = "INSERT INTO SEVERITY_SCALE(NAME) values (?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, severityScaleName);
			ResultSet rs = ps.executeQuery();

			sql = "SELECT SEVERITY_SCALE_ID from SEVERITY_SCALE WHERE NAME = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, severityScaleName);

			ResultSet rs1 = ps.executeQuery();
			String scaleid = null;
			while (rs1.next()) {
				scaleid = rs1.getString("SEVERITY_SCALE_ID");
			}

			// Adding severity scale value details of the severity scale entered above.
			sql = "INSERT INTO SEVERITY_SCALE_VALUE(SCALE_VALUE, SEVERITY_SCALE_ID, SORT) values(?,?,?)";

			Iterator<String> itr = scaleValues.iterator();
			Iterator<String> ito = order.iterator();

			while (itr.hasNext() && ito.hasNext()) {
				ps = conn.prepareStatement(sql);
				ps.setString(1, itr.next());
				ps.setString(2, scaleid);
				ps.setString(3, ito.next());
				rs = ps.executeQuery();
			}

			System.out.println("Added new severity scale with values");

		} else {
			System.out.println("Cannot record a Severity Scale without any scale values!");
		}
	}

}
