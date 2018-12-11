package btaw.client.view.filter;

import java.util.ArrayList;
import java.util.Date;

import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.filter.DateTableColumnFilter;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.value.LiteralDateValue;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import btaw.shared.model.query.column.TableColumn;

public class DateFilterPanel extends TableFilterPanel implements
		ValueChangeHandler<Date>, ChangeHandler {

	private Date date;
	private TextBox textBox;
	
	public DateFilterPanel(Column column) {
		super(column);
	}

	@Override
	protected Panel getDifferentPart() {
		HorizontalPanel hp = new HorizontalPanel();
		final PopupPanel popup = new PopupPanel(true);

		ArrayList<String> ops = new ArrayList<String>(DateTableColumn.opStringList());
		this.setOperators(ops);

		textBox = new TextBox();
		textBox.setWidth("80px");
		textBox.addChangeHandler(this);

		final DatePicker dp = new DatePicker();
		popup.add(dp);

		dp.addValueChangeHandler(this);

		textBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				dp.setValue(date, true);
				if (date != null)
					dp.setCurrentMonth(date);
				final int left, bottom;
				left = textBox.getAbsoluteLeft();
				bottom = textBox.getAbsoluteTop() + textBox.getOffsetHeight();
				popup.setPopupPositionAndShow(new PositionCallback() {

					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						popup.setPopupPosition(left, bottom);

					}
				});
			}
		});
		
		textBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				TablePresenter.get().setEdited();
			}
		});
		hp.add(textBox);

		return hp;
	}

	@Override
	public Filter getFilter() {

		if (this.date == null && this.getOpIndex() < DateTableColumn.Op.values().length - 2) {
			textBox.addStyleName("highlight");
			textBox.setFocus(true);
			return null;
		}
		
		if(this.getOpIndex() >= DateTableColumn.Op.values().length - 2) {
			return new DateTableColumnFilter(
					((DateTableColumn) this.getColumn()),
					DateTableColumn.Op.values()[this.getOpIndex()],
					null);
		}
		
		DateTableColumnFilter filter = new DateTableColumnFilter(
				((DateTableColumn) this.getColumn()),
				DateTableColumn.Op.values()[this.getOpIndex()],
				new LiteralDateValue(DateTimeFormat.getFormat("yyyy-MM-dd")
						.format(date)));

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
		rb.setCol(this.getColumn());
		rb.setValue(this.textBox.getValue());
	}
	public void setState(RebuildDataFilterPanel rb) {
		this.setLBrack(rb.isLeftPar());
		this.setRBrack(rb.isRightPar());
		this.getOperator().setSelectedIndex(rb.getOpIndex());
		if(this.isNullSelected()){
			disableExtraBoxes();
		} else {
			this.textBox.setValue(rb.getValue(), true);
			date = DateTimeFormat.getFormat("MM/dd/yyyy").parse(textBox.getText());
		}
		this.setColumn(rb.getCol());
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
	
	protected Widget getIntervalColumns() {
		return null;
	}

}
