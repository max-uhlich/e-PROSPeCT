package btaw.client.event;

import btaw.client.event.handlers.LoginEventHandler;

import com.google.gwt.event.shared.GwtEvent;


public class LoginEvent extends GwtEvent<LoginEventHandler> {
	public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	private String username;
	private String password;
	  
	/**
	 * LoginEvent thrown when logging into app
	 * @param username
	 * @param password
	 */
	public LoginEvent(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Gets the username
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the password
	 * @return
	 */
	public String getPassword() {
		return password;
	}


	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLogin(this);
		
	}

}
