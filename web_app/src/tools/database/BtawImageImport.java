package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import tools.admin.AdminConnector;

public class BtawImageImport {

	private Connection conn = null;

	public BtawImageImport() {
		// TODO: WHAT?
	}

	public static void main(String[] args) {
		Console cons = System.console();
		
		String rootDrPath = new String(cons.readLine("%s\n?", "The program assumes a directory structure of $ROOT$/patient/study_date/modality/images.png\n" +
				"Please specify $ROOT_DR$: "));
		String rootCrPath = new String(cons.readLine("%s\n?", "The program assumes a directory structure of $ROOT$/patient/study_date/modality/images.png\n" +
				"Please specify $ROOT_CR$: "));
		
		if(rootDrPath.length() == 0 || rootCrPath.length() == 0) {
			System.err.println("Error, one of the $ROOT$ left as blank, for linux root specify '/' please.");
			System.exit(-1);
		}
		
		BtawImageImport bii = new BtawImageImport();
		try {
			bii.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
		String insertImagesStrDR = "INSERT INTO btaw.img_reg_dr (img_reg_id, pid, study_date, image_modality, slicenr, link) VALUES(nextval('btaw_img_reg_sqn_dr'), ?, ?, ?, ?, ?)";
		String insertImagesStrCR = "INSERT INTO btaw.img_reg_cr (img_reg_id, pid, study_date, image_modality, slicenr, link) VALUES(nextval('btaw_img_reg_sqn_cr'), ?, ?, ?, ?, ?)";

		System.out.println("Connected to database.");
		try {
			bii.insertImages(rootDrPath, insertImagesStrDR);
			bii.insertImages(rootCrPath, insertImagesStrCR);
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
	
	public void insertImages(String root, String insStmt) throws SQLException, ParseException, IOException {
		String insertImagesStr = insStmt;
		PreparedStatement insertImagePS = conn.prepareStatement(insertImagesStr);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		File rootFile = new File(root);
		File[] pidDirs = rootFile.listFiles();
		
		for(int i = 0; i < pidDirs.length; i++) {
			File[] studyDirs = pidDirs[i].listFiles();
			for(int j = 0; j < studyDirs.length; j++) {
				File[] modalityDirs = studyDirs[j].listFiles();
				for(int k = 0; k < modalityDirs.length; k++) {
					File[] imageFiles = modalityDirs[k].listFiles();
					for(int l = 0; l < imageFiles.length; l++) {
						int pid = Integer.parseInt(pidDirs[i].getName().replaceAll("CR0*", ""));
						Date studyDate = df.parse(studyDirs[j].getName());
						String modality = modalityDirs[k].getName();
						int slicenr = Integer.parseInt(imageFiles[l].getName().replaceAll("IMG", "").replaceAll(".png", ""));
						insertImagePS.setInt(1, pid);
						insertImagePS.setDate(2, new java.sql.Date(studyDate.getTime()));
						insertImagePS.setString(3, modality);
						insertImagePS.setInt(4, slicenr);
						insertImagePS.setString(5, imageFiles[l].getAbsolutePath());
						
						insertImagePS.executeUpdate();
						conn.commit();
						insertImagePS.clearParameters();
					}
				}
			}
		}
	}
}
