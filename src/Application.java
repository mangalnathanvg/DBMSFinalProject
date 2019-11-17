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
import beans.Feedback;
import beans.MedicalFacility;
import beans.NegativeExperience;
import beans.OutcomeReport;
import beans.Patient;
import beans.ReferralReason;
import beans.ReferralStatus;
import beans.Rule;
import beans.RuleSymptom;
import beans.SeverityScale;
import beans.SeverityScaleValue;
import beans.Staff;
import beans.Symptom;
import beans.SymptomMetadata;
import beans.Treatment;
import beans.VitalSigns;

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

	public static void main(String[] args) throws SQLException {

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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
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

		stmt.close();
		rs.close();
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

		stmt.close();
		rs.close();
	}

	private static void loadSymptoms() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM symptom");
		while (rs.next()) {
			Symptom symptom = new Symptom();
			symptom.load(rs, bodyParts, severityScales);
			symptoms.put(symptom.getSymptomCode(), symptom);
		}

		stmt.close();
		rs.close();
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

		stmt.close();
		rs.close();
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

		stmt.close();
		rs.close();

	}

	private static int loadRuleSymptomsForIndex() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rule_symptom");
		int lastIndex = 0;

		while (rs.next()) {
			RuleSymptom ruleSym = new RuleSymptom();
			ruleSym.load(rs, bodyParts, symptoms, severityScaleValues);
			ruleSymptoms.put(ruleSym.getRuleSymptomId(), ruleSym);
			lastIndex = Math.max(lastIndex, ruleSym.getRuleSymptomId());
		}

		stmt.close();
		rs.close();

		return lastIndex;
	}

	private static int loadRulesForIndex() throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rule");
		int lastIndex = 0;

		while (rs.next()) {
			lastIndex = Math.max(lastIndex, rs.getInt("rule_id"));
		}

		stmt.close();
		rs.close();

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
			sb.append("4. Exit\n");
			System.out.println(sb.toString());

			choice = readNumber(1, 4);

			if (choice == 1) {
				displaySignIn();
			} else if (choice == 2) {
				displaySignUp();
			} else if (choice == 3) {
				displayDemoQueries();
			} else if (choice == 4) {
				break;
			}
		}

	}

	private static void displayDemoQueries() {

	}

	private static void displaySignUp() throws Exception {
		int choice = 0;
		StringBuilder sb = null;

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
			// TODO display message
		}
	}

	private static void displaySignIn() throws Exception {
		int choice = 0;
		StringBuilder sb = null;
		// TODO null cehckedIn people

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
					checkedInStaff = loadStaff(name, dateOfBirth, city, facilityId);
					if (checkedInStaff != null && checkedInStaff.isMedical()) {
						System.out.println("\nLogged in successfully.\n");
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
		ps.close();
		rs.close();
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
			patient.load(rs, true);
		}

		ps.close();
		rs.close();

		return patient;
	}

	// Devi - Method to display Staff Menu.
	private static void displayStaffMenu() throws Exception {
		StringBuilder sb = null;
		int choice = 0;

		while (true) {
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

			// TODO: check if medical staff, else show invalid privileges error

			choice = readNumber(1, 6);
			if (choice == 1) {
				staffProcessPatient();
			} else if (choice == 2) {
				treatedPatient();
			} else if (choice == 3) {
				addSymptoms();
			} else if (choice == 4) {
				addSeverityScale();
			} else if (choice == 5) {
				addAssessmentRule();
			} else if (choice == 6) {
				System.out.println("Invalid choice.\n");
				break;
			}
		}
	}

	private static void staffProcessPatient() throws Exception {
		StringBuilder sb = null;
		int choice = 0;

		int counter = 0;
		CheckIn selectedCheckIn = null;

		// Display list of patients who have finished self check-in
		String sql = "SELECT C.check_in_id, P.first_name, P.last_name, P.date_of_birth, P.phone_number, C.start_time FROM check_in C INNER JOIN patient P "
				+ "ON P.patient_id = C.patient_id LEFT JOIN treatment t ON t.check_in_id = c.check_in_id LEFT JOIN vital_signs v ON  v.check_in_id = c.check_in_id WHERE t.check_in_id IS NULL AND c.facility_id = ?";

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery(sql);
		stmt.setInt(1, checkedInStaff.getPrimaryDepartment(conn).getFacilityId());
		System.out.println("List of checked-in patients:\n");

		HashMap<Integer, CheckIn> checkedInPatientList = new HashMap<>();
		while (rs.next()) {
			CheckIn checkIn = new CheckIn();
			checkIn.load(rs, true);
			System.out.println(counter++ + ". " + checkIn.getPatient().getFullName());
			checkedInPatientList.put(counter, checkIn);
		}
		if (counter > 0) {
			System.out.println("Choose patient from the list");
			choice = readNumber(1, checkedInPatientList.size());
			selectedCheckIn = checkedInPatientList.get(choice);
			sb = new StringBuilder();
			sb.append("1. Enter Vitals\n");
			sb.append("2. Treat Patient\n");
			sb.append("3. Go back\n");

			System.out.println(sb.toString());
			choice = Integer.parseInt(br.readLine());

			choice = readNumber(1, 2);

			while (true) {
				if (choice == 1) {
					if (selectedCheckIn.getVitalSigns().getCheckInID() != 0) {
						staffEnterVitals(selectedCheckIn);
					} else {
						System.out.println("Vitals already entered for patient.");
					}
				} else if (choice == 2) {
					boolean treatable = false;
					ArrayList<String> treatableBodyParts = checkedInStaff.getTreatableBodyParts(conn);
					ArrayList<SymptomMetadata> metadata = selectedCheckIn.getSymptomMetadata(conn);
					for (SymptomMetadata metadatum : metadata) {
						if (treatableBodyParts.contains(metadatum.getBodyPartCode())) {
							treatable = true;
							break;
						}
					}
					if (treatable) {
						treatPatient(selectedCheckIn);
						break;
					} else {
						System.out.println("Inadequate privileges.\n");
					}
				} else if (choice == 3) {
					break;
				}
			}
		} else {
			System.out.println("List has no Patients to show");
		}
	}

	private static void treatPatient(CheckIn checkIn) throws SQLException {
		Treatment treatment = new Treatment();
		treatment.setCheckInId(checkIn.getCheckInId());
		treatment.setMedicalStaffId(checkedInStaff.getStaffId());
		treatment.setTreatmentTime(new Timestamp(System.currentTimeMillis()));
		treatment.insert(conn);
	}

	private static void staffEnterVitals(CheckIn checkIn) throws Exception {
		StringBuilder sb = new StringBuilder();
		System.out.println("Please enter the following details as prompted:\n");
		System.out.print("Temperature: ");
		int temp = readNumber(1, 110);
		System.out.print("\nSystolic Pressure: ");
		int systolic = readNumber(60, 200);
		System.out.print("\nDiastolic Pressure: ");
		int diastolic = readNumber(30, 110);

		// end time for check-in process
		System.out.println("\nMenu:\n");
		sb.append("1. Record\n");
		sb.append("2. Go Back\n");
		System.out.println(sb.toString());

		int choice = readNumber(1, 2);
		if (choice == 1) {
			checkIn.setEndTime(new Timestamp(System.currentTimeMillis()));
			checkIn.save(conn);

			VitalSigns vitalSigns = new VitalSigns(checkIn.getCheckInId(), checkedInStaff.getStaffId(), temp, systolic,
					diastolic);
			vitalSigns.save(conn);

			System.out.println("Priority assigned: " + stampPriority(checkIn));

			// go back
		}
	}

	private static String stampPriority(CheckIn checkIn) throws SQLException {
		ArrayList<SymptomMetadata> metadata = checkIn.getSymptomMetadata(conn);
		char priority = 'N';
		for (Rule rule : rules.values()) {
			boolean verified = assessRule(metadata, rule);
			if (verified) {
				priority = maxPriority(priority, rule.getPriority());
			}
		}
		checkIn.setPriority(priority);
		checkIn.save(conn);
		return "";
	}

	private static boolean assessRule(ArrayList<SymptomMetadata> metadata, Rule rule) {
		for (RuleSymptom ruleSymptom : rule.getRuleSymptoms()) {
			for (SymptomMetadata metadatum : metadata) {
				if (!metadatum.getSymptomCode().equals(ruleSymptom.getSymptom().getSymptomCode())) {
					continue;
				} else if (!metadatum.getBodyPartCode().equals(ruleSymptom.getBodyPart().getBodyPartCode())) {
					continue;
				}
			}
		}
		return true;
	}

	private static char maxPriority(char p1, char p2) {
		if (p1 == 'Q' || p2 == 'Q') {
			return 'Q';
		} else if (p1 == 'H' || p2 == 'H') {
			return 'H';
		}
		return 'N';
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

	private static void displayPatientAcknowledgement(CheckIn checkin) throws Exception {
		System.out.println("\n===| Patient Acknowledgement |===\n");
		OutcomeReport report = loadOutcomeReport(checkin.getCheckInId());
		displayReport(report);

		System.out.println("\nPlease choose one of the following options:\n");
		StringBuilder sb = new StringBuilder();
		sb.append("1. Yes\n");
		sb.append("2. No\n");
		sb.append("3. Go back\n");
		System.out.println(sb.toString());

		int choice = readNumber(1, 3);
		if (choice == 1) {
			report.setPatientConfirmation(1);
			report.save(conn);
		} else if (choice == 2) {
			Feedback feedback = new Feedback();
			System.out.println("Please enter some feedback:");
			feedback.setDescription(readNonEmptyString());
			feedback.insert(conn);
			report.setFeedbackId(feedback.getFeedbackId());
			report.save(conn);
		}
	}

	private static OutcomeReport loadOutcomeReport(int checkInId) throws SQLException {
		OutcomeReport report = null;
		String sql = "SELECT * FROM outcome_report r WHERE r.check_in_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, checkInId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			report = new OutcomeReport();
			report.load(rs);
		}

		ps.close();
		rs.close();

		return report;
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
					metadata.insert(conn);
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
				+ "LEFT JOIN outcome_report r ON r.check_in_id = c.check_in_id LEFT JOIN treatment t ON t.check_in_id = c.check_in_id "
				+ "WHERE c.patient_id = ? AND m.facility_id = ? AND r.patient_confirmation IS NULL) WHERE ROWNUM = 1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, patientId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			checkIn = new CheckIn();
			checkIn.load(rs, false);
		}

		ps.close();
		rs.close();

		return checkIn;
	}

	private static void displayReport(OutcomeReport report) throws SQLException {
		System.out.println("\n===| Outcome Report |===\n");
		System.out.println("\nDischarge status: " + report.getDischargeStatusName());

		if (report.isReferred()) {
			ReferralStatus referralStatus = report.getReferralStatus(conn);
			if (referralStatus != null) {
				int facilityId = referralStatus.getFacilityId();
				System.out.println("Facility name: " + facilities.get(facilityId).getName());
				System.out.println("Referrer name: " + getReferrerName(referralStatus.getMedicalStaffId()));
				ArrayList<ReferralReason> reasons = referralStatus.getReasons(conn);
				if (reasons != null) {
					System.out.println("Referral reasons:");
					for (ReferralReason reason : reasons) {
						System.out.println("Reason code: " + reason.getReasonCode());
						System.out.println("Service name: " + reason.getServiceName());
						System.out.println("Description: " + reason.getDescription());
						System.out.println();
					}
				}
			}

			System.out.println("Treatment description: " + report.getTreatmentDescription());
			NegativeExperience negativeExperience = report.getNegativeExperience(conn);
			if (negativeExperience != null) {
				System.out.println("\nNegative experience:");
				System.out.println("Negative experience code: " + negativeExperience.getExperienceCodeName());
				System.out.println("Description: " + negativeExperience.getDescription());
			}
		}
	}

	private static String getReferrerName(int medicalStaffId) throws SQLException {
		String name = "";
		String sql = "SELECT name FROM staff WHERE staff_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, medicalStaffId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			name = rs.getString("name");
		}

		ps.close();
		rs.close();

		return name;
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
				rs.close();
				ps.close();
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
				System.out.println("Current scale values with order (Higher the number, greater the severity) for "
						+ severityScaleName + ": ");
				Iterator<String> itr = scaleValues.iterator();
				Iterator<String> ito = order.iterator();
				while (itr.hasNext() && ito.hasNext()) {
					System.out.println(ito.next() + " - " + itr.next());
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
			ps.close();
			rs.close();
		} else {
			System.out.println("Cannot record a Severity Scale without any scale values!");
		}
	}

	private static void addAssessmentRule() {
		int choice = 0;
		int ruleCount = 0;
		boolean flag = true;
		StringBuilder sb = null;
		try {
			while (flag) {
				System.out.println("\nPlease choose one of the following options:\n");
				sb = new StringBuilder();
				sb.append("1. want to add more conditions in current Rule \n");
				sb.append("2. go back\n");
				System.out.println(sb.toString());

				choice = Integer.parseInt(br.readLine());
				if (choice == 1) {
					if (ruleCount > 0)
						addRuleCondition(false, true);
					else
						addRuleCondition(true, true);
					System.out.println("Condition successfully added!");
				}

				else if (choice == 2) {
					displayHome();
				} else {
					System.out.println("Invalid option! Please choose from the available options.");
					continue;
				}
				ruleCount++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addRuleCondition(boolean newRule, boolean newRuleSymbol) {
		try {

			int counter = 0;
			String sql = "select * from symptom";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			HashMap<Integer, Symptom> symptomList = new HashMap<Integer, Symptom>();
			while (rs.next()) {
				++counter;
				Symptom symptom = new Symptom();
				symptom.setName(rs.getString("NAME"));
				sql = "SELECT bp.body_part_code,bp.name FROM body_part bp INNER JOIN symptom sm ON sm.body_part_code = bp.body_part_code WHERE sm.body_part_code = ? ";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, rs.getString("body_part_code"));
				ResultSet rs1 = ps.executeQuery();
				rs1.next();
				BodyPart part = new BodyPart();
				part.setBodyPartCode(rs1.getString(1));
				part.setName(rs1.getString(2));
				symptom.setBodyPart(part);
				symptom.setSymptomCode(rs.getString("SYMPTOM_CODE"));
				symptomList.put(counter, symptom);
			}
			System.out.println("Choose symptom code to create assessment rules:");

			for (Map.Entry<Integer, Symptom> symptom : symptomList.entrySet()) {
				System.out.println(symptom.getKey() + " : " + symptom.getValue().getName());
			}

			int choice = Integer.parseInt(br.readLine());
			choice = readNumber(1, counter);
			String symptom_code = symptomList.get(choice).getSymptomCode();
			String selectedBodyPart = symptomList.get(choice).getBodyPart().getBodyPartCode();

			sql = "SELECT * FROM severity_scale_value ssv INNER JOIN symptom sm ON sm.severity_scale_id = ssv.severity_scale_id WHERE sm.symptom_code = ? ";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, symptom_code);
			rs = ps.executeQuery();
			HashMap<Integer, SeverityScaleValue> severityList = new HashMap<Integer, SeverityScaleValue>();
			counter = 0;
			int maxScale = 0;
			while (rs.next()) {
				++counter;
				SeverityScaleValue scale = new SeverityScaleValue();
				scale.setSeverityScaleId(rs.getInt(3));
				scale.setScaleValue(rs.getString(2));
				scale.setSort(rs.getInt(4));
				scale.setSeverityValueId(rs.getInt(1));
				if (maxScale < scale.getSort()) {
					maxScale = scale.getSort();
				}
				severityList.put(counter, scale);
			}
			System.out.println("Choose severity scale number for the symbol:");

			for (Map.Entry<Integer, SeverityScaleValue> symptom : severityList.entrySet()) {
				System.out.println(symptom.getKey() + " : " + symptom.getValue().getScaleValue());
			}

			choice = Integer.parseInt(br.readLine());
			choice = readNumber(1, counter);
			int selectedScaleValue = severityList.get(choice).getSeverityValueId();

			System.out.println("Enter Comparison Symbol : < , > , =");
			boolean isValid = true;
			char comparison = 0;
			while (isValid) {
				comparison = br.readLine().charAt(0);
				if (!Arrays.asList('>', '<', '=').contains(comparison)) {
					System.out.println("Enter valid comparsion symbol < , > , =");
					isValid = true;
				} else
					isValid = false;
			}

			System.out.println("Enter Priority : H , L , Q");
			isValid = true;
			char priority = 0;
			while (isValid) {
				priority = br.readLine().charAt(0);
				if (!Arrays.asList('H', 'L', 'Q').contains(priority)) {
					System.out.println("Enter valid comparsion symbol H , L , Q");
					isValid = true;
				} else
					isValid = false;
			}

			if (newRule) {
				sql = "INSERT INTO rule(priority) values ( to_char(?) )";
				ps = conn.prepareStatement(sql);
				ps.setString(1, String.valueOf(priority));
				rs = ps.executeQuery();
			}

			if (newRuleSymbol) {
				sql = "INSERT INTO rule_symptom(comparison_symbol,symptom_code, scale_value_id, body_part_code) values ( to_char(?) , ? , ? , ?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, String.valueOf(comparison));
				ps.setString(2, symptom_code);
				ps.setInt(3, selectedScaleValue);
				ps.setString(4, selectedBodyPart);
				rs = ps.executeQuery();
			}
			sql = "INSERT INTO rule_consists(rule_id,rule_symptom_id) values(?,?)";
			ps = conn.prepareStatement(sql);
			int ruleId = loadRulesForIndex();
			int symbolId = loadRuleSymptomsForIndex();
			ps.setInt(1, ruleId);
			ps.setInt(2, symbolId);
			rs = ps.executeQuery();

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.out.println("Error occured: " + e);

		}

	}

	private static void treatedPatient() {
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = false;
		int counter = 0;
		int selectedCheckInId = 0;

		try {
			System.out.println("List of treated patients:\n");

			Statement stmt = conn.createStatement();
			// TODO need to check if already treated
			ResultSet rs = stmt.executeQuery(
					"SELECT p.patient_id, ci.check_in_id, p.first_name, p.last_name FROM treatment trm INNER JOIN check_in ci on trm.check_in_id = ci.check_in_id INNER JOIN patient p ON ci.patient_id=p.patient_id");
			HashMap<Integer, Integer> treatedPatientList = new HashMap<>();
			while (rs.next()) {
				counter++;
				System.out.println(counter + ": " + rs.getString(3) + rs.getString(4));
				treatedPatientList.put(counter, rs.getInt(2));
			}
			if (counter > 0) {
				flag = true;
				System.out.println("Choose patient from the list");
				choice = Integer.parseInt(br.readLine());
				selectedCheckInId = treatedPatientList.get(choice);
				sb = new StringBuilder();
				sb.append("1. Checkout\n");
				sb.append("2. Go back\n");

				System.out.println(sb.toString());
				choice = Integer.parseInt(br.readLine());

				choice = readNumber(1, 2);

				while (flag) {
					if (choice == 1) {
						patientCheckout();
					} else if (choice == 2) {
						// TODO flag = false?
					} else {
						System.out.println("Enter valid choice");
						flag = true;
					}
				}
			} else if (choice == 0) {
				flag = false;
				System.out.println("List has no Patients to show");
				displayHome();
			} else {
				System.out.println("Entered Choice is not Valid");
			}
			stmt.close();
			rs.close();
		} catch (Exception e) {

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
			while (flag) {

				if (choice == 1) {
					report = dischargeStatus(report);
					System.out.println("Discharge Status added successfully");
//					patientCheckout();
				} else if (choice == 2) {
					report = referralStatus(report);
				} else if (choice == 3) {
					report = addTreatmentDescription(report);
					System.out.println("Description added successfully");
//					patientCheckout();
				} else if (choice == 6) {
					displayHome();
				} else if (choice == 4) {
					addNegativeExperience(report);
//					patientCheckout();
				} else if (choice == 5) {
					report = patientConfirmation(report);
					System.out.println("Patients Confirmation added successfully");
//					patientCheckout();
				} else if (choice == 7) {
					flag = displayStaffReportConfirmation(report);
//					submitReport(report);
//					System.out.println("Report submitted seccessfully");
//					flag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean displayStaffReportConfirmation(OutcomeReport report) throws Exception {
		System.out.println("\nPlease choose one of the following options:\n");
		StringBuilder sb = new StringBuilder();
		sb.append("1. Confirm\n");
		sb.append("2. Go back\n");
		System.out.println(sb.toString());

		int choice = readNumber(1, 2);
		if (choice == 1) {
			report.save(conn);
			System.out.println("Report submitted successfully");
		}
		return (choice == 1);
	}

	// TODO: Remove this. Needs to be done by patient
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
			while (flag) {

				if (choice == 1) {
					report.setPatientConfirmation(1);
					flag = false;
				} else if (choice == 2) {
					report.setPatientConfirmation(0);
					flag = false;
				} else if (choice == 3) {
					flag = false;
					patientCheckout();
				} else {
					System.out.print("Choose valid options");
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return report;
	}

	private static void submitReport(OutcomeReport report) {

		try {

			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

			String sql = "INSERT INTO outcome_report(discharge_status,treatment_description, patient_confirmation,generation_time,referral_id,feedback_id) "
					+ "values(to_char(?),?,?,?,?,?)";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, report.getDischargeStatus());
			ps.setString(2, report.getTreatmentDescription());
			ps.setInt(3, report.getPatientConfirmation());
			ps.setTimestamp(4, currentTimestamp);
			ps.setInt(5, report.getReferralId());
			ps.setInt(6, report.getFeedbackId());
			ps.executeQuery();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	private static void addNegativeExperience(OutcomeReport report) throws IOException {
		System.out.println("\n===| Negative Experience |===\n");
		System.out.println("\nPlease choose one of the following options:");
		System.out.println("1. Misdiagnosis");
		System.out.println("2. Patient acquired an infection during hospital stay");
		int expCode = readNumber(1, 2);
		System.out.println("Please enter a description:");
		String description = readNonEmptyString();

		System.out.println("\nPlease choose one of the following options:");
		System.out.println("1. Confirm");
		System.out.println("2. Go back");
		int choice = readNumber(1, 2);
		if (choice == 1) {
			NegativeExperience negativeExperience = new NegativeExperience(expCode, description);
			report.setNegativeExperience(negativeExperience);
			System.out.println("Negative Experince added successfully");
			// TODO haven't written to the db because not sure if it is at this point
		}
	}

	private static OutcomeReport referralStatus(OutcomeReport report) throws Exception {
		boolean flag = true;
		StringBuilder sb = new StringBuilder();
		int choice = 0;
		ArrayList<ReferralReason> reasons = new ArrayList<ReferralReason>();
		int referral_reason_id = 0;
		int reason_code = 0;
		int referral_id = 0;
		int facilityID = 0;
		String description = null;
		String name_of_service = null;

		ReferralStatus referralStatus = new ReferralStatus();
		referralStatus.setReferralId(report.getReferralId());

		System.out.println("\n===| Referral Status |===\n");
		System.out.println("\nPlease choose one of the following options:\n");

		while (flag) {
			sb.append("1. Enter Facility ID\n");
			sb.append("2. Enter Referrer ID\n");
			sb.append("3. Add reason\n");
			sb.append("4. Go back\n");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());

			if (choice == 1) {
				System.out.println("\nPlease enter facility ID (Enter 0 if there is no specific facility): ");
				facilityID = Integer.parseInt(br.readLine());
				referralStatus.setFacilityId(facilityID);
				continue;

			} else if (choice == 2) {
				if (facilityID != 0) {
					System.out.println("\nPlease enter referrer ID: ");
					int referrerID = Integer.parseInt(br.readLine());
					referralStatus.setMedicalStaffId(referrerID);

					// record values in referral_status table
					String sql = "INSERT INTO referral_status" + "(refID, facilityID) " + "values(?, ?)";
					PreparedStatement stmt = conn.prepareStatement(sql);

					stmt.setInt(1, referrerID);
					stmt.setInt(2, facilityID);
					stmt.executeUpdate();

					stmt.close();
				} else {
					System.out.println("Facility ID must be entered before attempting to enter referrer ID.");
					continue;
				}
			} else if (choice == 3) {
				// check if the number of associated reasons with the referral is less than or
				// equal to 4
				if (reasons.size() <= 4) {
					ReferralReason reason = new ReferralReason();
					reason.setReferralId(referralStatus.getReferralId());
					reasons.add(referralReason(reason));
				}
				// error message in case number of reasons is 4 already
				else {
					System.out.println("A referral cannot have more than 4 reasons associated with it.");
					continue;
				}
			} else {
				System.out.println("Invalid option. Please choose from the existing options.");
				continue;
			}
			flag = false;
		}
		report.setReferralStatus(referralStatus);
		report.setReferralId(referral_id);
		return report;

	}

	private static ReferralReason referralReason(ReferralReason reason) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean flag = true;
		int choice = 0;

		System.out.println("\nPlease enter the following information:\n");
		System.out.print("Reason code: ");
		int reason_code = Integer.parseInt(br.readLine());
		reason.setReasonCode(reason_code);

		System.out.print("\nName of service: ");
		String name_of_service = readNonEmptyString();
		reason.setServiceName(name_of_service);

		System.out.print("\nDescription: ");
		String description = readNonEmptyString();
		reason.setDescription(description);

		while (flag) {
			System.out.println("\n===| Referral Reason Menu |===\n");
			sb.append("\n1. Record reason\n");
			sb.append("2. Go back\n");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());

			if (choice == 1) {
				// record information in referral_reason table
				String sql = "INSERT INTO referral_reason " + "(reason_code, description, referral_id, name_of_service)"
						+ "values(?, ?, ?, ?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, reason_code);
				stmt.setString(2, description);
				stmt.setInt(3, reason.getReferralId());
				stmt.setString(4, name_of_service);
				stmt.executeUpdate();

				stmt.close();
			} else if (choice == 2) {
				break;
			} else {
				System.out.println("\nInvalid option. Please choose one of the existing options.\n");
				continue;
			}
			flag = false;
		}
		return reason;
	}

	private static OutcomeReport dischargeStatus(OutcomeReport report) {
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = true;
		char status = 'N';

		try {
			System.out.println("Discharge status:\n");
			sb = new StringBuilder();
			sb.append("1. Successful treatment\n");
			sb.append("2. Deceased\n");
			sb.append("3. Referred\n");
			sb.append("4. Go back\n");
			System.out.println(sb.toString());
			choice = Integer.parseInt(br.readLine());

			choice = readNumber(1, 4);
			while (flag) {

				if (choice == 1) {
					status = 'S';
					flag = false;
				} else if (choice == 2) {
					status = 'D';
					flag = false;
				} else if (choice == 3) {
					status = 'R';
					flag = false;
				} else if (choice == 4) {
					flag = false;
					patientCheckout();
				} else {
					System.out.println("Enter valid choice");
					flag = true;
				}

			}
			if (status != 'N') {
				report.setDischargeStatus(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return report;
	}

}
