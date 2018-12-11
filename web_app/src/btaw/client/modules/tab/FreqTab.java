package btaw.client.modules.tab;

import btaw.client.event.controllers.HeaderMenuSelectionController;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FreqTab extends Tab {

	private Element PsaAggregateDiv;
	private HorizontalPanel PsaAggregatePanel;
	private VerticalPanel vp;
	private FlexTable ft;
	private JavaScriptObject chart;
	
	JS_Module JS_Mod;
	
	public FreqTab(HeaderMenuSelectionController controller){
		super();
		
		ft = new FlexTable();
		ft.setStyleName("flexTable");
		ft.setCellSpacing(0);
		ft.setCellPadding(0);
		
		JS_Mod = new JS_Module(controller,"frequency_chart");
		
		//PsaAggregateDiv = DOM.createDiv();

		ScrollPanel sp = new ScrollPanel();
		sp.setSize("100%", "100%");
		
		PsaAggregatePanel = new HorizontalPanel();
		//PsaAggregatePanel.getElement().appendChild(PsaAggregateDiv);
		//PsaAggregatePanel.getElement().appendChild(JS_Mod.getDiv());
		
		ft.setWidget(0, 0, JS_Mod);
		//ft.getFlexCellFormatter().addStyleName(0, 0, "topAlign");
		//ft.getElement().setAttribute("cellpadding","20px");
		
		VerticalPanel label_panel = new VerticalPanel();
		label_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		HorizontalPanel hp = new HorizontalPanel();
		//hp.getElement().setAttribute("cellpadding","20px");
		hp.add(label_panel);
		hp.add(PsaAggregatePanel);

		vp = new VerticalPanel();
		vp.getElement().setAttribute("cellpadding","20px");
		vp.add(ft);
		vp.add(hp);
		sp.add(vp);
		this.getHPanel().add(sp);
		
	}
	
	public void populateStudiesAndShow(JsArrayString vals, JsArrayInteger size, JsArrayString jsTitle, JsArrayInteger jsDim) {
		//PsaAggregateDiv.removeAllChildren();
		chart = createHistogram(JS_Mod.getDiv(), vals, size, jsTitle, jsDim);
		JS_Mod.setChart(chart);
	}
	
	private native JavaScriptObject createHistogram(Element div, JsArrayString jsData, JsArrayInteger jsSize, JsArrayString jsTitle, JsArrayInteger jsDim)/*-{
		var chart = $wnd.d3_histogram(div, jsData, jsSize, jsTitle, jsDim);
		chart();
		return chart;
	}-*/;
	
}
