package btaw.client.event;

import btaw.client.event.handlers.ClearEventHandler;

import com.google.gwt.event.shared.GwtEvent;

public class ClearEvent extends GwtEvent<ClearEventHandler> {
	public static Type<ClearEventHandler> TYPE = ClearEventHandler.TYPE;	
	
	/**
	 * Clear event to clear the filter panel.
	 */
	public ClearEvent(){
		
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ClearEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ClearEventHandler handler) {
		handler.onClearEvent(this);
	}
}
