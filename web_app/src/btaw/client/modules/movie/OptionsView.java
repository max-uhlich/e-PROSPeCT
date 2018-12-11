package btaw.client.modules.movie;

import java.util.List;

import btaw.client.framework.AView;
import btaw.client.modules.movie.OptionsPresenter.View;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OptionsView extends AView implements View {
	private DialogBox main;
	private VerticalPanel containerPanel;
	
	private VerticalPanel usernamePanel;
	private VerticalPanel categoryPanel;
	
	private Button cancel;
	private Button done;
	
	public OptionsView() {
		main = new DialogBox();
		main.getElement().getStyle().setZIndex(255);
		containerPanel = new VerticalPanel();
		containerPanel.setWidth("100%");
		containerPanel.setHeight("100%");
		
		usernamePanel = new VerticalPanel();
		categoryPanel = new VerticalPanel();
		
		HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("Cancel");
		done = new Button("Done");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(done);
		alignPanel.add(cancel);
		
		//containerPanel.setSpacing(15);
		containerPanel.add(new Label("Please check the users you wish to see:"));
		containerPanel.add(new HTML("<hr/>"));
		containerPanel.add(usernamePanel);
		containerPanel.add(new Label("Please check the categories you wish to see:"));
		containerPanel.add(new HTML("<hr/>"));
		containerPanel.add(categoryPanel);
		containerPanel.add(bottom);
		main.add(new ScrollPanel(containerPanel));
		main.setText("Options");
		main.setModal(true);
		main.center();
		
	}
	
	@Override
	public void populateUserList(List<String> usernames) {
		for(String s : usernames) {
			CheckBox cb = new CheckBox(s);
			cb.setChecked(true);
			usernamePanel.add(cb);
		}
	}
	
	@Override
	public void populateCategoryList(List<String> categories) {
		for(String s : categories) {
			CheckBox cb = new CheckBox(s);
			cb.setChecked(true);
			categoryPanel.add(cb);
		}
	}
	@Override
	public void close() {
		main.hide();
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	
	public Button getDoneButton() {
		return this.done;
	}
	
	public VerticalPanel getUsernamePanel() {
		return this.usernamePanel;
	}
	
	public VerticalPanel getCategoryPanel() {
		return this.categoryPanel;
	}
}
