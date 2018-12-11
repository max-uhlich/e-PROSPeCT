package btaw.client.view.filter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import btaw.client.event.AggregateDeleteEvent;
import btaw.client.event.controllers.AggregateDeleteController;
import btaw.client.event.handlers.AggregateDeleteHandler;
import btaw.client.framework.Presenter;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.column.IntegerTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.filter.AggregateFunction;
import btaw.shared.model.query.saved.RebuildDataAggregatePanel;


public class AggregatePanel extends HorizontalPanel implements ClickHandler {

	private AggregateFunction filter;
	private Column column;
	private Presenter presenter;
	private HorizontalPanel inner;
	private PushButton delete;
	private Label columnName;
	private ListBox operator;
	private AggregateDeleteController deleteEventController;
	
	public AggregatePanel(Column column, Presenter p) {
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
		
		this.operator = new ListBox();
		
		Label ofL = new Label("of");
		
		inner.add(operator);
		inner.add(ofL);
		inner.add(columnName);
		
		this.deleteEventController = new AggregateDeleteController(this);
		for(Widget w: inner) {
			w.getElement().getStyle().setMarginRight(20, Unit.PX);
		}
		
		HorizontalPanel lilWrapper = new HorizontalPanel();
		lilWrapper.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		lilWrapper.setWidth("100%");
		lilWrapper.add(delete);
		delete.getElement().getStyle().setMarginRight(20, Unit.PX);
		
		if (column instanceof IntegerTableColumn
				|| column instanceof FloatTableColumn) {
			operator.addItem("SUM");
			operator.addItem("MAX");
			operator.addItem("MIN");
			operator.addItem("AVG");
		} else if (column instanceof DateTableColumn) {
			operator.addItem("MAX");
			operator.addItem("MIN");
		}

		operator.addItem("COUNT");
		operator.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				TablePresenter.get().setEdited();
			}
		});
		
		this.add(inner);
		this.add(lilWrapper);
		
	}
	public void addAggregateDeleteHandler(AggregateDeleteHandler handler)
	{
		this.deleteEventController.addAggregateDeleteHandler(handler);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == delete)
		{
			deleteEventController.fireEvent(new AggregateDeleteEvent());
		}
	}
	
	public Column getColumn() {
		return this.column;
	}

	public AggregateFunction getFilter() 
	{
		filter = new AggregateFunction();
		filter.setColumn((TableColumn)this.column);
		filter.setFunction(operator.getItemText(operator.getSelectedIndex()));
		return filter;
	}
	public void storeState(RebuildDataAggregatePanel rb) {
		rb.setCol(this.column);
		rb.setOpIndex(this.operator.getSelectedIndex());
	}
	public void setState(RebuildDataAggregatePanel rb) {
		this.column = rb.getCol();
		this.operator.setSelectedIndex(rb.getOpIndex());
	}

}
