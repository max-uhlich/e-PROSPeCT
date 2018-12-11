package btaw.client.modules.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.psa.PSA_Presenter.View;
import btaw.client.view.widgets.ContextMenu;
import btaw.shared.model.query.column.Column;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DashboardTab extends Tab{
	
	//private List<HorizontalPanel> Panels = new ArrayList<HorizontalPanel>();
	//private List<Element> Divs = new ArrayList<Element>();
	private final List<String> all_modules = Arrays.asList("Race_Module", "Risk_Module", "Primary_Treatments_Module", "Drug_Therapies_Module", "Progression_Module", "Metastasization_Module","Biopsy_Module",
														   "Age_01_Module","Age_03_Module","T_Stage_Module","N_Stage_Module","M_Stage_Module","Path_Stage_Module","PSA_01_Module","PSA_03_Module",
														   "Gleason_01_Module","Gleason_03_Module");
	private List<JS_Module> Modules = new ArrayList<JS_Module>();
	
	private VerticalPanel vp;
	private FlexTable ft;
	private HashMap<String, String> valmap;
	private String fieldHeight = "15px";
	private String fieldWidth = "70px";
	private JavaScriptObject chart;
	int r = 0;
	int c = 0;
	
	//private final HeaderMenuSelectionController controller;
	//private ContextMenu contextMenu;
	
	public DashboardTab(HeaderMenuSelectionController controller){
		super();

		//this.controller = controller;
		
		ft = new FlexTable();
		ft.setStyleName("flexTable");
		ft.setCellSpacing(0);
		ft.setCellPadding(0);
		
		ScrollPanel sp = new ScrollPanel();
		sp.setSize("100%", "100%");
		
		vp = new VerticalPanel();
		vp.add(ft);
		sp.add(vp);
		this.getHPanel().add(sp);
		
		//Create all the module panels
		for (int i = 0; i<all_modules.size(); i++) {
			
			/*Element div = DOM.createDiv();
			HorizontalPanel panel = new HorizontalPanel();
			panel.getElement().appendChild(div);
			
			Divs.add(div);
			Panels.add(panel);*/
			
			Modules.add(new JS_Module(controller,all_modules.get(i)));
			
		}
	
	}
	
	private void setFlexCell(FlexTable table, int row, int col, String val, String style){
		table.setText(row, col, val);
		table.getFlexCellFormatter().setStyleName(row, col, style);
		if(style.equals("fieldValue"))
			table.getFlexCellFormatter().addStyleName(row, col, "rightField");
			table.getFlexCellFormatter().addStyleName(row, col, "topAlign");
		if(style.equals("summaryValue")){
			table.getColumnFormatter().setWidth(col, fieldWidth);
			table.getFlexCellFormatter().getElement(row, col).setAttribute("border", "0px solid #BBBBBB");
		}
	}
	
	public FlexTable constructJaggedTable(List<List<String>> jagged){
		
		FlexTable table = new FlexTable();
		table.setStyleName("flexTable");
		table.setCellSpacing(5);
		table.setCellPadding(3);

		String cur = "";
		String pre = "";
		String post = "";
		int max_row = 0;
		
		for (int j = 0; j<jagged.size(); j++){
        	List<String> r = jagged.get(j);
            for (int i = 0; i<r.size(); i++){
            	cur = r.get(i);
            	if(cur.length()>=2){
            		pre = cur.substring(0,2);
            		if(cur.length()>=3)
            			post = cur.substring(2);
            		else
            			post = "";
            		
            		if(pre.equals("h:")){
            			setFlexCell(table, j, i, post, "headerValue");
            		} else if (pre.equals("t:")){
            			setFlexCell(table, j, i, post, "fieldValue");
            		} else if (pre.equals("v:")){
            			if(post.contains("-77") || post.contains("null"))
            				post = "";
            			post = post.replace("$", ",");
            			setFlexCell(table, j, i, post, "summaryValue");
            		} else if (pre.equals("m:")){
            			
            			int index = all_modules.indexOf(post);
            			if (index > -1){
            				table.setWidget(j, i, Modules.get(index));
            				table.getFlexCellFormatter().addStyleName(j, i, "topAlign");
            			}
            			
            		}
            	}
            }
            if(r.size()>max_row)
            	max_row = r.size();
        }

		return table;
	}
	
	public void populateTableAndShow(List<List<String>> jagged) {

		ft.setWidget(0, 0, constructJaggedTable(jagged));
		ft.getFlexCellFormatter().addStyleName(0, 0, "topAlign");
		
	}
	
	public void addBarChart(String name, JsArrayString psa_vals, JsArrayString dates, JsArrayInteger size, JsArrayString jsTitle){

		int index = all_modules.indexOf(name);
		if (index > -1){
			Modules.get(index).setChart(createBarchart(Modules.get(index).getDiv(), psa_vals, dates, size, jsTitle));
		}

	}
	
	public void addHistogram(String name, JsArrayString vals, JsArrayInteger size, JsArrayString jsTitle, JsArrayInteger jsDim){
		int index = all_modules.indexOf(name);
		if (index > -1){
			Modules.get(index).setChart(createHistogram(Modules.get(index).getDiv(), vals, size, jsTitle, jsDim));
		}
		
	}
	
	public void addTable(String name, JsArrayString rows, JsArrayString cols, JsArrayString cells, JsArrayInteger size, JsArrayString jsTitle){
		int index = all_modules.indexOf(name);
		if (index > -1){
			Modules.get(index).setChart(createTable(Modules.get(index).getDiv(), rows, cols, cells, size, jsTitle),true);
		}

	}
	
	private native JavaScriptObject createBarchart(Element div, JsArrayString jsData, JsArrayString jsDates, JsArrayInteger jsSize, JsArrayString jsTitle)/*-{
		var chart = $wnd.d3_barchart(div, jsData, jsDates, jsSize, jsTitle);
		chart();
		return chart;
	}-*/;
	
	private native JavaScriptObject createHistogram(Element div, JsArrayString jsData, JsArrayInteger jsSize, JsArrayString jsTitle, JsArrayInteger jsDim)/*-{
		var chart = $wnd.d3_histogram(div, jsData, jsSize, jsTitle, jsDim);
		chart();
		return chart;
	}-*/;
	
	private native JavaScriptObject createTable(Element div, JsArrayString jsRows, JsArrayString jsCols, JsArrayString jsCells, JsArrayInteger jsSize, JsArrayString jsTitle)/*-{
		var chart = $wnd.d3_tabulate(div, jsRows, jsCols, jsCells, jsSize, jsTitle);
		chart();
		return chart;
	}-*/;

}
