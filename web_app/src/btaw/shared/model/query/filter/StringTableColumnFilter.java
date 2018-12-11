package btaw.shared.model.query.filter;

import java.util.ArrayList;

import btaw.shared.model.query.column.StringTableColumn;
import btaw.shared.model.query.column.StringTableColumn.Op;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.value.StringValue;

public class StringTableColumnFilter extends TableColumnFilter {
	private static final long serialVersionUID = -4851374155202762540L;
	private StringTableColumn.Op oper;
	private StringValue value;

	public StringTableColumnFilter(){
		
	}
	public StringTableColumnFilter(StringTableColumn column, Op oper, StringValue value) {
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