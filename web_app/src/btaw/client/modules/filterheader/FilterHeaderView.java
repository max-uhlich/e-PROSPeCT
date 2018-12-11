package btaw.client.modules.filterheader;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.filterheader.FilterHeaderPresenter.View;

public class FilterHeaderView extends AView implements View {

	private DialogBox main;
	private VerticalPanel containerVP;
	private VerticalPanel filtersVP;
	private ArrayList<HorizontalPanel> filterHPs;
	
	public enum OpsIntDate {EQUALITY, INEQUALITY, LESSTHAN, GREATERTHAN};
	public enum OpsStrBool {EQUALITY, INEQUALITY};
	public enum OpsFloat {LESSTHAN, GREATERTHAN};
	public enum FilterType {INTORDATE, STRORBOOL, FLOAT};
	
	private FilterType currentType;
	private String colHeader;
	private Button addRow;
	private Button cancel;
	private Button ok;
	
	public FilterHeaderView(FilterType currentType, String colHeader) {
		main = new DialogBox();
		containerVP = new VerticalPanel();
		filtersVP = new VerticalPanel();
		addRow = new Button("+");
		filtersVP.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		this.currentType = currentType;
		this.colHeader = colHeader;
		this.filterHPs = new ArrayList<HorizontalPanel>();
		containerVP.setWidth("100%");
		containerVP.add(new Label("What would you like to filter out?"));
		containerVP.add(filtersVP);
		containerVP.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		containerVP.add(addRow);
		addFilterRow(true);
		
		HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("cancel");
		ok = new Button("ok");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(ok);
		alignPanel.add(cancel);
		containerVP.add(bottom);
		
		main.setText("Column Filter");
		main.add(containerVP);
		main.setModal(true);
		main.center();
		main.getElement().getStyle().setZIndex(3);
	}
	
	public void addFilterRow(boolean isFirst) {
		HorizontalPanel currentFilterRow = new HorizontalPanel();
		ListBox operations = new ListBox();
		Label colLabel = new Label(colHeader);
		TextBox tb = new TextBox();
		if(!isFirst) {
			this.filtersVP.add(new Label("And"));
		}
		if(currentType.equals(FilterType.FLOAT)) {
			for(OpsFloat val : OpsFloat.values()) {
				if(val.equals(OpsFloat.LESSTHAN)) {
					operations.addItem("<");
				} else if (val.equals(OpsFloat.GREATERTHAN)) {
					operations.addItem(">");
				}
			}
		} else if (currentType.equals(FilterType.INTORDATE)) {
			for(OpsIntDate val : OpsIntDate.values()) {
				if(val.equals(OpsIntDate.EQUALITY)) {
					operations.addItem("=");
				} else if(val.equals(OpsIntDate.INEQUALITY)) {
					operations.addItem("\u2260");
				} else if(val.equals(OpsIntDate.GREATERTHAN)) {
					operations.addItem(">");
				} else if(val.equals(OpsIntDate.LESSTHAN)) {
					operations.addItem("<");
				}
			}
		} else if (currentType.equals(FilterType.STRORBOOL)) {
			for(OpsStrBool val : OpsStrBool.values()) {
				if(val.equals(OpsStrBool.EQUALITY)) {
					operations.addItem("=");
				} else if(val.equals(OpsStrBool.INEQUALITY)) {
					operations.addItem("\u2260");
				}
			}
		}
		operations.addItem("Is Null");
		currentFilterRow.add(colLabel);
		currentFilterRow.add(operations);
		currentFilterRow.add(tb);
		
		this.filterHPs.add(currentFilterRow);
		filtersVP.add(currentFilterRow);
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	public Button getOkButton() {
		return this.ok;
	}
	public Button getAddButton() {
		return this.addRow;
	}
	public DialogBox getMain() {
		return this.main;
	}
	public ArrayList<HorizontalPanel> getFilters() {
		return this.filterHPs;
	}
	public FilterType getCurrentType() {
		return currentType;
	}
}
