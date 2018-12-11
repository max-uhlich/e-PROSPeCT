package btaw.shared.model.query.saved;

import java.io.Serializable;
import java.util.Date;

public class SavedQueryDisplayData implements Serializable {

	private static final long serialVersionUID = -1594241391904439659L;
	private String name = null;
	private String s_date = null;
	private Integer savedQueryID = null;
	private Date j_date;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return s_date;
	}
	public void setDate(String date) {
		this.s_date = date;
	}
	public Integer getSavedQueryID() {
		return savedQueryID;
	}
	public void setSavedQueryID(Integer savedQueryID) {
		this.savedQueryID = savedQueryID;
	}
	public Date getJavaDate() {
		return j_date;
	}
	public void setDate(Date date) {
		this.j_date = date;
	}
}
