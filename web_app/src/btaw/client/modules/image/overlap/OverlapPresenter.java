package btaw.client.modules.image.overlap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widgetideas.graphics.client.Color;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.ImagePresenter;
import btaw.client.modules.image.Marker;
import btaw.client.modules.image.jaccard.DrawingCanvasSafe;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.OverlapImageFilter;

public class OverlapPresenter extends ImagePresenter {

	public static final String IMAGES_PATH = "https://ellis-rv.cs.ualberta.ca/btaw/images/colin27_";
	
	public interface View extends ImagePresenter.View {
		ListBox getOperator();
		TextBox getOScoreLower();
		TextBox getOScoreUpper();
		void setOScoreAsRange();
		void setOScoreAsSingleVal();
		TextBox getSliceBox();
		void setImage(String url);
		Button getDownButton();
		Button getUpButton();
		void buildFilter(OverlapImageFilter filter);
		Button getBreakLine();
		DrawingCanvasSafe getCanvas();
		Button getCompleteLine();
	}
	
	private View ui;
	private int roi_id;
	private Integer session_id;
	private int query_id = Random.nextInt();
	private OverlapImageFilter filter;
	private Boolean doneFlood = false;
	
	public Integer getSessionId() {
		return session_id;
	}

	public void setSession_id(Integer session_id) {
		this.session_id = session_id;
	}

	public int getRoiId() {
		return roi_id;
	}

	public void setRoi_id(int roi_id) {
		this.roi_id = roi_id;
	}

	public OverlapPresenter(Presenter parent,View ui) {
		super(parent, ui);
		this.ui = ui;
		filter = new OverlapImageFilter();
		filter.setup();
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
		
		ui.getOperator().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox operator = ui.getOperator();
				Integer i = operator.getSelectedIndex();
				if(operator.getValue(i).equals("Between(not-inclusive)")) {
					ui.setOScoreAsRange();
				} else {
					ui.setOScoreAsSingleVal();
				}
			}
		});
		ui.getBreakLine().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.getCanvas().breakLine();
			}
		});
		ui.getCompleteLine().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.getCanvas().addCircle(ui.getCanvas().getPoints().get(0));
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
	
	public void querySessionID() {
		rpcService.getSessionID(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Integer result) {
				OverlapPresenter.this.setSession_id(result);
			}
			
		});
	}
	
	private class Point {
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		int x, y;
	};
	
	public void floodFill() {
		boolean[][] painted = new boolean[256][256];
		ArrayList<String> insStmts = new ArrayList<String>();
		for(Map.Entry<String, DrawingCanvasSafe> e : ((OverlapView)ui).getAllSlices().entrySet()) {
			ArrayList<Circle> a_tmp = e.getValue().getPoints();
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
				insStmts.add("INSERT INTO temp.drn_raw_voxels VALUES (nextval('raw_voxel_sqn'), " + this.session_id +", " + query_id + ", " + z + ", " + c1.x + ", " + c1.y + ")");
			}
		}
		rpcService.insertDrawnRegion(insStmts, query_id, new AsyncCallback<Void>() {

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
		filter.setQuery_id(this.query_id);
		filter.setSession_id(this.session_id);
		
		ui.buildFilter(filter);
		
		return filter;
	}

	@Override
	public boolean isType(String val) {
		return false;//return val.equals(ImagePresenter.OVERLAP);
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		// TODO Auto-generated method stub
		
	}

}
