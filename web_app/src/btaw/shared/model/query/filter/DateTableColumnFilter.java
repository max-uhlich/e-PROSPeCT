package btaw.shared.model.query.filter;

import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.DateTableColumn.Op;
import btaw.shared.model.query.value.DateValue;

public class DateTableColumnFilter extends TableColumnFilter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4086989802928521461L;
	private Op oper;
	private DateValue value;
	
	public DateTableColumnFilter(){
		
	}
	public DateTableColumnFilter(DateTableColumn column, Op oper, DateValue value) {
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