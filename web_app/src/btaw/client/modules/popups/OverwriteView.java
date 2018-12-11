package btaw.client.modules.popups;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.popups.OverwritePresenter.View;

public class OverwriteView extends AView implements View {
	
	private DialogBox main;
	private Button yes;
	private Button no;
	
	public OverwriteView(String name) {
		main = new DialogBox();
		
		VerticalPanel containerPanel = new VerticalPanel();
		containerPanel.setSize("100%", "100%");
		containerPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		containerPanel.add(new Label("You're about to overwrite definition "+name+" are you sure?"));
		
		HorizontalPanel bottom = new HorizontalPanel();
		yes = new Button("Yes, Overwrite!");
		no = new Button("Nope");
		bottom.add(yes);
		bottom.add(no);
		
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("Are you sure?");
		main.setModal(true);
		main.center();
	}
	
	public Button getYes() {
		return yes;
	}
	public Button getNo() {
		return no;
	}
	public void close() {
		main.hide();
	}

}
