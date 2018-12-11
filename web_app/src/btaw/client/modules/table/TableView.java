package btaw.client.modules.table;


import java.awt.Color;
import java.util.ArrayList;

import btaw.client.event.AddColumnEvent;
import btaw.client.event.PreviewDropEvent;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.event.controllers.PreviewDropEventController;
import btaw.client.event.handlers.DropEventHandler;
import btaw.client.event.handlers.HasDropHandlers;
import btaw.client.framework.AView;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.filter.FilterView;
import btaw.client.modules.tab.BarchartTab;
import btaw.client.modules.tab.DashboardTab;
import btaw.client.modules.tab.FreqTab;
import btaw.client.modules.tab.HistogramTab;
import btaw.client.modules.tab.KaplanMeierTab;
import btaw.client.modules.tab.PatientSummaryTab;
import btaw.client.modules.tab.PsaAggregateTab;
import btaw.client.modules.tab.Tab;
import btaw.client.view.widgets.ColumnLabel;
import btaw.client.view.widgets.MagicTextColumn;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.client.view.widgets.TabCloseButton;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.column.DateTableColumn;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class TableView extends AView implements TablePresenter.View{
	CellTable<TableRow> table;
	ArrayList<Tab> Tabs;
	SimplePager	pager;
	private int pageSize = 20;
	private static final int HeaderRowIndex = 0;
	private final PreviewDropEventController dropController;
	SingleSelectionModel<TableRow> smodel = new SingleSelectionModel<TableRow>();
	private Label warningLabel = new Label("Warning: The table below is from a previous definition, please click 'List' to update the table.");
	private ScrollPanel main;
	private HorizontalPanel warningPanel;
	private FlowPanel labelPanel;
	private boolean edited = false;
	private boolean red = true;
	//private VerticalPanel statisticsVP;
	private VerticalPanel outerAggregateVP;
	private HorizontalPanel tableHP;
	//private HorizontalPanel binSizeHP;
	//private TextBox binSize;
	private TextBox pageSizeTB;
	private VerticalPanel histogramSelectorVP;
	private CustomTabLayoutPanel mainTabbedPanel;
	private PushButton maximize;
	private PushButton minimize;
	private FlowPanel outerPanel;
	private VerticalPanel aggregateVP;
	private final PickupDragController dragController;
	
	public TableView(PickupDragController dragController){
		
		this.dragController = dragController;
		Tabs = new ArrayList<Tab>();
		
		Image max_icon = new Image("images/max.png");
		max_icon.setPixelSize(9, 11);
		max_icon.getElement().getStyle().setPaddingLeft(2, Unit.PX);
		maximize = new PushButton(max_icon);
		maximize.setStyleName("gwt-TabButton");
		maximize.setTitle("Maximize");
		
		Image min_icon = new Image("images/min.png");
		min_icon.setPixelSize(9, 11);
		min_icon.getElement().getStyle().setPaddingRight(2, Unit.PX);
		minimize = new PushButton(min_icon);
		minimize.setStyleName("gwt-TabButton");
		minimize.setTitle("Minimize");
		
		outerPanel = new FlowPanel();
		mainTabbedPanel = new CustomTabLayoutPanel(1.8, Unit.EM, minimize, maximize);
		labelPanel = new FlowPanel();
		warningPanel = new HorizontalPanel();
		labelPanel.getElement().getStyle().setBackgroundColor("#EAEBF4");
		labelPanel.getElement().addClassName("shadow");
		labelPanel.setWidth("100%");
		labelPanel.setHeight("2.4em");
		warningPanel.getElement().getStyle().setBackgroundColor("#EAEBF4");
		warningPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		warningPanel.setWidth("100%");
		warningPanel.add(warningLabel);
		outerPanel.add(labelPanel);
		outerPanel.add(warningPanel);
		warningPanel.setVisible(false);
		warningLabel.getElement().getStyle().setPadding(2, Unit.PX);
		warningLabel.getElement().getStyle().setColor("red");
		warningLabel.getElement().setId("countLabel");
		main = new ScrollPanel();
		outerPanel.add(main);
		main.setSize("100%","95%");
		mainTabbedPanel.add(outerPanel,"All");
		
		mainTabbedPanel.selectTab(0);
		mainTabbedPanel.setSize("100%","100%");
		main.getElement().setId("TablePanel");
		table = new CellTable<TableRow>();
		
		pageSizeTB = new TextBox();
		pageSizeTB.setText(String.valueOf(pageSize));
		pageSizeTB.setWidth("30px");
		pageSizeTB.setHeight("10px");
		Label pageSizeL = new Label("Rows per page: ");
		pageSizeL.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		//pageSizeL.getElement().getStyle().setPaddingTop(10, Unit.PX);
		pageSizeL.getElement().getStyle().setPaddingRight(4, Unit.PX);
		pageSizeL.setHeight("10px");
		labelPanel.add(pageSizeL);
		labelPanel.add(pageSizeTB);
		
		pager = new MySimplePager();
	    pager.setDisplay(table);
	    pager.setHeight("10px");
	    pager.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	    labelPanel.add(pager);
	    labelPanel.setVisible(false);
		
		tableHP = new HorizontalPanel();
		tableHP.setSpacing(10);
		tableHP.add(table);
		aggregateVP = new VerticalPanel();
		outerAggregateVP = new VerticalPanel();
		Label aggregateL = new Label("Functions:");
		aggregateL.setStyleName("titleLabel");
		outerAggregateVP.add(aggregateL);
		outerAggregateVP.add(aggregateVP);
		tableHP.add(outerAggregateVP);
		this.outerAggregateVP.setVisible(false);
		
		main.add(tableHP);
	    
		this.dropController = new PreviewDropEventController(main);
		this.initWidget(mainTabbedPanel);
		this.dragController.registerDropController(dropController);
		table.setRowData(new ArrayList<TableRow>());
		table.setSelectionModel(smodel);
		
	}
	
	public void setPagination(){
	    pager.setDisplay(table);
	    pager.setPageSize(pageSize);
	    if(pager.getPageCount()==0)
	    	labelPanel.setVisible(false);
	    else
	    	labelPanel.setVisible(true);
	}
	
	public void setKMName(String name) {
		//countLabel.setText(name);
	}
	
	public void setPageSize(int i){
		this.pageSize = i;
	}
	
	public void setBinSizeVisible(boolean bool) {
		//this.binSizeHP.setVisible(bool);
		//this.setHistogramSelectorVisible(bool);
	}
	
	public void setHistogramSelectorVisible(boolean bool) {
		//this.histogramSelectorVP.setVisible(bool);
		//if(!bool)
		//	this.histogramSelectorVP.clear();
	}
	
	public void clearHistogramSelector() {
		//this.histogramSelectorVP.clear();
	}
	
	public CheckBox addHistogramSelectorElement(String name) {
		/*HorizontalPanel currHP = new HorizontalPanel();
		CheckBox currCB = new CheckBox();
		currCB.setValue(true);
		Label currL = new Label(name);
		currHP.add(currCB);
		currHP.add(currL);
		this.histogramSelectorVP.add(currHP);
		return currCB;*/
		return null;
	}
	
	public ArrayList<String> getCheckedCBs() {
		/*ArrayList<String> alist = new ArrayList<String>();
		for(int i = 0; i < this.histogramSelectorVP.getWidgetCount(); i++) {
			HorizontalPanel currHP = (HorizontalPanel)this.histogramSelectorVP.getWidget(i);
			CheckBox currCB = (CheckBox)currHP.getWidget(0);
			Label currL = (Label)currHP.getWidget(1);
			if(currCB.getValue())
				alist.add(currL.getText());
		}
		return alist;*/
		return null;
	}
	
	public TextBox getBinSizeTB() {
		//return this.binSize;
		return ((HistogramTab)Tabs.get(Tabs.size()-1)).getBinSize();
	}
	
	public TextBox getPageSizeTB() {
		return this.pageSizeTB;
	}
	
	public TabCloseButton addPsaAggregateTab(String name) {
		
		PsaAggregateTab PAT = new PsaAggregateTab(this.dragController);
		Tabs.add(PAT);
		
		HorizontalPanel PAT_HP = PAT.getHPanel();
		PAT_HP.setSize("100%","100%");
		//PST_HP.setSpacing(10);
		
		Label tabTitle = new Label("Agg ");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,PAT);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);

		mainTabbedPanel.add(PAT_HP,tabView);
		mainTabbedPanel.selectTab(PAT_HP);
		
		return closeButton;
		
	}
	
	public TabCloseButton addKMTab(String name) {
		
		KaplanMeierTab KM = new KaplanMeierTab(this.dragController);
		Tabs.add(KM);
		
		HorizontalPanel KM_HP = KM.getHPanel();
		KM_HP.setSize("100%","100%");
		
		Label tabTitle = new Label("KM ");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,KM);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);

		mainTabbedPanel.add(KM_HP,tabView);
		mainTabbedPanel.selectTab(KM_HP);
		
		return closeButton;
		
		
		/*KaplanMeierTab KMT = new KaplanMeierTab();
		Tabs.add(KMT);

		Image KMChart = KMT.getIm();
		HorizontalPanel kmHP = KMT.getHPanel();
		kmHP.setSize("100%","100%");
		kmHP.setSpacing(10);
		VerticalPanel PStats = KMT.getPStats();
		VerticalPanel outerStatsVP = new VerticalPanel();
		Label statisticsL = new Label("Logrank Statistic:");
		statisticsL.setStyleName("titleLabel");
		outerStatsVP.setSpacing(10);
		outerStatsVP.add(statisticsL);
		outerStatsVP.add(PStats);
		kmHP.add(KMChart);
		kmHP.add(outerStatsVP);
		
		Label tabTitle = new Label("KM ");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,KMT);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);
		
		mainTabbedPanel.add(kmHP,tabView);
		mainTabbedPanel.selectTab(kmHP);
		
		return closeButton;*/
	}
	
	public TabCloseButton addFreqTab(HeaderMenuSelectionController controller, String name) {
		
		FreqTab PAT = new FreqTab(controller);
		Tabs.add(PAT);
		
		HorizontalPanel PAT_HP = PAT.getHPanel();
		PAT_HP.setSize("100%","100%");
		
		Label tabTitle = new Label("Hist ");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,PAT);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);

		mainTabbedPanel.add(PAT_HP,tabView);
		mainTabbedPanel.selectTab(PAT_HP);
		
		return closeButton;
		
	}
	
	public TabCloseButton addBarchartTab(HeaderMenuSelectionController controller, String name) {
		
		BarchartTab PAT = new BarchartTab(controller);
		Tabs.add(PAT);
		
		HorizontalPanel PAT_HP = PAT.getHPanel();
		PAT_HP.setSize("100%","100%");
		
		Label tabTitle = new Label("Hist ");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,PAT);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);

		mainTabbedPanel.add(PAT_HP,tabView);
		mainTabbedPanel.selectTab(PAT_HP);
		
		return closeButton;
		
	}
	
	public TabCloseButton addHistTab(String name){
		
		HistogramTab HT = new HistogramTab();
		Tabs.add(HT);
		
		HorizontalPanel binSizeHP = new HorizontalPanel();
		Label binL = new Label("Set Num Bins: ");
		binL.setStyleName("titleLabel");
		binSizeHP.add(binL);
		TextBox binSize = HT.getBinSize();
		binSize.setText("20");
		binSizeHP.add(binSize);
		
		Image Hist = HT.getIm();
		HorizontalPanel kmHP = HT.getHPanel();
		VerticalPanel PStats = HT.getPStats();
		kmHP.setSize("100%","100%");
		kmHP.setSpacing(10);
		VerticalPanel outerStatsVP = HT.getOuterStats();
		outerStatsVP.setSpacing(10);
		outerStatsVP.add(binSizeHP);
		outerStatsVP.add(PStats);
		kmHP.add(Hist);
		kmHP.add(outerStatsVP);
		
		Label tabTitle = new Label("Hist ");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,HT);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);

		mainTabbedPanel.add(kmHP,tabView);
		mainTabbedPanel.selectTab(kmHP);
		
		return closeButton;
	}
	
	public TabCloseButton addPatientSummaryTab(String name){
		
		// Find the max ID of any PS tab and increment it.
		int max = -1;
		int cur = 0;
		for (Tab t : Tabs){
			if (t instanceof PatientSummaryTab){
				cur = ((PatientSummaryTab)t).getNumber();
				if (cur>max){
					max = cur;
				}
			}
		}
		
		//PatientSummaryTab PST = new PatientSummaryTab(name, mainTabbedPanel.getSelectedIndex());
		PatientSummaryTab PST = new PatientSummaryTab(name, max+1);
		Tabs.add(PST);
		
		HorizontalPanel PST_HP = PST.getHPanel();
		PST_HP.setSize("100%","100%");
		
		Label tabTitle = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,PST);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(closeButton);

		mainTabbedPanel.add(PST_HP,tabView);
		mainTabbedPanel.selectTab(PST_HP);
		
		return closeButton;
	}
	
	public TabCloseButton addDashboardTab(HeaderMenuSelectionController controller, String name){
		
		if(name.replaceAll("\\s","").equals(""))
			name = "All";
		
		DashboardTab DT = new DashboardTab(controller);
		Tabs.add(DT);
		
		HorizontalPanel DT_HP = DT.getHPanel();
		DT_HP.setSize("100%","100%");
		
		Label tabTitle = new Label("Dashboard");
		Label tabSub = new Label(name);
		Image im = new Image("images/close.png");
		im.setPixelSize(8, 8);
		TabCloseButton closeButton = new TabCloseButton(im,DT);
		tabTitle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabTitle.getElement().getStyle().setMarginRight(4, Unit.PX);

		tabSub.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tabSub.getElement().getStyle().setMarginRight(0.6, Unit.EM);
		tabSub.getElement().getStyle().setFontSize(7,Unit.PT);
		tabSub.getElement().getStyle().setColor("#484848");
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(tabTitle);
		tabView.add(tabSub);
		tabView.add(closeButton);

		mainTabbedPanel.add(DT_HP,tabView);
		mainTabbedPanel.selectTab(DT_HP);
		
		return closeButton;
	}
	
	public Tab getCurrentTab() {
		return Tabs.get(mainTabbedPanel.getSelectedIndex()-1);
	}
	
	public Tab getTab() {
		return Tabs.get(Tabs.size()-1);
	}
	
	public VerticalPanel getStatisticVP() {
		return ((KaplanMeierTab)Tabs.get(Tabs.size()-1)).getPStats();
	}
	
	public void selectTableTab(){
		mainTabbedPanel.selectTab(outerPanel);
	}
	
	public void closeTab(Tab T){
		Tabs.remove(T);
		mainTabbedPanel.remove(T.getHPanel());
	}
	
	public void setKMChart(String url, String name) {
		main.remove(tableHP);
		//main.remove(kmHP);
		//main.add(kmHP);
		//KMChart.setUrl(url);
		this.setKMName(name);
	}
	
	public void setVisibleOuterStatsVP(boolean bool) {
		//this.outerStatsVP.setVisible(bool);
	}
	
	public void setStatisticLabel(String s) {
		//this.statisticsL.setText(s);
	}
	
	public void setTable() {
		main.remove(tableHP);
		//main.remove(kmHP);
		main.add(tableHP);
	}
	
	public void setEdited() {
		edited = true;
		//FilterPresenter.get().setExecuteRed();
		//warningPanel.setVisible(true);
	}
	
	public void setCurrent() {
		edited = false;
		//FilterPresenter.get().clearExecuteRed();
		//warningPanel.setVisible(false);
	}
	
	@Override
	public CellTable<TableRow> getTable() {
		return table;
	}
	@Override
	public HasDropHandlers getDropController() {
		return dropController;
	}
	public SingleSelectionModel<TableRow> getSmodel() {
		return this.smodel;
	}
	
	public void setFunctionResult(String str) {
		this.outerAggregateVP.setVisible(true);
		this.aggregateVP.add(new Label(str));
	}
	public void setFunctionResultOff() {
		this.outerAggregateVP.setVisible(false);
		this.aggregateVP.clear();
	}
	
	public PushButton getMaximizeButton() {
		return this.maximize;
	}
	
	public PushButton getMinimizeButton() {
		return this.minimize;
	}

	public void setBarChart() {
		//((HistogramTab)Tabs.get(Tabs.size()-1)).setBarChart();
	}
	
	public ArrayList<Tab> getAllTabs(){
		return Tabs;
	}
	
	public PickupDragController getDragController() {
		return this.dragController;
	}
	
}