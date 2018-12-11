package btaw.client.modules.movie;

import btaw.client.framework.AView;
import btaw.client.modules.movie.GlobalCommentEditPresenter.View;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GlobalCommentEditView extends AView implements View{
	private DialogBox main;
	private VerticalPanel containerPanel;
	
	private Label editingAsL;
	
	private TextArea comment;
	
	private Button cancel;
	private Button done;
	
	public GlobalCommentEditView() {
		main = new DialogBox();
		main.getElement().getStyle().setZIndex(255);
		containerPanel = new VerticalPanel();
		
		editingAsL = new Label("Editing as user : ");
		
		comment = new TextArea();
		comment.setWidth("300px");
		comment.setHeight("100px");
		
		HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("Cancel");
		done = new Button("Done");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(done);
		alignPanel.add(cancel);
		
		containerPanel.setSpacing(15);
		containerPanel.add(editingAsL);
		containerPanel.add(new Label("Edit Comment :"));
		containerPanel.add(comment);
		containerPanel.add(bottom);
		main.add(containerPanel);
		
	}
	
	@Override
	public void populateAndShow(String pid) {
		main.setText("Patient "+pid+" Studies");
		main.setModal(true);
		main.center();
	}
	
	@Override
	public void close() {
		main.hide();
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	
	@Override
	public void setUser(String user) {
		editingAsL.setText(editingAsL.getText() + user);
	}
	
	public Button getDoneButton() {
		return this.done;
	}
	public TextArea getCommentTA() {
		return this.comment;
	}
	
	public String getComment() {
		return this.comment.getText();
	}
}
