package btaw.client.modules.login;

import btaw.client.event.LoginEvent;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

//http://code.google.com/p/google-web-toolkit-incubator/wiki/LoginSecurityFAQ
public class LoginPresenter extends Presenter {
	public interface View extends IView {
		Button getLoginButton();

		TextBox getUserField();

		PasswordTextBox getPasswordField();
	}

	private final View ui;

	public LoginPresenter(Presenter parent, View view) {
		super(parent);
		this.ui = view;

	}

	@Override
	protected void bindAll() {
		ui.getPasswordField().addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getCharCode() == KeyCodes.KEY_ENTER){
					ui.getLoginButton().click();
					//doLoginEvent();
				}

			}
		});

		ui.getLoginButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doLoginEvent();
			}
		});

	}

	private void doLoginEvent() {
		
		String username = ui.getUserField().getValue();
		String password = ui.getPasswordField().getValue();
		fireEvent(new LoginEvent(username, password));
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}

	@Override
	public void activated(HasWidgets container) {

	}
}
