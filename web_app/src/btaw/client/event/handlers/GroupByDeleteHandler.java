
package btaw.client.event.handlers;

import btaw.client.event.GroupByDeleteEvent;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface GroupByDeleteHandler extends EventHandler{
	public static Type<GroupByDeleteHandler> TYPE = new Type<GroupByDeleteHandler>();
	public void OnGroupByDeleteEvent(GroupByDeleteEvent e) ;
}
