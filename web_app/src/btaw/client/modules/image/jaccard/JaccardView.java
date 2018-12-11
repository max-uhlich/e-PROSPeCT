package btaw.client.modules.image.jaccard;

import java.util.ArrayList;
import java.util.HashMap;
import btaw.client.framework.AView;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.Marker;
import btaw.client.modules.image.distance.DistancePresenter;
import btaw.shared.model.query.filter.JaccardImageFilter;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

public class JaccardView extends AView implements JaccardPresenter.View {

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
	private TextBox jscoreLower;
	private TextBox jscoreUpper;
	private HorizontalPanel jscoreControls;
	private Button breakLine;
	private Button finishLine;
	private Label jscoreRangeL;
	

	public JaccardView() {
		mainPanel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		jscoreControls = new HorizontalPanel();
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
		
		slices = new HashMap<String, DrawingCanvasSafe>();
		canvas = buildCanvas();
		
		mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		mainPanel.add(buttonPanel);
		mainPanel.add(jscoreControls);
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
	public DrawingCanvasSafe getCanvas() {
		return canvas;
	}
	
	@Override
	public void buildFilter(JaccardImageFilter filter) {
		
		if(operator.getItemText(operator.getSelectedIndex()).equals("=")) {
			filter.setJaccardFilter(filter.getTable_alias() + ".image_score=" + jscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals("<")) {
			filter.setJaccardFilter(filter.getTable_alias() + ".image_score<" + jscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals(">")) {
			filter.setJaccardFilter(filter.getTable_alias() + ".image_score>" + jscoreLower.getText());
		} else if(operator.getItemText(operator.getSelectedIndex()).equals("Between(not-inclusive)")) {
			filter.setJaccardFilter(filter.getTable_alias() + ".image_score between " + jscoreLower.getText() + " and " + jscoreUpper.getText());
		}
	}

}
