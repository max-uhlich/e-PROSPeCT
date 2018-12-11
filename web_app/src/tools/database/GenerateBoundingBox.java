package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.admin.AdminConnector;

public class GenerateBoundingBox {

	private Connection conn = null;

	public GenerateBoundingBox() {
	}

	public static void main(String[] args) {
		GenerateBoundingBox gbb = new GenerateBoundingBox();
		try {
			gbb.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Connected to database.");
		System.out.println("** DOING SMALLEST BOUNDING BOX **");
		gbb.smallest_bounding_box();
		System.out.println("** DOING MEDIUM BOUNDING BOX **");
		gbb.medium_bounding_box();
		System.out.println("** DOING LARGEST BOUNDING BOX **");
		gbb.largest_bounding_box();
		System.out.println("** DOING REGION VOLUME TBL **");
		gbb.volume();
		gbb.disconnectGracefully();
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
	
	public void disconnectGracefully() {
		try {
			conn.commit();
			conn.close();
		} catch (Exception ex) {
			System.out.println("Error closing connection.");
			ex.printStackTrace();
		}
		conn = null;
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

	/* fields */
	int yrange_id = 1;
	int td_bb_id = 1;
	int twod_bb_id = 1;

	public void smallest_bounding_box() {
		/* SQL Strings */
		String s_deleteAll_VSR = "DELETE FROM btaw.yrange_voxelset";
		String s_selectROIID_VS = "SELECT DISTINCT roi_id FROM btaw.btap_roi_voxelset";
		String s_selectStudyID_VS = "SELECT DISTINCT study_id FROM btaw.btap_roi_voxelset WHERE roi_id = ?";
		String s_selectZ_VS = "SELECT DISTINCT z FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ?";
		String s_selectX_VS = "SELECT DISTINCT x FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ? AND z = ?";
		String s_selectRange_VS = "SELECT y FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ? AND z = ? AND x = ? ORDER BY y";

		/* Temporary Variables */
		int curr_roi_id = -1;
		int curr_study_id = -1;
		int curr_z = -1;
		int curr_x = -1;
		int curr_y1 = -1;
		int curr_y2 = -1;
		List<Integer> yvals = null;

		try {
			/* Prepare PreparedStatements, since it's expensive */
			PreparedStatement p_deleteAll_VSR = conn
					.prepareStatement(s_deleteAll_VSR);
			PreparedStatement p_selectROIID_VS = conn
					.prepareStatement(s_selectROIID_VS);
			PreparedStatement p_selectStudyID_VS = conn
					.prepareStatement(s_selectStudyID_VS);
			PreparedStatement p_selectZ_VS = conn
					.prepareStatement(s_selectZ_VS);
			PreparedStatement p_selectX_VS = conn
					.prepareStatement(s_selectX_VS);
			PreparedStatement p_selectRange_VS = conn
					.prepareStatement(s_selectRange_VS);

			/* clear yrange_voxelset table */
			p_deleteAll_VSR.executeUpdate();

			/* Outer loop iterating over roi_id column */
			ResultSet rset_roiid = p_selectROIID_VS.executeQuery();
			while (rset_roiid.next()) {
				curr_roi_id = rset_roiid.getInt(1);

				p_selectStudyID_VS.setInt(1, curr_roi_id);
				System.out.println("Doing roi_id: " + curr_roi_id);

				/* First inner loop iterating over study_id column */
				ResultSet rset_studyid = p_selectStudyID_VS.executeQuery();
				while (rset_studyid.next()) {
					curr_study_id = rset_studyid.getInt(1);

					System.out.println("Doing study_id: " + curr_study_id);
					p_selectZ_VS.setInt(1, curr_roi_id);
					p_selectZ_VS.setInt(2, curr_study_id);
					ResultSet rset_z = p_selectZ_VS.executeQuery();

					/* Second inner loop, iterating over z column */
					while (rset_z.next()) {
						curr_z = rset_z.getInt(1);

						p_selectX_VS.setInt(1, curr_roi_id);
						p_selectX_VS.setInt(2, curr_study_id);
						p_selectX_VS.setInt(3, curr_z);
						ResultSet rset_x = p_selectX_VS.executeQuery();

						/* Third inner loop, iterating over x */
						while (rset_x.next()) {
							curr_x = rset_x.getInt(1);

							p_selectRange_VS.setInt(1, curr_roi_id);
							p_selectRange_VS.setInt(2, curr_study_id);
							p_selectRange_VS.setInt(3, curr_z);
							p_selectRange_VS.setInt(4, curr_x);
							ResultSet rset_y = p_selectRange_VS.executeQuery();

							/*
							 * Fourth inner loop, integrity check of y column,
							 * and inserting of y line-segments
							 */
							yvals = new ArrayList<Integer>();
							while (rset_y.next()) {
								yvals.add(rset_y.getInt(1));
							}

							/*
							 * I apologize to whoever has to see this
							 * Trying to get pixels to y ranges
							 * only complicated because gaps are possible
							 * and single pixel "lines" are possible
							 */
							if (yvals.size() == 1) {
								this.insertVSR(curr_study_id, curr_roi_id,
										curr_z, curr_x,
										yvals.get(0).intValue(), yvals.get(0)
												.intValue());
								continue;
							} else {
								curr_y1 = yvals.get(0).intValue();
								curr_y2 = yvals.get(1).intValue();
							}

							for (int i = 0; i + 1 < yvals.size(); i++) {
								if (yvals.get(i).intValue() + 1 == yvals.get(
										i + 1).intValue()) {
									curr_y2 = yvals.get(i + 1).intValue();
									if (yvals.size() == i + 2) {
										this.insertVSR(curr_study_id,
												curr_roi_id, curr_z, curr_x,
												curr_y1, curr_y2);
										break;
									} else if (yvals.get(i + 1).intValue() + 1 != yvals
											.get(i + 2).intValue()) {
										this.insertVSR(curr_study_id,
												curr_roi_id, curr_z, curr_x,
												curr_y1, curr_y2);
										curr_y1 = yvals.get(i + 2).intValue();
										if (yvals.size() == i + 3) {
											this.insertVSR(curr_study_id,
													curr_roi_id, curr_z,
													curr_x, curr_y1, curr_y1);
											break;
										} else {
											i = i + 2;
										}
									}
									continue;
								} else {
									this.insertVSR(curr_study_id, curr_roi_id,
											curr_z, curr_x, yvals.get(i).intValue(), yvals.get(i).intValue());
									if(yvals.size() > i + 2) {
										curr_y1 = yvals.get(i + 1).intValue();
										if (yvals.size() == i + 3) {
											this.insertVSR(curr_study_id,
													curr_roi_id, curr_z,
													curr_x, curr_y1, curr_y1);
											break;
										} else {
											i++;
										}
									} else {
										this.insertVSR(curr_study_id, curr_roi_id,
												curr_z, curr_x, yvals.get(i + 1).intValue(), yvals.get(i + 1).intValue());
										break;
									}
								}
							}
						}
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.disconnect();
		}
	}

	/*
	 * Insertion of the YRange Segment put in function because caller code is horrendous
	 */
	private void insertVSR(int study_id, int roi_id, int z, int x, int y1,
			int y2) {
		String s_insert_VSR = "INSERT INTO btaw.yrange_voxelset (yrange_id, study_id, roi_id, z, x, y1, y2) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement p_insert_VSR = conn
					.prepareStatement(s_insert_VSR);
			p_insert_VSR.setInt(1, yrange_id);
			p_insert_VSR.setInt(2, study_id);
			p_insert_VSR.setInt(3, roi_id);
			p_insert_VSR.setInt(4, z);
			p_insert_VSR.setInt(5, x);
			p_insert_VSR.setInt(6, y1);
			p_insert_VSR.setInt(7, y2);
			p_insert_VSR.executeUpdate();
			yrange_id++;
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void medium_bounding_box() {
		String s_deleteAll_2DBBox = "DELETE FROM btaw.two_d_bbox";
		String s_selectROIID_VS = "SELECT DISTINCT roi_id FROM btaw.btap_roi_voxelset";
		String s_selectStudyID_VS = "SElECT DISTINCT study_id FROM btaw.btap_roi_voxelset WHERE roi_id = ?";
		String s_selectZ_VS = "SELECT DISTINCT z FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ?";
		String s_selectBBox_VS = "SELECT min(x) as x1, max(x) as x2, min(y) as y1, max(y) as y2 FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ? AND z = ?";
		String s_insertBBox_2dbb = "INSERT INTO btaw.two_d_bbox (twod_bb_id, study_id, roi_id, z, x1, x2, y1, y2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			/* Prepare all the statements, so it goes faster! */
			PreparedStatement p_delete2DBBox = conn.prepareStatement(s_deleteAll_2DBBox);
			PreparedStatement p_selectROIID_VS = conn.prepareStatement(s_selectROIID_VS);
			PreparedStatement p_selectStudyID_VS = conn.prepareStatement(s_selectStudyID_VS);
			PreparedStatement p_selectZ_VS = conn.prepareStatement(s_selectZ_VS);
			PreparedStatement p_selectBBox_VS = conn.prepareStatement(s_selectBBox_VS);
			PreparedStatement p_insertBBox_2dbb = conn.prepareStatement(s_insertBBox_2dbb);
			
			/* delete all the old stuff */
			p_delete2DBBox.executeUpdate();
			
			/* temporary variables */
			int curr_roi_id = -1;
			int curr_study_id = -1;
			int curr_z = -1;
			int curr_x1 = -1;
			int curr_x2 = -1;
			int curr_y1 = -1;
			int curr_y2 = -1;
			
			/* for each roi_id */
			ResultSet rset_roiid = p_selectROIID_VS.executeQuery();
			while(rset_roiid.next()) {
				curr_roi_id = rset_roiid.getInt(1);
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
							
							p_insertBBox_2dbb.setInt(1, twod_bb_id);
							p_insertBBox_2dbb.setInt(2, curr_study_id);
							p_insertBBox_2dbb.setInt(3, curr_roi_id);
							p_insertBBox_2dbb.setInt(4, curr_z);
							p_insertBBox_2dbb.setInt(5, curr_x1);
							p_insertBBox_2dbb.setInt(6, curr_x2);
							p_insertBBox_2dbb.setInt(7, curr_y1);
							p_insertBBox_2dbb.setInt(8, curr_y2);
							p_insertBBox_2dbb.executeUpdate();
							conn.commit();
							twod_bb_id++;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void largest_bounding_box() {
		String s_deleteAll_3DBBox = "DELETE FROM btaw.three_d_bbox";
		String s_selectROIID_VS = "SELECT DISTINCT roi_id FROM btaw.btap_roi_voxelset";
		String s_selectStudy_id = "SELECT DISTINCT study_id FROM btaw.btap_roi_voxelset WHERE roi_id = ?";
		String s_selectBBFields = "SELECT min(z) AS z1, max(z) AS z2, min(x1) AS x1, max(x2) AS x2, min(y1) AS y1, max(y2) AS y2 FROM btaw.two_d_bbox WHERE roi_id = ? AND study_id = ?";
		String s_insert3DBBox = "INSERT INTO btaw.three_d_bbox (td_bb_id, study_id, roi_id, z1, z2, x1, x2, y1, y2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			/* prepare them all */
			PreparedStatement p_selectROIID_VS = conn.prepareStatement(s_selectROIID_VS);
			PreparedStatement p_selectStudy_id = conn.prepareStatement(s_selectStudy_id);
			PreparedStatement p_selectBBFields = conn.prepareStatement(s_selectBBFields);
			PreparedStatement p_insert3DBBox = conn.prepareStatement(s_insert3DBBox);
			PreparedStatement p_delete3DBBox = conn.prepareStatement(s_deleteAll_3DBBox);

			/* deleate old stuff */
			p_delete3DBBox.executeUpdate();
			
			/* temp variables */
			int curr_study_id = -1;
			int curr_roi_id = -1;
			int z1 = -1, z2 = -1, x1 = -1, x2 = -1, y1 = -1, y2 = -1;
			
			/* for each roi_id */
			ResultSet rset_roi_id = p_selectROIID_VS.executeQuery();
			while(rset_roi_id.next()) {
				curr_roi_id = rset_roi_id.getInt(1);
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
					
						p_insert3DBBox.setInt(1, td_bb_id);
						p_insert3DBBox.setInt(2, curr_study_id);
						p_insert3DBBox.setInt(3, curr_roi_id);
						p_insert3DBBox.setInt(4, z1);
						p_insert3DBBox.setInt(5, z2);
						p_insert3DBBox.setInt(6, x1);
						p_insert3DBBox.setInt(7, x2);
						p_insert3DBBox.setInt(8, y1);
						p_insert3DBBox.setInt(9, y2);
						p_insert3DBBox.executeUpdate();
						conn.commit();
						td_bb_id++;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* precomputed volume specifically for UI, done this way because of way columns are handled in categories */
	public void volume() {
		String d_volTbl = "DROP TABLE IF EXISTS btaw.roi_volume";
		String s_selectROIID_VS = "SELECT roi_id, roi_desc FROM btaw.btap_rois";
		String s_selectStudy_id = "SELECT DISTINCT study_id FROM btaw.btap_roi_voxelset";
		String s_pid = "SELECT pid FROM btaw.btap_study WHERE study_id = ?";
		String s_selectVol_VS = "SELECT count(*) FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ?";
		
		/* hashmaps to keep roi_id and roi_description straight */
		HashMap<Integer, String> rois = new HashMap<Integer, String>();
		HashMap<Integer, Integer> roi_vol;
		int studyId = -1;

		try {
			/* delete old stuff first, needs to be dropped because of the table generation */
			PreparedStatement p_dropVolTbl = conn.prepareStatement(d_volTbl);
			p_dropVolTbl.executeUpdate();
			conn.commit();
			
			/* select the rois in DB */
			PreparedStatement p_selectROIID_VS = conn.prepareStatement(s_selectROIID_VS);
			
			/* shove rois/description in hasmap */
			ResultSet rset_roi_id = p_selectROIID_VS.executeQuery();
			while(rset_roi_id.next()) {
				rois.put(rset_roi_id.getInt(1), rset_roi_id.getString(2));
			}
			/* create table and insertion stuff */
			String c_volTbl = "CREATE TABLE btaw.roi_volume ( study_id bigint PRIMARY KEY, pid bigint NOT NULL";
			String i_volTbl = "INSERT INTO btaw.roi_volume ( study_id, pid";
			
			if(rois.size() == 0) {
				System.err.println("NO ROIS FOUND IN btaw.btap_rois, ABORTING");
				this.disconnect();
			} else {
				/* make the create table statement */
				for(Map.Entry<Integer, String> e: rois.entrySet()) {
					c_volTbl += ", " + e.getValue() + " bigint";
				}
				c_volTbl += ");";
			}
			
			/* create table */
			PreparedStatement p_createTbl = conn.prepareStatement(c_volTbl);
			p_createTbl.executeUpdate();
			conn.commit();
			
			/* prepare select statements */
			PreparedStatement p_selectVol = conn.prepareStatement(s_selectVol_VS);
			PreparedStatement p_selectStudyId = conn.prepareStatement(s_selectStudy_id);
			PreparedStatement p_selectPID = conn.prepareStatement(s_pid);
			
			/* for each study_id */
			ResultSet rset_studyId = p_selectStudyId.executeQuery();
			while(rset_studyId.next()) {
				studyId = rset_studyId.getInt(1);
				System.out.println("DOING STUDY: " + studyId);
				roi_vol = new HashMap<Integer, Integer>();
				
				/* compute volumes for each roi, store it in HM */
				for(Map.Entry<Integer, String> e: rois.entrySet()) {
					p_selectVol.setInt(1, e.getKey());
					p_selectVol.setInt(2, studyId);
					
					ResultSet rset_vol = p_selectVol.executeQuery();
					while(rset_vol.next()) {
						roi_vol.put(e.getKey(), rset_vol.getInt(1));
					}
				}
				
				/* tmp is the insertion for volume statement, done strangely again because of table generation */
				String tmp = "" + i_volTbl;
				int colCnt = 0;
				/* arraylist used to maintain order integrity, so stuff isn't inserted backwards */
				ArrayList<Integer> order = new ArrayList<Integer>();
				for(Map.Entry<Integer, Integer> e: roi_vol.entrySet()) {
					tmp += ", " + rois.get(e.getKey());
					order.add(e.getKey());
					colCnt++;
				}
				tmp += ") VALUES (?";
				for(int i = 0; i <= colCnt; i++) {
					tmp += ", ?";
				}
				tmp += ")";
				
				/* prepare insertion statement */
				PreparedStatement insert_vol = conn.prepareStatement(tmp);
				
				/* setup and insert, also insert pid due to restrictions in how columns in cat are setup for UI */
				p_selectPID.setInt(1, studyId);
				ResultSet rset_pid = p_selectPID.executeQuery();
								
				colCnt = 1;
				insert_vol.setInt(colCnt++, studyId);
				if(rset_pid.next()){
					insert_vol.setInt(colCnt++, rset_pid.getInt(1));
				} else {
					System.err.println("DID NOT FIND PID CORRESPONDING TO STUDYID: " + studyId);
					this.disconnect();
				}
				
				/* actually put roi volumes into insert prepared statement */
				for(Integer i : order) {
					insert_vol.setInt(colCnt, roi_vol.get(i));
					colCnt++;
				}
				insert_vol.executeUpdate();
				conn.commit();
			}
			/* premissions must be done because of table creation, because of this make target must be run with user who can grant permissions */
			String grantVol = "GRANT SELECT ON btaw.roi_volume TO btaw_web";
			PreparedStatement p_grantVol = conn.prepareStatement(grantVol);
			p_grantVol.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
