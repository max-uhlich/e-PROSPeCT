package btaw.client.modules.tab;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.psa.PSA_Presenter.View;

public class PatientSummaryTabPresenter extends Presenter {

	public interface View extends IView {
		Button getCloseButton();
		void close();
		void populateStudiesAndShow(JsArrayString psa_vals, JsArrayString dates, JsArrayInteger size, String pid);
	}
	
	private String pid;
	private final View ui;
	
	public PatientSummaryTabPresenter(Presenter parent, View ui, String pid) {
		super(parent);

		this.pid = pid;
		this.ui = ui;
	}
	
	@Override
	protected void bindAll() {
		// TODO Auto-generated method stub
		
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
