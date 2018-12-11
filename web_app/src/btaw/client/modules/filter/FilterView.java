package btaw.client.modules.filter;

import java.util.ArrayList;
import java.util.List;

import btaw.client.event.controllers.PreviewDropEventController;
import btaw.client.framework.AView;
import btaw.client.view.filter.FilterPanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FilterView extends AView implements FilterPresenter.View{
	public enum Modality { QUERY, AGGREGATE, EXPERT, KAPLAN };
	
	private final Button synth;
	private final Button logout;
	private final Button changePassword;
	private final Button imageQuery;
	private final FlexTable mainPanel;
	private final FlexTable aggregateFunctionPanel;
	private final FlexTable groupByPanel;
	private final FlexTable aggregateFilterPanel;
	private final FlexTable definitionsFT;
	private final FlexTable defineFT;
	private final FlexTable functionsFT;
	private final PickupDragController dragController;
	private final PreviewDropEventController allFiltersDropController;
	private final PreviewDropEventController kmFilterByDropController;
	private final Button apply;
	private final Button clear;
	//private final Button saveQuery;
	private final Button exportDataButton;
	//private final Button saveQueryButton;
	private final Button deleteQueryButton;
	private final Button kaplanMeierButton;
	private final Button PSA_aggregate;
	private final Button Interval_Query;
	private final Button Export;
	private final Button Summarize;
	private final Button Dashboard;
	private final Button addToKaplanMeier;
	private final Button synonymButton;
	private final Button addDefinition;
	private final Button changeDef;
	private final Button addFunction;
	private final Button clearDefinition;
	private final TabLayoutPanel modality;
	private final TabLayoutPanel subgroupResults;
	private final TextArea expertInput;
	private final Image KMChart;
	private final TextBox defName;
	private final TextBox SummarizeTextBox;
	private MenuItem executeQuery;
	private Modality currentMode = Modality.KAPLAN;
	
	private final MenuBar menu;
	private final MenuItem dsetMenu2;
	private final MenuItem datacut;
//	private final MenuBar datasetSelector;
	private boolean useCCIdata = true;
	private boolean useAHSdata = true;
	/*
	 * TODO: Get rid of useless code... and rename things
	 */
	public FilterView(PickupDragController dragController)
	{
		
		this.dragController = dragController;
		
		menu = new MenuBar();
		menu.setWidth("100%");
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(false);
		
		MenuItem logoutMenu = new MenuItem("Logout", new Command() {
			@Override
			public void execute() {
				FilterView.this.logout.click();
			}
		});
		
		MenuItem changePasswordMenu = new MenuItem("Change Password", new Command() {
			@Override
			public void execute() {
				//Window.alert("Change password?");
				FilterView.this.changePassword.click();
			}
		});
		
		MenuBar userBar = new MenuBar(true);
		MenuItem userMenu = new MenuItem("User", userBar);
		userBar.addItem(logoutMenu);
		userBar.addItem(changePasswordMenu);
		
		logoutMenu.getElement().getStyle().setColor("#cc0000");
		menu.addItem(userMenu);

		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Export Data", new Command() {

			@Override
			public void execute() {
				FilterView.this.exportDataButton.click();
			}
			
		});
/* */
		//fileMenu.addSeparator();
		fileMenu.addItem("Create Column", new Command() {

			@Override
			public void execute() {
				FilterView.this.synth.click();
			}
			
		}); /* */
/*		fileMenu.addSeparator();
		fileMenu.addItem("Save Query", new Command() {
			
			@Override
			public void execute() {
				FilterView.this.saveQueryButton.click();
			}
		});*/
