package btaw.client.modules.function;

import java.util.ArrayList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;
import btaw.shared.model.DefinitionData;
import btaw.shared.model.FunctionData;

public class FunctionPresenter extends Presenter {

	public interface View extends IView {
		void buildDataPresentation(ArrayList<DefinitionData> data);
		void buildStatisticPresentation(ArrayList<DefinitionData> data);
		ListBox getFunctionTypeLB();
		ListBox getFunctionAllLB();
		void setAllFunctions(int index);
		Button getDoneButton();
		Button getCancelButton();
		void close();
		TextBox getFunctionName();
	}
	
	private final View ui;
	private ArrayList<DefinitionData> data;
	
	public FunctionPresenter(Presenter parent, View ui, ArrayList<DefinitionData> data) {
		super(parent);
		this.ui = ui;
		this.data = data;
	}
	
	@Override
	protected void bindAll() {
		ui.buildDataPresentation(data);
		ui.getFunctionTypeLB().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(ui.getFunctionTypeLB().getItemText(ui.getFunctionTypeLB().getSelectedIndex()).equals("Arithmetic")) {
					ui.buildDataPresentation(data);
				} else {
					ui.buildStatisticPresentation(data);
				}
			}
		});
		ui.getFunctionAllLB().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ui.setAllFunctions(ui.getFunctionAllLB().getSelectedIndex());
			}
		});
		ui.getDoneButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(ui.getFunctionName().getText().equals("")) {
					ui.getFunctionName().getElement().getStyle().setBackgroundColor("#FFAAAA");
					return;
				}
				FunctionData fd = new FunctionData(ui.getFunctionName().getText(), ui.getFunctionTypeLB().getItemText(ui.getFunctionTypeLB().getSelectedIndex()));
				fd.setDefData(data);
				FilterPresenter.get().addFunction(fd);
				ui.close();
			}
		});
		ui.getCancelButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.close();
			}
		});
	}

	@Override
	protected IView getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void activated(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}
}
