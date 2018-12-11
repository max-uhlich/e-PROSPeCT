package btaw.client.modules.synonym;

import java.util.ArrayList;

import btaw.client.framework.AView;
import btaw.client.modules.synonym.SynonymPresenter.View;
import btaw.shared.model.SynonymData;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SynonymView extends AView implements View {
	
	private HorizontalPanel header;
	private HorizontalPanel footer;
	private ArrayList<HorizontalPanel> synonymHPs;
	
	private VerticalPanel leftPanel;
	private VerticalPanel rightPanel;
	
	private VerticalPanel containerPanel;
	private HorizontalPanel innerContainerPanel;
	private DialogBox main;
	
	private Button save;
	private Button cancel;
	private Button addRow;
	private Button deleteRow;
	
	public SynonymView() {
		main = new DialogBox();
		containerPanel = new VerticalPanel();
		innerContainerPanel = new HorizontalPanel();
		
		header = new HorizontalPanel();
		footer = new HorizontalPanel();
		synonymHPs = new ArrayList<HorizontalPanel>();
		
		leftPanel = new VerticalPanel();
		rightPanel = new VerticalPanel();
		leftPanel.getElement().getStyle().setPaddingRight(25, Unit.PX);
		
		innerContainerPanel.add(leftPanel);
		innerContainerPanel.add(rightPanel);
		
		HorizontalPanel bottom = new HorizontalPanel();
		save = new Button("Save");
		cancel = new Button("Cancel");
		addRow = new Button("Add Row");
		deleteRow = new Button("Delete Row");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(addRow);
		alignPanel.add(deleteRow);
		alignPanel.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp"));
		alignPanel.add(save);
		alignPanel.add(cancel);
		
		containerPanel.add(innerContainerPanel);
		containerPanel.add(bottom);
		main.add(containerPanel);
		
		main.setText("Editing Synonym Dictionary");
		main.setModal(true);
		main.center();
	}
	
	public void displayGlobalSynonyms(ArrayList<SynonymData> synonyms) {
		Label wordL = new Label("Word");
		wordL.setStyleName("titleLabel");
		Label synonymL = new Label("Synonym");
		synonymL.setStyleName("titleLabel");
		leftPanel.add(wordL);
		rightPanel.add(synonymL);
		for(SynonymData sd : synonyms) {
			leftPanel.add(new Label(sd.getWord()));
			rightPanel.add(new Label(sd.getSynonym()));
		}
	}
	public void displayUserSynonyms(ArrayList<SynonymData> synonyms) {
		for(SynonymData sd : synonyms) {
			TextBox tbWord = new TextBox();
			tbWord.setText(sd.getWord());
			TextBox tbSynonym = new TextBox();
			tbSynonym.setText(sd.getSynonym());
			
			leftPanel.add(tbWord);
			rightPanel.add(tbSynonym);
		}
		addRow();
	}
	
	public Button getAddRowButton() {
		return this.addRow;
	}
	
	public void addRow() {
		TextBox tbInsertWord = new TextBox();
		TextBox tbInsertSynonym = new TextBox();
		
		leftPanel.add(tbInsertWord);
		rightPanel.add(tbInsertSynonym);
	}
	
	public Button getDeleteRowButton() {
		return this.deleteRow;
	}
	
	public void deleteRow() {
		if(leftPanel.getWidget(leftPanel.getWidgetCount() - 1) instanceof TextBox &&
				rightPanel.getWidget(rightPanel.getWidgetCount() - 1) instanceof TextBox) {
			leftPanel.remove(leftPanel.getWidgetCount() - 1);
			rightPanel.remove(rightPanel.getWidgetCount() - 1);
		}
	}
	
	public Button getSaveButton() {
		return this.save;
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	
	public void close() {
		main.hide();
	}
	
	public ArrayList<SynonymData> getSynonymData() {
		ArrayList<SynonymData> synonyms = new ArrayList<SynonymData>();
		
		for(int i = 0; i < leftPanel.getWidgetCount(); i++) {
			if(leftPanel.getWidget(i) instanceof TextBox) {
				TextBox wordTB = (TextBox) leftPanel.getWidget(i);
				TextBox synonymTB = (TextBox) rightPanel.getWidget(i);
				synonyms.add(new SynonymData(wordTB.getText(), synonymTB.getText()));
			}
		}
		
		return synonyms;
	}
}
