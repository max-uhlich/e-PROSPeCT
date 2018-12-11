package btaw.shared.model.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import btaw.shared.model.query.column.TableColumn;

/** A Table which exists in the database.
 * 
 * @author Jon VanAlten
 *
 */
public class Table implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4198736184586706510L;
	/* name is optional, tableName is not.
	 * Example:
	 * 
	 * SELECT name.columnName FROM tableName name
	 * 
	 * (Table may not be referred to by name)
	 */ 
	private String name, tableName;
	private ArrayList<TableColumn> columns;
	
	/** New Table object.
	 * 
	 * @param tableName The name of the new Table, as it appears in the database.
	 */
	public Table(){
		name = new String();
		columns = new ArrayList<TableColumn>();
	}
	public Table(String tableName) {
		this.tableName = tableName;
		this.name = "";
		columns = new ArrayList<TableColumn>();
	}

	/** Set a name for this Table.
	 * 
	 * @param name The name by which this table can be referred in an SQL statement.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/** Add a new Column to this Table.
	 * 
	 * @param column The new Column to be added.
	 */
	public void addColumn(TableColumn column) {
		columns.add(column);
	}
	
	/** Get the name of this Table in the context of an SQL query.
	 * 
	 * @return The name of this Table.
	 */
	public String getName() {
		return new String(name);
	}

	/** Get the name of this table in the database.
	 * 
	 * @return The database name of this table.
	 */
	public String getTableName() {
		return new String(tableName);
	}
	
	public Table getNamedTable(String name) {
		Table newTable = new Table(getTableName());
		newTable.setName(name);
		ListIterator<TableColumn> iter = columns.listIterator();
		while (iter.hasNext()) {
			newTable.addColumn(iter.next());
		}
		return newTable;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
}