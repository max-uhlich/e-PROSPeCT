package btaw.client.modules.popups;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.popups.PermissionPresenter.View;

public class PermissionView extends AView implements View {
	
	private DialogBox main;
	private Button close;
	
	public PermissionView(String name) {
		main = new DialogBox();
		
		VerticalPanel containerPanel = new VerticalPanel();
		containerPanel.setSize("100%", "100%");
		containerPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		containerPanel.add(new Label("User '" + name + "' does not have access to this feature."));
		
		HorizontalPanel bottom = new HorizontalPanel();
		close = new Button("Close");
		bottom.add(close);
		
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("Permission Denied");
		main.setModal(true);
		main.center();
	}
	
	public Button getClose() {
		return close;
	}
	public void close() {
		main.hide();
	}

}
