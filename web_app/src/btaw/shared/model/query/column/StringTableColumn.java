package btaw.shared.model.query.column;

import java.util.Arrays;
import java.util.List;

import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.StringFilterPanel;
import btaw.shared.model.query.Table;

public class StringTableColumn extends TableColumn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1623536048970353442L;
	private static List<String> OpNames = Arrays.asList("=", "<>", "IS NULL", "IS NOT NULL");

	public StringTableColumn(){
		
	}
	
	public StringTableColumn(String name, String sqlName, String treeHeader, Table table) {
		super(name, sqlName, treeHeader, table);
	}

	@Override
	public List<String> valueList() {
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
			ret = new String("<>");
			break;
		case NULL:
			ret = new String("IS NULL");
			break;
		case NOTNULL:
			ret = new String("IS NOT NULL");
			break;
		}
		return ret;
	}
	
	public enum Op implements TableColumn.Op {
		EQ, NEQ, NULL, NOTNULL
	}

	public static List<String> opStringList() {
		return OpNames;
	}

	@Override
	public FilterPanel getFilterPanel() {
		return new StringFilterPanel(this);
	}
}