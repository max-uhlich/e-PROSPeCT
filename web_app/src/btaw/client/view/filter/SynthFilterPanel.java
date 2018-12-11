package btaw.client.view.filter;

import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.synth.SynthValuePanel;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.saved.RebuildDataSynthFilterPanel;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SynthFilterPanel extends TableFilterPanel {
	protected SynthValuePanel panel;
	private String queryType;
	
	public SynthFilterPanel(Column text, String queryType) {
		super(text);
		this.panel.setName(queryType);
	}

	@Override
	protected HorizontalPanel getDifferentPart() {
		super.removeOperator();
		this.panel = new SynthValuePanel(FilterPresenter.get());
		return this.panel;
	}
	
	public SynthValuePanel getPanel() {
		return (SynthValuePanel)this.panel;
	}

	@Override
	public Filter getFilter() {
		return panel.getFilter();
	}

	public void storeState(RebuildDataFilterPanel rb) {
		rb.setLeftPar(this.lbrack());
		rb.setRightPar(this.rbrack());
		rb.setOpIndex(this.getOperator().getSelectedIndex());
		rb.setCol(this.getColumn());
		rb.setSynthFilterPanel(new RebuildDataSynthFilterPanel());
		rb.getSynthFilterPanel().setSimilarityMeasure(panel.getName());
//		rb.getSynthFilterPanel().setRegionTypes(panel.getRegionsOfInterest());
/*		if(panel.getSelection().equals(ImagePresenter.DISTANCE)) {
			rb.getImageFilterPanel().setDistancePoint(panel.getDistancePresenter().getPoint());
			rb.getImageFilterPanel().setSelectedRegionIndex(panel.getDistancePresenter().getRegionIndex());
		} else if(panel.getSelection().equals(ImagePresenter.SIMILARITY)) {
			rb.getImageFilterPanel().setDrawnPoints(panel.getSimilarityPresenter().getPoints());
			rb.getImageFilterPanel().setConsiderSliceIndex(panel.getSimilarityPresenter().getSliceIndex());
			rb.getImageFilterPanel().setPid(panel.getSimilarityPresenter().getPidInput());
			rb.getImageFilterPanel().setStudyList(panel.getSimilarityPresenter().getStudies());
			rb.getImageFilterPanel().setSelectedRegionIndex(panel.getSimilarityPresenter().getRegionTypeIndex());
			rb.getImageFilterPanel().setSimMeasureIndex(panel.getSimilarityPresenter().getSimMeasureIndex());
			rb.getImageFilterPanel().setLowerThreshold(panel.getSimilarityPresenter().getLowerThreshold());
			rb.getImageFilterPanel().setUpperThreshold(panel.getSimilarityPresenter().getUpperThreshold());
		} else if(panel.getSelection().equals(ImagePresenter.RATIOS)) {
			rb.getImageFilterPanel().setBotOpIndices(panel.getRatioPresenter().getBottomIndices());
			rb.getImageFilterPanel().setTopOpIndices(panel.getRatioPresenter().getUpperIndices());
			rb.getImageFilterPanel().setLowerThreshold(panel.getRatioPresenter().getLowerThresholdValue());
			rb.getImageFilterPanel().setUpperThreshold(panel.getRatioPresenter().getUpperThresholdValue());
		}*/
	}
	public void setState(RebuildDataFilterPanel rb) {
		this.setLBrack(rb.isLeftPar());
		this.setRBrack(rb.isRightPar());
		this.getOperator().setSelectedIndex(rb.getOpIndex());
		this.setColumn(rb.getCol());
		this.panel.setName(rb.getSynthFilterPanel().getSimilarityMeasure());
/*
		this.panel.setRegionsOfInterest(rb.getImageFilterPanel().getRegionTypes());
		if(panel.getSelection().equals(ImagePresenter.DISTANCE)) {
			panel.setupDistancePresenter();
			panel.getDistancePresenter().setRegionIndex(rb.getImageFilterPanel().getSelectedRegionIndex());
			panel.getDistancePresenter().setPoint(rb.getImageFilterPanel().getDistancePoint());
		} else if(panel.getSelection().equals(ImagePresenter.SIMILARITY)) {
			panel.setupSimilarityPresenter();
			panel.getSimilarityPresenter().setPoints(rb.getImageFilterPanel().getDrawnPoints());
			panel.getSimilarityPresenter().setSliceIndex(rb.getImageFilterPanel().getConsiderSliceIndex());
			panel.getSimilarityPresenter().setPidInput(rb.getImageFilterPanel().getPid());
			panel.getSimilarityPresenter().setStudies(rb.getImageFilterPanel().getStudyList());
			panel.getSimilarityPresenter().setRegionTypeIndex(rb.getImageFilterPanel().getSelectedRegionIndex());
			panel.getSimilarityPresenter().setSimMeasureIndex(rb.getImageFilterPanel().getSimMeasureIndex());
			panel.getSimilarityPresenter().setLowerThreshold(rb.getImageFilterPanel().getLowerThreshold());
			panel.getSimilarityPresenter().setUpperThresohld(rb.getImageFilterPanel().getUpperThreshold());
		} else if(panel.getSelection().equals(ImagePresenter.RATIOS)) {
			panel.setupRatioPresenter();
			panel.getRatioPresenter().setUpperIndices(rb.getImageFilterPanel().getTopOpIndices());
			panel.getRatioPresenter().setLowerIndices(rb.getImageFilterPanel().getBotOpIndices());
			panel.getRatioPresenter().setUpperThreshold(rb.getImageFilterPanel().getUpperThreshold());
			panel.getRatioPresenter().setLowerThreshold(rb.getImageFilterPanel().getLowerThreshold());
		}*/
	}
	
	@Override
	public String getDefinitionString() {
		String s = "";
		
		if(this.lbrack()) {
			s += "(";
		}
		s += this.getColumn().getName() + " ";
		s += this.getOperator().getItemText(this.getOpIndex()) + " ";
		if(this.rbrack()) {
			s += ")";
		}
		
		return s;
	}

	@Override
	public void enableExtraBoxes() {

	}

	@Override
	public void disableExtraBoxes() {

	}
	
	protected Widget getIntervalColumns() {
		return null;
	}

}
