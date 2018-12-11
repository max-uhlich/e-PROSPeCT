package btaw.client.modules.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.function.FunctionPresenter.View;
import btaw.shared.model.DefinitionData;
import btaw.shared.model.query.column.BooleanTableColumn;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.DateTableColumn;
import btaw.shared.model.query.column.StringTableColumn;

public class FunctionView extends AView implements View {

	public enum Function { KAPLANMEIER, STATISTICAL, ARITHMETIC };
	/* ORDER MATTERS FOR ARITHMETIC */
	public enum Arithmetic { NONE, COUNT, MAX, MIN, STDDEV, SUM, AVG };
	private DialogBox main;
	private VerticalPanel containerPanel;
	private Button done;
	private Button cancel;
	private ListBox functionTypeLB;
	private ListBox functionAllLB;
	private VerticalPanel definitionContainerPanel;
	private HorizontalPanel setAllFunctions;
	private HashMap<String, HashMap<Column, ListBox>> arithmeticPanelData;
	private TextBox functionName;
	
	public FunctionView () {
		arithmeticPanelData = new HashMap<String, HashMap<Column, ListBox>>();
		main = new DialogBox();
		containerPanel = new VerticalPanel();
		containerPanel.setSize("100%", "100%");
		definitionContainerPanel = new VerticalPanel();
		definitionContainerPanel.setSize("100%", "100%");
		
		final HorizontalPanel bottom = new HorizontalPanel();
		done = new Button("Done");
		cancel = new Button("Cancel");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel bottomAlignPanel = new HorizontalPanel();
		bottomAlignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottomAlignPanel.add(done);
		bottomAlignPanel.add(cancel);
		bottom.add(bottomAlignPanel);
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(5);
		this.functionName = new TextBox();
		namePanel.setWidth("100%");
		namePanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		namePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		namePanel.add(new Label("Please Enter Function Name:"));
		namePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		namePanel.add(functionName);

		HorizontalPanel top = new HorizontalPanel();
		functionTypeLB = new ListBox();
		functionTypeLB.addItem("Arithmetic");
		functionTypeLB.addItem("Kaplan-Meier Plot");
		functionTypeLB.addItem("Kaplan-Meier Test of Significance");
		top.setWidth("100%");
		top.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		top.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		top.add(new Label("Function Type:"));
		top.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		top.add(functionTypeLB);
		
		setAllFunctions = new HorizontalPanel();
		functionAllLB = new ListBox();
		for(Arithmetic a : Arithmetic.values()) {
			functionAllLB.addItem(a.name());
		}
		setAllFunctions.setWidth("100%");
		setAllFunctions.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		setAllFunctions.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		setAllFunctions.add(new Label("Set All Functions: "));
		setAllFunctions.add(functionAllLB);
		
		HTML verticalLine = new HTML("<hr>");
		verticalLine.setWidth("100%");
		
		ScrollPanel definitionContainerSP = new ScrollPanel(definitionContainerPanel);
		definitionContainerSP.setHeight("500px");
		
		containerPanel.add(namePanel);
		containerPanel.add(top);
		containerPanel.add(setAllFunctions);
		containerPanel.add(verticalLine);
		containerPanel.add(definitionContainerSP);
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("Add Function");
		main.setModal(true);
		main.center();
	}
	
	public void buildDataPresentation(ArrayList<DefinitionData> data) {
		setAllFunctions.setVisible(true);
		definitionContainerPanel.clear();
		arithmeticPanelData.clear();
		for(DefinitionData d : data) {
			String def = d.getDefName();
			ArrayList<Column> cols = d.getCols();
			HashMap<Column, ListBox> currentDefCols = new HashMap<Column, ListBox>();
			
			definitionContainerPanel.add(new Label("Definition: "+def));
			for(Column col : cols) {
				HorizontalPanel inner = new HorizontalPanel();
				inner.setSpacing(5);
				inner.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
				inner.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
				inner.getElement().getStyle().setMarginLeft(25, Unit.PX);
				ListBox functions = new ListBox();
				functions.setWidth("90px");
				for(Arithmetic a : Arithmetic.values()) {
					if(col instanceof BooleanTableColumn) {
						/* must stop at count */
						if(a.equals(Arithmetic.MAX))
							break;
					} else if (col instanceof DateTableColumn
							|| col instanceof StringTableColumn) {
						/* must stop at min/max */
						if(a.equals(Arithmetic.STDDEV))
							break;
					}
					functions.addItem(a.name());
				}
				inner.add(functions);
				inner.add(new Label("of"));
				inner.add(new Label(col.getName()));
				definitionContainerPanel.add(inner);
				currentDefCols.put(col, functions);
			}
			
			arithmeticPanelData.put(def, currentDefCols);
		}
	}
	
	public void buildStatisticPresentation(ArrayList<DefinitionData> data) {
		setAllFunctions.setVisible(false);
		definitionContainerPanel.clear();
		for(DefinitionData d : data) {
			definitionContainerPanel.add(new Label("Definition: "+d.getDefName()));
		}
	}
	
	public ListBox getFunctionTypeLB() {
		return this.functionTypeLB;
	}
	
	public ListBox getFunctionAllLB() {
		return this.functionAllLB;
	}
	
	public void setAllFunctions(int index) {
		for(Map.Entry<String, HashMap<Column, ListBox>> def : arithmeticPanelData.entrySet()){
			for(Map.Entry<Column, ListBox> col : def.getValue().entrySet()) {
				if( col.getValue().getItemCount() <= index ) {
					break;
				}
				col.getValue().setSelectedIndex(index);
			}
		}
	}
	
	public Button getDoneButton() {
		return this.done;
	}
	public Button getCancelButton() {
		return this.cancel;
	}
	public void close() {
		this.main.hide();
	}
	public TextBox getFunctionName() {
		return this.functionName;
	}
}
