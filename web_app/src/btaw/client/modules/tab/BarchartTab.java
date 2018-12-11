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

public class BarchartTab extends Tab {

	private Element PsaAggregateDiv;
	private HorizontalPanel PsaAggregatePanel;
	private VerticalPanel vp;
	private FlexTable ft;
	private JavaScriptObject chart;
	
	JS_Module JS_Mod;
	
	public BarchartTab(HeaderMenuSelectionController controller){
		super();
		
		ft = new FlexTable();
		ft.setStyleName("flexTable");
		ft.setCellSpacing(0);
		ft.setCellPadding(0);
		
		JS_Mod = new JS_Module(controller,"barchart");
		
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
	
	public void populateStudiesAndShow(JsArrayString psa_vals, JsArrayString dates, JsArrayInteger size, JsArrayString jsTitle) {
		//PsaAggregateDiv.removeAllChildren();
		chart = createBarchart(JS_Mod.getDiv(), psa_vals, dates, size, jsTitle);
		JS_Mod.setChart(chart);
	}
	
	private native JavaScriptObject createBarchart(Element div, JsArrayString jsData, JsArrayString jsDates, JsArrayInteger jsSize, JsArrayString jsTitle)/*-{
		var chart = $wnd.d3_barchart(div, jsData, jsDates, jsSize, jsTitle);
		chart();
		return chart;
	}-*/;
	
}
