package btaw.shared.model.query.column;

import java.util.Arrays;
import java.util.List;

import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.FloatFilterPanel;
import btaw.shared.model.query.Table;

public class FloatTableColumn extends TableColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 724760878430658490L;
	private static List<String> OpNames = Arrays.asList("=", "<>", "<", ">", "<=", ">=", "IS NULL", "IS NOT NULL");

	public FloatTableColumn(){
		
	}
	
	public FloatTableColumn(String name, String sqlName, String treeHeader, Table table) {
		super(name, sqlName, treeHeader, table);
	}

	@Override
	public List<Float> valueList() {
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
		return new FloatFilterPanel(this);
	}
}