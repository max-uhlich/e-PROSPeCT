package btaw.client.modules.popups;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.popups.PasswordChangePresenter.View;

public class PasswordChangeView extends AView implements View {
	
	private DialogBox main;
	private Button OK;
	private Button Cancel;
	
	private final PasswordTextBox cur_pass_field;
	private final PasswordTextBox new_pass_field;
	private final PasswordTextBox retype_pass_field;
	
	public PasswordChangeView(String name) {
		main = new DialogBox();
		
		VerticalPanel containerPanel = new VerticalPanel();
		containerPanel.setSize("100%", "100%");
		containerPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		//containerPanel.add(new Label("Make sure to choose a strong password."));
		
		Label cur_pass_label = new Label("Current Password:");
		cur_pass_label.getElement().setClassName("field-label");
		Label new_pass_label = new Label("New Password:");
		new_pass_label.getElement().setClassName("field-label");
		Label retype_pass_label = new Label("Retype New Password:");
		retype_pass_label.getElement().setClassName("field-label");
		
		cur_pass_field = new PasswordTextBox();
		cur_pass_field.setWidth("150px");
		cur_pass_field.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(new_pass_field.getText().equals(retype_pass_field.getText()) & !new_pass_field.getText().equals("") & !cur_pass_field.getText().equals("")){
					OK.setEnabled(true);
				} else {
					OK.setEnabled(false);
				}
			}
		});
		
		new_pass_field = new PasswordTextBox();
		new_pass_field.setWidth("150px");
		new_pass_field.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(new_pass_field.getText().equals(retype_pass_field.getText()) & !new_pass_field.getText().equals("") & !cur_pass_field.getText().equals("")){
					OK.setEnabled(true);
				} else {
					OK.setEnabled(false);
				}
			}
		});
		
		retype_pass_field = new PasswordTextBox();
		retype_pass_field.setWidth("150px");
		retype_pass_field.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(retype_pass_field.getText().equals(new_pass_field.getText()) & !retype_pass_field.getText().equals("") & !cur_pass_field.getText().equals("")){
					OK.setEnabled(true);
				} else {
					OK.setEnabled(false);
				}
			}
		});
		
		HorizontalPanel cur_pass_panel = new HorizontalPanel();
		cur_pass_panel.setWidth("400px");
		cur_pass_panel.add(cur_pass_label);
		cur_pass_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cur_pass_panel.add(cur_pass_field);
		
		HorizontalPanel new_pass_panel = new HorizontalPanel();
		new_pass_panel.setWidth("400px");
		new_pass_panel.add(new_pass_label);
		new_pass_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		new_pass_panel.add(new_pass_field);
		
		HorizontalPanel retype_pass_panel = new HorizontalPanel();
		retype_pass_panel.setWidth("400px");
		retype_pass_panel.add(retype_pass_label);
		retype_pass_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		retype_pass_panel.add(retype_pass_field);
		
		HorizontalPanel bottom = new HorizontalPanel();
		OK = new Button("Change my password!");
		OK.setEnabled(false);
		Cancel = new Button("Cancel");
		bottom.add(OK);
		bottom.add(Cancel);
		
		containerPanel.add(cur_pass_panel);
		containerPanel.add(new_pass_panel);
		containerPanel.add(retype_pass_panel);
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("Change Password");
		main.setModal(true);
		main.center();
	}
	
	public String get_cur_pass(){
		return cur_pass_field.getText();
	}
	
	public String get_new_pass(){
		return new_pass_field.getText();
	}
	
	public Button getOK() {
		return OK;
	}
	public Button getCancel() {
		return Cancel;
	}
	public void close() {
		main.hide();
	}

}
