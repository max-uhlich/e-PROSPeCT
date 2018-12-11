package btaw.shared.model.query.column;

import java.util.List;

import com.google.gwt.user.client.Window;

import btaw.shared.model.TableRow;
import btaw.shared.model.query.Table;

public abstract class TableColumn extends Column {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2480750890819483098L;
	private String name;
	private String sqlName;
	private String treeHeader;
	private Table table;
	
	public TableColumn(){
	}
	
	/** Some columns have a manageably small set of possible values.  This will be returned here.
	 * Implementors should specify the type of list they will return.  Caller should check for NULL, and
	 * consider Columns that return null as free-form entry.
	 * 
	 * @return List of possible values to use in dropdown in filter creator.
	 */
	public abstract List<?> valueList();
	
	/** Columns will have a set of valid operators that can be used for filters.
	 * 
	 */
	public abstract interface Op {
	}	
	
	public abstract String opToString(Op op);
	public static List<String> opStringList() {
		return null;
	}

	public TableColumn(String name, String sqlName, String treeHeader, Table table) {
		this.name = name;
		this.sqlName = sqlName;
		this.treeHeader = treeHeader;
		this.table = table;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getSQLName() {
		return sqlName;
	}
	
	public String getTreeHeader() {
		return treeHeader;
	}
	
	public String getTableName() {
		return table.getTableName();
	}
	
	public String getUniqueTableName() {
		return table.getName();
	}
	
	@Override
	public String toSQLString() {
		String sql = getTableName();
		if (sql.length() > 0) {
			sql = sql.concat(".");
		}
		sql = sql.concat(sqlName);
		return sql;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sqlName == null) ? 0 : sqlName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this instanceof IntervalTableColumn & obj instanceof IntervalTableColumn){
			if (((IntervalTableColumn)this).getStringRepresentation().equals(((IntervalTableColumn)obj).getStringRepresentation()))
				return true;
			else
				return false;
		}
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableColumn other = (TableColumn) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sqlName == null) {
			if (other.sqlName != null)
				return false;
		} else if (!sqlName.equals(other.sqlName))
			return false;
		return true;
	}
	
}