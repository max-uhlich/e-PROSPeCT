package btaw.client.modules.popups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;

public class PermissionPresenter extends Presenter {

	public interface View extends IView {
		Button getClose();
		void close();
	}
	
	private final View ui;
	
	public PermissionPresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
		
	}

	@Override
	protected void bindAll() {
		ui.getClose().addClickHandler(new ClickHandler() {
			
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
