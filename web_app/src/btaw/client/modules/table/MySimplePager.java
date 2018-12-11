package btaw.client.modules.table;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.Range;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.view.client.HasRows;

public class MySimplePager extends SimplePager {

	public MySimplePager(){
		super(MySimplePager.TextLocation.RIGHT);
	}
	
	@Override
	public void setPageStart(int index) {
	  if (getDisplay() != null) {
	    Range range = getDisplay().getVisibleRange();
	    int pageSize = range.getLength();

	    // Removed the min to show fixed ranges
	    //if (isRangeLimited && display.isRowCountExact()) {
	    //  index = Math.min(index, display.getRowCount() - pageSize);
	    //}

	    index = Math.max(0, index);
	    if (index != range.getStart()) {
	      getDisplay().setVisibleRange(index, pageSize);
	    }
	  }
	}

	@Override
	protected String createText() {
	    // Default text is 1 based.
	    NumberFormat formatter = NumberFormat.getFormat("#,###");
	    HasRows display = getDisplay();
	    Range range = display.getVisibleRange();
	    int pageStart = range.getStart() + 1;
	    int pageSize = range.getLength();
	    int dataSize = display.getRowCount();
	    int endIndex = Math.min(dataSize, pageStart + pageSize - 1);
	    endIndex = Math.max(pageStart, endIndex);
	    boolean exact = display.isRowCountExact();
	    return "Rows " + formatter.format(pageStart) + "-" + formatter.format(endIndex)
	        + (exact ? " of " : " of over ") + formatter.format(dataSize);
	  }
	
}
