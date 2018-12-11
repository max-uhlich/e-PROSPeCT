package btaw.shared.model.query.saved;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTPWorkaround implements Serializable, IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4174565224344231380L;
	private String x_value = "";
	private String y1_value = "";
	private String y2_value = "";
	private String z_value = "";
	
	public GWTPWorkaround() {
	}
	
	public GWTPWorkaround(String y1, String y2, String x, String z) {
		this.y1_value = y1;
		this.y2_value = y2;
		this.x_value = x;
		this.z_value = z;
	}

	public String getX_value() {
		return x_value;
	}

	public String getY1_value() {
		return y1_value;
	}
	
	public String getY2_value() {
		return y2_value;
	}

	public String getZ_value() {
		return z_value;
	}
}
