package btaw.client.modules.movie;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextArea;

public class GlobalCommentEditPresenter extends Presenter {

	public interface View extends IView {
		void close();
		Button getCancelButton();
		void populateAndShow(String pid);
		void setUser(String user);
		Button getDoneButton();
		String getComment();
		TextArea getCommentTA();
	}
	
	private String pid;
	private final View ui;
	
	public GlobalCommentEditPresenter(Presenter parent, View ui, String pid) {
		super(parent);
		
		this.pid = pid;
		this.ui = ui;
	}

	@Override
	protected void bindAll() {
		ui.populateAndShow(pid);
		this.rpcService.getUsername(new PresenterCallback<String>(GlobalCommentEditPresenter.this) {

			@Override
			protected void success(String result) {
				ui.setUser(result);
			}
			
		});
		ui.getCancelButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.close();
			}
		});
		ui.getDoneButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rpcService.insertGlobalComment(Integer.parseInt(GlobalCommentEditPresenter.this.pid), ui.getComment(),new PresenterCallback<Void>(GlobalCommentEditPresenter.this) {

					@Override
					protected void success(Void result) {
						ui.close();
						if(GlobalCommentEditPresenter.this.parent instanceof MoviePresenter) {
							((MoviePresenter)(GlobalCommentEditPresenter.this.parent)).repopulateComments();
						}
					}
					
				});
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
