package btaw.client.modules.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import btaw.client.event.AddColumnEvent;
import btaw.client.event.ClearEvent;
import btaw.client.event.HeaderMenuSelectionEvent;
import btaw.client.event.PersonSelectEvent;
import btaw.client.event.PreviewDropEvent;
import btaw.client.event.QueryChangedEvent;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.event.handlers.AddColumnHandler;
import btaw.client.event.handlers.ClearEventHandler;
import btaw.client.event.handlers.DropEventHandler;
import btaw.client.event.handlers.HasDropHandlers;
import btaw.client.event.handlers.HeaderMenuSelectionHandler;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.filter.FilterView;
import btaw.client.modules.filter.FilterView.Modality;
import btaw.client.modules.filterheader.FilterHeaderPresenter;
import btaw.client.modules.filterheader.FilterHeaderView;
import btaw.client.modules.kaplanmeier.KaplanMeierNamePresenter;
import btaw.client.modules.kaplanmeier.KaplanMeierNameView;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.tab.BarchartTab;
import btaw.client.modules.tab.FreqTab;
import btaw.client.modules.tab.KaplanMeierTab;
import btaw.client.modules.tab.HistogramTab;
import btaw.client.modules.tab.PatientSummaryTab;
import btaw.client.modules.tab.PsaAggregateTab;
import btaw.client.modules.tab.DashboardTab;
import btaw.client.modules.tab.Tab;
import btaw.client.view.widgets.ClickableHeader;
import btaw.client.view.widgets.ClickableHeaderNoContextMenu;
import btaw.client.view.widgets.ColumnLabel;
import btaw.client.view.widgets.MagicTextColumn;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.client.view.widgets.TabCloseButton;
import btaw.shared.model.DefinitionData;
import btaw.shared.model.FunctionData;
import btaw.shared.model.KM_Graph;
import btaw.shared.model.PSA_Graph;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.Table;
import btaw.shared.model.query.column.BooleanTableColumn;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.column.FunctionTableColumn;
import btaw.shared.model.query.column.IntegerTableColumn;
import btaw.shared.model.query.column.SavedDefinitionColumn;
import btaw.shared.model.query.column.StringTableColumn;
import btaw.shared.model.query.column.SynthTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.ImageQueryFilter;
import btaw.shared.model.query.filter.RegionContrastFilter;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.visualization.client.events.RangeChangeHandler;

public class TablePresenter extends Presenter implements DropEventHandler,HeaderMenuSelectionHandler {
	
	private static TablePresenter stat;
	
	public interface View extends IView {
		HasDropHandlers getDropController();
		public CellTable<TableRow> getTable();
		public SingleSelectionModel<TableRow> getSmodel();
		public void setEdited();
		public void setCurrent();
		void setKMChart(String url, String name);
		void setTable();
		VerticalPanel getStatisticVP();
		void setFunctionResult(String str);
		void setFunctionResultOff();
		void setVisibleOuterStatsVP(boolean bool);
		void setStatisticLabel(String s);
		TextBox getBinSizeTB();
		void setBinSizeVisible(boolean bool);
		CheckBox addHistogramSelectorElement(String name);
		ArrayList<String> getCheckedCBs();
		void clearHistogramSelector();
		void setPagination();
		void setPageSize(int i);
		TextBox getPageSizeTB();
		PushButton getMaximizeButton();
		PushButton getMinimizeButton();
		void selectTableTab();
		PushButton addKMTab(String name);
		void closeTab(Tab tab);
		Tab getTab();
		PushButton addHistTab(String result);
		Tab getCurrentTab();
		PushButton addPatientSummaryTab(String name);
		PushButton addBarchartTab(HeaderMenuSelectionController headerController, String name);
		PushButton addFreqTab(HeaderMenuSelectionController headerController, String name);
		PushButton addPsaAggregateTab(String name);
		PushButton addDashboardTab(HeaderMenuSelectionController headerController, String name);
	}
	
	public static TablePresenter get(){
		return stat;
	}
	private View ui;
	private CellTable<TableRow> table;

	private final HeaderMenuSelectionController headerController;
	private MagicTextColumn sortColumn;
	protected Column pid;
	protected Column definition;
	//private MagicTextColumn histCol;
	protected com.google.gwt.user.cellview.client.Column<TableRow, String> displayStudies;
	private String sortOrder="ASC";

	private int sim_cnt = 0;
	private boolean study_date = false;
	private Column sdate_column;
	ArrayList<Column> image_columns;
	Set<Filter> image_filters;
	Set<Filter> aggregate_filters;
	Set<Filter> ratio_filters;
	
	List<TableRow> tableCopy;
	List<MagicTextColumn> aggregateCols;
	List<MagicTextColumn> expertCols;
	List<MagicTextColumn> queryCols;
	
	private Modality previousModality = FilterView.Modality.QUERY;
	
	public TablePresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
		this.table = ui.getTable();
		this.headerController = new HeaderMenuSelectionController(this);
		this.headerController.addHeaderMenuSelectionHandler(this);
		//DisplayScansButtonCell buttonCell = new DisplayScansButtonCell();
		/*this.displayStudies = new com.google.gwt.user.cellview.client.Column<TableRow, String>(buttonCell) {
			
			@Override
			public String getValue(TableRow object) {
				try {
                    if(object.getRowData("HasScansBoolean").contains("true"))
                    	return "Display  Scans";
                    else
                    	return "No Scans Found";
                } catch (Exception e) {
                    return "No Scans Found";
                }
			}
		};*/
		
		/*this.displayStudies.setFieldUpdater(new FieldUpdater<TableRow, String>() {

			@Override
			public void update(int index, TableRow object, String value) {
				TablePresenter.this.fireEvent(new PersonSelectEvent(object.getRowData("pid")));
			}
			
		});*/
		
		//ClickableTextCell cell = new ClickableTextCell();
		//ClickableHeaderNoContextMenu header = new ClickableHeaderNoContextMenu(cell, this.displayStudies);
		//header.setText("Launch Viewer");
		
		//table.addColumn(this.displayStudies, header);

		Table t = new Table("SKIPME");
		this.definition = new StringTableColumn("Definition", "definitionIdentifier", "", t);
	
		aggregateCols = new ArrayList<MagicTextColumn>();
		expertCols = new ArrayList<MagicTextColumn>();
		queryCols = new ArrayList<MagicTextColumn>();
		
		image_columns = new ArrayList<Column>();
		image_filters = new HashSet<Filter>();
		aggregate_filters = new HashSet<Filter>();
		ratio_filters = new HashSet<Filter>();
		
		stat=this;
	
