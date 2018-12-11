
package btaw.client.event;
import btaw.client.event.handlers.GroupByDeleteHandler;
import com.google.gwt.event.shared.GwtEvent;
public class GroupByDeleteEvent extends GwtEvent<GroupByDeleteHandler> {

	public static Type<GroupByDeleteHandler> TYPE = GroupByDeleteHandler.TYPE;	
	
	/**
	 * FilterDeleteEvent used to delete filter panels.
	 */
	public GroupByDeleteEvent()
	{
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GroupByDeleteHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GroupByDeleteHandler handler) {
		handler.OnGroupByDeleteEvent(this);
	}
}
