package btaw.client.modules.table;

import java.util.Comparator;

import btaw.client.view.widgets.MagicTextColumn;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.column.TableColumn;

public class StringComparator implements Comparator<TableRow> {

	private MagicTextColumn col = null;
	private boolean isAscending = false;
	
	public StringComparator(MagicTextColumn col) {
		this.col = col;
	}
	
	public StringComparator(MagicTextColumn col, boolean isAscending) {
		this.col = col;
		this.isAscending = isAscending;
	}
	
	@Override
	public int compare(TableRow r1, TableRow r2) {
		//covers both null case
		if(r1 == r2) {
			return 0;
		}
		
		if(r1 != null) {
			String s1 = r1.getRowData(((TableColumn)((MagicTextColumn) col).getColumn()).getSQLName());
			String s2 = null;
			
			if(r2 != null) {
				s2 = r2.getRowData(((TableColumn)((MagicTextColumn) col).getColumn()).getSQLName());
			} else {
				return ifOp2Null();
			}
			
			if(s1 == s2) {
				return 0;
			}
			
			if(s1 != null) {
				if(this.isAscending) {
					return (s2 != null) ? s1.compareTo(s2) : ifOp2Null();
				} else {
					return (s2 != null) ? s2.compareTo(s1) : ifOp2Null();
				}
			} else {
				return ifOp1Null();
			}
		} else {
			return ifOp1Null();
		}
	}
	private int ifOp1Null() {
		return 1;
	}

	private int ifOp2Null() {
		return -1;
	}

}