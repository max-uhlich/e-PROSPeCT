
package btaw.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAddFilterHandlers extends HasHandlers{
	HandlerRegistration addAddFilterHandler(AddFilterHandler handle);
}
