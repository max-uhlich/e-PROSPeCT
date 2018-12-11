package btaw.client.modules.image.distance;

import java.util.LinkedHashMap;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.image.ImagePresenter;
import btaw.shared.model.query.filter.CenterMassImageFilter;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.Point;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;

public class DistancePresenter extends ImagePresenter {

	public static final String IMAGES_PATH = "https://ellis-rv.cs.ualberta.ca/btaw/images/colin27_";

	public interface View extends ImagePresenter.View {
		TextBox getSliceBox();

		void setImage(String url);

		Button getDownButton();

		Button getUpButton();

		void buildFilter(CenterMassImageFilter filter);
		
		void setSegTypes(LinkedHashMap<String, Integer> segTypes);
		
		int getRegionIndex();
		
		Point getPoint();
		
		void setRegionIndex(int i);
		
		void setPoint(Point p);
	}

	private View ui;

	public DistancePresenter(Presenter parent, View ui) {
		super(parent, ui);
		this.ui = ui;
	}

	@Override
	protected void bindAll() {
		ui.getSliceBox().addValueChangeHandler(
				new ValueChangeHandler<String>() {

					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						Integer slice = 1;
						TextBox sliceBox = ui.getSliceBox();
						try {
							slice = Integer.parseInt(sliceBox.getValue());
							if (slice < 1 || slice > 22) {
								sliceBox.setValue("1");
								ui.setImage(buildUrl("1"));
								return;
							}

						} catch (Exception e) {
							sliceBox.setValue("1");
							ui.setImage(buildUrl("1"));
							return;
						}
						System.err.println(ui.toString());
						ui.setImage(buildUrl(event.getValue()));
					}

				});
		ui.setImage(buildUrl("1"));
		ui.getDownButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				TextBox sliceBox = ui.getSliceBox();
				Integer i;
				try {
					i = Integer.parseInt(sliceBox.getValue());
					i = (i > 2 ? i-1 : 1);
				} catch (Exception e) {
					i = 1;
				}
				sliceBox.setText(i.toString());
				ui.setImage(buildUrl(sliceBox.getText()));
			}
		});
		ui.getUpButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				TextBox sliceBox = ui.getSliceBox();
				Integer i;
				try {
					i = Integer.parseInt(sliceBox.getValue());
					i = (i < 22) ? i+1 : 22;
				} catch (Exception e) {
					e.printStackTrace();
					i = 1;
				}
				sliceBox.setText(i.toString());
				ui.setImage(buildUrl(sliceBox.getText()));
			}
		});
	}

	private String buildUrl(String value) {
		return IMAGES_PATH + value +".png";
	}

	@Override
	public IView getDisplay() {
		return ui;
	}

	@Override
	protected void activated(HasWidgets container) {
		
	}

	@Override
	public Filter getFilter() {
		CenterMassImageFilter filter = new CenterMassImageFilter();
		
		ui.buildFilter(filter);
		
		return filter;
	}

	@Override
	public boolean isType(String val) {
		return val.equals(DISTANCE);
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		ui.setSegTypes(segTypes);
	}
	
	public int getRegionIndex() {
		return ui.getRegionIndex();
	}
	
	public Point getPoint() {
		return ui.getPoint();
	}
	
	public void setRegionIndex(int i) {
		ui.setRegionIndex(i);
	}
	
	public void setPoint(Point p) {
		ui.setPoint(p);
	}
}
