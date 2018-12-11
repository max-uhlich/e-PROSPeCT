package btaw.client.event.handlers;

import btaw.client.event.ClearEvent;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface ClearEventHandler extends EventHandler{

	public static Type<ClearEventHandler> TYPE = new Type<ClearEventHandler>();
	public void onClearEvent(ClearEvent e);
}
