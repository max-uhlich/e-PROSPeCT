package btaw.client.view.filter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import btaw.client.event.GroupByDeleteEvent;
import btaw.client.event.controllers.GroupByDeleteController;
import btaw.client.event.handlers.GroupByDeleteHandler;
import btaw.client.framework.Presenter;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.saved.RebuildDataGroupByPanel;


public class GroupByPanel extends HorizontalPanel implements ClickHandler {

	private Column column;
	private Presenter presenter;
	private HorizontalPanel inner;
	private PushButton delete;
	private Label columnName;
	private GroupByDeleteController deleteEventController;
	
	public GroupByPanel(Column column, Presenter p) {
		this.column=column;
		this.presenter=p;
		buildUI(column);
	}

	public void buildUI(Column column) {
		inner = new HorizontalPanel();
		inner.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.delete = new PushButton(new Image("images/delete.png"));
		delete.getElement().setClassName("upDownButton");
		delete.addClickHandler(this);
		
		this.setWidth("100%");
		this.columnName = new Label(column.getName());
		this.columnName.setWidth("100%");
		
		inner.add(columnName);
		
		this.deleteEventController = new GroupByDeleteController(this);
		for(Widget w: inner) {
			w.getElement().getStyle().setMarginRight(20, Unit.PX);
		}
		
		HorizontalPanel lilWrapper = new HorizontalPanel();
		lilWrapper.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		lilWrapper.setWidth("100%");
		lilWrapper.add(delete);
		delete.getElement().getStyle().setMarginRight(20, Unit.PX);
		
		this.add(inner);
		this.add(lilWrapper);
		
	}
	public void addGroupByDeletehandler(GroupByDeleteHandler handler)
	{
		this.deleteEventController.addGroupByDeleteHandler(handler);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		TablePresenter.get().setEdited();
		if(event.getSource() == delete)
		{
			deleteEventController.fireEvent(new GroupByDeleteEvent());
		}
	}
	
	public Column getColumn() {
		return this.column;
	}

	public void storeState(RebuildDataGroupByPanel rb) {
		rb.setCol(this.column);
	}
	public void setState(RebuildDataGroupByPanel rb) {
		this.column = rb.getCol();
	}

}
