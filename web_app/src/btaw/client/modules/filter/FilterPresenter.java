package btaw.client.modules.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import btaw.client.event.AddFilterEvent;
import btaw.client.event.AddFilterHandler;
import btaw.client.event.AggregateDeleteEvent;
import btaw.client.event.ClearEvent;
import btaw.client.event.FilterDeleteEvent;
import btaw.client.event.GroupByDeleteEvent;
import btaw.client.event.HeaderMenuSelectionEvent;
import btaw.client.event.PreviewDropEvent;
import btaw.client.event.QueryChangedEvent;
import btaw.client.event.UpDownEvent;
import btaw.client.event.UpDownHandler;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.event.handlers.AggregateDeleteHandler;
import btaw.client.event.handlers.ClearEventHandler;
import btaw.client.event.handlers.DropEventHandler;
import btaw.client.event.handlers.FilterDeleteHandler;
import btaw.client.event.handlers.GroupByDeleteHandler;
import btaw.client.event.handlers.HasDropHandlers;
import btaw.client.event.handlers.HeaderMenuSelectionHandler;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.category.CategoryPresenter;
import btaw.client.modules.filter.FilterView.Modality;
import btaw.client.modules.kaplanmeier.KaplanMeierNamePresenter;
import btaw.client.modules.kaplanmeier.KaplanMeierNameView;
import btaw.client.modules.kaplanmeier.KaplanMeierPresenter;
import btaw.client.modules.kaplanmeier.KaplanMeierView;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.popups.OverwritePresenter;
import btaw.client.modules.popups.OverwriteView;
import btaw.client.modules.popups.PasswordChangePresenter;
import btaw.client.modules.popups.PasswordChangeView;
import btaw.client.modules.popups.PermissionPresenter;
import btaw.client.modules.popups.PermissionView;
import btaw.client.modules.savequery.DeleteQueryPresenter;
import btaw.client.modules.savequery.DeleteQueryView;
import btaw.client.modules.savequery.SaveQueryPresenter;
import btaw.client.modules.savequery.SaveQueryView;
import btaw.client.modules.synonym.SynonymPresenter;
import btaw.client.modules.synonym.SynonymView;
import btaw.client.modules.synth.SynthPresenter;
import btaw.client.modules.synth.SynthView;
import btaw.client.modules.table.ExportDataPresenter;
import btaw.client.modules.table.ExportDataView;
import btaw.client.modules.table.TablePresenter;
import btaw.client.modules.tab.KaplanMeierTab;
import btaw.client.modules.tab.PatientSummaryTab;
import btaw.client.modules.tab.PsaAggregateTab;
import btaw.client.modules.tab.Tab;
import btaw.client.view.filter.AggregatePanel;
import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.GroupByPanel;
import btaw.client.view.filter.ImageFilterPanel;
import btaw.client.view.filter.IntervalFilterPanel;
import btaw.client.view.filter.OperatorPanel;
import btaw.client.view.filter.SubgroupFilterPanel;
import btaw.client.view.panels.DefinitionPanel;
import btaw.client.view.panels.FunctionPanel;
import btaw.client.view.widgets.ColumnLabel;
import btaw.client.view.widgets.MagicTextColumn;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.server.model.db.Database;
import btaw.shared.model.Category;
import btaw.shared.model.DefinitionData;
import btaw.shared.model.FunctionData;
import btaw.shared.model.PSA_Graph;
import btaw.shared.model.KM_Graph;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.Query;
import btaw.shared.model.query.Query.Order;
import btaw.shared.model.query.Table;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.column.IntervalTableColumn;
import btaw.shared.model.query.column.SavedDefinitionColumn;
import btaw.shared.model.query.column.SynthTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.filter.AggregateFunction;
import btaw.shared.model.query.filter.FilterStateException;
import btaw.shared.model.query.filter.NestedFilter;
import btaw.shared.model.query.filter.NestedFilter.Op;
import btaw.shared.model.query.saved.RebuildDataAggregatePanel;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.saved.SavedQuery;
import btaw.shared.util.ObjectCloner;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FilterPresenter extends Presenter implements FilterDeleteHandler, AggregateDeleteHandler, GroupByDeleteHandler, ClickHandler, HeaderMenuSelectionHandler{
	
	public interface View extends IView{
		
		HasDropHandlers getAllFiltersDropController();
		HasDropHandlers getKMFilterByDropController();
		FlexTable getMainPanel();
		FlexTable getAggregatePanel();
		FlexTable getGroupByPanel();
		FlexTable getAggregateFilterPanel();
		FlexTable getDefineFT();
		FlexTable getKMSubgroupPanel();
		Button getLogoutButton();
		Button getChangePasswordButton();
		Button getImageQueryButton();
		Button getApplyButton();
		Button getClearButton();
		Button getSynthButton();
		void clearAll();
		List<FilterPanel> getFilterPanels();
		TabLayoutPanel getModality();
		TextArea getExpertInput();
		void setExpertMode();
		Boolean isExpertMode();
		void setAggregateMode();
		Boolean isAggregateMode();
		void setQueryMode();
		Boolean isQueryMode();
		void setKMMode();
		Boolean isKMMode();
		Button getExportButton();
		//Button getSaveQueryButton();
		Button getDeleteQueryButton();
		Modality getMode();
		Button getKaplanMeierButton();
		Button getAddToKaplanMeier();
		Button getSynonymButton();
		Button addDefinitionButton();
		void setKMUrl(String url);
		Image getKMChart();
		FlexTable getFunctionsFT();
		void setExecuteRed();
		void clearExecuteRed();
		FlexTable getDefinitionsFT();
		Button getAddFunctionButton();
		TextBox getDefName();
		Button getClearDefButton();
		//Button getSaveQuery();
		Button getPsaAggregateButton();
		Button getIntervalQueryButton();
		PickupDragController getDragController();
		Button getSummarizeButton();
		Button getDashboardButton();
		TextBox getSummarizeTextBox();
		void setDatacutDate(String result);
	}

	private final View ui;
	private Integer unique=0;
	private SynthPresenter synth;
	private LinkedHashMap<String, Integer> regionsOfInterest;
	private final HeaderMenuSelectionController headerController;

	private boolean isTooltipsOn = false;
	private Presenter presenter;
	private Button exportDoneButton;
	private Button saveQCancelButton;
	private Button saveQDoneButton;
	private Button deleteQCancelButton;
	private Button deleteQDoneButton;
	private DialogBox imageSelectionBox;
	private DialogBox saveQueryBox;
	private DialogBox deleteQueryBox;
	private ExportDataPresenter exportP = null;
	private SaveQueryPresenter saveQP = null;
	private DeleteQueryPresenter deleteQP = null;
	private Modality previousModality = FilterView.Modality.QUERY;
	private static FilterPresenter fp;
	
	private final int ASCII_A = 65;
	private ArrayList<ArrayList<String>> kmPids;
	private ArrayList<String> kmNames;
	private List<DefinitionData> currentDD;
	
	private boolean currentResults = true;
	
	public static FilterPresenter get() {
		return fp;
	}
	
	public boolean isTooltipsOn() {
		return isTooltipsOn;
	}
	
	public FilterPresenter(Presenter parent, final View ui)
	{
		super(parent);

		this.ui = ui;
		
		this.headerController = new HeaderMenuSelectionController(this);
		this.headerController.addHeaderMenuSelectionHandler(this);
		
		presenter = parent;
		synth = new SynthPresenter(FilterPresenter.this, new SynthView());
		
		kmPids = new ArrayList<ArrayList<String>>();
		kmNames = new ArrayList<String>();
		rpcService.getRegionsOfInterest(new AsyncCallback<LinkedHashMap<String,Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(LinkedHashMap<String, Integer> result) {
				regionsOfInterest = result;
			}
			
		});
		fp = this;
	}

	@Override
	protected void bindAll() {
		synth.setActive();
		ui.getAllFiltersDropController().addDropHandler(new DropEventHandler () {
			@Override
			public void OnPreviewDrop(PreviewDropEvent e) {
				if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
					CategoryPresenter.get().resetHighlight();
					//FilterPresenter.this.loadQueryFilter((SavedQueryLabel)e.GetContext().selectedWidgets.get(0));
					return;
				}
			}
		});
		ui.getKMFilterByDropController().addDropHandler(new DropEventHandler() {

			@Override
			public void OnPreviewDrop(PreviewDropEvent e) {
				//Window.alert("This is where the drops are handled.");
				if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
					return;
				}
				if(e.GetContext().selectedWidgets.get(0) instanceof FlowPanel) {
					//Window.alert("You just dropped a saved definition column");
					Iterator<Widget> arrayOfWidgets = ((FlowPanel)e.GetContext().selectedWidgets.get(0)).iterator();
					while (arrayOfWidgets.hasNext()) {
					   Widget ch = arrayOfWidgets.next();
					   if (ch instanceof ColumnLabel) {
					       //Do something (in your case make an arraylist of your objects)
						   SavedDefinitionColumn tmp = (SavedDefinitionColumn)((ColumnLabel)ch).getColumn();
						   final DefinitionData load = tmp.getDefinitionData();
						   
						   MainPresenter.get().working(true);
						   rpcService.updateTable(load.getQuery(), new PresenterCallback<List<TableRow>>(FilterPresenter.this) {
							   @Override
							   protected void success(List<TableRow> result) {
								   //Window.alert("back from rpc rerunning that saved definition");
								   load.setTableData(result);
								   MainPresenter.get().finished();
							   }
						   });
						   
						   DefinitionPanel dp = new DefinitionPanel(presenter, headerController, load.getDefName(), load.getDefinition(), load);
						   //dp.setData(load);
						   
						   for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
							   Widget w = ui.getDefinitionsFT().getWidget(i, 0);
							   if(w instanceof DefinitionPanel) {
								   if(((DefinitionPanel)w).getName().equals(load.getDefName())) {
									   ui.getDefinitionsFT().setWidget(i, 0, dp);
									   return;
								   }
							   }
						   }
						   
						   ui.getDefinitionsFT().setWidget(ui.getDefinitionsFT().getRowCount(),0,dp);
						   ui.getDefName().setText( (char) (ASCII_A + ui.getDefinitionsFT().getRowCount()) + "");
						   
						   return;
					   }
					}
				}
				/*if(((ColumnLabel)e.GetContext().selectedWidgets.get(0)).getColumn() instanceof SavedDefinitionColumn) {
					//Window.alert("You just dropped a saved definition column");
					
					SavedDefinitionColumn tmp = (SavedDefinitionColumn)((ColumnLabel)e.GetContext().selectedWidgets.get(0)).getColumn();
					final DefinitionData load = tmp.getDefinitionData();
					
					MainPresenter.get().working(true);
					rpcService.updateTable(load.getQuery(), new PresenterCallback<List<TableRow>>(FilterPresenter.this) {
						
						@Override
						protected void success(List<TableRow> result) {
							//Window.alert("back from rpc rerunning that saved definition");
							load.setTableData(result);
							MainPresenter.get().finished();
						}
					});

					DefinitionPanel dp = new DefinitionPanel(presenter, headerController, load.getDefName(), load.getDefinition(), load);
					//dp.setData(load);
					
					for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
						Widget w = ui.getDefinitionsFT().getWidget(i, 0);
						if(w instanceof DefinitionPanel) {
							if(((DefinitionPanel)w).getName().equals(load.getDefName())) {
								ui.getDefinitionsFT().setWidget(i, 0, dp);
								return;
							}
						}
					}
					
					ui.getDefinitionsFT().setWidget(ui.getDefinitionsFT().getRowCount(),0,dp);
					ui.getDefName().setText( (char) (ASCII_A + ui.getDefinitionsFT().getRowCount()) + "");
					
					return;
				}*/
				
				ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
				FilterPresenter.this.addKMFilterPanel(l.getColumn().getFilterPanel());
				TablePresenter.get().setEdited();
			}
			
		});
		bindEvent(ClearEvent.TYPE, new ClearEventHandler() {
			
			@Override
			public void onClearEvent(ClearEvent e) {
				ui.clearAll();
				TablePresenter.get().setCurrent();
			}
		});
		bindEvent(UpDownEvent.TYPE, new UpDownHandler() {
			
			@Override
			public void OnUpDownEvent(UpDownEvent e) {
				if (FilterPresenter.this.ui.isKMMode()) {
					FilterPanel panel = e.getTheThingItCameFrom();
					int i = getKMWidgetRow(panel);
					if (i == -1)
						return;
					if (e.up()) {
						if (getKMWidgetRow(panel) != 0) {
							Widget w = ui.getDefineFT().getWidget(i - 2, 0);
							ui.getDefineFT().setWidget(i - 2, 0, panel);
							ui.getDefineFT().setWidget(i, 0, w);
						}
					} else {
						if (ui.getDefineFT().getRowCount() > i + 2) {
							Widget w = ui.getDefineFT().getWidget(i + 2, 0);
							ui.getDefineFT().setWidget(i + 2, 0, panel);
							ui.getDefineFT().setWidget(i, 0, w);
						}

					}
				} else if (FilterPresenter.this.ui.isQueryMode()) {
					FilterPanel panel = e.getTheThingItCameFrom();
					int i = getWidgetRow(panel);
					if (i == -1)
						return;
					if (e.up()) {
						if (getWidgetRow(panel) != 0) {
							Widget w = ui.getMainPanel().getWidget(i - 2, 0);
							ui.getMainPanel().setWidget(i - 2, 0, panel);
							ui.getMainPanel().setWidget(i, 0, w);
						}
					} else {
						if (ui.getMainPanel().getRowCount() > i + 2) {
							Widget w = ui.getMainPanel().getWidget(i + 2, 0);
							ui.getMainPanel().setWidget(i + 2, 0, panel);
							ui.getMainPanel().setWidget(i, 0, w);
						}

					}
				} else if (FilterPresenter.this.ui.isAggregateMode()) {
					FilterPanel panel = e.getTheThingItCameFrom();
					int i = getFilterByWidgetRow(panel);
					if (i == -1)
						return;
					if (e.up()) {
						if (getFilterByWidgetRow(panel) != 0) {
							Widget w = ui.getAggregateFilterPanel().getWidget(i - 2, 0);
							ui.getAggregateFilterPanel().setWidget(i - 2, 0, panel);
							ui.getAggregateFilterPanel().setWidget(i, 0, w);
						}
					} else {
						if (ui.getAggregateFilterPanel().getRowCount() > i + 2) {
							Widget w = ui.getAggregateFilterPanel().getWidget(i + 2, 0);
							ui.getAggregateFilterPanel().setWidget(i + 2, 0, panel);
							ui.getAggregateFilterPanel().setWidget(i, 0, w);
						}

					}
				}
				TablePresenter.get().setEdited();
				
			}
		});
		ui.getSynthButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				synth.show();
			}
		});
		bindEvent(AddFilterEvent.TYPE, new AddFilterHandler() {
			
			@Override
			public void OnAddFilterEvent(AddFilterEvent e) {
				addFilterPanel(e.getColumn().getFilterPanel());
				TablePresenter.get().setEdited();
			}
		});
		
		ui.getClearButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fireEvent(new ClearEvent());
			}
		});
		ui.getLogoutButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rpcService.logout(new PresenterCallback<Void>(FilterPresenter.this) {

					@Override
					protected void success(Void result) {
						logout();
					}
					
				});
			}
		});
		ui.getChangePasswordButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PasswordChangeView pcv = new PasswordChangeView(parent.getUsername());
				PasswordChangePresenter pcp = new PasswordChangePresenter(FilterPresenter.this, pcv);
				pcp.setActive();
			}
		});
		ui.getImageQueryButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//addFilterPanel(new ImageColumn().getFilterPanel());
				TablePresenter.get().setEdited();
			}
		});
		ui.getModality().addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 1) { /* expert mode */
					// Builds Query and Gets results from DB.
					FilterPresenter.this.ui.setExpertMode();
					if (previousModality.equals(FilterView.Modality.QUERY)) {
						NestedFilter f = MainPresenter.get()
								.getFilterPresenter().getFilter();
						if (f == null)
							return;
						Set<Column> fcols = f.getColumns();
						Query query = new Query();
						List<Column> cols = MainPresenter.get()
								.getTablePresenter().getColumns();
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
						query.setOrderBy(MainPresenter.get()
								.getTablePresenter().getSortColumn());
						query.setOrder(Query.Order.valueOf(Order.class,
								MainPresenter.get().getTablePresenter()
										.getSortOrder()));
						FilterPresenter.this.ui.getExpertInput().setText(
								query.toSQLString());
					} else if(previousModality.equals(FilterView.Modality.AGGREGATE)) {
						TablePresenter.get().clearSentinalColumns();
						NestedFilter af = MainPresenter.get().getFilterPresenter().getAggregateFilter();
						if(af == null)
							return;
						Query query = new Query();
						query.setModality(FilterView.Modality.AGGREGATE);
						query.setAggregateColumns((ArrayList<Column>) MainPresenter.get().getTablePresenter().getColumns());
						query.setGroupByColumns((ArrayList<Column>) MainPresenter.get().getFilterPresenter().getGroupByColumns());
						query.setAggregateFunctions((ArrayList<AggregateFunction>) MainPresenter.get().getFilterPresenter().getAggregateFunctions());
						query.setAggregateFilter(af);
						query.setOrderBy(MainPresenter.get().getTablePresenter().getSortColumn());
						query.setOrder(Query.Order.valueOf(Order.class, MainPresenter.get().getTablePresenter().getSortOrder()));
						FilterPresenter.this.ui.getExpertInput().setText(
								query.toSQLString());
					}
					TablePresenter.get().setExpertMode();
					previousModality = FilterView.Modality.EXPERT;
				} else if (event.getSelectedItem() == -1){ /*query mode */
					FilterPresenter.this.ui.setQueryMode();
					TablePresenter.get().setQueryMode();
					previousModality = FilterView.Modality.QUERY;
				} else if (event.getSelectedItem() == -1){ /* aggregate mode */
					FilterPresenter.this.ui.setAggregateMode();
					TablePresenter.get().setAggregateMode();
					previousModality = FilterView.Modality.AGGREGATE;
				} else if (event.getSelectedItem() == 0) { /* km mode */
					FilterPresenter.this.ui.setKMMode();
					TablePresenter.get().setQueryMode();
					previousModality = FilterView.Modality.KAPLAN;
				}
					
			}
		});
		ui.getExportButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//Window.alert("Export Button Clicked " + parent.getUsername() + " " + parent.getAccess());
				String logStatement = new String();
				if(parent.getAccess().equals("limited")){
					//Window.alert("Limited Access");
					PermissionView pv = new PermissionView(parent.getUsername());
					PermissionPresenter pp = new PermissionPresenter(FilterPresenter.this, pv);
					pp.setActive();
					
					logStatement = "\n\ttimestamp " + parent.getAccess() + " user " + parent.getUsername() + " attempted to export the following columns: ";
				} else if (parent.getAccess().equals("full")){
					//Window.alert("Full Access");
					FilterPresenter.this.exportP = new ExportDataPresenter(presenter, new ExportDataView());
					FilterPresenter.this.exportP.setActive();
					imageSelectionBox = buildExportDialogBox(FilterPresenter.this.exportP.getDisplay());
					imageSelectionBox.center();
					
					logStatement = "\n\ttimestamp " + parent.getAccess() + " user " + parent.getUsername() + " exported the following columns: ";
				}
				
				List<Column> tableCols = TablePresenter.get().getColumns();
				
				for (int i=0; i<tableCols.size(); i++){
					logStatement += tableCols.get(i).getSQLName();
					if (i<tableCols.size()-1)
						logStatement += ", ";
					else
						logStatement += ".";
				}
				writeToLog(logStatement);
				
				/*if(imageSelectionBox != null) {
					return;
				}
				if(exportP == null) {
					FilterPresenter.this.exportP = new ExportDataPresenter(presenter, new ExportDataView());
				}
				FilterPresenter.this.exportP.setActive();
				imageSelectionBox = buildExportDialogBox(FilterPresenter.this.exportP.getDisplay());
				imageSelectionBox.center();*/
			}
		});
