
package btaw.client.event;
import btaw.client.event.handlers.FilterDeleteHandler;
import com.google.gwt.event.shared.GwtEvent;
public class FilterDeleteEvent extends GwtEvent<FilterDeleteHandler> {

	public static Type<FilterDeleteHandler> TYPE = FilterDeleteHandler.TYPE;	
	
	/**
	 * FilterDeleteEvent used to delete filter panels.
	 */
	public FilterDeleteEvent()
	{
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FilterDeleteHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FilterDeleteHandler handler) {
		handler.OnFilterDeleteEvent(this);
	}
}
