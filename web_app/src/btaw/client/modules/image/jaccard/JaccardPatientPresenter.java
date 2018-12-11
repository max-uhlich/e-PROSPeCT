package btaw.client.modules.image.jaccard;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import btaw.client.framework.Presenter;
import btaw.client.modules.image.ImagePresenter;
import btaw.shared.model.Study;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.JaccardPatientFilter;

public class JaccardPatientPresenter extends ImagePresenter {

	public interface View extends ImagePresenter.View {
		void populateStudies(List<Study> result);
		TextBox getPatientBox();
		ListBox getStudyBox();
		Button getPopulateButton();
		void buildFilter(JaccardPatientFilter filter);
		ListBox getOperator();
		TextBox getJScoreLower();
		TextBox getJScoreUpper();
		void setJScoreAsRange();
		void setJScoreAsSingleVal();
	}
	
	private View ui;
	int roi_id;
	private JaccardPatientFilter filter;
	
	public int getRoi_id() {
		return roi_id;
	}

	public void setRoi_id(int roi_id) {
		this.roi_id = roi_id;
	}

	public JaccardPatientPresenter(Presenter parent,View ui) {
		super(parent, ui);
		this.ui = ui;
		filter = new JaccardPatientFilter();
		filter.setup();
	}

	@Override
	protected void bindAll() {
		ui.getPopulateButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				populateStudies();
			}
		});
		
		ui.getOperator().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox operator = ui.getOperator();
				Integer i = operator.getSelectedIndex();
				if(operator.getValue(i).equals("Between(not-inclusive)")) {
					ui.setJScoreAsRange();
				} else {
					ui.setJScoreAsSingleVal();
				}
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
	@Override
	public Filter getFilter() {
		filter.setRoi_id(this.roi_id);
		
		ui.buildFilter(filter);
		
		return filter;
	}

	@Override
	public boolean isType(String val) {
		return false;//return val.equals(ImagePresenter.PJACCARD);
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		// TODO Auto-generated method stub
		
	}
	
}
