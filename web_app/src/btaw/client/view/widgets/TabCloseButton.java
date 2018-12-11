package btaw.client.view.widgets;

import btaw.client.modules.tab.KaplanMeierTab;
import btaw.client.modules.tab.Tab;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class TabCloseButton extends PushButton {

	private Tab tabRef;
	
	public TabCloseButton(Image im, Tab T){
		//super("\u2715");
		super(im);
		//super(new Image("images/max.png"));
		//im.setSize("5%", "5%");
		//this.setSize("10px", "10px");
		this.getElement().getStyle().setFontSize(8, Unit.PT);
		this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		this.setStyleName("gwt-TabButton");
		this.setTitle("Close");
		this.tabRef = T;
	}
	
	public Tab getRef(){
		return this.tabRef;
	}
	
}
