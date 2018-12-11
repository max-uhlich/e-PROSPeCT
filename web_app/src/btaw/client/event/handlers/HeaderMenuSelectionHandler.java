package btaw.client.event.handlers;

import btaw.client.event.HeaderMenuSelectionEvent;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface HeaderMenuSelectionHandler extends EventHandler{
	public static Type<HeaderMenuSelectionHandler> TYPE = new Type<HeaderMenuSelectionHandler>();
	void OnHeaderMenuSelectionEvent(HeaderMenuSelectionEvent e) ;
}
