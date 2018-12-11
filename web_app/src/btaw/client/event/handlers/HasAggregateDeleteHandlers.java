
package btaw.client.event.handlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAggregateDeleteHandlers extends HasHandlers{
	HandlerRegistration addAggregateDeleteHandler(AggregateDeleteHandler handle);
}
