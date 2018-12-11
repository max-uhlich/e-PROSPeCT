
package btaw.client.event.controllers;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import btaw.client.event.handlers.AggregateDeleteHandler;
import btaw.client.event.handlers.HasAggregateDeleteHandlers;
import btaw.client.view.filter.AggregatePanel;

public class AggregateDeleteController implements HasAggregateDeleteHandlers{

	private HandlerManager handlers;
		
	public AggregateDeleteController(AggregatePanel source)
	{
		handlers = new HandlerManager(source);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlers.fireEvent(event);	
	}

	@Override
	public HandlerRegistration addAggregateDeleteHandler(AggregateDeleteHandler handle) {
		return handlers.addHandler(AggregateDeleteHandler.TYPE, handle);
	}

}
