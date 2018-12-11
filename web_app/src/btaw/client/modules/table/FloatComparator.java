package btaw.client.modules.table;

import java.util.Comparator;

import btaw.client.view.widgets.MagicTextColumn;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.column.TableColumn;

public class FloatComparator implements Comparator<TableRow> {

	private MagicTextColumn col = null;
	private boolean isAscending = false;
	
	public FloatComparator(MagicTextColumn col) {
		this.col = col;
	}
	
	public FloatComparator(MagicTextColumn col, boolean isAscending) {
		this.col = col;
		this.isAscending = isAscending;
	}
	
	@Override
	public int compare(TableRow r1, TableRow r2) {
		if(r1 == r2) {
			return 0;
		} else if(r1 == null) {
			return ifOp1Null();
		} else if(r2 == null) {
			return ifOp2Null();
		} else {
			String s1 = r1.getRowData(((TableColumn)((MagicTextColumn) col).getColumn()).getSQLName());
			String s2 = r2.getRowData(((TableColumn)((MagicTextColumn) col).getColumn()).getSQLName());
			if(s1 == s2) {
				return 0;
			}
			if(s1 == null) {
				return ifOp1Null();
			}
			if(s2 == null) {
				return ifOp2Null();
			}
			//don't think we need these bottom two lines anymore TODO: check
			s1 = s1.replaceAll("\\*", "");
			s2 = s2.replaceAll("\\*", "");
			double parsedR1 = Double.parseDouble(s1);
			double parsedR2 = Double.parseDouble(s2);
			if(this.isAscending) {
				return parsedR1 < parsedR2 ? -1 
					   : parsedR1 > parsedR2 ? 1 
					   : 0;
			} else {
				return parsedR1 < parsedR2 ? 1 
						   : parsedR1 > parsedR2 ? -1 
						   : 0;
			}
		}
	}

	private int ifOp1Null() {
		return 1;
	}

	private int ifOp2Null() {
		return -1;
	}
}