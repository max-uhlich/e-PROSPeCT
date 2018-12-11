package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import tools.admin.AdminConnector;

public class BtawFullImageImport {

	private Connection conn = null;

	public BtawFullImageImport() {
		// TODO: WHAT?
	}

	public static void main(String[] args) {
		Console cons = System.console();
		
		String rootPath = new String(cons.readLine("%s\n?", "The program assumes a directory structure of $ROOT$/patient/study_date/modality/raw/Image###.png\n" +
				"Please specify $ROOT$: "));
		
		if(rootPath.length() == 0) {
			System.err.println("Error, $ROOT$ left as blank, for linux root specify '/' please.");
			System.exit(-1);
		}
		
		BtawFullImageImport bfii = new BtawFullImageImport();
		try {
			bfii.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}

		System.out.println("Connected to database.");
		try {
			bfii.insertImages(rootPath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done.");
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
		System.exit(-1);
	}
	
	public void insertImages(String root) throws SQLException, ParseException, IOException {
		String insertStudy = "INSERT INTO btaw.btap_study (study_id, pid, study_description, study_date, institution_name, station_name) VALUES (nextval('study_id_sqn'), ?, ?, ?, ?, ?)";
		String insertImageType = "INSERT INTO btaw.btap_imagetype (imagetype_id, study_id, type_description, series_description, echo_time, inversion_time, repetition_time) VALUES (nextval('imagetype_id_sqn'), ?, ?, ?, ?, ?, ?)";
		String insertImage = "INSERT INTO btaw.btap_image (image_id, imagetype_id, slicenr, link) VALUES (nextval('image_id_sqn'), ?, ?, ?)";
		String selectStudy =  "SELECT study_id FROM btaw.btap_study WHERE pid = ? AND study_date = ?";
		String selectImageType = "SELECT imagetype_id FROM btaw.btap_imagetype WHERE study_id = ? AND type_description = ? AND series_description = ? AND echo_time = ? AND inversion_time = ? AND repetition_time = ?";
		
		PreparedStatement insertImagePS = conn.prepareStatement(insertImage);
		PreparedStatement insertImageTypePS = conn.prepareStatement(insertImageType);
		PreparedStatement insertStudyPS = conn.prepareStatement(insertStudy);
		PreparedStatement selectStudyPS = conn.prepareStatement(selectStudy);
		PreparedStatement selectImageTypePS = conn.prepareStatement(selectImageType);
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		File rootFile = new File(root);
		File[] pidDirs = rootFile.listFiles();
		
		for(int i = 0; i < pidDirs.length; i++) {
			File[] studyDirs = pidDirs[i].listFiles();
			int pid = Integer.parseInt(pidDirs[i].getName().replaceAll("CR0*", ""));
			for(int j = 0; j < studyDirs.length; j++) {
				Date studyDate = null;
				try {
					studyDate = df.parse(studyDirs[j].getName());
				} catch (ParseException e) {
					System.err.println(e.getMessage());
					continue;
				}
				File[] modalityDirs = studyDirs[j].listFiles();
				for(int k = 0; k < modalityDirs.length; k++) {
					if(!modalityDirs[k].isDirectory())
						continue;
					String modality = modalityDirs[k].getName();
					if(!(modality.equals("FLAIR") || modality.equals("T1") || modality.equals("T1C") || modality.equals("T2")))
						continue;
					File[] dicomDir = modalityDirs[k].listFiles();
					File[] imageFiles = null;
					File dicomFile = null;
					
					System.err.println("DOING pid : " + pid + " studyDate : " + studyDate.toString() + " modality : " + modality);
					
					if(dicomDir.length != 2) {
						System.err.println("DICOM DIR GREATER THAN 2");
					}
					
					for(int k1 = 0; k1 < dicomDir.length; k1++) {
						if(dicomDir[k1].getName().equals("raw")) {
							imageFiles = dicomDir[k1].listFiles();
						} else if(dicomDir[k1].getName().equals("SavedDicomFields.txt")) {
							dicomFile = dicomDir[k1];
						}
					}
					
					if(imageFiles == null || dicomFile == null) {
						System.err.println("DICOM OR RAW DIR NULL imageFiles : " + imageFiles + " dicomFile : " + dicomFile);
						continue;
					}
					
					Scanner scanner = new Scanner(new FileReader(dicomFile));
					String seriesDescription = null;
					String institutionName = null;
					String stationName = null;
					int echoTime = -1;
					int repetitionTime = -1;
					int inversionTime = -1;
					
					
					while( scanner.hasNextLine() ) {
						String line = scanner.nextLine();
						String fieldName = line.split("=")[0].trim();
						String fieldValue = line.split("=")[1].trim();
						
						if(fieldName.equals("SeriesDescription")) {
							seriesDescription = new String(fieldValue);
						} else if(fieldName.equals("InstitutionName")) {
							institutionName = new String(fieldValue);
						} else if(fieldName.equals("StationName")) {
							stationName = new String(fieldValue);
						} else if(fieldName.equals("EchoTime")) {
							echoTime = (int)Math.round(Double.parseDouble(fieldValue));
						} else if(fieldName.equals("RepetitionTime")) {
							repetitionTime = (int)Math.round(Double.parseDouble(fieldValue));
						} else if(fieldName.equals("InversionTime")) {
							inversionTime = (int)Math.round(Double.parseDouble(fieldValue));
						}
					}
					
					scanner.close();
					
					if(seriesDescription == null || institutionName == null || stationName == null || echoTime == -1 || repetitionTime == -1 || inversionTime == -1) {
						System.err.println("DICOM FIELD MISSING VALUE");
					}
					
					int studyId = -1;
					
					selectStudyPS.setInt(1, pid);
					selectStudyPS.setDate(2, new java.sql.Date(studyDate.getTime()));
					ResultSet rsetStudy = selectStudyPS.executeQuery();
					
					if(!rsetStudy.next()) {
						System.err.println("COULDN'T FIND STUDY");
						
						insertStudyPS.setInt(1, pid);
						insertStudyPS.setString(2, "Unknown");
						insertStudyPS.setDate(3, new java.sql.Date(studyDate.getTime()));
						insertStudyPS.setString(4, institutionName);
						insertStudyPS.setString(5, stationName);
						
						insertStudyPS.executeUpdate();
						conn.commit();
						
						selectStudyPS.clearParameters();
						selectStudyPS.setInt(1, pid);
						selectStudyPS.setDate(2, new java.sql.Date(studyDate.getTime()));
						
						rsetStudy = selectStudyPS.executeQuery();
						
						if(rsetStudy.next()) {
							studyId = rsetStudy.getInt(1);
						} else {
							System.err.println("COULDN'T FIND STUDY_ID AFTER INSERT PID="+pid+" STUDY_DATE="+studyDate.toString());
						}
					} else {
						studyId = rsetStudy.getInt(1);
					}
					
					int imageTypeId = -1;
					
					selectImageTypePS.setInt(1, studyId);
					selectImageTypePS.setString(2, modality);
					selectImageTypePS.setString(3, seriesDescription);
					selectImageTypePS.setInt(4, echoTime);
					selectImageTypePS.setInt(5, inversionTime);
					selectImageTypePS.setInt(6, repetitionTime);
					ResultSet rsetImageType = selectImageTypePS.executeQuery();
					
					if(!rsetImageType.next()) {
						System.err.println("COULDN'T FIND IMAGETYPE");
						
						insertImageTypePS.setInt(1, studyId);
						insertImageTypePS.setString(2, modality);
						insertImageTypePS.setString(3, seriesDescription);
						insertImageTypePS.setInt(4, echoTime);
						insertImageTypePS.setInt(5, inversionTime);
						insertImageTypePS.setInt(6, repetitionTime);
						
						insertImageTypePS.executeUpdate();
						conn.commit();
						
						selectImageTypePS.clearParameters();
						selectImageTypePS.setInt(1, studyId);
						selectImageTypePS.setString(2, modality);
						selectImageTypePS.setString(3, seriesDescription);
						selectImageTypePS.setInt(4, echoTime);
						selectImageTypePS.setInt(5, inversionTime);
						selectImageTypePS.setInt(6, repetitionTime);
						
						rsetImageType = selectImageTypePS.executeQuery();
						
						if(rsetImageType.next()) {
							imageTypeId = rsetImageType.getInt(1);
						} else {
							System.err.println("COULDN'T FIND IMAGETYPE_ID AFTER INSERT PID="+pid+" STUDY_ID="+studyId);
						}
					} else {
						imageTypeId = rsetImageType.getInt(1);
					}
					
					for(int l = 0; l < imageFiles.length; l++) {
						int slicenr = -1;
						try {
							slicenr = Integer.parseInt(imageFiles[l].getName().replaceAll("Image", "").replaceAll(".png", ""));
						} catch (NumberFormatException e) {
							System.err.println(e.getMessage());
							continue;
						}
						
						insertImagePS.setInt(1, imageTypeId);
						insertImagePS.setInt(2, slicenr);
						insertImagePS.setString(3, imageFiles[l].getAbsolutePath());
						
						insertImagePS.executeUpdate();
						conn.commit();
						insertImagePS.clearParameters();
					}
					
					insertImageTypePS.clearParameters();
					insertStudyPS.clearParameters();
					selectStudyPS.clearParameters();
					selectImageTypePS.clearParameters();
				}
			}
		}
	}
}
