package tools.database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Random;

import tools.admin.AdminConnector;
import btaw.shared.model.BTAWDatabaseException;

public class Generator {

	private Random rand = new Random(System.currentTimeMillis());
	private Connection conn = null;

	public Generator() {
		rand = new Random(System.currentTimeMillis());
	}

	public static void main(String[] args) {
		Generator gen = new Generator();
		try {
			gen.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Connected to database.");
		gen.genData(500);
		gen.disconnect();
	}

/* SECURE DATABASE CONNECTION METHODS */

	public void doConnect(String[] args) throws BTAWDatabaseException {
		conn = AdminConnector.doConnect(args);
	}

	public boolean isConnected() {
		try {
			return ((conn != null) && !conn.isClosed());
		} catch (Exception ex) {
			return false;
		}
	}

	public void disconnect() {
		try {
			conn.commit();
			conn.close();
		} catch (Exception ex) {
			System.out.println("Error closing connection.");
			ex.printStackTrace();
		}
		conn = null;
	}
	
	/*** FAKE DATA FIELDS ***/
	PreparedStatement ps_person = null;
	PreparedStatement ps_formdata_cns_systemic_therapy = null;
	PreparedStatement ps_formdata_cns_scans = null;
	PreparedStatement ps_formdata_cns_progression = null;
	PreparedStatement ps_formdata_cns_presentation = null;
	PreparedStatement ps_formdata_cns_medications = null;
	PreparedStatement ps_formdata_cns_initial_treatment = null;
	PreparedStatement ps_formdata_cns_clinical_trials = null;
	PreparedStatement ps_formdata_cns_diagnosis_bx = null;
	PreparedStatement ps_formdata_cns_history = null;
	PreparedStatement ps_formdata_cns_identification = null;
	PreparedStatement ps_btap_study = null;
	PreparedStatement ps_btap_rois = null;
	PreparedStatement ps_btap_bounding_box = null;
	PreparedStatement ps_btap_imagetype = null;
	PreparedStatement ps_btap_image = null;
	PreparedStatement ps_btap_roi_voxelset = null;
	PreparedStatement ps_btap_roi_voxelset_unreg = null;
	PreparedStatement ps_btap_roi_centerofmass = null;

	/*** FAKE DATA GENERATOR METHODS ***/
	
	//TODO: javadoc
	public void genData(int numRecords) {
		prepAllStatements();
		try {
			Statement truncateStmt = conn.createStatement();
			System.out.println("Clearing existing tables before adding fake data.");
			truncateStmt.execute("TRUNCATE btaw.btap_bounding_box, btaw.btap_image, btaw.btap_imagetype, btaw.btap_roi_voxelset, btaw.btap_roi_voxelset_unreg, btaw.btap_rois, btaw.btap_study, btaw.formdata_cns_clinical_trials, btaw.formdata_cns_diagnosis_bx, btaw.formdata_cns_history, btaw.formdata_cns_identification, btaw.formdata_cns_initial_treatment, btaw.formdata_cns_medications, btaw.formdata_cns_presentation, btaw.formdata_cns_progression, btaw.formdata_cns_scans, btaw.formdata_cns_systemic_therapy, btaw.person, btaw.btap_roi_centerofmass CASCADE");
			System.out.println("Adding fake data; this will take several minutes.");
			
			gen_btap_rois_insert(1);
			gen_btap_rois_insert(2);
			// TODO: Should have variable number of records depending on table
			for(int i = 0; i < numRecords; i++) {
				if ( i % 100 == 0) { // Let's do a commit every hundred rows
					conn.commit();
				}
				
				gen_person_insert(i); // TODO: Should track used pid's for use in other tables (foreign key)
				gen_btap_study_insert(i, i); // TODO: Should have a limited number of repeated study_id's
				gen_btap_imagetype_insert(i, i); // There really are that many distinct imagetype_id's
				gen_formdata_cns_clinical_trials_insert(i, i);
				gen_formdata_cns_diagnosis_bx_insert(i, i);
				gen_formdata_cns_history_insert(i, i);
				gen_formdata_cns_identification_insert(i, i);
				gen_formdata_cns_initial_treatment_insert(i, i);
				gen_formdata_cns_medications_insert(i, i);
				gen_formdata_cns_presentation_insert(i, i);
				gen_formdata_cns_progression_insert(i, i);
				gen_formdata_cns_scans_insert(i, i);
				gen_formdata_cns_systemic_therapy_insert(i, i);
				gen_btap_bounding_box_insert(i, i%10, (rand.nextInt(2) + 1)); // TODO: second i is z-values: do something sensible with them; third is roi_id - should store list of actual roi_id's
				gen_btap_roi_centerofmass_insert(i, i, (rand.nextInt(2) + 1),
						rand.nextFloat() * 327 + 19,
						rand.nextFloat() * 287 - 73,
						rand.nextFloat() * 75 + 2); //TODO: third i is roi_id - should store list of actual roi_id's
				gen_btap_roi_voxelset_insert(i, i, (rand.nextInt(2) + 1)); //TODO: get actual roi_id values instead of random 1 or 2
				gen_btap_roi_voxelset_unreg_insert(i, i, (rand.nextInt(2) + 1)); //TODO: get actual roi_id values instead of random 1 or 2
				gen_btap_image_insert(i);
			}
			
			conn.commit();
			
			// Free resources: PreparedStatements
			ps_btap_rois.close();
			ps_person.close();
			ps_btap_study.close();
			ps_btap_imagetype.close();
			ps_formdata_cns_clinical_trials.close();
			ps_formdata_cns_diagnosis_bx.close();
			ps_formdata_cns_history.close();
			ps_formdata_cns_identification.close();
			ps_formdata_cns_initial_treatment.close();
			ps_formdata_cns_medications.close();
			ps_formdata_cns_presentation.close();
			ps_formdata_cns_progression.close();
			ps_formdata_cns_scans.close();
			ps_formdata_cns_systemic_therapy.close();
			ps_btap_bounding_box.close();
			ps_btap_roi_centerofmass.close();
			ps_btap_roi_voxelset.close();
			ps_btap_roi_voxelset_unreg.close();
			ps_btap_image.close();

		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1); 
		}
	}
	
