package btaw.client.modules.table;

import java.util.Comparator;

import btaw.shared.model.TableRow;

public class ButtonComparator implements Comparator<TableRow> {

	private boolean isAscending = false;
	
	public ButtonComparator(boolean isAscending){
		this.isAscending = isAscending;
	}
	
	@Override
	public int compare(TableRow r1, TableRow r2) {
		//covers both null case
		if(r1 == r2) {
			return 0;
		}
		
		if(r1 != null) {
			String s1 = r1.getRowData("HasScansBoolean");
			String s2 = null;
			
			if(r2 != null) {
				s2 = r2.getRowData("HasScansBoolean");
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