package btaw.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageType implements Serializable{
	private static final long serialVersionUID = -8620422983486576117L;
	private String id;
	private String description;
	private ArrayList<String> images;
	
	public ImageType(){
		
	}
	
	public ImageType(String id, String description){
		this.id=id;
		this.description=description;
	}
	public String getId(){
		return id;
	}
	public String getDescription(){
		return this.description;
	}

	public void addImageId(String id) {
		if (images == null)
			images = new ArrayList<String>();
		images.add(id);
	}
	
	public List<String> getIds(){
		return images;
	}
}
