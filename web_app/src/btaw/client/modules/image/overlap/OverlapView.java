package btaw.client.modules.image.overlap;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

import btaw.client.framework.AView;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.Marker;
import btaw.client.modules.image.distance.DistancePresenter;
import btaw.client.modules.image.jaccard.DrawingCanvasSafe;
import btaw.shared.model.query.filter.OverlapImageFilter;

public class OverlapView extends AView implements OverlapPresenter.View {
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
	private ListBox operator;
	private TextBox oscoreLower;
	private TextBox oscoreUpper;
	private HorizontalPanel oscoreControls;
	private Button breakLine;
	private Button finishLine;
	private Label oscoreRangeL;


	public OverlapView() {
		mainPanel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		oscoreControls = new HorizontalPanel();
		initWidget(mainPanel);
		
		Label sliceL = new Label("Slice");
		sliceL.setStyleName("center-Label");
		slice = new TextBox();
		slice.setValue("1");
		slice.setWidth("50px");
		sliceDown = new Button("<");
		sliceUp = new Button(">");
		breakLine = new Button("Break Line Segment");
		finishLine = new Button("Complete Line Segment");
		buttonPanel.add(sliceL);
		buttonPanel.add(slice);
		buttonPanel.add(sliceDown);
		buttonPanel.add(sliceUp);
		buttonPanel.add(breakLine);
		buttonPanel.add(finishLine);
		
		Label oscoreL = new Label("Overlap Score");
		oscoreL.setStyleName("center-Label");
		operator = new ListBox();
		operator.addItem("=");
		operator.addItem("<");
		operator.addItem(">");
		operator.addItem("Between(not-inclusive)");
		operator.setVisibleItemCount(1);
		oscoreRangeL = new Label("And");
		oscoreRangeL.setStyleName("center-Label");
		oscoreLower = new TextBox();
		oscoreLower.setValue("0");
		oscoreLower.setWidth("25px");
		oscoreUpper = new TextBox();
		oscoreUpper.setValue("1");
		oscoreUpper.setWidth("25px");
		oscoreControls.add(oscoreL);
		oscoreControls.add(operator);
		oscoreControls.add(oscoreLower);
		
		slices = new HashMap<String, DrawingCanvasSafe>();
		canvas = buildCanvas();

		mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		mainPanel.add(buttonPanel);
		mainPanel.add(oscoreControls);
		mainPanel.add(canvas);
	}

	private DrawingCanvasSafe buildCanvas() {
		DrawingCanvasSafe canvas = new DrawingCanvasSafe(256, 256, new Marker(Color.RED,1));
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

	public Button getCompleteLine() {
		return finishLine;
	}
	@Override
	public Button getBreakLine() {
		return breakLine;
	}

	@Override
	public ArrayList<Circle> getPoints() {
		return canvas.getPoints();
	}
	
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
	public ListBox getOperator() {
		return operator;
	}
	
	@Override
	public TextBox getOScoreLower() {
		return oscoreLower;
	}
	
	@Override
	public TextBox getOScoreUpper() {
		return oscoreUpper;
	}
	
	@Override
	public void setOScoreAsRange() {
		if(oscoreUpper.isAttached()) {
			return;
		} else {
			oscoreControls.add(oscoreRangeL);
			oscoreControls.add(oscoreUpper);
		}
	}
	
	@Override
	public void setOScoreAsSingleVal() {
		if(oscoreUpper.isAttached()) {
			oscoreControls.remove(oscoreRangeL);
			oscoreControls.remove(oscoreUpper);
		}
	}
	@Override
	public DrawingCanvasSafe getCanvas() {
		return canvas;
	}
	
	@Override
	public void buildFilter(OverlapImageFilter filter) {
		
		if(operator.getItemText(operator.getSelectedIndex()).equals("=")) {
			filter.setOverlapFilter(filter.getTable_alias() + ".image_score=" + oscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals("<")) {
			filter.setOverlapFilter(filter.getTable_alias() + ".image_score<" + oscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals(">")) {
			filter.setOverlapFilter(filter.getTable_alias() + ".image_score>" + oscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals("Between(not-inclusive)")) {
			filter.setOverlapFilter(filter.getTable_alias() + ".image_score between " + oscoreLower.getText() + " and " + oscoreUpper.getText());
		}
	}
	
}
