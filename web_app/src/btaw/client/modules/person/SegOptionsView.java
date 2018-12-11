package btaw.client.modules.person;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.person.SegOptionsPresenter.View;

public class SegOptionsView extends AView implements View {

	private DialogBox main;
	private VerticalPanel innerPanel;
	private VerticalPanel patientPanel;
	private VerticalPanel userPanel;
	private LinkedHashMap<String, Integer> segmentations;
	private Button close;
	
	public SegOptionsView() {
		main = new DialogBox();
		innerPanel = new VerticalPanel();
		patientPanel = new VerticalPanel();
		userPanel = new VerticalPanel();
		
		Label patientSegL = new Label("Patient Segmentations");
		patientSegL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		Label userDefL = new Label("User Defined");
		userDefL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		close = new Button("Close");
		
		innerPanel.add(patientSegL);
		innerPanel.add(new HTML("<hr />"));
		innerPanel.add(patientPanel);
		innerPanel.add(new HTML("<br>"));
		innerPanel.add(userDefL);
		innerPanel.add(new HTML("<hr />"));
		innerPanel.add(userPanel);
		
		HorizontalPanel bottom = new HorizontalPanel();
		close = new Button("Close");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(close);
		
		innerPanel.add(bottom);
		
		main.add(innerPanel);
		
		main.setText("Display Segmentations");
		main.center();
		main.setModal(false);
		main.getElement().getStyle().setProperty("width", "auto");
	}
	
	public LinkedHashMap<Integer, ListBox> populateSegmentations(LinkedHashMap<String, Integer> segmentations) {
		this.segmentations = segmentations;
		
		LinkedHashMap<Integer, ListBox> segListBox = new LinkedHashMap<Integer, ListBox>();
		
		for(Map.Entry<String, Integer> entry : segmentations.entrySet()) {
			HorizontalPanel hp = new HorizontalPanel();
			ListBox options = new ListBox();
			Label segL = new Label(entry.getKey());
			segL.setStyleName("center-Label");
			options.addItem("Do Not Display");
			options.addItem("Fill");
			options.addItem("Outline");
			hp.setWidth("100%");
			hp.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
			hp.add(segL);
			hp.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
			hp.add(options);
			patientPanel.add(hp);
			segListBox.put(entry.getValue(), options);
		}
		
		return segListBox;
	}
	
	public LinkedHashMap<Integer, ListBox> populateUserSegmentations(LinkedHashMap<Date, Integer> userSegmentations) {
		if(userSegmentations == null) {
			userPanel.add(new Label("None Found."));
			return null;
		}
		
		LinkedHashMap<Integer, ListBox> segListBox = new LinkedHashMap<Integer, ListBox>();
		
		DateTimeFormat df = DateTimeFormat.getFormat("yyyy-MM-dd");
		for(Map.Entry<Date, Integer> entry : userSegmentations.entrySet()) {
			HorizontalPanel hp = new HorizontalPanel();
			ListBox options = new ListBox();
			Label segL = new Label(df.format(entry.getKey()));
			segL.setStyleName("center-Label");
			options.addItem("Do Not Display");
			options.addItem("Fill");
			options.addItem("Outline");
			hp.setWidth("100%");
			hp.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
			hp.add(segL);
			hp.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
			hp.add(options);
			userPanel.add(hp);
			segListBox.put(entry.getValue(), options);
		}
		
		return segListBox;
	}
	
	public Button getCloseButton() {
		return this.close;
	}
	
	public void close() {
		this.main.hide();
	}
}
