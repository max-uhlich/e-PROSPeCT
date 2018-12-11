package btaw.client.modules.movie;

import java.util.ArrayList;
import java.util.List;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OptionsPresenter extends Presenter {

	public interface View extends IView {
		void close();
		Button getCancelButton();
		Button getDoneButton();
		void populateUserList(List<String> usernames);
		void populateCategoryList(List<String> categories);
		VerticalPanel getUsernamePanel();
		VerticalPanel getCategoryPanel();
	}
	
	private final View ui;
	
	public OptionsPresenter(Presenter parent, View ui) {
		super(parent);
		
		this.ui = ui;
	}

	@Override
	protected void bindAll() {
		rpcService.getCommentCategories(new PresenterCallback<List<String>>(OptionsPresenter.this) {
			@Override
			public void success(List<String> result) {
				ui.populateCategoryList(result);
			}
		});
		rpcService.getUserList(new PresenterCallback<List<String>>(OptionsPresenter.this) {
			@Override
			public void success(List<String> result) {
				ui.populateUserList(result);
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
				List<String> usernames = new ArrayList<String>();
				List<String> categories = new ArrayList<String>();
				
				for(int i = 0; i < ui.getUsernamePanel().getWidgetCount(); i++) {
					if(ui.getUsernamePanel().getWidget(i) instanceof CheckBox && ((CheckBox)(ui.getUsernamePanel().getWidget(i))).isChecked()) {
						usernames.add( ((CheckBox)(ui.getUsernamePanel().getWidget(i))).getText() );
					}
				}
				for(int i = 0; i < ui.getCategoryPanel().getWidgetCount(); i++) {
					if(ui.getCategoryPanel().getWidget(i) instanceof CheckBox && ((CheckBox)(ui.getCategoryPanel().getWidget(i))).isChecked()) {
						categories.add( ((CheckBox)(ui.getCategoryPanel().getWidget(i))).getText() );
					}
				}
				if(parent instanceof MoviePresenter) {
					((MoviePresenter)(OptionsPresenter.this.parent)).setupFilters(usernames, categories);
				} else {
					System.err.println("parent not a MoviePresenter in OptionPresenter, what happened?");
				}
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
