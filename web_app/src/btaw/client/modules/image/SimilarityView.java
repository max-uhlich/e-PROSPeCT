package btaw.client.modules.image;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.shared.model.query.filter.ImageQueryFilter;

public class SimilarityView extends AView implements SimilarityPresenter.View {

	private VerticalPanel mainPanel;

	private HorizontalPanel segPanel;
	private ListBox segType;
	
	private HorizontalPanel drawnOrStudyPanel;
	private Button drawQuery;
	private Button selectStudyQuery;
	
	private HorizontalPanel coveragePanel;
	private ListBox coverageOptions;
	
	private HorizontalPanel queryTypePanel;
	private ListBox queryType;
	
	private HorizontalPanel thresholdPanel;
	private ListBox thresholdOperator;
	private TextBox thresholdLower;
	private Label thresholdFormulaL;
	private TextBox thresholdUpper;
	
	private LinkedHashMap<String, Integer> segmentations;
	
	public SimilarityView() {
		mainPanel = new VerticalPanel();
		segPanel = new HorizontalPanel();
		drawnOrStudyPanel = new HorizontalPanel();
		coveragePanel = new HorizontalPanel();
		queryTypePanel = new HorizontalPanel();
		thresholdPanel = new HorizontalPanel();
		initWidget(mainPanel);

		segPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		drawnOrStudyPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		coveragePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		queryTypePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		thresholdPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		
		Image setIntersect = new Image("images/set_intersect.svg");
		setIntersect.setSize("300px", "200px");
		
		Label specSegL = new Label("Define Region Type (B): ");
		specSegL.setStyleName("center-Label");
		segType = new ListBox();
		segPanel.setSpacing(5);
		segPanel.add(specSegL);
		segPanel.add(segType);
		
		Label selectL = new Label("Define Region (A): ");
		selectL.setStyleName("center-Label");
		drawQuery = new Button("Draw");
		selectStudyQuery = new Button("Select Case From Database");
		drawnOrStudyPanel.setSpacing(5);
		drawnOrStudyPanel.add(selectL);
		drawnOrStudyPanel.add(drawQuery);
		drawnOrStudyPanel.add(selectStudyQuery);
		
		Label considerL = new Label("Consider: ");
		considerL.setStyleName("center-Label");
		coverageOptions = new ListBox();
		coverageOptions.addItem("All Slices");
		coverageOptions.addItem("Drawn Slices");
		coveragePanel.setSpacing(5);
		coveragePanel.add(considerL);
		coveragePanel.add(coverageOptions);
		
		Label chooseQueryTypeL = new Label("Similarity Measure: ");
		chooseQueryTypeL.setStyleName("center-Label");
		chooseQueryTypeL.setWordWrap(false);
		queryType = new ListBox();
		queryType.addItem(SimilarityPresenter.QUERYOVERLAP);
		queryType.addItem(SimilarityPresenter.PATIENTOVERLAP);
		queryType.addItem(SimilarityPresenter.INTERSECT);
		queryType.addItem(SimilarityPresenter.JACCARD);
		queryTypePanel.setSpacing(5);
		queryTypePanel.add(chooseQueryTypeL);
		queryTypePanel.add(queryType);
		
		Label thresholdL = new Label("Threshold: ");
		thresholdL.setStyleName("center-Label");
		thresholdL.setWordWrap(false);
		Label thresholdOpL1 = new Label("<");
		thresholdOpL1.setStyleName("center-Label");
		Label thresholdOpL2 = new Label("<");
		thresholdOpL2.setStyleName("center-Label");
		thresholdFormulaL = new Label("(A \u2229 B)/A");
		thresholdFormulaL.setStyleName("center-Label");
		thresholdLower = new TextBox();
		thresholdLower.setValue("0");
		thresholdLower.setWidth("25px");
		thresholdUpper = new TextBox();
		thresholdUpper.setValue("1");
		thresholdUpper.setWidth("25px");
		thresholdPanel.setSpacing(5);
		thresholdPanel.add(thresholdL);
		thresholdPanel.add(thresholdLower);
		thresholdPanel.add(thresholdOpL1);
		thresholdPanel.add(thresholdFormulaL);
		thresholdPanel.add(thresholdOpL2);
		thresholdPanel.add(thresholdUpper);

		mainPanel.add(setIntersect);
		mainPanel.add(drawnOrStudyPanel);
		mainPanel.add(segPanel);
		mainPanel.add(coveragePanel);
		mainPanel.add(queryTypePanel);
		mainPanel.add(thresholdPanel);
		mainPanel.getElement().getStyle().setZIndex(3);
	}
	@Override
	public boolean isAllSlices() {
		return this.coverageOptions.getItemText(this.coverageOptions.getSelectedIndex()).equals("All Slices");
	}
	@Override
	public Label getDisplayFormula() {
		return thresholdFormulaL;
	}
	@Override
	public int getRoiId() {
		return segmentations.get(segType.getItemText(segType.getSelectedIndex()));
	}
	@Override
	public ListBox getQueryType() {
		return queryType;
	}
	
	@Override
	public Button getDrawQuery() {
		return drawQuery;
	}
	
	@Override
	public Button getSelectStudy() {
		return selectStudyQuery;
	}
	
	@Override 
	public ListBox getThresholdOperator() {
		return thresholdOperator;
	}

	public void populateSegmentation(LinkedHashMap<String, Integer> segTypes) {
		for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
			this.segType.addItem(entry.getKey());
		}
		this.segmentations = segTypes;
	}
	
	@Override
	public void buildFilter(ImageQueryFilter filter) {
		filter.setRegionId(segmentations.get(segType.getValue(segType.getSelectedIndex())));
		filter.setFilterString(filter.getTableAlias() + ".image_score > " + thresholdLower.getText() + " AND " + filter.getTableAlias() + ".image_score < " + thresholdUpper.getText());
		System.err.println(filter.getFilterString());
	}
	
	@Override
	public ArrayList<Circle> getPoints() {
		return null;
	}
	
	public int getRegionTypeIndex() {
		return segType.getSelectedIndex();
	}
	
	public int getSliceIndex() {
		return coverageOptions.getSelectedIndex();
	}
	
	public int getSimMeasureIndex() {
		return queryType.getSelectedIndex();
	}
	
	public String getLowerThreshold() {
		return thresholdLower.getValue();
	}
	
	public String getUpperThreshold() {
		return thresholdUpper.getValue();
	}
	
	public void setRegionTypeIndex(int i ) {
		segType.setSelectedIndex(i);
	}
	
	public void setSliceIndex(int i) {
		coverageOptions.setSelectedIndex(i);
	}
	
	public void setSimMeasureIndex(int i) {
		queryType.setSelectedIndex(i);
	}
	
	public void setLowerThreshold(String s) {
		thresholdLower.setValue(s);
	}
	
	public void setUpperThresohld(String s) {
		thresholdUpper.setValue(s);
	}
}
