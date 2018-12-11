package btaw.client.view.widgets;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.TableColumn;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;

public class MagicTextColumn extends com.google.gwt.user.cellview.client.Column<TableRow, String>{
	
	private Column columnHead;

	public MagicTextColumn(Column columnHead, Cell c)
	{
		super(c);
		this.columnHead = columnHead;
	}
	
	@Override
	public String getValue(TableRow object) {
		//System.out.println("IN GET VALUE " + columnHead.getName() + " " + object.getRowData("pid") + " " + object.getRowData(getKey()));
		return object.getRowData(getKey());
	}
	
	public String getKey() {
		String s =((Column)columnHead).getSQLName();
		int i = s.lastIndexOf('.');
		if(i>0){
			s=s.substring(i+1, s.length());
		}
		return s;
	}
	
	@Override
    public String getCellStyleNames(Context context, TableRow object) {
		if(object.isDuplicate(getKey())) {
			return "highlightedCell ";
		} else {
			return null;
		}
     }

	public String getColumnName()
	{
		return columnHead.getName();
	}
	
	public Column getColumn()
	{
		return columnHead;
	}
	
}
