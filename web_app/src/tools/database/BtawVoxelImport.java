package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.io.BufferedReader;
import java.io.Console;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import tools.admin.AdminConnector;

public class BtawVoxelImport {

	private Connection conn = null;
	private ArrayList<Integer> rois = null;

	public BtawVoxelImport() {
		rois = new ArrayList<Integer>();
	}

	public static void main(String[] args) {
		Console cons = System.console();
		
		String rootPath = new String(cons.readLine("%s\n?", "The program assumes filenames of the type CR#####_yyyy-MM-dd_SegmentationName and that the SegmentationName is already in the database.\n" +
				"Please specify root directory: "));
		
		if(rootPath.length() == 0) {
			System.err.println("Error, root directory left blank.  Aborting.");
			System.exit(-1);
		}
		
		BtawVoxelImport bvi = new BtawVoxelImport();
		try {
			bvi.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}

		System.out.println("Connected to database.");
		try {
			bvi.insertVoxels(rootPath);
			bvi.generateBoundingBoxes();
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
	
	public void insertVoxels(String root) throws SQLException, ParseException, IOException {
		String insertVoxelsStr = "INSERT INTO btaw.yrange_voxelset (yrange_id, study_id, roi_id, z, x, y1, y2) VALUES (nextval('yrange_sqn'), ?, ?, ?, ?, ?, ?)";
		String selectRoi = "SELECT roi_id FROM btaw.btap_rois WHERE roi_desc = ?";
		String selectStudyID = "SELECT study_id FROM btaw.btap_study WHERE pid = ? AND study_date = ?";
		PreparedStatement insertVoxelPS = conn.prepareStatement(insertVoxelsStr);
		PreparedStatement selectRoiPS = conn.prepareStatement(selectRoi);
		PreparedStatement selectStudyPS = conn.prepareStatement(selectStudyID);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		File rootFile = new File(root);
		File[] segFiles = rootFile.listFiles();
		
		for (int i = 0; i < segFiles.length; i++) {
			String[] splitStrings = segFiles[i].getName().split("_");
			int pid = Integer.parseInt(splitStrings[0].replaceAll("CR0*", ""));
			int segID = -1;
			int studyID = -1;
			Date studyDate = df.parse(splitStrings[1]);
			String segType = splitStrings[2].substring(0, splitStrings[2].length() - 4);
			segType = segType.substring(0, 1).toUpperCase() + segType.substring(1).toLowerCase();
			
			System.err.println("pid: " + pid + "\tstudyDate: " + df.format(studyDate) + "\tsegType: " + segType);
			
			selectRoiPS.setString(1, segType);
			ResultSet rsetRoi = selectRoiPS.executeQuery();
			
			if(rsetRoi.next()) {
				segID = rsetRoi.getInt(1);
			} else {
				System.err.println("Couldn't find roi id... aborting");
				System.exit(-1);
			}
			
			selectStudyPS.setInt(1, pid);
			selectStudyPS.setDate(2, new java.sql.Date(studyDate.getTime()));
			ResultSet rsetStudyID = selectStudyPS.executeQuery();
			
			if(rsetStudyID.next()) {
				studyID = rsetStudyID.getInt(1);
			} else {
				System.err.println("Couldn't find study id... inserting study");
				studyID = this.insertStudy(pid, studyDate);
				if(studyID == -1) {
					System.err.println("Couldn't insert study_id... aborting...");
					System.exit(-1);
				}
			}
			
			FileInputStream fstream = new FileInputStream(segFiles[i]);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while((strLine = br.readLine()) != null) {
				if(strLine.matches("[0-9]+"))
					continue;
				
				String[] splitLine = strLine.split("\\), \\(");
				
				String[] startPoint = splitLine[0].substring(1).split(", ");
				String[] endPoint = splitLine[1].substring(0, splitLine[1].length() - 1).split(", ");

				int x1 = Integer.parseInt(startPoint[0]);
				int y1 = Integer.parseInt(startPoint[1]);
				int z1 = Integer.parseInt(startPoint[2]);
				
				int x2 = Integer.parseInt(endPoint[0]);
				int y2 = Integer.parseInt(endPoint[1]);
				int z2 = Integer.parseInt(endPoint[2]);
				
				//System.err.println("x1 : " + x1 + "\ty1: " + y1 + "\tz1: " + z1 + "\tx2: "+ x2 + "\ty2: " + y2 + "\tz2: " + z2);
				
				insertVoxelPS.setInt(1, studyID);
				insertVoxelPS.setInt(2, segID);
				insertVoxelPS.setInt(3, z1);
				insertVoxelPS.setInt(4, x1);
				insertVoxelPS.setInt(5, y1);
				insertVoxelPS.setInt(6, y2);

				insertVoxelPS.executeUpdate();
				conn.commit();
				insertVoxelPS.clearParameters();
			}
			br.close();
		}
	}
	
	public int insertStudy(int pid, Date studyDate) {
		String s_insertStudyID = "INSERT INTO btaw.btap_study (study_id, pid, study_description, study_date, institution_name, station_name) VALUES (nextval('study_id_sqn'), ?, ?, ?, 'unknown', 'unknown')";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		String selectStudyID = "SELECT study_id FROM btaw.btap_study WHERE pid = ? AND study_date = ?";
		
		try {
			PreparedStatement p_insertStudyID = conn.prepareStatement(s_insertStudyID);
			p_insertStudyID.setInt(1, pid);
			p_insertStudyID.setString(2, df.format(studyDate));
			p_insertStudyID.setDate(3, new java.sql.Date(studyDate.getTime()));
			
			p_insertStudyID.executeUpdate();
			conn.commit();

			PreparedStatement selectStudyPS = conn.prepareStatement(selectStudyID);
			selectStudyPS.setInt(1, pid);
			selectStudyPS.setDate(2, new java.sql.Date(studyDate.getTime()));
			ResultSet rsetStudyID = selectStudyPS.executeQuery();
			
			if(rsetStudyID.next()) {
				return rsetStudyID.getInt(1);
			} else {
				System.err.println("Error couldn't find study_id after inserting... aborting...");
				System.exit(-1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void generateBoundingBoxes() {
		System.out.println("DOING BOUNDING BOXES");
		this.medium_bounding_box();
		this.largest_bounding_box();
	}
	

	public void medium_bounding_box() {
		String s_selectStudyID_VS = "SElECT DISTINCT study_id FROM btaw.yrange_voxelset WHERE roi_id = ?";
		String s_selectZ_VS = "SELECT DISTINCT z FROM btaw.yrange_voxelset WHERE roi_id = ? AND study_id = ?";
		String s_selectBBox_VS = "SELECT min(x) as x1, max(x) as x2, min(y1) as y1, max(y2) as y2 FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ? AND z = ?";
		String s_insertBBox_2dbb = "INSERT INTO btaw.two_d_bbox (twod_bb_id, study_id, roi_id, z, x1, x2, y1, y2) VALUES (nextval('twod_bb_sqn'), ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			/* Prepare all the statements, so it goes faster! */
			PreparedStatement p_selectStudyID_VS = conn.prepareStatement(s_selectStudyID_VS);
			PreparedStatement p_selectZ_VS = conn.prepareStatement(s_selectZ_VS);
			PreparedStatement p_selectBBox_VS = conn.prepareStatement(s_selectBBox_VS);
			PreparedStatement p_insertBBox_2dbb = conn.prepareStatement(s_insertBBox_2dbb);
			
			/* temporary variables */
			int curr_roi_id = -1;
			int curr_study_id = -1;
			int curr_z = -1;
			int curr_x1 = -1;
			int curr_x2 = -1;
			int curr_y1 = -1;
			int curr_y2 = -1;
			
			for(Integer i : rois) {
				curr_roi_id = i;
				System.out.println("Doing roi_id: " + curr_roi_id);
				
				p_selectStudyID_VS.setInt(1, curr_roi_id);
				
				/* for each study_id where roi_id = whatever from above */
				ResultSet rset_study_id = p_selectStudyID_VS.executeQuery();
				while(rset_study_id.next()) {
					curr_study_id = rset_study_id.getInt(1);
					System.out.println("Doing study_id: " + curr_study_id);
					
					p_selectZ_VS.setInt(1, curr_roi_id);
					p_selectZ_VS.setInt(2, curr_study_id);
					
					/* for each z value, where study_id is whatever above and roi_id whatever above */
					ResultSet rset_z = p_selectZ_VS.executeQuery();
					while(rset_z.next()) {
						curr_z = rset_z.getInt(1);
						
						p_selectBBox_VS.setInt(1, curr_roi_id);
						p_selectBBox_VS.setInt(2, curr_study_id);
						p_selectBBox_VS.setInt(3, curr_z);
						
						/* make the 2d bbox and insert */
						ResultSet rset_bbox = p_selectBBox_VS.executeQuery();
						while(rset_bbox.next()) {
							curr_x1 = rset_bbox.getInt(1);
							curr_x2 = rset_bbox.getInt(2);
							curr_y1 = rset_bbox.getInt(3);
							curr_y2 = rset_bbox.getInt(4);
							
							p_insertBBox_2dbb.setInt(1, curr_study_id);
							p_insertBBox_2dbb.setInt(2, curr_roi_id);
							p_insertBBox_2dbb.setInt(3, curr_z);
							p_insertBBox_2dbb.setInt(4, curr_x1);
							p_insertBBox_2dbb.setInt(5, curr_x2);
							p_insertBBox_2dbb.setInt(6, curr_y1);
							p_insertBBox_2dbb.setInt(7, curr_y2);
							p_insertBBox_2dbb.executeUpdate();
							conn.commit();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void largest_bounding_box() {
		String s_selectStudy_id = "SELECT DISTINCT study_id FROM btaw.yrange_voxelset WHERE roi_id = ?";
		String s_selectBBFields = "SELECT min(z) AS z1, max(z) AS z2, min(x1) AS x1, max(x2) AS x2, min(y1) AS y1, max(y2) AS y2 FROM btaw.two_d_bbox WHERE roi_id = ? AND study_id = ?";
		String s_insert3DBBox = "INSERT INTO btaw.three_d_bbox (td_bb_id, study_id, roi_id, z1, z2, x1, x2, y1, y2) VALUES (nextval('td_bb_sqn'), ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			/* prepare them all */
			PreparedStatement p_selectStudy_id = conn.prepareStatement(s_selectStudy_id);
			PreparedStatement p_selectBBFields = conn.prepareStatement(s_selectBBFields);
			PreparedStatement p_insert3DBBox = conn.prepareStatement(s_insert3DBBox);
			
			/* temp variables */
			int curr_study_id = -1;
			int curr_roi_id = -1;
			int z1 = -1, z2 = -1, x1 = -1, x2 = -1, y1 = -1, y2 = -1;
			
			for(Integer i : rois) {
				curr_roi_id = i;
				System.out.println("Doing roi_id: " + curr_roi_id);
				
				p_selectStudy_id.setInt(1, curr_roi_id);
				
				/* for each study_id */
				ResultSet rset_study_id = p_selectStudy_id.executeQuery();
				while(rset_study_id.next()) {
					curr_study_id = rset_study_id.getInt(1);
					System.out.println("Doing study_id: " + curr_study_id);
				
					p_selectBBFields.setInt(1, curr_roi_id);
					p_selectBBFields.setInt(2, curr_study_id);
					
					/* make the 3d bbox and insert */
					ResultSet rset_BBFields = p_selectBBFields.executeQuery();
					while(rset_BBFields.next()) {
						z1 = rset_BBFields.getInt(1);
						z2 = rset_BBFields.getInt(2);
						x1 = rset_BBFields.getInt(3);
						x2 = rset_BBFields.getInt(4);
						y1 = rset_BBFields.getInt(5);
						y2 = rset_BBFields.getInt(6);
					
						p_insert3DBBox.setInt(1, curr_study_id);
						p_insert3DBBox.setInt(2, curr_roi_id);
						p_insert3DBBox.setInt(3, z1);
						p_insert3DBBox.setInt(4, z2);
						p_insert3DBBox.setInt(5, x1);
						p_insert3DBBox.setInt(6, x2);
						p_insert3DBBox.setInt(7, y1);
						p_insert3DBBox.setInt(8, y2);
						p_insert3DBBox.executeUpdate();
						conn.commit();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