//		ui.getSaveQueryButton().addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				if(saveQueryBox != null) {
//					return;
//				}
//				if(saveQP == null) {
//					FilterPresenter.this.saveQP = new SaveQueryPresenter(presenter, new SaveQueryView());
//				}
//				FilterPresenter.this.saveQP.setActive();
//				saveQueryBox = buildSaveQDialogBox(FilterPresenter.this.saveQP.getDisplay());
//				saveQueryBox.center();
//			}
//		});
		ui.getDeleteQueryButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(deleteQueryBox != null) {
					return;
				}
				if(deleteQP == null) {
					FilterPresenter.this.deleteQP = new DeleteQueryPresenter(presenter, new DeleteQueryView());
				}
				FilterPresenter.this.deleteQP.setActive();
				deleteQueryBox = buildDeleteQDialogBox(FilterPresenter.this.deleteQP.getDisplay());
				deleteQueryBox.center();
			}
		});
		ui.getSummarizeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TextBox summarize = ui.getSummarizeTextBox();
				String val = summarize.getValue();
				
				// Input validation. Only positive integers allowed
				if (val == null) {
					return;
				}
				int length = val.length();
				if (length == 0) {
					return;
				}
				int i = 0;
				for (; i < length; i++) {
					char c = val.charAt(i);
					if (c <= '/' || c >= ':') {
						return;
					}
				}
				//System.out.println(val);
				TablePresenter.get().constructPatientSummary(val);
			}
			
		});
		ui.getDashboardButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				Set<String> pids = new HashSet<String>();
				String defNames = "  ";
				
				/* build table */
				int cnt = 0;
				for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
					Widget w = ui.getDefinitionsFT().getWidget(i, 0);
					if(w instanceof DefinitionPanel) {
						if(((DefinitionPanel)w).isChecked()) {
							defNames = defNames + (((DefinitionPanel)w).getData().getDefName()) + ", ";
							for(TableRow tr : ((DefinitionPanel)w).getData().getTableData()) {
								pids.add(tr.getRowData("pid").replaceAll("\\*", ""));
							}
							cnt++;
						}
					} else {
						System.err.println("INSERTION OF NON DefinitionPanel in DefinitionsFT");
					}
				}
				
				defNames = defNames.substring(0,defNames.lastIndexOf(","));
				TablePresenter.get().constructDashboard(defNames,pids);
				
				
				
				
				
				
				//TablePresenter.get().constructDashboard();
			}
		});
		ui.getIntervalQueryButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				// Create drop handlers right after creating the new Interval Filter Panel.
				// The new Interval Filter Panel needs to have access to dragController in order to register its drop controllers.

				final IntervalTableColumn itc = new IntervalTableColumn();
				final IntervalFilterPanel ifp = itc.getFilterPanel(FilterPresenter.this.ui.getDragController());
				
				//itc.setTimeInterval(ifp.getTimeInterval());
				
				FilterPresenter.this.addKMFilterPanel(ifp);
				TablePresenter.get().setEdited();
				
				ifp.getDropController1().addDropHandler(new DropEventHandler() {

					@Override
					public void OnPreviewDrop(PreviewDropEvent e) {

						if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
							return;
						}
						ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
						
						if(l.getColumn() instanceof DateTableColumn){
						
							itc.setStartColumn(l.getColumn());
							ifp.updateStartDate();
						
							//FilterPresenter.this.addKMFilterPanel(l.getColumn().getFilterPanel());
							//TablePresenter.get().setEdited();
						
						}
					}
					
				});
				
				ifp.getDropController2().addDropHandler(new DropEventHandler() {

					@Override
					public void OnPreviewDrop(PreviewDropEvent e) {

						if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
							return;
						}
						ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
						
						if(l.getColumn() instanceof DateTableColumn){
							
							itc.setEndColumn(l.getColumn());
							ifp.updateEndDate();
						
							//FilterPresenter.this.addKMFilterPanel(l.getColumn().getFilterPanel());
							//TablePresenter.get().setEdited();
						}
					}
					
				});

			}
		});
		ui.getPsaAggregateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Obtain all unique PIDs from current table
				// Obtain definition names and currently selected PID if applicable
				final ArrayList<String> pids = new ArrayList<String>();
				String curr_pid;
				String defNames = "  ";
				
				for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
					Widget w = ui.getDefinitionsFT().getWidget(i, 0);
					if(w instanceof DefinitionPanel) {
						if(((DefinitionPanel)w).isChecked()) {
							defNames = defNames + (((DefinitionPanel)w).getData().getDefName()) + ", ";
							ArrayList<String> currPids = new ArrayList<String>();
							for(TableRow tr : ((DefinitionPanel)w).getData().getTableData()) {
								curr_pid = tr.getRowData("pid").replaceAll("\\*", "");
								if(!pids.contains(curr_pid)){
									pids.add(curr_pid);
									//System.out.println("PID: " + curr_pid);
								}
							}
						}
					} else {
						System.err.println("INSERTION OF NON DefinitionPanel in DefinitionsFT");
					}
				}
				
				final String sel_pid = TablePresenter.get().getSelectedPID();
				defNames = defNames.substring(0,defNames.lastIndexOf(","));
				
				//System.out.println("Selected PID: " + sel_pid);
				final Tab currentTab = TablePresenter.get().addPsaAggregateTab(defNames,pids,sel_pid);
				
				rpcService.getPsaAggregateValues(pids, null, new PresenterCallback<ArrayList<PSA_Graph>>(FilterPresenter.this) {

					@Override
					protected void success(ArrayList<PSA_Graph> result) {
						
						/*for(int i = 0; i<pids.size(); i++){
							PSA_Graph graph = result.get(i);
							System.out.println("PID: " + pids.get(i));
							List<String> dates = graph.getDates();
							List<String> vals = graph.getVals();
							
							for(int j = 0; j<dates.size(); j++){
								System.out.println("       date/val: " + dates.get(j) + "  " + vals.get(j));
							}
						}*/
						
						/*ArrayList<String> implantDates = new ArrayList<String>();
						
						System.out.print("String[][] dateList = {");
						for(int i = 0; i<pids.size(); i++){
							PSA_Graph graph = result.get(i);
							
							List<String> dates = graph.getDates();
							implantDates.add(graph.getImplantDate());
							
							System.out.print("{");
							for(int j = 0; j<dates.size(); j++){
								System.out.print("\"" + dates.get(j) + "\"");
								if (j!=dates.size()-1)
									System.out.print(",");
							}
							System.out.print("}");
							if (i!=pids.size()-1)
								System.out.print(",");
							System.out.println();
						}
						System.out.print("};");
						System.out.println();
						
						System.out.print("String[][] psaList = {");
						for(int i = 0; i<pids.size(); i++){
							PSA_Graph graph = result.get(i);
							
							List<String> vals = graph.getVals();
							
							System.out.print("{");
							for(int j = 0; j<vals.size(); j++){
								System.out.print("\"" + vals.get(j) + "\"");
								if (j!=vals.size()-1)
									System.out.print(",");
							}
							System.out.print("}");
							if (i!=pids.size()-1)
								System.out.print(",");
							System.out.println();
						}
						System.out.print("};");
						System.out.println();
						
						System.out.print("String[] pids = {");
						for(int i = 0; i<pids.size(); i++){
								System.out.print("\"" + pids.get(i) + "\"");
								if (i!=pids.size()-1)
									System.out.print(",");
						}
						System.out.print("};");
						System.out.println();
						
						System.out.print("String[] implantDates = {");
						for(int i = 0; i<pids.size(); i++){
								System.out.print("\"" + implantDates.get(i) + "\"");
								if (i!=pids.size()-1)
									System.out.print(",");
						}
						System.out.print("};");
						System.out.println();
						*/

						JsArrayString jsPIDs = (JsArrayString)JavaScriptObject.createArray().cast();
						JsArrayString jsImplantDates = (JsArrayString)JavaScriptObject.createArray().cast();
						JsArray<JsArrayString> jsDateLists = (JsArray)JavaScriptObject.createArray().cast();
						JsArray<JsArrayString> jsPsaLists = (JsArray)JavaScriptObject.createArray().cast();
						JsArrayString jsSelPID = (JsArrayString)JavaScriptObject.createArray().cast();
						jsSelPID.push(sel_pid);
						JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
						jsSize.push(750); //Width
						jsSize.push(550); //Height
						
						for(int i = 0; i<pids.size(); i++){
							PSA_Graph graph = result.get(i);
							
							List<String> dates = graph.getDates();
							List<String> psa_vals = graph.getVals();
							
							JsArrayString jsDates = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jsPSA_Vals = (JsArrayString)JavaScriptObject.createArray().cast();
								
							for (int j = 0; j<dates.size(); j++) {
								jsDates.push(dates.get(j));
								jsPSA_Vals.push(psa_vals.get(j));
							}
							
							jsDateLists.push(jsDates);
							jsPsaLists.push(jsPSA_Vals);
							jsPIDs.push(pids.get(i));
							jsImplantDates.push(graph.getImplantDate());
							
						}
						
						((PsaAggregateTab)currentTab).populateStudiesAndShow(jsPIDs, jsPsaLists, jsDateLists, jsImplantDates, jsSelPID, jsSize);
						
					}
				});
				
			}
		});
		ui.getKaplanMeierButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ArrayList<String> kmNames = new ArrayList<String>();
				ArrayList<ArrayList<String>> kmPids = new ArrayList<ArrayList<String>>();
				String defNames = "  ";
				final JsArrayString jsLineNames = (JsArrayString)JavaScriptObject.createArray().cast();
				
				/* build table */
				int cnt = 0;
				for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
					Widget w = ui.getDefinitionsFT().getWidget(i, 0);
					if(w instanceof DefinitionPanel) {
						if(((DefinitionPanel)w).isChecked()) {
							kmNames.add(((DefinitionPanel)w).getData().getDefName());
							jsLineNames.push((((DefinitionPanel)w).getData().getDefName()));
							defNames = defNames + (((DefinitionPanel)w).getData().getDefName()) + ", ";
							ArrayList<String> currPids = new ArrayList<String>();
							for(TableRow tr : ((DefinitionPanel)w).getData().getTableData()) {
								currPids.add(tr.getRowData("pid").replaceAll("\\*", ""));
							}
							kmPids.add(currPids);
							cnt++;
						}
					} else {
						System.err.println("INSERTION OF NON DefinitionPanel in DefinitionsFT");
					}
				}
				
				defNames = defNames.substring(0,defNames.lastIndexOf(","));
				final Tab currentTab = TablePresenter.get().addKMTab(defNames,kmPids,kmNames);

				rpcService.generateKMChart(kmPids, kmNames, null,null,null, new PresenterCallback<ArrayList<KM_Graph>>(FilterPresenter.this) {
					@Override
					protected void success(ArrayList<KM_Graph> result) {
						
						JsArray<JsArrayString> jsLineXLists = (JsArray)JavaScriptObject.createArray().cast();
						JsArray<JsArrayString> jsLineYLists = (JsArray)JavaScriptObject.createArray().cast();
						JsArray<JsArrayString> jsCensXLists = (JsArray)JavaScriptObject.createArray().cast();
						JsArray<JsArrayString> jsCensYLists = (JsArray)JavaScriptObject.createArray().cast();
						JsArray<JsArrayString> jsAtRiskList = (JsArray)JavaScriptObject.createArray().cast();
						JsArrayInteger jsN = (JsArrayInteger)JavaScriptObject.createArray().cast();
						JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
						jsSize.push(800); //Width 1300
						jsSize.push(600); //Height 500
						
						for(int i = 0; i<result.size(); i++){
							KM_Graph first = result.get(i);

							List<String> lineX = first.getlineX();
							List<String> lineY = first.getlineY();
							List<String> censX = first.getcensX();
							List<String> censY = first.getcensY();
							List<String> atRisk = first.getAtRisk();
						
							JsArrayString jslineX = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jslineY = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jscensX = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jscensY = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jsAtRisk = (JsArrayString)JavaScriptObject.createArray().cast();
						
							for (int j = 0; j<lineX.size(); j++){
								jslineX.push(lineX.get(j));
								jslineY.push(lineY.get(j));
							}
							for (int j = 0; j<censX.size(); j++){
								jscensX.push(censX.get(j));
								jscensY.push(censY.get(j));
							}
							for (int j = 0; j<atRisk.size(); j++){
								jsAtRisk.push(atRisk.get(j));
							}
							
							jsLineXLists.push(jslineX);
							jsLineYLists.push(jslineY);
							jsCensXLists.push(jscensX);
							jsCensYLists.push(jscensY);
							jsAtRiskList.push(jsAtRisk);
							jsN.push(first.getN());
						
						}

						((KaplanMeierTab)currentTab).populateStudiesAndShow(jsLineXLists, jsLineYLists, jsCensXLists, jsCensYLists, jsAtRiskList, jsSize, jsLineNames, jsN);
					}
				});
				
				/*rpcService.getTempFilename(new PresenterCallback<String>(FilterPresenter.this) {
					@Override
					protected void success(String result) {
						((KaplanMeierTab)currentTab).setURL(result);
					}
				});*/
				
				/*rpcService.generateKMChart(kmPids, kmNames, new PresenterCallback<String>(FilterPresenter.this) {
					@Override
					protected void success(String result) {
						System.err.println("SUCCESS : " + result);
						//TablePresenter.get().addKMChart(result, "Kaplan - Meier");
						//TablePresenter.get().getTab().setURL(result);
						((KaplanMeierTab)currentTab).setURL(result);
					}
				});*/
				
				//TablePresenter.get().setVisibleStatisticsPanel(true);
				//TablePresenter.get().setBinSizeVisible(false);
				//TablePresenter.get().getStatisticVP().clear();
				//TablePresenter.get().setStatisticsLabel("Logrank Statistic");
				for(int i = 0; i < kmNames.size(); i++) {
					for(int j = i + 1; j < kmNames.size(); j++) {
						if(i != j) {
							final String s = kmNames.get(i) + " vs. " + kmNames.get(j);
							ArrayList<ArrayList<String>> currPairPids = new ArrayList<ArrayList<String>>();
							currPairPids.add(kmPids.get(i));
							currPairPids.add(kmPids.get(j));
							rpcService.getChiSqPVal(currPairPids,null,null,null, new PresenterCallback<String>(FilterPresenter.this) {

								@Override
								protected void success(String result) {
									//TablePresenter.get().getStatisticVP().add(new Label(s + " : " + result));
									//((KaplanMeierTab)TablePresenter.get().getTab()).getPStats().add(new Label(s + " : " + result));
									((KaplanMeierTab)currentTab).getPStats().add(new Label(s + " : " + result));
								}
							});
						}
					}
				}

			}
			
		});
		ui.getAddToKaplanMeier().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final KaplanMeierNamePresenter kp = new KaplanMeierNamePresenter(FilterPresenter.this, new KaplanMeierNameView());
				kp.setActive();
				if (!FilterPresenter.get().isResultsCurrent()) {
					TablePresenter.get().setCurrent();
					fireEvent(new QueryChangedEvent());
				}
			}
		});
		ui.getSynonymButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SynonymPresenter sp = new SynonymPresenter(FilterPresenter.this, new SynonymView());
				sp.setActive();
			}
		});
		ui.addDefinitionButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				/* check overwrite */
				
				String defName = ui.getDefName().getText();
				
				for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
					Widget w = ui.getDefinitionsFT().getWidget(i, 0);
					if(w instanceof DefinitionPanel) {
						if(((DefinitionPanel)w).getName().equals(defName)) {
							OverwriteView ov = new OverwriteView(defName);
							OverwritePresenter op = new OverwritePresenter(FilterPresenter.this, ov);
							op.setActive();
							return;
						}
					}
				}
				
				//fire away!
				addDefinition();
