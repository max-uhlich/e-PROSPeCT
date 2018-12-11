package btaw.client.event.handlers;

import btaw.client.event.PreviewDropEvent;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface DropEventHandler extends EventHandler{
	public static Type<DropEventHandler> TYPE = new Type<DropEventHandler>();
	void OnPreviewDrop(PreviewDropEvent e) ;
	
}