		this.exportConstructPatientSummary();
	}

	public void addHeaderMenuSelectionHandler(HeaderMenuSelectionHandler handler) {
		this.headerController.addHeaderMenuSelectionHandler(handler);
	}

	@Override
	protected void bindAll() {
		this.rpcService.getPidColumn(new PresenterCallback<Column>(this) {
			
			@Override
			protected void success(Column result) {
				TablePresenter.this.pid=result;
				TablePresenter.this.addDefaultColumns();
			}
		}
		);
		ui.getDropController().addDropHandler(this);
		bindEvent(ClearEvent.TYPE, new ClearEventHandler() {
			
			@Override
			public void onClearEvent(ClearEvent e) {
				for(int i =0; i<TablePresenter.this.table.getColumnCount();i++){
					if(!(TablePresenter.this.table.getColumn(i) instanceof MagicTextColumn))
						continue;
					MagicTextColumn col = (MagicTextColumn) TablePresenter.this.table.getColumn(i);
					if(col.getColumn() instanceof SynthTableColumn){
						rpcService.dropSynthTable((SynthTableColumn) col.getColumn(), true, new PresenterCallback<Void>(TablePresenter.this){

							@Override
							protected void success(Void result) {
								
							}});
					}
				}
				while(table.getColumnCount()>0){
					table.removeColumn(0);
				}
				study_date = false;
				sim_cnt = 0;
				TablePresenter.this.addDefaultColumns();
				ui.setFunctionResultOff();
			}
		});
		bindEvent(AddColumnEvent.TYPE, new AddColumnHandler() {
			@Override
			public void OnAddColumnEvent(AddColumnEvent e) {
				ui.setTable();
				FilterPresenter.get().addColumnToDefinitions(e.getColumn());
			}
		});
		/*ui.getBinSizeTB().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Integer i = (int)Math.round(Double.parseDouble(ui.getBinSizeTB().getText()));
				//ui.getStatisticVP().clear();
				generateHist(i, true);
			}
		});*/
		ui.getPageSizeTB().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Integer i = (int)Math.round(Double.parseDouble(ui.getPageSizeTB().getText()));
				ui.setPageSize(Integer.valueOf(i));
				ui.setPagination();
			}
		});
		ui.getMaximizeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPresenter.get().maximizeTableView();
			}
		});
		ui.getMinimizeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainPresenter.get().minimizeTableView();
			}
		});
		table.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Range range = table.getVisibleRange();
			    int start = range.getStart();
			    int length = range.getLength();
			    List<TableRow> toSet = new ArrayList<TableRow>(length);
			    for (int i = start; i < start + length && i < tableCopy.size(); i++)
			        toSet.add((TableRow) tableCopy.get(i));
			    table.setRowData(start, toSet);
			}
			
		});
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}

	@Override
	protected void activated(HasWidgets container) {

	}

	@Override
	public void OnPreviewDrop(PreviewDropEvent e) {
		// Fire event to query engine new column
		// Query Engine asks Filter presenter for the current filter and Table
		// presenter for current columns
		// Then passes the "Query" object to the backend to deal with.
		// Query engine passes new patient list to presenter to rebuild view
		/*ui.setTable();
		if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
			FilterPresenter.get().loadQueryTable((SavedQueryLabel)e.GetContext().selectedWidgets.get(0));
			return;
		}
		ColumnLabel l = (ColumnLabel) e.GetContext().selectedWidgets.get(0);

		System.out.println("Column " + l.getColumn().getName() + " dropped.");
		
		fireEvent(new AddColumnEvent(l.getColumn()));
		
		ui.setEdited();*/
		if(e.GetContext().selectedWidgets.get(0) instanceof FlowPanel) {
			return;
		}
		if(((ColumnLabel)e.GetContext().selectedWidgets.get(0)).getColumn() instanceof SavedDefinitionColumn | table.getRowCount()==0) {
			return;
		}

		ColumnLabel l = (ColumnLabel) e.GetContext().selectedWidgets.get(0);
		FilterPresenter.get().addColumnToCurrentDefinitions(l.getColumn());
		
	}
	
	public void addDefaultColumns() {
		this.addColumn(pid);
		this.addColumn(definition);
	}

	public boolean addColumn(Column column) {
		for (int i = 0; i < table.getColumnCount(); ++i) {
			if(!(TablePresenter.this.table.getColumn(i) instanceof MagicTextColumn))
				continue;
			MagicTextColumn c = (MagicTextColumn) table.getColumn(i);
			if (c.getColumn().equals(column))
				return false;
		}
		
		ClickableTextCell cell = new ClickableTextCell();
		MagicTextColumn col;

		if (column.getName().equals("Patient ID")){
			//System.out.println("In AddColumn : " + column.getName());

			PID_ButtonCell PID_cell = new PID_ButtonCell();
			col = new MagicTextColumn(column,PID_cell);
			col.setCellStyleNames("pidColumnStyle");
			col.setFieldUpdater(new FieldUpdater<TableRow, String>() {
				@Override
				public void update(int index, TableRow object, String value) {
					constructPatientSummary(object.getRowData("pid"));
					
					/*final Tab currentTab = addPatientSummaryTab(object.getRowData("pid"));
					
					//MainPresenter.get().working(false);
					
					rpcService.getPsaValues(object.getRowData("pid"), 
							new PresenterCallback<PSA_Graph>(TablePresenter.get()) {

						@Override
						protected void success(PSA_Graph result) {
									
							List<String> dates = result.getDates();
							List<String> psa_vals = result.getVals();
									
							JsArrayString jsDates = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jsPSA_Vals = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayString jsImplant = (JsArrayString)JavaScriptObject.createArray().cast();
							JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
							jsImplant.push(result.getImplantDate());
							jsSize.push(600); //Width
							jsSize.push(400); //Height
									
							for (int i = 0; i<dates.size(); i++) {
								jsDates.push(dates.get(i));
								jsPSA_Vals.push(psa_vals.get(i));
							}

							((PatientSummaryTab)currentTab).populateStudiesAndShow(jsPSA_Vals, jsDates, jsImplant, jsSize);
							//MainPresenter.get().finished();
						}
					});
					rpcService.getPatientSummary(object.getRowData("pid"),
						new PresenterCallback<ArrayList<String>>(TablePresenter.get()) {
							@Override
							protected void success(ArrayList<String> result) {
								JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
								JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
								jsSize.push(200); //Width
								jsSize.push(200); //Height
								
								for (int i = 0; i<result.size(); i++) {
									jsVals.push(result.get(i));
								}
								
								//((PatientSummaryTab)currentTab).populateTableAndShow(jsVals, jsSize);
								((PatientSummaryTab)currentTab).populateTableAndShow(result);
							}
					});*/
					
				}
			});
		} else {
			col = new MagicTextColumn(column,cell);
		}

		ClickableHeader header = new ClickableHeader(cell, col, this.headerController);
		header.setText(column.getName());

		table.addColumn(col, header);

		return true;
	}
	
	public native void exportConstructPatientSummary()/*-{
		var that = this;
		$wnd.constructPatientSummary = $entry(function(pid) {
			that.@btaw.client.modules.table.TablePresenter::constructPatientSummary(Ljava/lang/String;)(pid);
		});
	}-*/;
	
	public void constructPatientSummary(String pid){
		final Tab currentTab = addPatientSummaryTab(pid);
		
		//MainPresenter.get().working(false);

		rpcService.getPsaValues(pid, new PresenterCallback<PSA_Graph>(TablePresenter.get()) {

			@Override
			protected void success(PSA_Graph result) {
						
				List<String> dates = result.getDates();
				List<String> psa_vals = result.getVals();
						
				JsArrayString jsDates = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsPSA_Vals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsImplant = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				jsImplant.push(result.getImplantDate());
				jsSize.push(700); //Width
				jsSize.push(300); //Height
						
				for (int i = 0; i<dates.size(); i++) {
					jsDates.push(dates.get(i));
					jsPSA_Vals.push(psa_vals.get(i));
				}

				((PatientSummaryTab)currentTab).populateStudiesAndShow(jsPSA_Vals, jsDates, jsImplant, jsSize);
				//MainPresenter.get().finished();
			}
		});
		rpcService.getPatientSummary(pid,new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
				@Override
				protected void success(List<List<String>> result) {
					((PatientSummaryTab)currentTab).populateTableAndShow(result);
				}
		});
		rpcService.getPatientEvents(pid,new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {
				/*System.out.println("IN table presenter: ");
				for (int j = 0; j<result.size(); j++){
		        	List<String> r = result.get(j);
		            for (int i = 0; i<r.size(); i++){
		            	System.out.print(r.get(i) + "|");
		            }
		            System.out.println();
		        }*/
				((PatientSummaryTab)currentTab).populateEventsAndShow(result);
			}
		});
		
	}
	
	public void constructDashboard(String defNames, Set<String> pids){
		final Tab currentTab = addDashboardTab(defNames);
		
		rpcService.getDashboard(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {
				((DashboardTab)currentTab).populateTableAndShow(result);
			}
		});
		rpcService.getAgeSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayInteger jsDim = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Age 01 Cohort");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
				jsDim.push(20); //min
				jsDim.push(100); //max
				jsDim.push(2); //sub
				jsDim.push(10); //tick_interval
						
				List<String> cohort_01 = result.get(0);
				List<String> cohort_03 = result.get(1);

				for (int i = 0; i<cohort_01.size(); i++) {
					jsVals.push(cohort_01.get(i));
				}
				((DashboardTab)currentTab).addHistogram("Age_01_Module",jsVals, jsSize, jsTitle, jsDim);
				
				jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Age 03 Cohort");
				for (int i = 0; i<cohort_03.size(); i++) {
					jsVals.push(cohort_03.get(i));
				}
				((DashboardTab)currentTab).addHistogram("Age_03_Module", jsVals, jsSize, jsTitle, jsDim);
			}
		});
		rpcService.getGleasonSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsRows = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsCols1 = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsCols3 = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsCells1 = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsCells3 = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle1 = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle3 = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle1.push("Gleason 01 Cohort");
				jsTitle3.push("Gleason 03 Cohort");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
				
				List<String> rows = Arrays.asList("Primary", "Secondary", "Total");
				List<String> cols = Arrays.asList("Mean",    "Stdev",   "Range");
				//List<String> cells = Arrays.asList("1",    "2",   "3",     "4",    "5",    "6",        "7",      "8",   "9");
				List<String> cells1 = result.get(0);
				List<String> cells3 = result.get(1);
				
				for (int i = 0; i<rows.size(); i++) {
					jsRows.push(rows.get(i));
					jsCols1.push(cols.get(i));
					jsCols3.push(cols.get(i));
				}
				for (int i = 0; i<cells1.size(); i++) {
					jsCells1.push(cells1.get(i));
					jsCells3.push(cells3.get(i));
				}
				
				((DashboardTab)currentTab).addTable("Gleason_01_Module", jsRows, jsCols1, jsCells1, jsSize, jsTitle1);
				((DashboardTab)currentTab).addTable("Gleason_03_Module", jsRows, jsCols3, jsCells3, jsSize, jsTitle3);
				
				/*
				//This is not finished. Will have to make the grouped bar chart first
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Gleason 01 Cohort");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
						
				List<String> cohort_01 = result.get(0);
				List<String> cohort_03 = result.get(1);

				for (int i = 0; i<cohort_01.size(); i++) {
					jsVals.push(cohort_01.get(i));
				}
				//((DashboardTab)currentTab).addHistogram(jsVals, jsSize, jsTitle);
				
				jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Gleason 03 Cohort");
				for (int i = 0; i<cohort_03.size(); i++) {
					jsVals.push(cohort_03.get(i));
				}
				//((DashboardTab)currentTab).addHistogram(jsVals, jsSize, jsTitle);*/
			}
		});
		rpcService.getPrimaryTxSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Primary Tx");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
						
				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Primary_Treatments_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getBarSummary(pids, "msc_003a","miscellaneous",new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Risk Level");
				jsSize.push(300); //Width
				jsSize.push(200); //Height

				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Risk_Module",jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getProgressionSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Progression");
				jsSize.push(300); //Width
				jsSize.push(200); //Height

				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Progression_Module", jsVals, jsCats,  jsSize, jsTitle);
			}
		});
		rpcService.getMetastasisSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Metastasis");
				jsSize.push(300); //Width
				jsSize.push(200); //Height

				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Metastasization_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getBiopsySummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Biopsy (01 only)");
				jsSize.push(300); //Width
				jsSize.push(200); //Height

				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Biopsy_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getDrugTxSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Drug Therapies");
				jsSize.push(300); //Width
				jsSize.push(200); //Height

				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Drug_Therapies_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getBarSummary(pids, "in_002","intake",new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
				@Override
				protected void success(List<List<String>> result) {

					JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
					JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
					JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
					JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
					jsTitle.push("Race");
					jsSize.push(300); //Width
					jsSize.push(200); //Height
							
					//List<String> categories = Arrays.asList(  "Red", "Green", "Blue", "Purple", "Pink", "Yellow","Aquamarine","Turqoise","Steel","Black");
					//List<String> values = Arrays.asList("23",    "55",   "79",     "30",    "3",    "100",        "10",      "23",   "26",   "47");
					List<String> categories = result.get(0);
					List<String> values = result.get(1);
					
					for (int i = 0; i<categories.size(); i++) {
						jsCats.push(categories.get(i));
						jsVals.push(values.get(i));
					}
					((DashboardTab)currentTab).addBarChart("Race_Module", jsVals, jsCats, jsSize, jsTitle);
				}
		});
		rpcService.getBarSummary(pids,"ib_077","biopsy_diagnostic",new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("T Stage");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
						
				//List<String> categories = Arrays.asList(  "Red", "Green", "Blue", "Purple", "Pink", "Yellow","Aquamarine","Turqoise","Steel","Black");
				//List<String> values = Arrays.asList("23",    "55",   "79",     "30",    "3",    "100",        "10",      "23",   "26",   "47");
				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("T_Stage_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getBarSummary(pids,"ib_081","biopsy_diagnostic",new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("N Stage");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
						
				//List<String> categories = Arrays.asList(  "Red", "Green", "Blue", "Purple", "Pink", "Yellow","Aquamarine","Turqoise","Steel","Black");
				//List<String> values = Arrays.asList("23",    "55",   "79",     "30",    "3",    "100",        "10",      "23",   "26",   "47");
				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("N_Stage_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getBarSummary(pids,"ib_082","biopsy_diagnostic",new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("M Stage");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
						
				//List<String> categories = Arrays.asList(  "Red", "Green", "Blue", "Purple", "Pink", "Yellow","Aquamarine","Turqoise","Steel","Black");
				//List<String> values = Arrays.asList("23",    "55",   "79",     "30",    "3",    "100",        "10",      "23",   "26",   "47");
				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("M_Stage_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getBarSummary(pids,"rp_037","prostatectomy_pathology",new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("Pathologic");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
						
				//List<String> categories = Arrays.asList(  "Red", "Green", "Blue", "Purple", "Pink", "Yellow","Aquamarine","Turqoise","Steel","Black");
				//List<String> values = Arrays.asList("23",    "55",   "79",     "30",    "3",    "100",        "10",      "23",   "26",   "47");
				List<String> categories = result.get(0);
				List<String> values = result.get(1);
				
				for (int i = 0; i<categories.size(); i++) {
					jsCats.push(categories.get(i));
					jsVals.push(values.get(i));
				}
				((DashboardTab)currentTab).addBarChart("Path_Stage_Module", jsVals, jsCats, jsSize, jsTitle);
			}
		});
		rpcService.getPSACohortSummary(pids, new PresenterCallback<List<List<String>>>(TablePresenter.get()) {
			@Override
			protected void success(List<List<String>> result) {

				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayInteger jsDim = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("PSA 01 Cohort");
				jsSize.push(300); //Width
				jsSize.push(200); //Height
				jsDim.push(0); //min
				jsDim.push(100); //max
				jsDim.push(2); //sub
				jsDim.push(10); //tick_interval

				List<String> cohort_01 = result.get(0);
				List<String> cohort_03 = result.get(1);

				//int max = 0;
				//int tmp = 0;
				for (int i = 0; i<cohort_01.size(); i++) {
					jsVals.push(cohort_01.get(i));
					//tmp = (int) Math.round(Integer.parseInt(cohort_01.get(i)));
					//if (tmp>max)
					//	max = tmp;
				}
				//jsDim.set(1, max);
				((DashboardTab)currentTab).addHistogram("PSA_01_Module",jsVals, jsSize, jsTitle, jsDim);
				
				jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push("PSA 03 Cohort");
				
				//max = 0;
				for (int i = 0; i<cohort_03.size(); i++) {
					jsVals.push(cohort_03.get(i));
					//tmp = (int) Math.round(Integer.parseInt(cohort_01.get(i)));
					//if (tmp>max)
					//	max = tmp;
				}
				//jsDim.set(1, max);
				//System.err.println("THE MAX: " + max);
				((DashboardTab)currentTab).addHistogram("PSA_03_Module", jsVals, jsSize, jsTitle, jsDim);
			}
		});
	}
	
	public boolean addColumn(MagicTextColumn column) {
		for (int i = 0; i < table.getColumnCount(); ++i) {
			MagicTextColumn c = (MagicTextColumn) table.getColumn(i);
			if (c.getColumn().equals(column.getColumn()))
				return false;
		}
		ClickableTextCell cell = new ClickableTextCell();
		ClickableHeader header = new ClickableHeader(cell, column,
				this.headerController);
		header.setText(column.getColumnName());

		table.addColumn(column, header);

		return true;
	}

	public void rebuildModelData(List<TableRow> values, List<Column> newCols) {
		ui.setTable();
		
		List<Column> currentCols = this.getColumns();
		
		for(Column col : newCols){
			//Window.alert("Column: " + col.getName());
			if(!currentCols.contains(col)){
				this.addColumn(col);
				//Window.alert("added");
			}
		}

		for(Column col : currentCols){
			if(!newCols.contains(col) && !(col.getName().equals("Patient ID") || col.getName().equals("Definition"))){
				this.removeColumn(col);
				//Window.alert("removed " + col.getName());
			}
		}
		
		/*String tmp = new String();
		
		tmp = tmp.concat("LIST SIZE : : : " + values.size() + "\n");
		TableRow R = values.get(10);
		HashMap<String,String> HM = R.getRowHM();
		tmp = tmp.concat("KEY SET   : : : " + HM.keySet() + "\n");
		for (int i = 0; i < values.size(); i++)
			tmp = tmp.concat("ROW " + i + "    : : : " + values.get(i).getRowData("pid") + " " + values.get(i).getRowData("definitionIdentifier") + " " + values.get(i).getRowData("date of birth - date signed gt 5.3 years") + "\n");
		Window.alert(tmp);*/
		
		tableCopy = values;
		table.setRowData(values);
		this.ui.setPagination();
		ui.selectTableTab();
	}
	
	public void rebuildModelData(List<TableRow> values) {
		System.out.println("REBUILD MODEL DATA old");
		ui.setTable();
		tableCopy = values;
		table.setRowData(values);
		this.ui.setPagination();
		ui.selectTableTab();
	}
	
	public List<TableRow> getTableAsList() {
		return new ArrayList<TableRow>(tableCopy);
	}
	
	public List<Column> getColumns() {
		List<Column> columns = new ArrayList<Column>();
		for (int i = 0; i < table.getColumnCount(); ++i) {
			if(table.getColumn(i) instanceof MagicTextColumn) {
				MagicTextColumn c = (MagicTextColumn) table.getColumn(i);
				//Window.alert(c.getColumnName());
				columns.add(c.getColumn());
			}
		}
		return columns;
	}
	
	public void setColumns(List<Column> cols) {
		this.clearAllColumns();
		for(Column col : cols) {
			addColumn(col);
		}
	}
	
	public void removeColumn(Column col) {
		for(int i = 0; i < this.table.getColumnCount(); i++) {
			if (this.table.getColumn(i) instanceof MagicTextColumn) {
				if (col == ((MagicTextColumn) this.table.getColumn(i))
						.getColumn()) {
					this.table.removeColumn(i);
					return;
				}
			}
		}
	}
	
	public void setQueryMode() {
		saveColumnModality();
		this.clearColumns();
		for(MagicTextColumn c : queryCols) {
			this.addColumn(c);
		}
		previousModality = FilterView.Modality.QUERY;
	}
	
	public void setExpertMode() {
		saveColumnModality();
		this.clearAllColumns();
		for(MagicTextColumn c : expertCols) {
			this.addColumn(c);
		}
		previousModality = FilterView.Modality.EXPERT;
	}
	
	public void setAggregateMode() {
		saveColumnModality();
		this.clearAllColumns();
		for(MagicTextColumn c : aggregateCols) {
			this.addColumn(c);
		}
		previousModality = FilterView.Modality.AGGREGATE;
	}
	
	//deprecated
	private void saveColumnModality() {
		if(previousModality.equals(FilterView.Modality.QUERY)) {
			this.queryCols.clear();
			for(int i = 0; i < this.table.getColumnCount(); i++) {
				queryCols.add((MagicTextColumn) this.table.getColumn(i));
			}
		} else if(previousModality.equals(FilterView.Modality.AGGREGATE)) {
			this.aggregateCols.clear();
			for(int i = 0; i < this.table.getColumnCount(); i++) {
				aggregateCols.add((MagicTextColumn) this.table.getColumn(i));
			}
		} else if(previousModality.equals(FilterView.Modality.EXPERT)) {
			this.expertCols.clear();
			for(int i = 0; i < this.table.getColumnCount(); i++) {
				expertCols.add((MagicTextColumn) this.table.getColumn(i));
			}
		}
	}

	@Override
	public void OnHeaderMenuSelectionEvent(HeaderMenuSelectionEvent e) {
		if (e.getSelectItem().equals("remove")) {
			final MagicTextColumn mag = e.getSourceColumn();
			final Column col = e.getSourceColumn().getColumn();
			if(col instanceof SynthTableColumn){
				rpcService.dropSynthTable((SynthTableColumn) col,false, new PresenterCallback<Void>(this) {

					@Override
					protected void success(Void result) {
						TablePresenter.this.table.removeColumn(mag);
					}
				
				});
			}else{
				if(!(e.getSourceColumn().getColumn().equals(this.pid) || e.getSourceColumn().getColumn().equals(this.definition))) {
					this.table.removeColumn(e.getSourceColumn());
					FilterPresenter.get().removeColumnFromCurrentDefinitions(e.getSourceColumn().getColumn());
				}
			}
		}
		else if (e.getSelectItem().equals("left"))
		{
			int c = this.table.getColumnIndex(e.getSourceColumn());
			if (c > 1)
			{
				this.table.removeColumn(e.getSourceColumn());
				this.table.insertColumn(c-1, e.getSourceColumn(), e.getClickableHeader());
			}
		}
		else if (e.getSelectItem().equals("right"))
		{
			int c = this.table.getColumnIndex(e.getSourceColumn());
			if (c < this.table.getColumnCount()-1)
			{
				this.table.removeColumn(e.getSourceColumn());
				this.table.insertColumn(c+1, e.getSourceColumn(), e.getClickableHeader());
			}
		}
		else if (e.getSelectItem().equals("constrain"))
		{
			if(e.getSourceColumn().getColumn() instanceof BooleanTableColumn
					|| e.getSourceColumn().getColumn() instanceof StringTableColumn) {
				FilterHeaderView fhv = new FilterHeaderView(FilterHeaderView.FilterType.STRORBOOL,
						e.getSourceColumn().getColumnName());
				FilterHeaderPresenter fhp = new FilterHeaderPresenter(parent, fhv, e.getSourceColumn().getColumn(), tableCopy);
				fhp.setActive();
			} else if (e.getSourceColumn().getColumn() instanceof FloatTableColumn) {
				FilterHeaderView fhv = new FilterHeaderView(FilterHeaderView.FilterType.FLOAT,
						e.getSourceColumn().getColumnName());
				FilterHeaderPresenter fhp = new FilterHeaderPresenter(parent, fhv, e.getSourceColumn().getColumn(), tableCopy);
				fhp.setActive();
			} else if (e.getSourceColumn().getColumn() instanceof IntegerTableColumn
					|| e.getSourceColumn().getColumn() instanceof DateTableColumn) {
				FilterHeaderView fhv = new FilterHeaderView(FilterHeaderView.FilterType.INTORDATE,
						e.getSourceColumn().getColumnName());
				FilterHeaderPresenter fhp = new FilterHeaderPresenter(parent, fhv, e.getSourceColumn().getColumn(), tableCopy);
				fhp.setActive();
			}
		}
		else if (e.getSelectItem().equals("ASC")){
			List<TableRow> newData = tableCopy;
			if(e.getSourceColumn().getColumn() instanceof IntegerTableColumn) {
				Collections.sort(newData, new IntegerComparator(e.getSourceColumn(), true));
			} else if(e.getSourceColumn().getColumn() instanceof FloatTableColumn) {
				Collections.sort(newData, new FloatComparator(e.getSourceColumn(), true));
			} else {
				Collections.sort(newData, new StringComparator(e.getSourceColumn(), true));
			}
			table.setRowData(newData);
			this.ui.setPagination();
		}
		else if (e.getSelectItem().equals("DESC")){
			List<TableRow> newData = tableCopy;
			if(e.getSourceColumn().getColumn() instanceof IntegerTableColumn) {
				Collections.sort(newData, new IntegerComparator(e.getSourceColumn(), false));
			} else if(e.getSourceColumn().getColumn() instanceof FloatTableColumn) {
				Collections.sort(newData, new FloatComparator(e.getSourceColumn(), false));
			} else {
				Collections.sort(newData, new StringComparator(e.getSourceColumn(), false));
			}
			table.setRowData(newData);
			this.ui.setPagination();
		}
		else if (e.getSelectItem().equals("AVG")) {
			double avg = 0;
			double sum = 0;
			int cnt = 0;
			for(TableRow tr : tableCopy) {
				try {
					String val = e.getSourceColumn().getValue(tr);
					if (val != null) {
				        sum += Double.parseDouble(val.replaceAll("\\*", ""));
						cnt++;
					}
				} catch (NumberFormatException exception) {
					exception.printStackTrace();
				}
			}
			avg = sum/((float)cnt);

			ui.setFunctionResult("Average of " + e.getSourceColumn().getColumnName() + " : " + getFormatted(avg, 3));
		}
		else if (e.getSelectItem().equals("COUNT")) {
			int cnt = 0;
			for(TableRow tr : tableCopy) {
				String val = e.getSourceColumn().getValue(tr);
				if((val!=null)&&(!val.equals(""))) {
					cnt++;
				}
			}
			
			ui.setFunctionResult("Count of " + e.getSourceColumn().getColumnName() + " : " + Double.toString(cnt));
		}
		else if (e.getSelectItem().equals("MAX")) {
			double currMax = 0;
			for(TableRow tr : tableCopy) {
				try {
					String val = e.getSourceColumn().getValue(tr);
					if ((val!=null) && (currMax < Double.parseDouble(val))) {
						currMax = Double.parseDouble(val);
					}
				} catch (NumberFormatException exception) {
					exception.printStackTrace();
				}
			}
			
			ui.setFunctionResult("Maximum of " + e.getSourceColumn().getColumnName() + " : " + getFormatted(currMax, 3));
		}
		else if (e.getSelectItem().equals("MIN")) {
			double currMin = Double.MAX_VALUE;
			for(TableRow tr : tableCopy) {
				try {
					String val = e.getSourceColumn().getValue(tr);
					if ((val != null) && (currMin > Double.parseDouble(val))) {
						currMin = Double.parseDouble(val);
					}
				} catch (NumberFormatException exception) {
					exception.printStackTrace();
				}
			}
			ui.setFunctionResult("Minimum of " + e.getSourceColumn().getColumnName() + " : " + getFormatted(currMin, 3));
		}
		else if (e.getSelectItem().equals("SUM")) {
			double sum = 0;
			for(TableRow tr : tableCopy) {
				try {
					String val = e.getSourceColumn().getValue(tr);
					if (val!=null)
						sum += Double.parseDouble(val);
				} catch (NumberFormatException exception) {
					exception.printStackTrace();
				}
			}
			ui.setFunctionResult("Sum of " + e.getSourceColumn().getColumnName() + " : " + getFormatted(sum, 3));
		}
		else if (e.getSelectItem().equals("UNIQ")) {
			double sum = 0;
			Set<String> vals = new HashSet<String>();
			for(TableRow tr : tableCopy) {
				vals.add(e.getSourceColumn().getValue(tr));
			}
			ui.setFunctionResult("Unique Elements in " + e.getSourceColumn().getColumnName() + " : " + vals.size());
		} else if (e.getSelectItem().equals("HIST")) {
			
			if(e.getSourceColumn().getColumn() instanceof IntegerTableColumn || e.getSourceColumn().getColumn() instanceof FloatTableColumn) {
				// Populate a D3 Histogram
				final Tab currentTab = addFreqTab(e.getSourceColumn().getColumnName());
				
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayInteger jsDim = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push(e.getSourceColumn().getColumnName());
				jsSize.push(900); //Width
				jsSize.push(400); //Height

				int min = 0;
				int max = 0;
				for(TableRow tr : tableCopy) {
					String s = e.getSourceColumn().getValue(tr);
					if (s!=null){
						if (Integer.parseInt(s) < min)
							min = Integer.parseInt(s);
						if (Integer.parseInt(s) > max)
							max = Integer.parseInt(s);
						jsVals.push(s);
					}
				}
				
				int tick = 10;
				jsDim.push(min - (min % tick)); //min
				jsDim.push((max + tick) - (max % tick)); //max
				jsDim.push(2); //sub
				jsDim.push(tick); //tick_interval
				
				((FreqTab)currentTab).populateStudiesAndShow(jsVals, jsSize, jsTitle, jsDim);
				
			} else {
				// Populate a D3 Barchart
				final Tab currentTab = addBarchartTab(e.getSourceColumn().getColumnName());
				
				HashMap<String, Integer> hist = new HashMap<String, Integer>();
				for(TableRow tr : tableCopy) {
					String s = e.getSourceColumn().getValue(tr);
					if (s!=null){
						if(hist.containsKey(s)) {
							Integer i = hist.get(s);
							i++;
							hist.put(s, i);
						} else {
							Integer i = new Integer(1);
							hist.put(s, i);
						}
					}
				}
				
				//for(String str : hist.keySet()) {
				//	Window.alert(str + ": " + hist.get(str));
				//}
				
				JsArrayString jsCats = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayString jsVals = (JsArrayString)JavaScriptObject.createArray().cast();
				JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
				JsArrayString jsTitle = (JsArrayString)JavaScriptObject.createArray().cast();
				jsTitle.push(e.getSourceColumn().getColumnName());
				jsSize.push(900); //Width
				jsSize.push(400); //Height
						
				for(String str : hist.keySet()) {
					jsCats.push(str);
					jsVals.push(hist.get(str).toString());
				}

				((BarchartTab)currentTab).populateStudiesAndShow(jsVals, jsCats, jsSize, jsTitle);

			}

			/*
						
			final Tab currentTab = addHistTab(e.getSourceColumn().getColumnName());
			((HistogramTab)currentTab).setSource(e.getSourceColumn());
			((HistogramTab)currentTab).getBinSize().addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					//((TextBox)event.getSource()).getParent();
					final Tab curTab = ui.getCurrentTab();
					Integer i = (int)Math.round(Double.parseDouble(((HistogramTab)currentTab).getBinSize().getText()));
					//System.out.println("Changed Textbox to " + ui.getBinSizeTB().getText());
					generateHist(i, false, curTab);
				}
			});
			
			if(e.getSourceColumn().getColumn() instanceof IntegerTableColumn || e.getSourceColumn().getColumn() instanceof FloatTableColumn) {
				//histCol = e.getSourceColumn();
				//((HistogramTab)currentTab).setSource(e.getSourceColumn());
				//TablePresenter.this.ui.clearHistogramSelector();
				generateHist(20, false, currentTab);
			} else {
				HashMap<String, Integer> hist = new HashMap<String, Integer>();
				for(TableRow tr : tableCopy) {
					String s = e.getSourceColumn().getValue(tr);
					if (s!=null){
						if(hist.containsKey(s)) {
							Integer i = hist.get(s);
							i++;
							hist.put(s, i);
						} else {
							Integer i = new Integer(1);
							hist.put(s, i);
						}
					}
				}
				final String name = e.getSourceColumn().getColumnName();
				rpcService.generateBarChart(hist, name, new PresenterCallback<String>(parent) {

					@Override
					protected void success(String result) {
						//TablePresenter.this.ui.setVisibleOuterStatsVP(false);
						//TablePresenter.this.ui.setBinSizeVisible(false);
						
						//((HistogramTab)currentTab).setURL(result);
						//((HistogramTab)currentTab).setBarChart();
						//Window.alert("BAR CHART RESULT: " + result);
						
						String url = GWT.getModuleBaseURL() + "ImageServlet?fileInfo1=" + result;
						//Window.alert("BAR CHART RESULT: " + url);
						((HistogramTab)currentTab).setURL(url);
						((HistogramTab)currentTab).setBarChart();
						
						
						//TablePresenter.this.getTab().setURL(result);
						//TablePresenter.this.ui.setBarChart();
					}
				});
			}*/
		} else if (e.getSelectItem().equals("DUPE")) {
			HashMap<String, Boolean> duplicates = new HashMap<String, Boolean>();
			/* find duplicates and then highlight them
			 * can't highlight as we find them because
			 * we miss the first two
			 */
			for(TableRow tr : tableCopy) {
				String val = e.getSourceColumn().getValue(tr);
				if (val!=null) {
					if( duplicates.containsKey(val) ) {
						duplicates.put(val, new Boolean(true));
					} else {
						duplicates.put(val, new Boolean(false));
					}
				}
			}
			String rowKey = e.getSourceColumn().getKey();
			for(TableRow tr : tableCopy) {
				String val = e.getSourceColumn().getValue(tr);
				if (val!=null){
					if( duplicates.get(val) )
						tr.setDuplicate(rowKey);
				}
			}
			table.redraw();
			
		} else if (e.getSelectItem().equals("export_module")) {
			//System.err.println("EXPORT D3 EVENT IN TABLE PRESENTER");
			this.export_module(e.getChart());
		} else {
			return;
		}
	}
	
	private native void export_module(JavaScriptObject chart)/*-{
		chart.export_module();
	}-*/;
	
	public void sortButtonCol(boolean isAscending) {
		Collections.sort(tableCopy, new ButtonComparator(isAscending));
		table.setRowData(tableCopy);
		this.ui.setPagination();
	}

	public void generateHist(int binSize, boolean checkCBs, final Tab currentTab) {
		MagicTextColumn histCol = ((HistogramTab)currentTab).getSource();
		
		if(checkCBs && ui.getCheckedCBs().size() == 0) {
			return;
		}

		LinkedHashMap<String,ArrayList<Double>> columnValues = new LinkedHashMap<String, ArrayList<Double>>();
		for(TableRow tr : tableCopy) {
			if(histCol.getValue(tr) == null || histCol.getValue(tr).equals(""))
				continue;
			if(!columnValues.containsKey(tr.getRowData("definitionIdentifier"))) {
				if(checkCBs && !ui.getCheckedCBs().contains(tr.getRowData("definitionIdentifier")))
					continue;
				ArrayList<Double> alist = new ArrayList<Double>();
				columnValues.put(tr.getRowData("definitionIdentifier"), alist);
			}
			columnValues.get(tr.getRowData("definitionIdentifier")).add(Double.parseDouble(histCol.getValue(tr)));
		}
		TablePresenter.this.ui.setStatisticLabel("Histogram Statistics");
		HashMap<String, Double> hmMean = new HashMap<String, Double>();
		HashMap<String, Double> hmStdDev = new HashMap<String, Double>();
		for (Entry<String, ArrayList<Double>> entry : columnValues.entrySet()) {
			double[] values = new double[entry.getValue().size()];
			double sum = 0, variance = 0, mean = 0, sumSq = 0, stdDev = 0;
			for (int i = 0; i < entry.getValue().size(); i++) {
				values[i] = entry.getValue().get(i);
				sum += entry.getValue().get(i);
			}
			mean = sum / (values.length * 1.0f);
			for (int i = 0; i < values.length; i++) {
				sumSq += (mean - values[i]) * (mean - values[i]);
			}
			variance = sumSq / (values.length * 1.0f);
			stdDev = Math.sqrt(variance);
			hmMean.put(entry.getKey(), new Double(mean));
			hmStdDev.put(entry.getKey(), new Double(stdDev));
			((HistogramTab)currentTab).getPStats().clear();
			((HistogramTab)currentTab).getPStats().add(new Label(entry.getKey() + " Mean : " + NumberFormat.getFormat("#.0000").format(mean)));
			((HistogramTab)currentTab).getPStats().add(new Label(entry.getKey() + " Standard Deviation : " + NumberFormat.getFormat("#.0000").format(stdDev)));
			//TablePresenter.this.ui.getStatisticVP().add(
					//new Label(entry.getKey() + " Mean : " + NumberFormat.getFormat("#.0000").format(mean)));
			//TablePresenter.this.ui.getStatisticVP().add(
					//new Label(entry.getKey() + " Standard Deviation : " + NumberFormat.getFormat("#.0000").format(stdDev)));
			/*if (!checkCBs) {
				TablePresenter.this.ui.addHistogramSelectorElement(entry.getKey()).addValueChangeHandler(
						new ValueChangeHandler<Boolean>() {

							@Override
							public void onValueChange(
									ValueChangeEvent<Boolean> event) {
								Integer i = (int) Math.round(Double.parseDouble(ui.getBinSizeTB().getText()));
								//ui.getStatisticVP().clear();
								TablePresenter.get().generateHist(i, true);
							}
						});
			}*/
		}
		final String name = histCol.getColumnName();
		rpcService.generateHistogram(columnValues, name, binSize, hmMean, hmStdDev, new PresenterCallback<String>(parent) {

			@Override
			protected void success(String result) {
				//Window.alert("HIST RESULT: " + result);
				//TablePresenter.this.getTab().setURL(result);
				//((HistogramTab)currentTab).setURL(result);
				
				String url = GWT.getModuleBaseURL() + "ImageServlet?fileInfo1=" + result;
				//Window.alert("HIST URL RESULT: " + url);
				((HistogramTab)currentTab).setURL(url);
				
				
				//TablePresenter.this.ui.setVisibleOuterStatsVP(true);
				//TablePresenter.this.ui.setBinSizeVisible(true);
				/*TablePresenter.this.ui.getBinSizeTB().addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						Integer i = (int)Math.round(Double.parseDouble(ui.getBinSizeTB().getText()));
						System.out.println("Changed Textbox to " + ui.getBinSizeTB().getText());
						generateHist(i, false);
					}
				});*/
			}
		});
	}
	public static String getFormatted(double value, int decimalCount) {
	    StringBuilder numberPattern = new StringBuilder(
	            (decimalCount <= 0) ? "" : ".");
	    for (int i = 0; i < decimalCount; i++) {
	        numberPattern.append('0');
	    }
	    return NumberFormat.getFormat(numberPattern.toString()).format(value);
	}
	
	private void setSortOrder(String string) {
		this.sortOrder=string;
	}
	public String getSortOrder(){
		return this.sortOrder;
	}

	public void setSortColumn(MagicTextColumn sourceColumn) {
		this.sortColumn = sourceColumn;
	}
	public Column getSortColumn(){
		
		if(this.sortColumn==null || !this.getColumns().contains(this.sortColumn.getColumn())){
			return this.pid;
		}
		return this.sortColumn.getColumn();
	}
	public void clearColumns() {
		while(table.getColumnCount()>0) {
			table.removeColumn(0);
		}
		this.addDefaultColumns();
	}
	public void clearAllColumns() {
		while(table.getColumnCount()>0) {
			table.removeColumn(0);
		}
	}
	public void clearAllModalityColumns() {
		aggregateCols.clear();
		expertCols.clear();
		queryCols.clear();
		this.clearAllColumns();
	}
	public void clearSentinalColumns() {
		for(Column col : this.getColumns()) {
			if(col instanceof FunctionTableColumn) {
				this.removeColumn(col);
			}
		}
	}
	/*
	public void clearImageColumns() {
		for (int i = 0; i < table.getColumnCount(); i++) {
			if(((MagicTextColumn)table.getColumn(i)).getColumnName().startsWith("Jaccard") 
					|| ((MagicTextColumn)table.getColumn(i)).getColumnName().startsWith("Study Date") )
				table.removeColumn(i);
		}
		j_drawn_cnt = 0;
		j_patient_cnt = 0;
		for(Column col : image_columns) {
			for (int i = 1; i < table.getColumnCount(); i++) {
				MagicTextColumn c = (MagicTextColumn) table.getColumn(i);
				if (c.getColumn().equals(col) || c.getColumn().equals(sdate_column))
					table.removeColumn(i);
			}
		}
		study_date = false;
		image_filters.clear();
	}*/
	
		public void handleSimilarity(ImageQueryFilter filter) {
		if(image_filters.contains(filter)) {
			return;
		}
		image_filters.add(filter);
		sim_cnt++;
		filter.setTableAlias(new String("simquery_" + sim_cnt));
		Table stable = new Table("simquery_" + sim_cnt);
		TableColumn scolumn = null;
		if(sim_cnt > 1) {
			scolumn = new FloatTableColumn("Similarity Query " + sim_cnt, "image_score", "", stable);
		} else {
			scolumn = new FloatTableColumn("Similarity Query", "image_score", "", stable );
		}
		this.addColumn(scolumn);
		if(!study_date) {
			Table studyTable = new Table("btaw.btap_study");
			sdate_column = new DateTableColumn("Study Date", "study_date", "", studyTable);
			this.addColumn(sdate_column);
			study_date = true;
		}
		image_columns.add(scolumn);
	}
	
	public void handleRatios(RegionContrastFilter filter) {
		if(ratio_filters.contains(filter)) {
			return;
		}
		Table rtable = new Table("SKIPME");
		TableColumn rcolumn = new FloatTableColumn("Ratio Score", "ratio_score", "", rtable);
		this.addColumn(rcolumn);
		if(!study_date) {
			Table studyTable = new Table("btaw.btap_study");
			sdate_column = new DateTableColumn("Study Date", "study_date", "", studyTable);
			this.addColumn(sdate_column);
			study_date = true;
		}
	}
	
	public void setEdited() {
		this.ui.setEdited();
		FilterPresenter.get().setResultsCurrent(false);
	}
	
	public void setCurrent() {
		this.ui.setCurrent();
		FilterPresenter.get().setResultsCurrent(true);
	}
	
	public void doKaplanMeier(FunctionData fd) {
		final String kmName = fd.getFuncName();
		
		ArrayList<String> kmNames = new ArrayList<String>();
		ArrayList<ArrayList<String>> kmPids = new ArrayList<ArrayList<String>>();
		
		ArrayList<DefinitionData> dData = fd.getDefData();
		
		for(DefinitionData d : dData) {
			kmNames.add(d.getDefName());
			ArrayList<String> currPids = new ArrayList<String>();
			for(TableRow tr : d.getTableData()) {
				currPids.add(tr.getRowData("pid").replaceAll("\\*", ""));
			}
			kmPids.add(currPids);
		}
		/*rpcService.generateKMChart(kmPids, kmNames, new PresenterCallback<String>(TablePresenter.this) {

			@Override
			protected void success(String result) {
				System.err.println("SUCCESS : " + result);
				ui.setKMChart(result, kmName);
				ui.setVisibleOuterStatsVP(true);
				ui.setBinSizeVisible(false);
				ui.setStatisticLabel("Logrank Statistic");
			}
		});*/
	}
	
	public Tab addPsaAggregateTab(String name, final ArrayList<String> pids, final String sel_pid) {

		PushButton close_button = ui.addPsaAggregateTab(name);
		final PsaAggregateTab agg_tab = ((PsaAggregateTab)ui.getTab());
		
		close_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				agg_tab.unregisterDropControllers();
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});
		
		agg_tab.getCenterDropController().addDropHandler(new DropEventHandler() {
			@Override
			public void OnPreviewDrop(PreviewDropEvent e) {
				if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
					return;
				}
				ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
				if(l.getColumn() instanceof DateTableColumn){
					agg_tab.setCenterColumn(l.getColumn());
				}
			}
		});
		
		agg_tab.getRecomputeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rpcService.getPsaAggregateValues(pids, agg_tab.getCenterColumn(), new PresenterCallback<ArrayList<PSA_Graph>>(TablePresenter.this) {

					@Override
					protected void success(ArrayList<PSA_Graph> result) {

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
						
						agg_tab.populateStudiesAndShow(jsPIDs, jsPsaLists, jsDateLists, jsImplantDates, jsSelPID, jsSize);
						
					}
				});

			}
		});

		return ui.getTab();
	}
	
	public Tab addKMTab(String name, final ArrayList<ArrayList<String>> pids, final ArrayList<String> kmNames) {
		
		PushButton close_button = ui.addKMTab(name);
		final KaplanMeierTab kmtab = ((KaplanMeierTab)ui.getTab());
		
		close_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				kmtab.unregisterDropControllers();
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});

		kmtab.getStartDropController().addDropHandler(new DropEventHandler() {
			@Override
			public void OnPreviewDrop(PreviewDropEvent e) {
				if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
					return;
				}
				ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
				if(l.getColumn() instanceof DateTableColumn){
					kmtab.setStartColumn(l.getColumn());
				}
			}
			});
		kmtab.getEndDropController().addDropHandler(new DropEventHandler() {
			@Override
			public void OnPreviewDrop(PreviewDropEvent e) {
				if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
					return;
				}
				ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
				if(l.getColumn() instanceof DateTableColumn){
					kmtab.setEndColumn(l.getColumn());
				}
			}
			});
		kmtab.getCensorDropController().addDropHandler(new DropEventHandler() {
			@Override
			public void OnPreviewDrop(PreviewDropEvent e) {
				if(e.GetContext().selectedWidgets.get(0) instanceof SavedQueryLabel) {
					return;
				}
				ColumnLabel l = (ColumnLabel)e.GetContext().selectedWidgets.get(0);
				if(l.getColumn() instanceof DateTableColumn){
					kmtab.setCensorColumn(l.getColumn());
				}
			}
			});
		
		kmtab.getRecomputeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rpcService.generateKMChart(pids, kmNames, kmtab.getStartColumn(),kmtab.getEndColumn(),kmtab.getCensorColumn(), new PresenterCallback<ArrayList<KM_Graph>>(TablePresenter.this) {
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

						kmtab.populateStudiesAndShow(jsLineXLists, jsLineYLists, jsCensXLists, jsCensYLists, jsAtRiskList, jsSize, kmtab.getNames(), jsN);
					}
				});

				kmtab.getPStats().clear();
				for(int i = 0; i < kmNames.size(); i++) {
					for(int j = i + 1; j < kmNames.size(); j++) {
						if(i != j) {
							final String s = kmNames.get(i) + " vs. " + kmNames.get(j);
							ArrayList<ArrayList<String>> currPairPids = new ArrayList<ArrayList<String>>();
							currPairPids.add(pids.get(i));
							currPairPids.add(pids.get(j));
							rpcService.getChiSqPVal(currPairPids,kmtab.getStartColumn(),kmtab.getEndColumn(),kmtab.getCensorColumn(), new PresenterCallback<String>(TablePresenter.this) {

								@Override
								protected void success(String result) {
									//TablePresenter.get().getStatisticVP().add(new Label(s + " : " + result));
									//((KaplanMeierTab)TablePresenter.get().getTab()).getPStats().add(new Label(s + " : " + result));
									kmtab.getPStats().add(new Label(s + " : " + result));
								}
							});
						}
					}
				}
				
				
			}
		});
		
		return ui.getTab();
	}
	
	public Tab addFreqTab(String name) {
		ui.addFreqTab(headerController,name).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});
		
		return ui.getTab();
	}
	
	public Tab addBarchartTab(String name) {
		ui.addBarchartTab(headerController,name).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});
		
		return ui.getTab();
	}
	
	public Tab addHistTab(String name) {
		ui.addHistTab(name).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});
		
		return ui.getTab();
	}
	
	public Tab addPatientSummaryTab(String name) {
		ui.addPatientSummaryTab(name).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});
		
		return ui.getTab();
	}
	
	public Tab addDashboardTab(String name) {
		ui.addDashboardTab(headerController,name).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.closeTab(((TabCloseButton)event.getSource()).getRef());
			}
		});
		
		return ui.getTab();
	}
	
	public Tab getTab(){
		return ui.getTab();
	}
	
	public String getSelectedPID(){
		TableRow tr = this.ui.getSmodel().getSelectedObject();
		if (tr!=null)
			return tr.getRowData("pid").replaceAll("\\*", "");
		else
			return "-1";
	}
	
	public VerticalPanel getStatisticVP() {
		return ui.getStatisticVP();
	}
	public void clearFunctionResults() {
		ui.setFunctionResultOff();
	}
	public void setVisibleStatisticsPanel(boolean bool) {
		ui.setVisibleOuterStatsVP(bool);
	}
	
	public void setBinSizeVisible(boolean bool) {
		ui.setBinSizeVisible(bool);
	}
	
	public void setStatisticsLabel(String str) {
		ui.setStatisticLabel(str);
	}
	
	private class PID_ButtonCell extends ClickableTextCell {
		public PID_ButtonCell(){ 
            super();
        }
		@Override 
		public void render(final Context context, final SafeHtml data, final SafeHtmlBuilder sb) { 
		    	sb.appendHtmlConstant("<div class='pidColumnStyle'>");
		        super.render(context, data, sb);
		        sb.appendHtmlConstant("</div>");
		        
		}
	}
	private class DisplayScansButtonCell extends ButtonCell 
	    { 
	        /** 
	         * Constructor. 
	         */ 
	        public DisplayScansButtonCell() 
	        { 
	            super(); 

	        } 
	        @Override 
	        public void render(final Context context, final SafeHtml data, final SafeHtmlBuilder sb) 
	        { 
	        	String s = "<button type=\"button\" style=\"font: bold 7px Arial\"";
            	s += "class=\"handPointer\">DISPLAY";
	        	//String s = "<button type=\"button\" tabindex=\"-1\"";
            	//s += "class=\"handPointer\">";
	            if (data.asString().equals("No Scans Found")) {
	            } else {
		            sb.appendHtmlConstant(s);
	            }
	            
	            if(data.asString().equals("No Scans Found")) {
	            } else {
	            	//sb.appendHtmlConstant("<img src=\"images/brainthumb.png\"/ height=\"1\" width=\"1\">");
		            sb.appendHtmlConstant("</button>"); 
	            }
	            /*
	            if (data != null) 
	            { 
	                sb.append(data); 
	            } */
	        }

	    }
}