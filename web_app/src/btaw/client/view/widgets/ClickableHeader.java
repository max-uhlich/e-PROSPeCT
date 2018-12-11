package btaw.client.view.widgets;

import btaw.client.event.HeaderMenuSelectionEvent;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.event.handlers.HeaderMenuSelectionHandler;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Clickable header is header for the cell columns which is 
 * 
 * @author BTAWS
 *
 */
public class ClickableHeader extends Header<String> implements HasText, HeaderMenuSelectionHandler
{
		private final HeaderMenuSelectionController controller;
		private final ContextMenu popup;
	    private String text = "";
	    private final MagicTextColumn column;
			
	    public ClickableHeader(ClickableTextCell cell, MagicTextColumn column, HeaderMenuSelectionController controller) {
	        super(cell);
	        this.controller=controller;
	        this.column=column;
        	this.popup = new ContextMenu(this.controller, this.column, this);
        	this.controller.addHeaderMenuSelectionHandler(this);
        	this.setUpdater(new ValueUpdater<String>() {

				@Override
				public void update(String value) {
					System.err.println("CLICKABLE HEADER : " + value);
					
				}
        		
        	});
	    }

	    @Override
	    public String getValue() {
	        return text;
	    }
			
	    @Override
	    public void setText(String text) {
	        if (text == null)
		    text = "";
		this.text = text;
	    }
			
	    @Override
	    public String getText() {
	        return text;
	    }
	    
	    @Override
	    public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
		    final Event evt = Event.as(event);
		    int eventType = evt.getTypeInt();
		    //Window.alert("EVENT1");
		    switch (eventType){
		        case Event.ONCLICK:
		        	popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		                public void setPosition(int offsetWidth, int offsetHeight) {
		                    int left = (evt.getClientX());
		                    int top = (evt.getClientY());
		                    if(left+offsetWidth>Window.getClientWidth()){
		                    	left-=(((left+offsetWidth)-Window.getClientWidth()));
		                    }
		                    popup.setPopupPosition(left, top);
		                  }
		                });
		        	break;
		    }
		    super.onBrowserEvent(context, elem, event);
		    popup.setAutoHideEnabled(true);
	    }
			
	    @Override
	    public void render(Context context, SafeHtmlBuilder sb) {
	    	sb.appendHtmlConstant("<a href='#' class='clickable-header-style'>");
	        super.render(context, sb);
	        sb.appendHtmlConstant("</a>");
	    }

		@Override
		public void OnHeaderMenuSelectionEvent(HeaderMenuSelectionEvent e) {
			this.popup.hide();
		}
}
