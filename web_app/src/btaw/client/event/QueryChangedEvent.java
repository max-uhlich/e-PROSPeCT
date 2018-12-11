package btaw.client.event;

import btaw.client.event.handlers.QueryChangeHandler;

import com.google.gwt.event.shared.GwtEvent;

public class QueryChangedEvent extends GwtEvent<QueryChangeHandler> {
	
	public static Type<QueryChangeHandler> TYPE = QueryChangeHandler.TYPE;
	
	/**
	 * This event is fired when the on screen query ie columns or filters
	 * have changed.
	 */
	public QueryChangedEvent()
	{
		
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<QueryChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(QueryChangeHandler handler) {
		handler.OnQueryChangedEvent(this);
	}

}
