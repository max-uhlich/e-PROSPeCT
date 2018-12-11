
package btaw.client.event;

import btaw.client.event.PersonSelectEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface PersonSelectHandler extends EventHandler{
	public static Type<PersonSelectHandler> TYPE = new Type<PersonSelectHandler>();
	void OnPersonSelectEvent(PersonSelectEvent e) ;
}
