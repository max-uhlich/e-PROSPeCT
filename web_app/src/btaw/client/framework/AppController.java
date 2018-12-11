package btaw.client.framework;

import java.util.HashMap;

import btaw.client.classes.ErrorPopupPanel;
import btaw.client.event.LoginEvent;
import btaw.client.event.handlers.LoginEventHandler;
import btaw.client.modules.login.LoginPresenter;
import btaw.client.modules.login.LoginView;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.main.MainView;
import btaw.client.rpc.ModelServiceAsync;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController extends Presenter implements
		ValueChangeHandler<String> {

	private HasWidgets container;
	private final Page loginPage;
	private final Page mainPage;

	private static HashMap<String, Page> pages;

	public AppController(ModelServiceAsync rpcService, HandlerManager eventBus) {
		super(rpcService, eventBus);
		mainPage = new Page("main",
				new MainPresenter(this, new MainView()),
				true, null, null);
		loginPage = new Page("login",
				new LoginPresenter(this, new LoginView()),
				false, null, null);
		
		loginPage.setRedirectPage(mainPage);
		mainPage.setLoginPage(loginPage);
		
		registerPage(loginPage);
		registerPage(mainPage);

		checkAuth();

	}

	protected void setUserParameters() {
		
		rpcService.getUserParameters(new PresenterCallback<HashMap<String, String>>(this) {
					@Override
					protected void success(HashMap<String, String> parameters) {
						setUserParameters(parameters);
					}
				});
		
	}
	
	protected void checkAuth() {
		
		rpcService.isSessionValid(
				new PresenterCallback<Boolean>(this) {
					@Override
					protected void success(Boolean valid) {
						setAuthenticated(valid);
					}
				});
		
	}
	
	@Override
	protected void bindAll() {

		History.addValueChangeHandler(this);
		bindEvent(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onLogin(LoginEvent e) {
				doLogin(e.getUsername(), e.getPassword());
			}

		});
	}

	private void registerPage(Page page) {
		if (pages == null)
			pages = new HashMap<String, Page>();
		pages.put(page.getToken(), page);
	}

	@Override
	protected void activated(final HasWidgets container) {
		checkAuth();
		AppController.this.container = container;
		if (History.getToken().isEmpty()) {
			loginPage.go();
		} else {
			History.fireCurrentHistoryState();
		}
	}

	protected void doLogin(String name, String password) {

		rpcService.login(name, password, new PresenterCallback<Boolean>(this) {
			@Override
			protected void success(Boolean loggedIn) {
				if (loggedIn) {
					setAuthenticated(true);
					setUserParameters();
					mainPage.go();
				} else {
					AppController.this.authenticationFailed();

				}

			}

		});
	}

	@Override
	public void error(Throwable caught) {
		caught.printStackTrace();
		String message = caught.getMessage();
		if (message != null && !message.isEmpty() && ! message.trim().equals("0")){
			ErrorPopupPanel popup = new ErrorPopupPanel("Error!", message, "#FFFFFF");
			popup.center();
		}
	}

	@Override
	public void authenticationFailed() {
		this.error(new Exception("Login Failed"));
		setAuthenticated(false);
		mainPage.setActive(container);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		Page newPage = pages.get(token);
		if (newPage != null) {
			newPage.setActive(container);
		}
	}

	@Override
	protected IView getDisplay() {
		return null;
	}
	
	protected void logout(){
//		System.err.println("Executing AppController logout method");
		this.loginPage.go();
		this.loginPage.reload();
	}

}
