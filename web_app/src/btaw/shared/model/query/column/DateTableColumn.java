package btaw.shared.model.query.column;

import java.util.Arrays;
import java.util.List;
import java.sql.Date;

import btaw.client.view.filter.DateFilterPanel;
import btaw.client.view.filter.FilterPanel;
import btaw.shared.model.query.Table;

public class DateTableColumn extends TableColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9202699573650584889L;
	private static List<String> OpNames = Arrays.asList("=", "<>", "<", ">", "<=", ">=", "IS NULL", "IS NOT NULL");
	DateTableColumn(){
		
	}
	
	public DateTableColumn(String name, String sqlName, String treeHeader, Table table) {
		super(name, sqlName, treeHeader, table);
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

	@Override
	public FilterPanel getFilterPanel() {
		return new DateFilterPanel(this);
	}
}