//				ui.getApplyButton().click();
			}
		});
		ui.getKMChart().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final KaplanMeierPresenter kp = new KaplanMeierPresenter(FilterPresenter.this, new KaplanMeierView());
				kp.setActive();
				addKMData(kp);
			}
		});
		ui.getApplyButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//Window.alert("apply button clicked");
				
				//List<TableRow> data = new ArrayList<TableRow>();
				//List<Column> cols = new ArrayList<Column>();
				currentDD = new ArrayList<DefinitionData>();
				for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
					Widget w = ui.getDefinitionsFT().getWidget(i, 0);
					if(w instanceof DefinitionPanel) {
						if(((DefinitionPanel)w).isChecked()) {
							DefinitionData defData = ((DefinitionPanel) w).getData();
							//currentDD.add(defData);
							if (defData != null) {
								currentDD.add(defData);
								/*List<TableRow> tableData = defData.getTableData();
								List<Column> tableCols = defData.getCols();
								if (tableData != null){
									for(Column col : tableCols){
										//Window.alert(col.getName());
										if(!cols.contains(col)){
											//Window.alert("is not contained");
											cols.add(col);
										}
									}
									data.addAll(tableData);
								} else
									System.err.println("tableData is null");*/
							} else
								System.err.println("defData is null");
						}
					} else {
						System.err.println("INSERTION OF NON DefinitionPanel in DefinitionsFT");
					}
				}

				MainPresenter.get().working(true);
				rpcService.replicate_definition(currentDD, new PresenterCallback<List<DefinitionData>>(FilterPresenter.this) {
					@Override
					protected void success(List<DefinitionData> result) {
						currentDD = result;
						//Window.alert("ABout to Rebuild");
						//for(DefinitionData d: currentDD){
						//	Window.alert(d.getQuery().toSQLString());
						//}
						TablePresenter.get().clearColumns();
						updateTableColumns();
						MainPresenter.get().finished();
					}
				});
				
				/*Window.alert("ABout to Rebuild");
				for(DefinitionData d: currentDD){
					Window.alert(d.getQuery().toSQLString());
				}
				TablePresenter.get().rebuildModelData(data,cols);
				TablePresenter.get().clearFunctionResults();*/
			}
			
		});
		ui.getClearDefButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.getDefineFT().removeAllRows();
			}
		});
