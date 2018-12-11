
package btaw.client.event;
import btaw.client.event.handlers.AggregateDeleteHandler;
import com.google.gwt.event.shared.GwtEvent;
public class AggregateDeleteEvent extends GwtEvent<AggregateDeleteHandler> {

	public static Type<AggregateDeleteHandler> TYPE = AggregateDeleteHandler.TYPE;	
	
	/**
	 * FilterDeleteEvent used to delete filter panels.
	 */
	public AggregateDeleteEvent()
	{
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AggregateDeleteHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AggregateDeleteHandler handler) {
		handler.OnAggregateDeleteEvent(this);
	}
}
