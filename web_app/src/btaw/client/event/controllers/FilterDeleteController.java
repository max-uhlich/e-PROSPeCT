
package btaw.client.event.controllers;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import btaw.client.event.handlers.HasFilterDeleteHandlers;
import btaw.client.event.handlers.FilterDeleteHandler;
import btaw.client.view.filter.FilterPanel;

public class FilterDeleteController implements HasFilterDeleteHandlers{

	private HandlerManager handlers;
		
	public FilterDeleteController(FilterPanel source)
	{
		handlers = new HandlerManager(source);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlers.fireEvent(event);	
	}

	@Override
	public HandlerRegistration addFilterDeleteHandler(FilterDeleteHandler handle) {
		return handlers.addHandler(FilterDeleteHandler.TYPE, handle);
	}

}
