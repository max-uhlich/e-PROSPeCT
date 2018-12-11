package btaw.shared.model.query.filter;

import btaw.shared.model.query.column.TableColumn;

public abstract class TableColumnFilter extends Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5360207068628416116L;
	private TableColumn column;
	public TableColumnFilter(){
		
	}
	public TableColumnFilter(TableColumn column) {
		this.column = column;
	}
	
	public TableColumn getColumn() {
		return this.column;
	}
	public void setColumn(TableColumn column){
		this.column=column;
	}
	
}