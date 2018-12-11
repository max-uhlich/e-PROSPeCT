package btaw.client.view.filter;

import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.table.TablePresenter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class SubgroupFilterPanel extends HorizontalPanel implements ClickHandler {

	private HorizontalPanel inner;
	private PushButton delete;
	private Label groupNameL;
	
	public SubgroupFilterPanel(String groupName) {
		buildUI(groupName);
	}

	public void buildUI(String groupName) {
		inner = new HorizontalPanel();
		inner.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.delete = new PushButton(new Image("images/delete.png"));
		delete.getElement().setClassName("upDownButton");
		delete.addClickHandler(this);
		
		this.setWidth("100%");
		this.groupNameL = new Label(groupName);
		this.groupNameL.setWidth("100%");
		
		inner.add(groupNameL);
		
		for(Widget w: inner) {
			w.getElement().getStyle().setMarginRight(20, Unit.PX);
		}
		
		HorizontalPanel lilWrapper = new HorizontalPanel();
		lilWrapper.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		lilWrapper.setWidth("100%");
		lilWrapper.add(delete);
		//delete.getElement().getStyle().setMarginRight(20, Unit.PX);
		
		this.add(inner);
		this.add(lilWrapper);
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		TablePresenter.get().setEdited();
		if(event.getSource() == delete)
		{
			FilterPresenter.get().removeGroup(this.groupNameL.getText());
		}
	}
	
	public String getName() {
		return this.groupNameL.getText();
	}

}
