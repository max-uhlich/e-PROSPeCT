package btaw.client.modules.synth;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.synth.SynthPresenter.View;
import btaw.shared.model.query.column.SynthTableColumn.Function;

public class SynthView extends AView implements View{
	private VerticalPanel panel = new VerticalPanel();
	PopupPanel popup = new PopupPanel();
	TextBox name = new TextBox();
	TextArea formula = new TextArea();
	Button cancel = new Button("Cancel");
	Button create = new Button("Create");
	ListBox functions = new ListBox();
	public SynthView(){
		this.initWidget(panel);
		popup.add(this);
		popup.setModal(true);
		HorizontalPanel function = new HorizontalPanel();
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.add(new Label("Column Name: "));
		namePanel.add(name);
		panel.add(namePanel);
		//panel.add(function);
		for(Function f: Function.values()){
			functions.addItem(f.toString());
		}
		function.add(new Label("Function: "));
		function.add(functions);
		
		HorizontalPanel formulaPanel = new HorizontalPanel();
		formulaPanel.add(new Label("Formula :"));
		formulaPanel.add(formula);
		panel.add(formulaPanel);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.add(cancel);
		buttons.add(create);
		panel.add(buttons);
	}
	@Override
	public Button getCancel() {
		return cancel;
	}
	@Override
	public Button getCreate() {
		return create;
	}
	@Override
	public String getFormula() {
		return formula.getText();
	}
	@Override
	public String getName() {
		return name.getText();
	}
	@Override
	public void close() {
		popup.hide();
	}
	@Override
	public void show() {
		popup.center();
	}
	@Override
	public Function getFunction() {
		return Function.valueOf(this.functions.getValue(functions.getSelectedIndex()));
	}

}
