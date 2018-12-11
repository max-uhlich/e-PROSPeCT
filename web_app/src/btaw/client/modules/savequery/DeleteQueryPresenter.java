package btaw.client.modules.savequery;

import java.util.List;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.category.CategoryPresenter;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.shared.model.query.saved.SavedQueryDisplayData;

public class DeleteQueryPresenter extends Presenter implements DoubleClickHandler {

	public interface View extends IView {

		void addSavedQueries(List<SavedQueryDisplayData> result, DeleteQueryPresenter presenter);
	}
	private View ui;
	private Presenter presenter;
	
	public DeleteQueryPresenter(Presenter parent, View ui) {
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

	@Override
	protected void activated(HasWidgets container) {
		getNamedQueries();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		System.err.println("GOT HERE WITH " + ((SavedQueryLabel)event.getSource()).getText());
		this.rpcService.deleteSavedQuery(((SavedQueryLabel)event.getSource()).getSavedQueryId(), new PresenterCallback<Void> (this) {

			@Override
			protected void success(Void result) {
				getNamedQueries();
				CategoryPresenter.get().getNamedQueries();
			}
			
		});
	}
	
	public void getNamedQueries() {
		
		this.rpcService.getNamedQueries(new PresenterCallback<List<SavedQueryDisplayData>> (this) {
			@Override
			protected void success(List<SavedQueryDisplayData> result) {
				DeleteQueryPresenter.this.ui.addSavedQueries(result, DeleteQueryPresenter.this);
			}
		});
		
	}
}
