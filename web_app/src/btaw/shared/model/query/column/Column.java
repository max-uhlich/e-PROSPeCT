package btaw.shared.model.query.column;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.cellview.client.TextColumn;

import btaw.client.view.filter.FilterPanel;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.SQLProducer;

/** Top level Column class to be used in Query.
 * 
 * @author Jon VanAlten
 *
 */
public abstract class Column implements SQLProducer, Serializable{
	public Column(){
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8889016086328575720L;

	/** Return this column's name.
	 * 
	 * @return A concise string which describes this column's contents; this would appear at
	 * the head of the column in the UI.
	 */
	public abstract String getName();
	
	public abstract FilterPanel getFilterPanel();

	public abstract String getSQLName();
	
}