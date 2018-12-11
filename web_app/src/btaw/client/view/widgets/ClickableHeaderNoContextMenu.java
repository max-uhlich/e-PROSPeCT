package btaw.client.view.widgets;

import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.TableRow;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasText;

public class ClickableHeaderNoContextMenu extends Header<String> implements HasText 
{
	private String text = "";
	private final Column<TableRow, String> col;
	private boolean isAscending = true;

	public ClickableHeaderNoContextMenu(Cell<String> cell, Column<TableRow, String> col) {
		super(cell);
		this.col = col;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
        if (text == null)
		    text = "";
		this.text = text;
	}

	@Override
	public String getValue() {
		return text;
	}
	
    @Override
    public void render(Context context, SafeHtmlBuilder sb) 
    {
    	sb.appendHtmlConstant("<a href='#' class='clickable-header-style'>");
        super.render(context, sb);
        sb.appendHtmlConstant("</a>");
        
    }
    
    @Override
    public void onBrowserEvent(Context context, Element elem, NativeEvent event) 
    {
	    final Event evt = Event.as(event);
	    int eventType = evt.getTypeInt();
	    System.err.println("Event type : " + event.toString());
	    switch (eventType) 
	    {
	        case Event.ONCLICK:
	        	TablePresenter.get().sortButtonCol(isAscending);
	        	isAscending = !isAscending;
	        	break;
	        
	    }
	    super.onBrowserEvent(context, elem, event);
    }

}
