
package btaw.client.event.handlers;

import btaw.client.event.FilterDeleteEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface FilterDeleteHandler extends EventHandler{
	public static Type<FilterDeleteHandler> TYPE = new Type<FilterDeleteHandler>();
	public void OnFilterDeleteEvent(FilterDeleteEvent e) ;
}
