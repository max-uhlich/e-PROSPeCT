package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tools.admin.AdminConnector;

public class QueryScratch {

	private Connection conn = null;

	public QueryScratch() {
		// TODO: WHAT?
	}

	public static void main(String[] args) {
		QueryScratch qs = new QueryScratch();
		try {
			qs.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Connected to database.");
		System.out.println("** DOING SMALLEST BOUNDING BOX **");
		qs.smallest_bounding_box();
		System.out.println("** DOING MEDIUM BOUNDING BOX **");
		qs.medium_bounding_box();
		System.out.println("** DOING LARGEST BOUNDING BOX **");
		qs.largest_bounding_box();
		qs.disconnect();
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

	/* fields */
	int yrange_id = 1;
	int td_bb_id = 1;
	int twod_bb_id = 1;

	public void smallest_bounding_box() {
		/* SQL Strings */
		String s_deleteAll_VSR = "DELETE FROM btaw.yrange_voxelset";
		String s_selectROIID_VS = "SELECT DISTINCT roi_id FROM btaw.btap_roi_voxelset";
		String s_selectStudyID_VS = "SElECT DISTINCT study_id FROM btaw.btap_roi_voxelset WHERE roi_id = ?";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.disconnect();
		}
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		System.err
				.println("yrange_id: " + yrange_id + "\t curr_study_id: "
						+ study_id + "\t curr_roi_id: " + roi_id
						+ "\t curr_z: " + z + "\t curr_x: " + x
						+ "\t curr_y1: " + y1 + "\t curr_y2: " + y2); */
	}
	
	public void medium_bounding_box() {
		String s_deleteAll_2DBBox = "DELETE FROM btaw.two_d_bbox";
		String s_selectROIID_VS = "SELECT DISTINCT roi_id FROM btaw.btap_roi_voxelset";
		String s_selectStudyID_VS = "SElECT DISTINCT study_id FROM btaw.btap_roi_voxelset WHERE roi_id = ?";
		String s_selectZ_VS = "SELECT DISTINCT z FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ?";
		String s_selectBBox_VS = "SELECT min(x) as x1, max(x) as x2, min(y) as y1, max(y) as y2 FROM btaw.btap_roi_voxelset WHERE roi_id = ? AND study_id = ? AND z = ?";
		String s_insertBBox_2dbb = "INSERT INTO btaw.two_d_bbox (twod_bb_id, study_id, roi_id, z, x1, x2, y1, y2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement p_delete2DBBox = conn.prepareStatement(s_deleteAll_2DBBox);
			PreparedStatement p_selectROIID_VS = conn.prepareStatement(s_selectROIID_VS);
			PreparedStatement p_selectStudyID_VS = conn.prepareStatement(s_selectStudyID_VS);
			PreparedStatement p_selectZ_VS = conn.prepareStatement(s_selectZ_VS);
			PreparedStatement p_selectBBox_VS = conn.prepareStatement(s_selectBBox_VS);
			PreparedStatement p_insertBBox_2dbb = conn.prepareStatement(s_insertBBox_2dbb);
			
			p_delete2DBBox.executeUpdate();
			
			int curr_roi_id = -1;
			int curr_study_id = -1;
			int curr_z = -1;
			int curr_x1 = -1;
			int curr_x2 = -1;
			int curr_y1 = -1;
			int curr_y2 = -1;
			
			ResultSet rset_roiid = p_selectROIID_VS.executeQuery();
			while(rset_roiid.next()) {
				curr_roi_id = rset_roiid.getInt(1);
				System.out.println("Doing roi_id: " + curr_roi_id);
				
				p_selectStudyID_VS.setInt(1, curr_roi_id);
				
				ResultSet rset_study_id = p_selectStudyID_VS.executeQuery();
				while(rset_study_id.next()) {
					curr_study_id = rset_study_id.getInt(1);
					System.out.println("Doing study_id: " + curr_study_id);
					
					p_selectZ_VS.setInt(1, curr_roi_id);
					p_selectZ_VS.setInt(2, curr_study_id);
					
					ResultSet rset_z = p_selectZ_VS.executeQuery();
					while(rset_z.next()) {
						curr_z = rset_z.getInt(1);
						
						p_selectBBox_VS.setInt(1, curr_roi_id);
						p_selectBBox_VS.setInt(2, curr_study_id);
						p_selectBBox_VS.setInt(3, curr_z);
						
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
							/*
							System.err
							.println("curr_study_id: "
									+ curr_study_id + "\t curr_roi_id: " + curr_roi_id
									+ "\t curr_z: " + curr_z + "\t curr_x1: " + curr_x1 + "\t curr_x2: " + curr_x2 
									+ "\t curr_y1: " + curr_y1 + "\t curr_y2: " + curr_y2); */
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
			PreparedStatement p_selectROIID_VS = conn.prepareStatement(s_selectROIID_VS);
			PreparedStatement p_selectStudy_id = conn.prepareStatement(s_selectStudy_id);
			PreparedStatement p_selectBBFields = conn.prepareStatement(s_selectBBFields);
			PreparedStatement p_insert3DBBox = conn.prepareStatement(s_insert3DBBox);
			PreparedStatement p_delete3DBBox = conn.prepareStatement(s_deleteAll_3DBBox);

			p_delete3DBBox.executeUpdate();
			
			int curr_study_id = -1;
			int curr_roi_id = -1;
			int z1 = -1, z2 = -1, x1 = -1, x2 = -1, y1 = -1, y2 = -1;
			
			ResultSet rset_roi_id = p_selectROIID_VS.executeQuery();
			while(rset_roi_id.next()) {
				curr_roi_id = rset_roi_id.getInt(1);
				System.out.println("Doing roi_id: " + curr_roi_id);
				
				p_selectStudy_id.setInt(1, curr_roi_id);
				
				ResultSet rset_study_id = p_selectStudy_id.executeQuery();
				while(rset_study_id.next()) {
					curr_study_id = rset_study_id.getInt(1);
					System.out.println("Doing study_id: " + curr_study_id);
				
					p_selectBBFields.setInt(1, curr_roi_id);
					p_selectBBFields.setInt(2, curr_study_id);
					
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
}
