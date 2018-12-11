package btaw.client.event.controllers;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import btaw.client.event.handlers.HasHeaderMenuSelectionHandler;
import btaw.client.event.handlers.HeaderMenuSelectionHandler;
import btaw.client.framework.Presenter;

public class HeaderMenuSelectionController implements HasHeaderMenuSelectionHandler{

	
	private HandlerManager handlers;
	
	public HeaderMenuSelectionController(Presenter source)
	{
		handlers = new HandlerManager(source);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlers.fireEvent(event);
		
	}
	
	@Override
	public HandlerRegistration addHeaderMenuSelectionHandler(HeaderMenuSelectionHandler handle) {
		return handlers.addHandler(HeaderMenuSelectionHandler.TYPE, handle);
	}

}
