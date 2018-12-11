package btaw.client.modules.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import btaw.client.event.PersonSelectEvent;
import btaw.client.event.PersonSelectHandler;
import btaw.client.event.QueryChangedEvent;
import btaw.client.event.handlers.QueryChangeHandler;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.category.CategoryPresenter;
import btaw.client.modules.category.CategoryView;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.filter.FilterView;
import btaw.client.modules.person.PersonPresenter;
import btaw.client.modules.person.PersonView;
import btaw.client.modules.psa.PSA_Presenter;
import btaw.client.modules.psa.PSA_View;
import btaw.client.modules.table.TablePresenter;
import btaw.client.modules.table.TableView;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.Query;
import btaw.shared.model.query.Query.Order;
import btaw.shared.model.query.Table;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.FunctionTableColumn;
import btaw.shared.model.query.column.StringTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.filter.AggregateFunction;
import btaw.shared.model.query.filter.NestedFilter;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class MainPresenter extends Presenter {

	public interface View extends IView {
		FilterView getFilterView();
		TableView getTableView();
		CategoryView getCategoryView();
		void working(boolean cancellable);
		void finished();
		Button getCancelButton();
		void maximizeTableView(boolean val);
		SplitLayoutPanel getMainPanel();
		Button getIAgreeButton();
	}

	private static MainPresenter mp;
	public static MainPresenter get() {
		return mp;
	}

	private View ui;
	private final FilterPresenter filterPresenter;
	private final TablePresenter tablePresenter;
	private CategoryPresenter categoryPresenter;
	private Boolean abortingQuery = false;
	private Boolean keyDepress = false;
	private Boolean maxTableView = false;
	private Boolean minTableView = false;
	private double tableSize;
	
	public MainPresenter(Presenter parent, View v) {
		super(parent);
		this.ui = v;
		this.filterPresenter = new FilterPresenter(this, ui.getFilterView());
		this.tablePresenter = new TablePresenter(this, ui.getTableView());
		this.categoryPresenter = new CategoryPresenter(this, ui.getCategoryView());
		mp = this;
	}

	@Override
	protected void bindAll() {
		this.ui.getIAgreeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				((MainView)ui).datafinished();
			}
		});
		this.ui.getCancelButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rpcService.cancelRawQuery(new PresenterCallback<Void>(MainPresenter.this) {

					@Override
					protected void success(Void result) {
						System.err.println("CANCEL BUTTON HIT, SUCCESS -- RAW QUERY");
					}
					
				});
				rpcService.cancelInsert(new PresenterCallback<Void>(MainPresenter.this) {

					@Override
					protected void success(Void result) {
						System.err.println("CANCEL BUTTON HIT, SUCCESS -- INSERT QUERY");
					}
					
				});
				rpcService.cancelValidatedQuery(new PresenterCallback<Void>(MainPresenter.this) {

					@Override
					protected void success(Void result) {
						System.err.println("CANCEL BUTTON HIT, SUCCESS -- VALIDATED QUERY");
					}
					
				});
			}
		});
		HandlerRegistration previousQueryHandler = Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent ne = event.getNativeEvent();
				if(ne.getCtrlKey() && (ne.getKeyCode() =='r' || ne.getKeyCode() == 'R')) {
					ne.preventDefault();
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							if(!keyDepress) {
								CategoryPresenter.get().getPreviousQuery();
								keyDepress = true;
							} else {
								keyDepress = false;
							}
						}
					});
				}
			}
			
		});
		bindEvent(QueryChangedEvent.TYPE, new QueryChangeHandler() {
			
			@Override
			public void OnQueryChangedEvent(QueryChangedEvent e) {
				MainPresenter.this.ui.working(true);
				if (filterPresenter.isExpertMode()) {
					rpcService.updateTable(filterPresenter.getExpertText(), 
							new PresenterCallback<HashMap<List<String>,List<TableRow>>>(MainPresenter.this) {
						@Override
						protected void success(
								HashMap<List<String>, List<TableRow>> result) {
							tablePresenter.clearAllColumns();
							for(Map.Entry<List<String>, List<TableRow>> entry: result.entrySet()) {
								for(String col : entry.getKey()) {
									Table tmpTbl = new Table(col);
									TableColumn tmpCol = new StringTableColumn(col, col, "", tmpTbl);
									tablePresenter.addColumn(tmpCol);
								}
								tablePresenter.rebuildModelData(entry.getValue());
							}
							MainPresenter.this.ui.finished();
							filterPresenter.saveUnnamedQuery();
						}
					});
				} else if (filterPresenter.isKMMode()) {
					System.err.println("KM");
					NestedFilter f = filterPresenter.getKMFilter();
					if (f == null)
						return;
					Set<Column> fcols = f.getColumns();
					Query query = new Query();
					List<Column> cols = tablePresenter.getColumns();
					List<Column> temp = new ArrayList<Column>();
					temp.addAll(cols);
					for (Column col : fcols) {
						if (!cols.contains(col)) {
							temp.add(col);
						}
					}
					query.setModality(FilterView.Modality.QUERY);
					query.addColumns(temp);
					query.setFilter(f);
					query.setOrderBy(tablePresenter.getSortColumn());
					query.setOrder(Query.Order.valueOf(Order.class,
							tablePresenter.getSortOrder()));
					if(abortingQuery) {
						abortingQuery = false;
						return;
					}
					rpcService.updateTable(query,
							new PresenterCallback<List<TableRow>>(
									MainPresenter.this) {
								@Override
								protected void success(List<TableRow> result) {
									tablePresenter.rebuildModelData(result);
									MainPresenter.this.ui.finished();
									filterPresenter.saveUnnamedQuery();
								}
							});
				} else if (filterPresenter.isQueryMode()) {
					System.err.println("Q");
					// Builds Query and Gets results from DB.
					NestedFilter f = filterPresenter.getFilter();
					if (f == null)
						return;
					Set<Column> fcols = f.getColumns();
					Query query = new Query();
					List<Column> cols = tablePresenter.getColumns();
					List<Column> temp = new ArrayList<Column>();
					temp.addAll(cols);
					for (Column col : fcols) {
						if (!cols.contains(col)) {
							temp.add(col);
						}
					}
					query.setModality(FilterView.Modality.QUERY);
					query.addColumns(temp);
					query.setFilter(f);
					query.setOrderBy(tablePresenter.getSortColumn());
					query.setOrder(Query.Order.valueOf(Order.class,
							tablePresenter.getSortOrder()));
					if(abortingQuery) {
						abortingQuery = false;
						return;
					}
					rpcService.updateTable(query,
							new PresenterCallback<List<TableRow>>(
									MainPresenter.this) {
								@Override
								protected void success(List<TableRow> result) {
									tablePresenter.rebuildModelData(result);
									MainPresenter.this.ui.finished();
									filterPresenter.saveUnnamedQuery();
								}
							});
				} else if (filterPresenter.isAggregateMode()) {
					tablePresenter.clearSentinalColumns();
					NestedFilter af = filterPresenter.getAggregateFilter();
					if (af == null)
						return;
					Query query = new Query();
					query.setModality(FilterView.Modality.AGGREGATE);
					query.setAggregateColumns((ArrayList<Column>) tablePresenter.getColumns());
					query.setGroupByColumns((ArrayList<Column>) filterPresenter.getGroupByColumns());
					query.setAggregateFunctions((ArrayList<AggregateFunction>) filterPresenter.getAggregateFunctions());
					query.setAggregateFilter(af);
					query.setOrderBy(tablePresenter.getSortColumn());
					query.setOrder(Query.Order.valueOf(Order.class,
							tablePresenter.getSortOrder()));
					for(AggregateFunction aggFunc : filterPresenter.getAggregateFunctions()) {
						tablePresenter.addColumn(
								new FunctionTableColumn(aggFunc.getFunction() + "( " + aggFunc.getColumn().getName() + " )",
										aggFunc.getFunction() + "_" + aggFunc.getColumn().toSQLString().replaceAll("\\.", "_"),
										new Table("SENTINAL")));
					}
					if(abortingQuery) {
						abortingQuery = false;
						return;
					}
					rpcService.updateTable(query,
							new PresenterCallback<List<TableRow>>(
									MainPresenter.this) {
								@Override
								protected void success(List<TableRow> result) {
									tablePresenter.rebuildModelData(result);
									MainPresenter.this.ui.finished();
									filterPresenter.saveUnnamedQuery();
								}
							});
				}
			}
		});
		
		
		bindEvent(PersonSelectEvent.TYPE, new PersonSelectHandler() {
			
			@Override
			public void OnPersonSelectEvent(PersonSelectEvent e) {
				if(e.getPid() == null)
					return;
				MainPresenter.this.working(false);
				System.err.println("PERSON PRESENTER CLICKED");
				//PersonPresenter pp = new PersonPresenter(MainPresenter.this, new PersonView(), e.getPid().replaceAll("\\*", ""));	
				//pp.setActive();
				PSA_Presenter psa_presenter = new PSA_Presenter(MainPresenter.this, new PSA_View(), e.getPid().replaceAll("\\*", ""));
				psa_presenter.setActive();
			}
		});
		
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}

	@Override
	protected void activated(HasWidgets container) {
		((MainView)ui).data_agreement();
		filterPresenter.setActive();
		tablePresenter.setActive();
		categoryPresenter.setActive();
	}

	public void maximizeTableView(){
		if (!minTableView){
			maxTableView = !maxTableView;
			ui.maximizeTableView(maxTableView);
		} else {
			this.minimizeTableView();
			this.maximizeTableView();
		}
	}
	
	public void minimizeTableView(){
		if (!maxTableView) {
			if (!minTableView){
				tableSize = ui.getMainPanel().getWidgetSize(ui.getFilterView());
				ui.getMainPanel().setWidgetSize(ui.getFilterView(),Window.getClientHeight()-28);
				minTableView = !minTableView;
			} else {
				ui.getMainPanel().setWidgetSize(ui.getFilterView(),tableSize);
				minTableView = !minTableView;
			}
		} else {
			if (minTableView){
				this.maximizeTableView();
			} else {
				this.maximizeTableView();
				this.minimizeTableView();
			}
		}
	}

	public FilterPresenter getFilterPresenter() {
		return filterPresenter;
	}

	public TablePresenter getTablePresenter() {
		return tablePresenter;
	}

	public CategoryPresenter getCategoryPresenter() {
		return categoryPresenter;
	}
	
	public void working(boolean cancellable) {
		this.ui.working(cancellable);
	}
	
	public void finished() {
		this.ui.finished();
	}

	public Boolean getAbortingQuery() {
		return abortingQuery;
	}

	public void setAbortingQuery(Boolean abortingQuery) {
		this.abortingQuery = abortingQuery;
	}

}
