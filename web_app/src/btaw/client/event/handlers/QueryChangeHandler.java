package btaw.client.event.handlers;

import btaw.client.event.QueryChangedEvent;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface QueryChangeHandler extends EventHandler{
	public static Type<QueryChangeHandler> TYPE = new Type<QueryChangeHandler>();
	void OnQueryChangedEvent(QueryChangedEvent e) ;
}
