package btaw.client.view.filter;

import java.util.ArrayList;
import java.util.Date;

import btaw.client.event.controllers.PreviewDropEventController;
import btaw.client.event.handlers.HasDropHandlers;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.IntervalTableColumn;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.filter.DateTableColumnFilter;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.FloatTableColumnFilter;
import btaw.shared.model.query.filter.IntervalTableColumnFilter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.value.LiteralDateValue;
import btaw.shared.model.query.value.LiteralFloatValue;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;

public class IntervalFilterPanel extends TableFilterPanel implements
		ValueChangeHandler<Date>, ChangeHandler {

	private Date date;
	private TextBox textBox;
	private ListBox timeInterval;
	
	private final PreviewDropEventController date1DropController;
	private final PreviewDropEventController date2DropController;
	private final PickupDragController dragController;
	private Label date1;
	private Label date2;
	
	public IntervalFilterPanel(Column column) {
		super(column);
		
		this.date1.setText(((IntervalTableColumn)this.getColumn()).getStartColumn().getName());
		this.date1.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.date1.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.date1.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		
		this.date2.setText(((IntervalTableColumn)this.getColumn()).getEndColumn().getName());
		this.date2.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.date2.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.date2.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		
		this.dragController = null;
		this.date1DropController = null;
		this.date2DropController = null;
	}
	
	public IntervalFilterPanel(Column column, PickupDragController dragController){
		super(column);
	
		this.dragController = dragController;
		
		this.date1DropController = new PreviewDropEventController(date1);
		this.date2DropController = new PreviewDropEventController(date2);
		dragController.registerDropController(date1DropController);
		dragController.registerDropController(date2DropController);
	}
	
	public void updateStartDate(){
		this.date1.setText(((IntervalTableColumn)this.getColumn()).getStartColumn().getName());
		this.date1.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.date1.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.date1.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		dragController.unregisterDropController(date1DropController);
	}
	
	public void updateEndDate(){
		this.date2.setText(((IntervalTableColumn)this.getColumn()).getEndColumn().getName());
		this.date2.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.date2.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.date2.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		dragController.unregisterDropController(date2DropController);
	}

	@Override
	protected Panel getDifferentPart() {
		HorizontalPanel hp = new HorizontalPanel();

		ArrayList<String> ops = new ArrayList<String>(DateTableColumn.opStringList());
		this.setOperators(ops);
		
		textBox = new TextBox();
		textBox.setWidth("40px");
		textBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				TablePresenter.get().setEdited();
				try
				{
					Float.parseFloat(textBox.getText());
					textBox.getElement().getStyle().setBackgroundColor("#FFFFFF");
				}
				catch (NumberFormatException e)
				{
					if(IntervalFilterPanel.this.getOpIndex() < FloatTableColumn.Op.values().length - 2) {
						textBox.getElement().getStyle().setBackgroundColor("#FFAAAA");
						textBox.setFocus(true);
					}
				}
			}
		});

		hp.add(textBox);
		
		this.timeInterval = new ListBox();
		this.timeInterval.addItem("years");
		this.timeInterval.addItem("months");
		this.timeInterval.addItem("days");
		hp.add(timeInterval);

		return hp;
	}

	public ListBox getTimeInterval() {
		return this.timeInterval;
	}
	
	@Override
	public Filter getFilter() {
		
		Float passVal = 0.0f;
		try
		{
			passVal = Float.parseFloat(textBox.getText());
			textBox.getElement().getStyle().setBackgroundColor("#FFFFFF");
		}
		catch (NumberFormatException e)
		{
			if(IntervalFilterPanel.this.getOpIndex() < IntervalTableColumn.Op.values().length - 2) {
				textBox.getElement().getStyle().setBackgroundColor("#FFAAAA");
				textBox.setFocus(true);
			}
		}
		IntervalTableColumnFilter filter = new IntervalTableColumnFilter((IntervalTableColumn)this.getColumn(), IntervalTableColumn.Op.values()[this.getOpIndex()], passVal, this.timeInterval.getItemText(this.timeInterval.getSelectedIndex()));
		return filter;
	}

	@Override
	public void onValueChange(ValueChangeEvent<Date> event) {
		date = event.getValue();
		textBox.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(date));
		textBox.removeStyleName("highlight");
	}

	@Override
	public void onChange(ChangeEvent event) {

		DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yyyy");
		try {
			date = dtf.parse(textBox.getText());
			textBox.addStyleName("highlight");
		} catch (IllegalArgumentException e) {
			textBox.removeStyleName("highlight");
			textBox.setFocus(true);
		}

	}
	public void storeState(RebuildDataFilterPanel rb) {
		rb.setLeftPar(this.lbrack());
		rb.setRightPar(this.rbrack());
		rb.setOpIndex(this.getOperator().getSelectedIndex());
		rb.setInterval(true);
		rb.setTimeIndex(this.getTimeInterval().getSelectedIndex());
		rb.setCol(((IntervalTableColumn)this.getColumn()).getStartColumn());
		rb.setCol2(((IntervalTableColumn)this.getColumn()).getEndColumn());
		rb.setValue(this.textBox.getValue());
	}
	public void setState(RebuildDataFilterPanel rb) {
		this.setLBrack(rb.isLeftPar());
		this.setRBrack(rb.isRightPar());
		this.getOperator().setSelectedIndex(rb.getOpIndex());
		this.getTimeInterval().setSelectedIndex(rb.getTimeIndex());
		if(this.isNullSelected()){
			disableExtraBoxes();
		} else {
			this.textBox.setValue(rb.getValue(), true);
		}
		this.setColumn(new IntervalTableColumn(rb.getCol(),rb.getCol2()));
	}
	
	@Override
	public String getDefinitionString() {
		String s = "";
		
		if(this.lbrack()) {
			s += "(";
		}
		s += this.getColumn().getName() + " ";
		s += this.getOperator().getItemText(this.getOpIndex()) + " ";
		s += "'" + this.textBox.getText() + "' ";
		if(this.rbrack()) {
			s += ")";
		}
		
		return s;
	}

	@Override
	public void disableExtraBoxes() {
		this.textBox.setText("");
		this.textBox.setEnabled(false);
	}
	
	@Override
	public void enableExtraBoxes() {
		this.textBox.setEnabled(true);
	}

	@Override
	protected Widget getIntervalColumns() {
		
		this.date1 = new Label("Drag Start Date Here");
		this.date1.getElement().getStyle().setColor("rgba(0, 0, 0, 0.6)");
		this.date1.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		this.date1.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.2)");
		this.date2 = new Label("Drag End Date Here");
		this.date2.getElement().getStyle().setColor("rgba(0, 0, 0, 0.6)");
		this.date2.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		this.date2.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.2)");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(date1);
		hp.add(new Label("  -  "));
		hp.add(date2);
		
		return hp;
	}

	public PreviewDropEventController getDropController1() {
		return this.date1DropController;
	}
	
	public PreviewDropEventController getDropController2() {
		return this.date2DropController;
	}
	
	public void unregisterDropControllers() {
		if(dragController!=null){
			this.dragController.unregisterDropController(date1DropController);
			this.dragController.unregisterDropController(date2DropController);
		}
	}

}
