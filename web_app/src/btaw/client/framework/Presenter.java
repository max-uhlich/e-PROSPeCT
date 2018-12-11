package btaw.client.framework;

import java.io.BufferedWriter;
import java.util.HashMap;

import btaw.client.modules.filter.FilterPresenter;
import btaw.client.rpc.ModelServiceAsync;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

public abstract class Presenter {
	protected final ModelServiceAsync rpcService;
	private final HandlerManager eventBus;
	public Presenter parent;
	private boolean authenticated;
	private String username;
	private String access;
	private String logFile;

	public Presenter(ModelServiceAsync rpcService, HandlerManager eventBus) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
	}

	public Presenter(Presenter parent) {
		this.rpcService = parent.rpcService;
		this.eventBus = parent.eventBus;
		this.parent = parent;
	}

	protected <H extends EventHandler> void bindEvent(Type<H> type, H handler) {
		eventBus.addHandler(type, handler);
	}

	protected void fireEvent(GwtEvent<?> event) {
		eventBus.fireEvent(event);
	}

	protected abstract void bindAll();

	public void setActive(final HasWidgets container) {
		bindAll();
		container.clear();
		IView view = getDisplay();
		if (view != null)
			container.add(view.getRootWidget());
		activated(container);
		
	}
	public void setActive(){
		bindAll();
		activated(null);
	}

	protected abstract IView getDisplay();

	protected abstract void activated(final HasWidgets container);

	public void error(Throwable caught) {
		if (parent != null)
			parent.error(caught);
	}

	public void authenticationFailed() {
		if (parent != null)
			parent.authenticationFailed();
	}

	public String getUsername(){
		if (parent != null)
			return parent.getUsername();
		return username;
	}
	
	public String getAccess(){
		if (parent != null)
			return parent.getAccess();
		return access;
	}
	
	protected boolean isAuthenticated(){
		if (parent != null)
			return parent.isAuthenticated();
		return authenticated;
	}
	
	protected void writeToLog(String statement){
		//Window.alert("Logging something");
		rpcService.initializeLog(statement, new PresenterCallback<String>(Presenter.this) {

			@Override
			protected void success(String result) {
				//Window.alert("Just logged a timestamp into: " + result);
				logFile = result;
			}
			
		});
	}
	
	protected void setUserParameters(HashMap<String,String> parameters){
		if (parent != null)
			 parent.setUserParameters(parameters);
		else{
			this.username = parameters.get("username");
			this.access = parameters.get("access");
			writeToLog("\ntimestamp " + this.access + " user " + this.username + " successfully logged in.");
		}
		
	}
	
	protected void setAuthenticated(boolean authenticated){
		if (parent != null)
			 parent.setAuthenticated(authenticated);
		else
			this.authenticated = authenticated;
	}
	
	protected void logout() {
//		System.err.println("Executing Presenter class logout");
		if (parent != null)
			parent.logout();
	}

	public ModelServiceAsync getRpcService() {
		return this.rpcService;
	}
	
}
