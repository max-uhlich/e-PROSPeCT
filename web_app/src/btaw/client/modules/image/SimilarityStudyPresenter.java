package btaw.client.modules.image;

import java.util.LinkedHashMap;
import java.util.List;

import btaw.client.framework.Presenter;
import btaw.shared.model.Study;
import btaw.shared.model.query.filter.Filter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

public class SimilarityStudyPresenter extends ImagePresenter {
	
	public interface View extends ImagePresenter.View {
		Button getPopulateButton();
		void populateStudies(List<Study> result);
		TextBox getPatientBox();
		int getStudyId();
		String getPidInput();
		List<String> getStudies();
		void setStudies(List<String> studies);
		void setPidInput(String s);
	}

	private View ui;
	
	public SimilarityStudyPresenter(Presenter parent,View ui) {
		super(parent, ui);
		this.ui = ui;
	}
	
	@Override
	protected void bindAll() {
		ui.getPatientBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				populateStudies();
			}
		});
		ui.getPopulateButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				populateStudies();
			}
		});
	}

	private void populateStudies() {
		rpcService.getStudies(ui.getPatientBox().getValue(), 
				new AsyncCallback<List<Study>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Study> result) {
				ui.populateStudies(result);
			}
		});
	}
	
	public int getStudyId() {
		return ui.getStudyId();
	}
	
	public String getPidInput() {
		return ui.getPidInput();
	}
	
	public List<String> getStudies() {
		return ui.getStudies();
	}

	public void setStudies(List<String> studies) {
		ui.setStudies(studies);
	}
	public void setPidInput(String s) {
		ui.setPidInput(s);
	}
	
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isType(String val) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		// TODO Auto-generated method stub
		
	}
}
