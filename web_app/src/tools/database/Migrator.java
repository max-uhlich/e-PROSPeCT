package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import tools.admin.AdminConnector;

public class Migrator {
	private enum ConnectionProperty {
		SURL, DURL, USERNAME, PASSWORD, KS, KS_PASS, TS, TS_PASS
	}

	private Connection connDest = null;
	private Connection connSrc = null;
	private FileWriter logFile = null;
	private String newLine = null;

	public Migrator() {
		//TODO: WHAT?
	}
	
	public static void main(String[] args) {
		Migrator mig = new Migrator();
		try {
			mig.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Connected to database.");
		mig.migrate();
		mig.disconnect();
	}

/* SECURE DATABASE CONNECTION METHODS */

	public void doConnect(String[] args) throws BTAWDatabaseException {
		Console cons = System.console();
		String password = null;
		String sourceURL = null;
		String sourceUser = null;
		
		/*** PARSE ARGUMENTS ***/
		if(args.length == 12) {
			String[] result = new String[8];
			System.arraycopy(args, 0, result, 0, 8);
			connDest = AdminConnector.doConnect(result);
			for(int i = 8; (i + 1) < args.length; i++) {
				String param = args[i++];
				String value = args[i];
				if(param.equals("-s")) {
					sourceURL = value;
				} else if (param.equals("-S")) {
					sourceUser = value;
				}
			}
			password = new String(cons.readPassword("%s\n? ", "Please enter source database password."));
			
			if(sourceURL == null || password == null || sourceUser == null) {
				System.err.println("Source database URL, source user, or source password was not entered.");
			}
		} else {
			System.err.println("Incorrect number of arguments given, args given:");
			for(String arg : args) {
				System.err.print(" " + arg);
			}
			System.err.println("");
			System.exit(-1);
		}
		
		/*** FIND JDBC DRIVER FOR MYSQL ***//**
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error, jdbc driver not found: " + e.getMessage());
			System.exit(-1);
		}**/
		
		/*** DO THE DEED ***/
		try {
			connSrc = DriverManager.getConnection(sourceURL + "?zeroDateTimeBehavior=convertToNull", sourceUser, password);
			connSrc.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error, could not get connection: " + e.getMessage());
			System.exit(-1);
		}
	}

	public boolean isConnected() {
		try {
			return ((connDest != null) && !connDest.isClosed() &&
					(connSrc != null) && !connSrc.isClosed());
		} catch (Exception ex) {
			return false;
		}
	}

	public void disconnect() {
		try {
			connDest.commit();
			connDest.close();
			connSrc.commit();
			connSrc.close();
		} catch (Exception ex) {
			System.out.println("Error closing connection.");
			ex.printStackTrace();
		}
		connDest = null;
		connSrc = null;
	}
	
	/*** INSERT FIELDS ***/
	PreparedStatement ins_person = null;
	PreparedStatement ins_formdata_cns_systemic_therapy = null;
	PreparedStatement ins_formdata_cns_scans = null;
	PreparedStatement ins_formdata_cns_progression = null;
	PreparedStatement ins_formdata_cns_presentation = null;
	PreparedStatement ins_formdata_cns_medications = null;
	PreparedStatement ins_formdata_cns_initial_treatment = null;
	PreparedStatement ins_formdata_cns_clinical_trials = null;
	PreparedStatement ins_formdata_cns_diagnosis_bx = null;
	PreparedStatement ins_formdata_cns_history = null;
	PreparedStatement ins_formdata_cns_identification = null;
	PreparedStatement ins_btap_study = null;
	PreparedStatement ins_btap_rois = null;
	PreparedStatement ins_btap_bounding_box = null;
	PreparedStatement ins_btap_imagetype = null;
	PreparedStatement ins_btap_image = null;
	PreparedStatement ins_btap_roi_voxelset = null;
	PreparedStatement ins_btap_roi_voxelset_unreg = null;
	PreparedStatement ins_btap_roi_aggregate_info = null;
	
	/*** SELECT FIELDS ***/
	PreparedStatement sel_person = null;
	PreparedStatement sel_formdata_cns_systemic_therapy = null;
	PreparedStatement sel_formdata_cns_scans = null;
	PreparedStatement sel_formdata_cns_progression = null;
	PreparedStatement sel_formdata_cns_presentation = null;
	PreparedStatement sel_formdata_cns_medications = null;
	PreparedStatement sel_formdata_cns_initial_treatment = null;
	PreparedStatement sel_formdata_cns_clinical_trials = null;
	PreparedStatement sel_formdata_cns_diagnosis_bx = null;
	PreparedStatement sel_formdata_cns_history = null;
	PreparedStatement sel_formdata_cns_identification = null;
	PreparedStatement sel_btap_study = null;
	PreparedStatement sel_btap_rois = null;
	PreparedStatement sel_btap_bounding_box = null;
	PreparedStatement sel_btap_imagetype = null;
	PreparedStatement sel_btap_image = null;
	PreparedStatement sel_btap_roi_voxelset = null;
	PreparedStatement sel_btap_roi_voxelset_unreg = null;
	PreparedStatement sel_btap_roi_aggregate_info = null;
	
	/*** DELETE FIELDS ***/
	PreparedStatement del_person = null;
	PreparedStatement del_formdata_cns_systemic_therapy = null;
	PreparedStatement del_formdata_cns_scans = null;
	PreparedStatement del_formdata_cns_progression = null;
	PreparedStatement del_formdata_cns_presentation = null;
	PreparedStatement del_formdata_cns_medications = null;
	PreparedStatement del_formdata_cns_initial_treatment = null;
	PreparedStatement del_formdata_cns_clinical_trials = null;
	PreparedStatement del_formdata_cns_diagnosis_bx = null;
	PreparedStatement del_formdata_cns_history = null;
	PreparedStatement del_formdata_cns_identification = null;
	PreparedStatement del_btap_study = null;
	PreparedStatement del_btap_rois = null;
	PreparedStatement del_btap_bounding_box = null;
	PreparedStatement del_btap_imagetype = null;
	PreparedStatement del_btap_image = null;
	PreparedStatement del_btap_roi_voxelset = null;
	PreparedStatement del_btap_roi_voxelset_unreg = null;
	PreparedStatement del_btap_roi_aggregate_info = null;
	
	/** Migration Methods **/
	public void migrate() {
		try {
			newLine = System.getProperty("line.separator");
			logFile = new FileWriter("./migration_log");
			logFile.append("----------------------------" + newLine);
			logFile.append(Calendar.getInstance().getTime().toString() + newLine);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String insertPerson = "INSERT INTO btaw.person VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFSysTherapy = "INSERT INTO btaw.formdata_cns_systemic_therapy VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFScans = "INSERT INTO btaw.formdata_cns_scans VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFProgression = "INSERT INTO btaw.formdata_cns_progression VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFPresentation = "INSERT INTO btaw.formdata_cns_presentation VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFMedication = "INSERT INTO btaw.formdata_cns_medications VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFTreatment = "INSERT INTO btaw.formdata_cns_initial_treatment VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFClinicalTrials = "INSERT INTO btaw.formdata_cns_clinical_trials VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFDiagnosisBx = "INSERT INTO btaw.formdata_cns_diagnosis_bx VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFHistory = "INSERT INTO btaw.formdata_cns_history VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFIdentification = "INSERT INTO btaw.formdata_cns_identification VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertStudy = "INSERT INTO btaw.btap_study VALUES(?, ?, ?, ?, ?, ?)";
		String insertRois = "INSERT INTO btaw.btap_rois VALUES(?, ?)";
		String insertBBox = "INSERT INTO btaw.btap_bounding_box VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertImageType = "INSERT INTO btaw.btap_imagetype VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertImage = "INSERT INTO btaw.btap_image VALUES(?, ?, ?, ?)";
		String insertRVoxelset = "INSERT INTO btaw.btap_roi_voxelset VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertRVoxelsetUnreg = "INSERT INTO btaw.btap_roi_voxelset_unreg VALUES(?, ?, ?, ?, ?, ?, ?)";
		String insertRoisAggregate = "INSERT INTO btaw.btap_roi_aggregate_info VALUES(?, ?, ?, ?, ?)";
		
		String selectPerson = "SELECT * FROM pix4h.person";
		String selectFSysTherapy = "SELECT * FROM pix4h.formdata_cns_systemic_therapy";
		String selectFScans = "SELECT * FROM pix4h.formdata_cns_scans";
		String selectFProgression = "SELECT * FROM pix4h.formdata_cns_progression";
		String selectFPresentation = "SELECT * FROM pix4h.formdata_cns_presentation";
		String selectFMedication = "SELECT * FROM pix4h.formdata_cns_medications";
		String selectFTreatment = "SELECT * FROM pix4h.formdata_cns_initial_treatment";
		String selectFClinicalTrials = "SELECT * FROM pix4h.formdata_cns_clinical_trials";
		String selectFDiagnosisBx = "SELECT * FROM pix4h.formdata_cns_diagnosis_bx";
		String selectFHistory = "SELECT * FROM pix4h.formdata_cns_history";
		String selectFIdentification = "SELECT * FROM pix4h.formdata_cns_identification";
		String selectStudy = "SELECT * FROM pix4h.btap_study";
		String selectRois = "SELECT * FROM pix4h.btap_rois";
		String selectBBox = "SELECT * FROM pix4h.btap_bounding_box";
		String selectImageType = "SELECT * FROM pix4h.btap_imagetype";
		String selectImage = "SELECT * FROM pix4h.btap_image";
		String selectRVoxelset = "SELECT * FROM pix4h.btap_roi_voxelset LIMIT ?, ?";
		String selectRVoxelsetUnreg = "SELECT * FROM pix4h.btap_roi_voxelset_unreg LIMIT ?, ?";
		String selectRoisAggregate = "SELECT * FROM pix4h.btap_roi_aggregate_info";
		
		/******************************************************************************/
		String delPerson = "TRUNCATE TABLE btaw.person CASCADE";
		String delFSysTherapy = "TRUNCATE TABLE btaw.formdata_cns_systemic_therapy CASCADE";
		String delFScans = "TRUNCATE TABLE btaw.formdata_cns_scans CASCADE";
		String delFProgression = "TRUNCATE TABLE btaw.formdata_cns_progression CASCADE";
		String delFPresentation = "TRUNCATE TABLE btaw.formdata_cns_presentation CASCADE";
		String delFMedication = "TRUNCATE TABLE btaw.formdata_cns_medications CASCADE";
		String delFTreatment = "TRUNCATE TABLE btaw.formdata_cns_initial_treatment CASCADE";
		String delFClinicalTrials = "TRUNCATE TABLE btaw.formdata_cns_clinical_trials CASCADE";
		String delFDiagnosisBx = "TRUNCATE TABLE btaw.formdata_cns_diagnosis_bx CASCADE";
		String delFHistory = "TRUNCATE TABLE btaw.formdata_cns_history CASCADE";
		String delFIdentification = "TRUNCATE TABLE btaw.formdata_cns_identification CASCADE";
		String delStudy = "TRUNCATE TABLE btaw.btap_study CASCADE";
		String delRois = "TRUNCATE TABLE btaw.btap_rois CASCADE";
		String delBBox = "TRUNCATE TABLE btaw.btap_bounding_box CASCADE";
		String delImageType = "TRUNCATE TABLE btaw.btap_imagetype CASCADE";
		String delImage = "TRUNCATE TABLE btaw.btap_image CASCADE";
		String delRVoxelset = "TRUNCATE TABLE btaw.btap_roi_voxelset CASCADE";
		String delRVoxelsetUnreg = "TRUNCATE TABLE btaw.btap_roi_voxelset_unreg CASCADE";
		String delRoisAggregate = "TRUNCATE TABLE btaw.btap_roi_aggregate_info CASCADE";
		
		try {
			del_person = connDest.prepareStatement(delPerson);
			del_formdata_cns_systemic_therapy = connDest.prepareStatement(delFSysTherapy);
			del_formdata_cns_scans = connDest.prepareStatement(delFScans);
			del_formdata_cns_progression = connDest.prepareStatement(delFProgression);
			del_formdata_cns_presentation = connDest.prepareStatement(delFPresentation);
			del_formdata_cns_medications = connDest.prepareStatement(delFMedication);
			del_formdata_cns_initial_treatment = connDest.prepareStatement(delFTreatment);
			del_formdata_cns_clinical_trials = connDest.prepareStatement(delFClinicalTrials);
			del_formdata_cns_diagnosis_bx = connDest.prepareStatement(delFDiagnosisBx);
			del_formdata_cns_history = connDest.prepareStatement(delFHistory);
			del_formdata_cns_identification = connDest.prepareStatement(delFIdentification);
			del_btap_study = connDest.prepareStatement(delStudy);
			del_btap_rois = connDest.prepareStatement(delRois);
			del_btap_bounding_box = connDest.prepareStatement(delBBox);
			del_btap_imagetype = connDest.prepareStatement(delImageType);
			del_btap_image = connDest.prepareStatement(delImage);
			del_btap_roi_voxelset = connDest.prepareStatement(delRVoxelset);
			del_btap_roi_voxelset_unreg = connDest.prepareStatement(delRVoxelsetUnreg);
			del_btap_roi_aggregate_info = connDest.prepareStatement(delRoisAggregate);
			/*
			del_person.executeUpdate();
			del_formdata_cns_systemic_therapy.executeUpdate();
			del_formdata_cns_scans.executeUpdate();
			del_formdata_cns_progression.executeUpdate();
			del_formdata_cns_presentation.executeUpdate();
			del_formdata_cns_medications.executeUpdate();
			del_formdata_cns_initial_treatment.executeUpdate();
			del_formdata_cns_clinical_trials.executeUpdate();
			del_formdata_cns_diagnosis_bx.executeUpdate();
			del_formdata_cns_history.executeUpdate();
			del_formdata_cns_identification.executeUpdate();
			del_btap_study.executeUpdate();
			del_btap_rois.executeUpdate();
			del_btap_bounding_box.executeUpdate();
			del_btap_imagetype.executeUpdate();
			del_btap_image.executeUpdate();
			del_btap_roi_voxelset.executeUpdate();
			del_btap_roi_voxelset_unreg.executeUpdate();
			*/
			del_btap_roi_aggregate_info.executeUpdate();
			connDest.commit();
		} catch (SQLException ignore) {
			try {
				connDest.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//don't care if this really executes or not, trying to clear db for insertion
		} 
		/************************************************/
		try {
			ins_person = connDest.prepareStatement(insertPerson);
			ins_formdata_cns_systemic_therapy = connDest.prepareStatement(insertFSysTherapy);
			ins_formdata_cns_scans = connDest.prepareStatement(insertFScans);
			ins_formdata_cns_progression = connDest.prepareStatement(insertFProgression);
			ins_formdata_cns_presentation = connDest.prepareStatement(insertFPresentation);
			ins_formdata_cns_medications = connDest.prepareStatement(insertFMedication);
			ins_formdata_cns_initial_treatment = connDest.prepareStatement(insertFTreatment);
			ins_formdata_cns_clinical_trials = connDest.prepareStatement(insertFClinicalTrials);
			ins_formdata_cns_diagnosis_bx = connDest.prepareStatement(insertFDiagnosisBx);
			ins_formdata_cns_history = connDest.prepareStatement(insertFHistory);
			ins_formdata_cns_identification = connDest.prepareStatement(insertFIdentification);
			ins_btap_study = connDest.prepareStatement(insertStudy);
			ins_btap_rois = connDest.prepareStatement(insertRois);
			ins_btap_bounding_box = connDest.prepareStatement(insertBBox);
			ins_btap_imagetype = connDest.prepareStatement(insertImageType);
			ins_btap_image = connDest.prepareStatement(insertImage);
			ins_btap_roi_voxelset = connDest.prepareStatement(insertRVoxelset);
			ins_btap_roi_voxelset_unreg = connDest.prepareStatement(insertRVoxelsetUnreg);
			ins_btap_roi_aggregate_info = connDest.prepareStatement(insertRoisAggregate);
			
			sel_person = connSrc.prepareStatement(selectPerson);
			sel_formdata_cns_systemic_therapy = connSrc.prepareStatement(selectFSysTherapy);
			sel_formdata_cns_scans = connSrc.prepareStatement(selectFScans);
			sel_formdata_cns_progression = connSrc.prepareStatement(selectFProgression);
			sel_formdata_cns_presentation = connSrc.prepareStatement(selectFPresentation);
			sel_formdata_cns_medications = connSrc.prepareStatement(selectFMedication);
			sel_formdata_cns_initial_treatment = connSrc.prepareStatement(selectFTreatment);
			sel_formdata_cns_clinical_trials = connSrc.prepareStatement(selectFClinicalTrials);
			sel_formdata_cns_diagnosis_bx = connSrc.prepareStatement(selectFDiagnosisBx);
			sel_formdata_cns_history = connSrc.prepareStatement(selectFHistory);
			sel_formdata_cns_identification = connSrc.prepareStatement(selectFIdentification);
			sel_btap_study = connSrc.prepareStatement(selectStudy);
			sel_btap_rois = connSrc.prepareStatement(selectRois);
			sel_btap_bounding_box = connSrc.prepareStatement(selectBBox);
			sel_btap_imagetype = connSrc.prepareStatement(selectImageType);
			sel_btap_image = connSrc.prepareStatement(selectImage);
			sel_btap_roi_voxelset = connSrc.prepareStatement(selectRVoxelset);
			sel_btap_roi_voxelset_unreg = connSrc.prepareStatement(selectRVoxelsetUnreg);
			sel_btap_roi_aggregate_info = connSrc.prepareStatement(selectRoisAggregate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		migrate_person();
		migrate_formdata_cns_clinical_trials();
		migrate_formdata_cns_diagnosis_bx();
		migrate_formdata_cns_history();
		migrate_formdata_cns_identification();
		migrate_formdata_cns_initial_treatment();
		migrate_formdata_cns_medications();
		migrate_formdata_cns_presentation();
		migrate_formdata_cns_progression();
		migrate_formdata_cns_scans();
		migrate_formdata_cns_systemic_therapy();
		migrate_btap_study();
		migrate_btap_rois();
		migrate_btap_imagetype();
		migrate_btap_image();
		migrate_btap_bounding_box();
		migrate_btap_roi_voxelset();
		migrate_btap_roi_voxelset_unreg();
		migrate_btap_roi_aggregate_info();
	}
	
	
	public void migrate_person() {
		try {
			ResultSet rset = sel_person.executeQuery();
			while(rset.next()) {
				int pid  = rset.getInt("pid");
				java.sql.Timestamp date_reg = rset.getTimestamp("date_reg");
				java.sql.Date date_birth = rset.getDate("date_birth");
				String sex = rset.getString("sex");
				String initials = rset.getString("initials");
				java.sql.Timestamp date_update = rset.getTimestamp("date_update");
				String modify_id = rset.getString("modify_id");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				String create_id = rset.getString("create_id");
				java.sql.Timestamp create_time = rset.getTimestamp("create_time");
				int export = rset.getInt("export");
				int source_cci = rset.getInt("source_cci");
				int source_cr = rset.getInt("source_cr");
				
				ins_person.setInt(1, pid);
				ins_person.setObject(2, date_reg, java.sql.Types.TIMESTAMP);
				ins_person.setObject(3, date_birth, java.sql.Types.DATE);
				ins_person.setObject(4, sex, java.sql.Types.CHAR);
				ins_person.setObject(5, initials, java.sql.Types.VARCHAR);
				ins_person.setObject(6, modify_time, java.sql.Types.TIMESTAMP);
				ins_person.setObject(7, source_cci == 1, java.sql.Types.BOOLEAN);
				ins_person.setObject(8, source_cr == 1, java.sql.Types.BOOLEAN);
				ins_person.setObject(9, date_update, java.sql.Types.TIMESTAMP);
				ins_person.setObject(10, modify_id, java.sql.Types.VARCHAR);
				ins_person.setObject(11, create_id, java.sql.Types.VARCHAR);
				ins_person.setObject(12, create_time, java.sql.Types.TIMESTAMP);
				ins_person.setInt(13, export);
				ins_person.executeUpdate();
				connDest.commit();
			}
			sel_person.close();
			ins_person.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_clinical_trials() {
		try {
			ResultSet rset = sel_formdata_cns_clinical_trials.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String study_igar = rset.getString("study_igar");
				String study_inhouse = rset.getString("study_inhouse");
				String study_industry = rset.getString("study_industry");
				String study_ncic = rset.getString("study_ncic");
				String study_rtog = rset.getString("study_rtog");
				String study_ocog = rset.getString("study_ocog");
				String study_other = rset.getString("study_other");
				String study_number = rset.getString("study_number");
				String study_name = rset.getString("study_name");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_clinical_trials.setInt(1, formdata_id);
				ins_formdata_cns_clinical_trials.setInt(2, pid);
				ins_formdata_cns_clinical_trials.setObject(3, study_number, java.sql.Types.VARCHAR);
				ins_formdata_cns_clinical_trials.setObject(4, study_name, java.sql.Types.VARCHAR);
				ins_formdata_cns_clinical_trials.setObject(5, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_clinical_trials.setObject(6, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_clinical_trials.setObject(7, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_clinical_trials.setObject(8, modify_time, java.sql.Types.TIMESTAMP);
				if(study_igar != null && study_igar.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "igar", java.sql.Types.VARCHAR);
				} else if(study_inhouse != null && study_inhouse.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "inhouse", java.sql.Types.VARCHAR);
				} else if(study_industry != null && study_industry.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "industry", java.sql.Types.VARCHAR);
				} else if(study_ncic != null && study_ncic.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "ncic", java.sql.Types.VARCHAR);
				} else if(study_rtog != null && study_rtog.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "rtog", java.sql.Types.VARCHAR);
				} else if(study_ocog != null && study_ocog.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "ocog", java.sql.Types.VARCHAR);
				} else if(study_other != null && study_other.equals("True")) {
					ins_formdata_cns_clinical_trials.setObject(9, "other", java.sql.Types.VARCHAR);
				} else {
					ins_formdata_cns_clinical_trials.setObject(9, "other", java.sql.Types.VARCHAR);
				}
				ins_formdata_cns_clinical_trials.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_clinical_trials.close();
			ins_formdata_cns_clinical_trials.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_diagnosis_bx() {
		try {
			ResultSet rset = sel_formdata_cns_diagnosis_bx.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String surgery_bx = rset.getString("surgery_bx");
				String date_bx = rset.getString("date_bx");
				String surgical_facility = rset.getString("surgical_facility");
				String extend_of_resection = rset.getString("extend_of_resection");
				String prelim_diag = rset.getString("prelim_diag");
				String prelim_diag_icd = rset.getString("prelim_diag_icd");
				String prelim_diag_grade = rset.getString("prelim_diag_grade");
				String date_prelim_diag_path_reported = rset.getString("date_prelim_diag_path_reported");
				String final_diag = rset.getString("final_diag");
				String final_diag_icd = rset.getString("final_diag_icd");
				String final_diag_grade = rset.getString("final_diag_grade");
				String date_final_diag_path_reported = rset.getString("date_final_diag_path_reported");
				String p_loss_of_heterozyg = rset.getString("p_loss_of_heterozyg");
				String q_loss_of_heterozyg = rset.getString("q_loss_of_heterozyg");
				String mgmt = rset.getString("mgmt");
				String diag_path_consult = rset.getString("diag_path_consult");
				String diag_path_consult_icd = rset.getString("diag_path_consult_icd");
				String diag_path_consult_grade = rset.getString("diag_path_consult_grade");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_diagnosis_bx.setInt(1, formdata_id);
				ins_formdata_cns_diagnosis_bx.setInt(2, pid);
				ins_formdata_cns_diagnosis_bx.setObject(3, yes_no_to_bool(surgery_bx), java.sql.Types.BOOLEAN);
				ins_formdata_cns_diagnosis_bx.setObject(4, dd_MM_yyyy_to_sqlDate(date_bx), java.sql.Types.DATE);
				ins_formdata_cns_diagnosis_bx.setObject(5, surgical_facility, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(6, extend_of_resection, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(7, prelim_diag, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setInt(8, parseInt(prelim_diag_icd));
				ins_formdata_cns_diagnosis_bx.setObject(9, prelim_diag_grade, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(10, dd_MM_yyyy_to_sqlDate(date_prelim_diag_path_reported));
				ins_formdata_cns_diagnosis_bx.setObject(11, final_diag, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setInt(12, parseInt(final_diag_icd));
				ins_formdata_cns_diagnosis_bx.setObject(13, final_diag_grade, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(14, dd_MM_yyyy_to_sqlDate(date_final_diag_path_reported), java.sql.Types.DATE);
				ins_formdata_cns_diagnosis_bx.setObject(15, yes_no_to_bool(p_loss_of_heterozyg), java.sql.Types.BOOLEAN);
				ins_formdata_cns_diagnosis_bx.setObject(16, yes_no_to_bool(q_loss_of_heterozyg), java.sql.Types.BOOLEAN);
				ins_formdata_cns_diagnosis_bx.setObject(17, yes_no_to_bool(mgmt), java.sql.Types.BOOLEAN);
				ins_formdata_cns_diagnosis_bx.setObject(18, diag_path_consult, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setInt(19, parseInt(diag_path_consult_icd));
				ins_formdata_cns_diagnosis_bx.setObject(20, diag_path_consult_grade, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(21, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(22, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_diagnosis_bx.setObject(23, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_diagnosis_bx.setObject(24, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_diagnosis_bx.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_diagnosis_bx.close();
			ins_formdata_cns_diagnosis_bx.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_history() {
		try {
			ResultSet rset = sel_formdata_cns_history.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String past_diagnoses = rset.getString("past_diagnoses");
				String allergies = rset.getString("allergies");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_history.setInt(1, formdata_id);
				ins_formdata_cns_history.setInt(2, pid);
				ins_formdata_cns_history.setObject(3, past_diagnoses, java.sql.Types.VARCHAR);
				ins_formdata_cns_history.setObject(4, allergies, java.sql.Types.VARCHAR);
				ins_formdata_cns_history.setObject(5, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_history.setObject(6, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_history.setObject(7, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_history.setObject(8, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_history.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_history.close();
			ins_formdata_cns_history.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_identification() {
		try {
			ResultSet rset = sel_formdata_cns_identification.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String date_death = rset.getString("date_death");
				String patient_height = rset.getString("patient_height");
				String patient_weight = rset.getString("patient_weight");
				String handedness = rset.getString("handedness");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_identification.setInt(1, formdata_id);
				ins_formdata_cns_identification.setInt(2, pid);
				ins_formdata_cns_identification.setObject(3, dd_MM_yyyy_to_sqlDate(date_death), java.sql.Types.DATE);
				ins_formdata_cns_identification.setObject(4, patient_height, java.sql.Types.REAL);
				ins_formdata_cns_identification.setObject(5, patient_weight, java.sql.Types.REAL);
				ins_formdata_cns_identification.setObject(6, handedness, java.sql.Types.VARCHAR);
				ins_formdata_cns_identification.setObject(7, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_identification.setObject(8, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_identification.setObject(9, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_identification.setObject(10, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_identification.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_identification.close();
			ins_formdata_cns_identification.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_initial_treatment() {
		try {
			ResultSet rset = sel_formdata_cns_initial_treatment.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String initial_treat_rt = rset.getString("initial_treat_rt");
				String initial_treat_rt_start_date = rset.getString("initial_treat_rt_start_date");
				String initial_treat_rt_end_date = rset.getString("initial_treat_rt_end_date");
				String initial_treat_fraction_nr = rset.getString("initial_treat_fraction_nr");
				String initial_treat_rt_total_dose = rset.getString("initial_treat_rt_total_dose");
				String initial_treat_rt_type = rset.getString("initial_treat_rt_type");
				String initial_treat_second_rt_start_date = rset.getString("initial_treat_second_rt_start_date");
				String initial_treat_second_rt_end_date = rset.getString("initial_treat_second_rt_end_date");
				String initial_treat_second_fraction_nr = rset.getString("initial_treat_second_fraction_nr");
				String initial_treat_second_rt_total_dose = rset.getString("initial_treat_second_rt_total_dose");
				String initial_treat_second_rt_type = rset.getString("initial_treat_second_rt_type");
				String initial_treat_chemo = rset.getString("initial_treat_chemo");
				String initial_treat_drug_tmz = rset.getString("initial_treat_drug_tmz");
				String initial_treat_drug_other = rset.getString("initial_treat_drug_other");
				String initial_treat_name_other_drug = rset.getString("initial_treat_name_other_drug");
				String initial_treat_chemo_payment = rset.getString("initial_treat_chemo_payment");
				String initial_treat_chemo_start_date = rset.getString("initial_treat_chemo_start_date");
				String initial_treat_chemo_end_date = rset.getString("initial_treat_chemo_end_date");
				String initial_treat_chemo_post_rt_cycles = rset.getString("initial_treat_chemo_post_rt_cycles");
				String initial_treat_chemo_total_dose = rset.getString("initial_treat_chemo_total_dose");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_initial_treatment.setInt(1, formdata_id);
				ins_formdata_cns_initial_treatment.setInt(2, pid);
				ins_formdata_cns_initial_treatment.setObject(3, initial_treat_rt, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(4, f_initial_treatment(formdata_id, pid, initial_treat_rt_start_date), java.sql.Types.DATE);
				ins_formdata_cns_initial_treatment.setObject(5, f_initial_treatment(formdata_id, pid, initial_treat_rt_end_date), java.sql.Types.DATE);
				ins_formdata_cns_initial_treatment.setObject(6, initial_treat_fraction_nr, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(7, initial_treat_rt_total_dose, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(8, initial_treat_rt_type, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(9, f_initial_treatment(formdata_id, pid, initial_treat_second_rt_start_date), java.sql.Types.DATE);
				ins_formdata_cns_initial_treatment.setObject(10, f_initial_treatment(formdata_id, pid, initial_treat_second_rt_end_date), java.sql.Types.DATE);
				ins_formdata_cns_initial_treatment.setObject(11, initial_treat_second_rt_total_dose, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(12, initial_treat_second_fraction_nr, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(13, initial_treat_second_rt_type, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(14, yes_no_to_bool(initial_treat_chemo), java.sql.Types.BOOLEAN);
				ins_formdata_cns_initial_treatment.setObject(15, tf_to_bool(initial_treat_drug_tmz), java.sql.Types.BOOLEAN);
				ins_formdata_cns_initial_treatment.setObject(16, tf_to_bool(initial_treat_drug_other), java.sql.Types.BOOLEAN);
				ins_formdata_cns_initial_treatment.setObject(17, initial_treat_name_other_drug, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(18, initial_treat_chemo_payment, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(19, f_initial_treatment(formdata_id, pid, initial_treat_chemo_start_date), java.sql.Types.DATE);
				ins_formdata_cns_initial_treatment.setObject(20, f_initial_treatment(formdata_id, pid, initial_treat_chemo_end_date), java.sql.Types.DATE);
				ins_formdata_cns_initial_treatment.setObject(21, initial_treat_chemo_total_dose, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(22, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(23, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_initial_treatment.setObject(24, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_initial_treatment.setObject(25, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_initial_treatment.setInt(26, parseInt(initial_treat_chemo_post_rt_cycles));
				ins_formdata_cns_initial_treatment.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_initial_treatment.close();
			ins_formdata_cns_initial_treatment.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_medications() {
		try {
			ResultSet rset = sel_formdata_cns_medications.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String decadron = rset.getString("decadron");
				String decadron_current_dose = rset.getString("decadron_current_dose");
				String dilantin = rset.getString("dilantin");
				String dilantin_current_dose = rset.getString("dilantin_current_dose");
				String other_medication = rset.getString("other_medication");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_medications.setInt(1, formdata_id);
				ins_formdata_cns_medications.setInt(2, pid);
				ins_formdata_cns_medications.setObject(3, yes_no_to_bool(decadron), java.sql.Types.BOOLEAN);
				ins_formdata_cns_medications.setObject(4, decadron_current_dose, java.sql.Types.VARCHAR);
				ins_formdata_cns_medications.setObject(5, yes_no_to_bool(dilantin), java.sql.Types.BOOLEAN);
				ins_formdata_cns_medications.setObject(6, dilantin_current_dose, java.sql.Types.VARCHAR);
				ins_formdata_cns_medications.setObject(7, other_medication, java.sql.Types.VARCHAR);
				ins_formdata_cns_medications.setObject(8, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_medications.setObject(9, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_medications.setObject(10, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_medications.setObject(11, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_medications.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_medications.close();
			ins_formdata_cns_medications.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_presentation() {
		try {
			ResultSet rset = sel_formdata_cns_presentation.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String presenting_symptom = rset.getString("presenting_symptom");
				String presenting_symptom_date = rset.getString("presenting_symptom_date");
				String ecog = rset.getString("ecog");
				String kps = rset.getString("kps");
				String seizures = rset.getString("seizures");
				String headaches = rset.getString("headaches");
				String memory_loss = rset.getString("memory_loss");
				String nausea_vomiting = rset.getString("nausea_vomiting");
				String speech_problems = rset.getString("speech_problems");
				String difficulty_walking = rset.getString("difficulty_walking");
				String tinnitus = rset.getString("tinnitus");
				String visual_changes = rset.getString("visual_changes");
				String hearing_changes = rset.getString("hearing_changes");
				String focal_weakness = rset.getString("focal_weakness");
				String confusion = rset.getString("confusion");
				String personality_changes = rset.getString("personality_changes");
				String dizziness = rset.getString("dizziness");
				String other_symptoms = rset.getString("other_symptoms");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_presentation.setInt(1, formdata_id);
				ins_formdata_cns_presentation.setInt(2, pid);
				ins_formdata_cns_presentation.setFloat(3, parseFloat(ecog));
				ins_formdata_cns_presentation.setInt(4, parseInt(kps));
				ins_formdata_cns_presentation.setObject(5, tf_to_bool(seizures), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(6, tf_to_bool(headaches), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(7, tf_to_bool(memory_loss), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(8, tf_to_bool(nausea_vomiting), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(9, tf_to_bool(speech_problems), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(10, tf_to_bool(difficulty_walking), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(11, tf_to_bool(tinnitus), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(12, tf_to_bool(visual_changes), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(13, tf_to_bool(hearing_changes), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(14, tf_to_bool(focal_weakness), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(15, tf_to_bool(confusion), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(16, tf_to_bool(personality_changes), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(17, tf_to_bool(dizziness), java.sql.Types.BOOLEAN);
				ins_formdata_cns_presentation.setObject(18, other_symptoms, java.sql.Types.VARCHAR);
				ins_formdata_cns_presentation.setObject(19, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_presentation.setObject(20, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_presentation.setObject(21, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_presentation.setObject(22, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_presentation.setObject(23, presenting_symptom, java.sql.Types.VARCHAR);
				ins_formdata_cns_presentation.setObject(24, dd_MM_yyyy_to_sqlDate(presenting_symptom_date), java.sql.Types.DATE);
				ins_formdata_cns_presentation.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_presentation.close();
			ins_formdata_cns_presentation.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_progression() {
		try {
			ResultSet rset = sel_formdata_cns_progression.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String prog_type = rset.getString("prog_type");
				String prog_date_scan = rset.getString("prog_date_scan");
				String prog_scanning_facility = rset.getString("prog_scanning_facility");
				String prog_trans_confirmed = rset.getString("prog_trans_confirmed");
				String prog_surgery = rset.getString("prog_surgery");
				String prog_date_surgery = rset.getString("prog_date_surgery");
				String prog_surgical_facility = rset.getString("prog_surgical_facility");
				String prog_extend_of_resection = rset.getString("prog_extend_of_resection");
				String prog_prelim_diag = rset.getString("prog_prelim_diag");
				String prog_prelim_diag_icd = rset.getString("prog_prelim_diag_icd");
				String prog_prelim_diag_grade = rset.getString("prog_prelim_diag_grade");
				String prog_date_prelim_diag_path_reported = rset.getString("prog_date_prelim_diag_path_reported");
				String prog_final_diag = rset.getString("prog_final_diag");
				String prog_final_diag_icd = rset.getString("prog_final_diag_icd");
				String prog_final_diag_grade = rset.getString("prog_final_diag_grade");
				String prog_date_final_diag_path_reported = rset.getString("prog_date_final_diag_path_reported");
				String prog_p_loss_of_heterozyg = rset.getString("prog_p_loss_of_heterozyg");
				String prog_q_loss_of_heterozyg = rset.getString("prog_q_loss_of_heterozyg");
				String prog_mgmt = rset.getString("prog_mgmt");
				String prog_diag_path_consult = rset.getString("prog_diag_path_consult");
				String prog_diag_path_consult_icd = rset.getString("prog_diag_path_consult_icd");
				String prog_diag_path_consult_grade = rset.getString("prog_diag_path_consult_grade");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_progression.setInt(1, formdata_id);
				ins_formdata_cns_progression.setInt(2, pid);
				ins_formdata_cns_progression.setObject(3, prog_type, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(4, dd_MM_yyyy_to_sqlDate(prog_date_scan), java.sql.Types.DATE);
				ins_formdata_cns_progression.setObject(5, prog_scanning_facility, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(6, yes_no_to_bool(prog_trans_confirmed), java.sql.Types.BOOLEAN);
				ins_formdata_cns_progression.setObject(7, yes_no_to_bool(prog_surgery), java.sql.Types.BOOLEAN);
				ins_formdata_cns_progression.setObject(8, dd_MM_yyyy_to_sqlDate(prog_date_surgery), java.sql.Types.DATE);
				ins_formdata_cns_progression.setObject(9, prog_surgical_facility, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(10, prog_extend_of_resection, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(11, prog_final_diag_icd, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(12, prog_final_diag, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(13, prog_final_diag_grade, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(14, dd_MM_yyyy_to_sqlDate(prog_date_final_diag_path_reported), java.sql.Types.DATE);
				ins_formdata_cns_progression.setObject(15, yes_no_to_bool(prog_p_loss_of_heterozyg), java.sql.Types.BOOLEAN);
				ins_formdata_cns_progression.setObject(16, yes_no_to_bool(prog_q_loss_of_heterozyg), java.sql.Types.BOOLEAN);
				ins_formdata_cns_progression.setObject(17, yes_no_to_bool(prog_mgmt), java.sql.Types.BOOLEAN);
				ins_formdata_cns_progression.setObject(18, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(19, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_progression.setObject(20, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(21, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_progression.setObject(22, prog_prelim_diag, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(23, prog_prelim_diag_icd, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(24, prog_prelim_diag_grade, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(25, prog_date_prelim_diag_path_reported, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(26, prog_diag_path_consult, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(27, prog_diag_path_consult_icd, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.setObject(28, prog_diag_path_consult_grade, java.sql.Types.VARCHAR);
				ins_formdata_cns_progression.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_progression.close();
			ins_formdata_cns_progression.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_scans() {
		try {
			ResultSet rset = sel_formdata_cns_scans.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String initial_scan = rset.getString("initial_scan");
				String date_initial_scan = rset.getString("date_initial_scan");
				String scanning_facility = rset.getString("scanning_facility");
				String x_size_tumour_on_scan = rset.getString("x_size_tumour_on_scan");
				String y_size_tumour_on_scan = rset.getString("y_size_tumour_on_scan");
				String z_size_tumour_on_scan = rset.getString("z_size_tumour_on_scan");
				String loc_left_frontal = rset.getString("loc_left_frontal");
				String loc_right_frontal = rset.getString("loc_right_frontal");
				String loc_left_temporal = rset.getString("loc_left_temporal");
				String loc_right_temporal = rset.getString("loc_right_temporal");
				String loc_left_parietal = rset.getString("loc_left_parietal");
				String loc_right_parietal = rset.getString("loc_right_parietal");
				String loc_left_occipital = rset.getString("loc_left_occipital");
				String loc_right_occipital = rset.getString("loc_right_occipital");
				String loc_left_cerebellum = rset.getString("loc_left_cerebellum");
				String loc_right_cerebellum = rset.getString("loc_right_cerebellum");
				String loc_brainstem = rset.getString("loc_brainstem");
				String loc_spine = rset.getString("loc_spine");
				String loc_comments = rset.getString("loc_comments");
				String enhancement = rset.getString("enhancement");
				String date_presentation_neurosurgery = rset.getString("date_presentation_neurosurgery");
				String date_initial_consultation = rset.getString("date_initial_consultation");
				String multifocal = rset.getString("multifocal");
				String multifocal_comments = rset.getString("multifocal_comments");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp form_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				String location = rset.getString("location");
				
				ins_formdata_cns_scans.setInt(1, formdata_id);
				ins_formdata_cns_scans.setInt(2, pid);
				ins_formdata_cns_scans.setObject(3, initial_scan, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(4, dd_MM_yyyy_to_sqlDate(date_initial_scan), java.sql.Types.DATE);
				ins_formdata_cns_scans.setObject(5, scanning_facility, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setFloat(6, parseFloat(x_size_tumour_on_scan));
				ins_formdata_cns_scans.setFloat(7, parseFloat(y_size_tumour_on_scan));
				ins_formdata_cns_scans.setFloat(8, parseFloat(z_size_tumour_on_scan));
				ins_formdata_cns_scans.setObject(9, tf_to_bool(loc_left_frontal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(10, tf_to_bool(loc_right_frontal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(11, tf_to_bool(loc_left_temporal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(12, tf_to_bool(loc_right_temporal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(13, tf_to_bool(loc_left_parietal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(14, tf_to_bool(loc_right_parietal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(15, tf_to_bool(loc_left_occipital), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(16, tf_to_bool(loc_right_occipital), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(17, tf_to_bool(loc_left_cerebellum), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(18, tf_to_bool(loc_right_cerebellum), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(19, tf_to_bool(loc_brainstem), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(20, tf_to_bool(loc_spine), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(21, loc_comments, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(22, enhancement, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(23, date_presentation_neurosurgery, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(24, dd_MM_yyyy_to_sqlDate(date_initial_consultation), java.sql.Types.DATE);
				ins_formdata_cns_scans.setObject(25, tf_to_bool(multifocal), java.sql.Types.BOOLEAN);
				ins_formdata_cns_scans.setObject(26, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(27, form_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_scans.setObject(28, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(29, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_scans.setObject(30, multifocal_comments, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.setObject(31, location, java.sql.Types.VARCHAR);
				ins_formdata_cns_scans.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_scans.close();
			ins_formdata_cns_scans.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_formdata_cns_systemic_therapy() {
		try {
			ResultSet rset = sel_formdata_cns_systemic_therapy.executeQuery();
			while(rset.next()) {
				int formdata_id = rset.getInt("formdata_id");
				int pid = rset.getInt("pid");
				String sys_therapy_regimen = rset.getString("sys_therapy_regimen");
				String sys_therapy_payment = rset.getString("sys_therapy_payment");
				String sys_therapy_start_date = rset.getString("sys_therapy_start_date");
				String sys_therapy_end_date = rset.getString("sys_therapy_end_date");
				String sys_therapy_nr_cycles = rset.getString("sys_therapy_nr_cycles");
				String sys_therapy_total_dose = rset.getString("sys_therapy_total_dose");
				String user_create = rset.getString("user_create");
				java.sql.Timestamp formdata_create_time = rset.getTimestamp("formdata_create_time");
				String modify_user = rset.getString("modify_user");
				java.sql.Timestamp modify_time = rset.getTimestamp("modify_time");
				
				ins_formdata_cns_systemic_therapy.setInt(1, formdata_id);
				ins_formdata_cns_systemic_therapy.setInt(2, pid);
				ins_formdata_cns_systemic_therapy.setObject(3, sys_therapy_regimen, java.sql.Types.VARCHAR);
				ins_formdata_cns_systemic_therapy.setObject(4, sys_therapy_payment, java.sql.Types.VARCHAR);
				ins_formdata_cns_systemic_therapy.setObject(5, dd_MM_yyyy_to_sqlDate(sys_therapy_start_date), java.sql.Types.DATE);
				ins_formdata_cns_systemic_therapy.setObject(6, dd_MM_yyyy_to_sqlDate(sys_therapy_end_date), java.sql.Types.DATE);
				ins_formdata_cns_systemic_therapy.setObject(7, sys_therapy_nr_cycles, java.sql.Types.VARCHAR);
				ins_formdata_cns_systemic_therapy.setObject(8, sys_therapy_total_dose, java.sql.Types.VARCHAR);
				ins_formdata_cns_systemic_therapy.setObject(9, user_create, java.sql.Types.VARCHAR);
				ins_formdata_cns_systemic_therapy.setObject(10, formdata_create_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_systemic_therapy.setObject(11, modify_user, java.sql.Types.VARCHAR);
				ins_formdata_cns_systemic_therapy.setObject(12, modify_time, java.sql.Types.TIMESTAMP);
				ins_formdata_cns_systemic_therapy.executeUpdate();
				connDest.commit();
			}
			sel_formdata_cns_systemic_therapy.close();
			ins_formdata_cns_systemic_therapy.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_bounding_box() {
		try {
			ResultSet rset = sel_btap_bounding_box.executeQuery();
			while(rset.next()) {
				int bb_id = rset.getInt("bb_id");
				int study_id = rset.getInt("study_id");
				int roi_id = rset.getInt("roi_id");
				String bound_box_z = rset.getString("bound_box_z");
				
				/*
				if(!bound_box_z.matches("x1=[0-9]+")) {
					//TOOD: WHAT?
				}*/
				String[] bound_box_split = bound_box_z.split(";");
				
				ins_btap_bounding_box.setInt(1, bb_id);
				ins_btap_bounding_box.setInt(2, study_id);
				ins_btap_bounding_box.setInt(3, Integer.parseInt((bound_box_split[4].split("="))[1]));
				ins_btap_bounding_box.setInt(4, roi_id);
				ins_btap_bounding_box.setInt(5, Integer.parseInt((bound_box_split[0].split("="))[1]));
				ins_btap_bounding_box.setInt(6, Integer.parseInt((bound_box_split[2].split("="))[1]));
				ins_btap_bounding_box.setInt(7, Integer.parseInt((bound_box_split[1].split("="))[1]));
				ins_btap_bounding_box.setInt(8, Integer.parseInt((bound_box_split[3].split("="))[1]));
				ins_btap_bounding_box.executeUpdate();
				connDest.commit();
			}
			sel_btap_bounding_box.close();
			ins_btap_bounding_box.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_image() {
		try {
			ResultSet rset = sel_btap_image.executeQuery();
			while(rset.next()) {
				int image_id = rset.getInt("image_id");
				int imagetype_id = rset.getInt("imagetype_id");
				String slicenr = rset.getString("slicenr");
				String link = rset.getString("link");
				
				File file = new File(link);
				FileInputStream fis = null;
				
				ins_btap_image.setInt(1, image_id);
				ins_btap_image.setInt(2, imagetype_id);
				ins_btap_image.setInt(3, parseInt(slicenr));
				try {
					fis = new FileInputStream(file);
					ins_btap_image.setBinaryStream(4, fis, (int)file.length());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						logFile.append("Error adding image_id=" + image_id + " ERR:" + e.getMessage() + newLine);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.exit(-1);
					}
				} finally {
					ins_btap_image.executeUpdate();
					if(fis != null) try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}	
					file = null;
				}
				connDest.commit();
			}
			ins_btap_image.close();
			sel_btap_image.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_imagetype() {
		try {
			ResultSet rset = sel_btap_imagetype.executeQuery();
			while(rset.next()) {
				int imagetype_id = rset.getInt("imagetype_id");
				int study_id = rset.getInt("study_id");
				String type_description = rset.getString("type_description");
				String comment = rset.getString("comment");
				float echo_time = rset.getFloat("echo_time");
				float inversion_time = rset.getFloat("inversion_time");
				String scon_sequence = rset.getString("scon_sequence");
				float repetition_time = rset.getFloat("repetition_time");
				
				ins_btap_imagetype.setInt(1, imagetype_id);
				ins_btap_imagetype.setInt(2, study_id);
				ins_btap_imagetype.setObject(3, type_description, java.sql.Types.VARCHAR);
				ins_btap_imagetype.setObject(4, comment, java.sql.Types.VARCHAR);
				ins_btap_imagetype.setFloat(5, echo_time);
				ins_btap_imagetype.setFloat(6, inversion_time);
				ins_btap_imagetype.setFloat(7, repetition_time);
				ins_btap_imagetype.setObject(8, scon_sequence, java.sql.Types.VARCHAR);
				ins_btap_imagetype.executeUpdate();
				connDest.commit();
			}
			sel_btap_imagetype.close();
			ins_btap_imagetype.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_roi_aggregate_info() {
		try {
			ResultSet rset = sel_btap_roi_aggregate_info.executeQuery();
			while(rset.next()) {
				int roi_agg_id = rset.getInt("roi_agg_id");
				int study_id = rset.getInt("study_id");
				int roi_id = rset.getInt("roi_id");
				int author_id = rset.getInt("author_id");
				String description = rset.getString("description");
				String value = rset.getString("value");
				
				ins_btap_roi_aggregate_info.setInt(1, roi_agg_id);
				ins_btap_roi_aggregate_info.setInt(2, study_id);
				ins_btap_roi_aggregate_info.setInt(3, roi_id);
				ins_btap_roi_aggregate_info.setObject(4, description, java.sql.Types.VARCHAR);
				ins_btap_roi_aggregate_info.setObject(5, value, java.sql.Types.VARCHAR);
				ins_btap_roi_aggregate_info.executeUpdate();
				connDest.commit();
			}
			ins_btap_roi_aggregate_info.close();
			sel_btap_roi_aggregate_info.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_roi_voxelset() {
		try {
			PreparedStatement sel_rcnt = connSrc.prepareStatement("SELECT count(*) FROM pix4h.btap_roi_voxelset");
			ResultSet rset_rcnt = sel_rcnt.executeQuery();
			rset_rcnt.next();
			int row_cnt = rset_rcnt.getInt(1);
			for(int i = 0; i < row_cnt; i = i + 5000) {
				sel_btap_roi_voxelset.setInt(1, i);
				sel_btap_roi_voxelset.setInt(2, 5000);
				ResultSet rset = sel_btap_roi_voxelset.executeQuery();
				while(rset.next()) {
					int roi_voxelset_id = rset.getInt("roi_voxelset_id");
					int study_id = rset.getInt("study_id");
					int roi_id = rset.getInt("roi_id");
					int author_id = rset.getInt("author_id");
					int x = rset.getInt("x");
					int y = rset.getInt("y");
					int z = rset.getInt("z");
					int bounder = rset.getInt("bounder");
	
					ins_btap_roi_voxelset.setInt(1, roi_voxelset_id);
					ins_btap_roi_voxelset.setInt(2, study_id);
					ins_btap_roi_voxelset.setInt(3, roi_id);
					ins_btap_roi_voxelset.setInt(4, x);
					ins_btap_roi_voxelset.setInt(5, y);
					ins_btap_roi_voxelset.setInt(6, z);
					ins_btap_roi_voxelset.setObject(7, getNullableInt("png_intensity", rset), java.sql.Types.INTEGER);
					ins_btap_roi_voxelset.setObject(8, getNullableInt("dcm_intensity", rset), java.sql.Types.INTEGER);
					ins_btap_roi_voxelset.setBoolean(9, bounder == 1);
					ins_btap_roi_voxelset.executeUpdate();
					connDest.commit();
				}
			}
			sel_btap_roi_voxelset.close();
			ins_btap_roi_voxelset.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_roi_voxelset_unreg() {
		try {
			PreparedStatement sel_rcnt = connSrc.prepareStatement("SELECT count(*) FROM pix4h.btap_roi_voxelset_unreg");
			ResultSet rset_rcnt = sel_rcnt.executeQuery();
			rset_rcnt.next();
			int row_cnt = rset_rcnt.getInt(1);
			for(int i = 0; i < row_cnt; i = i + 5000) {
				sel_btap_roi_voxelset_unreg.setInt(1, i);
				sel_btap_roi_voxelset_unreg.setInt(2, 5000);
				ResultSet rset = sel_btap_roi_voxelset_unreg.executeQuery();
				while(rset.next()) {
					int roi_voxelset_unreg_id = rset.getInt("roi_voxelset_unreg_id");
					int study_id = rset.getInt("study_id");
					int roi_id = rset.getInt("roi_id");
					int x = rset.getInt("x");
					int y = rset.getInt("y");
					int z = rset.getInt("z");
					int bounder = rset.getInt("bounder");
					
					ins_btap_roi_voxelset_unreg.setInt(1, roi_voxelset_unreg_id);
					ins_btap_roi_voxelset_unreg.setInt(2, study_id);
					ins_btap_roi_voxelset_unreg.setInt(3, roi_id);
					ins_btap_roi_voxelset_unreg.setInt(4, x);
					ins_btap_roi_voxelset_unreg.setInt(5, y);
					ins_btap_roi_voxelset_unreg.setInt(6, z);
					ins_btap_roi_voxelset_unreg.setBoolean(7, bounder == 1);
					ins_btap_roi_voxelset_unreg.executeUpdate();
					connDest.commit();
				}
			}
			sel_btap_roi_voxelset_unreg.close();
			ins_btap_roi_voxelset_unreg.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_rois() {
		try {
			ResultSet rset = sel_btap_rois.executeQuery();
			while(rset.next()) {
				int roi_id = rset.getInt("roi_id");
				String roi_description = rset.getString("roi_description");
				
				ins_btap_rois.setInt(1, roi_id);
				ins_btap_rois.setObject(2, roi_description, java.sql.Types.VARCHAR);
				ins_btap_rois.executeUpdate();
				connDest.commit();
			}
			sel_btap_rois.close();
			ins_btap_rois.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public void migrate_btap_study() {
		try {
			ResultSet rset = sel_btap_study.executeQuery();
			while(rset.next()) {
				int study_id = rset.getInt("study_id");
				int pid = rset.getInt("pid");
				String study_description = rset.getString("study_description");
				java.sql.Timestamp study_date = rset.getTimestamp("study_date");
				String institution_name = rset.getString("institution_name");
				String station_name = rset.getString("station_name");
				
				ins_btap_study.setInt(1, study_id);
				ins_btap_study.setInt(2, pid);
				ins_btap_study.setObject(3, study_description, java.sql.Types.VARCHAR);
				ins_btap_study.setObject(4, study_date, java.sql.Types.TIMESTAMP);
				ins_btap_study.setObject(5, institution_name, java.sql.Types.VARCHAR);
				ins_btap_study.setObject(6, station_name, java.sql.Types.VARCHAR);
				ins_btap_study.executeUpdate();
				connDest.commit();
			}
			ins_btap_study.close();
			sel_btap_study.close();
		} catch(SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	public Boolean yes_no_to_bool(String str) {
		if(str == null) {
			return new Boolean(false);
		} else if(str.toLowerCase().equals("yes")) {
			return new Boolean(true);
		} else if(str.toLowerCase().equals("no")) {
			return new Boolean(false);
		} else {
			return new Boolean(false);
		}
	}
	
	public Boolean tf_to_bool(String str) {
		if(str == null) {
			return new Boolean(false);
		} else if(str.toLowerCase().equals("true")) {
			return new Boolean(true);
		} else if(str.toLowerCase().equals("false")) {
			return new Boolean(false);
		} else {
			return new Boolean(false);
		}
	}
	
	public java.sql.Date dd_MM_yyyy_to_sqlDate(String str) {
		if(str == null) {
			return null;
		} else if(str.equals("")) {
			return null;
		} else {
			try {
				return new java.sql.Date(new SimpleDateFormat("dd-MMM-yyyy").parse(str).getTime());
			} catch (ParseException e) {
				return null;
			}
		}
	}
	
	public java.sql.Date f_initial_treatment(int formdata_id, int pid, String str) {
		if(str == null) {
			return null;
		} else if(str.equals("")) {
			return null;
		} else if(str.toLowerCase().contains("did not start") || str.toLowerCase().contains("did not finish")){
			return null;
		} else {
			try {
				return new java.sql.Date(new SimpleDateFormat("dd-MMM-yyyy").parse(str).getTime());
			} catch (ParseException e) {
				try {
					logFile.append("Error parsing date on: formdata_id=" + formdata_id + " pid=" + pid + " date=" + str + newLine);
					return null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public int parseInt(String str) {
		if(str == null) {
			return -1;
		} else if (str.equals("")){
			return -1;
		} else {
			try {
			return Integer.parseInt(str.trim());
			} catch (NumberFormatException ignore) {
				return -1;
			}
		}
	}
	
	public float parseFloat(String str) {
		if(str == null) {
			return -1;
		} else if (str.equals("")){
			return -1;
		} else {
			try {
			return Float.parseFloat(str.trim());
			} catch (NumberFormatException ignore) {
				return -1;
			}
		}
	}
	
	Integer getNullableInt(String colName, ResultSet rs) throws SQLException {
		  
		int colValue = rs.getInt(colName);  
		  
		if (rs.wasNull()) {  
			return null;  
		}  
		return colValue;  
	}
}
