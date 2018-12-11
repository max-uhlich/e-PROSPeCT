package btaw.client.modules.popups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.classes.ErrorPopupPanel;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.main.MainPresenter;

public class PasswordChangePresenter extends Presenter {

	public interface View extends IView {
		String get_cur_pass();
		String get_new_pass();
		Button getOK();
		Button getCancel();
		void close();
	}
	
	private final View ui;
	
	public PasswordChangePresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
		
	}

	@Override
	protected void bindAll() {
		ui.getOK().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//FilterPresenter.get().addDefinition();
				//Window.alert("Changing your password now");
				MainPresenter.get().working(true);
				rpcService.change_password(ui.get_cur_pass(), ui.get_new_pass(), new PresenterCallback<Boolean>(PasswordChangePresenter.this) {
					@Override
					protected void success(Boolean result) {
						MainPresenter.get().finished();
						//Window.alert(result.toString());
						if (!result){
							ErrorPopupPanel popup = new ErrorPopupPanel("Uh Oh", "Your password has not been changed.", "#FFFFFF");
							popup.center();
						} else {
							ErrorPopupPanel popup = new ErrorPopupPanel("Success!", "Your password has been changed.", "#FFFFFF");
							popup.center();
						}
					}
				});
				ui.close();
			}
		});
		ui.getCancel().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.close();
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
