
package btaw.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasUpDownHandlers extends HasHandlers{
	HandlerRegistration addUpDownHandler(UpDownHandler handle);
}
