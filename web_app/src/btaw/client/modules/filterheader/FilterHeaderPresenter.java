package btaw.client.modules.filterheader;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.filterheader.FilterHeaderView.FilterType;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.TableColumn;

public class FilterHeaderPresenter extends Presenter {

	public interface View extends IView {
		void addFilterRow(boolean isFirst);
		Button getCancelButton();
		Button getOkButton();
		Button getAddButton();
		DialogBox getMain();
		ArrayList<HorizontalPanel> getFilters();
		FilterType getCurrentType();
	}

	View ui;
	Column col;
	List<TableRow> currTableCopy;
	
	public FilterHeaderPresenter(Presenter parent, View ui, Column col, List<TableRow> currTableCopy) {
		super(parent);
		this.ui = ui;
		this.col = col;
		this.currTableCopy = currTableCopy;
	}

	@Override
	protected void bindAll() {
		this.ui.getAddButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.addFilterRow(false);
			}
		});
		this.ui.getCancelButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.getMain().hide();
			}
		});
		this.ui.getOkButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ArrayList<TableRow> newTable = new ArrayList<TableRow>();
				for(TableRow tr : currTableCopy) {
					for (HorizontalPanel hp : ui.getFilters()) {
						Widget w1 = hp.getWidget(1);
						Widget w2 = hp.getWidget(2);
						ListBox lb = (ListBox) w1;
						String s = lb.getItemText(lb.getSelectedIndex());
						TextBox tb = (TextBox) w2;
						String s2 = tb.getText();
						String tableStr = tr.getRowData(getKey());
						
						// Bret: was previously crashing on nulls, now keeping all nulls by default
						if (tableStr==null){
							if (!s.equals("Is Null"))
								newTable.add(tr);
							continue;
						}
						if (s.equals("=")) {
							if(!s2.equals(tableStr)) {
								newTable.add(tr);
							}
						} else if (s.equals("\u2260")) {
							if (tableStr!=null){
								if(s2.equals(tableStr)) {
									newTable.add(tr);
								}
							}
						} else if (s.equals(">")) {
							if(col instanceof DateTableColumn) {
								if(tableStr.compareTo(s2) <= 0) {
									newTable.add(tr);
								}
							} else {
								try {
									double dbl1 = Double.parseDouble(tableStr);
									double dbl2 = Double.parseDouble(s2);
									if(!(dbl1 > dbl2)) {
										newTable.add(tr);
									}
								} catch(NumberFormatException e) {
									e.printStackTrace();
								}
							}
						} else if (s.equals("<")) {
							if(col instanceof DateTableColumn) {
								if(tableStr.compareTo(s2) >= 0) {
									newTable.add(tr);
								}
							} else {
								try {
									if (tableStr!=null){
										double dbl1 = Double.parseDouble(tableStr);
										double dbl2 = Double.parseDouble(s2);
										if(!(dbl1 < dbl2)) {
											newTable.add(tr);
										}
									}
								} catch(NumberFormatException e) {
									e.printStackTrace();
								}
							}
						} else if (s.equals("Is Null")) {
							if(!tableStr.equals("")) {
								newTable.add(tr);
							}
						}
					}
				}
				TablePresenter.get().rebuildModelData(newTable);
				ui.getMain().hide();
			}
		});
	}
	
	public String getKey() {
		String s =((TableColumn)col).getSQLName();
		int i = s.lastIndexOf('.');
		if(i>0){
			s=s.substring(i+1, s.length());
		}
		return s;
	}

	@Override
	protected IView getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void activated(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}
	
}
