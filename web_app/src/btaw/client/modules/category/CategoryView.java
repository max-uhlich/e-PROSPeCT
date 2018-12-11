package btaw.client.modules.category;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import btaw.client.framework.AView;
import btaw.client.view.widgets.ColumnLabel;
import btaw.client.view.widgets.QueryDeleteButton;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.client.view.widgets.SavedFeatureLabel;
import btaw.client.view.widgets.TabCloseButton;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.SavedDefinitionColumn;
import btaw.shared.model.query.column.SynthTableColumn;
import btaw.shared.model.query.column.TableColumn;
import btaw.shared.model.query.saved.SavedQueryDisplayData;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CategoryView extends AView implements CategoryPresenter.View{

	private FlowPanel vp;
	private SuggestBox search;
	private MyStackPanel categories;
	private final PickupDragController dragController;
	private MultiWordSuggestOracle suggestOracle;
	private ColumnLabel selected;
	private ScrollPanel previousQueryScrollPanel;
	private ScrollPanel previousFeaturesScrollPanel;
	private ScrollPanel savedQueryScrollPanel;
	private VerticalPanel previousQueryPanel;
	private VerticalPanel previousFeaturesPanel;
	private VerticalPanel savedQueryPanel;
	private Boolean isOddPanel = true;
	private int backCnt = 0;
	private Tree previousQueryRoot;
	private Widget previouslyLoadedQuery = null;
	
	public CategoryView(PickupDragController dragController)
	{
		
		vp = new FlowPanel();
		ScrollPanel sp = new ScrollPanel();
		sp.setSize("100%", "100%");
		//sp.getElement().getStyle().setWidth(900, Unit.PX);
		initWidget(vp);
		
		this.getElement().setId("left-panel");
		
		this.dragController = dragController;
		
		search = new SuggestBox();
		suggestOracle = (MultiWordSuggestOracle) search.getSuggestOracle();
		
		categories = new MyStackPanel();
		categories.setWidth("350px");
		//categories.getElement().getStyle().setWidth(900, Unit.PX);
		
		search.setText("Search...");
		search.getValueBox().getElement().getStyle().setColor("#AAAAAA");
		search.setWidth("100%");
		categories.getElement().setId("category-table");
		
		int width = 287;
		int height = 100;
		int spacer = 10;
		
		int amii_width = 129;
		int amii_height = 100;
		
		VerticalPanel logopanel = new VerticalPanel();
		HorizontalPanel amii_panel = new HorizontalPanel();
		Image logo = new Image("images/apcari_logo2.png");
		Image amii_logo = new Image("images/transp_amii.png");
		logo.setPixelSize(width, height);
		amii_logo.setPixelSize(amii_width, amii_height);
		amii_panel.add(amii_logo);
		amii_panel.getElement().getStyle().setPaddingLeft(26, Unit.PX);
		logopanel.add(amii_panel);
		logopanel.add(logo);
		
		logopanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		logopanel.getElement().getStyle().setTop(Window.getClientHeight()-(height+spacer+amii_height), Unit.PX);
		logopanel.getElement().getStyle().setLeft(spacer/2, Unit.PX);
		logopanel.getElement().getStyle().setZIndex(0);
		sp.getElement().getStyle().setZIndex(1);
		
		vp.getElement().getStyle().setPadding(3, Unit.PX);
		vp.setWidth("500px");
		vp.add(search);
		sp.add(categories);
		vp.add(sp);
		vp.add(logopanel);
		
		previousQueryPanel = new VerticalPanel();
		previousQueryPanel.setSize("100%", "100%");
		savedQueryPanel = new VerticalPanel();
		savedQueryPanel.setSize("100%", "100%");
		previousFeaturesPanel = new VerticalPanel();
		previousFeaturesPanel.setSize("100%", "100%");

		previousQueryScrollPanel = new ScrollPanel(previousQueryPanel);
// BH		previousQueryScrollPanel.setWidth("192px");
		previousQueryScrollPanel.setWidth("200px");
		previousQueryScrollPanel.setHeight("250px");
		previousFeaturesScrollPanel = new ScrollPanel(previousFeaturesPanel);
// BH		previousFeaturesScrollPanel.setWidth("192px");
		previousFeaturesScrollPanel.setWidth("200px");
		previousFeaturesScrollPanel.setHeight("250px");
		savedQueryScrollPanel = new ScrollPanel(savedQueryPanel);
		savedQueryScrollPanel.setWidth("192px");
		savedQueryScrollPanel.setHeight("250px");
		
		previousQueryRoot = new Tree();
		previousQueryPanel.add(previousQueryRoot);
	}
	
	public QueryDeleteButton addColumn(Column c)
	{
		
		//Add a saved definition column to the saved definitions flowpanel
		//FlowPanel fp = (FlowPanel)categories.getWidget(0);
		
		//for(int i=0; i<fp.getWidgetCount(); i++){
		//	Window.alert(i + ": " + ((ColumnLabel)fp.getWidget(i)).getText());
		//	if(((ColumnLabel)fp.getWidget(i)).getText().equals(c.getName()))
		//		fp.remove(i);
		//}
		
		FlowPanel fp = (FlowPanel)categories.getWidget(0);
		
		for(int i=0; i<fp.getWidgetCount(); i++){
			Iterator<Widget> arrayOfWidgets = ((FlowPanel)fp.getWidget(i)).iterator();
			while (arrayOfWidgets.hasNext()) {
				Widget ch = arrayOfWidgets.next();
				if (ch instanceof ColumnLabel) {
					if(((ColumnLabel)ch).getText().equals(c.getName()))
						fp.remove(i);
				}
			}
		}
		
		//ColumnLabel label = new ColumnLabel(c);
		//dragController.makeDraggable(label);
		//fp.add(label);
		
		ColumnLabel label = new ColumnLabel(c);

		Image im = new Image("images/close.png");
		im.setPixelSize(6, 6);
		QueryDeleteButton deleteButton = new QueryDeleteButton(im,c);
		label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		//label.getElement().getStyle().setMarginLeft(4, Unit.PX);
		
		FlowPanel tabView = new FlowPanel();
		tabView.add(deleteButton);
		tabView.add(label);
		
		dragController.makeDraggable(tabView,label);
		fp.add(tabView);
		
		return deleteButton;
		
	}
	
	public void deleteColumn(Column c)
	{
		
		FlowPanel fp = (FlowPanel)categories.getWidget(0);
		
		for(int i=0; i<fp.getWidgetCount(); i++){
			Iterator<Widget> arrayOfWidgets = ((FlowPanel)fp.getWidget(i)).iterator();
			while (arrayOfWidgets.hasNext()) {
				Widget ch = arrayOfWidgets.next();
				if (ch instanceof ColumnLabel) {
					if(((ColumnLabel)ch).getText().equals(c.getName()))
						fp.remove(i);
				}
			}
		}
		
	}
	
	public List<QueryDeleteButton> addCategory(String title, List<Column> columns)
	{
		List<QueryDeleteButton> delete_buttons = new ArrayList<QueryDeleteButton>();
		
		//If any column has a tree header whose name is different from title, we need to construct a tree for this category. Otherwise we construct a flowpanel.
		boolean buildTree = false;
		for (Column c: columns){
			if(!(c instanceof SavedDefinitionColumn)){
				if(!((TableColumn)c).getTreeHeader().equals(title)){
					buildTree = true;
					break;
				}
			}
		}

		if(buildTree){
			
			List<String> treeHeaders = new ArrayList<String>();
			
			for (Column c: columns){
				if(!treeHeaders.contains(((TableColumn)c).getTreeHeader())){
					treeHeaders.add(((TableColumn)c).getTreeHeader());
				}
			}
			
			Tree tree = new Tree();
			categories.add(tree, title, false);
			tree.setAnimationEnabled(true);
			
			for (String h: treeHeaders){
				TreeItem root = tree.addTextItem(h);
				
				FlowPanel fp = new FlowPanel();
				root.addItem(fp);
				for (Column c: columns){
					if (((TableColumn)c).getTreeHeader().equals(h)){
						ColumnLabel label = new ColumnLabel(c);
						dragController.makeDraggable(label);
						fp.add(label);
					}
				}
			}

			/*String treeHeader = ((TableColumn)columns.get(0)).getTreeHeader();
			Tree tree = new Tree();
			categories.add(tree, title, false);
			tree.setAnimationEnabled(true);
			TreeItem root = tree.addTextItem(treeHeader);
			
			for (Column c: columns){
				if(!((TableColumn)c).getTreeHeader().equals(treeHeader)){
					treeHeader = ((TableColumn)c).getTreeHeader();
					root = tree.addTextItem(treeHeader);
				}
				
				ColumnLabel label = new ColumnLabel(c);
				dragController.makeDraggable(label);
				root.addItem(label);
			}*/
			
		} else {
			FlowPanel fp = new FlowPanel();
			categories.add(fp, title, false);
			for (Column c: columns){
				
				if(c instanceof SavedDefinitionColumn){
					//Window.alert(c.getName());
					
					ColumnLabel label = new ColumnLabel(c);

					Image im = new Image("images/close.png");
					im.setPixelSize(6, 6);
					QueryDeleteButton deleteButton = new QueryDeleteButton(im,c);
					label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					//label.getElement().getStyle().setMarginLeft(4, Unit.PX);
					
					FlowPanel tabView = new FlowPanel();
					delete_buttons.add(deleteButton);
					tabView.add(deleteButton);
					tabView.add(label);
					
					dragController.makeDraggable(tabView,label);
					fp.add(tabView);
				} else {
					ColumnLabel label = new ColumnLabel(c);
					dragController.makeDraggable(label);
					fp.add(label);
				}
			}
		}
		
		return delete_buttons;
	}
	
	public void setupSavedCats() {
		categories.add(previousQueryScrollPanel, "Saved Definitions", false);
	}
	
	/* Assumption that List is in order for dates */
	public void addPreviousQueries(List<SavedQueryDisplayData> data, CategoryPresenter presenter) {
		previousQueryRoot.clear();
		HashSet<String> years = new HashSet<String>();
		HashSet<String> months = new HashSet<String>();
		HashSet<String> days = new HashSet<String>();
		TreeItem currYear = null;
		TreeItem currMonth = null;
		TreeItem currDay = null;
		
		for(SavedQueryDisplayData currData : data) {
			
			SavedQueryLabel label = new SavedQueryLabel(currData.getDate(), currData.getSavedQueryID());
			label.addClickHandler(presenter);
			
			if(years.add(DateTimeFormat.getFormat("yyyy").format(currData.getJavaDate()))) {
				currYear = new TreeItem(SimpleHtmlSanitizer.sanitizeHtml(DateTimeFormat.getFormat("yyyy").format(currData.getJavaDate())));
				previousQueryRoot.addItem(currYear);
				
				months = new HashSet<String>();
				months.add(DateTimeFormat.getFormat("MMMM").format(currData.getJavaDate()));
				currMonth = new TreeItem(SimpleHtmlSanitizer.sanitizeHtml(DateTimeFormat.getFormat("MMMM").format(currData.getJavaDate())));
				currYear.addItem(currMonth);
				
				days = new HashSet<String>();
				days.add(DateTimeFormat.getFormat("dd").format(currData.getJavaDate()));
				currDay = new TreeItem(SimpleHtmlSanitizer.sanitizeHtml(DateTimeFormat.getFormat("dd").format(currData.getJavaDate())));
				currMonth.addItem(currDay);
				
				currDay.addItem(label);
			} else {
				if(months.add(DateTimeFormat.getFormat("MMMM").format(currData.getJavaDate()))) {
					currMonth = new TreeItem(SimpleHtmlSanitizer.sanitizeHtml(DateTimeFormat.getFormat("MMMM").format(currData.getJavaDate())));
					currYear.addItem(currMonth);
					
					days = new HashSet<String>();
					days.add(DateTimeFormat.getFormat("dd").format(currData.getJavaDate()));
					currDay = new TreeItem(SimpleHtmlSanitizer.sanitizeHtml(DateTimeFormat.getFormat("dd").format(currData.getJavaDate())));
					currMonth.addItem(currDay);
					
					currDay.addItem(label);
				} else {
					if(days.add(DateTimeFormat.getFormat("dd").format(currData.getJavaDate()))) {
						currDay = new TreeItem(SimpleHtmlSanitizer.sanitizeHtml(DateTimeFormat.getFormat("dd").format(currData.getJavaDate())));
						currMonth.addItem(currDay);
						
						currDay.addItem(label);
					} else {
						currDay.addItem(label);
					}
				}
			}
		}
	}
	
	public void addSavedQueries(List<SavedQueryDisplayData> data, CategoryPresenter presenter) {
		isOddPanel = true;
		savedQueryPanel.clear();
		for(SavedQueryDisplayData currData : data) {
			FlowPanel fp = new FlowPanel();
			if(isOddPanel) {
				fp.getElement().getStyle().setBackgroundColor("#E3EBFF");
				isOddPanel = false;
			} else {
				isOddPanel = true;
			}
			savedQueryPanel.add(fp);
			SavedQueryLabel label = new SavedQueryLabel(cleanHTML(currData.getDate()) + "<br>" + cleanHTML(currData.getName()) , currData.getSavedQueryID());
			label.getElement().getStyle().setPaddingTop(5, Unit.PX);
			label.getElement().getStyle().setPaddingLeft(5, Unit.PX);
			fp.add(label);
			label.addClickHandler(presenter);
		}
	}

	
	public void addSavedFeatures(List<SynthTableColumn> data, CategoryPresenter presenter) {
		isOddPanel = true;
		previousFeaturesPanel.clear();
		for(SynthTableColumn currData : data) {
			FlowPanel fp = new FlowPanel();
			if(isOddPanel) {
				fp.getElement().getStyle().setBackgroundColor("#E3EBFF");
				isOddPanel = false;
			} else {
				isOddPanel = true;
			}
			previousFeaturesPanel.add(fp);
			SavedFeatureLabel label = new SavedFeatureLabel(cleanHTML(currData.getName()) , currData.getId());
			label.getElement().getStyle().setPaddingTop(5, Unit.PX);
			label.getElement().getStyle().setPaddingLeft(5, Unit.PX);
			fp.add(label);
			label.addClickHandler(presenter);
		}

		// add ability to create a new feature
		FlowPanel fp = new FlowPanel();
		if(isOddPanel) {
			fp.getElement().getStyle().setBackgroundColor("#E3EBFF");
			isOddPanel = false;
		} else {
			isOddPanel = true;
		}
		previousFeaturesPanel.add(fp);
		SynthTableColumn stc = new SynthTableColumn("Create New Feature", "synthetic", null);
		SavedFeatureLabel label = new SavedFeatureLabel(cleanHTML("Create New Feature") , stc.getId());
		label.getElement().getStyle().setPaddingTop(5, Unit.PX);
		label.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		fp.add(label);
		label.addClickHandler(presenter);
	}

	public void setupSavedFeatures() {
		categories.add(previousFeaturesScrollPanel, "User Created Features", false);
	}

	@Override
	public FlowPanel getMainPanel() {
		return this.vp;
	}

	@Override
	public MultiWordSuggestOracle getSuggestOracle() {
		return suggestOracle;
	}

	@Override
	public SuggestBox getSuggestBox() {
		return search;
	}

	@Override
	public ColumnLabel search(Column col, String cat) {
		categories.showStack(categories.getSelectedIndex());
		clearSearch();
		int header = 0;
		for (Widget w : categories){
			if (w instanceof FlowPanel){
				FlowPanel fp = (FlowPanel) w;
				for (Widget cl : fp){
					if (cl instanceof ColumnLabel){
						ColumnLabel c = (ColumnLabel) cl;
						if (c.getColumn().getName().equals(col.getName()) && categories.getStackText(header).equals(cat)){
							categories.showStack(categories.getWidgetIndex(fp));
							c.getElement().getStyle().setBackgroundColor("#FFFF33");
							selected = c;
							selectAllSearch();
							return c;
						}
					} else if (cl instanceof FlowPanel){
						Iterator<Widget> arrayOfWidgets = ((FlowPanel)cl).iterator();
						while (arrayOfWidgets.hasNext()) {
							Widget ch = arrayOfWidgets.next();
							if (ch instanceof ColumnLabel){
								ColumnLabel c = (ColumnLabel) ch;
								if (c.getColumn().getName().equals(col.getName()) && categories.getStackText(header).equals(cat)){
									categories.showStack(categories.getWidgetIndex(fp));
									c.getElement().getStyle().setBackgroundColor("#FFFF33");
									selected = c;
									selectAllSearch();
									return c;
								}
							}
						}
					}
				}
			} else if (w instanceof Tree){
				Tree t = (Tree) w;
				for(int i=0; i<t.getItemCount(); i++){
					TreeItem ti = t.getItem(i);
					FlowPanel fp = (FlowPanel) ti.getChild(0).getWidget();
					for (Widget cl : fp){
						if (cl instanceof ColumnLabel){
							ColumnLabel c = (ColumnLabel) cl;
							if (c.getColumn().getName().equals(col.getName()) && categories.getStackText(header).equals(cat)){
								categories.showStack(categories.getWidgetIndex(t));
								ti.setState(true);
								c.getElement().getStyle().setBackgroundColor("#FFFF33");
								selected = c;
								selectAllSearch();
								return c;
							}
						}
					}
				}
			}
			header = header+1;
		}
		return null;
	}

	@Override
	public void selectAllSearch() {
		search.getValueBox().setSelectionRange(0, search.getValueBox().getText().length());
	}

	@Override
	public void clearSearch() {
		if (selected != null){
			selected.getElement().getStyle().setBackgroundColor(ColumnLabel.defaultColor);
		}
			
	}
	
	public SavedQueryLabel getPreviousQuery() {
		int widgetCnt = 0;
		previouslyLoadedQuery = null;
		for(int i = 0; i < previousQueryRoot.getItemCount(); i++) {
			for(int j = 0; j < previousQueryRoot.getItem(i).getChildCount(); j++) {
				for(int k = 0; k < previousQueryRoot.getItem(i).getChild(j).getChildCount(); k++) {
					for(int l = 0; l < previousQueryRoot.getItem(i).getChild(j).getChild(k).getChildCount(); l++) {
						if(widgetCnt >= backCnt) {
							backCnt++;
							previousQueryRoot.getItem(i).setState(true);
							previousQueryRoot.getItem(i).getChild(j).setState(true);
							previousQueryRoot.getItem(i).getChild(j).getChild(k).setState(true);
							if(previouslyLoadedQuery != null) {
								previouslyLoadedQuery.getElement().getStyle().setBackgroundColor("#FFFFFF");
							}
							previouslyLoadedQuery = previousQueryRoot.getItem(i).getChild(j).getChild(k).getChild(l).getWidget();
							previouslyLoadedQuery.getElement().getStyle().setBackgroundColor("#DDFF00");
							return (SavedQueryLabel) previousQueryRoot.getItem(i).getChild(j).getChild(k).getChild(l).getWidget();
						} else {
							widgetCnt++;
							previouslyLoadedQuery = previousQueryRoot.getItem(i).getChild(j).getChild(k).getChild(l).getWidget();
						}
					}
				}
			}
		}
		return null;
	}
	
	public void resetCnt() {
		if(previouslyLoadedQuery != null) {
			previouslyLoadedQuery.getElement().getStyle().setBackgroundColor("#FFFFFF");
		} else {
			if(previousQueryRoot.getItemCount() == 0) {
				return;
			}
			previousQueryRoot.getItem(0).getChild(0).getChild(0).getChild(0).getWidget().getElement().getStyle().setBackgroundColor("#FFFFFF");
		}
		backCnt = 0;
	}
	
	public String cleanHTML(String raw) {
		return raw.replaceAll( "&", "&amp;" ).replaceAll( "\"", 
				"&quot;" ).replaceAll( ">", "&gt;" ).replaceAll( "<", 
						"&lt;" ).replaceAll( "\n", "<br>" );
	}
	
}
