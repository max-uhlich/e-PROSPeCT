package btaw.client.modules.image;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.shared.model.query.filter.RegionContrastFilter;

public class RatioView extends AView implements RatioPresenter.View {

	private VerticalPanel mainPanel;
	
	private HorizontalPanel topAddPanel;
	private HorizontalPanel topAddWrapperPanel;
	private HorizontalPanel topVolPanel;
	private HorizontalPanel midLinePanel;
	private HorizontalPanel bottomVolPanel;
	private HorizontalPanel bottomAddWrapperPanel;
	private HorizontalPanel bottomAddPanel;
	private HorizontalPanel thresholdPanel;
	
	private Button topAdd;
	private Button topRem;
	private ArrayList<ListBox> topVols;
	private HTML midLine;
	private ArrayList<ListBox> bottomVols;
	private Button bottomRem;
	private Button bottomAdd;
	
	private LinkedHashMap<String, Integer> segTypes;
	private ListBox tempListBox;
	
	private ArrayList<String> ops;
	
	private TextBox lwr;
	private TextBox upr;
	
	public RatioView() {
		mainPanel = new VerticalPanel();
		topAddPanel = new HorizontalPanel();
		topAddWrapperPanel = new HorizontalPanel();
		topVolPanel = new HorizontalPanel();
		midLinePanel = new HorizontalPanel();
		bottomVolPanel = new HorizontalPanel();
		bottomAddWrapperPanel = new HorizontalPanel();
		bottomAddPanel = new HorizontalPanel();
		thresholdPanel = new HorizontalPanel();
		initWidget(mainPanel);
		
		topAddPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		topVolPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		midLinePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		bottomVolPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		bottomAddPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		
		topAdd = new Button("Add");
		topRem = new Button("Remove");
		topAddWrapperPanel.add(topRem);
		topAddWrapperPanel.add(topAdd);
		topAddPanel.add(topAddWrapperPanel);
		topAddPanel.setWidth("100%");
		
		topVols = new ArrayList<ListBox>();
		tempListBox = new ListBox();
		topVols.add(tempListBox);
		for(ListBox lbt: topVols)
			topVolPanel.add(lbt);
		topVolPanel.setWidth("100%");
		
		midLine = new HTML("<hr/>");
		midLinePanel.add(midLine);
		midLinePanel.setWidth("100%");
		
		bottomVols = new ArrayList<ListBox>();
		tempListBox = new ListBox();
		bottomVols.add(tempListBox);
		for(ListBox lbb: bottomVols)
			bottomVolPanel.add(lbb);
		bottomVolPanel.setWidth("100%");
		
		bottomAdd = new Button("Add");
		bottomRem = new Button("Remove");
		bottomAddWrapperPanel.add(bottomRem);
		bottomAddWrapperPanel.add(bottomAdd);
		bottomAddPanel.add(bottomAddWrapperPanel);
		bottomAddPanel.setWidth("100%");
		
		Label thresholdL = new Label("Threshold: ");
		thresholdL.setStyleName("center-Label");
		thresholdL.setWordWrap(false);
		Label thresholdOpL1 = new Label("<=");
		thresholdOpL1.setStyleName("center-Label");
		Label thresholdOpL2 = new Label("<=");
		thresholdOpL2.setStyleName("center-Label");
		Label ratioL = new Label("Ratio");
		ratioL.setStyleName("center-Label");
		lwr = new TextBox();
		upr = new TextBox();
		lwr.setWidth("25px");
		lwr.setValue("0");
		upr.setWidth("25px");
		upr.setValue("100");
		thresholdPanel.add(thresholdL);
		thresholdPanel.add(lwr);
		thresholdPanel.add(thresholdOpL1);
		thresholdPanel.add(ratioL);
		thresholdPanel.add(thresholdOpL2);
		thresholdPanel.add(upr);
		
		mainPanel.setWidth("100%");
		mainPanel.add(topAddPanel);
		mainPanel.add(topVolPanel);
		mainPanel.add(midLinePanel);
		mainPanel.add(bottomVolPanel);
		mainPanel.add(bottomAddPanel);
		mainPanel.add(thresholdPanel);
		mainPanel.getElement().getStyle().setZIndex(3);
		
		ops = new ArrayList<String>();
		ops.add("+");
		ops.add("-");
	}
	
