package btaw.shared.model.query.saved;

import java.io.Serializable;
import java.util.List;

import btaw.shared.model.DefinitionData;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.column.Column;

public class SavedQuery implements Serializable {
	private static final long serialVersionUID = 1590563365036772618L;
	
	private List<DefinitionData> definitionData;
	private List<TableRow> tableData;
	private List<Column> tableColumns;
	private String name;
	private Boolean isNamed = false;

	public List<Column> getTableColumns() {
		return tableColumns;
	}
	public void setTableColumns(List<Column> tableColumns) {
		this.tableColumns = tableColumns;
	}
	public List<TableRow> getTableData() {
		return tableData;
	}
	public void setTableData(List<TableRow> tableData) {
		this.tableData = tableData;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsNamed() {
		return isNamed;
	}
	public void setIsNamed(Boolean isNamed) {
		this.isNamed = isNamed;
	}
	public List<DefinitionData> getDefinitionData() {
		return definitionData;
	}
	public void setDefinitionData(List<DefinitionData> definitionData) {
		this.definitionData = definitionData;
	}

}
