package btaw.shared.model.query.filter;

import btaw.shared.model.query.column.IntervalTableColumn;
import btaw.shared.model.query.column.IntervalTableColumn.Op;
import btaw.shared.model.query.column.IntervalTableColumn;
import btaw.shared.model.query.value.FloatValue;

public class IntervalTableColumnFilter extends TableColumnFilter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3463700508834217871L;
	private Op oper;
	private Float value;
	private String timeInterval;
	
	public IntervalTableColumnFilter(){
		
	}
	public IntervalTableColumnFilter(IntervalTableColumn column, Op oper, Float value, String timeInterval) {
		super(column);
		this.timeInterval = timeInterval;
		this.oper = oper;
		if ((oper == Op.NULL) || (oper == Op.NOTNULL)) {
			this.value = null;
		} else {
			this.value = value;
		}
		
		column.setStringRepresentation(toSQLString());
		column.setOp(this.oper);
		column.setVal(this.value);
		column.setTimeInt(this.timeInterval);
	}
	
	public String getTimeInterval(){
		return this.timeInterval;
	}
	
	@Override
	public String toSQLString() {
		String sql = new String();
		sql = sql.concat(((IntervalTableColumn)getColumn()).toSQLString(this.timeInterval));
		sql = sql.concat(" " + getColumn().opToString(oper) + " ");
		if (value != null) {
			sql = sql.concat("" + value);
		}
		return sql;
	}
}