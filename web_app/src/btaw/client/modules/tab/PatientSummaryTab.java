package btaw.client.modules.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.psa.PSA_Presenter.View;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class PatientSummaryTab extends Tab {
	
	final private Element PSA_div;
	private Element Summary_div;
	private VerticalPanel vp;
	private HorizontalPanel PSA_panel;
	private FlexTable ft;
	private FlexTable eventsTable;
	private String pid;
	private int number;
	private HashMap<String, String> valmap;
	private String fieldHeight = "15px";
	private String fieldWidth = "70px";
	private JavaScriptObject chart;
	
	public PatientSummaryTab(String pid, int number){
		super();

		this.pid = pid;
		this.number = number;
		
		ft = new FlexTable();
		ft.setStyleName("flexTable");
		ft.setCellSpacing(0);
		ft.setCellPadding(0);
		
		eventsTable = new FlexTable();
		
		PSA_div = DOM.createDiv();
		
		ScrollPanel sp = new ScrollPanel();
		sp.setSize("100%", "100%");
		
		PSA_panel = new HorizontalPanel();
		PSA_panel.getElement().appendChild(PSA_div);
		
		vp = new VerticalPanel();
		vp.add(ft);
		sp.add(vp);
		this.getHPanel().add(sp);
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public void populateStudiesAndShow(JsArrayString psa_vals, JsArrayString dates, JsArrayString implant, JsArrayInteger size) {
		JsArrayString jsNumber = (JsArrayString)JavaScriptObject.createArray().cast();
		jsNumber.push(String.valueOf(number));
		chart = createPSAGraph(PSA_div, psa_vals, dates, implant, size, jsNumber);
	}
	
	private void setFlexCell(FlexTable table, int row, int col, String val, String style){
		table.setText(row, col, val);
		table.getFlexCellFormatter().setStyleName(row, col, style);
		if(style.equals("fieldValue"))
			table.getFlexCellFormatter().addStyleName(row, col, "rightField");
		if(style.equals("summaryValue"))
			table.getColumnFormatter().setWidth(col, fieldWidth);
	}
	
	public CheckBox addEventRow(FlexTable table, int row, String rowName, String date, final String eventCode, boolean checked){
		setFlexCell(table, row, 0, date, "summaryValue");
		setFlexCell(table, row, 1, rowName, "summaryValue");

		CheckBox cb = new CheckBox();
		cb.setValue(checked);
		table.setWidget(row, 2, cb);
		
		return cb;
	}
	
	public ClickHandler addEventClickHandler(final String lineName, final String date, final String eventCode){
		ClickHandler ch = new ClickHandler() {
		      @Override
		      public void onClick(ClickEvent event) {
		    	  boolean checked = ((CheckBox) event.getSource()).getValue();
		    	  JsArrayString jsName = (JsArrayString)JavaScriptObject.createArray().cast();
		    	  JsArrayString jsDate = (JsArrayString)JavaScriptObject.createArray().cast();
		    	  JsArrayString jsLine = (JsArrayString)JavaScriptObject.createArray().cast();
		    	  JsArrayString jsNumber = (JsArrayString)JavaScriptObject.createArray().cast();
		    	  JsArrayString jsToggle = (JsArrayString)JavaScriptObject.createArray().cast();
		    	  jsName.push(lineName);
		    	  jsDate.push(date);
		    	  jsLine.push(eventCode);
		    	  jsNumber.push(String.valueOf(number));
		    	  jsToggle.push(String.valueOf(checked));
		    	  drawEvent(chart,jsName,jsDate,jsLine,jsNumber,jsToggle);
		      }
		    };
		    
		    return ch;
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
            		}
            	}
            }
            if(r.size()>max_row)
            	max_row = r.size();
        }


		return table;
	}
	
	public void populateEventsAndShow(List<List<String>> jagged) {
		
		eventsTable.setStyleName("flexTable");
		eventsTable.setCellSpacing(5);
		eventsTable.setCellPadding(3);

		int row = 0;
		CheckBox cb;

		String date = "";
		String event = "";
		String details = "";
		for (int j = 0; j<jagged.size(); j++){
        	List<String> r = jagged.get(j);

        	date = r.get(0);
        	event = r.get(1);
        	if(r.size()>=3)
        		details = r.get(2);
        	
        	cb = new CheckBox();
			cb = addEventRow(eventsTable, row, event, date, "as_001", false);
			cb.addClickHandler(addEventClickHandler(details, date, "as_001"));
			row = row+1;
        	
		}
	
		eventsTable.getColumnFormatter().setWidth(1, "200px");

	}
	
	public void populateTableAndShow(List<List<String>> jagged) {

		ft.setWidget(0, 0, constructJaggedTable(jagged));
		
		ft.setWidget(1, 0, PSA_panel);
		
		VerticalPanel events_all = new VerticalPanel();
		
		FlexTable eventsHeader = new FlexTable();
		
		eventsHeader.setStyleName("flexTable");
		eventsHeader.setCellSpacing(5);
		eventsHeader.setCellPadding(3);

		setFlexCell(eventsHeader, 0, 0, "Events", "headerValue");
		eventsHeader.getColumnFormatter().setWidth(0, fieldWidth);

		ScrollPanel sp = new ScrollPanel();
		sp.add(eventsTable);
		sp.setSize("400px", "280px");
		
		events_all.add(eventsHeader);
		events_all.add(sp);
		
		ft.setWidget(1, 1, events_all);
		
		ft.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		ft.getFlexCellFormatter().addStyleName(1, 1, "topAlign");
		ft.getFlexCellFormatter().addStyleName(0, 0, "topAlign");
		ft.getFlexCellFormatter().addStyleName(0, 1, "topAlign");
		ft.getFlexCellFormatter().addStyleName(1, 0, "topAlign");
		ft.getFlexCellFormatter().addStyleName(1, 1, "topAlign");
		
	}

	private native void drawEvent(JavaScriptObject chart, JsArrayString jsName, JsArrayString jsDate, JsArrayString jsLine, JsArrayString jsNumber, JsArrayString jsToggle)/*-{
		chart.drawEvent(jsName, jsDate, jsLine, jsNumber, jsToggle);
	}-*/;
	
	private native JavaScriptObject createPSAGraph(Element div, JsArrayString jsData, JsArrayString jsDates, JsArrayString jsImplant, JsArrayInteger jsSize, JsArrayString jsNumber)/*-{
		var chart = $wnd.d3_psa_graph(div, jsData, jsDates, jsImplant, jsSize, jsNumber);
		chart();
		return chart;
	}-*/;

}
