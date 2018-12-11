
package btaw.client.event.handlers;

import btaw.client.event.AggregateDeleteEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface AggregateDeleteHandler extends EventHandler{
	public static Type<AggregateDeleteHandler> TYPE = new Type<AggregateDeleteHandler>();
	public void OnAggregateDeleteEvent(AggregateDeleteEvent e) ;
}