/*
		fileMenu.addItem("Delete Saved Query", new Command() {
			
			@Override
			public void execute() {
				FilterView.this.deleteQueryButton.click();
			}
		});
*/
		fileMenu.addItem("Manage Synonyms", new Command() {
			@Override
			public void execute() {
				FilterView.this.synonymButton.click();
			}
		});
		
		
		datacut = new MenuItem("", new Command() { public void execute(){}});

		MenuBar dsetMenu = new MenuBar(true);
		MenuBar datasetSelector = new MenuBar(true);
		dsetMenu2 = new MenuItem("Current Dataset: CCI and AHS Data",datasetSelector);
		dsetMenu.addItem("CCI Data", new Command()
	    {
	     	public void execute() {
	       		useCCIdata=true;
	       		useAHSdata=false;
	       		dsetMenu2.setText("Current Dataset: CCI Data");
	       	}
	    });
        dsetMenu.addItem("AHS Data", new Command()
        {
        	public void execute() {
        		useAHSdata=true;
        		useCCIdata=false;
       		    dsetMenu2.setText("Current Dataset: AHS Data");
        	}
        });
        dsetMenu.addItem("CCI and AHS Data", new Command()
        {
        	public void execute() {
        		useAHSdata=true;
        		useCCIdata=true;
        		dsetMenu2.setText("Current Dataset: CCI and AHS Data");
        	}
        });
        fileMenu.addItem(new MenuItem("Select Dataset", dsetMenu));
        
		fileMenu.addItem("Exit", new Command() {
			@Override
			public void execute() {
				//FilterView.this.logout.click();
			}
		});

		//menu.addItem(new MenuItem("File", fileMenu));
		/*
		menu.addSeparator();
		menu.addSeparator();
		menu.addItem("Clear Definition", new Command() {
			@Override
			public void execute() {
				FilterView.this.clear.click();
			}
		});
		menu.addSeparator();
		executeQuery = menu.addItem("Execute Query", new Command() {
			@Override
			public void execute() {
				FilterView.this.apply.click();
			}
		});
		menu.addSeparator();*/
		
		// hack way of adding space
		MenuItem padding = new MenuItem("", new Command() { public void execute(){}});
		padding.setEnabled(false);
// can't seem to remove background highlighting when hovering over this added space
// tried all of the following without success:
//		padding.removeStyleDependentName("selected");
//		padding.removeStyleDependentName("hover");
//		padding.removeStyleName("gwt-MenuItem-selected");
//		padding.removeStyleName("gwt-MenuItem-hover");
		padding.getElement().getStyle().setWidth(30, Unit.IN);
		menu.addItem(padding);

		datasetSelector.addItem("CCI Data", new Command()
	    {
	     	public void execute() {
	       		useCCIdata=true;
	       		useAHSdata=false;
	       		dsetMenu2.setText("Current Dataset: CCI Data");
	       	}
	    });
        datasetSelector.addItem("AHS Data", new Command()
        {
        	public void execute() {
        		useAHSdata=true;
        		useCCIdata=false;
       		    dsetMenu2.setText("Current Dataset: AHS Data");
        	}
        });
        datasetSelector.addItem("CCI and AHS Data", new Command()
        {
        	public void execute() {
        		useAHSdata=true;
        		useCCIdata=true;
        		dsetMenu2.setText("Current Dataset: CCI and AHS Data");
        	}
        });
        dsetMenu2.getElement().getStyle().setWidth(2.5,Unit.IN);
        //menu.addItem(dsetMenu2);
        
        datacut.getElement().getStyle().setWidth(2.5,Unit.IN);
        datacut.setEnabled(false);
		MenuItem padding2 = new MenuItem("", new Command() { public void execute(){}});
		padding2.getElement().getStyle().setWidth(3.4, Unit.IN);
		padding2.setEnabled(false);
		//menu.addItem(padding2);

        menu.addItem(datacut);
