package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import tools.admin.AdminConnector;

public class AddRegionOfInterest {

	private Connection conn = null;

	public AddRegionOfInterest() {
		// TODO: WHAT?
	}

	public static void main(String[] args) {
		Console cons = System.console();
		
		String roi_desc = new String(cons.readLine("%s\n?", "Please enter region of interest description"));
		String s_roi_id = new String(cons.readLine("%s\n?", "PLease enter region of interest integer identifier"));
		Integer roi_id = -1;
		
		if(roi_desc.length() == 0 || s_roi_id.length() == 0) {
			System.err.println("Error, something left blank.");
			System.exit(-1);
		}
		try {
			roi_id = Integer.parseInt(s_roi_id);
		} catch (NumberFormatException e) {
			System.err.println("Error, could not parse region of interest identifier, please ensure it is an integer.");
			System.exit(-1);
		}
		
		AddRegionOfInterest add = new AddRegionOfInterest();
		try {
			add.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Connected to database.");
		add.addRegionOfInterest(roi_id, roi_desc);
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

	public void addRegionOfInterest(Integer roi_id, String roi_desc) {
		String ins_ROI = "INSERT INTO btaw.btap_rois (roi_id, roi_desc) VALUES(?, ?)";
		
		try {
			PreparedStatement p_ins_ROI = conn.prepareStatement(ins_ROI);
			
			p_ins_ROI.setInt(1, roi_id);
			p_ins_ROI.setString(2, roi_desc);
			p_ins_ROI.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
