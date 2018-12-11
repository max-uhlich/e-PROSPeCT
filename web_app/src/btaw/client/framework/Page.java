package btaw.client.framework;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

public class Page {

	private String token;
	private Presenter presenter;
	private boolean secured;
	private Page loginPage;
	private Page redirectPage;



	public Page(String token, Presenter presenter, boolean secured, Page loginPage, Page redirectPage) {
		this.token = token;
		this.presenter = presenter;
		this.secured = secured;
		
		if (redirectPage == null)
			this.redirectPage = this;
		else
			this.redirectPage = redirectPage;
		
		if (loginPage == null)
			this.loginPage = this;
		else
			this.loginPage = loginPage;
	}
	

	public Presenter getPresenter(){
		return presenter;
	}

	public String getToken() {
		return token;
	}

	public void go() {
		go(true);
		
	}
	
	public void go(boolean b) {
		History.newItem(token,b);
	}

	public void setActive(final HasWidgets container) {
			presenter.rpcService.isSessionValid(new PresenterCallback<Boolean>(presenter) {
				@Override
				protected void success(Boolean valid) {
					if (secured){
						if (valid){
							presenter.setActive(container);
						} else {
							loginPage.go(false);
						}
					} else if (valid && Page.this == loginPage){
						redirectPage.go(false);
					} else {
						presenter.setActive(container);
					}
					
				}
			});
		
	}



	public void setLoginPage(Page loginPage) {
		this.loginPage = loginPage;
	}

	public void setRedirectPage(Page redirectPage) {
		this.redirectPage = redirectPage;
	}
	
	public native void reload() /*-{ 
    	$wnd.location.reload(); 
   	}-*/; 
}
