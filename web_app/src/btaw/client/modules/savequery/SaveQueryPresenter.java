package btaw.client.modules.savequery;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;

public class SaveQueryPresenter extends Presenter implements ClickHandler {

	public interface View extends IView {
		TextBox getName();
	}
	private View ui;
	private Presenter presenter;
	
	public SaveQueryPresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
		this.presenter = parent;
	}
	
	@Override
	public IView getDisplay() {
		return ui;
	}


	@Override
	protected void bindAll() {
	}
	
	public String getName() {
		return ui.getName().getText();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void activated(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}
}
