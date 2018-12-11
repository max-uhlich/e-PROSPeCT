package btaw.client.modules.movie;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.classes.ErrorPopupPanel;
import btaw.client.framework.AView;
import btaw.client.modules.movie.OtherCommentEditPresenter.View;

public class OtherCommentEditView extends AView implements View {
	private DialogBox main;
	private VerticalPanel containerPanel;
	
	private Label editingAsL;
	
	private ListBox categories;
	private TextBox newCategory;
	
	private TextArea comment;
	
	private Button cancel;
	private Button done;
	
	public OtherCommentEditView() {
		main = new DialogBox();
		main.getElement().getStyle().setZIndex(255);
		containerPanel = new VerticalPanel();
		
		editingAsL = new Label("Editing as user : ");
		
		categories = new ListBox();
		categories.addItem("Please Select a Category");
		Label categoryL = new Label("or");
		newCategory = new TextBox();
		newCategory.setText("Please Enter a New Category");
		newCategory.setWidth("300px");
		
		comment = new TextArea();
		comment.setWidth("100%");
		comment.setHeight("100%");
		
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
		containerPanel.add(categories);
		containerPanel.add(categoryL);
		containerPanel.add(newCategory);
		containerPanel.add(new Label("Edit Comment :"));
		containerPanel.add(comment);
		containerPanel.add(bottom);
		main.add(containerPanel);
		
	}
	
	@Override
	public void populateAndShow(String pid, List<String> categories) {
		for(String s : categories) {
			this.categories.addItem(s);
		}
		if(categories.isEmpty()) {
			this.categories.clear();
			this.categories.addItem("No Categories Found in Database");
		}
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
	
	public ListBox getCategoryLB() {
		return this.categories;
	}
	
	public TextArea getCommentTA() {
		return this.comment;
	}
	
	public String getCategory() {
		if(categories.getSelectedIndex() == 0 && newCategory.getText().equals("Please Enter a New Category")) {
			ErrorPopupPanel error = new ErrorPopupPanel("Error!", "Please enter a new category or select a category.", "#FFFFFF");
			error.center();
			return null;
		} else if (categories.getSelectedIndex() == 0) {
			return newCategory.getText();
		} else {
			return categories.getValue(categories.getSelectedIndex());
		}
	}
	
	public String getComment() {
		return this.comment.getText();
	}
	
}
