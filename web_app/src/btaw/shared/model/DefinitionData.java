package btaw.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.Window;

import btaw.client.modules.filter.FilterView;
import btaw.client.modules.main.MainPresenter;
import btaw.shared.model.query.Query;
import btaw.shared.model.query.Query.Order;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;

public class DefinitionData implements Serializable {

	private static final long serialVersionUID = 7223387622994684452L;
	private ArrayList<Column> cols;
	String sql;
	String defName;
	String def;

	Query query;
	List<TableRow> tableData;
	List<RebuildDataFilterPanel> rebuildFilters;
	
	public DefinitionData(){
		
	}
	
	public DefinitionData(String name, String sql, ArrayList<Column> cols) {
		this.cols = cols;
		this.defName = name;
		this.sql = sql;
	}

	public void addCol(Column col){
		if (!cols.contains(col)) {
			cols.add(col);
		}
	}
	
	public void removeCol(Column col){
		int position = cols.indexOf(col);
		if (position > -1) {
			cols.remove(position);
		}
	}
	
	public void setCols(ArrayList<Column> cols) {
		this.cols = cols;
	}

	public ArrayList<Column> getCols() {
		return cols;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getDefName() {
		return defName;
	}

	public void setDefName(String defName) {
		this.defName = defName;
	}

	public List<TableRow> getTableData() {
		return tableData;
	}

	public void setTableData(List<TableRow> tableData) {
		for(TableRow tr : tableData) {
			tr.addEntry("definitionIdentifier", defName);
		}
		this.tableData = tableData;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public List<RebuildDataFilterPanel> getRebuildFilters() {
		return rebuildFilters;
	}

	public void setRebuildFilters(List<RebuildDataFilterPanel> rebuildFilters) {
		this.rebuildFilters = rebuildFilters;
	}

	public String getDefinition() {
		return def;
	}

	public void setDefinition(String def) {
		this.def = def;
	}
}
