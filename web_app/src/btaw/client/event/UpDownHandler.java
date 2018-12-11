
package btaw.client.event;

import btaw.client.event.UpDownEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface UpDownHandler extends EventHandler{
	public static Type<UpDownHandler> TYPE = new Type<UpDownHandler>();
	void OnUpDownEvent(UpDownEvent e) ;
}
