package btaw.client.modules.image;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.shared.model.Study;

public class SimilarityStudyView extends AView implements SimilarityStudyPresenter.View {

	private VerticalPanel mainPanel;
	private TextBox patients;
	private ListBox studies;
	private Button populateStudies;
	private HorizontalPanel pidInputPanel;
	private HorizontalPanel studyInputPanel;
	
	public SimilarityStudyView() {
		mainPanel = new VerticalPanel();
		pidInputPanel = new HorizontalPanel();
		studyInputPanel = new HorizontalPanel();
		initWidget(mainPanel);
		
		patients = new TextBox();
		studies = new ListBox();
		populateStudies = new Button("Populate Studies");
		
		Label pidL = new Label("PID");
		pidL.setStyleName("center-Label");
		patients.setValue("Please Input PID");
		pidInputPanel.setSpacing(5);
		pidInputPanel.add(pidL);
		pidInputPanel.add(patients);
		
		Label studyL = new Label("Study");
		studyL.setStyleName("center-Label");
		studies.addItem("Input PID and Press Enter");
		studyInputPanel.setSpacing(5);
		studyInputPanel.add(studyL);
		studyInputPanel.add(studies);

		mainPanel.add(pidInputPanel);
		mainPanel.add(studyInputPanel);
		//mainPanel.add(populateStudies);
	}
	@Override
	public int getStudyId() {
		return Integer.parseInt(studies.getValue(studies.getSelectedIndex()).split(" - ")[0]);
	}
	@Override
	public Button getPopulateButton() {
		return populateStudies;
	}
	@Override
	public void populateStudies(List<Study> result) {
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		studies.clear();
		for(Study s : result) {
			studies.addItem(s.getId() + " - " + fmt.format(s.getDate()));
		}
	}
	@Override
	public TextBox getPatientBox() {
		return patients;
	}
	
	@Override
	public ArrayList<Circle> getPoints() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getPidInput() {
		return patients.getText();
	}
	public List<String> getStudies() {
		List<String> ret = new ArrayList<String>();
		
		for(int i = 0; i < studies.getItemCount(); i++) {
			ret.add(studies.getItemText(i));
		}
		
		return ret;
	}
	
	public void setPidInput(String s) {
		patients.setText(s);
	}
	
	public void setStudies(List<String> studies) {
		for(String s : studies) {
			this.studies.addItem(s);
		}
	}

}
