package btaw.client.modules.savequery;

import java.util.List;
import btaw.client.framework.AView;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.shared.model.query.saved.SavedQueryDisplayData;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DeleteQueryView extends AView implements DeleteQueryPresenter.View {

	private VerticalPanel mainPanel;
	private HorizontalPanel nameQPanel;
	private TextBox name;
	private Boolean isOddPanel = true;
	private VerticalPanel savedQueryPanel;
	private ScrollPanel savedQueryScrollPanel;
	
	public DeleteQueryView () {
		mainPanel = new VerticalPanel();
		nameQPanel = new HorizontalPanel();
		savedQueryPanel = new VerticalPanel();
		savedQueryScrollPanel = new ScrollPanel(savedQueryPanel);
		savedQueryScrollPanel.setSize("350px", "300px");
		initWidget(mainPanel);
		
		Label nameQL = new Label("Double-Click to Delete");
		nameQL.setStyleName("center-Label");
		nameQPanel.add(nameQL);
		
		mainPanel.add(nameQPanel);
		mainPanel.add(new HTML("<hr />"));
		mainPanel.add(savedQueryScrollPanel);
	}
	
	public void addSavedQueries(List<SavedQueryDisplayData> data, DeleteQueryPresenter presenter) {
		isOddPanel = true;
		savedQueryPanel.clear();
		for(SavedQueryDisplayData currData : data) {
			FlowPanel fp = new FlowPanel();
			fp.setWidth("330px");
			if(isOddPanel) {
				fp.getElement().getStyle().setBackgroundColor("#E3EBFF");
				isOddPanel = false;
			} else {
				isOddPanel = true;
			}
			savedQueryPanel.add(fp);
			SavedQueryLabel label = new SavedQueryLabel(currData.getDate(), currData.getSavedQueryID());
			SavedQueryLabel label2 = new SavedQueryLabel(currData.getName(), currData.getSavedQueryID());
			label.getElement().getStyle().setPaddingTop(5, Unit.PX);
			label.getElement().getStyle().setPaddingLeft(5, Unit.PX);
			label2.getElement().getStyle().setPaddingBottom(5, Unit.PX);
			label2.getElement().getStyle().setPaddingLeft(5, Unit.PX);
			fp.add(label);
			fp.add(label2);
			label.addDoubleClickHandler(presenter);
			label2.addDoubleClickHandler(presenter);
		}
	}

}
