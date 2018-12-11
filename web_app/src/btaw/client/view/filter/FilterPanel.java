package btaw.client.view.filter;

import java.util.List;

import btaw.client.event.FilterDeleteEvent;
import btaw.client.event.UpDownEvent;
import btaw.client.event.controllers.FilterDeleteController;
import btaw.client.event.handlers.FilterDeleteHandler;
import btaw.client.framework.EventBus;
import btaw.client.modules.table.TablePresenter;
import btaw.client.view.panels.DefinitionPanel;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.IntervalTableColumn;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class FilterPanel extends HorizontalPanel implements ClickHandler
{
	//private final Filter filter;
	private PushButton delete, up, down;
	private Widget columnName;
	private ListBox operator;
	private Widget value;
	private ToggleButton leftPar, rightPar;
	private FilterDeleteController deleteEventController;
	private Column column;
	private HorizontalPanel inner;

	public FilterPanel(Column column){
		
		//this.filter = filter;
		this.column=column;
		inner = new HorizontalPanel();
		inner.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.delete = new PushButton(new Image("images/delete.png"));
		delete.getElement().setClassName("upDownButton");
		this.up = new PushButton(new Image("images/up.png"));
		up.getElement().setClassName("upDownButton");
		this.up.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				EventBus.getInstance().fireEvent(new UpDownEvent(true, FilterPanel.this));
			}
		});
		
		this.down = new PushButton(new Image("images/down.png"));
		down.getElement().setClassName("upDownButton");
		this.down.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				EventBus.getInstance().fireEvent(new UpDownEvent(false, FilterPanel.this));
			}
		});
		
		delete.addClickHandler(this);
		this.leftPar = new ToggleButton(new Image("images/parLeftUp.png"), new Image("images/parLeftDown.png"));
		this.leftPar.getElement().setClassName("parenthesis");
		this.rightPar = new ToggleButton(new Image("images/parRightUp.png"), new Image("images/parRightDown.png"));
		this.rightPar.getElement().setClassName("parenthesis");
		
		// Get name from column
		this.setWidth("100%");
		if(column instanceof IntervalTableColumn){
			this.columnName = this.getIntervalColumns();
		} else {
			this.columnName = new Label(column.getName());
			this.columnName.setWidth("100px");
		}
		
		// Decide which ValuePanel to use
		this.operator = new ListBox();

		this.value = this.getDifferentPart();
		inner.add(leftPar);
		inner.add(columnName);
		inner.add(operator);
		inner.add(value);
		
		this.deleteEventController = new FilterDeleteController(this);
		this.getElement().addClassName("filterPanelClass");
		for(Widget w: inner){
			w.getElement().getStyle().setMarginRight(5, Unit.PX);
		}
		HorizontalPanel lilWrapper = new HorizontalPanel();
		lilWrapper.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		lilWrapper.setWidth("100%");
		HorizontalPanel lilPanel = new HorizontalPanel();
		
		lilPanel.add(rightPar);
		lilPanel.add(up);
		lilPanel.add(down);
		
		for (Widget w : lilPanel)
		{
			w.getElement().getStyle().setMarginRight(5, Unit.PX);
		}
		lilPanel.add(delete);
		lilWrapper.add(lilPanel);
		this.add(inner);
		this.add(lilWrapper);
	}
	
	
	public void addFilterDeleteHandler(FilterDeleteHandler handler)
	{
		this.deleteEventController.addFilterDeleteHandler(handler);
	}
	
	protected abstract Widget getDifferentPart();
	protected abstract Widget getIntervalColumns();

	@Override
	public void onClick(ClickEvent event) {
		
		TablePresenter.get().setEdited();
		if (event.getSource() == delete)
		{
			deleteEventController.fireEvent(new FilterDeleteEvent());
		}
		
		
	}
	
	public Column getColumn(){
		return this.column;
	}
	
	public void setColumn(Column col) {
		this.column = col;
	}
	
	public void setOperators(List<String> ops)
	{
		for (String op : ops){
			this.operator.addItem(op);
		}
	}
	public boolean isNullSelected() {
		for (int i = 0; i < this.operator.getItemCount(); ++i) {
			if (operator.isItemSelected(i) && (operator.getItemText(i).contains("NULL"))) {
				return true;
			}
		}
		return false;
	}
	public void removeOperator() {
		this.operator.setVisible(false);
	}
	
	public ListBox getOperator() {
		return this.operator;
	}
	
	public int getOpIndex()
	{
		return this.operator.getSelectedIndex();
	}

	public abstract Filter getFilter();
	
	public boolean lbrack(){
		return this.leftPar.isDown();
	}
	public boolean rbrack(){
		return this.rightPar.isDown();
	}
	public void setLBrack(boolean val) {
		this.leftPar.setValue(val);
	}
	public void setRBrack(boolean val) {
		this.rightPar.setValue(val);
	}
	public abstract void storeState(RebuildDataFilterPanel rb);
	public abstract void setState(RebuildDataFilterPanel rb);
	public abstract String getDefinitionString();
}
