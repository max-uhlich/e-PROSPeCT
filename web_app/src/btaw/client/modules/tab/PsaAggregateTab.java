package btaw.client.modules.tab;

import btaw.client.event.controllers.PreviewDropEventController;
import btaw.shared.model.query.column.Column;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PsaAggregateTab extends Tab {

	private Element PsaAggregateDiv;
	private HorizontalPanel PsaAggregatePanel;
	private VerticalPanel vp;
	private final PreviewDropEventController centerDropController;
	private final PickupDragController dragController;
	private FlexTable ft;
	private JavaScriptObject chart;
	
	private Label center_date;
	private final Button recompute;
	
	private Column center_col;
	
	public PsaAggregateTab(PickupDragController dragController){
		super();
		
		this.center_date = new Label("Center Date: Biopsy date");
		this.center_date.getElement().getStyle().setMarginTop(30, Unit.PX);
		this.center_date.getElement().getStyle().setMarginBottom(15, Unit.PX);
		this.center_date.getElement().getStyle().setColor("rgba(0, 0, 0, 0.6)");
		this.center_date.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		this.center_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.2)");
		
		this.recompute = new Button("Recompute");
		this.recompute.getElement().getStyle().setMarginBottom(15, Unit.PX);
		
		this.dragController = dragController;
		this.centerDropController = new PreviewDropEventController(center_date);
		dragController.registerDropController(centerDropController);

		ft = new FlexTable();
		ft.setStyleName("flexTable");
		ft.setCellSpacing(0);
		ft.setCellPadding(0);
		
		PsaAggregateDiv = DOM.createDiv();
		
		ScrollPanel sp = new ScrollPanel();
		sp.setSize("100%", "100%");
		
		PsaAggregatePanel = new HorizontalPanel();
		PsaAggregatePanel.getElement().appendChild(PsaAggregateDiv);
		
		VerticalPanel label_panel = new VerticalPanel();
		label_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		label_panel.add(center_date);
		label_panel.add(recompute);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(label_panel);
		hp.add(PsaAggregatePanel);
		
		//sp.add(PsaAggregatePanel);
		//this.getHPanel().add(sp);
		
		vp = new VerticalPanel();
		vp.add(ft);
		vp.add(hp);
		sp.add(vp);
		this.getHPanel().add(sp);
		
	}
	
	public void populateStudiesAndShow(JsArrayString jsPIDs, JsArray<JsArrayString> jsPsaLists, JsArray<JsArrayString> jsDateLists, JsArrayString jsImplantDates, JsArrayString jsSelPID, JsArrayInteger jsSize) {
		PsaAggregateDiv.removeAllChildren();
		chart = createAggregate(PsaAggregateDiv, jsPIDs, jsPsaLists, jsDateLists, jsImplantDates, jsSelPID, jsSize);
	}
	
	private native JavaScriptObject createAggregate(Element div, JsArrayString jsPIDs, JsArray<JsArrayString> jsPsaLists, JsArray<JsArrayString> jsDateLists, JsArrayString jsImplantDates, JsArrayString jsSelPID, JsArrayInteger jsSize)/*-{
		var chart = $wnd.d3_aggregate(div, jsPIDs, jsPsaLists, jsDateLists, jsImplantDates, jsSelPID, jsSize);
		chart();
		return chart;
	}-*/;
	
	public void setCenterColumn(Column c){
		this.center_date.setText("Center Date: " + c.getName());
		this.center_date.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.center_date.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.center_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		this.center_col = c;
	}
	
	public Button getRecomputeButton() {
		return this.recompute;
	}
	
	public Column getCenterColumn() {
		return this.center_col;
	}
	
	public void unregisterDropControllers() {
		this.dragController.unregisterDropController(centerDropController);
	}
	
	public PreviewDropEventController getCenterDropController() {
		return this.centerDropController;
	}
	
}
