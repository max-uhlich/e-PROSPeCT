package btaw.client.view.filter;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.FloatTableColumnFilter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.value.LiteralFloatValue;

public class FloatFilterPanel extends TableFilterPanel {

	private TextBox tb;
	public FloatFilterPanel(Column column) {
		super(column);
	}

	@Override
	protected Panel getDifferentPart() {
		
		HorizontalPanel hp = new HorizontalPanel();
		tb = new TextBox();
		ArrayList<String> ops = new ArrayList<String>(FloatTableColumn.opStringList());
		this.setOperators(ops);
		tb.setWidth("40px");
		
		tb.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				TablePresenter.get().setEdited();
				try 
				{
					Float.parseFloat(tb.getText());
					tb.getElement().getStyle().setBackgroundColor("#FFFFFF");
				}
				catch (NumberFormatException e)
				{
					if(FloatFilterPanel.this.getOpIndex() < FloatTableColumn.Op.values().length - 2) {
						tb.getElement().getStyle().setBackgroundColor("#FFAAAA");
						tb.setFocus(true);
					}
				}
			}
		});
		hp.add(tb);
		return hp;
	}

	@Override
	public Filter getFilter() {
		Float passVal = 0.0f;
		try 
		{
			passVal = Float.parseFloat(tb.getText());
			tb.getElement().getStyle().setBackgroundColor("#FFFFFF");
		}
		catch (NumberFormatException e)
		{
			if(FloatFilterPanel.this.getOpIndex() < FloatTableColumn.Op.values().length - 2) {
				tb.getElement().getStyle().setBackgroundColor("#FFAAAA");
				tb.setFocus(true);
			}
		}
		FloatTableColumnFilter filter = new FloatTableColumnFilter((FloatTableColumn)this.getColumn(), FloatTableColumn.Op.values()[this.getOpIndex()], new LiteralFloatValue(passVal));
		return filter;
	}

	public void storeState(RebuildDataFilterPanel rb) {
		rb.setLeftPar(this.lbrack());
		rb.setRightPar(this.rbrack());
		rb.setOpIndex(this.getOperator().getSelectedIndex());
		rb.setCol(this.getColumn());
		rb.setValue(this.tb.getText());
	}
	public void setState(RebuildDataFilterPanel rb) {
		this.setLBrack(rb.isLeftPar());
		this.setRBrack(rb.isRightPar());
		this.getOperator().setSelectedIndex(rb.getOpIndex());
		if(this.isNullSelected()){
			disableExtraBoxes();
		} else {
			this.tb.setText(rb.getValue());
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
		s += "'" + this.tb.getText() + "' ";
		if(this.rbrack()) {
			s += ")";
		}
		
		return s;
	}

	@Override
	public void disableExtraBoxes() {
		this.tb.setText("");
		this.tb.setEnabled(false);		
	}
	
	@Override
	public void enableExtraBoxes() {
		this.tb.setEnabled(true);
	}
	
	protected Widget getIntervalColumns() {
		return null;
	}
}
