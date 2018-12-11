
package btaw.client.event.controllers;

import btaw.client.event.handlers.GroupByDeleteHandler;
import btaw.client.event.handlers.HasGroupByDeleteHandlers;
import btaw.client.view.filter.GroupByPanel;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class GroupByDeleteController implements HasGroupByDeleteHandlers{

	private HandlerManager handlers;
		
	public GroupByDeleteController(GroupByPanel source)
	{
		handlers = new HandlerManager(source);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlers.fireEvent(event);	
	}

	@Override
	public HandlerRegistration addGroupByDeleteHandler(GroupByDeleteHandler handle) {
		return handlers.addHandler(GroupByDeleteHandler.TYPE, handle);
	}

}
