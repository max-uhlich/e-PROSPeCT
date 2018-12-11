package btaw.client.modules.movie;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.movie.MoviePresenter;

public class OtherCommentEditPresenter extends Presenter {

	public interface View extends IView {
		void close();
		Button getCancelButton();
		void populateAndShow(String pid, List<String> categories);
		void setUser(String user);
		Button getDoneButton();
		String getComment();
		String getCategory();
		ListBox getCategoryLB();
		TextArea getCommentTA();
	}
	
	private String pid;
	private final View ui;
	
	public OtherCommentEditPresenter(Presenter parent, View ui, String pid) {
		super(parent);
		
		this.pid = pid;
		this.ui = ui;
	}

	@Override
	protected void bindAll() {
		this.rpcService.getCommentCategories(new PresenterCallback<List<String>>(OtherCommentEditPresenter.this) {

			@Override
			protected void success(List<String> result) {
				ui.populateAndShow(OtherCommentEditPresenter.this.pid, result);
			}
			
		});
		this.rpcService.getUsername(new PresenterCallback<String>(OtherCommentEditPresenter.this) {

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
				rpcService.insertUserComment(Integer.parseInt(OtherCommentEditPresenter.this.pid), ui.getCategory(), ui.getComment(),new PresenterCallback<Void>(OtherCommentEditPresenter.this) {

					@Override
					protected void success(Void result) {
						ui.close();
						if(OtherCommentEditPresenter.this.parent instanceof MoviePresenter) {
							((MoviePresenter)(OtherCommentEditPresenter.this.parent)).repopulateComments();
						}
					}
					
				});
			}
		});
		ui.getCategoryLB().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				rpcService.getUserComment(Integer.parseInt(pid), ui.getCategoryLB().getValue(ui.getCategoryLB().getSelectedIndex()), new PresenterCallback<String>(OtherCommentEditPresenter.this) {

					@Override
					protected void success(String result) {
						System.err.println(result);
						ui.getCommentTA().setText(result);
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
