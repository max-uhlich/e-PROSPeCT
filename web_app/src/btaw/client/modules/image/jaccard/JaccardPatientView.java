package btaw.client.modules.image.jaccard;

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
import btaw.client.modules.image.Circle;
import btaw.shared.model.Study;
import btaw.shared.model.query.filter.JaccardPatientFilter;

public class JaccardPatientView extends AView implements JaccardPatientPresenter.View {

	private VerticalPanel mainPanel;
	private TextBox patients;
	private ListBox studies;
	private Button populateStudies;
	private ListBox operator;
	private TextBox jscoreLower;
	private TextBox jscoreUpper;
	private HorizontalPanel jscoreControls;
	private HorizontalPanel pidInputPanel;
	private HorizontalPanel studyInputPanel;
	private Label jscoreRangeL;
	
	public JaccardPatientView() {
		mainPanel = new VerticalPanel();
		jscoreControls = new HorizontalPanel();
		pidInputPanel = new HorizontalPanel();
		studyInputPanel = new HorizontalPanel();
		initWidget(mainPanel);
		
		patients = new TextBox();
		studies = new ListBox();
		populateStudies = new Button("Populate Studies");
		
		Label pidL = new Label("PID");
		pidL.setStyleName("center-Label");
		patients.setValue("Please Input PID");
		pidInputPanel.add(pidL);
		pidInputPanel.add(patients);
		
		Label studyL = new Label("Study");
		studyL.setStyleName("center-Label");
		studies.addItem("Input PID and click Populate");
		studyInputPanel.add(studyL);
		studyInputPanel.add(studies);
		
		Label jscoreL = new Label("Jaccard Score");
		jscoreL.setStyleName("center-Label");
		operator = new ListBox();
		operator.addItem("=");
		operator.addItem("<");
		operator.addItem(">");
		operator.addItem("Between(not-inclusive)");
		operator.setVisibleItemCount(1);
		jscoreRangeL = new Label("And");
		jscoreRangeL.setStyleName("center-Label");
		jscoreLower = new TextBox();
		jscoreLower.setValue("0");
		jscoreLower.setWidth("25px");
		jscoreUpper = new TextBox();
		jscoreUpper.setValue("1");
		jscoreUpper.setWidth("25px");
		jscoreControls.add(jscoreL);
		jscoreControls.add(operator);
		jscoreControls.add(jscoreLower);

		mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		mainPanel.add(jscoreControls);
		mainPanel.add(pidInputPanel);
		mainPanel.add(studyInputPanel);
		mainPanel.add(populateStudies);
	}
	@Override
	public ArrayList<Circle> getPoints() {
		// TODO Auto-generated method stub
		return null;
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
	public ListBox getStudyBox() {
		return studies;
	}
	@Override
	public ListBox getOperator() {
		return operator;
	}
	
	@Override
	public TextBox getJScoreLower() {
		return jscoreLower;
	}
	
	@Override
	public TextBox getJScoreUpper() {
		return jscoreUpper;
	}
	
	@Override
	public void setJScoreAsRange() {
		if(jscoreUpper.isAttached()) {
			return;
		} else {
			jscoreControls.add(jscoreRangeL);
			jscoreControls.add(jscoreUpper);
		}
	}
	
	@Override
	public void setJScoreAsSingleVal() {
		if(jscoreUpper.isAttached()) {
			jscoreControls.remove(jscoreRangeL);
			jscoreControls.remove(jscoreUpper);
		}
	}
	@Override
	public void buildFilter(JaccardPatientFilter filter) {
		String study_id = studies.getValue(studies.getSelectedIndex()).split(" - ")[0];
		filter.setSid(Integer.parseInt( study_id ));
		
		if(operator.getItemText(operator.getSelectedIndex()).equals("=")) {
			filter.setJaccardString(filter.getTable_alias() + ".image_score=" + jscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals("<")) {
			filter.setJaccardString(filter.getTable_alias() + ".image_score<" + jscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals(">")) {
			filter.setJaccardString(filter.getTable_alias() + ".image_score>" + jscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals("Between")) {
			filter.setJaccardString(filter.getTable_alias() + ".image_score between " + jscoreLower.getText() + " and " + jscoreUpper.getText());
		}
	}
}
