package btaw.shared.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Window;

public class KM_Graph implements Serializable{

	private List<String> lineX;
	private List<String> lineY;
	private List<String> censX;
	private List<String> censY;
	private List<String> atRisk;
	private int n;

	public KM_Graph(){
		
	}
	
	public KM_Graph(List<String> lineX, List<String> lineY, List<String> censX, List<String> censY){
		this.lineX = lineX;
		this.lineY = lineY;
		this.censX = censX;
		this.censY = censY;
	}
	
	public KM_Graph(List<String> lineX, List<String> lineY, List<String> censX, List<String> censY, List<String> atRisk, int n){
		this.lineX = lineX;
		this.lineY = lineY;
		this.censX = censX;
		this.censY = censY;
		this.atRisk = atRisk;
		this.n = n;
	}

	public List<String> getlineX(){
		return lineX;
	}
	
	public List<String> getlineY(){
		return lineY;
	}
	
	public List<String> getcensX(){
		return censX;
	}
	
	public List<String> getcensY(){
		return censY;
	}
	
	public List<String> getAtRisk(){
		return atRisk;
	}
	
	public int getN(){
		return n;
	}

}
