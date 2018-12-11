
package btaw.client.event;
import btaw.client.event.AddFilterHandler;
import btaw.shared.model.query.column.Column;

import com.google.gwt.event.shared.GwtEvent;
public class AddFilterEvent extends GwtEvent<AddFilterHandler> {

	public static Type<AddFilterHandler> TYPE = AddFilterHandler.TYPE;
		
	Column column;
	/**
	 * Event to add a new filter panel
	 * @param column
	 */
	public AddFilterEvent(Column column)
	{
		this.column = column;
	}
		
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AddFilterHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddFilterHandler handler) {
		handler.OnAddFilterEvent(this);
	}
	
	/**
	 * Gets the column that threw the event.
	 * @return
	 */
	public Column getColumn()
	{
		return this.column;
	}
}
