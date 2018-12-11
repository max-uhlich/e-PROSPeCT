package btaw.client.modules.psa;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.main.MainPresenter;
import btaw.shared.model.PSA_Graph;

public class PSA_Presenter extends Presenter {

	public interface View extends IView {

		Button getCloseButton();

		void close();

		void populateStudiesAndShow(JsArrayString psa_vals, JsArrayString dates, JsArrayInteger size, String pid);
		
	}
	
	private String pid;
	private final View ui;
	
	public PSA_Presenter(Presenter parent, View ui, String pid) {
		super(parent);

		this.pid = pid;
		this.ui = ui;
	}

	@Override
	protected void bindAll() {
		this.rpcService.getPsaValues(pid,
				new PresenterCallback<PSA_Graph>(this) {

					@Override
					protected void success(PSA_Graph result) {
						
						List<String> dates = result.getDates();
						List<String> psa_vals = result.getVals();
						
						JsArrayString jsDates = (JsArrayString)JavaScriptObject.createArray().cast();
						JsArrayString jsPSA_Vals = (JsArrayString)JavaScriptObject.createArray().cast();
						JsArrayInteger jsSize = (JsArrayInteger)JavaScriptObject.createArray().cast();
						jsSize.push(600); //Width
						jsSize.push(400); //Height
						
						for (int i = 0; i<dates.size(); i++) {
							jsDates.push(dates.get(i));
							jsPSA_Vals.push(psa_vals.get(i));
						}
						
						ui.populateStudiesAndShow(jsPSA_Vals, jsDates, jsSize, pid);
						MainPresenter.get().finished();
					}
				});
		
		ui.getCloseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ui.close();
			}
		});
		
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}

	@Override
	protected void activated(HasWidgets container) {
	}
	
}
