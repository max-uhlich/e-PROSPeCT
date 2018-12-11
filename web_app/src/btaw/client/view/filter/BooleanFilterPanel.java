package btaw.client.view.filter;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.BooleanTableColumn;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.filter.BooleanTableColumnFilter;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.value.LiteralBooleanValue;


public class BooleanFilterPanel extends TableFilterPanel {

	private ListBox values;
	public BooleanFilterPanel(Column column) {
		super(column);
	}

	@Override
	protected Panel getDifferentPart() {
		HorizontalPanel hp = new HorizontalPanel();
		
		this.setOperators(BooleanTableColumn.opStringList());
		
		values = new ListBox();
		values.addItem("TRUE");
		values.addItem("FALSE");
		
		values.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				TablePresenter.get().setEdited();
			}
		});
		hp.add(values);
		return hp;
	}

	@Override
	public Filter getFilter() 
	{
		boolean passVal = Boolean.parseBoolean(this.values.getValue(this.values.getSelectedIndex()));
		BooleanTableColumnFilter filter = new BooleanTableColumnFilter((BooleanTableColumn)this.getColumn(), BooleanTableColumn.Op.values()[this.getOpIndex()], new LiteralBooleanValue(passVal));
		
		return filter;
	}
	public void storeState(RebuildDataFilterPanel rb) {
		rb.setLeftPar(this.lbrack());
		rb.setRightPar(this.rbrack());
		rb.setOpIndex(this.getOperator().getSelectedIndex());
		rb.setCol(this.getColumn());
		rb.setValueIndex(values.getSelectedIndex());
	}
	public void setState(RebuildDataFilterPanel rb) {
		this.setLBrack(rb.isLeftPar());
		this.setRBrack(rb.isRightPar());
		this.getOperator().setSelectedIndex(rb.getOpIndex());
		if(this.isNullSelected()){
			disableExtraBoxes();
		} else {
			values.setSelectedIndex(rb.getValueIndex());
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
		s += "'" + this.values.getItemText(this.values.getSelectedIndex()) + "' ";
		if(this.rbrack()) {
			s += ")";
		}
		
		return s;
	}

	@Override
	public void disableExtraBoxes() {
		this.values.setEnabled(false);
	}
	
	@Override
	public void enableExtraBoxes() {
		this.values.setEnabled(true);
	}
	
	protected Widget getIntervalColumns() {
		return null;
	}
}
