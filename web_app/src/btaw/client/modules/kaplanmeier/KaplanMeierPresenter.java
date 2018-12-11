package btaw.client.modules.kaplanmeier;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

public class KaplanMeierPresenter extends Presenter {

	public interface View extends IView {
		void close();
		Button getCancelButton();
		Button getDoneButton();
		void addKMUrl (String url);
		Button getDeleteButton();
		Button getStatButton();
	}
	
	private final View ui;
	
	public KaplanMeierPresenter(Presenter parent, View ui) {
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
				ui.close();
			}
		});
		ui.getDeleteButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KaplanMeierGroupSelectorPresenter kmg = new KaplanMeierGroupSelectorPresenter(KaplanMeierPresenter.this, new KaplanMeierGroupSelectorView());
				kmg.setActive();
			}
		});
		ui.getStatButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KaplanMeierStatisticPresenter kms = new KaplanMeierStatisticPresenter(KaplanMeierPresenter.this, new KaplanMeierStatisticView());
				kms.setActive();
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
	
	public void setKMUrl(String url) {
		ui.addKMUrl(url);
	}
}
