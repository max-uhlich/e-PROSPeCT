package btaw.shared.model.query.filter;

public class CenterMassImageFilter extends ImageFilter {
	private static final long serialVersionUID = 7526862837034338043L;
	
	//FIXME this is a rough estimate...
	/*A ratio of pixels to centimeters (centimeters per pixel)*/
	private static final double RATIO = 0.09039548022598870056497175141243;


	private double xCm;
	private double yCm;
	private int zSlice;

	private double distance;

	public CenterMassImageFilter() {
		
	}

	public double getXCm() {
		return xCm;
	}

	public void setXCm(double x) {
		this.xCm = x;
	}
	
	public void setYCm(double y) {
		this.yCm = y;
	}
	
	public void setXPixels(double x) {
		this.xCm = x*RATIO;
	}

	public double getYCm() {
		return yCm;
	}

	public void setYPixels(double y) {
		this.yCm = y*RATIO;
	}

	public int getzSlice() {
		return zSlice;
	}

	public void setzSlice(int zSlice) {
		this.zSlice = zSlice;
	}

	@Override
	public String toSQLString() {
		String sql = "study_c_mass_within(btaw.person.pid,"+ this.regionId + "," +
		distance*10 +","+ xCm*10 +","+ yCm*10 +","+ 6.0*zSlice +")";
		//String sql = "SELECT study_c_mass_within(b.study_id,"+ this.regionId + "," + distance*10 +","+ xCm*10 +","+ yCm*10 +","+ 6.0*zSlice +") from btap_roi_centerofmass b";
		// "EXISTS (SELECT * FROM (SELECT * FROM BTAW.BTAP_STUDY s WHERE s.pid = btaw.person.pid) AS ps WHERE study_c_mass_within(ps.study_id," + this.regionId + "," + distance*10 +","+ xCm*10 +","+ yCm*10 +","+ 6.0*zSlice + "))";
		System.err.println("Center of Mass sql! " + sql);
		return sql; 
	}

	public void setDistancePixels(int r) {
		this.distance = r*RATIO;
	}

	public double getDistance() {
		return this.distance;
	}

}
