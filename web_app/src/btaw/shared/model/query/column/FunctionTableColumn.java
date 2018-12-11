package btaw.shared.model.query.column;

import java.util.List;

import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.FloatFilterPanel;
import btaw.shared.model.query.Table;

public class FunctionTableColumn extends TableColumn {
	/**
	 * Kind of a sentinal class, used for keeping things in Query straight.
	 */
	private static final long serialVersionUID = -2737711837071066938L;

	public FunctionTableColumn(String name, String sqlName, Table table) {
		super(name, sqlName, "", table);
	}
	public FunctionTableColumn(){
		
	}

	@Override
	public List<Integer> valueList() {
		return null;
	}

	@Override
	public String opToString(TableColumn.Op op) {
		return null;
	}
	@Override
	public FilterPanel getFilterPanel() {
		return new FloatFilterPanel(this);
	}
}