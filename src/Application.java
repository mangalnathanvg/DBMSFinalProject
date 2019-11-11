import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

	//static ArrayList<BodyPart> bodyPartsMasterList = new ArrayList<BodyPart>();
	//static ArrayList<SeverityScale> severityScaleMasterList = new ArrayList<SeverityScale>();
	
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
			Statement stmt = conn.createStatement();

			// demo read code
			ResultSet rs = stmt.executeQuery("SELECT * FROM cat");

			// Demo for reading to a variable
			// Rule rule = new Rule();
			// rule.setRuleId(rs.getInt("rule_id"));
			// rule.setPriority(rs.getString("priority").charAt(0));

			while (rs.next()) {
				String name = rs.getString("name");
				System.out.println(name);
			}

			// SETUP GLOBALS HASHMAPS
			loadFacilities();
			loadSeverityScales();
			loadBodyParts();
			loadSymptoms();
			loadRules();

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

	private static void loadFacilities() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT * FROM medical_facility f INNER JOIN address a ON f.address_id = a.address_id");

		while (rs.next()) {
			MedicalFacility facility = new MedicalFacility();
			facility.load(rs);
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

		while (choice != 4) {
			sb = new StringBuilder();
			sb.append("\nPlease choose from the below options:\n");
			sb.append("1. Sign-in\n");
			sb.append("2. Sign-up (patient)\n");
			sb.append("3. Demo queries\n");
			sb.append("4. Exit\n");
			sb.append("5. Add Symptoms\n");
			System.out.println(sb.toString());

			choice = Integer.parseInt(br.readLine());
			if (choice == 1) {
				displaySignIn();
			} else if (choice == 2) {
				displaySignUp();
			} else if (choice == 3) {
				displayDemoQueries();
			} else if (choice == 5) {
				addSymptoms();
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

		// TODO: Check if already signed in

		while (choice != 2) {

			String facilityId, dob, city, lname, patient;
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
		System.out.println("Logged in");

	}

	private static void displayPatientRouting() {
		System.out.println("Logged in");
	}
	
	private static void addSymptoms() throws Exception {
		//Adding boy parts in master list.
//		BodyPart b1 = new BodyPart();
//		BodyPart b2 = new BodyPart();
//		BodyPart b3 = new BodyPart();
//		
//		b1.setBodyPartCode("BR001");
//		b1.setName("Brain");
//		
//		b2.setBodyPartCode("BR002");
//		b2.setName("Heart");
//		
//		b3.setBodyPartCode("BR003");
//		b3.setName("Lungs");
//		
//		bodyPartsMasterList.add(b1);
//		bodyPartsMasterList.add(b2);
//		bodyPartsMasterList.add(b3);
//		
//		SeverityScale s1 = new SeverityScale();
//		SeverityScale s2 = new SeverityScale();
//		SeverityScale s3 = new SeverityScale();
//		
//		s1.setSeverityScaleId(1400);
//		s1.setName("Pain Scale 1 to 10");
//		
//		s2.setSeverityScaleId(14001);
//		s2.setName("Bleeding Heavy Normal or Light");
//		
//		s3.setSeverityScaleId(14002);
//		s3.setName("Wellness 1 to 5");
//		
//		severityScaleMasterList.add(s1);
//		severityScaleMasterList.add(s2);
//		severityScaleMasterList.add(s3);
		
		int choice, severityID;
		String symptomName, bodyPartAssocCode, temp;
		System.out.println("Please enter the details as prompted");
		System.out.println("\n Symptom Name: ");
		symptomName = br.readLine();
		System.out.println("\n Body Part associated (Select option and press enter or press enter to leave blank): ");
		int i=1;
		ArrayList<String> bpoptions = new ArrayList<String>();
		for(String bpcode: bodyParts.keySet()) {
			bpoptions.add(bpcode);
			String value = bodyParts.get(bpcode).getName().toString();
			System.out.println(Integer.toString(i) + ") " + bpcode + " - " + value);
			i++;
		}
		
		temp = br.readLine();
		
		if(temp!="") {
			choice = Integer.parseInt(temp);
			bodyPartAssocCode = bodyParts.get(bpoptions.get(choice-1)).getName().toString();
		}else {
			bodyPartAssocCode = "No Specific Body Part";
		}
		
		System.out.println("Choose the severity scale below (Press Enter if you want to leave it blank)");
		
		i=1;
		ArrayList<Integer> svcoptions = new ArrayList<Integer>();
		for(int sv: severityScales.keySet()) {
			svcoptions.add(sv);
			String value = severityScales.get(sv).getName().toString();
			System.out.println(Integer.toString(i)+ ") " + sv + " - " + value);
			i++;
		}
		temp = br.readLine();
		
		if(temp!="") {
			choice = Integer.parseInt(temp);
			severityID = svcoptions.get(choice-1);
		}else {
			severityID = 0;
		}
		
		// SymptomName , BodyPartAssocCode and SeverityID is associated successfully
		System.out.println("1) Record");
		System.out.println("2) Go Back");
		System.out.print("\nChoice: ");
		choice = Integer.parseInt(br.readLine());
		if(choice==1) {
			
			PreparedStatement ps = null;
			String sql;
			sql = "INSERT INTO mvg_symptomtable values ( ? , ? , ? )";
			ps = conn.prepareStatement(sql);
			ps.setString(1, symptomName);
			ps.setString(2, Integer.toString(severityID));
			ps.setString(3, bodyPartAssocCode);
			ResultSet rs = ps.executeQuery();
			if(rs!=null) {
				System.out.println("Recorded Symptoms Successfully");
			}else {
				System.out.println("Unable to record symptoms");
			}
			
		}	
	}
	private static void addSeverityScale() {
		
	}
}
