
package btaw.client.event.handlers;

import btaw.client.event.AddColumnEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface AddColumnHandler extends EventHandler{
	public static Type<AddColumnHandler> TYPE = new Type<AddColumnHandler>();
	void OnAddColumnEvent(AddColumnEvent e) ;
}
