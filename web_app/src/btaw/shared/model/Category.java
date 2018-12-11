package btaw.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import btaw.shared.model.query.column.Column;

public class Category implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6651135338983615121L;
	private String name;
	private List<Column> columns;
	
	public Category(String name){
		this.name = name;
		this.columns = new ArrayList<Column>();
	}
	public Category(){
		
	}
	
	public void addColumns(List<Column> columns){
		this.columns.addAll(columns);
	}
	public void addColumn(Column column){
		this.columns.add(column);
	}
	public String getName(){
		return new String(this.name);
	}

	public List<Column> getColumns() {
		return this.columns;
	}
}
