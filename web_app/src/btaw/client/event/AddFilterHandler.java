
package btaw.client.event;

import btaw.client.event.AddFilterEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface AddFilterHandler extends EventHandler{
	public static Type<AddFilterHandler> TYPE = new Type<AddFilterHandler>();
	void OnAddFilterEvent(AddFilterEvent e) ;
}
