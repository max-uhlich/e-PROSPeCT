package btaw.shared.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Window;

public class PSA_Graph implements Serializable{

	private List<String> dates;
	private List<String> psa_vals;
	private String center_date;
	//private JsArrayString dates;
	//private JsArrayNumber psa_vals;
	
	public PSA_Graph(){
		
	}
	public PSA_Graph(List<String> dates, List<String> psa_vals){
		this.dates = dates;
		this.psa_vals = psa_vals;
	}

	public List<String> getDates(){
		return dates;
	}
	
	public List<String> getVals(){
		return psa_vals;
	}
	
	public void setCenterDate(String center_date) {
		this.center_date = center_date;
	}
	
	public String getImplantDate(){
		if(this.center_date!=null)
				return this.center_date;
		else
			return "-1";
	}

}
