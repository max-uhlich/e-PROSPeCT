package btaw.client.modules.savequery;

import btaw.client.framework.AView;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveQueryView extends AView implements SaveQueryPresenter.View {

	private VerticalPanel mainPanel;
	private HorizontalPanel nameQPanel;
	private TextBox name;
	
	public SaveQueryView () {
		mainPanel = new VerticalPanel();
		nameQPanel = new HorizontalPanel();
		initWidget(mainPanel);
		
		Label nameQL = new Label("Name Query: ");
		nameQL.setStyleName("center-Label");
		name = new TextBox();
		nameQPanel.add(nameQL);
		nameQPanel.add(name);
		
		mainPanel.add(nameQPanel);
	}
	
	public TextBox getName() {
		return name;
	}

}
