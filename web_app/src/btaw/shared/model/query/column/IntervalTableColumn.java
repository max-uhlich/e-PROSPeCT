package btaw.shared.model.query.column;

import java.util.Arrays;
import java.util.List;
import java.sql.Date;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

import btaw.client.view.filter.DateFilterPanel;
import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.IntervalFilterPanel;
import btaw.shared.model.query.Table;
import btaw.shared.model.query.value.FloatValue;

public class IntervalTableColumn extends TableColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9202699573650584889L;
	private static List<String> OpNames = Arrays.asList("=", "<>", "<", ">", "<=", ">=", "IS NULL", "IS NOT NULL");

	private Column column1;
	private Column column2;
	private String representation;
	private Op op;
	private Float val;
	private String timeInterval;
	
	//ListBox timeInterval;
	
	public IntervalTableColumn(Column col1, Column col2) {
		super("", "", "", null);
		this.setStartColumn(col1);
		this.setEndColumn(col2);
	}
	
	public IntervalTableColumn() {
		super("", "", "", null);
	}

	public Column getStartColumn(){
		return column1;
	}
	
	public Column getEndColumn(){
		return column2;
	}
	
	public void setStartColumn(Column column){
		this.column1 = column;
	}
	
	public void setEndColumn(Column column){
		this.column2 = column;
	}
	public String getStringRepresentation(){
		return this.representation;
	}
	public void setStringRepresentation(String str){
		this.representation = str;
	}
	
	
	public void setVal(Float val){
		this.val = val;
	}
	public void setOp(Op op){
		this.op = op;
	}
	public void setTimeInt(String timeInterval){
		this.timeInterval = timeInterval;
	}
	public String getVal(){
		if(this.val!=null)
			return " " + this.val;
		else
			return "";
	}
	public String getOp(){
		if(this.op!=null)
			return " " + this.op.toString();
		else
			return "";
	}
	public String getTimeInt(){
		if(this.timeInterval!=null)
			return " " + this.timeInterval;
		else
			return "";
	}
	
	@Override
	public List<Date> valueList() {
		return null;
	}

	@Override
	public String opToString(TableColumn.Op op) {
		if (!(op instanceof Op)) {
			return null;
		}
		String ret = null;
		switch ((Op) op) {
		case EQ:
			ret = "=";
			break;
		case NEQ:
			ret = "<>";
			break;
		case LT:
			ret = "<";
			break;
		case GT:
			ret = ">";
			break;
		case LEQ:
			ret = "<=";
			break;
		case GEQ:
			ret = ">=";
			break;
		case NULL:
			ret = "IS NULL";
			break;
		case NOTNULL:
			ret = "IS NOT NULL";
			break;
		}
		return ret;
	}

	public enum Op implements TableColumn.Op {
		EQ, NEQ, LT, GT, LEQ, GEQ, NULL, NOTNULL
	}
	public static List<String> opStringList() {
		return OpNames;
	}
	public IntervalFilterPanel getFilterPanel(PickupDragController dragController) {
		return new IntervalFilterPanel(this,dragController);
	}

	@Override
	public FilterPanel getFilterPanel() {
		return null;
	}
	
	@Override
	public String getName() {
		String name = new String(this.column1.getName() + " - " + this.column2.getName() + this.getOp() + this.getTimeInt());
		return name;
	}
	
	public String getSQLName() {
		//String name = new String(this.column1.getName() + " - " + this.column2.getName());
		//return name;
		return this.getName();
	}
	
	public void setTimeInterval(ListBox timeInterval) {
		//Window.alert("setting Time INterval");
		//this.timeInterval = timeInterval;
	}
	
	public String toSQLString(String interval) {
		String sql = new String();
		
		if (interval.equals("years")){
			sql = new String("(DATE_PART('year', " + this.column2.toSQLString() + " ) - DATE_PART('year', " + this.column1.toSQLString() + "))");
		} else if (interval.equals("months")){
			sql = new String("(DATE_PART('year', " + this.column2.toSQLString() + " ) - DATE_PART('year', " + this.column1.toSQLString() + "))*12 + (DATE_PART('month', " + this.column2.toSQLString() + " ) - DATE_PART('month', " + this.column1.toSQLString() + "))"); 
		} else if (interval.equals("days")){
			sql = new String("(" + this.column2.toSQLString() + "::date - " + this.column1.toSQLString() + "::date)");
		}
		
		return sql;
	}
	
}