	@Override
	public ArrayList<Circle> getPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateSegmentation(LinkedHashMap<String, Integer> segTypes) {
		this.segTypes = segTypes;
		for(ListBox lbt: topVols) {
			for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
				lbt.addItem(entry.getKey());
			}
		}
		for(ListBox lbb: bottomVols) {
			for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
				lbb.addItem(entry.getKey());
			}
		}
	}
	
	@Override
	public Button getTopAddButton() {
		return topAdd;
	}
	
	@Override
	public Button getBotAddButton() {
		return bottomAdd;
	}
	
	@Override
	public Button getTopRemButton() {
		return topRem;
	}
	
	@Override
	public Button getBotRemButton() {
		return bottomRem;
	}
	
	@Override
	public void addTop() {
		ListBox tmpListOps = new ListBox();
		ListBox tmpListVols = new ListBox();
		for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
			tmpListVols.addItem(entry.getKey());
		}
		for(String s: ops) {
			tmpListOps.addItem(s);
		}
		topVols.add(tmpListOps);
		topVols.add(tmpListVols);
		for(ListBox lbt: topVols) {
			topVolPanel.add(lbt);
		}
	}
	
	@Override
	public void remTop() {
		if(topVols.size() <= 1) {
			return;
		}
		topVols.get(topVols.size() - 1).setVisible(false);
		topVols.remove(topVols.size() - 1);
		topVols.get(topVols.size() - 1).setVisible(false);
		topVols.remove(topVols.size() - 1);
	}
	
	@Override
	public void addBot() {
		ListBox tmpListOps = new ListBox();
		ListBox tmpListVols = new ListBox();
		for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
			tmpListVols.addItem(entry.getKey());
		}
		for(String s: ops) {
			tmpListOps.addItem(s);
		}
		bottomVols.add(tmpListOps);
		bottomVols.add(tmpListVols);
		for(ListBox lbb: bottomVols) {
			bottomVolPanel.add(lbb);
		}
	}
	
	@Override
	public void remBot() {
		if(bottomVols.size() <= 1) {
			return;
		}
		bottomVols.get(bottomVols.size() - 1).setVisible(false);
		bottomVols.remove(bottomVols.size() - 1);
		bottomVols.get(bottomVols.size() - 1).setVisible(false);
		bottomVols.remove(bottomVols.size() - 1);
	}
	
	@Override
	public void buildFilter(RegionContrastFilter filter) {
		String tmp = "";

		String tmpNumer = "CAST(";
		for(ListBox lb1: topVols) {
			tmp = lb1.getItemText(lb1.getSelectedIndex());
			if(tmp.equals("+") || tmp.equals("-")) {
				tmpNumer += tmp + " ";
			} else {
				tmpNumer += "btaw.roi_volume." + tmp + " ";
			}
		}
		tmpNumer += "AS FLOAT)/";

		String tmpDenom = "(";
		for(ListBox lb2: bottomVols) {
			tmp = lb2.getItemText(lb2.getSelectedIndex());
			if(tmp.equals("+") || tmp.equals("-")) {
				tmpDenom += tmp + " ";
			} else {
				tmpDenom += "btaw.roi_volume." + tmp + " ";
			}
		}
		tmpDenom += ")";
		
		filter.setSelectClause(tmpNumer + tmpDenom + " as ratio_score");
		filter.setFromClause(" btaw.roi_volume ");
		filter.setWhereClause(" btaw.roi_volume.study_id=btaw.btap_study.study_id ");
		filter.setSqlFilter(tmpNumer + tmpDenom + " >= " + lwr.getValue() + " AND " + tmpNumer + tmpDenom + " <= " + upr.getValue() + " AND " + tmpDenom + " <> 0 ");
	}
	
	public List<Integer> getUpperIndices() {
		List<Integer> ret = new ArrayList<Integer>();
		for(ListBox lb : topVols) {
			ret.add(lb.getSelectedIndex());
		}
		return ret;
	}
	
	public List<Integer> getBottomIndices() {
		List<Integer> ret = new ArrayList<Integer>();
		for(ListBox lb : bottomVols) {
			ret.add(lb.getSelectedIndex());
		}
		return ret;
	}

	public String getLowerThresholdValue() {
		return lwr.getText();
	}
	
	public String getUpperThresholdValue() {
		return upr.getText();
	}
	
	public void setUpperIndices(List<Integer> indices) {
		boolean op = false;
		for(Integer i : indices) {
			if(op) {
				topVols.add(getOpTypeListBox(i));
				op = false;
			} else {
				topVols.add(getSegTypeListBox(i));
				op = true;
			}
		}
	}
	public void setLowerIndices(List<Integer> indices) {
		boolean op = false;
		for(Integer i : indices) {
			if(op) {
				bottomVols.add(getOpTypeListBox(i));
				op = false;
			} else {
				bottomVols.add(getSegTypeListBox(i));
				op = true;
			}
		}
	}
	public void setLowerThreshold(String s) {
		this.lwr.setText(s);
	}
	public void setUpperThreshold(String s) {
		this.upr.setText(s);
	}
	
	public ListBox getSegTypeListBox(int i) {
		ListBox lb = new ListBox();
		for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
			lb.addItem(entry.getKey());
		}
		lb.setSelectedIndex(i);
		return lb;
	}
	
	public ListBox getOpTypeListBox(int i) {
		ListBox lb = new ListBox();
		for(String s: ops) {
			lb.addItem(s);
		}
		lb.setSelectedIndex(i);
		return lb;
	}
}
