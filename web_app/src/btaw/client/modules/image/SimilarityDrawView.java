package btaw.client.modules.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import btaw.client.framework.AView;
import btaw.client.modules.image.jaccard.DrawingCanvasSafe;
import btaw.shared.model.query.saved.Point;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

public class SimilarityDrawView extends AView implements SimilarityDrawPresenter.View {
	protected static final Color COLOR = Color.RED;
	protected static final int THICKNESS = 1;
	
	protected static ImageElement image;
	private VerticalPanel mainPanel;
	protected Circle circle;
	protected boolean remove;
	private TextBox slice;
	private HashMap<String,DrawingCanvasSafe> slices;
	private DrawingCanvasSafe canvas;
	private Button sliceDown;
	private Button sliceUp;
	private Button completeRegion;
	
	public SimilarityDrawView() {

		mainPanel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		initWidget(mainPanel);
		
		Label sliceL = new Label("Slice");
		sliceL.setStyleName("center-Label");
		slice = new TextBox();
		slice.setValue("1");
		slice.setWidth("50px");
		sliceDown = new Button("<");
		sliceUp = new Button(">");
		completeRegion = new Button("Complete Region");
		buttonPanel.add(sliceL);
		buttonPanel.add(slice);
		buttonPanel.add(sliceDown);
		buttonPanel.add(sliceUp);
		buttonPanel.add(completeRegion);
		
		slices = new HashMap<String, DrawingCanvasSafe>();
		canvas = buildCanvas();
		
		mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		mainPanel.add(buttonPanel);
		mainPanel.add(canvas);
	}
	
	private DrawingCanvasSafe buildCanvas() {
		DrawingCanvasSafe canvas = new DrawingCanvasSafe(256, 256, new Marker(Color.RED,1));
		canvas.getElement().addClassName("cursor-crosshair");
		slices.put(getSliceUrl(), canvas);
		return canvas;
	}
	
	private DrawingCanvasSafe buildCanvas(int i) {
		DrawingCanvasSafe canvas = new DrawingCanvasSafe(256, 256, new Marker(Color.RED,1));
		canvas.getElement().addClassName("cursor-crosshair");
		slices.put(getSliceUrl(i), canvas);
		return canvas;
	}
	
	private String getSliceUrl() {
		return SimilarityDrawPresenter.IMAGES_PATH + getSlice() + ".png";
	}
	
	private String getSliceUrl(int i) {
		return SimilarityDrawPresenter.IMAGES_PATH + i + ".png";
	}
	
	private Integer getSlice() {
		return Integer.parseInt(slice.getValue());
	}
	
	@Override
	public Button getCompleteRegion() {
		return completeRegion;
	}
	
	@Override
	public ArrayList<Circle> getPoints() {
		return canvas.getPoints();
	}	
	
	@Override
	public HashMap<String, DrawingCanvasSafe> getAllSlices() {
		return slices;
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
	public DrawingCanvasSafe getCanvas() {
		return canvas;
	}
	
	public void setPoints(List<Point> points) {
		for(Point p : points) {
			if(slices.containsKey(this.getSliceUrl(p.z))) {
				slices.get(this.getSliceUrl(p.z)).addCircleSimple(new Circle(p.x, p.y, 1, new Marker(Color.RED, 1)));
			} else {
				this.buildCanvas(p.z);
				slices.get(this.getSliceUrl(p.z)).addCircleSimple(new Circle(p.x, p.y, 1, new Marker(Color.RED, 1)));
			}
		}
	}
}
