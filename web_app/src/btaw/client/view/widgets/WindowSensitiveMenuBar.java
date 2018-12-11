package btaw.client.view.widgets;

import com.google.gwt.user.client.DOM;

public class WindowSensitiveMenuBar extends com.google.gwt.user.client.ui.MenuBar {
	public WindowSensitiveMenuBar(boolean b) {
		super(b);
	}
	public void onAttach(){
		DOM.setStyleAttribute(this.getElement(), "left", "100px");
		super.onAttach();
	}
	public int getAbsoluteLeft(){
		return 0;
	}
}
