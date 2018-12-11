package btaw.shared.model.query.column;

import java.util.Arrays;
import java.util.List;

import btaw.client.view.filter.BooleanFilterPanel;
import btaw.client.view.filter.FilterPanel;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.Table;

public class BooleanTableColumn extends TableColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -100722323057058340L;
	private static List<String> OpNames = Arrays.asList("=", "<>", "IS NULL", "IS NOT NULL");

	public BooleanTableColumn(){
		
	}
	
	public BooleanTableColumn(String name, String sqlName, String treeHeader, Table table) {
		super(name, sqlName, treeHeader, table);
	}

	@Override
	public List<Boolean> valueList() {
		return null;
	}

	@Override
	public String opToString(TableColumn.Op op) {
		if (!(op instanceof Op)) {
			return null;
		}
		return OpNames.get(((Op) op).ordinal());
	}
	
	public enum Op implements TableColumn.Op {
		EQ, NEQ, NULL, NOTNULL;
	}	

	public static List<String> opStringList() {
		return OpNames;
	}

	@Override
	public FilterPanel getFilterPanel() {
		return new BooleanFilterPanel(this);
	}

}