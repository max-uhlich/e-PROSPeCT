
package btaw.client.event;
import btaw.client.event.handlers.AddColumnHandler;
import btaw.shared.model.query.column.Column;

import com.google.gwt.event.shared.GwtEvent;
public class AddColumnEvent extends GwtEvent<AddColumnHandler> {
	private Column col;
	public static Type<AddColumnHandler> TYPE = AddColumnHandler.TYPE;
		
	/**
	 * AddColumnEvent is the event thrown when a column is to be added.
	 * @param col
	 */
	public AddColumnEvent(Column col){
		this.col=col;
	}
	/**
	 * Gets the column from the event.
	 * @return
	 */
	public Column getColumn(){
		return this.col;
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AddColumnHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddColumnHandler handler) {
		handler.OnAddColumnEvent(this);
	}
}
