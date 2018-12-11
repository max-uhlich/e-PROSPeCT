package btaw.client.modules.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import btaw.client.framework.Presenter;
import btaw.client.modules.image.jaccard.DrawingCanvasSafe;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.Point;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widgetideas.graphics.client.Color;

public class SimilarityDrawPresenter extends ImagePresenter {

	public static final String IMAGES_PATH = "https://ellis-rv.cs.ualberta.ca/btaw/images/colin27_";

	public interface View extends ImagePresenter.View {
		public Button getCompleteRegion();
		public ArrayList<Circle> getPoints();
		public HashMap<String, DrawingCanvasSafe> getAllSlices();
		public TextBox getSliceBox();
		public void setImage(String url);
		public Button getDownButton();
		public Button getUpButton();
		public DrawingCanvasSafe getCanvas();
		void setPoints(List<Point> points);
	}
	
	private View ui;
	private Integer sessionId;
	private int queryId;
	private Boolean doneFlood = false;
	private Boolean firstRegionDone = false;
	
	public SimilarityDrawPresenter(Presenter parent, View ui) {
		super(parent, ui);
		this.ui = ui;
	}
	
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}
	
	public Integer getSessionId() {
		return this.sessionId;
	}
	
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}
	
	@Override
	protected void bindAll() {
		ui.getCompleteRegion().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(firstRegionDone) {
					ui.getCanvas().addCircle(ui.getCanvas().getRegionStart());
					ui.getCanvas().breakLine();
				} else {
					ui.getCanvas().addCircle(ui.getCanvas().getPoints().get(0));
					ui.getCanvas().breakLine();
					firstRegionDone = true;
				}
			}
		});
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
						
						firstRegionDone = false;
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
				firstRegionDone = false;
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
				firstRegionDone = false;
			}
		});
	}

	private String buildUrl(String value) {
		return IMAGES_PATH + value +".png";
	}
	
	public void floodFill() {
		boolean[][] painted = new boolean[256][256];
		ArrayList<String> insStmts = new ArrayList<String>();
		for(Map.Entry<String, DrawingCanvasSafe> e : ((SimilarityDrawView)ui).getAllSlices().entrySet()) {
			ArrayList<Circle> a_tmp = e.getValue().getPoints();
			
			if(a_tmp.isEmpty()) {
				continue;
			}
			
			int z = Integer.parseInt(e.getKey().substring(IMAGES_PATH.length(), e.getKey().length()-4));
			
			for(Circle c: a_tmp) {
				painted[c.x][c.y] = true; 
			}

			Queue<Point> queue = new LinkedList<Point>();
			queue.add(new Point(0, 0));
			
			while(!queue.isEmpty()) {
				Point p = queue.remove();
				
				if((p.x >= 0) && (p.x <= 255) && (p.y >= 0) && (p.y <= 255)) {
					if(!painted[p.x][p.y]) {
						painted[p.x][p.y] = true;
						
						queue.add(new Point(p.x + 1, p.y));
						queue.add(new Point(p.x, p.y + 1));
						queue.add(new Point(p.x - 1, p.y));
						queue.add(new Point(p.x, p.y - 1));
					}
				}
			}
			
			Marker m = new Marker(Color.RED, 1);
			for(int i = 0; i < 256; i++) {
				for(int j = 0; j < 256; j++) {
					if(!painted[i][j]) {
						painted[i][j] = true;
						e.getValue().addCircleSimple(new Circle(i, j, 1, m));
					}
				}
			}
			for(Circle c1 : a_tmp) {
				insStmts.add("INSERT INTO temp.drn_raw_voxels VALUES (nextval('raw_voxel_sqn'), " + this.sessionId +", " + this.queryId + ", " + z + ", " + c1.x + ", " + c1.y + ")");
			}
		}
		rpcService.insertDrawnRegion(insStmts, this.queryId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				doneFlood = true;
			}
			
		});
	}
	
	public boolean isDoneFlood() {
		return doneFlood;
	}
	
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isType(String val) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		// TODO Auto-generated method stub
		
	}
	
	public List<Point> getPoints() {
		List<Point> points = new ArrayList<Point>();
		for(Map.Entry<String, DrawingCanvasSafe> e : ((SimilarityDrawView)ui).getAllSlices().entrySet()) {
			ArrayList<Circle> a_tmp = e.getValue().getPoints();	
			
			if(a_tmp.isEmpty()) {
				continue;
			}
			
			int z = Integer.parseInt(e.getKey().substring(IMAGES_PATH.length(), e.getKey().length()-4));
			
			for(Circle c : a_tmp) {
				points.add(new Point(c.x, c.y, z));
			}
		}
		return points;
	}
	
	public void setPoints(List<Point> points) {
		ui.setPoints(points);
	}
}
