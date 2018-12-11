package btaw.server;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import btaw.server.model.db.Database;
import btaw.server.util.GrayFilter;
import btaw.shared.model.BTAWDatabaseException;

public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = -3638250543676159525L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*String result = req.getParameter("fileInfo1");
        
        Path path = Paths.get(result);
        String fileName = path.getFileName().toString();

        int BUFFER = 1024*100;
        //resp.setContentType( "application/octet-stream" );
        resp.setContentType("image/png");
        //resp.setHeader("Content-Disposition:", "attachment;filename=" + "\"" + fileName + "\"" );
        resp.setHeader("Content-Disposition", "filename=" + fileName);
        ServletOutputStream outputStream = resp.getOutputStream();
        //resp.setContentLength(Long.valueOf(getfile(fileName).length()).intValue());
        resp.setBufferSize(BUFFER);
        //Your IO code goes here to create a file and set to outputStream//

        //Path path = Paths.get("/opt/apache-tomcat-8.0.33/webapps/ROOT/resources/runtime/" + fileName);
        //Path path = Paths.get("C:\\Users\\uhlich\\workspace\\btaw\\war\\resources\\runtime\\" + fileName);
        byte[] data = Files.readAllBytes(path);
        resp.setContentLength(Long.valueOf(data.length).intValue());
        
        ServletOutputStream out = resp.getOutputStream();
        out.write(data);
        out.flush();*/
        
        
        
        //your image servlet code here
        resp.setContentType("image/png");

        // Set content size
        File file = new File(req.getParameter("fileInfo1"));
        resp.setContentLength((int)file.length());

        // Open the file and output streams
        FileInputStream in = new FileInputStream(file);
        ServletOutputStream out = resp.getOutputStream();

        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        in.close();
        out.close();
        
        
		

		/*
		if (req.getSession().getAttribute("auth-token") == null)
			return;
		
		String id = req.getParameter("id");
		String pid = req.getParameter("pid");
		String study_date = req.getParameter("study_date");
		String modality = req.getParameter("modality");
		String slicenr = req.getParameter("slicenr");
		boolean cr = Boolean.parseBoolean(req.getParameter("cr"));
		boolean dr = Boolean.parseBoolean(req.getParameter("dr"));
		boolean er = Boolean.parseBoolean(req.getParameter("er"));
		String scaleFactor = req.getParameter("sf");
		String offset = req.getParameter("offset");
		boolean hist_eq = Boolean.parseBoolean(req.getParameter("hist_eq"));
		boolean apply_wl = Boolean.parseBoolean(req.getParameter("apply_wl"));
		String url = req.getParameter("url");
		String url2 = req.getParameter("url2");
		
		if(url2 != null && url != null) {
			ArrayList<BufferedImage> inputImages = new ArrayList<BufferedImage>();
			inputImages.add(ImageIO.read(new URL(url)));
			inputImages.add(ImageIO.read(new URL(url2)));
			inputImages.add(ImageIO.read(new URL(url2)));
			inputImages.add(ImageIO.read(new URL(url2)));
			inputImages.add(ImageIO.read(new URL(url2)));
			inputImages.add(ImageIO.read(new URL(url2)));
			resp.setContentType("jpg");
			ImageIO.write(histogramEqualization(ImageIO.read(new URL(url)), inputImages), "jpg", resp.getOutputStream());
			System.err.println("Done multi-image histogram equalization.");
			return;
		}
		
		if(url != null) {
			final long startTime = System.nanoTime();
			resp.setContentType("jpg");
			ImageIO.write(histogramEqualization(ImageIO.read(new URL(url))), "jpg", resp.getOutputStream());
			System.err.println("Done single-image histogram equalization.");
			System.err.println("Total Time in ns: " + (System.nanoTime() - startTime));
			return;
		}
		
		System.err.println("id: " + id + " pid: " + pid + " study_date: " + study_date +" modality: " + modality + " slicenr: " + slicenr+" cr: "+cr);
		
		if(pid != null && study_date != null && modality != null && slicenr != null && (cr || dr)) {
			try {
				Database d = Database.getInstance(this.getServletContext());
				String query = null;
				if(cr) {
					query = "SELECT link FROM btaw.img_reg_cr WHERE pid = "+pid+" AND study_date = '"+study_date.replaceAll("/", "-")+"' AND image_modality = '"+modality+"' AND slicenr = "+slicenr;
				} else {
					query = "SELECT link FROM btaw.img_reg_dr WHERE pid = "+pid+" AND study_date = '"+study_date.replaceAll("/", "-")+"' AND image_modality = '"+modality+"' AND slicenr = "+slicenr;
				}
				System.err.println(query);
				ResultSet r = d.execute(req.getSession(), query);
				if (r.next()) {
					String link = r.getString("link");
					int length = 0;
					File image = new File(link);
					
					if(hist_eq) {
						final long startTime3 = System.nanoTime();
						resp.setContentType("png");
				        String selectLUT = "SELECT lut FROM btaw.histogram_lut, btaw.btap_study "
				        		+"WHERE btaw.histogram_lut.study_id = btaw.btap_study.study_id "
				        		+"AND btaw.histogram_lut.pid = "+pid+" "
				        		+"AND btaw.btap_study.study_date = '"+study_date.replaceAll("/", "-")+"' "
				        		+"AND btaw.histogram_lut.type_description = '"+modality+"'";
				        System.err.println(selectLUT);
				        ResultSet rSetLUT = d.execute(req.getSession(), selectLUT);
				        if(!rSetLUT.next()) {
							ImageIO.write(histogramEqualization(ImageIO.read(image)), "png", resp.getOutputStream());
							System.err.println("Histogram Equalization Done (no LUT found) in " + (System.nanoTime() - startTime3));
							return;
				        }
						byte[] lutBytes = rSetLUT.getBytes(1);
						ObjectInputStream oip = new ObjectInputStream(new ByteArrayInputStream(lutBytes));
						Object object = oip.readObject();
						oip.close();
						rSetLUT.close();
						ImageIO.write(histogramEqualizationPrecomputedLUT(ImageIO.read(image), (ArrayList<int[]>)object), "png", resp.getOutputStream());
						System.err.println("Histogram Equalization Done in " + (System.nanoTime() - startTime3));
						return;
					}
					
					
					if(apply_wl && 
							req.getParameter("lwr") != null &&
							req.getParameter("upr") != null) {
						double lwr = Double.parseDouble(req.getParameter("lwr"));
						double upr = Double.parseDouble(req.getParameter("upr"));
						System.err.println("lwr : " + lwr + " upr : " + upr);
						final long startTime = System.nanoTime();
						resp.setContentType("png");
						GrayFilter filter = new GrayFilter(upr, lwr);
						BufferedImage src = ImageIO.read(image);
						FilteredImageSource fImg = new FilteredImageSource(src.getSource(), filter);
						BufferedImage dest = new BufferedImage(src.getHeight(), src.getWidth(), BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2 = dest.createGraphics();
						g2.drawImage(Toolkit.getDefaultToolkit().createImage(fImg), 0, 0, null);
						ImageIO.write(dest, "png", resp.getOutputStream());
						g2.dispose();
						System.err.println("Total time for win/lvl in ns: " + (System.nanoTime() - startTime));
						
						return;
						
					}
					
					if(scaleFactor != null && offset != null) {
						BufferedImage biSrc = ImageIO.read(image);
						BufferedImage biDest = ImageIO.read(image);
						
						RescaleOp rescale = new RescaleOp(Float.parseFloat(scaleFactor), Float.parseFloat(offset), null);
						
						rescale.filter(biSrc, biDest);
						
						resp.setContentType("image/png");
						
						ImageIO.write(biDest, "png", resp.getOutputStream());
						return;
					}
					
					DataInputStream in = new DataInputStream(
							new FileInputStream(image));
					byte[] bbuf = new byte[(int) image.length()];

					resp.setContentType("image/png");
					while ((in != null) && ((length = in.read(bbuf)) != -1)) {
						resp.getOutputStream().write(bbuf, 0, length);
					}
					in.close();
					return;
				} else {
					System.err.println("ResultSet had no data for query: "
							+ query);
				}
				
			} catch (BTAWDatabaseException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (id == null || er)
			return;
		
		try {
			Database d = Database.getInstance(this.getServletContext());
			String query = "SELECT link FROM btaw.btap_image WHERE image_id = '"+id+"'";

			ResultSet r = d.execute(req.getSession(), query);
			if (r.next()) {
				String link = r.getString("link");
				int length = 0;
				File image = new File(link);
				
				if(hist_eq) {
					resp.setContentType("png");
			        String selectLUT = "SELECT btaw.histogram_lut.lut FROM btaw.histogram_lut, btaw.btap_study "
			        		+"WHERE btaw.histogram_lut.study_id = btaw.btap_study.study_id "
			        		+"AND btaw.histogram_lut.pid = "+pid+" "
			        		+"AND btaw.btap_study.study_date = '"+study_date.replaceAll("/", "-")+"' "
					        		+"AND btaw.histogram_lut.type_description = '"+modality+"'";
			        System.err.println(selectLUT);
			        ResultSet rSetLUT = d.execute(req.getSession(), selectLUT);
			        if(!rSetLUT.next()) {
						ImageIO.write(histogramEqualization(ImageIO.read(image)), "png", resp.getOutputStream());
			        }
					byte[] lutBytes = rSetLUT.getBytes(1);
					ObjectInputStream oip = new ObjectInputStream(new ByteArrayInputStream(lutBytes));
					Object object = oip.readObject();
					oip.close();
					rSetLUT.close();
					ImageIO.write(histogramEqualizationPrecomputedLUT(ImageIO.read(image), (ArrayList<int[]>)object), "png", resp.getOutputStream());
					return;
				}
				
				if(apply_wl &&
						req.getParameter("lwr") != null &&
						req.getParameter("upr") != null) {
					double lwr = Double.parseDouble(req.getParameter("lwr"));
					double upr = Double.parseDouble(req.getParameter("upr"));
					final long startTime = System.nanoTime();
					resp.setContentType("png");
					GrayFilter filter = new GrayFilter(upr, lwr);
					BufferedImage src = ImageIO.read(image);
					FilteredImageSource fImg = new FilteredImageSource(src.getSource(), filter);
					BufferedImage dest = new BufferedImage(src.getHeight(), src.getWidth(), BufferedImage.TYPE_BYTE_GRAY);
					Graphics2D g2 = dest.createGraphics();
					g2.drawImage(Toolkit.getDefaultToolkit().createImage(fImg), 0, 0, null);
					ImageIO.write(dest, "png", resp.getOutputStream());
					g2.dispose();
					System.err.println("Total time for win/lvl in ns: " + (System.nanoTime() - startTime));
					
					return;
					
				}
				
				if(scaleFactor != null && offset != null) {
					BufferedImage biSrc = ImageIO.read(image);
					BufferedImage biDest = ImageIO.read(image);
					
					RescaleOp rescale = new RescaleOp(Float.parseFloat(scaleFactor), Float.parseFloat(offset), null);
					
					rescale.filter(biSrc, biDest);
					
					resp.setContentType("image/png");
					
					ImageIO.write(biDest, "png", resp.getOutputStream());
					return;
				}
				
				DataInputStream in = new DataInputStream(new FileInputStream(image));
				byte[] bbuf = new byte[(int) image.length()];
				
				resp.setContentType("image/png");
				while ((in != null) && ((length = in.read(bbuf)) != -1)) {
					resp.getOutputStream().write(bbuf, 0, length);
				}
				in.close();
				return;
			} else {
				System.err.println("ResultSet had no data for id = " + id);
			}
			
		} catch (BTAWDatabaseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
	
	private static BufferedImage histogramEqualization(BufferedImage original, ArrayList<BufferedImage> originalImages) {
		 
        int red;
        int green;
        int blue;
        int alpha;
        int newPixel = 0;
 
        // Get the Lookup table for histogram equalization
		final long startTime = System.nanoTime();
        ArrayList<int[]> histLUT = histogramEqualizationLUT(bufferedImageStitcherVertical(originalImages));
		System.err.println("LUT Time in ns: " + (System.nanoTime() - startTime));
		
		final long startTime2 = System.nanoTime();
        BufferedImage histogramEQ = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
 
        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {
 
                // Get pixels by R, G, B
                alpha = new Color(original.getRGB (i, j)).getAlpha();
                red = new Color(original.getRGB (i, j)).getRed();
                green = new Color(original.getRGB (i, j)).getGreen();
                blue = new Color(original.getRGB (i, j)).getBlue();
 
                // Set new pixel values using the histogram lookup table
                red = histLUT.get(0)[red];
                green = histLUT.get(1)[green];
                blue = histLUT.get(2)[blue];
 
                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);
 
                // Write pixels into image
                histogramEQ.setRGB(i, j, newPixel);
 
            }
        }
        System.err.println("Equalization Process in ns: " + (System.nanoTime() - startTime2));
 
        return histogramEQ;
 
	}
	
	private static BufferedImage histogramEqualizationPrecomputedLUT(BufferedImage original, ArrayList<int[]> histLUT) {
		 
        int red;
        int green;
        int blue;
        int alpha;
        int newPixel = 0;
        
        BufferedImage histogramEQ = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        
        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {
 
                // Get pixels by R, G, B
                alpha = new Color(original.getRGB (i, j)).getAlpha();
                red = new Color(original.getRGB (i, j)).getRed();
                green = new Color(original.getRGB (i, j)).getGreen();
                blue = new Color(original.getRGB (i, j)).getBlue();
 
                // Set new pixel values using the histogram lookup table
                red = histLUT.get(0)[red];
                green = histLUT.get(1)[green];
                blue = histLUT.get(2)[blue];
 
                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);
 
                // Write pixels into image
                histogramEQ.setRGB(i, j, newPixel);
 
            }
        }
 
        return histogramEQ;
 
	}
 
    private static BufferedImage histogramEqualization(BufferedImage original) {
 
        int red;
        int green;
        int blue;
        int alpha;
        int newPixel = 0;
 
        // Get the Lookup table for histogram equalization
		final long startTime = System.nanoTime();
        ArrayList<int[]> histLUT = histogramEqualizationLUT(original);
		System.err.println("LUT Time in ns: " + (System.nanoTime() - startTime));
 
        BufferedImage histogramEQ = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
 
        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {
 
                // Get pixels by R, G, B
                alpha = new Color(original.getRGB (i, j)).getAlpha();
                red = new Color(original.getRGB (i, j)).getRed();
                green = new Color(original.getRGB (i, j)).getGreen();
                blue = new Color(original.getRGB (i, j)).getBlue();
 
                // Set new pixel values using the histogram lookup table
                red = histLUT.get(0)[red];
                green = histLUT.get(1)[green];
                blue = histLUT.get(2)[blue];
 
                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);
 
                // Write pixels into image
                histogramEQ.setRGB(i, j, newPixel);
 
            }
        }
 
        return histogramEQ;
 
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
 
    // Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha; newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
 
}