/*		
		ListBox lBox = new ListBox();
		lBox.addItem("CCI Data");
		lBox.addItem("AHS Data");
		lBox.addItem("CCI and AHS Data");
//		menu.addItem("Dataset:");
        menu.addItem(lBox,new Command() {
        	public void execute() {
        	}
        });
*/
/*
        MenuBar datasetSelector = new MenuBar(true);
        dsetMenu = new MenuItem("CCI and AHS Data", datasetSelector);
        datasetSelector.setTitle("CCI Data");
        datasetSelector.addItem("CCI Data", new Command()
        {
        	public void execute() {
        		useCCIdata=true;
        		useAHSdata=false;
        		dsetMenu.setText("CCI Data");
        	}
        });
        datasetSelector.addItem("AHS Data", new Command()
        {
        	public void execute() {
        		useAHSdata=true;
        		useCCIdata=false;
        		dsetMenu.setText("AHS Data");
        	}
        });
        datasetSelector.addItem("CCI and AHS Data", new Command()
        {
        	public void execute() {
        		useAHSdata=true;
        		useCCIdata=true;
        		dsetMenu.setText("CCI and AHS Data");
        	}
        });
		menu.addItem(dsetMenu);
*/
		VerticalPanel innerQueryPanel = new VerticalPanel();
		
		/* TODO: REMOVE! */
		SplitLayoutPanel innerAggregatePanel = new SplitLayoutPanel(5);
		innerAggregatePanel.setSize("100%", "100%");
		innerAggregatePanel.setStyleName("grey-SplitLayoutPanel");
		
		ScrollPanel aggregateScrollPanel = new ScrollPanel();
		ScrollPanel groupByScrollPanel = new ScrollPanel();
		ScrollPanel filterScrollPanel = new ScrollPanel();
		aggregateScrollPanel.setSize("100%", "100%");
		groupByScrollPanel.setSize("100%", "100%");
		filterScrollPanel.setSize("100%", "100%");
		
		VerticalPanel innerFunctionPanel = new VerticalPanel();
		innerFunctionPanel.setWidth("100%");
		aggregateScrollPanel.add(innerFunctionPanel);
		
		VerticalPanel innerGroupByPanel = new VerticalPanel();
		innerGroupByPanel.setWidth("100%");
		groupByScrollPanel.add(innerGroupByPanel);
		
		VerticalPanel innerFilterPanel = new VerticalPanel();
		innerFilterPanel.setWidth("100%");
		filterScrollPanel.add(innerFilterPanel);

		innerAggregatePanel.addWest(aggregateScrollPanel, 300);
		innerAggregatePanel.addEast(groupByScrollPanel, 300);
		innerAggregatePanel.add(filterScrollPanel);
		
		SplitLayoutPanel mainSplitLayoutPanel = new SplitLayoutPanel(5);
		mainSplitLayoutPanel.setSize("100%", "93%");
		mainSplitLayoutPanel.setStyleName("grey-SplitLayoutPanel");
		
		ScrollPanel kmSubgroupSP = new ScrollPanel();
		ScrollPanel defineSP = new ScrollPanel();
		kmSubgroupSP.setSize("100%", "100%");
		defineSP.setSize("98%", "100%");
		
		KMChart = new Image();
		KMChart.setWidth("100%");
		
		VerticalPanel innerDefinitionsPanel = new VerticalPanel();
		innerDefinitionsPanel.setWidth("100%");
		kmSubgroupSP.add(innerDefinitionsPanel);
		
		VerticalPanel innerDefPanel = new VerticalPanel();
		innerDefPanel.setWidth("100%");
		innerDefPanel.setHeight("100%");
		defineSP.add(innerDefPanel);
		
		HorizontalPanel addDefPanel = new HorizontalPanel();
		addDefPanel.setWidth("100%");
		addDefPanel.setHeight("24px");
		this.addDefinition = new Button("Create Definition");
		this.changeDef = new Button("Alter Definition");
		addDefPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		VerticalPanel innerAddGroupAlignmentPanel = new VerticalPanel();
		addDefPanel.add(innerAddGroupAlignmentPanel);
		//innerAddGroupAlignmentPanel.add(changeDef);
		//innerAddGroupAlignmentPanel.add(addDefinition);
		
		LayoutPanel definePanel = new LayoutPanel();
		
		definePanel.add(defineSP);
		definePanel.add(addDefPanel);
		
		definePanel.setWidgetBottomHeight(defineSP, 48, Unit.PX, 80, Unit.PCT);
		definePanel.setWidgetBottomHeight(addDefPanel, 0, Unit.PX, 48, Unit.PX);
		
		VerticalPanel functionResultsInnerPanel = new VerticalPanel();

		kaplanMeierButton = new Button("Kaplan-Meier");
		kaplanMeierButton.setWidth("100%");
		kaplanMeierButton.setStyleName("gwt-Button-NoBG");
		apply = new Button("Tabulate");
		apply.setWidth("100%");
		apply.setStyleName("gwt-Button-NoBG");
		PSA_aggregate = new Button("Aggregate PSA");
		PSA_aggregate.setWidth("100%");
		PSA_aggregate.setStyleName("gwt-Button-NoBG");
		
		Export = new Button("Export");
		Export.setWidth("100%");
		Export.setStyleName("gwt-Button-NoBG");
		
		Interval_Query = new Button("Interval Query");
		Interval_Query.setWidth("100%");
		Interval_Query.setStyleName("gwt-Button-NoBG");
		
		Summarize = new Button("Patient");
		Summarize.setWidth("100%");
		Summarize.setStyleName("gwt-Button-NoBG");
		
		SummarizeTextBox = new TextBox();
		SummarizeTextBox.setWidth("100%");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(Summarize);
		hp.add(SummarizeTextBox);
		
		Dashboard = new Button("Dashboard");
		Dashboard.setWidth("100%");
		Dashboard.setStyleName("gwt-Button-NoBG");
		
		//hp.setWidth("100%");
		//hp.setStyleName("gwt-Button-NoBG");
		
		//saveQuery = new Button("Save Definitions");
		//saveQuery.setWidth("100%");
		//saveQuery.setStyleName("gwt-Button-NoBG");
		functionsFT = new FlexTable();
		functionsFT.setSize("100%", "100%");
		Label functionsL = new Label("Operations:");
		functionsL.setStyleName("titleLabel");
		functionResultsInnerPanel.add(functionsL);
		functionResultsInnerPanel.add(apply);
		functionResultsInnerPanel.add(Export);
		functionResultsInnerPanel.add(kaplanMeierButton);
		functionResultsInnerPanel.add(PSA_aggregate);
		functionResultsInnerPanel.add(Dashboard);
		functionResultsInnerPanel.add(hp);
		functionResultsInnerPanel.add(Interval_Query);
		//functionResultsInnerPanel.add(saveQuery);
		functionResultsInnerPanel.setWidth("100%");
		
		subgroupResults = new TabLayoutPanel(23, Unit.PX);
		subgroupResults.setSize("100%", "100%");
		
		//ScrollPanel subgroupResultsSP = new ScrollPanel(functionResultsInnerPanel);
		//subgroupResultsSP.setSize("100%", "100%");
		
		VerticalPanel kmchartVP = new VerticalPanel();
		kmchartVP.add(KMChart);
		kmchartVP.setSize("100%", "100%");
		
		mainSplitLayoutPanel.addEast(functionResultsInnerPanel, 120);
		mainSplitLayoutPanel.addWest(defineSP, 600);
		mainSplitLayoutPanel.add(kmSubgroupSP);
		
		innerQueryPanel.setWidth("100%");
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.setHeight("100%");
		outerPanel.setWidth("100%");
		ScrollPanel scrollGraphical = new ScrollPanel(innerQueryPanel);
		
		modality = new TabLayoutPanel(2, Unit.EM);
		modality.setWidth("100%");
		modality.setHeight("100%");
		
		/* TODO: expert input now has to only show up when the user wants
		 * it to
		 */
		expertInput = new TextArea();
		expertInput.setWidth("100%");
		expertInput.setHeight("85%");
		
		/* Old buttons, left this way... */
		imageQuery = new Button("Image Query");
		clear = new Button("Clear");
		logout = new Button("Logout");
		changePassword = new Button("Change Password");
		synth = new Button("Create Column");
		exportDataButton = new Button("Export Data");
		//saveQueryButton = new Button("Save Query");
		deleteQueryButton = new Button("Delete Saved Query");
		addToKaplanMeier = new Button("Add To Kaplan-Meier");
		synonymButton = new Button("Manage Synonyms");
		
		mainPanel = new FlexTable();
		innerQueryPanel.add(mainPanel);
		
		aggregateFunctionPanel = new FlexTable();
		aggregateFunctionPanel.setWidth("100%");
		Label functionL = new Label("Operation: ");
		functionL.setStyleName("titleLabel");
		innerFunctionPanel.add(functionL);
		innerFunctionPanel.add(aggregateFunctionPanel);
		
		groupByPanel = new FlexTable();
		groupByPanel.setWidth("100%");
		Label groupByL = new Label("Define Subgroups: ");
		groupByL.setStyleName("titleLabel");
		innerGroupByPanel.add(groupByL);
		innerGroupByPanel.add(groupByPanel);
		
		aggregateFilterPanel = new FlexTable();
		aggregateFilterPanel.setWidth("100%");
		Label defineL = new Label("Define: ");
		defineL.setStyleName("titleLabel");
		
		this.defName = new TextBox();
		this.defName.setText("A");
		this.clearDefinition = new Button("Clear Definition");
		
		HorizontalPanel defineNameHP = new HorizontalPanel();
		defineNameHP.setWidth("100%");
		defineNameHP.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		defineNameHP.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		HorizontalPanel defineNameAlignmentWrapper = new HorizontalPanel();
		defineNameAlignmentWrapper.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		defineNameAlignmentWrapper.add(defineL);
		defineNameAlignmentWrapper.add(defName);
		defineNameHP.add(defineNameAlignmentWrapper);
		defineNameHP.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		defineNameHP.add(clearDefinition);
		defineNameHP.add(addDefinition);
		
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		definitionsFT = new FlexTable();
		definitionsFT.setWidth("100%");
		Label definitionsL = new Label("Definitions:");
		this.addFunction = new Button("Create definition");
		HorizontalPanel defAlignmentPanel = new HorizontalPanel();
		defAlignmentPanel.setWidth("100%");
		defAlignmentPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		defAlignmentPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		defAlignmentPanel.add(definitionsL);
		//defAlignmentPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		//defAlignmentPanel.add(addFunction);
		definitionsL.setStyleName("titleLabel");
		innerDefinitionsPanel.add(defAlignmentPanel);
		innerDefinitionsPanel.add(definitionsFT);
		
		defineFT = new FlexTable();
		defineFT.setWidth("100%");
		innerDefPanel.add(defineNameHP);
		innerDefPanel.add(defineFT);
		
		outerPanel.add(menu);
		outerPanel.add(buttonsPanel);
		
		buttonsPanel.setVisible(false);
		outerPanel.add(mainSplitLayoutPanel);

		mainPanel.setWidth("100%");
		scrollGraphical.setHeight("85%");
		HorizontalPanel leftPanel = new HorizontalPanel();
		leftPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);

		// add buttons to panel here as otherwise "clicking" them seems to go unnoticed
		leftPanel.add(logout);
		leftPanel.add(changePassword);
		leftPanel.add(synth);
		leftPanel.add(imageQuery);
		leftPanel.add(exportDataButton);
		leftPanel.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp"));
		//leftPanel.add(saveQueryButton);
		leftPanel.add(deleteQueryButton);
		leftPanel.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp"));
		//leftPanel.add(kaplanMeierButton);
		leftPanel.add(addToKaplanMeier);
		leftPanel.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp"));
		leftPanel.add(synonymButton);
		
		HorizontalPanel rightPanel = new HorizontalPanel();
		HorizontalPanel innerRightAlignmentPanel = new HorizontalPanel();
		rightPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		rightPanel.setWidth("100%");
		rightPanel.getElement().setAttribute("float", "right");
		rightPanel.add(innerRightAlignmentPanel);
		//innerRightAlignmentPanel.add(apply);
		innerRightAlignmentPanel.add(clear);
		innerRightAlignmentPanel.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp"));
		//innerRightAlignmentPanel.add(logout);
		
		buttonsPanel.add(leftPanel);
		buttonsPanel.add(rightPanel);
		
		buttonsPanel.setWidth("100%");
		buttonsPanel.getElement().setId("buttonBar");

		this.allFiltersDropController = new PreviewDropEventController(outerPanel);
		this.kmFilterByDropController = new PreviewDropEventController(mainSplitLayoutPanel);
		initWidget(outerPanel);
		dragController.registerDropController(allFiltersDropController);
		dragController.registerDropController(kmFilterByDropController);
	}
	
