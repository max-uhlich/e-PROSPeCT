package btaw.client.modules.image.distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import btaw.client.framework.AView;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.Marker;
import btaw.shared.model.query.filter.CenterMassImageFilter;
import btaw.shared.model.query.saved.Point;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

public class DistanceView extends AView implements DistancePresenter.View {

	protected static ImageElement image;
	private VerticalPanel mainPanel;
	protected Circle circle;
	protected boolean remove;
	private TextBox slice;
	private HashMap<String,DistanceCanvas> slices;
	private DistanceCanvas canvas;
	private Button sliceDown;
	private Button sliceUp;
	private ListBox segType;
	private LinkedHashMap<String, Integer> segmentations;
	
	public DistanceView() {
		mainPanel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		initWidget(mainPanel);
		
		Label sliceL = new Label("Slice");
		slice = new TextBox();
		slice.setValue("1");
		slice.setWidth("50px");
		sliceDown = new Button("<");
		sliceUp = new Button(">");
		buttonPanel.add(sliceL);
		buttonPanel.add(slice);
		buttonPanel.add(sliceDown);
		buttonPanel.add(sliceUp);
		Label regL = new Label("Region Type: ");
		segType = new ListBox();
		regL.setStyleName("center-Label");
		buttonPanel.add(regL);
		buttonPanel.add(segType);
		
		mainPanel.add(buttonPanel);
		
		slices = new HashMap<String, DistanceCanvas>();
		canvas = buildCanvas();
		mainPanel.add(canvas);
	}
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		for(Map.Entry<String, Integer> entry : segTypes.entrySet()) {
			this.segType.addItem(entry.getKey());
		}
		this.segmentations = segTypes;
	}

	private DistanceCanvas buildCanvas() {
		DistanceCanvas canvas = new DistanceCanvas(256, 256, new Marker(Color.RED,3));
		canvas.getElement().addClassName("cursor-crosshair");
		slices.put(getSliceUrl(), canvas);
		return canvas;
	}


	private String getSliceUrl() {
		return DistancePresenter.IMAGES_PATH + getSlice() + ".png";
	}


	private Integer getSlice() {
		return Integer.parseInt(slice.getValue());
	}


	@Override
	public ArrayList<Circle> getPoints() {
		return canvas.getPoints();
	}


	@Override
	public TextBox getSliceBox() {
		return slice;
	}


	@Override
	public void setImage(String url) {
		mainPanel.remove(canvas);
		canvas = this.slices.get(url);
		if (canvas == null)
			canvas = buildCanvas();
		canvas.setImage(url);
		mainPanel.add(canvas);
	}


	@Override
	public Button getDownButton() {
		return sliceDown;
	}


	@Override
	public Button getUpButton() {
		return sliceUp;
	}


	@Override
	public void buildFilter(CenterMassImageFilter filter) {
		System.err.println("buildFilter reporting in!");
		filter.setXPixels(canvas.getCircle().x);
		filter.setYPixels(canvas.getCircle().y);
		filter.setDistancePixels(canvas.getCircle().r);
		filter.setzSlice(Integer.parseInt(this.slice.getValue()));
		filter.setType(segmentations.get(segType.getItemText(segType.getSelectedIndex())));
	}
	
	public int getRegionIndex() {
		return segType.getSelectedIndex();
	}
	
	public Point getPoint() {
		return new Point(canvas.getCircle().x, canvas.getCircle().y, this.getSlice(), canvas.getCircle().r);
	}
	
	public void setRegionIndex(int i) {
		segType.setSelectedIndex(i);
	}
	
	public void setPoint(Point p) {
		slice.setText(Integer.toString(p.z));
		canvas.setCircle(new Circle(p.x, p.y, p.r, new Marker(Color.RED, 3)));
	}

}
