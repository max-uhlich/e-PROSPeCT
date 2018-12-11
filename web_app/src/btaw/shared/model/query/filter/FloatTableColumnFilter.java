package btaw.shared.model.query.filter;

import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.column.FloatTableColumn.Op;
import btaw.shared.model.query.value.FloatValue;

public class FloatTableColumnFilter extends TableColumnFilter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3463700508834217871L;
	private Op oper;
	private FloatValue value;
	
	public FloatTableColumnFilter(){
		
	}
	public FloatTableColumnFilter(FloatTableColumn column, Op oper, FloatValue value) {
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