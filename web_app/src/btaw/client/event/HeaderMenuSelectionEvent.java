package btaw.client.event;

import btaw.client.event.handlers.HeaderMenuSelectionHandler;
import btaw.client.view.widgets.ClickableHeader;
import btaw.client.view.widgets.MagicTextColumn;
import btaw.shared.model.DefinitionData;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;

public class HeaderMenuSelectionEvent extends GwtEvent<HeaderMenuSelectionHandler> {
	
	public static Type<HeaderMenuSelectionHandler> TYPE = HeaderMenuSelectionHandler.TYPE;
	private String selectedItem;
	private MagicTextColumn columnName;
	private ClickableHeader header;
	private DefinitionData dd;
	private JavaScriptObject chart;
	
	/**
	 * HeaderMenuSelectionEvent used for header menu selections.
	 * @param selectedItem - The string label of the action selected
	 * @param columnName - The column that was selected
	 * @param header - The header of the column that was selected
	 */
	public HeaderMenuSelectionEvent(String selectedItem, MagicTextColumn columnName, ClickableHeader header)
	{
		this.selectedItem=selectedItem;
		this.columnName=columnName;
		this.header = header;
	}

	public HeaderMenuSelectionEvent(String selectedItem,DefinitionData dd)
	{
		this.selectedItem=selectedItem;
		this.dd = dd;
		this.columnName=null;
		this.header = null;
	}
	
	public HeaderMenuSelectionEvent(String selectedItem, JavaScriptObject chart)
	{
		this.selectedItem=selectedItem;
		this.dd = null;
		this.columnName=null;
		this.header = null;
		this.chart = chart;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<HeaderMenuSelectionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HeaderMenuSelectionHandler handler) {
		handler.OnHeaderMenuSelectionEvent(this);
	}
	
	/**
	 * Gets the Selected item title
	 * @return
	 */
	public String getSelectItem(){
		return this.selectedItem;
	}

	/**
	 * Gets the column that the header owns
	 * @return
	 */
	public MagicTextColumn getSourceColumn() {
		return this.columnName;
	}
	
	/**
	 * Gets the header that was clicked
	 * @return
	 */
	public ClickableHeader getClickableHeader()
	{
		return this.header;
	}

	public DefinitionData getDefinitionData() {
		return this.dd;
	}
	
	public JavaScriptObject getChart() {
		return this.chart;
	}
}
