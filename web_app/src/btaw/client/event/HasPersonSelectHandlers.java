
package btaw.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasPersonSelectHandlers extends HasHandlers{
	HandlerRegistration addPersonSelectHandler(PersonSelectHandler handle);
}
