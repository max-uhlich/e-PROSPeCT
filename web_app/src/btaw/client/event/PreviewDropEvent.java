package btaw.client.event;

import btaw.client.event.handlers.DropEventHandler;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.event.shared.GwtEvent;

public class PreviewDropEvent extends GwtEvent<DropEventHandler>{

	// I'm not 100% sure about this but it seems to work.
	public static Type<DropEventHandler> TYPE = DropEventHandler.TYPE;
	private DragContext context;
	/**
	 * PreviewDropEvent is thrown when an item is dropped on a dropController
	 * this event happens before the object "lands" on the controller.
	 * @param context
	 */
	public PreviewDropEvent(DragContext context)
	{
		this.context = context;
	}
	/**
	 * Gets the object that was dragged.
	 * @return
	 */
	public DragContext GetContext()
	{
		return this.context;
	}
	
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DropEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DropEventHandler handler){
		handler.OnPreviewDrop(this);
		
	}

}
