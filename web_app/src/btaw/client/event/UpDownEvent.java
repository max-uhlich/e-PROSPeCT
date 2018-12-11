
package btaw.client.event;
import btaw.client.event.UpDownHandler;
import btaw.client.view.filter.FilterPanel;

import com.google.gwt.event.shared.GwtEvent;
public class UpDownEvent extends GwtEvent<UpDownHandler> {

	public static Type<UpDownHandler> TYPE = UpDownHandler.TYPE;
	private boolean up=true;
	private FilterPanel theThingItCameFrom;
	/**
	 * UpDownEvent thrown when a filterpanel up or down button has been
	 * selected.
	 * @param up - if Up is true move panel up, else down
	 * @param p - the panel the event fired from
	 */
	public UpDownEvent(boolean up, FilterPanel p){
		this.up=up;
		this.theThingItCameFrom = p;
	}
	public boolean up(){
		return up;
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UpDownHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpDownHandler handler) {
		handler.OnUpDownEvent(this);
	}
	
	public FilterPanel getTheThingItCameFrom()
	{
		return theThingItCameFrom;
	}
	
}
