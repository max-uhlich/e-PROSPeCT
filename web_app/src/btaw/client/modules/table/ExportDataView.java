package btaw.client.modules.table;

import java.util.ArrayList;
import java.util.List;

import btaw.client.framework.AView;
import btaw.client.modules.image.Circle;
import btaw.shared.model.TableRow;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExportDataView extends AView implements ExportDataPresenter.View {

	private VerticalPanel mainPanel;
	private VerticalPanel csvPanel;
	private HTML csvLink;
	private String linkStart = "<a href=\"";
	private String linkEnd = "\" target=\"_blank\">Download Data</a>";
	
	private List<TableRow> sourceTable;
	
	public ExportDataView () {
		mainPanel = new VerticalPanel();
		csvPanel = new VerticalPanel();
		initWidget(mainPanel);
		
		csvLink = new HTML();
		csvPanel.add(new HTML("Please wait until download link appears and then right-click, save link as to download.<br>"));
		csvPanel.add(csvLink);
		mainPanel.add(csvPanel);
		
		sourceTable = TablePresenter.get().getTableAsList();
		
	}
	
	@Override
	public ArrayList<Circle> getPoints() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setLink(String link) {
		if(link == null) {
			csvLink.setHTML("Sorry no data!");
		}
		csvLink.setHTML(linkStart + link + linkEnd);
	}

}
