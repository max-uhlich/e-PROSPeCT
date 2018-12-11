package btaw.shared.model.query.filter;

import btaw.shared.model.query.column.BooleanTableColumn;
import btaw.shared.model.query.column.BooleanTableColumn.Op;
import btaw.shared.model.query.value.BooleanValue;

public class BooleanTableColumnFilter extends TableColumnFilter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9138194998429363768L;
	private Op oper;
	private BooleanValue value;
	
	public BooleanTableColumnFilter(){
		
	}
	public BooleanTableColumnFilter(BooleanTableColumn column, Op oper, BooleanValue value) {
		super(column);
		this.oper = oper;
		if ((oper == Op.NULL) || (oper == Op.NOTNULL)) {
			this.value = null;
		} else {
			this.value = value;
		}
	}
	
	@Override
	public String toSQLString() {
		String sql = new String();
		
		sql = sql.concat(getColumn().toSQLString());
		sql = sql.concat(" " + getColumn().opToString(oper) + " ");
		if (value != null) {
			sql = sql.concat(value.toSQLString());
		}
		return sql;
	}

}