	//TODO: javadoc
	public void prepAllStatements() {
		String insertPerson = "INSERT INTO btaw.person (pid, date_reg, date_birth, sex, initials, modify_time, source_cci, source_cr, date_update, modify_id, create_id, create_time, export) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFSysTherapy = "INSERT INTO btaw.formdata_cns_systemic_therapy (formdata_id, pid, sys_therapy_regimen, sys_therapy_payment, sys_therapy_start_date, sys_therapy_end_date, sys_therapy_nr_cycles, sys_therapy_total_dose, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFScans = "INSERT INTO btaw.formdata_cns_scans (formdata_id, pid, initial_scan, date_initial_scan, scanning_facility, x_size_tumour, y_size_tumour, z_size_tumour, loc_left_frontal, loc_right_frontal, loc_left_temporal, loc_right_temporal, loc_left_parietal, loc_right_parietal, loc_left_occipital, loc_right_occipital, loc_left_cerebellum, loc_right_cerebellum, loc_brainstem, loc_spine, loc_comments, enhancement, date_presentation_neurosurgery, date_initial_consultation, multifocal, user_create, form_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFProgression = "INSERT INTO btaw.formdata_cns_progression (formdata_id, pid, prog_type, prog_date_scan, prog_scanning_facility, prog_trans_confirmed, prog_surgery, prog_date_surgery, prog_surgical_facility, prog_extent_of_resection, prog_final_diag_icd, prog_final_diag, prog_final_diag_grade, prog_date_final_diag_path_reported, prog_p_loss_of_heterozyg, prog_q_loss_of_heterozyg, prog_mgmt, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFPresentation = "INSERT INTO btaw.formdata_cns_presentation (formdata_id, pid, ecog, kps, seizures, headaches, memory_loss, nausea_vomiting, speech_problems, difficulty_walking, tinnitus, visual_changes, hearing_changes, focal_weakness, confusion, personality_changes, dizziness, other_symptoms, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFMedication = "INSERT INTO btaw.formdata_cns_medications (formdata_id, pid, decadron, decadron_current_dose, dilantin, dilantin_current_dose, other_medication, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFTreatment = "INSERT INTO btaw.formdata_cns_initial_treatment (formdata_id, pid, initial_treat_rt, initial_treat_rt_start_date, initial_treat_rt_end_date, initial_treat_fraction_nr, initial_treat_rt_total_dose, initial_treat_rt_type, initial_treat_second_rt_start_date, initial_treat_second_rt_end_date, initial_treat_second_rt_total_dose, initial_treat_second_fraction_nr, initial_treat_second_rt_type, initial_treat_chemo, initial_treat_drug_tmz, initial_treat_drug_other, initial_treat_name_other_drug, initial_treat_chemo_payment, initial_treat_chemo_start_date, initial_treat_chemo_end_date, initial_treat_chemo_total_dose, user_create, formdata_create_time, modify_user, modify_time, initial_treat_chemo_post_rt_cycles) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFClinicalTrials = "INSERT INTO btaw.formdata_cns_clinical_trials (formdata_id, pid, study_number, study_name, user_create, formdata_create_time, modify_user, modify_time, study_type) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFDiagnosisBx = "INSERT INTO btaw.formdata_cns_diagnosis_bx (formdata_id, pid, surgery_bx, date_bx, surgical_facility, extent_of_resection, prelim_diag, prelim_diag_icd, prelim_diag_grade, date_prelim_diag_path_reported, final_diag, final_diag_icd, final_diag_grade, date_final_diag_path_reported, p_loss_of_heterozyg, q_loss_of_heterozyg, mgmt, diag_path_consult, diag_path_consult_icd, diag_path_consult_grade, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFHistory = "INSERT INTO btaw.formdata_cns_history (formdata_id, pid, past_diagnoses, allergies, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertFIdentification = "INSERT INTO btaw.formdata_cns_identification (formdata_id, pid, date_death, patient_height, patient_weight, handedness, user_create, formdata_create_time, modify_user, modify_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertStudy = "INSERT INTO btaw.btap_study (study_id, pid, study_date, institution_name, station_name) VALUES(?, ?, ?, ?, ?)";
		String insertRois = "INSERT INTO btaw.btap_rois (roi_id, roi_desc) VALUES(?, ?)";
		String insertBBox = "INSERT INTO btaw.btap_bounding_box (bb_id, study_id, z, roi_id, x1, x2, y1, y2) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertImageType = "INSERT INTO btaw.btap_imagetype (imagetype_id, study_id, type_description, comment, echo_time, inversion_time, repetition_time, scon_sequence) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertImage = "INSERT INTO btaw.btap_image (image_id, imagetype_id, slicenr, image) VALUES(?, ?, ?, ?)";
		String insertRVoxelset = "INSERT INTO btaw.btap_roi_voxelset (roi_voxelset_id, study_id, roi_id, x, y, z, png_intensity, bounder) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		String insertRVoxelsetUnreg = "INSERT INTO btaw.btap_roi_voxelset_unreg (roi_voxelset_unreg_id, study_id, roi_id, x, y, z, bounder) VALUES(?, ?, ?, ?, ?, ?, ?)";
		String insertRoisAggregate = "INSERT INTO btaw.btap_roi_centerofmass (roi_com_id, study_id, roi_id, x, y, z) VALUES(?, ?, ?, ?, ?, ?)";
		
		try {
			ps_person = conn.prepareStatement(insertPerson);
			ps_formdata_cns_systemic_therapy = conn.prepareStatement(insertFSysTherapy);
			ps_formdata_cns_scans = conn.prepareStatement(insertFScans);
			ps_formdata_cns_progression = conn.prepareStatement(insertFProgression);
			ps_formdata_cns_presentation = conn.prepareStatement(insertFPresentation);
			ps_formdata_cns_medications = conn.prepareStatement(insertFMedication);
			ps_formdata_cns_initial_treatment = conn.prepareStatement(insertFTreatment);
			ps_formdata_cns_clinical_trials = conn.prepareStatement(insertFClinicalTrials);
			ps_formdata_cns_diagnosis_bx = conn.prepareStatement(insertFDiagnosisBx);
			ps_formdata_cns_history = conn.prepareStatement(insertFHistory);
			ps_formdata_cns_identification = conn.prepareStatement(insertFIdentification);
			ps_btap_study = conn.prepareStatement(insertStudy);
			ps_btap_rois = conn.prepareStatement(insertRois);
			ps_btap_bounding_box = conn.prepareStatement(insertBBox);
			ps_btap_imagetype = conn.prepareStatement(insertImageType);
			ps_btap_image = conn.prepareStatement(insertImage);
			ps_btap_roi_voxelset = conn.prepareStatement(insertRVoxelset);
			ps_btap_roi_voxelset_unreg = conn.prepareStatement(insertRVoxelsetUnreg);
			ps_btap_roi_centerofmass = conn.prepareStatement(insertRoisAggregate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*** Insertion Methods ***/
	
	/**
	 * Inserts into person fake data.
	 * @param Takes pid (primary key).
	 */
	private void gen_person_insert(int pid) {
		try {
			//ps_person.clearParameters();
			ps_person.setInt(1, pid);
			ps_person.setTimestamp(2, gen_timestamp());
			ps_person.setDate(3, gen_date());
			ps_person.setString(4, gen_person_gender());
			ps_person.setString(5, gen_person_initials());
			ps_person.setTimestamp(6, gen_timestamp());
			ps_person.setObject(7, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_person.setObject(8, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_person.setTimestamp(9, gen_timestamp());
			ps_person.setString(10, null);
			ps_person.setString(11, null);
			ps_person.setTimestamp(12, gen_timestamp());
			ps_person.setInt(13, rand.nextInt(2));
			ps_person.execute();
			//ps_person.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into the table with randomly generated values for the table formdata_cns_systemic_therapy
	 * @param Takes in two integers, first specifying the formdata_id (primary key) and the second the pid (foreign_key)
	 */
	public void gen_formdata_cns_systemic_therapy_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_systemic_therapy.clearParameters();
			ps_formdata_cns_systemic_therapy.setInt(1, formdata_id);
			ps_formdata_cns_systemic_therapy.setInt(2, pid);
			ps_formdata_cns_systemic_therapy.setObject(3, gen_systemicTherapy_sys_therapy_regimen(), java.sql.Types.VARCHAR);
			ps_formdata_cns_systemic_therapy.setObject(4, gen_payment(), java.sql.Types.VARCHAR);
			ps_formdata_cns_systemic_therapy.setDate(5, gen_date());
			ps_formdata_cns_systemic_therapy.setDate(6, gen_date());
			ps_formdata_cns_systemic_therapy.setObject(7, gen_systemicTherapy_sys_therapy_nr_cycles(), java.sql.Types.VARCHAR);
			ps_formdata_cns_systemic_therapy.setObject(8, gen_systemicTherapy_sys_therapy_total_dose(), java.sql.Types.VARCHAR);
			ps_formdata_cns_systemic_therapy.setObject(9, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_systemic_therapy.setTimestamp(10, gen_timestamp());
			ps_formdata_cns_systemic_therapy.setObject(11, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_systemic_therapy.setTimestamp(12, gen_timestamp());
			ps_formdata_cns_systemic_therapy.execute();
			//ps_formdata_cns_systemic_therapy.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}

	/**
	 * Inserts into the table formdata_cns_scan with randomly generated values.
	 * @param Takes in formdata_id (primary key) and pid (foreign key)
	 */
	public void gen_formdata_cns_scans_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_scans.clearParameters();
			ps_formdata_cns_scans.setInt(1, formdata_id);
			ps_formdata_cns_scans.setInt(2, pid);
			ps_formdata_cns_scans.setObject(3, gen_cnsScans_initial_scan(), java.sql.Types.VARCHAR);
			ps_formdata_cns_scans.setDate(4, gen_date());
			ps_formdata_cns_scans.setObject(5, gen_cnsScans_scanning_facility(), java.sql.Types.VARCHAR);
			for(int i = 6; i < 9; i++) {
				ps_formdata_cns_scans.setFloat(i, gen_cnsScans_size_tumour_on_scan());
			}
			for(int i = 9; i < 21; i++) {
				ps_formdata_cns_scans.setObject(i, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			}
			ps_formdata_cns_scans.setObject(21, gen_cnsScans_loc_comments(), java.sql.Types.VARCHAR);
			ps_formdata_cns_scans.setObject(22, gen_cnsScans_enhancement(), java.sql.Types.VARCHAR);
			ps_formdata_cns_scans.setDate(23, gen_date());
			ps_formdata_cns_scans.setDate(24, gen_date());
			ps_formdata_cns_scans.setObject(25, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_scans.setObject(26, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_scans.setTimestamp(27, gen_timestamp());
			ps_formdata_cns_scans.setObject(28, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_scans.setTimestamp(29, gen_timestamp());
			ps_formdata_cns_scans.execute();
			//ps_formdata_cns_scans.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
		
	}

	/**
	 * Inserts into formdata_cns_progression a row with random values.
	 * @param Takes in two integers, first formdata_id (primary key) and second pid (foreign key).
	 */
	public void gen_formdata_cns_progression_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_progression.clearParameters();
			ps_formdata_cns_progression.setInt(1, formdata_id);
			ps_formdata_cns_progression.setInt(2, pid);
			ps_formdata_cns_progression.setObject(3, gen_cnsProgression_prog_type(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setDate(4, gen_date());
			ps_formdata_cns_progression.setObject(5, gen_cnsProgression_prog_scanning_facility(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setObject(6, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_progression.setObject(7, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_progression.setDate(8, gen_date());
			ps_formdata_cns_progression.setObject(9, gen_surgical_facility(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setObject(10, gen_extent_of_resection(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setObject(11, gen_random_int_or_null(5), java.sql.Types.BIGINT);
			ps_formdata_cns_progression.setObject(12, gen_diag(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setObject(13, gen_cnsProgression_prog_final_diag(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setDate(14, gen_date());
			ps_formdata_cns_progression.setObject(15, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_progression.setObject(16, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_progression.setObject(17, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_progression.setObject(18, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setTimestamp(19, gen_timestamp());
			ps_formdata_cns_progression.setObject(20, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_progression.setTimestamp(21, gen_timestamp());
			ps_formdata_cns_progression.execute();
			//ps_formdata_cns_progression.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
		
	}

	/**
	 * Inserts randomly generated values into formdata_cns_presentation
	 * @param Takes in two integers specifying formdata_id (primary key) and pid (foreign key)
	 */
	public void gen_formdata_cns_presentation_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_presentation.clearParameters();
			ps_formdata_cns_presentation.setInt(1, formdata_id);
			ps_formdata_cns_presentation.setInt(2, pid);
			ps_formdata_cns_presentation.setObject(3, gen_cnsPresentation_ecog(), java.sql.Types.FLOAT);
			ps_formdata_cns_presentation.setInt(4, rand.nextInt(11)*10);
			for(int i = 5; i <= 17; i++) {
				ps_formdata_cns_presentation.setObject(i, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			}
			ps_formdata_cns_presentation.setObject(18, gen_cnsPresentation_other_symptoms(), java.sql.Types.VARCHAR);
			ps_formdata_cns_presentation.setObject(19, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_presentation.setTimestamp(20, gen_timestamp());
			ps_formdata_cns_presentation.setObject(21, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_presentation.setTimestamp(22, gen_timestamp());
			ps_formdata_cns_presentation.execute();
			//ps_formdata_cns_presentation.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
		
	}

	/**
	 * Inserts into formdata_cns_medications fake data.
	 * @param Takes formdata_id (primary key) and pid (foreign key).
	 */
	private void gen_formdata_cns_medications_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_medications.clearParameters();
			ps_formdata_cns_medications.setInt(1, formdata_id);
			ps_formdata_cns_medications.setInt(2, pid);
			ps_formdata_cns_medications.setBoolean(3, gen_bool());
			ps_formdata_cns_medications.setObject(4, gen_cnsMedications_decadron_current_dose(), java.sql.Types.VARCHAR);
			ps_formdata_cns_medications.setBoolean(5, gen_bool());
			ps_formdata_cns_medications.setObject(6, gen_cnsMedications_dilantin_current_dose(), java.sql.Types.VARCHAR);
			ps_formdata_cns_medications.setObject(7, gen_cnsMedications_other_medication(), java.sql.Types.VARCHAR);
			ps_formdata_cns_medications.setObject(8, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_medications.setTimestamp(9, gen_timestamp());
			ps_formdata_cns_medications.setObject(10, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_medications.setTimestamp(11, gen_timestamp());
			ps_formdata_cns_medications.execute();
			//ps_formdata_cns_medications.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}

	/**
	 * Inserts into formdata_cns_initial_treatment fake data.
	 * @param Takes formdata_id (primary key) and pid (foreign key).
	 */
	private void gen_formdata_cns_initial_treatment_insert(int formdata_id, int pid) {
		java.sql.Date tmp;
		try {
			//ps_formdata_cns_initial_treatment.clearParameters();
			ps_formdata_cns_initial_treatment.setInt(1, formdata_id);
			ps_formdata_cns_initial_treatment.setInt(2, pid);
			ps_formdata_cns_initial_treatment.setObject(3, gen_cnsInitialTreatment_initial_treat_rt(), java.sql.Types.VARCHAR);
			tmp = gen_date();
			ps_formdata_cns_initial_treatment.setDate(4, tmp);
			ps_formdata_cns_initial_treatment.setDate(5, gen_date_after(tmp));
			ps_formdata_cns_initial_treatment.setObject(6, gen_cnsInitialTreatment_initial_treat_fraction_nr(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setObject(7, gen_cnsInitialTreatment_initial_treat_rt_total_dose(), java.sql.Types.VARCHAR);
			tmp = gen_date();
			ps_formdata_cns_initial_treatment.setDate(8, tmp);
			ps_formdata_cns_initial_treatment.setDate(9, gen_date_after(tmp));
			ps_formdata_cns_initial_treatment.setDate(10, gen_date_after(tmp));
			ps_formdata_cns_initial_treatment.setObject(11, gen_cnsInitialTreatment_initial_treat_rt_total_dose(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setObject(12, gen_cnsInitialTreatment_initial_treat_fraction_nr(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setObject(13, gen_cnsInitialTreatment_initial_treat_rt_type(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setObject(14, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_initial_treatment.setObject(15, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_initial_treatment.setObject(16, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_initial_treatment.setObject(17, gen_cnsInitialTreatment_initial_treat_name_other_drug(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setObject(18, gen_payment(), java.sql.Types.VARCHAR);
			tmp = gen_date();
			ps_formdata_cns_initial_treatment.setDate(19, tmp);
			ps_formdata_cns_initial_treatment.setDate(20, gen_date_after(tmp));
			ps_formdata_cns_initial_treatment.setObject(21, gen_cnsInitialTreatment_initial_treat_total_dose(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setObject(22, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setTimestamp(23, gen_timestamp());
			ps_formdata_cns_initial_treatment.setObject(24, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_initial_treatment.setTimestamp(25, gen_timestamp());
			ps_formdata_cns_initial_treatment.setObject(26, gen_random_int_or_null(1), java.sql.Types.SMALLINT);
			ps_formdata_cns_initial_treatment.execute();
			//ps_formdata_cns_initial_treatment.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Insert into formdata_cns_clinical_trials fake data.
	 * @param Takes formdata_id (primary key) and pid (foreign key).
	 */
	private void gen_formdata_cns_clinical_trials_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_clinical_trials.clearParameters();
			ps_formdata_cns_clinical_trials.setInt(1, formdata_id);
			ps_formdata_cns_clinical_trials.setInt(2, pid);
			ps_formdata_cns_clinical_trials.setObject(3, gen_clinicalTrials_study_number(), java.sql.Types.VARCHAR);
			ps_formdata_cns_clinical_trials.setObject(4, gen_clinicalTrials_study_name(), java.sql.Types.VARCHAR);
			ps_formdata_cns_clinical_trials.setObject(5, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_clinical_trials.setTimestamp(6, gen_timestamp());
			ps_formdata_cns_clinical_trials.setObject(7, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_clinical_trials.setTimestamp(8, gen_timestamp());
			ps_formdata_cns_clinical_trials.setObject(9, gen_clinicalTrials_study_type(), java.sql.Types.VARCHAR);
			ps_formdata_cns_clinical_trials.execute();
			//ps_formdata_cns_clinical_trials.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Insert into formdata_cns_diagnosis_bx fake data.
	 * @param Takes formdata_id (primary key) and pid (foreign key).
	 */
	private void gen_formdata_cns_diagnosis_bx_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_diagnosis_bx.clearParameters();
			ps_formdata_cns_diagnosis_bx.setInt(1, formdata_id);
			ps_formdata_cns_diagnosis_bx.setInt(2, pid);
			ps_formdata_cns_diagnosis_bx.setObject(3, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_diagnosis_bx.setDate(4, gen_date());
			ps_formdata_cns_diagnosis_bx.setObject(5, gen_surgical_facility(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setObject(6, gen_extent_of_resection(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setObject(7, gen_diag(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setObject(8, gen_random_int_or_null(5), java.sql.Types.BIGINT);
			ps_formdata_cns_diagnosis_bx.setObject(9, gen_diag_grade(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setDate(10, gen_date());
			ps_formdata_cns_diagnosis_bx.setObject(11, gen_diag(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setObject(12, gen_random_int_or_null(5), java.sql.Types.BIGINT);
			ps_formdata_cns_diagnosis_bx.setObject(13, gen_diag(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setDate(14, gen_date());
			ps_formdata_cns_diagnosis_bx.setObject(15, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_diagnosis_bx.setObject(16, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_diagnosis_bx.setObject(17, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_formdata_cns_diagnosis_bx.setObject(18, gen_diag(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setObject(19, gen_random_int_or_null(5), java.sql.Types.BIGINT);
			ps_formdata_cns_diagnosis_bx.setObject(20, gen_diag_grade(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setObject(21, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setTimestamp(22, gen_timestamp());
			ps_formdata_cns_diagnosis_bx.setObject(23, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_diagnosis_bx.setTimestamp(24, gen_timestamp());
			ps_formdata_cns_diagnosis_bx.execute();
			//ps_formdata_cns_diagnosis_bx.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Insert into formdata_cns_history fake data.
	 * @param Takes formdata_id (primary key) and pid (foreign key).
	 */
	private void gen_formdata_cns_history_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_history.clearParameters();
			ps_formdata_cns_history.setInt(1, formdata_id);
			ps_formdata_cns_history.setInt(2, pid);
			ps_formdata_cns_history.setObject(3, gen_cnsHistory_past_diagnoses(), java.sql.Types.VARCHAR);
			ps_formdata_cns_history.setObject(4, gen_cnsHistory_allergies(), java.sql.Types.VARCHAR);
			ps_formdata_cns_history.setObject(5, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_history.setTimestamp(6, gen_timestamp());
			ps_formdata_cns_history.setObject(7, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_history.setTimestamp(8, gen_timestamp());
			ps_formdata_cns_history.execute();
			//ps_formdata_cns_history.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Insert into formdata_cns_identification fake data.
	 * @param Takes formdata_id (primary key) and pid (foreign key).
	 */
	private void gen_formdata_cns_identification_insert(int formdata_id, int pid) {
		try {
			//ps_formdata_cns_identification.clearParameters();
			ps_formdata_cns_identification.setInt(1, formdata_id);
			ps_formdata_cns_identification.setInt(2, pid);
			ps_formdata_cns_identification.setDate(3, gen_date()); //TODO: date death after date birth not enforced...
			ps_formdata_cns_identification.setFloat(4, rand.nextFloat() + rand.nextInt(150) + 100);
			ps_formdata_cns_identification.setFloat(5, rand.nextFloat() + rand.nextInt(300) + 75);
			ps_formdata_cns_identification.setObject(6, gen_cnsIdentification_handedness(), java.sql.Types.VARCHAR);
			ps_formdata_cns_identification.setObject(7, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_identification.setTimestamp(8, gen_timestamp());
			ps_formdata_cns_identification.setObject(9, gen_user(), java.sql.Types.VARCHAR);
			ps_formdata_cns_identification.setTimestamp(10, gen_timestamp());
			ps_formdata_cns_identification.execute();
			//ps_formdata_cns_identification.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}

	/**
	 * Inserts into btap_study fake data.
	 * @param Takes study_id (primary key) and pid (foreign key).
	 */
	private void gen_btap_study_insert(int study_id, int pid) {
		try {
			//ps_btap_study.clearParameters();
			ps_btap_study.setInt(1, study_id);
			ps_btap_study.setInt(2, pid);
			ps_btap_study.setDate(3, gen_date());
			ps_btap_study.setObject(4, gen_btapStudy_institution_name(), java.sql.Types.VARCHAR);
			ps_btap_study.setObject(5, gen_btapStudy_station_name(), java.sql.Types.VARCHAR);
			ps_btap_study.execute();
			//ps_btap_study.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Insert into btap_rois fake data.
	 * @param Takes roi_id (primary key)
	 */
	private void gen_btap_rois_insert(int roi_id) {
		try {
			//ps_btap_rois.clearParameters();
			ps_btap_rois.setInt(1, roi_id);
			ps_btap_rois.setString(2, gen_btapRois_roi_description());
			ps_btap_rois.execute();
			//ps_btap_rois.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into btap_bounding_box fake data.
	 * @param Takes study_id (foreign key), z (primary key) and roi_id (foreign key).
	 */
	private void gen_btap_bounding_box_insert(int study_id, int z, int roi_id) {
		try {
			//ps_btap_bounding_box.clearParameters();
			ps_btap_bounding_box.setInt(1, study_id);
			ps_btap_bounding_box.setInt(2, study_id);
			ps_btap_bounding_box.setInt(3, z);
			ps_btap_bounding_box.setInt(4, roi_id);
			ps_btap_bounding_box.setInt(5, rand.nextInt(256));
			ps_btap_bounding_box.setInt(6, rand.nextInt(256));
			ps_btap_bounding_box.setInt(7, rand.nextInt(256));
			ps_btap_bounding_box.setInt(8, rand.nextInt(256));
			ps_btap_bounding_box.execute();
			//ps_btap_bounding_box.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into btap_imagetype fake data.
	 * @param Takes imagetype_id (primary key) and study_id (foreign key).
	 */
	private void gen_btap_imagetype_insert(int imagetype_id, int study_id) {
		try {
			//ps_btap_imagetype.clearParameters();
			ps_btap_imagetype.setInt(1, imagetype_id);
			ps_btap_imagetype.setInt(2, study_id);
			ps_btap_imagetype.setObject(3, gen_btapImageType_type_description(), java.sql.Types.VARCHAR);
			ps_btap_imagetype.setObject(4, gen_btapImageType_comment(), java.sql.Types.VARCHAR);
			ps_btap_imagetype.setFloat(5, rand.nextFloat());
			ps_btap_imagetype.setFloat(6, rand.nextFloat());
			ps_btap_imagetype.setFloat(7, rand.nextFloat());
			ps_btap_imagetype.setString(8, "scon_sequence");
			ps_btap_imagetype.execute();
			//ps_btap_imagetype.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into btap_image fake data.
	 * @param Takes image_id (primary key).
	 */
	private void gen_btap_image_insert(int image_id) {
		try {
			//ps_btap_image.clearParameters();
			File file = new File("BTAW-GWT/resources/img/fake_image.jpg");
			try {
				FileInputStream fis = new FileInputStream(file);
				ps_btap_image.setInt(1, image_id);
				ps_btap_image.setInt(2, image_id);  //TODO: Total fudgery. This is actually image_type_id.
				ps_btap_image.setInt(3, rand.nextInt(600));
				ps_btap_image.setBinaryStream(4, fis, (int)file.length());
				ps_btap_image.execute();
				//ps_btap_image.close();
				//conn.commit();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into btap_roi_voxelset fake data.
	 * @param Takes roi_voxelset_id (primary key), roi_id (foreign key), study_id (foreign key).
	 */
	private void gen_btap_roi_voxelset_insert(int roi_voxelset_id, int roi_id, int study_id) {
		try {
			//ps_btap_roi_voxelset.clearParameters();
			ps_btap_roi_voxelset.setInt(1, roi_voxelset_id);
			ps_btap_roi_voxelset.setInt(2, roi_id);
			ps_btap_roi_voxelset.setInt(3, study_id);
			ps_btap_roi_voxelset.setInt(4, (rand.nextInt(250) + 1));
			ps_btap_roi_voxelset.setInt(5, (rand.nextInt(250) + 1));
			ps_btap_roi_voxelset.setInt(6, (rand.nextInt(250) + 1));
			ps_btap_roi_voxelset.setObject(7, gen_random_int_or_null(3), java.sql.Types.BIGINT );
			ps_btap_roi_voxelset.setObject(8, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_btap_roi_voxelset.execute();
			//ps_btap_roi_voxelset.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into btap_roi_voxelset_unreg fake data.
	 * @param Takes roi_voxelset_unreg_id (primary key), roi_id (foreign key), study_id (foreign key).
	 */
	private void gen_btap_roi_voxelset_unreg_insert(int roi_voxelset_unreg_id, int roi_id, int study_id) {
		try {
			//ps_btap_roi_voxelset_unreg.clearParameters();
			ps_btap_roi_voxelset_unreg.setInt(1, roi_voxelset_unreg_id);
			ps_btap_roi_voxelset_unreg.setInt(2, roi_id);
			ps_btap_roi_voxelset_unreg.setInt(3, study_id);
			ps_btap_roi_voxelset_unreg.setInt(4, (rand.nextInt(250) + 1));
			ps_btap_roi_voxelset_unreg.setInt(5, (rand.nextInt(250) + 1));
			ps_btap_roi_voxelset_unreg.setInt(6, (rand.nextInt(250) + 1));
			ps_btap_roi_voxelset_unreg.setObject(7, gen_bool_or_null(), java.sql.Types.BOOLEAN);
			ps_btap_roi_voxelset_unreg.execute();
			//ps_btap_roi_voxelset_unreg.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/**
	 * Inserts into btap_roi_centerofmass fake data.
	 * @param Takes btap_roi_agg_id (primary key), study_id (foreign key), roi_id (foreign key)
	 */
	private void gen_btap_roi_centerofmass_insert(int roi_com_id, int study_id, int roi_id, float x, float y, float z) {
		// TODO
		
		try {
			//ps_btap_roi_centerofmass.clearParameters();
			ps_btap_roi_centerofmass.setInt(1, roi_com_id);
			ps_btap_roi_centerofmass.setInt(2, study_id);
			ps_btap_roi_centerofmass.setInt(3, roi_id);
			ps_btap_roi_centerofmass.setFloat(4, x);
			ps_btap_roi_centerofmass.setFloat(5, y);
			ps_btap_roi_centerofmass.setFloat(6, z);
			//ps_btap_roi_centerofmass.setString(4, gen_btapRoisAggregateInfo_description());
			//ps_btap_roi_centerofmass.setString(5, gen_btapRoisAggregateInfo_value());
			ps_btap_roi_centerofmass.execute();
			//ps_btap_roi_centerofmass.close();
			//conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
			System.exit(-1);
		}
	}
	
	/*** Helper Methods ***/
	/*** Data applicable to multiple tables ***/
	
	/** 
	 * Returns a valid SqlDate since 1900.
	 * @return Valid SqlDate date since 1900
	 * It will be up to the caller to make sure that this date makes sense in context (eg. use gen_date_after(java.sql.Date startDate))
	 */
	private java.sql.Date gen_date() {
		Calendar calendarDate = Calendar.getInstance();
		java.sql.Date sqlDate = null;		
		calendarDate.set( Calendar.DAY_OF_MONTH, rand.nextInt(31) + 1 ); 	// Calendar days: 1-31
		calendarDate.set( Calendar.MONTH, rand.nextInt(12) );				// Calendar months: 0-11
		calendarDate.set( Calendar.YEAR, rand.nextInt(111) + 1900 ) ;
	    calendarDate.set(Calendar.HOUR_OF_DAY, 0);
	    calendarDate.set(Calendar.MINUTE, 0);
	    calendarDate.set(Calendar.SECOND, 0);
	    calendarDate.set(Calendar.MILLISECOND, 0);

	    sqlDate = new java.sql.Date(calendarDate.getTime().getTime());
		
		// Debugging
		//System.out.println( sqlDate.toString() );
		
		return sqlDate;
	}
	
	/** 
	 * Returns a valid SqlDate after date passed as argument.
	 * @return Valid SqlDate after date passed as argument.
	 * @param Valid SqlDate that this date should be after.
	 */
	private java.sql.Date gen_date_after(java.sql.Date startDate) {
		Calendar eDate = Calendar.getInstance();
		java.sql.Date sqlDate = null;
		
		 /* Strangely java.sql.Date is a subclass of java.util.Date deprecated functions and all.  Terrible to use, hence the conversion. */
		Calendar sDate = Calendar.getInstance();
		sDate.setTimeInMillis(startDate.getTime());
		
		eDate.set( Calendar.DAY_OF_MONTH, rand.nextInt(28) + 1);
		eDate.set( Calendar.MONTH, rand.nextInt(12) + 1);
		eDate.set( Calendar.YEAR, rand.nextInt(Math.abs(111 - (sDate.get(Calendar.YEAR) - 1900 - 1))) + sDate.get(Calendar.YEAR) + 1 );
	    eDate.set(Calendar.HOUR_OF_DAY, 0);
	    eDate.set(Calendar.MINUTE, 0);
	    eDate.set(Calendar.SECOND, 0);
	    eDate.set(Calendar.MILLISECOND, 0);

	    sqlDate = new java.sql.Date(eDate.getTime().getTime());
	    
	    if(eDate.before(sDate)) {
	    	eDate.set( Calendar.YEAR, eDate.get(Calendar.YEAR) + 1);
	    }
		
		return sqlDate;
	}
	
	/**
	 * Returns a valid SqlTimestamp since 2000.
	 * @return Valid SqlTimestamp date since 2000
	 */
	private java.sql.Timestamp gen_timestamp() {
		Calendar calendarDate = Calendar.getInstance();
		java.sql.Timestamp sqlTimestamp = null;
		
		calendarDate.set( Calendar.DAY_OF_MONTH, rand.nextInt(31) + 1 ); 	// Calendar days: 1-31
		calendarDate.set( Calendar.MONTH, rand.nextInt(12) );				// Calendar months: 0-11
		calendarDate.set( Calendar.YEAR, rand.nextInt(11) + 2000 ) ;
	    calendarDate.set( Calendar.HOUR_OF_DAY, rand.nextInt(24) );
	    calendarDate.set( Calendar.MINUTE, rand.nextInt(60) );
	    calendarDate.set( Calendar.SECOND, rand.nextInt(60) );
	    calendarDate.set( Calendar.MILLISECOND, rand.nextInt(1000) );

	    sqlTimestamp = new java.sql.Timestamp(calendarDate.getTime().getTime());
		
		// Debugging
		//System.out.println( sqlTimestamp.toString() );
		
		return sqlTimestamp;
	}
	
	/**
	 * Returns Boolean - NOT null
	 * @return Boolean. Disallows null values.
	 */
	private Boolean gen_bool() {
		return rand.nextBoolean();
	}
	
	/**
	 * Returns Boolean OR null
	 * @return Boolean. *Allows* null values.
	 */
	private Boolean gen_bool_or_null() { // Null is not a valid boolean value, so have to fall back to Object
		int chance = rand.nextInt(11);
		switch (chance) {
			case  1: case  2: case  3: case  4: case  5: 
				return false;
			case  6: case  7: case  8: case  9:	case 10: 
				return true;
			default: 
				return null;
		}
	}

	/**
	 * Returns a random integer, taking the number of digits as an argument.
	 * @param int - number of digits to return
	 * @return Random int with the requested number of digits
	 */
	private int gen_random_int( int num_digits ) {
		String intString = "";
		if ( num_digits >= 1)
		{
			intString += (rand.nextInt(9) + 1); // First digit non-zero
			for ( int i = 1; i < num_digits; i++ )  // Remaining digits
			{
				intString += (rand.nextInt(10));
			}
		}
		//System.out.println( intString );
		return Integer.valueOf( intString );
	}
	
	/**
	 * Returns a random integer (taking the number of digits as an argument) - OR null (10% of the time)
	 * @param int - number of digits to return
	 * @return Random int with the requested number of digits OR null (10% of the time)
	 */
	private Integer gen_random_int_or_null( int num_digits ) {
		int chance = rand.nextInt( 10 );
		if ( chance == 0 ) {
			//System.out.println( "null" );
			return null;
		}
		else {
			return gen_random_int( num_digits );
		}
	}
	
	/**
	 * Returns a random integer (taking the number of digits as an argument) - OR null (10% of the time)
	 * @param int - number of digits to return
	 * @return Random int with the requested number of digits OR null (10% of the time) as a String.
	 */
	private String gen_random_int_or_null_Str( int num_digits ) {
		Integer tmp = gen_random_int_or_null(num_digits);
		if( tmp == null ) {
			return null;
		} else {
			return Integer.toString(tmp);
		}
	}
	
	/**
	 * Returns one of the users entering data into the DB.
	 * @return User who entered the data into the DB
	 */
	private String gen_user() {
		String[] users = { null, "", "admin", "juliette", "LW", "maike", "prg", "sinja", "susan", };
		int index = rand.nextInt( users.length );
		String user = users[index] ;	
		//System.out.println(user);
		return user;
	}
	
	/**
	 * Returns a fake surgical facility.
	 * @return A fake surgical facility
	 * NOTE: Used in formdata_cns_diagnosis_bx, formdata_cns_progression
	 */
	private String gen_surgical_facility() {
		String[] facilities = { null, "", "RAH", "UAH", "other" };
		int index = rand.nextInt( facilities.length );
		String facility = facilities[index] ;
		//System.out.println(facility);
		return facility;		
	}
	
	/** Returns a fake extent of resection.
	 * @return A fake extent of resection
	 * NOTE: In the legacy DB, this field was misspelled as "extend"; fixed in Postgres
	 * NOTE: Use in formdata_cns_diagnosis_bx, formdata_cns_progression
	 */
	private String gen_extent_of_resection() {
		String[] extents = { null, "Bx", "Subtotal", "Total", "None", "Other" };
		int index = rand.nextInt( extents.length );
		String extent = extents[index] ;
		//System.out.println(extent);
		return extent;		
	}
	
	/**
	 * Returns fake prelim/final diag/progression diagnosis.
	 * @return Fake prelim/final diag/progression diagnosis as String.
     * NOTE: Use for *_diag, *_consult fields
	 */
	private String gen_diag() {
		String [] diags = { null, "Astrocytoma, NOS", "Astrocytoma, anaplastic", "Glioblastoma, NOS", "Glioma, malignant", "Meningioma, NOS", "Mixed glioma", "Multiple brain metastases", "Oligodendroglioma, NOS", "Oligodendroglioma, anaplastic", "Primitive neuoectodermal tumour", };
		int index = rand.nextInt( diags.length );
		String diag = diags[index];
		return diag;
	}

	/**
	 * Returns fake diag/progression prelim/final diag/consult grade.
	 * @return Fake diag/progression prelim/final diag/consult grade.
	 * NOTE: Used in formdata_cns_diagnosis_bx, formdata_cns_progression for *_grade
	 */
	private String gen_diag_grade() {
		String [] gen_diag_grades = { null, "4", "3", "1", "2-3", "2", "Unk" };
		int index = rand.nextInt( gen_diag_grades.length );
		String gen_diag_grade = gen_diag_grades[index];
		return gen_diag_grade;
	}
	
	
	/*** Table-specific data ***/
		/*  Naming convention: gen_tableName_ field_name() */
	
	
	/*** person ***/
	
	/* pid - postgres to assign */
	/* date_reg - use gen_date() */
	/* date_birth - use gen_date() */
	
	/**
	 * Returns a random m/f String for gender.
	 * @return Random gender (String: m/f)
	 */
	private String gen_person_gender() {
		if ( rand.nextInt(2) == 1 ) {
			return "f";
		}
		else {
			return "m";
		}
	}
	
	/**
	 * Returns a random set of initials for a person.
	 * @return Random initials (three-letter upper-case String)
	 * Use for person:initials
	 */
	private String gen_person_initials() {
		String initials = "";
		for ( int i = 0; i < 3; i++ )
		{
					initials += ((char) (rand.nextInt(26) + 'A'));
		}
		//System.out.println( initials );
		return initials;
	}
	
	/* modify_time - use gen_timestamp() */
	/* source_cci - use gen_boolean_or_null (I think; the postgres field type is actually bit */
	/* source_cr - use gen_boolean_or_null (I think; the postgres field type is actually bit */
	
	/* NOT USED */
	/**
	 * Returns a random first name (list taken from Wikipedia).
	 * @return Random first name
	 * Use for person:first_name
	 */
	private String gen_person_first_name() {

		String[] names = { "Aada", "Aaron", "Abigail", "Adam", "Adrian", "Agnieszka", "Aidan", "Aino", "Alba", "Alberto", "Alejandro", "Aleksander", "Alessandro", "Alessia", "Alex", "Alexander", "Alexandre", "Alexis", "Alice", "Alicia", "Alva", "Amy", "Ana", "Andre", "Andrea", "Andreas", "Andrew", "Anna", "Anna,", "Anne", "Anni", "Anouk", "Anthony", "Antoine", "Antonia", "Aoife", "Arnau", "Arthur", "Ashley", "Audrey", "Aurora", "Ava", "Barbara", "Beatriz", "Ben", "Benjamin", "Birta", "Caitlin", "Callum", "Cameron", "Camille", "Carla", "Carlos", "Carolina", "Caroline", "Catalina", "Catarina", "Catherine", "Charlie", "Charlotte", "Chiara", "Chloe", "Christopher", "Cian", "Ciara", "Clara", "Claudia", "Connor", "Conor", "Constanza", "Coralie", "Daniel", "David", "Davide", "Diana", "Diego", "Dorina", "Dylan", "Eduardo", "Elias", "Elin", "Ella", "Ellie", "Emil", "Emilia", "Emilie", "Emily", "Emma", "Emre", "Enzo", "Erin", "Eszter", "Ethan", "Eva", "Ewa", "Fabian", "Fanni", "Felipe", "Felix", "Fernanda", "Filip", "Filipa", "Florian", "Francesca", "Francesco", "Francisca", "Frederik", "Freja", "Furkan", "Gabriel", "Gabriele", "Georgia", "Gerard", "Giada", "Giorgia", "Giovanna", "Giulia", "Grace", "Graciela", "Guilherme", "Gulia", "Gustavo", "Hanna", "Hannah", "Harry", "Helga", "Henrik", "Hugo", "Ida", "Iida", "Ingrid", "Irati", "Iris", "Isa", "Isaac", "Isabel", "Isabella", "Isak", "Izaro", "Jack", "Jacob", "Jade", "Jake", "James", "Jamie", "Jan", "Jana", "Javier", "Javiera", "Jeremy", "Jessica", "Joana", "Joel", "John", "Jonas", "Jordi", "Jorge", "Joseph", "Joshua", "Juan", "Julia", "Julian", "Julie", "June", "Katarzyna", "Katharina", "Katie", "Kauan", "Kevin", "Kristian", "Kristina", "Krystyna", "Lachlan", "Laia", "Lara", "Larissa", "Laura", "Laurence", "Lea", "Lea,", "Leah", "Leire", "Lena", "Leon", "Leonie", "Lewis", "Liam", "Lily", "Lisa", "Logan", "Lorenzo", "Lotte", "Louis", "Luca", "Lucas", "Lucy", "Luis", "Luiz", "Lukas", "Lukas,", "Luke", "Madison", "Magnus", "Maia", "Maja", "Malin", "Manon", "Marc", "Margarida", "Maria", "Mariana", "Marie", "Markus", "Marta", "Martin", "Martina", "Matheus", "Mathias", "Mathilde", "Matteo", "Matthew", "Mattia", "Maxime", "Maximilian", "Megan", "Megane", "Mehmet", "Melissa", "Mia", "Michael", "Michela", "Miguel", "Mikkel", "Milan", "Mustafa", "Nahia", "Naroa", "Nathan", "Nerea", "Nicholas", "Nico", "Nicole", "Nikolaj", "Nina", "Noah", "Noemie", "Nora", "Norma", "Oliver", "Olivia", "Olivier", "Oscar", "Pablo", "Paige", "Patrick", "Pau", "Paul", "Paula", "Pedro", "Petra", "Pol", "Quentin", "Rachel", "Rasmus", "Rebecca", "Ricardo", "Robbe", "Roberto", "Robin", "Rosa", "Rosalie", "Ruby", "Ryan", "Samantha", "Samuel", "Sander", "Sandra", "Sanne", "Sara", "Sarah", "Sean", "Sergio", "Shania", "Siiri", "Silvia", "Simon", "Simone", "Sofia", "Sofie", "Sophie", "Sophie,", "Susana", "Sydney", "Taylor", "Telma", "Teresa", "Thea", "Thomas", "Tim", "Tobias", "Tyler", "Valentina", "Vanessa", "Venla", "Vicente", "Victor", "Viktor", "Vivien", "William", "Xavier", "Yasmin", "Yusuf", "Zac", "Zofia", };
		int index = rand.nextInt( names.length );
		String first_name = names[index];
		
		//System.out.println( first_name );
		return first_name;
	}
	
	/* NOT USED */
	/**
	 * Returns a random last name (list taken from Wikipedia).
	 * @return Random last name
	 */
	private String gen_person_last_name() {

		String[] names = { "Aadal", "Aadmi", "Aafjes", "Aaftink", "Aahmed", "Aaij", "Aajoud", "Aalam", "Aalberts", "Aalbers", "Aalberse", "Aalberts", "Aalbrecht", "Beach", "Beacom", "Beadle", "Beaker", "Beal", "Beale", "Bealey", "Bealie", "Beall", "Bealy", "Beaumont", "Bean", "Beane", "Beanz", "Bear", "Beard", "Bearne", "Bears", "Beasley", "Beatie", "Beato", "Beatrice", "Beatris", "Beattie", "Beatty", "Beau", "Beauchamp", "Beaudoin", "Beaudouin", "Beaufort", "Beaujean", "Beaumanor", "Beaumont", "Beauregard", "Beavan", "Beaver", "Beavers", "Beavis", "Bec", "Bech", "Bechard", "Becht", "Beck", "Beckand", "Becker", "Beckers", "Beckering", "Beckeringh", "Beckers", "Beckert", "Becket", "Beckett", "Beckford", "Beckham", "Becki", "Becking", "Beckley", "Beckman", "Beckmann", "Becquet", "Becskereky", "Becsky", "Becze", "Bedcsula", "Beddeu", "Beddie", "Bede", "Bedecoridius", "Bedekovich", "Beder", "Bedert", "Bednarska", "Bedredine", "Cialdella", "Ciambor", "Ciampa", "Ciampaglio", "Ciancio", "Ciano", "Cibelli", "Cicek", "Cicero", "Ciceu", "Cicco", "Ciccolella", "Ciccolini", "Ciccone", "Cicretto", "Cielo", "Cierra", "Djalili", "Djaout", "Effah", "Effendi", "Effeny", "Effting", "Efimov", "Fraanje", "Fraiquin", "Fran", "Francene", "Frances", "Francesca", "Franceschini", "Franchesca", "Franchini", "Francina", "Franciny", "Francis", "Francisca", "Francisco", "Francissen", "Franck", "Francke", "Francken", "Franco", "Francoei", "Francois", "Francq", "Francuza", "Francyne", "Frandsen", "Franek", "Frank", "Franka", "Franke", "Frankel", "Franken", "Frankena", "Frankenweiter", "Franklin", "Franklyn", "Franks", "Frans", "Franse", "Fransen", "Fransisca", "Franson", "Fransse", "Franssen", "Franssens", "Fransz", "Frant", "Franti", "Frantjoek", "Frantz", "Frantzen", "Franz", "Franzen", "Gu", "Guajardo", "Gualda", "Guang", "Guaraci", "Guardado", "Guardiola", "Guardo", "Guarino", "Gubbels", "Gubbiotti", "Guber", "Gucci", "Gudbrandsen", "Gudde", "Gude", "Gudelj", "Gudiksen", "Gudmundsen", "Gudmundson", "Gudmundsson", "Gudnason", "Gudz", "Guei", "Guengerung", "Guenter", "Guenther", "Guenna", "Gueria", "Guerin", "Guerra", "Guerrero", "Guertin", "Guest", "Gueta", "Guetta", "Guevara", "Guevera", "Guffens", "Guglielmo", "Gugliuzza", "Guha", "Guichard", "Guida", "Guidice", "Guidry", "Guijt", "Guikema", "Hwang", "Hyakuzuka", "Hyams", "Hyatt", "Hyautake", "Hybels", "Ian", "Iannetta", "Iannuzzi", "Jeletich", "Jelgersma", "Jelinek", "Jelinger", "Jellema", "Jelles", "Jellesma", "Jellinek", "Jelletich", "Jelsma", "Jemima", "Jemison", "Jemmerson", "Jena", "Jene", "Jenell", "Jenelle", "Jenet", "Jeneva", "Jeni", "Jenice", "Jenifer", "Jeniffer", "Jeninga", "Jenise", "Jenkin", "Jenkins", "Jenkinson", "Jenn", "Jenna", "Jennefer", "Jennekens", "Jennell", "Jenner", "Jenni", "Jennine", "Jenning", "Jennings", "Khamanei", "Khamatova", "Khamphian", "Khan", "Khandari", "Khandelwai", "Khanzhonkov", "Kharat", "Kharzeev", "Khatami", "Khataukar", "Khatchaturian", "Khattak", "Khattar", "Khattiya", "Khatib", "Khawaya", "Khayame", "Khayane", "Khedoe", "Khettabi", "Khimani", "Khoedri", "Khoi", "Khoja", "Khomasuridze", "Khomeini", "Khouri", "Khoury", "Lie", "Liebe", "Lieben", "Lieber", "Liebergot", "Lieberman", "Liebermann", "Lieberwirth", "Liebeskind", "Liebgott", "Liebrand", "Liebreich", "Liebrecht", "Liebrechts", "Liebregts", "Liefmans", "Liekens", "Lieman", "Lienen", "Lienicke", "Liens", "Leipelt", "Liepertz", "Lieselotte", "Lieshout", "Lieske", "Lieu", "Lieuwes", "Lieuwma", "Lieuwsma", "Lievaart", "Lievanos", "Lieven", "Lievens", "Lievense", "Lieverdink", "Lievers", "Lievre", "Liewes", "Liezenga", "Lifeng", "Ligeti", "Light", "Lighthart", "Ligthart", "Lightfoot", "Lihala", "Lijbaart", "Lijbrandt", "Lijesen", "Lijmbach", "Lijne", "Lijnse", "Lijphart", "Lijsen", "Lijsten", "Likora", "Lilas", "Lilau", "Lilian", "Lilien", "Lilja", "Lill", "Lilley", "Lilly", "Lilygreen", "Lim", "Lima", "Liman", "Limberti", "Limmen", "Limo", "Limos", "Limpers", "Mikael", "Mikel", "Mikelsons", "Mikhail", "Mikhailopoulos", "Mikhailovic", "Mikhailovich", "Mikhalkov", "Mikhout", "Mikkelsen", "Mikkers", "Mikkola", "Miklas", "Miklasen", "Miklassen", "Miklasson", "Mikolai", "Mikolajczyk", "Mikols", "Mikov", "Mikova", "Mikroutsikos", "Mikroutsikou", "Mikulic", "Milan", "Milankovic", "Milankovitch", "Milano", "Milanovic", "Milashevsky", "Milasinovic", "Milat", "Milbacher", "Milbert", "Milbrand", "Milburn", "Milchan", "Mildo", "Miles", "Miletiu", "Mileva", "Milevic", "Milic", "Milien", "Milinar", "Milinkevitsj", "Milito", "Milius", "Milke", "Milkowski", "Milkwood", "Mill", "Milla", "Millais", "Millan", "Millar", "Millard", "Millarson", "Millela", "Miller", "Millet", "Millette", "Milliband", "Milligan", "Milliken", "Millin", "Million", "Millman", "Mills", "Nunez", "Nuninga", "Olbers", "Olbracht", "Olcay", "Olcott", "Olczyk", "Oldebeuving", "Oldegbers", "Olde Husink", "Oldenbeuving", "Olders", "Oldersma", "O'Leary", "Olff", "Olgers", "Olham", "Olijve", "Olink", "Oliphant", "Olivares", "Olive", "Oliveira", "Oliveiras", "Oliveiro", "Oliver", "Oliveros", "Olivier", "Olivijn", "Olivjeras", "Ollevier", "O'Looney", "Papapetrou", "Paparazzo", "Papas", "Papastamatiou", "Papathanassiou", "Papathanassioupoulos", "Papathemeli", "Papathemelis", "Papavasileiou", "Papavassiliou", "Papavasillou", "Papazian", "Pape", "Papillon", "Papineau", "Paping", "Papma", "Papovic", "Papp", "Pappano", "Pappas", "Paquay", "Paquet", "Parfanov", "Paquette", "Paquin", "Paralkar", "Paramasivan", "Paraskeva", "Paraskevadi", "Paraskevadis", "Paraskevaki", "Paraskevakis", "Paraskevas", "Paraskevatos", "Paraskevatou", "Paraskevidi", "Paraskevidis", "Paraskevopoulos", "Paraskevoupoulou", "Parata", "Paratene", "Parcel", "Parcells", "Pardo", "Pardon", "Paredes", "Paredes Olay", "Parel", "Parensov", "Parenti", "Parentucelli", "Parete", "Pareto", "Parham", "Paridon", "Parhar", "Parikh", "Parini", "Parinussa", "Paris", "Parisius", "Park", "Parke", "Parker", "Parkes", "Parkin", "Parkinson", "Parks", "Parla", "Parmegiani", "Parnell", "Parnes", "Parnis", "Parnoutsoukian", "Paronelli", "Parr", "Parra", "Parras", "Parreira", "Parris", "Parrish", "Parry", "Parsell", "Parsipur", "Parson", "Parsons", "Partakusuma", "Parton", "Parvani", "Parvanov", "Parveen", "Parvez", "Parvin", "Paquay", "Quarrie", "Quartz", "Quast", "Quatani", "Queck", "Quedens", "Queen", "Queenie", "Queiroz", "Queloz", "Quenet", "Quentin", "Querashi", "Quest", "Queyras", "Queysen", "Rigas", "Rigby", "Rigg", "Riggs", "Righard", "Righini", "Rigopoulos", "Rigopoulou", "Rigores", "Rigter", "Rigterink", "Rigters", "Riina", "Riis", "Riise", "Rijcken", "Rijenga", "Rijff", "Rijghard", "Rijks", "Rijksen", "Rijnaard", "Rijkaart", "Rijke", "Rijkeboer", "Rijken", "Rijkens", "Rijker", "Rijkers", "Rijkink", "Rijkmans", "Rijks", "Rijksen", "Rijme", "Rijna", "Rijnaard", "Rijnboutt", "Rijnders", "Rijneke", "Rijnsen", "Rijnsent", "Rijntjes", "Rijper", "Rijpkema", "Rijpma", "Rijs", "Rijven", "Rijxman", "Rikken", "Rikkers", "Riksen", "Rikze", "Riley", "Rimall", "Rimbaud", "Rimbault", "Rimersma", "Rimes", "Rimsky", "Rimsky Korsakov", "Rinaldi", "Rinck", "Rind", "Rindertsma", "Rindler", "Rindt", "Rinehart", "Ringelberg", "Ringeling", "Ringers", "Ringertz", "Ringrow", "Ringnalda", "Rings", "Rink", "Rinkema", "Rinks", "Rinnen", "Rinopoulos", "Rinopoulou", "Rinpoche", "Rinsma", "Rinsema", "Rinzema", "Rios", "Spiridonov", "Spirinov", "Spiro", "Spit", "Spiteri", "Spits", "Spitzer", "Spivey", "Spizziri", "Spizzo", "Spock", "Spoel", "Spoelders", "Spoelstra", "Spong", "Spook", "Spoor", "Spooren", "Spottag", "Spotz", "Sprague", "Sprang", "Sprangers", "Sprengen", "Sprenger", "Sprenkels", "Sprewell", "Spring", "Springer", "Springfield", "Spruyt", "Spuensens", "Spyri", "Tijdink", "Tijdsma", "Tijmes", "Tijmstra", "Tijnman", "Tijsinger", "Tijsseling", "Tijssen", "Tijssens", "Tijsterman", "Tijtgat", "Tijuana", "Tika", "Tikema", "Tiktak", "Til", "Tilbert", "Tilden", "Tilia", "Tiljaninko", "Tilghman", "Tilke", "Till", "Tillema", "Tilley", "Tilli", "Tillie", "Tilling", "Tillman", "Tilly", "Tilmans", "Tilmant", "Tilse", "Tilstra", "Tilz", "Timberlake", "Timina", "Timisela", "Timane", "Timm", "Timman", "Timmer", "Timmons", "Timofeef", "Timosjenko", "Timostsjoek", "Timothy", "Timp", "Wyn", "Wyndy", "Wynema", "Wynette", "Wynia", "Wynn", "Wynne", 
};
		int index = rand.nextInt( names.length );
		String last_name = names[index];
		
		//System.out.println( last_name );
		return last_name;
	}		

	/* NOT USED*/
	/**
	 * Returns one of the sources for person info, from the values given in the "last_name" field of the MySQL DB
	 * @return Source of person info ("btap", "CancerReg", etc.)
	 */
	private String gen_person_source() { // Generate fake data for field *called* "last_name" in Persons table 
		String[] sources = { null, "btap", "btap-CNS", "Calgary", "Calgary-btap", "Calgary-cc", "CancerReg", "CC", "CNS-cc", "CNS-cc-calgary", };
		int index = rand.nextInt( sources.length );
		String source = sources[index] ;	
		//System.out.println(source);
		return source;
	}
	
	/*** formdata_cns_clinical_trials ***/
	
	/* format_data_id - postgres to assign */
	/* form_id - use gen_random_int(3) */
	/* pid - postgres to assign */

	/* study_number */
	/**
	 * Returns a fake study number.
	 * @return A fake study number
	 */
	private String gen_clinicalTrials_study_number() {
		String[] study_numbers = { null, "Eth 95-95-95", "Eth 12345", "Eth 95-25-25", "Eth 16666 / 98-98-98", "Eth 95-12-12", "Eth 23456", "Eth 12345 / 98-77-77", "Eth 25432", "Eth 12345 / 95-25-25", "Eth 15432 / 95-23-23", "Eth 95-59-25", "Eth 98-52-25", "Eth 97-25-25", "Eth 01-01-01 / 15432", "Eth 12345 / 98-62-62", "Eth 22222", "Eth 012345", "Eth 19876", "Eth 17890 / 03-22-22", "Eth 21098", "Eth 25555", "P01224", "DX-FAL-001", "Eth 21789", "Eth 22333", "Eth 23555", "Eth 26745", "Eth 23098", };
		int index = rand.nextInt( study_numbers.length );
		String study_number = study_numbers[index] ;	
		//System.out.println(study_number);
		return study_number;		
	}
	
	/* study_name */
	/**
	 * Returns a bogus study name.
	 * @return A bogus study name
	 */
	private String gen_clinicalTrials_study_name() {
		String[] study_names = { null, "some very long description of a study", "my study name", "another study", "YASN" };
		int index = rand.nextInt( study_names.length );
		String study_name = study_names[index] ;
		//System.out.println(study_number);
		return study_name;		
	}
	
	/* user_create - use gen_user() */
	/* formdata_create_time - use gen_timestamp() */
	/* modify_user - use gen_user() */
	/* modify_time use gen_timestamp() */
	
	/* study_type */
	/**
	 * Returns one of the possible Clinical Trial types.
	 * @return A study type
	 * NOTE: In the legacy DB, this field was broken into a boolean field for each study type
	 */
	private String gen_clinicalTrials_study_type() {
		String[] studies = { null, "igar", "inhouse", "industry", "ncic", "rtog", "ocog", "CBCF", "ACB", };
		int index = rand.nextInt( studies.length );
		String study = studies[index] ;	
		//System.out.println(study);
		return study;		
	}
	
	/*** formdata_cns_diagnosis_bx ***/
	
	/* formdata_id - postgres to assign */
	/* pid - postgres to assign */
	/* surgery_bx - use gen_gen_boolean_or_null() */
	/* date_bx - use gen_date() */
	/* surgical_facility - use gen_surgical_facility() */
	/* extent_of_resection - use gen_extent_of_resection() */
	/* prelim_diag - use gen_diag() */
	/* prelim_diag_icd - use gen_random_int_or_null(5) */
	/* prelim_diag_grade - use gen_diag_grade() */
	/* date_prelim_diag_path_reported - use gen_date() */
	/* final_diag - use gen_diag() */
	/* final_diag_icd - use gen_random_int_or_null(5) */
	/* final_diag_grade - use gen_diag_grade() */
	/* date_final_diag_path_reported - use gen_date() */
	/* p_loss_of_heterozyg - use gen_boolean_or_null() */
	/* q_loss_of_heterozyg - use gen_boolean_or_null() */
	/* mgmt - use gen_boolean_or_null() */
	/* diag_path_consult - use gen_diag() */
	/* diag_path_consult_icd - use gen_random_int_or_null(5) */
	/* diag_path_consult_grade - use gen_diag_grade() */
	/* user_create - use gen_user() */
	/* formdata_create_time - use gen_timestamp() */
	/* modify_user - use gen_user() */
	/* modify_time - use gen_timestamp() */
	
	/*** formdata_cns_history ***/
	
	/* formdata_id - postgres to assign */
	/* pid - postgres to assign */
	
	/* past_diagnoses */
	/** Returns a fake history of past diagnoses.
	 * @return A fake history of past diagnoses
	 */
	private String gen_cnsHistory_past_diagnoses() {
		int chance = rand.nextInt( 10 );
		if ( chance < 5 ) { // Let's say that about half of people have no past diagnoses
			//System.out.println( "null" );
			return null;
		}
		else {
			String[] diagnoses_values = { null, "tonsillectomy", "hypertension", "migraines", "Diabetes", "meningioma", "Alzheimer's" };
			int index = rand.nextInt( diagnoses_values.length );
			String diagnoses = diagnoses_values[index] ;
			//System.out.println(diagnoses);
			return diagnoses;
		}
	}
	
	/* allergies */
	/** Returns fake allergies.
	 * @return Fake allergies
	 */
	private String gen_cnsHistory_allergies() {
		int chance = rand.nextInt( 10 );
		if ( chance < 7 ) { // Let's say that about 70% of people have no known allergies
			//System.out.println( "null" );
			return null;
		}
		else {
			String[] allergies_values = { null, "Penicillin", "Sulfa drugs", "peanuts", "cats", "Dilantin" };
			int index = rand.nextInt( allergies_values.length );
			String allergies = allergies_values[index] ;
			//System.out.println(allergies);
			return allergies;		
		}
	}
	
	/* formdata_create_time - use gen_timestamp() */
	/* modify_user - use gen_user() */
	/* modify_time - use gen_timestamp() */
	
	/*** formdata_cns_identification ***/
	
	/* formdata_id - postgres to assign */
	/* pid - postgres to assign */
	/* death_date - use gen_date() */
	/* patient_height - use gen_random_int_or_null(3) (field actually expects double) */
	/* patient_weight - use gen_random_int_or_null(3) (field actually expects double) */
	
	/* handedness */
	/** Returns a fake patient handedness.
	 * @return A fake patient handedness
	 * NOTE: Not all values are simply "L" or "R", there are a few oddballs
	 */
	private String gen_cnsIdentification_handedness() {
		String[] handedness_values = { null, "Unk", "R", "L", "L -> R" };
		int index = rand.nextInt( handedness_values.length );
		String handedness = handedness_values[index] ;
		//System.out.println(handedness);
		return handedness;		
	}

	/* user_create - use gen_user() */
	/* formdata_create_time - use gen_timestamp() */
	/* modify_user - use gen_user() */
	/* modify_time - use gen_timestamp() */
	
	
	/*** Misc Usage ***/
	
	/**
	 * Returns a fake payment method.  Meant to be used in multiple tables.
	 * @return A payment method as a string.  Either Prov or Priv randomly.
	 */
	private String gen_payment() {
		String [] therapy_payments = { null, "Prov", "Priv" };
		int index = rand.nextInt( therapy_payments.length );
		String therapy_payment = therapy_payments[index];
		return therapy_payment;
	}
	
	/*** formdata_cns_systemic_therapy ***/

	/**
	 * Returns a fake Therapy Regiment.
	 * @return A fake therapy regiment.  To be used in formdata_cns_systemic_therapy
	 */
	private String gen_systemicTherapy_sys_therapy_regimen() {
		String [] therapy_regiments = { null, "TMZ", "CCNU", "TMZ; CCNU; Etoposide", "BCNU", "TMZ; CCNU", "TMZ; CCNU; VP16", "TMZ; CCNU; VP_16", "TMZ; BCNU", "Lomustine (CCNU)", "TMZ; Etoposide", "Lomustine, Etoposide", "CCNU/Etoposide", "CCNU; Etoposide", "Unk", "TMZ; (5/28)" };
		int index = rand.nextInt( therapy_regiments.length );
		String therapy_regimen = therapy_regiments[index];
		//System.out.println(therapy_regimen);
		return therapy_regimen;
	}
	
	/**
	 * Returns a fake number of cycles with some realistic unclean values.
	 * @return Number of therapy cycles, with unclean values.  To be used in formdata_cns_systemic_therapy
	 */
	private String gen_systemicTherapy_sys_therapy_nr_cycles() {
		String [] nr_cycles = { null, "0", "1", "2", "3", "4", "5", "6", "7", "8", "8 out of 12", "10", "12" };
		int index = rand.nextInt( nr_cycles.length );
		String nr_cycle = nr_cycles[index];
		return nr_cycle;
	}
	
	/**
	 * Returns a number representing total dose from therapy, including unclean values.
	 * @return Number representing total dose, including unclean values.  To be used in formdata_cns_systemic_therapy
	 */
	private String gen_systemicTherapy_sys_therapy_total_dose() {
		String [] total_doses = { null, "1920", "1200", "1690", "4200", "3300", "130 mg per day", "75mg/m2/day(concurrent)", "4725", "75 mg/m2/day(adjuvant)" };
		int index = rand.nextInt( total_doses.length );
		String total_dose = total_doses[index];
		return total_dose;
	}
	
	/*** formdata_cns_scans ***/

	/**
	 *  Returns a fake initial scan.  To be used in formdata_cns_scans
	 *  @return Fake initial scan string, including unclean values.
	 */
	private String gen_cnsScans_initial_scan() {
		String [] initial_scans = { null, "MR", "CT", "?", "Ultrasound" };
		int index = rand.nextInt( initial_scans.length );
		String initial_scan = initial_scans[index];
		return initial_scan;
	}
	
	/**
	 * Returns a fake scanning facility.  To be used in formdata_cns_scans
	 * @return Fake scanning facility string, including unclean values.
	 */
	private String gen_cnsScans_scanning_facility() {
		String [] scanning_facilities = { null, "RAH", "UAH", "GNH", "Other", "CCI", "MIS", "SCH" };
		int index = rand.nextInt( scanning_facilities.length );
		String scanning_facility = scanning_facilities[index];
		return scanning_facility;
	}
	
	/**
	 * Returns Fake size tumour on scan.  To be used in formdata_cns_scans
	 * @return Fake size tumour on scan real, including unclean values.
	 */
	private Float gen_cnsScans_size_tumour_on_scan() {
		return rand.nextInt(10) + rand.nextFloat();
	}
	
	/**
	 * Returns fake loc comments.  To be used in formdata_cns_scans
	 * @return Fake loc comments, including nulls.
	 */
	private String gen_cnsScans_loc_comments() {
		String [] loc_comments = { null, "Ventricle, central", "Overlapping lesion of brain, left", "Overlapping lesion of brain, right", "Parietoccipital subcortex on the right", "Left hemisphere extend into the body of corpus callosum", "Right temporal and parietal lobes", "Brain, right", "Brain, central", "Brain, left", "Right frontal lobe", "Left frontal lobe", "Cerebrum left", "Cerebellum, left", "Cerebellum, right", "Cerebrum right", "Deep left frontal parietal periventricular white matter", "Left occipital-temporal region; left temporal convexity" };
		int index = rand.nextInt( loc_comments.length );
		String loc_comment = loc_comments[index];
		return loc_comment;
	}
	
	/**
	 * Returns fake enhancement text as string.  To be used in formdata_cns_scans
	 * @return Fake enhancement text as string.
	 */
	private String gen_cnsScans_enhancement() {
		String [] enhancements = { null, "Yes", "No", "Unk", "Yes (left temporal); No (posterior frontal lobe)" };
		int index = rand.nextInt( enhancements.length );
		String enhancement = enhancements[index];
		return enhancement;
	}
	
	/*** formdata_cns_progression ***/
	
	/**
	 * Returns fake prog type.
	 * @return Fake prog type as String.  To be used in formdata_cns_progression
	 */
	private String gen_cnsProgression_prog_type() {
		String [] prog_types = { null, "Growth", "Growth/Transformation", "progformation" };
		int index = rand.nextInt( prog_types.length );
		String prog_type = prog_types[index];
		return prog_type;
	}
	
	/**
	 * Returns fake scanning facility.
	 * @return Fake scanning facility as String.  To be used in formdata_cns_progression
	 */
	private String gen_cnsProgression_prog_scanning_facility() {
		String [] prog_scanning_facilities = { null, "CCI", "MIS", "UAH", "Other", "RAH", "Unk", "Other - Queen Elizabeth II Hospital", "Other - PQEA", "SCH", "Other - Elizabeth II Hospital" };
		int index = rand.nextInt( prog_scanning_facilities.length );
		String prog_scanning_facility = prog_scanning_facilities[index];
		return prog_scanning_facility;
	}
	
	/* formdata_cns_progression: extent_of_resection - use gen_extent_of_resection() */
	/* formdata_cns_progression: final_diag_icd - use gen_random_int_or_null(5) */

	
	/**
	 * Returns fake final diagnosis.
	 * @return Fake final diagnosis as String.  To be used in formdata_cns_progression
	 */
	private String gen_cnsProgression_prog_final_diag() {
		String [] prog_final_diags = { null, "Glioblastoma, NOS", "Oligodendroglioma, anaplastic",   };
		int index = rand.nextInt( prog_final_diags.length );
		String prog_final_diag = prog_final_diags[index];
		return prog_final_diag;
	}
	
	/*** formdata_cns_presentation ***/
	
	/**
	 * Returns fake ecog value. Can be null.
	 * @return Fake ecog value, as float; or null.  To be used in formdata_cns_presentation.
	 */
	private Float gen_cnsPresentation_ecog() {
		String [] ecogs = { null, "0", "2", "1", "3", "2.5", "4", "3.5" };
		int index = rand.nextInt( ecogs.length );
		String ecog = ecogs[index];
		if ( ecog != null) {
			return Float.parseFloat(ecog);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns fake other symptoms.
	 * @return Fake other symptoms, as String.  To be used in formdata_cns_presentation
	 */
	private String gen_cnsPresentation_other_symptoms() {
		String [] other_symptoms_values = { null, "Lumbar puncture", "Lower limbs discomfort", "Reduced conecntration and apetite", "Decreased mental function", "Decreased level of conciousness; decreased apetite", "Progressive ataxia", "Urination problem for 3 weeks(incontinence)", "Intermittent incontinence of urine", "Mild fatigue post op", "Stroke-like symptoms", "Decrease in appetite", "Abnormal taste in mouth", "Syncope", "Excessive sleepyness" };
		int index = rand.nextInt( other_symptoms_values.length );
		String other_symptoms = other_symptoms_values[index];
		return other_symptoms;
	}
	
	/*** formdata_cns_medications ***/
	
	/**
	 * Returns fake decadron current dose.
	 * @return Fake decadron current dose, as String.  To be used in formdata_cns_medications
	 */
	private String gen_cnsMedications_decadron_current_dose() {
		String [] current_dose_values = { null, "4mg tid", "4mg po bid", "4 mg qid", "3 mg po tid", "10 mg po", "taper 1 mg tid", "2 mg qid", "3 mg po", "0.5 mg tid", "2 mg po tid", "2 mg po tid"  };
		int index = rand.nextInt( current_dose_values.length );
		String current_dose = current_dose_values[index];
		return current_dose;
	}
	
	/**
	 * Returns fake dilantin current dose.
	 * @return Fake dilantin current dose, as String.  To be used in formdata_cns_medications
	 */
	private String gen_cnsMedications_dilantin_current_dose() {
		String [] current_dose_values = { null, "100 mg tid", "600 mg", "100 mg po tid", "100 mg qid", "300 mg 1 daily", "200 mg po bid", "300 mg", "100 mg po bid", "2mg BID", "200 mg BID" };
		int index = rand.nextInt( current_dose_values.length );
		String current_dose = current_dose_values[index];
		return current_dose;
	}
	
	/**
	 * Returns fake other medication.
	 * @return Fake other medication, as String.  To be used in formdata_cns_medications
	 */
	private String gen_cnsMedications_other_medication() {
		String [] other_medications = { null, "Carbamazepine", "Ranitidine", "synthroid", "novo-lorazepam", "Metoprolol 50 mg po bid", "Tegretol 400 mg po tid", "Zantac", "Lipitor", "T3", "Ativan", "Percocet", "Flomax 04 mg" };
		int index = rand.nextInt( other_medications.length );
		String other_med = other_medications[index];
		return other_med;
	}
	
	/*** formdata_cns_initial_treatment ***/
	
	/**
	 * Returns fake initial treatment rt.
	 * @return Fake initial treatment rt, as String.  To be used in formdata_cns_initial_treatment
	 */
	private String gen_cnsInitialTreatment_initial_treat_rt() {
		String [] treat_rt_values = { null, "Yes", "No", "Yes (did not finish)" };
		int index = rand.nextInt( treat_rt_values.length );
		String treat_rt = treat_rt_values[index];
		return treat_rt;
	}
	
	/**
	 * Returns fake initial treatment fraction nr.
	 * @return Fake initial treatment fraction nr, as String.  To be used in formdata_cns_initial_treatment
	 */
	private String gen_cnsInitialTreatment_initial_treat_fraction_nr() {
		String [] fraction_nr_values = { null, "25(planned 30", "15 (1)", "2; 30", "23; 14", "8 (15)" };
		int index = rand.nextInt( fraction_nr_values.length );
		String fraction_nr = fraction_nr_values[index];
		if(rand.nextInt(2) == 0) {
			return Integer.toString(rand.nextInt(35));
		} else {
			return fraction_nr;
		}
	}
	
	/**
	 * Returns fake initial treatment rt total dose.
	 * @return Fake initial treatment rt total dose, as String.  To be used in formdata_cns_initial_treatment
	 */
	private String gen_cnsInitialTreatment_initial_treat_rt_total_dose() {
		String [] total_dose_values = { null, "880 RBE", "5400", "6000", "5000 (planned 6000)", "4000 (2667 cGy)", "400; 5400", "4600;2800", "21336(4000)", "520", "7800", "3386", "1400" };
		int index = rand.nextInt( total_dose_values.length );
		String total_dose = total_dose_values[index];
		return total_dose;
	}
	
	/**
	 * Returns fake initial treatment rt type.
	 * @return Fake initial treatment rt type, as String.  To be used in formdata_cns_initial_treatment
	 */
	private String gen_cnsInitialTreatment_initial_treat_rt_type() {
		String [] rt_type_values = { null, "Linac Conformal", "TOMO", "Linac IMRT", "Linac IMRT; Linac Conformal", "LINoPAC IMRT" };
		int index = rand.nextInt( rt_type_values.length );
		String rt_type = rt_type_values[index];
		return rt_type;
	}
	
	/**
	 * Returns fake name of other drug.
	 * @return Fake name of other drug, as String.  To be used in formdata_cns_initial_treatment
	 */
	private String gen_cnsInitialTreatment_initial_treat_name_other_drug() {
		String [] other_drugs = { null, "CCNU", "CCNU/Etoposide", "BCNU", "BCNU/VP16", "CCNU/VP_16", "CCNU/etuposide", "Etopside/CCNU", "Etoposide" };
		int index = rand.nextInt( other_drugs.length );
		String other_drug = other_drugs[index];
		return other_drug;
	}
	
	/**
	 * Returns fake initial treatment chemo total dose.
	 * @return Fake initial treatment chemo total dose, as String.  To be used in formdata_cns_initial_treatment
	 */
	private String gen_cnsInitialTreatment_initial_treat_total_dose() {
		String [] total_dose_values = { null, "(concurrent)", "75 mg/m2/day", "130 mg per day", "75 mg/m2/day (adjuvant)" };
		int index = rand.nextInt( total_dose_values.length );
		String total_dose = total_dose_values[index];
		if(rand.nextInt(2) == 0) {
			return Integer.toString(rand.nextInt(10)) + Integer.toString(rand.nextInt(10)) + Integer.toString(rand.nextInt(10)) + Integer.toString(rand.nextInt(10));
		} else {
			return total_dose;
		}
	}
	
	/*** btap_study ***/
	
	/**
	 * Returns fake institution_name
	 * @return Fake institution_name as String.  To be used in table btap_study
	 */
	private String gen_btapStudy_institution_name() {
		String [] institutions = { "University of Alberta Hospital", "Cross Cancer", "CHR-FOOTHILLS MEDICAL CENTRE", "Unknown-instituion", "Royal Alexandra Hospital", "MISERICORDIA", "Western Canada MRI - Vancouver", "Medical Imaging Consultants", "MEADOWLARK MRI", "Banner Baywood Medical Center", "Queen Elizabeth II Hospital", "MIC-CENTURY PARK", "U.A.H", "GREY NUNS COMMUNITY HOSPITAL", "LGH", "Victoria General Hospital" };
		int index = rand.nextInt( institutions.length );
		String institution = institutions[index];
		return institution;
	}
	
	/**
	 * Return fake station name.
	 * @return Fake station name as String.  To be used in table btap_study
	 */
	private String gen_btapStudy_station_name() {
		String [] station_names = { "MRC14064", "NTSCAN", "MR01OW", "INTERA15", "Unknown_station", "mri3tcbiar", "PMSN-EHEFJ17H9D", "PHILIPS-7EADFA8", "MRC14095", "MRC21112", "MRC21130", "fmcmri02", "MRS2OW", "MRC22769", "MRI_3T_CBIAR", "IMIMDLMR1" };
		int index = rand.nextInt( station_names.length );
		String station_name = station_names[index];
		return station_name;
	}
	
	/*** btap_imagetype ***/
	
	/**
	 * Return fake type description.
	 * @return Fake type description as String.  To be used in table btap_imagetype
	 */
	private String gen_btapImageType_type_description() {
		String [] type_descriptions = { "T1C", "FLAIR", "T1", "T2" };
		int index = rand.nextInt( type_descriptions.length );
		String type_description = type_descriptions[index];
		return type_description;
	}
	
	
	/**
	 * Return a fake comment on image.
	 * @return A fake comment on an image, return type is a String.  To be used in table btap_imagetype
	 */
	private String gen_btapImageType_comment() {
		String tmp = "Series=";
		switch(rand.nextInt(9)) {
			case 0: tmp = tmp+"Fast Brain"; break;
			case 1: tmp = tmp+"T1"; break;
			case 2: tmp = tmp+"AX T1"; break;
			case 3: tmp = tmp+"AX FLAIR"; break;
			case 4: tmp = tmp+"CONF"; break;
			case 5: tmp = tmp+"TT2"; break;
			case 6: tmp = tmp+"FLAIR"; break;
			case 7: tmp = tmp+"Brain"; break;
			case 8: tmp = tmp+"Eth21656"; break;
		}
		tmp = tmp+"~~";
		if(rand.nextInt(5) == 0) {
			tmp = tmp+"EchoTime=-1";
		} else {
			tmp = tmp+"EchoTime="+rand.nextInt(9000);
		}
		tmp = tmp+"~~";
		if(rand.nextInt(2) == 0) {
			tmp = tmp+"InversionTime=-1";
		} else {
			tmp = tmp+"InversionTime="+(2000+rand.nextInt(100));
		}
		tmp = tmp+"ReceivingCoil=field_not_found";
		return tmp;
	}
	
	/*** btap_image ***/
		/* no new methods required */
	
	/*** btap_rois ***/

	/* roi_id */
	/**
	 * Returns fake ROI id (where 5 = tumour, 6 = edema).
	 * @return Fake ROI id (where 5 = tumour, 6 = edema)
	 */
	private int gen_btapRois_roi_id() {
		int[] ids = { 5, 6 };
		int index = rand.nextInt( ids.length );
		int id = ids[index];
		return id;
	}
	
	/**
	 * Returns fake ROI description.
	 * @return Fake ROI description
	 */
	private String gen_btapRois_roi_description() {
		String [] descriptions = { "Edema", "Tumour" };
		int index = rand.nextInt( descriptions.length );
		String description = descriptions[index];
		return description;
	}

	/*** btap_bounding_box ***/
		/* no new methods required */
	
	/*** btap_roi_centerofmass ***/
	/* roi_agg_id - postgres to assign */
	/* study_id - postgres to assign */
	
	/* description */
	/**
	 * Returns fake ROI Aggregate Info description.
	 * @return Fake ROI Aggregate Info description
	 */
	private String gen_btapRoisAggregateInfo_description() {
		String[] descriptions = { "CenterOfMass" };
		int index = rand.nextInt( descriptions.length );
		String description = descriptions[index];
		return description;
	}
	
	/* value */
	/**
	 * Returns fake ROI Aggregate Info x/y/z-values as String.
	 * @return Fake ROI Aggregate Info x/y/z-values as String
	 */
	private String gen_btapRoisAggregateInfo_value() {
		// In reality, these do not all have to be two-digit
		String xyz_string = "CMx=" + rand.nextInt(201) + "." + gen_random_int(2) + ";" +
							"CMy=" + rand.nextInt(201) + "." + gen_random_int(2) + ";" +
							"CMz=" + rand.nextInt(21) + "." + gen_random_int(2);		
		return xyz_string;
	}
	
	/*** btap_roi_voxelset ***/
		/* no new methods required */
	
	/*** btap_roi_voxelset_unreg ***/
		/* no new methods required */
}
