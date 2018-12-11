package btaw.client;

import btaw.client.framework.AppController;
import btaw.client.framework.EventBus;
import btaw.client.rpc.ModelService;
import btaw.client.rpc.ModelServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class BTAW_GUI implements EntryPoint {

	private static ModelServiceAsync rpcService;
	
	public void onModuleLoad() {
		rpcService = getRpcService();
		
		HandlerManager eventBus = EventBus.getInstance();
		AppController appViewer = new AppController(rpcService, eventBus);
		appViewer.setActive(RootLayoutPanel.get());
	}

	public static ModelServiceAsync getRpcService() {
		if (rpcService == null)
			rpcService = GWT.create(ModelService.class);
		return rpcService;
	}
}
