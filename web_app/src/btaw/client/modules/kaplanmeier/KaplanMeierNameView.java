package btaw.client.modules.kaplanmeier;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.kaplanmeier.KaplanMeierNamePresenter.View;

public class KaplanMeierNameView extends AView implements View {

	public enum Function {  KAPLANMEIER, SUM, MAX, MIN, AVG, COUNT };
	private DialogBox main;
	private VerticalPanel containerPanel;
	private VerticalPanel displayPanel;
	private Button cancel;
	private Button done;
	private TextBox name;
	private ListBox operator;
	private ListBox columnNames;
	private Label ofL;
	private Label nameL;
	
	public KaplanMeierNameView() {
		main = new DialogBox();
		containerPanel = new VerticalPanel();
		containerPanel.setWidth("100%");
		containerPanel.setHeight("100%");
		displayPanel = new VerticalPanel();
		
		final HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("Cancel");
		done = new Button("Execute Query");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		alignPanel.add(done);
		alignPanel.add(cancel);
		bottom.add(alignPanel);
		
		name = new TextBox();
		name.setWidth("100%");
		nameL = new Label("Please enter group name, or click done for generic name:");
		nameL.setWordWrap(true);
		Label nameL2 = new Label("or click done for generic name:");
		nameL.setStyleName("center-Label");
		nameL2.setStyleName("center-Label");
		
		HorizontalPanel functionPanel = new HorizontalPanel();
		functionPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		functionPanel.setWidth("100%");
		operator = new ListBox();
		columnNames = new ListBox();
		ofL = new Label("of");
		ofL.setStyleName("center-Label");
		functionPanel.add(operator);
		functionPanel.add(ofL);
		functionPanel.add(columnNames);

		for(Function op : Function.values()) {
			operator.addItem(op.toString());
		}
		
		columnNames.addItem("Group");
		
		displayPanel.setSpacing(5);
		displayPanel.add(functionPanel);
		displayPanel.add(nameL);
		//displayPanel.add(nameL2);
		displayPanel.add(name);
		
		containerPanel.setSpacing(5);
		containerPanel.add(displayPanel);
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("Please Name Group");
		main.setModal(true);
		main.center();
		
		name.setFocus(true);
	}
	
	public ListBox getOperatorLB() {
		return this.operator;
	}
	
	public ListBox getColNamesLB() {
		return this.columnNames;
	}
	
	public Label getNameL() {
		return this.nameL;
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	
	public Button getDoneButton() {
		return this.done;
	}
	
	public void close() {
		main.hide();
	}
	
	public String getName() {
		return this.name.getText();
	}
	
	public void clearName() {
		this.name.setText("");
	}
	
	public String getSelectedColumnName() {
		return this.columnNames.getItemText(this.columnNames.getSelectedIndex());
	}
	
	public String getSelectedFunctionName() {
		return this.operator.getItemText(this.operator.getSelectedIndex());
	}
}
