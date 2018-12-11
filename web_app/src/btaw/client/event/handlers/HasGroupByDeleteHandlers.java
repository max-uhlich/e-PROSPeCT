
package btaw.client.event.handlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasGroupByDeleteHandlers extends HasHandlers{
	HandlerRegistration addGroupByDeleteHandler(GroupByDeleteHandler handle);
}
