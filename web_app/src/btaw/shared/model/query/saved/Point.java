package btaw.shared.model.query.saved;

import java.io.Serializable;

public class Point implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 784199411330774795L;
	public Integer x, y, z, r;
	public Point() {
		
	}
	
	public Point(Integer x, Integer y) {
		this.x = x;
		this.y = y;
		this.z = -1;
	}
	
	public Point(Integer x, Integer y, Integer z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = -1;
	}
	
	public Point(Integer x, Integer y, Integer z, Integer r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
	}
};
