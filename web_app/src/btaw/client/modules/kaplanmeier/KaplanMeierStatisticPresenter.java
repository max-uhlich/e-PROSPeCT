package btaw.client.modules.kaplanmeier;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.filter.FilterPresenter;

public class KaplanMeierStatisticPresenter extends Presenter {
	
	public interface View extends IView {
		Button getCancelButton();
		Button getDoneButton();
		void close();
		Button getCalculateButton();
		void setPVal(String p);
		ListBox getGroup1();
		ListBox getGroup2();
		void setPValCalculating();
	}

	private final View ui;
	public KaplanMeierStatisticPresenter(Presenter parent, View ui) {
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
		ui.getCalculateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.setPValCalculating();
				ArrayList<ArrayList<String>> pids = new ArrayList<ArrayList<String>>();
				pids.add(FilterPresenter.get().getKMPids().get(ui.getGroup1().getSelectedIndex()));
				pids.add(FilterPresenter.get().getKMPids().get(ui.getGroup2().getSelectedIndex()));
				/*rpcService.getChiSqPVal(pids, new PresenterCallback<String>(KaplanMeierStatisticPresenter.this) {

					@Override
					protected void success(String result) {
						ui.setPVal(result);
					}
					
				});*/
			}
		});
		if (FilterPresenter.get().getKMNames().size() >= 2) {
			ui.getCalculateButton().click();
		}
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
