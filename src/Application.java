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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import beans.Address;
import beans.BodyPart;
import beans.CheckIn;
import beans.MedicalFacility;
import beans.OutcomeReport;
import beans.Patient;
import beans.Rule;
import beans.RuleSymptom;
import beans.SeverityScale;
import beans.SeverityScaleValue;
import beans.Staff;
import beans.Symptom;
import beans.SymptomMetadata;

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
	static HashMap<Integer, RuleSymptom> ruleSymptoms = new HashMap<Integer, RuleSymptom>(); 

	static BodyPart dummyBodyPart = new BodyPart();

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
			e.printStackTrace();
		}
	}

	private static void loadFacilities() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT * FROM medical_facility f INNER JOIN address a ON f.address_id = a.address_id");

		facilities = new HashMap<Integer, MedicalFacility>();
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

		severityScales = new HashMap<Integer, SeverityScale>();
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

		bodyParts = new HashMap<String, BodyPart>();
		while (rs.next()) {
			BodyPart bodyPart = new BodyPart();
			bodyPart.load(rs);
			if (bodyPart.getBodyPartCode().contentEquals(BodyPart.DUMMY_BODY_PART_CODE)) {
				dummyBodyPart = bodyPart;
			} else {
				bodyParts.put(bodyPart.getBodyPartCode(), bodyPart);
			}
		}
	}

	private static void loadRules() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT r.*,rs.* FROM rule r INNER JOIN rule_consists rc ON r.rule_id = rc.rule_id "
						+ "INNER JOIN rule_symptom rs ON rc.rule_symptom_id = rs.rule_symptom_id");

		rules = new HashMap<Integer, Rule>();
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
	private static int loadRuleSymptomsForIndex() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rule_symptom");
		int lastIndex = 0 ;

		while (rs.next()) {
			RuleSymptom ruleSym = new RuleSymptom();
			ruleSym.load(rs,bodyParts,symptoms,severityScaleValues);
			ruleSymptoms.put(ruleSym.getRuleSymptomId(), ruleSym);
			lastIndex = Math.max(lastIndex, ruleSym.getRuleSymptomId());
		}
		
		return lastIndex;
	}
	
	private static int loadRulesForIndex() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rule");
		int lastIndex = 0 ;

		while (rs.next()) {
			lastIndex = Math.max(lastIndex,rs.getInt("rule_id"));
		}
		
		return lastIndex;
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

	private static long readLong(int size) throws IOException {
		long choice = -1;
		String line = null;
		while (true) {
			try {
				line = br.readLine();
				choice = Long.parseLong(line);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number:");
				choice = -1;
			}
			if (choice != -1 && (line.length() != size)) {
				System.out.println("Please enter a valid choice:");
			} else if (choice != -1) {
				break;
			}
		}
		return choice;
	}

	private static String readString(String[] options) throws IOException {
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
		return str;
	}

	private static String readNonEmptyString() throws IOException {
		String str;
		while (true) {
			str = br.readLine();
			if (!str.isEmpty()) {
				break;
			}
			System.out.println("Please enter a valid option:");
		}
		return str;
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

	private static void displaySignUp() throws Exception {
		int choice = 0;
		StringBuilder sb = null;

		while (true) {
			System.out.println("\n===| Sign-Up (Patient) |===\n");

			System.out.println("Please enter the details of the Patient as prompted");
			System.out.println("\nFirst name: ");
			String fname = readNonEmptyString();

			System.out.println("\nLast name: ");
			String lname = readNonEmptyString();

			System.out.println("\nPhone number (e.g. 9999999999): ");
			long phone = readLong(10);

			System.out.println("Date of birth (YYYY-MM-DD):");
			Date dateOfBirth = readDate();

			System.out.println("\nPlease enter the details of the Address as prompted");
			System.out.println("\nAddress number: ");
			long addNumber = readLong(10);

			System.out.println("Street: ");
			String street = readNonEmptyString();

			System.out.println("city: ");
			String city = readNonEmptyString();

			System.out.println("State: ");
			String state = readNonEmptyString();

			System.out.println("Country: ");
			String country = readNonEmptyString();

			System.out.println("\nPlease choose from the below options:");
			sb = new StringBuilder();
			sb.append("1. Sign-up\n");
			sb.append("2. Go back\n");
			System.out.println(sb.toString());

			choice = readNumber(1, 2);
			if (choice == 1) {
				Address address = new Address(addNumber, street, city, state, country);
				address.save(conn);
				Patient patient = new Patient(fname, lname, dateOfBirth, phone, address);
				patient.save(conn);
			} else if (choice == 2) {
				break;
			}

		}
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
			int facilityIdx = readNumber(1, facilityList.size());
			int facilityId = facilityList.get(facilityIdx - 1).getFacilityId();

			System.out.println("Patient? (y/n):");
			String[] options = new String[] { "n", "y" };
			char patient = readString(options).charAt(0);
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
						displayPatientRouting(facilityId);

					}
				} else {
					System.out.println("\nLogged in successfully.\n");
					checkedInStaff = loadStaff(name, dateOfBirth, city, facilityId);
					if (checkedInStaff != null && checkedInStaff.isMedical()) {
						displayStaffMenu();
					}
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
				+ "WHERE upper(s.name) = upper(?) AND upper(a.city) = upper(?) AND to_char(s.date_of_birth, 'YYYY-MM-DD') = ? and s.facility_id = ?";
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
			sb.append("6 List of Treated patients\n");
			sb.append("7. Go back\n");
			System.out.println(sb.toString());

			// TODO: check if medical staff, else show invalid privileges error

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
				treatedPatient();
			} else if (choice == 7) {
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

	private static void displayPatientRouting(int facilityId) throws Exception {
		System.out.println("\n===| Patient Routing |===\n");

		while (true) {
			System.out.println("\nPlease choose one of the following options:\n");
			StringBuilder sb = new StringBuilder();
			sb.append("1. Check-in\n");
			sb.append("2. Check-out acknowledgement\n");
			sb.append("3. Go back\n");
			System.out.println(sb.toString());

			CheckIn checkinUnderProcess = loadCheckinUnderProcess(checkedInPatient.getPatientId(), facilityId);
			int choice = 0;
			while (true) {
				choice = readNumber(1, 3);
				// TODO : verify logic
				if (choice == 2 && (checkinUnderProcess == null
						|| checkinUnderProcess.getTreatment().getTreatmentTime() == null)) {
					System.out.println("Cannot checkout without being treated.");
				} else if (checkinUnderProcess != null && choice == 1) {
					System.out.println("Cannot checkin without checking out first.");
				} else {
					break;
				}
			}
			if (choice == 1) {
				checkinUnderProcess = new CheckIn();
				checkinUnderProcess.setFacilityId(facilityId);
				checkinUnderProcess.setPatientId(checkedInPatient.getPatientId());
				checkinUnderProcess.save(conn);
				displayPatientCheckIn(checkinUnderProcess);
			} else if (choice == 2) {
				displayPatientAcknowledgement(checkinUnderProcess);
			} else if (choice == 3) {
				break;
			}
		}
	}

	private static void displayPatientAcknowledgement(CheckIn checkin) {

	}

	private static void displayPatientCheckIn(CheckIn checkin) throws Exception {
		System.out.println("\n===| Patient Check-in |===\n");
		ArrayList<SymptomMetadata> metadataList = new ArrayList<SymptomMetadata>();

		while (true) {
			Symptom symptom = null;
			System.out.println("\nPlease choose one of the following symptoms:\n");
			int idx = 1;
			ArrayList<Symptom> symptomList = new ArrayList<Symptom>();
			symptomList.addAll(symptoms.values());
			for (Symptom s : symptomList) {
				System.out.println(idx++ + " - " + s.getName());
			}
			System.out.println(idx++ + " - Other");
			System.out.println(idx + " - Done");
			int choice = readNumber(1, symptomList.size() + 2);
			if (choice == idx) {
				if (metadataList.isEmpty()) {
					System.out.println("Please enter atleast 1 symptom.");
					continue;
				}
				for (SymptomMetadata metadata : metadataList) {
					metadata.save(conn);
				}
				checkin.setStartTime(new Timestamp(System.currentTimeMillis()));
				checkin.save(conn);
				System.out.println("\nCheck-in started at " + checkin.getStartTime());
				break;
			}
			if (choice != (idx - 1)) {
				symptom = symptomList.get(choice - 1);
			}
			SymptomMetadata metadata = displaySymptomMetadata(checkin, symptom);
			metadataList.add(metadata);
		}
	}

	private static SymptomMetadata displaySymptomMetadata(CheckIn checkin, Symptom symptom) throws IOException {
		SymptomMetadata metadata = new SymptomMetadata();
		metadata.setCheckInId(checkin.getCheckInId());

		System.out.println("Please enter the details as prompted");
		if (symptom != null) {
			metadata.setSymptomCode(symptom.getSymptomCode());
			if (!symptom.hasBodyPart()) {
				System.out
						.println("Please choose one of the following body parts associated with " + symptom.getName());
				ArrayList<BodyPart> bodyPartList = new ArrayList<BodyPart>();
				bodyPartList.addAll(bodyParts.values());
				int idx = 1;
				for (BodyPart b : bodyPartList) {
					System.out.println(idx++ + " - " + b.getName());
				}
				int choice = readNumber(1, bodyPartList.size());
				BodyPart bodyPart = bodyPartList.get(choice - 1);
				metadata.setBodyPartCode(bodyPart.getBodyPartCode());
			}
		} else {
			System.out.println("Description:");
			String description = readNonEmptyString();
			metadata.setDescription(description);
		}

		System.out.println("Duration (in days)");
		metadata.setDurationDays(readNumber(0, 999));

		System.out.println("Reoccurring? (y/n)");
		String[] options = new String[] { "n", "y" };
		char option = readString(options).charAt(0);
		int firstOccurrence = (option == 'y' || option == 'Y') ? 0 : 1;
		metadata.setFirstOccurrence(firstOccurrence);

		System.out.println("Please choose one of the following severity values associated with " + symptom.getName());
		ArrayList<SeverityScaleValue> severityValueList = new ArrayList<SeverityScaleValue>();
		SeverityScale severityScale = severityScales.get(symptom.getSeverityScale().getSeverityScaleId());
		severityValueList.addAll(severityScale.getSeverityScaleValues());
		int idx = 1;
		for (SeverityScaleValue b : severityValueList) {
			System.out.println(idx++ + " - " + b.getScaleValue());
		}
		int choice = readNumber(1, severityValueList.size());
		SeverityScaleValue severityValue = severityValueList.get(choice - 1);
		metadata.setSeverityScaleValueId(severityValue.getSeverityScaleId());

		System.out.println("Cause:");
		String cause = readNonEmptyString();
		metadata.setCause(cause);

		return metadata;
	}

	private static CheckIn loadCheckinUnderProcess(int patientId, int facilityId) throws SQLException {
		CheckIn checkIn = null;
		String sql = "SELECT * FROM (SELECT * FROM check_in c INNER JOIN patient p ON c.patient_id = p.patient_id "
				+ "INNER JOIN medical_facility m ON m.facility_id = c.facility_id LEFT JOIN vital_signs v ON v.check_in_id = v.check_in_id "
				+ "LEFT JOIN treatment t ON t.check_in_id = c.check_in_id WHERE c.patient_id = ? AND m.facility_id = ? AND t.treatment_time is null) WHERE ROWNUM = 1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, patientId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			checkIn = new CheckIn();
			checkIn.load(rs);
		}
		return checkIn;
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

		private static void addAssessmentRule(){
			int choice = 0;
			int ruleCount = 0;
			boolean flag = true;
			StringBuilder sb = null;
			try {
				while(flag){
					System.out.println("\nPlease choose one of the following options:\n");
					sb = new StringBuilder();
					sb.append("1. want to add more conditions in current Rule \n");		
					sb.append("2. go back\n");
					System.out.println(sb.toString());
			 
					choice = Integer.parseInt(br.readLine());
					if (choice == 1) {
						if(ruleCount>0)
						addRuleCondition(false,true);
						else
						addRuleCondition(true,true);	
						System.out.println("Condition successfully added!");
					} 
				
					else if (choice == 2) {
							displayHome();
						} 
					else {
						System.out.println("Invalid option! Please choose from the available options.");
						continue;
					}
					ruleCount++;
				}
		}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	private static void addRuleCondition(boolean newRule,boolean newRuleSymbol) {
		try {
			
		    int count = 0;
		    ArrayList<RuleSymptom> ruleSymList = new ArrayList<RuleSymptom>();
			Rule rule = new Rule();
			boolean prioritySelected = true;
			
			while(prioritySelected) {
				HashMap<Integer,Symptom> symptomList = new HashMap<Integer,Symptom> ();
				RuleSymptom ruleSym = new RuleSymptom();
		    
				for (Map.Entry<String,Symptom> symptom : symptoms.entrySet()) {
					symptomList.put(++count, symptom.getValue());
				}
				System.out.println("Choose symptom");
				for (Map.Entry<Integer,Symptom> symptom : symptomList.entrySet()) {
					System.out.println(count+" : "+symptom.getValue().getName());
				}
				System.out.println(count+1 +" : Select Priority");
				int choice = Integer.parseInt(br.readLine());
				if(choice == count+1) {
					prioritySelected = selectPriority(ruleSymList, rule);
				}
				choice = readNumber(1, count);
				String selectedSymptom = symptomList.get(choice).getSymptomCode();
				ruleSym.setSymptom(symptomList.get(choice));
			
				if(symptomList.get(choice).getBodyPart() == null)
				{
					count = 0;
					HashMap<Integer,BodyPart> partList = new HashMap<Integer,BodyPart> ();
					for (Map.Entry<String,BodyPart> part : bodyParts.entrySet()) {
						partList.put(++count, part.getValue());
					}
				
					System.out.println("No Body is associated with the symtom. Choose body part of your choice!");
					for (Map.Entry<Integer,BodyPart> part : partList.entrySet()) {
						System.out.println(part.getKey()+" : "+part.getValue().getName());
					}
					choice = Integer.parseInt(br.readLine());
					choice = readNumber(1, count);
					ruleSym.setBodyPart(partList.get(choice));
				}
				else 
				{
				ruleSym.setBodyPart(symptomList.get(choice).getBodyPart());
				}
			
				String selectedSeverityScale = null;
				boolean flag = true;
				if(symptoms.get(selectedSymptom).getSeverityScale() == null) {
					System.out.println("No Severity Scale is associated with this Sysmptom. Choose one from below!");
					while(flag) {
						System.out.println("1. Present \n 2.Absent");
						choice = Integer.parseInt(br.readLine());
						if(choice == 1) 
							{
							selectedSeverityScale = "Present";
							flag = false;}
						else if(choice == 2) 
						{
						selectedSeverityScale = "Absent";
						flag = false; }
						else
						{
						System.out.println("Choose a valid Symptom");
						flag = true;
						}
					}
					for (Map.Entry<Integer,SeverityScaleValue> value : severityScaleValues.entrySet()) {
						if(value.getValue().getScaleValue() == selectedSeverityScale)
							ruleSym.setScaleValue(value.getValue());
					}
				}
			else {
				loadSeverityScales();
				count = 0;
				HashMap<Integer,SeverityScaleValue> valueList = new HashMap<Integer,SeverityScaleValue> ();
				for (Map.Entry<Integer,SeverityScaleValue> value : severityScaleValues.entrySet())
					valueList.put(++count,value.getValue());
					
				for (Map.Entry<Integer,SeverityScaleValue> value : valueList.entrySet())
					System.out.println(value.getKey() +" : "+value.getValue().getScaleValue());
				
				choice = Integer.parseInt(br.readLine());
				ruleSym.setScaleValue(valueList.get(choice));
			}
			
			System.out.println("Enter Comparison Symbol : < , > , =");
			boolean isValid = true;
			char comparison = 0 ;
			while(isValid) {
				comparison = br.readLine().charAt(0);
				if (!Arrays.asList('>','<','=').contains(comparison))
					{
					System.out.println("Enter valid comparsion symbol < , > , =");
					isValid = true;
					}
				else
					isValid = false;
				}
			ruleSym.setComparisonSymbol(comparison);
			ruleSymList.add(ruleSym);
			}

			
		} catch (Exception e) {
			System.out.println("Error occured: " + e);
		} 
	}
	private static boolean selectPriority(ArrayList<RuleSymptom> ruleSymlist, Rule rule) {
		try {
			if(ruleSymlist.size() == 0 || ruleSymlist.get(0).getSymptom() == null )
				return false;
	    ResultSet rs;
		PreparedStatement ps = null;
		System.out.println("Enter Priority : H , L , Q");
		boolean isValid = true;
		char priority = 0 ;
		while(isValid) {
			priority = br.readLine().charAt(0);
			if (!Arrays.asList('H','L','Q').contains(priority))
				{
				System.out.println("Enter valid comparsion symbol H , L , Q");
				isValid = true;
				}
			else
				isValid = false;
			}

		rule.setPriority(priority);
		String sql = "INSERT INTO rule(priority) values ( to_char(?) )";
		ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, String.valueOf(rule.getPriority()));
		ps.executeUpdate();
		rs = ps.getGeneratedKeys();
		if (rs.next()) {
			rule.setRuleId(rs.getInt(1));
		}

		for(RuleSymptom ruleSym:ruleSymlist) {
			
			sql = "INSERT INTO rule_symptom(comparison_symbol,symptom_code, scale_value_id, body_part_code) values ( to_char(?) , ? , ? , ?)";
			ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, String.valueOf(ruleSym.getComparisonSymbol()));
			ps.setString(2, ruleSym.getSymptom().getSymptomCode());
			ps.setInt(3, ruleSym.getScaleValue().getSeverityValueId());
			ps.setString(4, ruleSym.getBodyPart().getBodyPartCode());
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				ruleSym.setRuleSymptomId(rs.getInt(1));
			}
			sql = "INSERT INTO rule_consists(rule_id,rule_symptom_id) values(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, rule.getRuleId());
			ps.setInt(2, ruleSym.getRuleSymptomId());
			rs = ps.executeQuery();
		}
		loadRules();

	} catch (Exception e) {
		System.out.println("Error occured: " + e);
	}
		return true;
		
	}

	private static void treatedPatient() {
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = false;
		int counter = 0;

		try {
			System.out.println("List of treated patients:\n");
			String sql = "SELECT p.patient_id, ci.check_in_id, p.first_name, p.last_name FROM treatment trm INNER JOIN check_in ci "
					+ "on trm.check_in_id = ci.check_in_id INNER JOIN patient p ON ci.patient_id=p.patient_id INNER JOIN outcome_report or "
					+ "on or.check_in_id != ci.check_in_id "
					+ "WHERE trm.medical_staff_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, checkedInStaff.getStaffId());
			ResultSet rs = ps.executeQuery();
			HashMap<Integer, Integer> treatedPatientList = new HashMap<>();
			while (rs.next()) {
				counter++;
				System.out.println(counter+": "+ rs.getString(3) + rs.getString(4));
				treatedPatientList.put(counter,rs.getInt(2));
			}
			if(counter>0) {
				flag = true;
				System.out.println("Choose patient from the list");	
				choice = Integer.parseInt(br.readLine());
				sb = new StringBuilder();
				sb.append("1. Checkout\n");		
				sb.append("2. Go back\n");
				System.out.println(sb.toString());
				choice = Integer.parseInt(br.readLine());
				choice = readNumber(1, 2);
					while(flag) {
						if (choice == 1) {
							patientCheckout();
						}
						else if (choice ==2) {
							displayHome();
						}
						else {
							System.out.println("Enter valid choice");
							flag = true;
						}
					}
			}
			else if(counter==0) {
				flag = false;
				System.out.println("List has no Patients to show");
				displayHome();
			}
			else {
				System.out.println("Entered Choice is not Valid");
			}
		}
		catch(Exception e) {
			System.out.println("Error occured: " + e);
		}
	}

	private static void patientCheckout() {
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = true;
		OutcomeReport report = new OutcomeReport();
		
		try {
		System.out.println("Choose options:\n");
		sb = new StringBuilder();
		sb.append("1. Discharge Status\n");	
		sb.append("2. Referal Status\n");
		sb.append("3. Treatment\n");
		sb.append("4. Negative Experience\n");
		sb.append("5. Patient Confirmation\n");
		sb.append("6. Go back\n");
		sb.append("7. Submit\n");
		System.out.println(sb.toString());
		choice = Integer.parseInt(br.readLine());
		
		choice = readNumber(1, 6);
		while(flag) {
		
			if (choice == 1) {
				report = dischargeStatus(report);
				System.out.println("Discharge Status added successfully");
				patientCheckout();
			}
			else if (choice == 2) {
				report = referralStatus();
			}
			else if (choice == 3) {
				report = addTreatmentDescription(report);
				System.out.println("Description added successfully");
				patientCheckout();
			}
			else if (choice == 6) {
				displayHome();
			}
			else if (choice == 4) {
				report = negativeExperience();
				System.out.println("Negative Experince added successfully");
				patientCheckout();
			}
			else if (choice == 5) {
				report = patientConfirmation(report);
				System.out.println("Patients Confirmation added successfully");
				patientCheckout();
			}
			else if (choice == 7) {
				if(submitReport(report)) {
					System.out.println("Report submitted seccessfully");
					flag = false;
				}
				else{
					patientCheckout();
					flag = true;
				}
			}
			else {
				System.out.println("Enter valid choice");
				flag = true;
			}
		}
	}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static OutcomeReport patientConfirmation(OutcomeReport report) {
		
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = true;
		try {
			System.out.println("Patient needs to provide confirmation!");
			sb = new StringBuilder();
			sb.append("1. Yes\n");
			sb.append("2. No\n");
			sb.append("3. Go back\n");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());
			choice = readNumber(1, 3);
			while(flag) {
				
				if(choice==1) {
					report.setPatientConfirmation(1);
					flag = false;
				}
				else if(choice==2) {
					report.setPatientConfirmation(0);
					flag = false;
				}
				else if(choice==3) {
					flag = false;
					patientCheckout();
				}
				else {
					System.out.print("Choose valid option");
					flag = true;
				}
			}
		}
			catch (Exception e) {
				e.printStackTrace();
			}
		return report;
	}

	private static boolean submitReport(OutcomeReport report) {
		
		try 
		{
			if(report.getDischargeStatus() == 0)
				{
				System.out.println("Discharge Status is not valid");
				return false;
				}
			
			else 
			{
				java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		
				String sql = "INSERT INTO outcome_report(discharge_status,treatment_description, patient_confirmation,generation_time,referral_id,feedback_id) "
				+ "values(to_char(?),?,?,?,?,?)";
		
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, report.getDischargeStatus());
				ps.setString(2, report.getTreatmentDescription());
				ps.setInt(3,report.getPatientConfirmation());
				ps.setTimestamp(4,currentTimestamp);
				ps.setInt(5, report.getReferralId());
				ps.setInt(6, report.getFeedbackId());
				ps.executeQuery();
			
		} 
			}
		catch (SQLException e) {
				e.printStackTrace();
				}	
		return true;
	}

	private static OutcomeReport addTreatmentDescription(OutcomeReport report) {
		try {
			
			System.out.println("Enter treatment description for selected patient:\n");
			String description = br.readLine();
			report.setTreatmentDescription(description);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return report;
	}

	private static OutcomeReport negativeExperience() {
		return null;
		// TODO Auto-generated method stub
	}

	private static OutcomeReport referralStatus() {
		return null;
		// TODO Auto-generated method stub
	}

	private static OutcomeReport dischargeStatus(OutcomeReport report) {
		
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = true;
		char status = 'N';
		
		try {
		System.out.println("List of treated patients:\n");
		sb = new StringBuilder();
		sb.append("1. Successful treatment\n");	
		sb.append("2. Deceased\n");
		sb.append("3. Referred\n");
		sb.append("4. Go back\n");
		System.out.println(sb.toString());
		choice = Integer.parseInt(br.readLine());
		
		choice = readNumber(1, 4);
		while(flag) {
		
			if (choice == 1) {
				status = 'S';
				flag = false;
			}
			else if(choice == 2)
			{
				status = 'D';
				flag = false;
			}
			else if(choice == 3) {
				status = 'R';
				flag = false;
			}
			else if(choice == 4) {
				flag = false;
				patientCheckout();
			}
			else {
				System.out.println("Enter valid choice");
				flag = true;
			}
			
		}
		if(status != 'N') {
			report.setDischargeStatus(status);
		}
	}catch(Exception e){
			e.printStackTrace();
		}
		
		return report;
	}

}
