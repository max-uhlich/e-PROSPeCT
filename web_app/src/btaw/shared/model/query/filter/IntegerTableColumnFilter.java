package btaw.shared.model.query.filter;

import btaw.shared.model.query.column.IntegerTableColumn;
import btaw.shared.model.query.column.IntegerTableColumn.Op;
import btaw.shared.model.query.value.IntegerValue;

public class IntegerTableColumnFilter extends TableColumnFilter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1636778454673503080L;
	private Op oper;
	private IntegerValue value;
	
	public IntegerTableColumnFilter(){
		
	}
	public IntegerTableColumnFilter(IntegerTableColumn column, Op oper, IntegerValue value) {
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
		//System.err.println(sql);
		return sql;
	}
}