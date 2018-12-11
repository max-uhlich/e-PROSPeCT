package btaw.shared.model.query.column;

import java.util.Arrays;
import java.util.List;

import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.IntegerFilterPanel;
import btaw.shared.model.query.Table;

public class IntegerTableColumn extends TableColumn {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2737711837071066938L;
	private static List<String> OpNames = Arrays.asList("=", "<>", "<", ">", "<=", ">=", "IS NULL", "IS NOT NULL");

	public IntegerTableColumn(String name, String sqlName, String treeHeader, Table table) {
		super(name, sqlName, treeHeader, table);
	}
	public IntegerTableColumn(){
		
	}
	public enum Op implements TableColumn.Op {
		EQ, NEQ, LT, GT, LEQ, GEQ, NULL, NOTNULL
	}

	@Override
	public List<Integer> valueList() {
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
	public static List<String> opStringList() {
		return OpNames;
	}

	@Override
	public FilterPanel getFilterPanel() {
		return new IntegerFilterPanel(this);
	}
}