//		ui.getSaveQuery().addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				if (saveQueryBox != null) {
//					return;
//				}
//				if (saveQP == null) {
//					FilterPresenter.this.saveQP = new SaveQueryPresenter(
//							presenter, new SaveQueryView());
//				}
//				FilterPresenter.this.saveQP.setActive();
//				saveQueryBox = buildSaveQDialogBox(FilterPresenter.this.saveQP
//						.getDisplay());
//				saveQueryBox.center();
//			}
//		});
	}
	
	@Override
	public void OnHeaderMenuSelectionEvent(HeaderMenuSelectionEvent e) {
		if (e.getSelectItem().equals("save_def")) {
			
			//Will need to save this DefinitionData into a file corresponding to this particular user created on the server when this button is clicked.
			//Each definition will be a file with the same name in that users folder.
			//Each definition in that folder will need to be loaded at startup for a given user and populate the Saved Queries Category.
			//When a 'saved query' is dropped onto the filter panel, the server will need to load this definitiondata and rerun the query.
			
			final DefinitionData dd = e.getDefinitionData();
			SavedDefinitionColumn tmp = new SavedDefinitionColumn(dd);
			CategoryPresenter.get().addColumn(tmp);

			rpcService.saveDefinition(dd, new PresenterCallback<SavedDefinitionColumn>(this){
				@Override
				protected void success(SavedDefinitionColumn c) {
					//Useless right now
				}
			});
			
			/*final MagicTextColumn mag = e.getSourceColumn();
			final Column col = e.getSourceColumn().getColumn();
			if(col instanceof SynthTableColumn){
				rpcService.dropSynthTable((SynthTableColumn) col,false, new PresenterCallback<Void>(this) {

					@Override
					protected void success(Void result) {
						TablePresenter.this.table.removeColumn(mag);
					}
				
				});
			}else{
				if( !(e.getSourceColumn().getColumn().equals(this.pid)
						|| e.getSourceColumn().getColumn().equals(this.definition)) ) {
					this.table.removeColumn(e.getSourceColumn());
					FilterPresenter.get().removeColumnFromCurrentDefinitions(e.getSourceColumn().getColumn());
				}
			}*/
		}
	}
	
	public List<DefinitionData> getCurrentDefinitons(){
		return currentDD;
	}
	
	public void addColumnToCurrentDefinitions(Column col){
		
		MainPresenter.get().working(true);
		rpcService.updateCurrentDefinitions(currentDD, col, new PresenterCallback<List<DefinitionData>>(FilterPresenter.this) {
			@Override
			protected void success(List<DefinitionData> result) {
				currentDD = result;
				updateTableColumns();
				MainPresenter.get().finished();
			}
		});
		
	}
	
	public void removeColumnFromCurrentDefinitions(Column col){
		//Window.alert("Removing column: " + col.getName());
		
		for(int i = 0; i < currentDD.size(); i++) {
			currentDD.get(i).getQuery().removeColumn(col);
			currentDD.get(i).removeCol(col);
		}
	}

	public void updateTableColumns(){
		List<TableRow> data = new ArrayList<TableRow>();
		List<Column> cols = new ArrayList<Column>();
		for(int i = 0; i < currentDD.size(); i++) {
			DefinitionData defData = currentDD.get(i);
			if (defData != null) {
				List<TableRow> tableData = defData.getTableData();
				List<Column> tableCols = defData.getCols();
				if (tableData != null) {
					for(Column c : tableCols){
						//Window.alert("DropColumn Column: " + c.getName());
						if(!cols.contains(c))
							cols.add(c);
					}
					data.addAll(tableData);
				}
				else
					System.err.println("tableData is null");
			}
			else
				System.err.println("defData is null");
		}
		TablePresenter.get().rebuildModelData(data,cols);
		TablePresenter.get().clearFunctionResults();
	}
	
	public void addColumnToDefinitions(Column col) {
		for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
			Widget w = ui.getDefinitionsFT().getWidget(i, 0);
			if(w instanceof DefinitionPanel) {
				//List<RebuildDataFilterPanel> rb = ((DefinitionPanel)w).getData().getRebuildFilters();
				System.out.println("Definition Panel name: " + ((DefinitionPanel)w).getName() + " " + ui.getDefinitionsFT().getRowCount());
				
				this.setFilters(((DefinitionPanel)w).getData().getRebuildFilters(), ((DefinitionPanel)w).getName());
				TablePresenter.get().addColumn(col);
				if(((DefinitionPanel)w).isChecked()) {
					addDefinition(true);
				} else {
					addDefinition();
				}
			} else {
				System.err.println("INSERTION OF NON DefinitionPanel in DefinitionsFT");
			}
		}
	}
	public void addDefinition() {
//		addDefinition(false);
		addDefinition(true,true);
	}
	public void addDefinition(boolean isChecked) {
		String defName = ui.getDefName().getText();
		String definition = "";
		
		/* ensure that the query is valid */
		//fireEvent(new QueryChangedEvent());
		//TablePresenter.get().setCurrent();
		/* generate sql string */
		if(MainPresenter.get().getAbortingQuery())
			return;
		NestedFilter f = this.getFilter();
		if (f == null)
			return;
		Set<Column> fcols = f.getColumns();
		Query query = new Query();
		List<Column> cols = MainPresenter.get()
				.getTablePresenter().getColumns();
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
		query.setOrderBy(MainPresenter.get()
				.getTablePresenter().getSortColumn());
		query.setOrder(Query.Order.valueOf(Order.class,
				MainPresenter.get().getTablePresenter()
						.getSortOrder()));
		/* generate definition */
		
		for(int i = 0; i < ui.getDefineFT().getRowCount(); i++) {
			Widget w = ui.getDefineFT().getWidget(i, 0);
			if(w instanceof FilterPanel) {
				definition += ((FilterPanel)w).getDefinitionString();
			} else if (w instanceof OperatorPanel) {
				definition += ((OperatorPanel)w).getDefinitionString();
			}
		}
		
		final DefinitionData dd = new DefinitionData(defName, query.toSQLString(), new ArrayList<Column>(temp));
		MainPresenter.get().working(true);
		rpcService.updateTable(query, new PresenterCallback<List<TableRow>>(FilterPresenter.this) {

			@Override
			protected void success(List<TableRow> result) {
				dd.setTableData(result);
				MainPresenter.get().finished();
			}
		});
		dd.setQuery(query);
		/*
		ArrayList<Widget> widgets = new ArrayList<Widget>();
		for(int i = 0; i < ui.getDefineFT().getRowCount(); i++) {
			Widget w = ui.getDefineFT().getWidget(i, 0);
			widgets.add(w);
		}
		
		dd.setFilters(widgets);*/
		dd.setRebuildFilters(FilterPresenter.this.getQueryFilterRebuildData());
		dd.setDefinition(definition);
		
		DefinitionPanel dp = new DefinitionPanel(presenter, headerController, defName, definition, dd);
		//dp.setData(dd);
		if(isChecked)
			dp.setChecked();
		
		for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
			Widget w = ui.getDefinitionsFT().getWidget(i, 0);
			if(w instanceof DefinitionPanel) {
				if(((DefinitionPanel)w).getName().equals(defName)) {
					ui.getDefinitionsFT().setWidget(i, 0, dp);
					return;
				}
			}
		}
		
		ui.getDefinitionsFT().setWidget(ui.getDefinitionsFT().getRowCount(),0,dp);
		ui.getDefName().setText( (char) (ASCII_A + ui.getDefinitionsFT().getRowCount()) + "");
	}

	public void addDefinition(boolean isChecked, boolean uncheckRest) {

		String defName = ui.getDefName().getText();
		String definition = "";
		
		/* ensure that the query is valid */
		//fireEvent(new QueryChangedEvent());
		//TablePresenter.get().setCurrent();
		/* generate sql string */
		if(MainPresenter.get().getAbortingQuery())
			return;
		NestedFilter f = this.getFilter();
		if (f == null){
			return;
		}
		Set<Column> fcols = f.getColumns();
		
		//for(Column c : fcols){
		//	Window.alert(c.getName());
		//}
		
		Query query = new Query();
		List<Column> cols = MainPresenter.get().getTablePresenter().getColumns();
		List<Column> temp = new ArrayList<Column>();
		temp.addAll(cols);
		for (Column col : fcols) {
			//Window.alert("Adding the column " + col.getName());
			if (!cols.contains(col)) {
				temp.add(col);
			}
		}
		for (Column col : cols){
			if (!fcols.contains(col) && !(col.getName().equals("Patient ID") || col.getName().equals("Definition")))
				temp.remove(col);
		}
		
		// if query contains anything from roi_volume, include study date by default
		Column studyDateColumn = new DateTableColumn("Study Date", "study_date", "", new Table("btaw.scan_information"));
		if (!temp.contains(studyDateColumn)) {
			for (Column col : cols) {
				String s = col.getName();
				if (s.contains("Volume of")) {
					temp.add(studyDateColumn);
				}
//				System.err.println(col.getName());
			}
		}
		
		query.setModality(FilterView.Modality.QUERY);
		query.addColumns(temp);
		query.setFilter(f);
		query.setOrderBy(MainPresenter.get().getTablePresenter().getSortColumn());
		query.setOrder(Query.Order.valueOf(Order.class,MainPresenter.get().getTablePresenter().getSortOrder()));
		/* generate definition */
		
		for(int i = 0; i < ui.getDefineFT().getRowCount(); i++) {
			Widget w = ui.getDefineFT().getWidget(i, 0);
			if(w instanceof FilterPanel) {
				definition += ((FilterPanel)w).getDefinitionString();
			} else if (w instanceof OperatorPanel) {
				definition += ((OperatorPanel)w).getDefinitionString();
			}
		}
		
		final DefinitionData dd = new DefinitionData(defName, query.toSQLString(), new ArrayList<Column>(temp));
		MainPresenter.get().working(true);
		rpcService.updateTable(query, new PresenterCallback<List<TableRow>>(FilterPresenter.this) {
			
			@Override
			protected void success(List<TableRow> result) {

				//Window.alert("back from rpc");
				dd.setTableData(result);
				MainPresenter.get().finished();
				ui.getApplyButton().click();
			}
		});
		dd.setQuery(query);
		/*
		ArrayList<Widget> widgets = new ArrayList<Widget>();
		for(int i = 0; i < ui.getDefineFT().getRowCount(); i++) {
			Widget w = ui.getDefineFT().getWidget(i, 0);
			widgets.add(w);
		}
		
		dd.setFilters(widgets);*/
		dd.setRebuildFilters(FilterPresenter.this.getQueryFilterRebuildData());
		dd.setDefinition(definition);
		
		DefinitionPanel dp = new DefinitionPanel(presenter, headerController, defName, definition, dd);
		//dp.setData(dd);
		if (isChecked)
			dp.setChecked();
		
		for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
			Widget w = ui.getDefinitionsFT().getWidget(i, 0);
			if(w instanceof DefinitionPanel) {
				if(((DefinitionPanel)w).getName().equals(defName)) {
					ui.getDefinitionsFT().setWidget(i, 0, dp);
					return;
				}
				else if (uncheckRest)
					((DefinitionPanel)w).setUnchecked();
			}
		}
		
		ui.getDefinitionsFT().setWidget(ui.getDefinitionsFT().getRowCount(),0,dp);
		ui.getDefName().setText( (char) (ASCII_A + ui.getDefinitionsFT().getRowCount()) + "");
	}

	public void setFilters(List<RebuildDataFilterPanel> filters, String name) {
		ui.getDefineFT().removeAllRows();

		for(int i = 0; i<filters.size(); i++){
			RebuildDataFilterPanel rb = filters.get(i);
			if(rb.isOperator()) {
				OperatorPanel op = new OperatorPanel(rb.getUnique());
				op.setState(rb);
				FilterPresenter.this.rebuildOpPanel(op);
			} else if (rb.isInterval()) {
				IntervalFilterPanel ifp = new IntervalFilterPanel(new IntervalTableColumn(rb.getCol(),rb.getCol2()));
				ifp.setState(rb);
				FilterPresenter.this.rebuildFilterPanel(ifp);
			} else {
				FilterPanel fp = rb.getCol().getFilterPanel();
				fp.setState(rb);
				FilterPresenter.this.rebuildFilterPanel(fp);
			}
		}
		ui.getDefName().setText(name);
	}
	
	public void addFunction(FunctionData fd) {
		FunctionPanel fp = new FunctionPanel(this, fd.getFuncName(), fd);
		ui.getFunctionsFT().setWidget(ui.getFunctionsFT().getRowCount(), 0, fp);
	}
	
	public boolean isResultsCurrent() {
		return this.currentResults;
	}
	
	public void setResultsCurrent(boolean isCurrent) {
		//ui.addDefinitionButton().setEnabled(isCurrent);
		this.currentResults = isCurrent;
	}
	
	public void addCurrPids(String name) {
		if(name == null || name.equals("")) {
			name = "Group " + (char)(ASCII_A + kmNames.size());
		}
		if(kmNames.contains(name)) {
			name = name + " 1";
		}
		ui.getKMSubgroupPanel().setWidget(ui.getKMSubgroupPanel().getRowCount(), 0, new SubgroupFilterPanel(name));
		kmNames.add(name);
		ui.getAddToKaplanMeier().setText("Add To Kaplan-Meier("+ (kmPids.size() + 1) + ")");
		
		ArrayList<String> currPids = new ArrayList<String>();
		for(TableRow tr : TablePresenter.get().getTableAsList()) {
			currPids.add(tr.getRowData("pid").replaceAll("\\*", ""));
		}
		
		kmPids.add(currPids);
		/*rpcService.generateKMChart(kmPids, kmNames, new PresenterCallback<String>(FilterPresenter.this) {

			@Override
			protected void success(String result) {
				System.err.println("SUCCESS : " + result);
				ui.setKMUrl(result);
			}
		});*/
	}
	
	public void addFunctionSubgroup(String name) {
		ui.getKMSubgroupPanel().setWidget(ui.getKMSubgroupPanel().getRowCount(), 0, new SubgroupFilterPanel(name));
	}
	

	public void addToSubgroupResultsPanel(String name) {
		ui.getFunctionsFT().setWidget(ui.getFunctionsFT().getRowCount(), 0, new SubgroupFilterPanel(name));
	}
	
	public void removeGroup(String name) {
		for(int i = 0; i < ui.getFunctionsFT().getRowCount(); i++) {
			if( name.equals(((SubgroupFilterPanel)ui.getFunctionsFT().getWidget(i, 0)).getName()) ) {
				ui.getFunctionsFT().removeRow(i);
			}
		}
		int n = name.lastIndexOf("=");
		if(n>0) {
			name = name.substring(0, n-1);
			int m = name.lastIndexOf("=");
			name = name.substring(0, m-1);
		}
		for(int i = 0; i < ui.getKMSubgroupPanel().getRowCount(); i++) {
			if( name.equals(((SubgroupFilterPanel)ui.getKMSubgroupPanel().getWidget(i, 0)).getName()) ) {
				ui.getKMSubgroupPanel().removeRow(i);
			}
		}
	}
	
	public void removeDefinition(String defName) {
		for(int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
			Widget w = ui.getDefinitionsFT().getWidget(i, 0);
			if( defName.equals(((DefinitionPanel)w).getName()) ) {
				ui.getDefinitionsFT().removeRow(i);
			}
		}
	}
	
	public void removeFunction(String funcDiscriptor) {
		for(int i = 0; i < ui.getFunctionsFT().getRowCount(); i++) {
			Widget w = ui.getFunctionsFT().getWidget(i, 0);
			if( funcDiscriptor.equals(((FunctionPanel)w).getFuncDescriptor()) ) {
				ui.getFunctionsFT().removeRow(i);
			}
		}
	}
	
	public void addKMData(final KaplanMeierPresenter kp) {
		if(kmPids == null) {
			System.err.println("kmPids NULL in FilterPresenter");
			return;
		}
		/*rpcService.generateKMChart(kmPids, kmNames, new PresenterCallback<String>(FilterPresenter.this) {

			@Override
			protected void success(String result) {
				System.err.println("SUCCESS : " + result);
				kp.setKMUrl(result);
			}
			
		});*/
	}
	
	public void updateKMData(final KaplanMeierPresenter kp) {
		ui.getAddToKaplanMeier().setText("Add To Kaplan-Meier(" + kmPids.size() + ")");
		if(kmPids.size() == 0) {
			kmNames.add(new String("All Patients"));
			/*rpcService.generateKMChart(null, kmNames, new PresenterCallback<String>(FilterPresenter.this) {

				@Override
				protected void success(String result) {
					System.err.println("SUCCESS : " + result);
					kp.setKMUrl(result);
				}
			
			});*/
			kmNames.clear();
		} else {
			addKMData(kp);
		}
	}
	
	public ArrayList<ArrayList<String>> getKMPids() {
		return this.kmPids;
	}
	
	public void setKMPids(ArrayList<ArrayList<String>> kmPids) {
		this.kmPids = kmPids;
	}
	
	public ArrayList<String> getKMNames() {
		return this.kmNames;
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}
	
	private DialogBox buildExportDialogBox(IView view) {
	    final DialogBox dialogBox = new DialogBox();
	    dialogBox.getElement().getStyle().setZIndex(3);
	    dialogBox.setText("Export Data");
	    VerticalPanel container = new VerticalPanel();
	    
	    container.add(view.getRootWidget());
	    
	    HorizontalPanel buttonPanelWrapper = new HorizontalPanel();
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    exportDoneButton = new Button("Done");
	    exportDoneButton.addClickHandler(this);
	    
	    buttonPanel.add(exportDoneButton);
	    buttonPanel.setSpacing(5);
	    buttonPanelWrapper.setWidth("100%");
	    buttonPanelWrapper.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
	    buttonPanelWrapper.add(buttonPanel);
	    
	    container.add(buttonPanelWrapper);
	    dialogBox.add(container);
	    
		return dialogBox;
	}
	
	private DialogBox buildSaveQDialogBox(IView view) {
	    final DialogBox dialogBox = new DialogBox();
	    dialogBox.getElement().getStyle().setZIndex(3);
	    dialogBox.setText("Save Query");
	    VerticalPanel container = new VerticalPanel();
	    
	    container.add(view.getRootWidget());
	    
	    HorizontalPanel buttonPanelWrapper = new HorizontalPanel();
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    saveQDoneButton = new Button("Done");
	    saveQCancelButton = new Button("Cancel");
	    saveQCancelButton.addClickHandler(this);
	    saveQDoneButton.addClickHandler(this);
	    
	    buttonPanel.add(saveQDoneButton);
	    buttonPanel.add(saveQCancelButton);
	    buttonPanel.setSpacing(5);
	    buttonPanelWrapper.setWidth("100%");
	    buttonPanelWrapper.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
	    buttonPanelWrapper.add(buttonPanel);
	    
	    container.add(buttonPanelWrapper);
	    dialogBox.add(container);
	    
		return dialogBox;
	}
	
	private DialogBox buildDeleteQDialogBox(IView view) {
	    final DialogBox dialogBox = new DialogBox();
	    dialogBox.getElement().getStyle().setZIndex(3);
	    dialogBox.setText("Delete Query");
	    VerticalPanel container = new VerticalPanel();
	    
	    container.add(view.getRootWidget());
	    
	    HorizontalPanel buttonPanelWrapper = new HorizontalPanel();
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    deleteQDoneButton = new Button("Done");
	    deleteQCancelButton = new Button("Cancel");
	    deleteQCancelButton.addClickHandler(this);
	    deleteQDoneButton.addClickHandler(this);
	    
	    buttonPanel.add(deleteQDoneButton);
	    buttonPanel.add(deleteQCancelButton);
	    buttonPanel.setSpacing(5);
	    buttonPanelWrapper.setWidth("100%");
	    buttonPanelWrapper.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
	    buttonPanelWrapper.add(buttonPanel);
	    
	    container.add(buttonPanelWrapper);
	    dialogBox.add(container);
	    
		return dialogBox;
	}

	@Override
	protected void activated(HasWidgets container) {
		rpcService.getDatacutDate(new PresenterCallback<String>(this) {
			@Override
			protected void success(String result) {
				ui.setDatacutDate(result);
			}
		});
	}

	private void addFilterPanel(FilterPanel fp) {
		//Window.alert("Addding FILTER PANEL");
		
		if(ui.isAggregateMode()) {
			addAggregateFilterPanel(fp);
			return;
		}
		if(fp instanceof ImageFilterPanel) {
			((ImageFilterPanel)fp).getPanel().setRegionsOfInterest(regionsOfInterest);
		}
		fp.addFilterDeleteHandler(this);
		if(ui.getMainPanel().getRowCount()>0){
			ui.getMainPanel().setWidget(ui.getMainPanel().getRowCount(), 0, new OperatorPanel(unique++));
		}
		ui.getMainPanel().setWidget(ui.getMainPanel().getRowCount(), 0, fp);
	}
	
	private void addKMFilterPanel(FilterPanel fp) {
		if(fp instanceof ImageFilterPanel) {
			((ImageFilterPanel)fp).getPanel().setRegionsOfInterest(regionsOfInterest);
		}
		fp.addFilterDeleteHandler(this);
		if(ui.getDefineFT().getRowCount()>0){
			ui.getDefineFT().setWidget(ui.getDefineFT().getRowCount(), 0, new OperatorPanel(unique++));
		}
		ui.getDefineFT().setWidget(ui.getDefineFT().getRowCount(), 0, fp);
		//TablePresenter.get().addColumn(fp.getColumn());
	}
	
	private void addAggregateFilterPanel(FilterPanel fp) {
		if(fp instanceof ImageFilterPanel) {
			((ImageFilterPanel)fp).getPanel().setRegionsOfInterest(regionsOfInterest);
		}
		fp.addFilterDeleteHandler(this);
		if(ui.getAggregateFilterPanel().getRowCount()>0){
			ui.getAggregateFilterPanel().setWidget(ui.getAggregateFilterPanel().getRowCount(), 0, new OperatorPanel(unique++));
		}
		ui.getAggregateFilterPanel().setWidget(ui.getAggregateFilterPanel().getRowCount(), 0, fp);
	}
	private void rebuildFilterPanel(FilterPanel fp) {
		if(fp instanceof ImageFilterPanel) {
			((ImageFilterPanel)fp).getPanel().setRegionsOfInterest(regionsOfInterest);
		}
		fp.addFilterDeleteHandler(this);
		ui.getDefineFT().setWidget(ui.getDefineFT().getRowCount(), 0, fp);
	}
	private void rebuildOpPanel(OperatorPanel op) {
		ui.getDefineFT().setWidget(ui.getDefineFT().getRowCount(), 0, op);
	}
	private void rebuildAggregateFilterPanel(FilterPanel fp) {
		if(fp instanceof ImageFilterPanel) {
			((ImageFilterPanel)fp).getPanel().setRegionsOfInterest(regionsOfInterest);
		}
		fp.addFilterDeleteHandler(this);
		if(ui.getAggregateFilterPanel().getRowCount()>0){
			ui.getAggregateFilterPanel().setWidget(ui.getAggregateFilterPanel().getRowCount(), 0, new OperatorPanel(unique++));
		}
		ui.getAggregateFilterPanel().setWidget(ui.getAggregateFilterPanel().getRowCount(), 0, fp);
	}
	private void rebuildAggregateOpPanel(OperatorPanel op) {
		ui.getAggregateFilterPanel().setWidget(ui.getAggregateFilterPanel().getRowCount(), 0, op);
	}
	
	public void addToGroupBy(Column col) {
		GroupByPanel gbp = new GroupByPanel(col, FilterPresenter.this);
		ui.getGroupByPanel().setWidget(ui.getGroupByPanel().getRowCount(), 0, gbp);
		gbp.addGroupByDeletehandler(FilterPresenter.this);
	}

	@Override
	public void OnFilterDeleteEvent(FilterDeleteEvent e) {
		
		if(e.getSource() instanceof IntervalFilterPanel)
			((IntervalFilterPanel)e.getSource()).unregisterDropControllers();
		
		if (this.ui.isKMMode()) {
			int i = getKMWidgetRow(((FilterPanel) e.getSource())) - 1;
			if (i >= 0) {
				ui.getDefineFT().removeRow(i);
			} else if (ui.getDefineFT().getRowCount() > 1) {
				ui.getDefineFT().removeRow(i + 2);
			}

			ui.getDefineFT().removeRow(
					getKMWidgetRow((FilterPanel) e.getSource()));
		} else if (this.ui.isQueryMode()) {
			int i = getWidgetRow(((FilterPanel) e.getSource())) - 1;
			if (i >= 0) {
				ui.getMainPanel().removeRow(i);
			} else if (ui.getMainPanel().getRowCount() > 1) {
				ui.getMainPanel().removeRow(i + 2);
			}

			ui.getMainPanel().removeRow(
					getWidgetRow((FilterPanel) e.getSource()));
		} else if (this.ui.isAggregateMode()) {		
			int i = getFilterByWidgetRow(((FilterPanel) e.getSource())) - 1;
			if (i >= 0) {
				ui.getAggregateFilterPanel().removeRow(i);
			} else if (ui.getAggregateFilterPanel().getRowCount() > 1) {
				ui.getAggregateFilterPanel().removeRow(i + 2);
			}

			ui.getAggregateFilterPanel().removeRow(
					getFilterByWidgetRow((FilterPanel) e.getSource()));
		}
	}

	@Override
	public void OnAggregateDeleteEvent(AggregateDeleteEvent e) {
		int i = getAggregateWidgetRow(((AggregatePanel)e.getSource()));
		if(i>=0){
			ui.getAggregatePanel().removeRow(i);
		}
	}

	@Override
	public void OnGroupByDeleteEvent(GroupByDeleteEvent e) {
		int i = getGroupByWidgetRow((GroupByPanel)e.getSource());
		if(i>=0) {
			ui.getGroupByPanel().removeRow(i);
		}
		TablePresenter.get().removeColumn(((GroupByPanel)e.getSource()).getColumn());
	}
	
	public NestedFilter getFilter()
	{
		NestedFilter f = new NestedFilter();
		Op op=null;
		try{
			for (int i=0; i<ui.getDefineFT().getRowCount();i++)
			{
				Widget widget = ui.getDefineFT().getWidget(i, 0);
				if(widget instanceof OperatorPanel){
					op = ((OperatorPanel)widget).isAnd()?Op.AND:Op.OR;
					f.operator(op);
				}else if(widget instanceof FilterPanel){
					FilterPanel panel = (FilterPanel) widget;
					if(panel.lbrack()){
						f.startNestedPart();
					}
					f.add(panel.getFilter());
					if(panel.rbrack()){
						f.endNestedPart();
					}
				}
			}
		}catch(FilterStateException e){
			this.error(e);
			return null;
		}
		if(!f.stackEmpty()){
			this.error(new FilterStateException("Unclosed brackets."));
			return null;
		}
		return f;
	}
	
	public NestedFilter getKMFilter() {
		NestedFilter f = new NestedFilter();
		Op op=null;
		try{
			for (int i=0; i<ui.getDefineFT().getRowCount();i++)
			{
				Widget widget = ui.getDefineFT().getWidget(i, 0);
				if(widget instanceof OperatorPanel){
					op = ((OperatorPanel)widget).isAnd()?Op.AND:Op.OR;
					f.operator(op);
				}else if(widget instanceof FilterPanel){
					FilterPanel panel = (FilterPanel) widget;
					if(panel.lbrack()){
						f.startNestedPart();
					}
					f.add(panel.getFilter());
					if(panel.rbrack()){
						f.endNestedPart();
					}
				}
			}
		}catch(FilterStateException e){
			this.error(e);
			return null;
		}
		if(!f.stackEmpty()){
			this.error(new FilterStateException("Unclosed brackets."));
			return null;
		}
		return f;
	}
	
	public NestedFilter getAggregateFilter()
	{
		NestedFilter f = new NestedFilter();
		Op op=null;
		try{
			for (int i=0; i<ui.getAggregateFilterPanel().getRowCount();i++)
			{
				Widget widget = ui.getAggregateFilterPanel().getWidget(i, 0);
				if(widget instanceof OperatorPanel){
					op = ((OperatorPanel)widget).isAnd()?Op.AND:Op.OR;
					f.operator(op);
				}else if(widget instanceof FilterPanel){
					FilterPanel panel = (FilterPanel) widget;
					if(panel.lbrack()){
						f.startNestedPart();
					}
					f.add(panel.getFilter());
					if(panel.rbrack()){
						f.endNestedPart();
					}
				}
			}
		}catch(FilterStateException e){
			this.error(e);
			return null;
		}
		if(!f.stackEmpty()){
			this.error(new FilterStateException("Unclosed brackets."));
			return null;
		}
		return f;
	}
	
	public boolean isInGroupBy(Column col) {
		for(int i = 0; i < this.ui.getGroupByPanel().getRowCount(); i++) {
			if(((GroupByPanel)this.ui.getGroupByPanel().getWidget(i, 0)).getColumn().equals(col)) {
				return true;
			}
		}
		return false;
	}
	
	public int getKMWidgetRow(Widget w)
	{
		for (int i = 0; i < ui.getDefineFT().getRowCount(); ++i )
		{
			if ( ui.getDefineFT().getWidget(i, 0) == w)
				return i;
		}
		return -1;
	}

	public int getWidgetRow(Widget w)
	{
		for (int i = 0; i < ui.getMainPanel().getRowCount(); ++i )
		{
			if ( ui.getMainPanel().getWidget(i, 0) == w)
				return i;
		}
		return -1;
	}
	public int getAggregateWidgetRow(Widget w)
	{
		for (int i = 0; i < ui.getAggregatePanel().getRowCount(); ++i )
		{
			if ( ui.getAggregatePanel().getWidget(i, 0) == w)
				return i;
		}
		return -1;
	}
	public int getGroupByWidgetRow(Widget w)
	{
		for (int i = 0; i < ui.getGroupByPanel().getRowCount(); ++i )
		{
			if ( ui.getGroupByPanel().getWidget(i, 0) == w)
				return i;
		}
		return -1;
	}
	public int getFilterByWidgetRow(Widget w)
	{
		for (int i = 0; i < ui.getAggregateFilterPanel().getRowCount(); ++i )
		{
			if ( ui.getAggregateFilterPanel().getWidget(i, 0) == w)
				return i;
		}
		return -1;
	}
	
	public Boolean isExpertMode() {
		return ui.isExpertMode();
	}
	public Boolean isQueryMode() {
		return ui.isQueryMode();
	}
	public Boolean isAggregateMode() {
		return ui.isAggregateMode();
	}
	public Boolean isKMMode() {
		return ui.isKMMode();
	}
	public String getExpertText() {
		return ui.getExpertInput().getText();
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == exportDoneButton) {
			imageSelectionBox.hide();
			imageSelectionBox = null;
		} else if (event.getSource() == saveQCancelButton){
			saveQueryBox.hide();
			saveQueryBox = null;
		} else if (event.getSource() == saveQDoneButton) {
			saveNamedQuery(saveQP.getName());
			saveQueryBox.hide();
			saveQueryBox = null;
		} else if (event.getSource() == deleteQCancelButton){
			deleteQueryBox.hide();
			deleteQueryBox = null;
		} else if (event.getSource() == deleteQDoneButton) {
			deleteQueryBox.hide();
			deleteQueryBox = null;
		}
	}
	
	public void saveNamedQuery(String name) {
		SavedQuery sq = new SavedQuery();
		sq.setName(name);
		sq.setIsNamed(true);
		sq.setTableData(TablePresenter.get().getTableAsList());
		sq.setTableColumns(TablePresenter.get().getColumns());
		sq.setDefinitionData(getAllQueryFilterRebuildData());
		System.err.println("FilterPresenter.saveNamedQuery method: " + name);
		rpcService.saveQuery(sq, new PresenterCallback<Void>(FilterPresenter.this) {

			@Override
			protected void success(Void result) {
				CategoryPresenter.get().getNamedQueries();
				CategoryPresenter.get().getUnnamedQueries();
			}
		});
	}
	
	//for between sessions, only one -- possible use a name
	public void saveUnnamedQuery() {
		SavedQuery sq = new SavedQuery();
		sq.setIsNamed(false);
		sq.setTableData(TablePresenter.get().getTableAsList());
		sq.setTableColumns(TablePresenter.get().getColumns());
		sq.setDefinitionData(getAllQueryFilterRebuildData());
		rpcService.saveQuery(sq, new PresenterCallback<Void>(FilterPresenter.this) {

			@Override
			protected void success(Void result) {
				CategoryPresenter.get().getNamedQueries();
				CategoryPresenter.get().getUnnamedQueries();
			}
		});
	}
	
	public List<RebuildDataAggregatePanel> getAggregateRebuildData() {
		List<RebuildDataAggregatePanel> rebuildData = new ArrayList<RebuildDataAggregatePanel>();
		RebuildDataAggregatePanel currRebuildData;
		for(int i = 0; i < ui.getAggregatePanel().getRowCount(); i++) {
			currRebuildData = new RebuildDataAggregatePanel();
			((AggregatePanel)ui.getAggregatePanel().getWidget(i, 0)).storeState(currRebuildData);
			rebuildData.add(currRebuildData);
		}
		return rebuildData;
	}
	
	public List<DefinitionData> getAllQueryFilterRebuildData() {
		ArrayList<DefinitionData> allData = new ArrayList<DefinitionData>();
		for (int i = 0; i < ui.getDefinitionsFT().getRowCount(); i++) {
			Widget w = ui.getDefinitionsFT().getWidget(i, 0);
			if (w instanceof DefinitionPanel) {
				DefinitionPanel dp = ((DefinitionPanel)w);
				allData.add(dp.getData());
			} else {
				System.err.println("INSERTION OF NON DefinitionPanel in DefinitionsFT");
			}
		}
		return allData;
	}
	
	public List<RebuildDataFilterPanel> getQueryFilterRebuildData() {
		//Window.alert("Building rebuild data");
		List<RebuildDataFilterPanel> rebuildData = new ArrayList<RebuildDataFilterPanel>();
		RebuildDataFilterPanel currRebuildData;
		for(int i = 0; i < ui.getDefineFT().getRowCount(); i++) {
			currRebuildData = new RebuildDataFilterPanel();
			if(ui.getDefineFT().getWidget(i, 0) instanceof OperatorPanel) {
				//Window.alert("Operator Panel");
				((OperatorPanel)ui.getDefineFT().getWidget(i, 0)).storeState(currRebuildData);
			} else {
				//Window.alert("Filter Panel");
				((FilterPanel)ui.getDefineFT().getWidget(i, 0)).storeState(currRebuildData);
			}
			rebuildData.add(currRebuildData);
		}
		//Window.alert("Size: " + rebuildData.size());
		
		/*for(int i = 0; i<rebuildData.size(); i++){
			RebuildDataFilterPanel rb = rebuildData.get(i);
			if(rb.isOperator()) {
				Window.alert("Unique: " + rb.getUnique() + " And? " + rb.isAnd());
			} else if (rb.isInterval()){
				Window.alert("filter");
				Window.alert(rb.isLeftPar() + "LP ");
				Window.alert(" Op " + rb.getOpIndex());
				Window.alert(" Name1 " + rb.getCol());
				Window.alert(" Name2 " + rb.getCol2());
				Window.alert(" Val " + rb.getValue());
				Window.alert(" Time " + rb.getTimeIndex());
				Window.alert(" RP" + rb.isRightPar());
			} else {
				Window.alert("filter");
				Window.alert(rb.isLeftPar() + "LP ");
				Window.alert(" Op " + rb.getOpIndex());
				Window.alert(" Name " + rb.getCol());
				Window.alert(" Val " + rb.getValue());
				Window.alert(" RP" + rb.isRightPar());
			}
		}*/

		return rebuildData;
	}
	
	public List<RebuildDataFilterPanel> getAggregateFilterRebuildData() {
		List<RebuildDataFilterPanel> rebuildData = new ArrayList<RebuildDataFilterPanel>();
		RebuildDataFilterPanel currRebuildData;
		for(int i = 0; i < ui.getAggregateFilterPanel().getRowCount(); i++) {
			currRebuildData = new RebuildDataFilterPanel();
			if(ui.getAggregateFilterPanel().getWidget(i, 0) instanceof OperatorPanel) {
				((OperatorPanel)ui.getAggregateFilterPanel().getWidget(i, 0)).storeState(currRebuildData);
			} else {
				((FilterPanel)ui.getAggregateFilterPanel().getWidget(i, 0)).storeState(currRebuildData);
			}
			rebuildData.add(currRebuildData);
		}
		return rebuildData;
	}
	public void loadQuery(SavedQueryLabel lbl) {
		if(lbl == null) {
			return;
		}
		ui.clearAll();
		TablePresenter.get().clearAllModalityColumns();
		//this.loadQueryFilter(lbl);
		this.loadQueryTable(lbl);
	}
	public void loadQueryTable(SavedQueryLabel lbl) {
		rpcService.getSavedQuery(lbl.getSavedQueryId(), new PresenterCallback<SavedQuery>(FilterPresenter.this) {
			@Override
			protected void success(SavedQuery result) {
				if(result == null) {
					System.err.println("SQ RESULT IS NULL");
					return;
				}
				TablePresenter.get().setColumns(result.getTableColumns());
				TablePresenter.get().rebuildModelData(result.getTableData());
			}
		});
	}/*
	public void loadQueryFilter(SavedQueryLabel lbl) {
		ui.clearAll();
		rpcService.getSavedQuery(lbl.getSavedQueryId(), new PresenterCallback<SavedQuery>(FilterPresenter.this) {
			@Override
			protected void success(SavedQuery result) {
				if(result == null) {
					System.err.println("SQ RESULT IS NULL");
					return;
				}
				ui.getModality().selectTab(result.getMode().ordinal(), false);
				for(RebuildDataFilterPanel rb : result.getAggregateFilterPanels()) {
					if(rb.isOperator()) {
						OperatorPanel op = new OperatorPanel(rb.getUnique());
						op.setState(rb);
						FilterPresenter.this.rebuildAggregateOpPanel(op);
					} else {
						FilterPanel fp = rb.getCol().getFilterPanel();
						fp.setState(rb);
						FilterPresenter.this.rebuildAggregateFilterPanel(fp);
					}
				}
				for(RebuildDataFilterPanel rb : result.getQueryFilterPanels()) {
					if(rb.isOperator()) {
						OperatorPanel op = new OperatorPanel(rb.getUnique());
						op.setState(rb);
						FilterPresenter.this.rebuildOpPanel(op);
					} else {
						FilterPanel fp = rb.getCol().getFilterPanel();
						fp.setState(rb);
						FilterPresenter.this.rebuildFilterPanel(fp);
					}
				}
				for(Column col : result.getAggregateGroupByPanels()) {
					FilterPresenter.this.addToGroupBy(col);
				}
				for(RebuildDataAggregatePanel apData : result.getAggregatePanels()) {
					AggregatePanel ap = new AggregatePanel(apData.getCol(), FilterPresenter.this);
					ap.addAggregateDeleteHandler(FilterPresenter.this);
					ap.setState(apData);
					ui.getAggregatePanel().setWidget(ui.getAggregatePanel().getRowCount(), 0, ap);
				}
				ui.getExpertInput().setText(result.getExpertString());
			}
		});
	}*/
	
	public List<Column> getGroupByColumns() {
		List<Column> cols = new ArrayList<Column>();
		for(int i = 0; i < ui.getGroupByPanel().getRowCount(); i++) {
			cols.add(((GroupByPanel)ui.getGroupByPanel().getWidget(i, 0)).getColumn());
		}
		return cols;
	}
	
	public List<AggregateFunction> getAggregateFunctions() {
		List<AggregateFunction> fils = new ArrayList<AggregateFunction>();
		for(int i = 0; i < ui.getAggregatePanel().getRowCount(); i++) {
			fils.add(((AggregatePanel)ui.getAggregatePanel().getWidget(i, 0)).getFilter());
		}
		return fils;
	}
	public LinkedHashMap<String, Integer> getRegionsOfInterest() {
		return regionsOfInterest;
	}
	public void setExecuteRed() {
		ui.setExecuteRed();
	}
	public void clearExecuteRed() {
		ui.clearExecuteRed();
	}
}
