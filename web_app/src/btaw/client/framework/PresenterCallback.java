package btaw.client.framework;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class PresenterCallback<T> implements AsyncCallback<T> {

	private Presenter presenter;

	public PresenterCallback(Presenter p){
		this.presenter = p;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		presenter.error(caught);
	}

	@Override
	public void onSuccess(T result) {
		success(result);
	}

	protected abstract void success(T result);


	

}
