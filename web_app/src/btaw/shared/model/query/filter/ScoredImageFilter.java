package btaw.shared.model.query.filter;

import java.util.HashMap;

import btaw.shared.model.BTAWDatabaseException;

public abstract class ScoredImageFilter extends ImageFilter {

	private static final long serialVersionUID = -2863379372753416670L;
	protected HashMap<Integer, ImageRegion> tumourSlices;
	protected HashMap<Integer, ImageRegion> edemaSlices;
	private int index;
	
	public ScoredImageFilter() {
		tumourSlices = new HashMap<Integer, ImageRegion>();
		edemaSlices = new HashMap<Integer, ImageRegion>();
		index = -1;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void addTumourRegion(ImageRegion slice) {
		tumourSlices.put(new Integer(slice.getSliceNumber()), slice);
	}
	
	public void addEdemaRegion(ImageRegion slice) {
		edemaSlices.put(new Integer(slice.getSliceNumber()), slice);
	}
	
	public HashMap<Integer, ImageRegion> getTumourRegions() {
		return tumourSlices;
	}
	
	public HashMap<Integer, ImageRegion> getEdemaRegions() {
		return edemaSlices;
	}

	public class ImageRegion {
		private static final int maxX = 255;
		private static final int maxY = 255;
		private static final int maxZ = 255;
		
		private int zValue;
		private boolean[][] regionMap;
		
		public ImageRegion(int zValue) throws BTAWDatabaseException {
			validateSliceRange(zValue);
			this.zValue = zValue;
			regionMap = new boolean[maxX][maxY];
		}
		
		public int getMaxSliceNumber() {
			return maxZ;
		}
		
		public int getMaxXValue() {
			return maxX;
		}
		
		public int getMaxYValue() {
			return maxY;
		}
		
		public void setSliceNumber(int zValue) throws BTAWDatabaseException {
			validateSliceRange(zValue);
			this.zValue = zValue;
		}
		
		public int getSliceNumber() {
			return zValue;
		}
		
		public void setInteresting(int xValue, int yValue, boolean interesting) throws BTAWDatabaseException {
			validateRange(xValue, yValue);
			regionMap[xValue][yValue] = interesting;
		}
		
		public boolean interesting(int xValue, int yValue) throws BTAWDatabaseException {
			validateRange(xValue, yValue);
			return regionMap[xValue][yValue];
		}
		
		public void validateRange(int xValue, int yValue) throws BTAWDatabaseException {
			if ((xValue < 0 ) || (xValue >= maxX) || (yValue < 0 ) || (yValue >= maxY)) {
				throw new BTAWDatabaseException("Pixel location out of range.");
			}
		}
		
		public void validateSliceRange(int zValue) throws BTAWDatabaseException {
			if ((zValue < 0) || (zValue >= maxZ)) {
				throw new BTAWDatabaseException("Invalid z-value.");
			}
		}
	}
}