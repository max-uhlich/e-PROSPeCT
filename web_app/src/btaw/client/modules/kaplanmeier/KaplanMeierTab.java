package btaw.client.modules.kaplanmeier;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KaplanMeierTab {

	private Image KMChart;
	private VerticalPanel PStats;
	private HorizontalPanel HPanel;
	
	public KaplanMeierTab(){
		this.KMChart = new Image();
		this.PStats = new VerticalPanel();
		this.HPanel = new HorizontalPanel();
	}
	
	public void setURL(String url){
		this.KMChart.setUrl(url);
	}
	
	public Image getKMChart(){
		return this.KMChart;
	}
	
	public VerticalPanel getPStats(){
		return this.PStats;
	}
	
	public HorizontalPanel getHPanel(){
		return this.HPanel;
	}
	
}
