package btaw.client.modules.kaplanmeier;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;

public class KaplanMeierGroupSelectorPresenter extends Presenter {
	
	public interface View extends IView {
		Button getCancelButton();
		Button getDoneButton();
		void close();
		void doDelete();
	}

	private final View ui;
	public KaplanMeierGroupSelectorPresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
	}

	@Override
	protected void bindAll() {
		ui.getCancelButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.close();
			}
		});
		ui.getDoneButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.doDelete();
				ui.close();
				FilterPresenter.get().updateKMData((KaplanMeierPresenter)parent);
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
