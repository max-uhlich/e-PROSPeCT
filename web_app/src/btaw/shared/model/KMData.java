package btaw.shared.model;

import java.io.Serializable;

public class KMData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5828688832931877732L;
	private String pid;
	private String lifeSpan;
	private String lifeSpanAlt;
	private String startDate;
	
	public KMData() {
		
	}

	public KMData(String startDate, String pid, String lifeSpan, String lifeSpanAlt) {
		this.startDate = startDate;
		this.pid = pid;
		this.lifeSpan = lifeSpan;
		this.lifeSpanAlt = lifeSpanAlt;
	}
	
	public KMData(String pid, String lifeSpan, String lifeSpanAlt) {
		this.pid = pid;
		this.lifeSpan = lifeSpan;
		this.lifeSpanAlt = lifeSpanAlt;
	}
	public String getPid() {
		return pid;
	}
	
	public String getStartDate() {
		return this.startDate;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(String lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public String getLifeSpanAlt() {
		return lifeSpanAlt;
	}

	public void setLifeSpanAlt(String lifeSpanAlt) {
		this.lifeSpanAlt = lifeSpanAlt;
	}
	
	public void setNullVals(String lifeSpan, String lifeSpanAlt){
	    if (this.lifeSpan == null)
	        this.lifeSpan = lifeSpan;
	    if (this.lifeSpanAlt == null)
	        this.lifeSpanAlt = lifeSpanAlt;
	}
	
	public void null_negatives(){
		if (this.lifeSpan != null)
			if (this.lifeSpan.contains("-")) this.lifeSpan = null;
		if (this.lifeSpanAlt != null)
			if (this.lifeSpanAlt.contains("-")) this.lifeSpanAlt = null;
	}
	
	public void conform(){
	    if (this.lifeSpan != null){
	        this.lifeSpanAlt = null;
	    }
	}

}
