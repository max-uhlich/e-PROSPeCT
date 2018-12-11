package btaw.client.event.controllers;


import btaw.client.event.PreviewDropEvent;
import btaw.client.event.handlers.DropEventHandler;
import btaw.client.event.handlers.HasDropHandlers;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;


public class PreviewDropEventController extends SimpleDropController implements HasDropHandlers{

		private HandlerManager handlers;
		/**
		 * PreviewDropEventController is used to link the dropcontroller which handles 
		 * drag and drop in the DND library and events that allow presenters to act on 
		 * drop events.
		 * @param eventSource
		 */
		public PreviewDropEventController(Widget eventSource)
		{
			super(eventSource);
			
			handlers = new HandlerManager(eventSource);
		}
		
		@Override
		public void onDrop(DragContext context)
		{
			
			super.onDrop(context);
			
		}
		
		@Override
		/**
		 * onPreviewDrop is overrode to stop dropping. This stops widgets from being
		 * left where they were dropped.
		 */
		public void onPreviewDrop(DragContext context) throws VetoDragException
		{
			fireEvent(new PreviewDropEvent(context));
			throw new VetoDragException();
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			handlers.fireEvent(event);
			
		}

		@Override
		public HandlerRegistration addDropHandler(DropEventHandler handler) {
			return handlers.addHandler(DropEventHandler.TYPE, handler);
		
		}
		
		
		
}
