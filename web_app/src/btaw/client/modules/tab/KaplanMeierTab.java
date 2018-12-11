package btaw.client.modules.tab;

import java.util.ArrayList;
import java.util.HashMap;

import btaw.client.event.controllers.PreviewDropEventController;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.IntervalTableColumn;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KaplanMeierTab extends Tab{

	private Image Im;
	private VerticalPanel PStats;
	
	final private Element PSA_div;
	private Element Summary_div;
	private VerticalPanel vp;
	private HorizontalPanel PSA_panel;
	private FlexTable ft;
	private HashMap<String, String> valmap;
	private String fieldHeight = "15px";
	private String fieldWidth = "70px";
	private JavaScriptObject chart;
	private JsArrayString jsLineNames = (JsArrayString)JavaScriptObject.createArray().cast();
	
	private final PreviewDropEventController startDropController;
	private final PreviewDropEventController endDropController;
	private final PreviewDropEventController censDropController;
	private final PickupDragController dragController;
	
	private Label start_date;
	private Label end_date;
	private Label censor_date;
	private final Button recompute;
	
	private Column start_col;
	private Column end_col;
	private Column censor_col;
	
	public KaplanMeierTab(PickupDragController dragController){
		super();
		this.Im = new Image();
		this.PStats = new VerticalPanel();
		
		this.start_date = new Label("Start Date: Biopsy date");
		this.start_date.getElement().getStyle().setMarginTop(30, Unit.PX);
		this.start_date.getElement().getStyle().setMarginBottom(15, Unit.PX);
		this.start_date.getElement().getStyle().setColor("rgba(0, 0, 0, 0.6)");
		this.start_date.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		this.start_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.2)");
		this.end_date = new Label("End Date: Failure Date");
		//this.end_date.getElement().getStyle().setMarginTop(15, Unit.PX);
		this.end_date.getElement().getStyle().setMarginBottom(15, Unit.PX);
		this.end_date.getElement().getStyle().setColor("rgba(0, 0, 0, 0.6)");
		this.end_date.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		this.end_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.2)");
		this.censor_date = new Label("Censor Date: Last Contact Date");
		//this.censor_date.getElement().getStyle().setMarginTop(15, Unit.PX);
		this.censor_date.getElement().getStyle().setMarginBottom(15, Unit.PX);
		this.censor_date.getElement().getStyle().setColor("rgba(0, 0, 0, 0.6)");
		this.censor_date.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		this.censor_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.2)");
		
		this.recompute = new Button("Recompute");
		this.recompute.getElement().getStyle().setMarginBottom(15, Unit.PX);
		
		this.dragController = dragController;
		this.startDropController = new PreviewDropEventController(start_date);
		this.endDropController = new PreviewDropEventController(end_date);
		this.censDropController = new PreviewDropEventController(censor_date);
		dragController.registerDropController(startDropController);
		dragController.registerDropController(endDropController);
		dragController.registerDropController(censDropController);

		ft = new FlexTable();
		ft.setStyleName("flexTable");
		ft.setCellSpacing(0);
		ft.setCellPadding(0);
		
		PSA_div = DOM.createDiv();
		//Summary_div = DOM.createDiv();
		
		ScrollPanel sp = new ScrollPanel();
		sp.setSize("100%", "100%");
		
		//HorizontalPanel Summary_panel = new HorizontalPanel();
		//Summary_panel.getElement().appendChild(Summary_div);
		
		PSA_panel = new HorizontalPanel();
		PSA_panel.getElement().appendChild(PSA_div);
		
		VerticalPanel label_panel = new VerticalPanel();
		label_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		label_panel.add(start_date);
		label_panel.add(end_date);
		label_panel.add(censor_date);
		label_panel.add(recompute);
		label_panel.add(PStats);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(label_panel);
		hp.add(PSA_panel);
		
		vp = new VerticalPanel();
		//vp.add(Summary_panel);
		vp.add(ft);
		vp.add(hp);
		//vp.add(this.Im);
		sp.add(vp);
		this.getHPanel().add(sp);
		
	}
	
	public void populateStudiesAndShow(JsArray<JsArrayString> jslineX, JsArray<JsArrayString> jslineY, JsArray<JsArrayString> jscensX, JsArray<JsArrayString> jscensY,JsArray<JsArrayString> jsAtRisk, JsArrayInteger jsSize, JsArrayString jsLineNames, JsArrayInteger jsN) {
		this.jsLineNames = jsLineNames;
		PSA_div.removeAllChildren();
		chart = createKM(PSA_div, jslineX, jslineY, jscensX, jscensY, jsAtRisk, jsSize, jsLineNames, jsN);
	}
	
	public JsArrayString getNames(){
		return this.jsLineNames;
	}
	
	public VerticalPanel getPStats(){
		return this.PStats;
	}
	
	public void setURL(String url){
		this.Im.setUrl(url);
	}
	
	public Image getIm(){
		return this.Im;
	}
	
	public Column getStartColumn(){
		return this.start_col;
	}
	public Column getEndColumn(){
		return this.end_col;
	}
	public Column getCensorColumn(){
		return this.censor_col;
	}
	
	public void setStartColumn(Column c){
		this.start_date.setText("Start Date: " + c.getName());
		this.start_date.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.start_date.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.start_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		this.start_col = c;
	}
	public void setEndColumn(Column c){
		this.end_date.setText("End Date: " + c.getName());
		this.end_date.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.end_date.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.end_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		this.end_col = c;
	}
	public void setCensorColumn(Column c){
		this.censor_date.setText("Censor Date: " + c.getName());
		this.censor_date.getElement().getStyle().setColor("rgba(0, 0, 0, 1)");
		this.censor_date.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		this.censor_date.getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0)");
		this.censor_col = c;
	}
	public PreviewDropEventController getStartDropController() {
		return this.startDropController;
	}
	public PreviewDropEventController getEndDropController() {
		return this.endDropController;
	}
	public PreviewDropEventController getCensorDropController() {
		return this.censDropController;
	}
	
	public Button getRecomputeButton() {
		return this.recompute;
	}
	
	public void unregisterDropControllers() {
		this.dragController.unregisterDropController(startDropController);
		this.dragController.unregisterDropController(endDropController);
		this.dragController.unregisterDropController(censDropController);
	}
	
	private native JavaScriptObject createKM(Element div, JsArray<JsArrayString> jslineX, JsArray<JsArrayString> jslineY, JsArray<JsArrayString> jscensX, JsArray<JsArrayString> jscensY, JsArray<JsArrayString> jsAtRisk, JsArrayInteger jsSize, JsArrayString jsLineNames, JsArrayInteger jsN)/*-{
		var chart = $wnd.d3_kmchart(div, jslineX, jslineY, jscensX, jscensY, jsAtRisk, jsSize, jsLineNames, jsN);
		chart();
		return chart;
	}-*/;
	
}
