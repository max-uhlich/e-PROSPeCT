package btaw.client.modules.login;

import btaw.client.framework.AView;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginView extends AView implements LoginPresenter.View {

	private final Button loginButton;
	private final TextBox userField;
	private final PasswordTextBox passwordField;
	private final VerticalPanel loginPanel;
	private final HorizontalPanel mainPanel;
	private final Widget titleLabel;

	public LoginView() {
		mainPanel = new HorizontalPanel();
		initWidget(mainPanel);
		loginPanel = new VerticalPanel();
		mainPanel.setWidth("100%");
		mainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		loginPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		loginPanel.getElement().setId("btaw-login-panel");
		mainPanel.add(loginPanel);

		titleLabel = new Label("PROSPeCT");
		titleLabel.getElement().setId("btaw-title");
		titleLabel.setTitle("Predictive Research Online System for Prostate Cancer Tasks");

		Label userLabel = new Label("Username:");
		//Label userLabel = new HTML("<b>Username:</b>");
		userLabel.getElement().setClassName("field-label");
		Label passLabel = new Label("Password:");
		//Label passLabel = new HTML("<br>Password:</b>");
		passLabel.getElement().setClassName("field-label");

		passwordField = new PasswordTextBox();
		passwordField.setWidth("150px");
		passwordField.getElement().setClassName("textBox_Box");
		userField = new TextBox();
		userField.setWidth("150px");
		userField.getElement().setClassName("textBox_Box2");
		//userField.getElement().setAttribute("margin-top", "5px");
		loginButton = new Button("Login");
		loginButton.getElement().setId("btaw-login-button");

		HorizontalPanel loginbuttonpanel = new HorizontalPanel();
		loginbuttonpanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		loginbuttonpanel.add(loginButton);
		
		HorizontalPanel utextpanel = new HorizontalPanel();
		HorizontalPanel ptextpanel = new HorizontalPanel();
		utextpanel.add(userField);
		ptextpanel.add(passwordField);
		
		VerticalPanel textBoxes = new VerticalPanel();
		textBoxes.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		textBoxes.add(userField);
		textBoxes.add(passwordField);
		textBoxes.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		textBoxes.add(loginButton);
		
		VerticalPanel labels = new VerticalPanel();
		labels.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		labels.add(userLabel);
		labels.add(passLabel);
		
		HorizontalPanel console_panel = new HorizontalPanel();
		console_panel.add(labels);
		console_panel.add(textBoxes);
		
		//HorizontalPanel upanel = new HorizontalPanel();
		//upanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		//upanel.add(userLabel);
		//upanel.add(utextpanel);
		//HorizontalPanel ppanel = new HorizontalPanel();
		//ppanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		//ppanel.add(passLabel);
		//ppanel.add(ptextpanel);

		HorizontalPanel wpanel = new HorizontalPanel();
		Label data_warning = new HTML("<center>Use of patient data should be in accordance <br>with the Alberta HIA and APCaRI policies. </br><br> Please note that all your actions using PROSPeCT <br>will be recorded as part of an audit trail used for <br>quality control and privacy regulations.</center>");
		
		wpanel.add(data_warning);
		data_warning.getElement().setId("btaw-data_warning");

		loginPanel.add(titleLabel);
		loginPanel.add(console_panel);
		//loginPanel.add(upanel);
		//loginPanel.add(ppanel);
		//loginPanel.add(loginbuttonpanel);
		loginPanel.add(wpanel);
		userField.setFocus(true);
	}

	@Override
	public Button getLoginButton() {
		return loginButton;
	}

	@Override
	public TextBox getUserField() {
		return userField;
	}

	@Override
	public PasswordTextBox getPasswordField() {
		return passwordField;
	}

}
