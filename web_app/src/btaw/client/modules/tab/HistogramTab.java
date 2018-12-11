package btaw.client.modules.tab;

import btaw.client.view.widgets.MagicTextColumn;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HistogramTab extends Tab{

	private Image Im;
	private TextBox binSize;
	private VerticalPanel PStats;
	private VerticalPanel outerStats;
	private MagicTextColumn source;
	
	public HistogramTab(){
		super();
		this.Im = new Image();
		this.binSize = new TextBox();
		this.PStats = new VerticalPanel();
		this.outerStats = new VerticalPanel();
	}
	
	public TextBox getBinSize(){
		return this.binSize;
	}
	
	public void setBarChart(){
		outerStats.setVisible(false);
	}
	
	public void setSource(MagicTextColumn src){
		this.source = src;
	}
	
	public MagicTextColumn getSource(){
		return this.source;
	}
	
	public VerticalPanel getPStats(){
		return this.PStats;
	}
	
	public VerticalPanel getOuterStats(){
		return this.outerStats;
	}
	
	public void setURL(String url){
		//Window.alert(url);
		this.Im.setUrl(url);
	}
	
	public Image getIm(){
		return this.Im;
	}
	
}
