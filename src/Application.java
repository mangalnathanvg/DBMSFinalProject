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
			System.out.println("=========== Closed connection ===========");
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

	private static long readLong(int size, char sym) throws IOException {
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
			if (choice != -1) {
				if (sym == '=' && (line.length() != size)) {
					System.out.println("Please enter a valid choice with size = " + size + ":");
				} else if (sym == '<' && (line.length() > size)) {
					System.out.println("Please enter a valid choice with size < " + size + ":");
				} else {
					break;
				}
				continue;
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

	private static java.sql.Timestamp validateTimeStamp() {
		java.sql.Timestamp timeStamp = null;
		while (true) {
			try {
				timeStamp = java.sql.Timestamp.valueOf(br.readLine());
			} catch (Exception e) {
				System.out.println("Please enter a valid timestamp in the specified format yyyy-MM-dd hh:mm:ss:");
			}
			if (timeStamp != null) {
				break;
			}
		}
		return timeStamp;

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

	private static void displayDemoQueries() throws Exception {
		int choice = 0;
		StringBuilder sb = null;
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		String sql = null;
		System.out.println("DEMO QUERIES");
		while (true) {
			sb = new StringBuilder();
			sb.append("\nPlease choose Query from the below options:\n");
			sb.append("1. Find all patients that were discharged but had negative experiences at any facility\n");
			sb.append("2. Find facilities that did not have a negative experience for a specific period \n");
			sb.append("3.  For each facility, find the facility that sends the most referrals to.\n");
			sb.append("4. Find facilities that had no negative experience for patients with cardiac symptoms\n");
			sb.append("5. Find the facility with the most number of negative experiences\n");
			sb.append(
					"6. Find each facility, list the patient encounters with the top five longest check-in phases \n");
			sb.append("7. Exit\n");
			System.out.println(sb.toString());

			choice = readNumber(1, 7);

			if (choice == 1) {
				rs = stmt.executeQuery(
						"SELECT p.first_name , p.last_name , c.start_time, otr.generation_time, ne.description "
								+ "FROM patient p INNER JOIN check_in c ON p.patient_id = c.patient_id "
								+ "INNER JOIN outcome_report otr ON c.check_in_id = otr.check_in_id "
								+ "INNER JOIN negative_experience ne ON otr.report_id = ne.report_id "
								+ "WHERE otr.patient_confirmation IS NOT NULL");

				if (!rs.isBeforeFirst()) {
					System.out.println("Query returns no rows!");
					continue;
				}

				System.out.println("First_Name	Last_Name	Check-In Date	Discharge Date	Negative experiences");
				while (rs.next()) {
					System.out.println(rs.getString(1) + "	" + rs.getString(2) + "	" + rs.getTimestamp(3) + "	"
							+ rs.getTimestamp(4) + "	" + rs.getString(5));
				}

			} else if (choice == 2) {
				java.sql.Timestamp startTime = null;
				java.sql.Timestamp endTime = null;

				System.out.println("Enter Start Time in format yyyy-MM-dd hh:mm:ss:");
				startTime = validateTimeStamp();
				System.out.println("Enter End Time in format yyyy-MM-dd hh:mm:ss: ");
				endTime = validateTimeStamp();

				sql = "select mft.name from medical_facility mft "
						+ "where mft.facility_id NOT IN ( select c.facility_id from check_in c, outcome_report otr, negative_experience ne "
						+ "where c.check_in_id = otr.check_in_id and otr.report_id = ne.report_id "
						+ "and otr.generation_time between ? and ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setTimestamp(1, startTime);
				ps.setTimestamp(2, endTime);
				rs = ps.executeQuery();
				if (!rs.isBeforeFirst()) {
					System.out.println("Query returns no rows!");
					continue;
				}
				while (rs.next()) {
					System.out.println(rs.getString(1));
				}

			} else if (choice == 3) {
				rs = stmt.executeQuery(
						"Select s.referedId, max(count) from (select c.facility_id as referedId, r.facility_id as referred_facility_id, "
								+ "COUNT(r.facility_id) as count from referral_status r inner join outcome_report o on o.referral_id = r.referral_id inner join "
								+ "check_in c on c.check_in_id = o.check_in_id inner join medical_facility f on f.facility_id = r.facility_id group by c.facility_id, "
								+ "r.facility_id) s, medical_facility mf where s.referedId = mf.facility_id  group by s.referedId");
				System.out.println("Referred facility          Count");
				while (rs.next()) {
					System.out.println(rs.getString(1) + "	     " + rs.getString(2));
				}

			} else if (choice == 4) {
				rs = stmt.executeQuery("SELECT f.name " + "FROM medical_facility f "
						+ "INNER JOIN check_in c ON f.facility_id = c.facility_id "
						+ "INNER JOIN patient p ON p.patient_id = c.patient_id "
						+ "INNER JOIN outcome_report o ON o.check_in_id = c.check_in_id "
						+ "INNER JOIN symptom_metadata sm ON sm.check_in_id = c.check_in_id "
						+ "INNER JOIN symptom s ON s.symptom_code = sm.symptom_code "
						+ "INNER JOIN negative_experience n ON n.report_id = o.report_id "
						+ "WHERE s.name = 'Heart' AND n.description  = NULL");

				System.out.println("Facility Name");
				while (rs.next()) {
					System.out.println(rs.getString(1));
				}

			} else if (choice == 5) {
				rs = stmt.executeQuery(
						"SELECT M.FACILITY_ID, COUNT(PC.CHECK_IN_ID) AS NEG FROM MEDICAL_FACILITY M, CHECK_IN PC,"
								+ "NEGATIVE_EXPERIENCE N, OUTCOME_REPORT O WHERE M.FACILITY_ID = PC.FACILITY_ID AND PC.CHECK_IN_ID = O.CHECK_IN_ID"
								+ " AND O.REPORT_ID = N.REPORT_ID GROUP BY M.FACILITY_ID ORDER BY NEG DESC"

				);

				System.out.println("Facility ID		Count");
				rs.next();
				String facility = rs.getString(1);
				String count = rs.getString(2);

				ResultSet rs2 = stmt.executeQuery("SELECT NAME FROM MEDICAL_FACILITY WHERE FACILITY_ID = " + facility);
				rs2.next();
				String name = rs2.getString(1);

				System.out.println(name + " 		   " + count);

			} else if (choice == 6) {
				rs = stmt.executeQuery(
						"SELECT \"patient_name\", \"date\", \"facility_name\", \"duration\", \"names\" FROM("
								+ "SELECT c.check_in_id, f.name \"facility_name\", p.first_name || ' ' || p.last_name \"patient_name\", (end_time-start_time)"
								+ "\"duration\", TO_CHAR(c.start_time, 'YYYY-MM-DD') \"date\", rank() OVER(PARTITION BY c.facility_id ORDER BY(end_time-start_time) DESC) rank, sym.\"names\" "
								+ "FROM check_in c INNER JOIN medical_facility f ON f.facility_id = c.facility_id INNER JOIN patient p ON p.patient_id = c.patient_id "
								+ "LEFT JOIN( SELECT ck.check_in_id, LISTAGG(s.name, ',') WITHIN GROUP(ORDER BY s.name) \"names\" FROM check_in ck INNER JOIN symptom_metadata m "
								+ "ON ck.check_in_id = m.check_in_id INNER JOIN symptom s ON s.symptom_code = m.symptom_code GROUP BY ck.check_in_id ORDER BY ck.check_in_id) sym "
								+ "ON sym.check_in_id = c.check_in_id ) WHERE rank<5");

				System.out.println(
						"Patient Name			    Date			Facility Name		 		Duration 					Names");

				while (rs.next()) {
					System.out.println(rs.getString(1) + "			" + rs.getString(2) + "			" + rs.getString(3)
							+ "			" + rs.getString(4) + "				" + rs.getString(5));
				}
			} else if (choice == 7) {
				break;
			}
		}

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
		long phone = readLong(10, '=');

		System.out.println("Date of birth (YYYY-MM-DD):");
		Date dateOfBirth = readDate();

		System.out.println("\nPlease enter the details of the Address as prompted");
		System.out.println("\nAddress number: ");
		long addNumber = readLong(10, '<');

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
			System.out.println("Patient has successfully been added.");
		}
	}

	private static void displaySignIn() throws Exception {
		int choice = 0;
		StringBuilder sb = null;
		checkedInPatient = null;
		checkedInStaff = null;

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
			char patient = 'n';// readString(options).charAt(0);
			boolean isPatient = (patient == 'y' || patient == 'Y');
			if (isPatient) {
				System.out.println("Last Name:");
			} else {
				System.out.println("Name:");
			}
			name = "zach";// br.readLine();

			System.out.println("Date of birth (YYYY-MM-DD):");
			Date dateOfBirth = Date.valueOf("1993-01-27");// readDate();

			System.out.println("City of address:");
			city = "raleigh";// br.readLine();

			System.out.println("\nPlease choose from the below options:");
			sb = new StringBuilder();
			sb.append("1. Sign-in\n");
			sb.append("2. Go back\n");
			System.out.println(sb.toString());

			choice = readNumber(1, 2);
			if (choice == 1) {
				if (isPatient) {
					checkedInPatient = loadPatient(name, dateOfBirth, city);
					if (checkedInPatient != null) {
						System.out.println("\nLogged in successfully.\n");
						displayPatientRouting(facilityId);
					}
				} else {
					checkedInStaff = loadStaff(name, dateOfBirth, city, facilityId);
					if (checkedInStaff != null) {
						System.out.println("\nLogged in successfully.\n");
						if (checkedInStaff.isMedical()) {
							displayStaffMenu();
						} else {
							System.out.println("You do not have privileges to do further actions.");
						}
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
		String sql = "SELECT * FROM check_in C INNER JOIN patient P "
				+ "ON P.patient_id = C.patient_id LEFT JOIN treatment t ON t.check_in_id = c.check_in_id LEFT JOIN vital_signs v ON v.check_in_id = c.check_in_id WHERE t.check_in_id IS NULL AND c.facility_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, checkedInStaff.getPrimaryDepartment(conn).getFacilityId());
		ResultSet rs = ps.executeQuery();

		HashMap<Integer, CheckIn> checkedInPatientList = new HashMap<>();
		while (rs.next()) {
			CheckIn checkIn = new CheckIn();
			checkIn.load(rs, true);
			checkedInPatientList.put(counter, checkIn);
			if (counter == 0) {
				System.out.println("List of checked-in patients:\n");
			}
			System.out.println(++counter + ". " + checkIn.getPatient().getFullName());
		}
		if (counter > 0) {
			System.out.println("Choose patient from the list");
			choice = readNumber(1, checkedInPatientList.size());
			selectedCheckIn = checkedInPatientList.get(choice - 1);

			while (true) {
				sb = new StringBuilder();
				sb.append("1. Enter Vitals\n");
				sb.append("2. Treat Patient\n");
				sb.append("3. Go back\n");

				System.out.println(sb.toString());
				choice = readNumber(1, 3);
				if (choice == 1) {
					if (selectedCheckIn.getEndTime() == null) {
						staffEnterVitals(selectedCheckIn);
					} else {
						System.out.println("Vitals already entered for patient.");
					}
				} else if (choice == 2) {
					boolean treatable = false;
					ArrayList<String> treatableBodyParts = checkedInStaff.getTreatableBodyParts(conn, bodyParts);
					ArrayList<SymptomMetadata> metadata = selectedCheckIn.getSymptomMetadata(conn);
					for (SymptomMetadata metadatum : metadata) {
						if (metadatum.getBodyPartCode() == null
								|| treatableBodyParts.contains(metadatum.getBodyPartCode())) {
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
			System.out.println("\nList has no Patients to show");
		}
	}

	private static void treatPatient(CheckIn checkIn) throws SQLException {
		Treatment treatment = new Treatment();
		treatment.setCheckInId(checkIn.getCheckInId());
		treatment.setMedicalStaffId(checkedInStaff.getStaffId());
		treatment.setTreatmentTime(new Timestamp(System.currentTimeMillis()));
		treatment.insert(conn);
		System.out.println("Patient treated.");
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
		return getPriorityName(priority);
	}

	private static String getPriorityName(char priority) {
		String name = "";
		switch (priority) {
		case 'H':
			name = "High";
			break;
		case 'Q':
			name = "Quarantine";
			break;
		case 'N':
			name = "Normal";
			break;
		}
		return name;
	}

	private static boolean assessRule(ArrayList<SymptomMetadata> metadata, Rule rule) {
		int count = 0;
		for (RuleSymptom ruleSymptom : rule.getRuleSymptoms()) {
			for (SymptomMetadata metadatum : metadata) {
				if (metadatum.getSymptomCode() == null) {
					continue;
				} else if (!metadatum.getSymptomCode().equals(ruleSymptom.getSymptom().getSymptomCode())) {
					continue;
				} else if (!metadatum.getBodyPartCode().equals(ruleSymptom.getBodyPart().getBodyPartCode())) {
					continue;
				} else {
					count++;
					SeverityScaleValue scaleValue = severityScaleValues.get(metadatum.getSeverityScaleValueId());
					char symbol = ruleSymptom.getComparisonSymbol();
					boolean pass = false;
					switch (symbol) {
					case '<':
						if (scaleValue.getSort() < ruleSymptom.getScaleValue().getSort()) {
							pass = true;
						}
						break;
					case '>':
						if (scaleValue.getSort() > ruleSymptom.getScaleValue().getSort()) {
							pass = true;
						}
						break;
					case '=':
						if (scaleValue.getSort() == ruleSymptom.getScaleValue().getSort()) {
							pass = true;
						}
						break;
					}
					if (!pass) {
						return false;
					}
				}
			}
		}
		return (count == rule.getRuleSymptoms().size());
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
			CheckIn checkinUnderProcess = loadCheckinUnderProcess(checkedInPatient.getPatientId(), facilityId);
			System.out.println("\nPlease choose one of the following options:\n");
			StringBuilder sb = new StringBuilder();
			sb.append("1. Check-in\n");
			sb.append("2. Check-out acknowledgement\n");
			sb.append("3. Go back\n");
			System.out.println(sb.toString());

			int choice = readNumber(1, 3);
			if (choice == 1) {
				if (checkinUnderProcess != null && checkinUnderProcess.getStartTime() != null) {
					System.out.println("Cannot checkin without checking out first.");
				} else {
					checkinUnderProcess = new CheckIn();
					checkinUnderProcess.setFacilityId(facilityId);
					checkinUnderProcess.setPatientId(checkedInPatient.getPatientId());
					checkinUnderProcess.save(conn);
					displayPatientCheckIn(checkinUnderProcess);
				}
			} else if (choice == 2) {
				if (checkinUnderProcess == null || checkinUnderProcess.getTreatment() == null
						|| checkinUnderProcess.getTreatment().getTreatmentTime() == null) {
					System.out.println("Cannot checkout without being treated.");
				} else {
					displayPatientAcknowledgement(checkinUnderProcess);
				}
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
			System.out.println("Successfully entered symptom " + metadataList.size() + "\n");
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
				System.out.println(symptom.getBodyPart().getBodyPartCode() + " - " + bodyPart.getName());
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

		if (symptom != null) {
			System.out.println("Please choose one of the following severity values");
			ArrayList<SeverityScaleValue> severityValueList = new ArrayList<SeverityScaleValue>();
			SeverityScale severityScale = severityScales.get(symptom.getSeverityScale().getSeverityScaleId());
			severityValueList.addAll(severityScale.getSeverityScaleValues());
			int idx = 1;
			for (SeverityScaleValue b : severityValueList) {
				System.out.println(idx++ + " - " + b.getScaleValue());
			}
			int choice = readNumber(1, severityValueList.size());
			SeverityScaleValue severityValue = severityValueList.get(choice - 1);
//			System.out.println(severityScale.getSeverityScaleId() + " " + severityValue.getScaleValue());
			metadata.setSeverityScaleValueId(severityValue.getSeverityValueId());
		}

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
		ps.setInt(2, facilityId);
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
					System.out.println("Enter the order of the scale value " + scname + " (Numeric):");
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

	private static void addAssessmentRule() throws IOException, SQLException {

		int count = 0;
		ArrayList<RuleSymptom> ruleSymList = new ArrayList<RuleSymptom>();
		Rule rule = new Rule();
		boolean prioritySelected = true;
		int choice;

		while (prioritySelected) {
			count = 0;
			HashMap<Integer, Symptom> symptomList = new HashMap<Integer, Symptom>();
			RuleSymptom ruleSym = new RuleSymptom();

			for (Map.Entry<String, Symptom> symptom : symptoms.entrySet()) {
				count++;
				symptomList.put(count, symptom.getValue());
			}
			System.out.println("Choose symptom");
			for (Map.Entry<Integer, Symptom> symptom : symptomList.entrySet()) {
				System.out.println(symptom.getKey() + " : " + symptom.getValue().getName());
			}
			System.out.println(count + 1 + " : Select Priority");
			choice = Integer.parseInt(br.readLine());
			if (choice == count + 1) {
				prioritySelected = selectPriority(ruleSymList, rule);
				continue;
			}

			String selectedSymptom = symptomList.get(choice).getSymptomCode();
			ruleSym.setSymptom(symptomList.get(choice));
			if (symptomList.get(choice).getBodyPart() == null) {
				count = 0;
				HashMap<Integer, BodyPart> partList = new HashMap<Integer, BodyPart>();
				for (Map.Entry<String, BodyPart> part : bodyParts.entrySet()) {
					partList.put(++count, part.getValue());
				}
//				System.out.println(symptomList.get(choice).getName());
				System.out.println("No Body is associated with the symtom. Choose body part of your choice!");
				for (Map.Entry<Integer, BodyPart> part : partList.entrySet()) {
					System.out.println(part.getKey() + " : " + part.getValue().getName());
				}
				choice = readNumber(1, count);
				ruleSym.setBodyPart(partList.get(choice));
			} else {
				ruleSym.setBodyPart(symptomList.get(choice).getBodyPart());
			}
			if (symptoms.get(selectedSymptom).getSeverityScale() == null) {
				for (Map.Entry<Integer, SeverityScaleValue> value : severityScaleValues.entrySet()) {
					if (value.getValue().getScaleValue() == "Present")
						ruleSym.setScaleValue(value.getValue());
				}
			} else {
				loadSeverityScales();
				count = 0;
				HashMap<Integer, SeverityScaleValue> valueList = new HashMap<Integer, SeverityScaleValue>();
				for (Map.Entry<Integer, SeverityScaleValue> value : severityScaleValues.entrySet()) {
					if (symptoms.get(selectedSymptom).getSeverityScale().getSeverityScaleId() == value.getValue()
							.getSeverityScaleId())
						valueList.put(++count, value.getValue());
				}

				for (Map.Entry<Integer, SeverityScaleValue> value : valueList.entrySet())
					System.out.println(value.getKey() + " : " + value.getValue().getScaleValue());

				choice = readNumber(1, count);
				ruleSym.setScaleValue(valueList.get(choice));
			}

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
			ruleSym.setComparisonSymbol(comparison);
			ruleSymList.add(ruleSym);
		}

	}

	private static boolean selectPriority(ArrayList<RuleSymptom> ruleSymlist, Rule rule) {
		try {
			if (ruleSymlist.size() == 0 || ruleSymlist.get(0).getSymptom() == null) {
				System.out.println("No Rules conditions are added. Add atleast one rule.");
				return true;
			}
			ResultSet rs;
			PreparedStatement ps = null;
			System.out.println("Enter Priority : H , N , Q");
			boolean isValid = true;
			char priority = 0;
			while (isValid) {
				priority = br.readLine().charAt(0);
				if (!Arrays.asList('H', 'N', 'Q').contains(priority)) {
					System.out.println("Enter valid priority H , N , Q");
					isValid = true;
				} else
					isValid = false;
			}

			rule.setPriority(priority);
			String[] primaryKey = { "RULE_ID" };
			String sql = "INSERT INTO rule(priority) values ( to_char(?) )";
			ps = conn.prepareStatement(sql, primaryKey);
			ps.setString(1, String.valueOf(rule.getPriority()));
			ps.executeUpdate();
			ResultSet rs1 = ps.getGeneratedKeys();
			if (rs1.next()) {
				rule.setRuleId(rs1.getInt(1));
			}

			String[] primaryKey1 = { "RULE_SYMPTOM_ID" };
			for (RuleSymptom ruleSym : ruleSymlist) {

				sql = "INSERT INTO rule_symptom(comparison_symbol,symptom_code, scale_value_id, body_part_code) values ( to_char(?) , ? , ? , ?)";
				ps = conn.prepareStatement(sql, primaryKey1);
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
			System.out.println("Rule generated Successfully");
			loadRules();

		} catch (Exception e) {
			System.out.println("Error occured: " + e);
		}
		return false;

	}

	private static void treatedPatient() throws Exception {
		StringBuilder sb = null;
		int choice = 0;
		int counter = 0;

		String sql = "SELECT UNIQUE p.patient_id, ci.check_in_id, p.first_name, p.last_name FROM treatment trm "
				+ "INNER JOIN check_in ci ON trm.check_in_id = ci.check_in_id "
				+ "INNER JOIN patient p ON ci.patient_id = p.patient_id "
				+ "INNER JOIN outcome_report r ON r.check_in_id != ci.check_in_id WHERE trm.medical_staff_id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, checkedInStaff.getStaffId());
		ResultSet rs = ps.executeQuery();
		HashMap<Integer, Integer> treatedPatientList = new HashMap<>();
		while (rs.next()) {
			counter++;
			System.out.println(counter + ": " + rs.getInt(2) + " " + rs.getString(3) + " " + rs.getString(4));
			treatedPatientList.put(counter, rs.getInt(2));
		}

		if (counter > 0) {
			System.out.println("Choose patient from the list\n");
			choice = readNumber(1, counter);
			int selectedCheckInId = treatedPatientList.get(choice);
			sb = new StringBuilder();
			sb.append("1. Checkout\n");
			sb.append("2. Go back\n");
			System.out.println(sb.toString());
			choice = readNumber(1, 2);
			if (choice == 1) {
				patientCheckout(selectedCheckInId);
			}
		} else if (counter == 0) {
			System.out.println("List has no patients to show");
		}
		rs.close();
		ps.close();
	}

	private static void patientCheckout(int checkInId) {
		StringBuilder sb = null;
		int choice = 0;
		boolean flag = true;
		OutcomeReport report = new OutcomeReport();
		report.setCheckInId(checkInId);

		try {
			while (flag) {
				System.out.println("\nChoose options:\n");
				sb = new StringBuilder();
				sb.append("1. Discharge Status\n");
				sb.append("2. Referal Status\n");
				sb.append("3. Treatment\n");
				sb.append("4. Negative Experience\n");
				sb.append("5. Go back\n");
				sb.append("6. Submit\n");
				System.out.println(sb.toString());

				choice = readNumber(1, 6);
				if (choice == 1) {
					dischargeStatus(report);
					System.out.println("Discharge Status added successfully");
				} else if (choice == 2) {
					if (report.getDischargeStatus() == 'R') {
						addReferralStatus(report);
					} else {
						System.out.println("You can refer a patient only if the discharge status is Referred.");
					}
				} else if (choice == 3) {
					System.out.println("Enter treatment description for selected patient:\n");
					report.setTreatmentDescription(readNonEmptyString());
					System.out.println("Description added successfully");
				} else if (choice == 4) {
					addNegativeExperience(report);
				} else if (choice == 5) {
					break;
				} else if (choice == 6) {
					if (report.getDischargeStatus() == ' ') {
						System.out.println("Discharge status is required.");
						continue;
					}
					if (report.getDischargeStatus() == 'R') {
						if (report.getReferralStatus(conn) == null) {
							System.out.println("Referral Status is required when discharge status is Referred.");
							continue;
						}
						if (report.getReferralStatus(conn).getFacilityId() == 0) {
							System.out.println("Facility is required in Referral Status");
							continue;
						}
						if (report.getReferralStatus(conn).getMedicalStaffId() == 0) {
							System.out.println("Referrer is required in Referral Status");
							continue;
						}
					}
					if (report.getTreatmentDescription() == null) {
						System.out.println("Treatment description is required.");
						continue;
					}
					flag = displayStaffReportConfirmation(report);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean displayStaffReportConfirmation(OutcomeReport report) throws Exception {
		displayReport(report);

		System.out.println("\nPlease choose one of the following options:\n");
		StringBuilder sb = new StringBuilder();
		sb.append("1. Confirm\n");
		sb.append("2. Go back\n");
		System.out.println(sb.toString());

		int choice = readNumber(1, 2);
		if (choice == 1) {
			if (report.getDischargeStatus() == 'R') {
				ReferralStatus referralStatus = report.getReferralStatus(conn);
				referralStatus.insert(conn);
				for (ReferralReason reason : referralStatus.getReasons(conn)) {
					reason.setReferralId(referralStatus.getReferralId());
					reason.insert(conn);
				}
				report.setReferralId(referralStatus.getReferralId());
			}
			report.save(conn);
			NegativeExperience negativeExperience = report.getNegativeExperience(conn);
			negativeExperience.setReportId(report.getReportId());
			negativeExperience.insert(conn);
			System.out.println("Report submitted successfully!");
		}
		return (choice == 2);
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
		}
	}

	private static void addReferralStatus(OutcomeReport report) throws Exception {
		int choice = 0;
		int facilityId = 0;
		ReferralStatus referralStatus = report.getReferralStatus(conn);

		if (referralStatus == null) {
			referralStatus = new ReferralStatus();
			report.setReferralStatus(referralStatus);
		}

		System.out.println("\n===| Referral Status |===\n");

		while (true) {
			System.out.println("\nPlease choose one of the following options:\n");
			StringBuilder sb = new StringBuilder();
			sb.append("1. Enter Facility\n");
			sb.append("2. Enter Referrer\n");
			sb.append("3. Add reason\n");
			sb.append("4. Go back\n");
			System.out.println(sb.toString());

			choice = readNumber(1, 4);

			if (choice == 1) {
				if (referralStatus.getFacilityId() != 0) {
					System.out.println("Facililty already entered.");
					continue;
				}
				System.out.println("\nPlease enter facility ID (Enter 0 if there is no specific facility): ");

				// display all facility names
				int idx = 1;
				ArrayList<MedicalFacility> facilityList = new ArrayList<MedicalFacility>();
				facilityList.addAll(facilities.values());
				for (MedicalFacility facility : facilityList) {
					System.out.println(idx++ + " - " + facility.getName());
				}

				// take user's input as the index number of the desired facility
				int facilityIdx = readNumber(1, facilityList.size());
				facilityId = facilityList.get(facilityIdx - 1).getFacilityId();

				// set facility id in referral status
				referralStatus.setFacilityId(facilityId);
			} else if (choice == 2) {
				if (facilityId == 0) {
					System.out.println("Facility must be entered before attempting to enter referrer.");
				} else if (referralStatus.getMedicalStaffId() != 0) {
					System.out.println("Referrer already entered.");
				} else {
					// create list of medical staff employees
					ArrayList<Staff> medicalStaff = new ArrayList<Staff>();
					System.out.println("\nPlease enter referrer ID: ");

					String sql = "SELECT * FROM staff s INNER JOIN medical_staff ms ON s.staff_id = ms.medical_staff_id INNER JOIN service_department sd ON ms.primary_department_code = sd.department_code WHERE sd.facility_id = ? ";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, facilityId);
					ResultSet rs = ps.executeQuery();

					int idx = 1;
					while (rs.next()) {
						Staff staff = new Staff();
						staff.load(rs);
						medicalStaff.add(staff);
						System.out.println(idx++ + " - " + staff.getName());
					}
					ps.close();
					rs.close();

					// let user pick a staff member
					int staffIdx = readNumber(1, medicalStaff.size());
					int referrerId = medicalStaff.get(staffIdx - 1).getStaffId();

					// set staff id in
					referralStatus.setMedicalStaffId(referrerId);
				}
			} else if (choice == 3) {
				// check if the number of associated reasons with the referral is less than or
				// equal to 4
				if (referralStatus.getReasons(conn).size() < 4) {
					addReferralReason(referralStatus);
				}
				// error message in case number of reasons is 4 already
				else {
					System.out.println("A referral cannot have more than 4 reasons associated with it.");
				}
			} else if (choice == 4) {
				break;
			}
		}
	}

	private static void addReferralReason(ReferralStatus referralStatus) throws Exception {
		StringBuilder sb = new StringBuilder();
		int choice = 0;

		ReferralReason reason = new ReferralReason();
		reason.setReferralId(referralStatus.getReferralId());

		System.out.println("\n===| Referral Reason Menu |===\n");
		System.out.println("\nPlease enter the following information:\n");

		System.out.print("Reason code: Please choose one of the following options.\n");
		System.out.println("1. Service unavailable at time of visit\n");
		System.out.println("2. Service not present at facility\n");
		System.out.println("3. Non-payment\n");
		int reasonCode = readNumber(1, 3);
		reason.setReasonCode(reasonCode);

		System.out.print("\nName of service: ");
		String nameOfService = readNonEmptyString();
		reason.setServiceName(nameOfService);

		System.out.print("\nDescription: ");
		String description = readNonEmptyString();
		reason.setDescription(description);

		System.out.print("\nPlease choose one of the following options.\n");
		sb.append("1. Record reason\n");
		sb.append("2. Go back\n");
		System.out.println(sb.toString());
		choice = readNumber(1, 2);

		if (choice == 1) {
			referralStatus.addReferralReason(reason);
		}
	}

	private static void dischargeStatus(OutcomeReport report) throws IOException {
		char status = 'N';

		System.out.println("\nPlease pick a discharge status:\n");
		StringBuilder sb = new StringBuilder();
		sb.append("1. Successful treatment\n");
		sb.append("2. Deceased\n");
		sb.append("3. Referred\n");
		sb.append("4. Go back\n");
		System.out.println(sb.toString());

		int choice = readNumber(1, 4);

		if (choice == 1) {
			status = 'S';
		} else if (choice == 2) {
			status = 'D';
		} else if (choice == 3) {
			status = 'R';
		}
		if (status != 'N') {
			report.setDischargeStatus(status);
		}
	}

}
