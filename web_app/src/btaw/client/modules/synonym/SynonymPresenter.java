package btaw.client.modules.synonym;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.shared.model.SynonymData;

public class SynonymPresenter extends Presenter {

	public interface View extends IView
	{
		void displayGlobalSynonyms(ArrayList<SynonymData> synonyms);
		void displayUserSynonyms(ArrayList<SynonymData> synonyms);
		Button getAddRowButton();
		Button getDeleteRowButton();
		Button getSaveButton();
		Button getCancelButton();
		void addRow();
		void deleteRow();
		void close();
		ArrayList<SynonymData> getSynonymData();
	}
	
	private final View ui;
	public SynonymPresenter (Presenter parent, View ui) {
		super(parent);
		
		this.ui = ui;
	}
	@Override
	protected void bindAll() {
		rpcService.getGlobalSynonyms(new PresenterCallback<ArrayList<SynonymData>>(this) {

			@Override
			protected void success(ArrayList<SynonymData> result) {
				ui.displayGlobalSynonyms(result);
				SynonymPresenter.this.getUserSynonyms();
			}
		});
		ui.getAddRowButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ui.addRow();
			}
		});
		ui.getDeleteRowButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ui.deleteRow();
			}
		});
		ui.getSaveButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				rpcService.insertUserSynonyms(ui.getSynonymData(), new PresenterCallback<Void>(SynonymPresenter.this) {

					@Override
					protected void success(Void result) {
						ui.close();
					}
					
				});
			}
			
		});
		ui.getCancelButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ui.close();
			}
		});
	}
	
	private void getUserSynonyms() {
		rpcService.getUserSynonyms(new PresenterCallback<ArrayList<SynonymData>>(this) {

			@Override
			protected void success(ArrayList<SynonymData> result) {
				ui.displayUserSynonyms(result);
			}
		});
	}
	
	@Override
	protected IView getDisplay() {
		return ui;
	}
	@Override
	protected void activated(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}
}