//	public Button getSaveQuery() {
//		return saveQuery;
//	}

	public List<FilterPanel> getFilterPanels(){
		ArrayList<FilterPanel> filters = new ArrayList<FilterPanel>();
		for(int i = 0; i<this.getMainPanel().getRowCount(); i++){
			if(this.getMainPanel().getWidget(i,0) instanceof FilterPanel){
				filters.add((FilterPanel)this.getMainPanel().getWidget(i,0));
			}
		}
		return filters;
	}
	public PreviewDropEventController getAllFiltersDropController() {
		return this.allFiltersDropController;
	}
	public PreviewDropEventController getKMFilterByDropController() {
		return this.kmFilterByDropController;
	}
	
	public FlexTable getMainPanel()
	{
		return this.mainPanel;
	}
	public FlexTable getAggregatePanel()
	{
		return this.aggregateFunctionPanel;
	}
	public FlexTable getGroupByPanel()
	{
		return this.groupByPanel;
	}
	public FlexTable getAggregateFilterPanel()
	{
		return this.aggregateFilterPanel;
	}
	public FlexTable getDefineFT()
	{
		return this.defineFT;
	}
	public FlexTable getKMSubgroupPanel()
	{
		return this.definitionsFT;
	}
	
//	@Override
//	public Button getSaveQueryButton() {
//		return saveQueryButton;
//	}
	
	@Override
	public Button getLogoutButton() {
		return logout;
	}
	
	public Button getChangePasswordButton() {
		return changePassword;
	}

	@Override
	public Button getImageQueryButton() {
		return imageQuery;
	}

	@Override
	public Button getApplyButton() {
		return apply;
	}

	@Override
	public Button getClearButton() {
		return clear;
	}
	
	@Override
	public Button getDeleteQueryButton() {
		return deleteQueryButton;
	}
	
	@Override
	public void clearAll() 
	{
		this.getMainPanel().removeAllRows();
		this.getAggregateFilterPanel().removeAllRows();
		this.getAggregatePanel().removeAllRows();
		this.getGroupByPanel().removeAllRows();
		this.getDefineFT().removeAllRows();
		this.getKMSubgroupPanel().removeAllRows();
		this.getFunctionsFT().removeAllRows();
		this.expertInput.setText("");
	}

	public Button getSynthButton(){
		return this.synth;
	}
	
	public TabLayoutPanel getModality() {
		return modality;
	}
	
	public TextArea getExpertInput() {
		return expertInput;
	}
	
	public void setExpertMode() {
		currentMode = Modality.EXPERT;
	}
	
	public void setKMMode() {
		currentMode = Modality.KAPLAN;
	}

	public Boolean isExpertMode() {
		return currentMode.equals(Modality.EXPERT);
	}
	public void setAggregateMode() {
		currentMode = Modality.AGGREGATE;
	}

	public Boolean isAggregateMode() {
		return currentMode.equals(Modality.AGGREGATE);
	}
	public void setQueryMode() {
		currentMode = Modality.QUERY;
	}
	public Boolean isQueryMode() {
		return currentMode.equals(Modality.QUERY);
	}
	
	public Boolean isKMMode() {
		return currentMode.equals(Modality.KAPLAN);
	}
	public Modality getMode() {
		return this.currentMode;
	}
	public TextBox getSummarizeTextBox() {
		return this.SummarizeTextBox;
	}
	public Button getExportButton() {
		return this.Export;
	}
	public Button getIntervalQueryButton() {
		return this.Interval_Query;
	}
	public Button getPsaAggregateButton() {
		return this.PSA_aggregate;
	}
	public Button getSummarizeButton() {
		return this.Summarize;
	}
	public Button getDashboardButton() {
		return this.Dashboard;
	}
	public Button getKaplanMeierButton() {
		return this.kaplanMeierButton;
	}
	public Button getAddToKaplanMeier() {
		return this.addToKaplanMeier;
	}
	public Button getSynonymButton() {
		return this.synonymButton;
	}
	public Button addDefinitionButton() {
		return this.addDefinition;
	}
	public void setKMUrl(String url) {
		this.KMChart.setUrl(url);
	}
	public Image getKMChart() {
		return this.KMChart;
	}
	public FlexTable getFunctionsFT() {
		return this.functionsFT;
	}
	public void setExecuteRed() {
		//this.apply.getElement().getStyle().setColor("#FF0000");
	}
	public void clearExecuteRed() {
		//this.apply.getElement().getStyle().clearColor();
	}
	public FlexTable getDefinitionsFT() {
		return this.definitionsFT;
	}
	public Button getAddFunctionButton() {
		return this.addFunction;
	}
	public TextBox getDefName() {
		return this.defName;
	}
	public Button getClearDefButton() {
		return this.clearDefinition;
	}
	public PickupDragController getDragController() {
		return this.dragController;
	}
	public void setDatacutDate(String d) {
		this.datacut.setText("Datacut: " + d);
	}
}
