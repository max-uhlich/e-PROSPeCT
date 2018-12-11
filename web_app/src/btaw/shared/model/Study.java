package btaw.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class Study implements Serializable, Comparable<Study> {

	private static final long serialVersionUID = -1175568595990098666L;
	private Date date;
	private String id;
	private LinkedHashMap<String, ImageType> types = new LinkedHashMap<String, ImageType>();
	public Study(){
		
	}
	public Study(String studyId, java.util.Date date){
		this.id=studyId;
		this.date= date;
	}
	public String getId(){
		return this.id;
	}
	public Date getDate(){
		return this.date;
	}
	public void addImageType(ImageType imageType) {
		this.types.put(imageType.getDescription(), imageType);
	}
	public List<ImageType> getTypes(){
		List<ImageType> ret = new ArrayList<ImageType>();
		
		for(Map.Entry<String, ImageType> entry : types.entrySet()) {
			ret.add(entry.getValue());
		}
		
		return ret;
	}
	public ImageType getType(String description) {
		return this.types.get(description);
	}
	@Override
	public int compareTo(Study o) {
		return this.getDate().compareTo(o.getDate());
	}
}
