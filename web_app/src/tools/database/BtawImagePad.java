package tools.database;

import btaw.shared.model.BTAWDatabaseException;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import tools.admin.AdminConnector;

public class BtawImagePad {

	private Connection conn = null;

	public BtawImagePad() {
		// TODO: WHAT?
	}

	public static void main(String[] args) {
		BtawImagePad bip = new BtawImagePad();
		try {
			bip.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}

		System.out.println("Connected to database.");
		try {
			bip.makeVolLUT();
		} catch (SQLException e) {
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
	
	public void makeVolLUT() throws SQLException, IOException {
		String queryPids = "SELECT DISTINCT pid FROM btaw.person";
		String queryStudies = "SELECT DISTINCT study_id FROM btaw.btap_study WHERE pid = ?";
		String queryTypes = "SELECT DISTINCT type_description FROM btaw.btap_imagetype WHERE study_id = ?";
		String queryLinks = "SELECT btaw.btap_image.link, btaw.btap_study.study_id, btaw.btap_study.pid "
				+"FROM btaw.btap_image, btaw.btap_imagetype, btaw.btap_study "
				+"WHERE btaw.btap_image.imagetype_id = btaw.btap_imagetype.imagetype_id "
				+"AND btaw.btap_imagetype.study_id = btaw.btap_study.study_id "
				+"AND btaw.btap_study.pid = ? AND btaw.btap_study.study_id = ? "
				+"AND btaw.btap_imagetype.type_description = ?";
		String insertLUT = "INSERT INTO btaw.histogram_lut VALUES (nextval('lut_id_sqn'), ?, ?, ?, ?)";
		
		PreparedStatement pSelectPids = conn.prepareStatement(queryPids);
		PreparedStatement pSelectStudies = conn.prepareStatement(queryStudies);
		PreparedStatement pSelectTypes = conn.prepareStatement(queryTypes);
		PreparedStatement pSelectLinks = conn.prepareStatement(queryLinks);
		PreparedStatement pInsertLUT = conn.prepareStatement(insertLUT);
		
		int pid = -1;
		int study_id = -1;
		String type_description = null;
		
		ResultSet rSetPids = pSelectPids.executeQuery();
		while(rSetPids.next()) {
			pid = rSetPids.getInt(1);
			pSelectStudies.setInt(1, pid);
			
			ResultSet rSetStudies = pSelectStudies.executeQuery();
			
			while(rSetStudies.next()) {
				study_id = rSetStudies.getInt(1);
				pSelectTypes.setInt(1, study_id);
				
				ResultSet rSetTypes = pSelectTypes.executeQuery();
				
				while(rSetTypes.next()) {
					type_description = rSetTypes.getString(1);
					
					pSelectLinks.setInt(1, pid);
					pSelectLinks.setInt(2, study_id);
					pSelectLinks.setString(3, type_description);
					
					ResultSet rSetLinks = pSelectLinks.executeQuery();
					
					ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
					
					while(rSetLinks.next()) {
						images.add(ImageIO.read(new File(rSetLinks.getString(1))));
					}
					
					if(images.isEmpty())
						continue;
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oout = new ObjectOutputStream(baos);
					final long startTime = System.nanoTime();
					oout.writeObject(histogramEqualizationLUT(bufferedImageStitcherVertical(images)));
					oout.close();
					System.err.println("Hist Eq LUT for PID = "+pid+" STUDY_ID = "+study_id+" TYPE_DESCRIPTION = "+type_description+" DONE IN : " + (System.nanoTime() - startTime) +" in ns");
					
					pInsertLUT.setInt(1, pid);
					pInsertLUT.setInt(2, study_id);
					pInsertLUT.setString(3, type_description);
					pInsertLUT.setBytes(4, baos.toByteArray());
					pInsertLUT.executeUpdate();
					conn.commit();
				}
			}
		}
	}
	
    // Get the histogram equalization lookup table for separate R, G, B channels
    private static ArrayList<int[]> histogramEqualizationLUT(BufferedImage input) {
 
        // Get an image histogram - calculated values by R, G, B channels
        ArrayList<int[]> imageHist = imageHistogram(input);
 
        // Create the lookup table
        ArrayList<int[]> imageLUT = new ArrayList<int[]>();
 
        // Fill the lookup table
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];
 
        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
        long sumr = 0;
        long sumg = 0;
        long sumb = 0;
 
        // Calculate the scale factor
        float scale_factor = (float) (255.0 / (input.getWidth() * input.getHeight()));
 
        for(int i=0; i<rhistogram.length; i++) {
            sumr += imageHist.get(0)[i];
            int valr = (int) (sumr * scale_factor);
            if(valr > 255) {
                rhistogram[i] = 255;
            }
            else rhistogram[i] = valr;
 
            sumg += imageHist.get(1)[i];
            int valg = (int) (sumg * scale_factor);
            if(valg > 255) {
                ghistogram[i] = 255;
            }
            else ghistogram[i] = valg;
 
            sumb += imageHist.get(2)[i];
            int valb = (int) (sumb * scale_factor);
            if(valb > 255) {
                bhistogram[i] = 255;
            }
            else bhistogram[i] = valb;
        }
 
        imageLUT.add(rhistogram);
        imageLUT.add(ghistogram);
        imageLUT.add(bhistogram);
 
        return imageLUT;
 
    }
 
    // Return an ArrayList containing histogram values for separate R, G, B channels
    public static ArrayList<int[]> imageHistogram(BufferedImage input) {
 
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];
 
        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
 
                int red = new Color(input.getRGB (i, j)).getRed();
                int green = new Color(input.getRGB (i, j)).getGreen();
                int blue = new Color(input.getRGB (i, j)).getBlue();
                
                // Increase the values of colors
                rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;
 
            }
        }
 
        ArrayList<int[]> hist = new ArrayList<int[]>();
        hist.add(rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);
 
        return hist;
 
    }
    
	private static BufferedImage bufferedImageStitcherVertical(ArrayList<BufferedImage> originalImages) {
		int height = 0;
		int width = 0;
		
		for(BufferedImage bi : originalImages) {
			height += bi.getHeight();
			if(bi.getWidth() > width) {
				width = bi.getWidth();
			}
		}
		
		BufferedImage output = new BufferedImage(width, height, originalImages.get(0).getType());
		Graphics2D g2 = output.createGraphics();
		
		int h = 0;
		
		for(BufferedImage bi : originalImages) {
			g2.drawImage(bi, 0, h, null);
			h += bi.getHeight();
		}
		
		
		return output;
	}
    
}
