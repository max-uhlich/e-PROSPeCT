package btaw.client.event.handlers;

import btaw.client.event.LoginEvent;

import com.google.gwt.event.shared.EventHandler;

public interface LoginEventHandler extends EventHandler {
	void onLogin(LoginEvent e);
}
