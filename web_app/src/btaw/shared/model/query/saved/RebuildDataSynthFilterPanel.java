package btaw.shared.model.query.saved;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class RebuildDataSynthFilterPanel implements Serializable {

	private static final long serialVersionUID = 5338881925767728622L;
	private String similarityMeasure;
	private LinkedHashMap<String, Integer> regionTypes;
	private int selectedRegionIndex;
	private int considerSliceIndex;
	private int simMeasureIndex;
	private String lowerThreshold;
	private String upperThreshold;
	private List<Point> drawnPoints;
	private String pid;
	private List<String> studyList;
	private int studyListIndex;
	private List<Integer> topOpIndices;
	private List<Integer> botOpIndices;
	private Point distancePoint;
	
	public String getSimilarityMeasure() {
		return similarityMeasure;
	}
	public void setSimilarityMeasure(String similarityMeasure) {
		this.similarityMeasure = similarityMeasure;
	}
	public LinkedHashMap<String, Integer> getRegionTypes() {
		return regionTypes;
	}
	public void setRegionTypes(LinkedHashMap<String, Integer> regionTypes) {
		this.regionTypes = regionTypes;
	}
	public int getSelectedRegionIndex() {
		return selectedRegionIndex;
	}
	public void setSelectedRegionIndex(int selectedRegionIndex) {
		this.selectedRegionIndex = selectedRegionIndex;
	}
	public int getConsiderSliceIndex() {
		return considerSliceIndex;
	}
	public void setConsiderSliceIndex(int considerSliceIndex) {
		this.considerSliceIndex = considerSliceIndex;
	}
	public int getSimMeasureIndex() {
		return simMeasureIndex;
	}
	public void setSimMeasureIndex(int simMeasureIndex) {
		this.simMeasureIndex = simMeasureIndex;
	}
	public String getLowerThreshold() {
		return lowerThreshold;
	}
	public void setLowerThreshold(String lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}
	public String getUpperThreshold() {
		return upperThreshold;
	}
	public void setUpperThreshold(String upperThreshold) {
		this.upperThreshold = upperThreshold;
	}
	public List<Point> getDrawnPoints() {
		return drawnPoints;
	}
	public void setDrawnPoints(List<Point> drawnPoints) {
		this.drawnPoints = drawnPoints;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public List<String> getStudyList() {
		return studyList;
	}
	public void setStudyList(List<String> studyList) {
		this.studyList = studyList;
	}
	public int getStudyListIndex() {
		return studyListIndex;
	}
	public void setStudyListIndex(int studyListIndex) {
		this.studyListIndex = studyListIndex;
	}
	public List<Integer> getTopOpIndices() {
		return topOpIndices;
	}
	public void setTopOpIndices(List<Integer> topOpIndices) {
		this.topOpIndices = topOpIndices;
	}
	public List<Integer> getBotOpIndices() {
		return botOpIndices;
	}
	public void setBotOpIndices(List<Integer> botOpIndices) {
		this.botOpIndices = botOpIndices;
	}
	public Point getDistancePoint() {
		return distancePoint;
	}
	public void setDistancePoint(Point distancePoint) {
		this.distancePoint = distancePoint;
	}

}
