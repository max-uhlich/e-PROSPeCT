package btaw.client.modules.image;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import btaw.client.framework.Presenter;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.RegionContrastFilter;

public class RatioPresenter extends ImagePresenter {

	public interface View extends ImagePresenter.View {
		void populateSegmentation(LinkedHashMap<String, Integer> segTypes);
		Button getTopAddButton();
		Button getBotAddButton();
		Button getTopRemButton();
		Button getBotRemButton();
		void addTop();
		void addBot();
		void remTop();
		void remBot();
		void buildFilter(RegionContrastFilter filter);
		List<Integer> getUpperIndices();
		List<Integer> getBottomIndices();
		String getLowerThresholdValue();
		String getUpperThresholdValue();
		void setUpperIndices(List<Integer> indices);
		void setLowerIndices(List<Integer> indices);
		void setLowerThreshold(String s);
		void setUpperThreshold(String s);
	}
	
	private View ui;
	private HashMap<String, Integer> segTypes;
	private RegionContrastFilter filter;
	
	public RatioPresenter(Presenter parent, View view) {
		super(parent, view);
		this.ui = view;
		filter = new RegionContrastFilter();
		
		filter.setup();
	}

	@Override
	protected void bindAll() {
		ui.getTopAddButton().addClickHandler(new ClickHandler () {
			@Override
			public void onClick(ClickEvent event) {
				RatioPresenter.this.ui.addTop();
			}
		});
		ui.getBotAddButton().addClickHandler(new ClickHandler () {
			@Override
			public void onClick(ClickEvent event) {
				RatioPresenter.this.ui.addBot();
			}
		});
		ui.getTopRemButton().addClickHandler(new ClickHandler () {
			@Override
			public void onClick(ClickEvent event) {
				RatioPresenter.this.ui.remTop();
			}
		});
		ui.getBotRemButton().addClickHandler(new ClickHandler () {
			@Override
			public void onClick(ClickEvent event) {
				RatioPresenter.this.ui.remBot();
			}
		});
	}
	
	@Override
	public Filter getFilter() {
		RegionContrastFilter filter = new RegionContrastFilter();
		ui.buildFilter(filter);
		return filter;
	}

	@Override
	public boolean isType(String val) {
		return val.equals(ImagePresenter.RATIOS);
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		this.segTypes = segTypes;
		this.ui.populateSegmentation(segTypes);
	}

	public List<Integer> getUpperIndices() {
		return ui.getUpperIndices();
	}
	public List<Integer> getBottomIndices() {
		return ui.getBottomIndices();
	}
	public String getLowerThresholdValue() {
		return ui.getLowerThresholdValue();
	}
	public String getUpperThresholdValue() {
		return ui.getUpperThresholdValue();
	}
	public void setUpperIndices(List<Integer> indices) {
		ui.setUpperIndices(indices);
	}
	public void setLowerIndices(List<Integer> indices) {
		ui.setLowerIndices(indices);
	}
	public void setLowerThreshold(String s) {
		ui.setLowerThreshold(s);
	}
	public void setUpperThreshold(String s) {
		ui.setUpperThreshold(s);
	}
}
