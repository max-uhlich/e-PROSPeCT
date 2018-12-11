package btaw.client.modules.kaplanmeier;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import btaw.client.event.QueryChangedEvent;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.filter.FilterView;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.Query;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.column.IntegerTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.filter.AggregateFunction;
import btaw.shared.model.query.filter.NestedFilter;

public class KaplanMeierNamePresenter extends Presenter {

	public interface View extends IView {
		Button getCancelButton();
		Button getDoneButton();
		void close();
		String getName();
		void clearName();
		ListBox getOperatorLB();
		ListBox getColNamesLB();
		Label getNameL();
		String getSelectedColumnName();
		String getSelectedFunctionName();
	}
	
	private final View ui;
	
	public KaplanMeierNamePresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
	}
	
	@Override
	protected void bindAll() {
		ui.getCancelButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.clearName();
				ui.close();
			}
		});
		ui.getDoneButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String op = ui.getSelectedFunctionName();
				if(op.equals(KaplanMeierNameView.Function.KAPLANMEIER.toString())) {
					if (!FilterPresenter.get().isResultsCurrent()) {
						TablePresenter.get().setCurrent();
						fireEvent(new QueryChangedEvent());
						ui.getDoneButton().setText("Done");
					} else {
						FilterPresenter.get().addCurrPids(ui.getName());
						ui.close();
					}
				} else {
					NestedFilter filter = FilterPresenter.get().getKMFilter();
					if(filter == null)
						return;
					
					Query query = new Query();
					query.setModality(FilterView.Modality.AGGREGATE);
					List<Column> groupByCols = new ArrayList<Column>();
					List<AggregateFunction> aggregateFunction = new ArrayList<AggregateFunction>();
					AggregateFunction af = new AggregateFunction();
					AggregateFunction pidCount = new AggregateFunction();
					for(Column col : TablePresenter.get().getColumns()) {
						if(col.getName().equals(ui.getSelectedColumnName())) {
							af.setColumn((TableColumn) col);
						} else if(col.getName().equals("Patient ID")) {
							pidCount.setColumn((TableColumn) col);
						} else if(!col.getName().equals(ui.getSelectedColumnName()) && !col.getName().equals("Patient ID")) {
							groupByCols.add(col);
						}
					}
					query.setGroupByColumns((ArrayList<Column>) groupByCols);
					query.setAggregateColumns((ArrayList<Column>) groupByCols);
					af.setFunction(ui.getSelectedFunctionName());
					pidCount.setFunction("COUNT");
					aggregateFunction.add(af);
					aggregateFunction.add(pidCount);
					query.setAggregateFunctions((ArrayList<AggregateFunction>) aggregateFunction);
					query.setAggregateFilter(filter);
					MainPresenter.get().working(true);
					rpcService.getAggregateResult(query, new AsyncCallback<String>() {
						
						@Override
						public void onSuccess(String result) {
							String name = KaplanMeierNamePresenter.this.ui.getName();
							if(name == null || name.equals("")) {
								name = KaplanMeierNamePresenter.this.ui.getSelectedFunctionName()
										+ " of " + KaplanMeierNamePresenter.this.ui.getSelectedColumnName();
							}
							FilterPresenter.get().addToSubgroupResultsPanel(name + " = " + result);
							FilterPresenter.get().addFunctionSubgroup(name);
							MainPresenter.get().finished();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							
						}
					});
					ui.close();
				}
			}
		});
		ui.getOperatorLB().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String op = ui.getSelectedFunctionName();
				if(op.equals(KaplanMeierNameView.Function.KAPLANMEIER.toString())) {
					ui.getColNamesLB().clear();
					ui.getColNamesLB().addItem("Group");
					if (!FilterPresenter.get().isResultsCurrent()) {
						ui.getDoneButton().setText("Execute Query");
					} else {
						ui.getDoneButton().setText("Done");
					}
				} else {
					ui.getDoneButton().setText("Done");
					ui.getColNamesLB().clear();
					if (op.equals(KaplanMeierNameView.Function.AVG.toString())) {
						addNumberCols();
					} else if (op.equals(KaplanMeierNameView.Function.SUM
							.toString())) {
						addNumberCols();
					} else if (op.equals(KaplanMeierNameView.Function.MAX
							.toString())) {
						addNumberCols();
						addDateCols();
					} else if (op.equals(KaplanMeierNameView.Function.MIN
							.toString())) {
						addNumberCols();
						addDateCols();
					} else if (op.equals(KaplanMeierNameView.Function.COUNT
							.toString())) {
						addAll();
					}
				}
			}
		});
		ui.getColNamesLB().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
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
	
	public String getName() {
		return ui.getName();
	}
	
	private void addNumberCols() {
		for(Column col : TablePresenter.get().getColumns()) {
			if(col instanceof IntegerTableColumn
				|| col instanceof FloatTableColumn) {
				ui.getColNamesLB().addItem(col.getName());
			}
		}
	}
	
	private void addDateCols() {
		for(Column col : TablePresenter.get().getColumns()) {
			if(col instanceof DateTableColumn) {
				ui.getColNamesLB().addItem(col.getName());
			}
		}
	}
	
	private void addAll() {
		for(Column col : TablePresenter.get().getColumns()) {
			if(col.getName().equals("Patient ID"))
				continue;
			ui.getColNamesLB().addItem(col.getName());
		}
	}
}
