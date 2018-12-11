
package btaw.client.event.handlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAddColumnHandlers extends HasHandlers{
	HandlerRegistration addAddColumnHandler(AddColumnHandler handle);
}
