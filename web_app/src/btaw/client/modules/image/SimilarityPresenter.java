package btaw.client.modules.image;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.main.MainPresenter;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.ImageQueryFilter;
import btaw.shared.model.query.saved.Point;

public class SimilarityPresenter extends ImagePresenter implements ClickHandler {

	public interface View extends ImagePresenter.View {
		void buildFilter(ImageQueryFilter filter);
		ListBox getThresholdOperator();
		Button getDrawQuery();
		Button getSelectStudy();
		ListBox getQueryType();
		int getRoiId();
		Label getDisplayFormula();
		boolean isAllSlices();
		int getRegionTypeIndex();
		int getSliceIndex();
		int getSimMeasureIndex();
		String getLowerThreshold();
		String getUpperThreshold();
		void setRegionTypeIndex(int i);
		void setSliceIndex(int i);
		void setSimMeasureIndex(int i);
		void setLowerThreshold(String s);
		void setUpperThresohld(String s);
	}

	public static final String QUERYOVERLAP = "(A \u2229 B) / A";
	public static final String PATIENTOVERLAP = "(A \u2229 B) / B";
	public static final String INTERSECT = "A \u2229 B";
	public static final String JACCARD = "(A \u2229 B) / (A \u222A B)";
	
	private View ui;
	private int roiId;
	private int queryId = Random.nextInt();
	private Integer sessionId = null;
	private ImageQueryFilter filter;
	private SimilarityDrawPresenter drawP = null;
	private SimilarityStudyPresenter studyP = null;
	private Presenter presenter;
	private Button cancelButton;
	private Button doneButton;
	private DialogBox imageSelectionBox;
	private int studyId = -1;
	
	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public int getRoiId() {
		return roiId;
	}

	public void setRoiId(int roiId) {
		this.roiId = roiId;
	}
	
	public SimilarityPresenter(Presenter parent, View ui) {
		super(parent, ui);
		this.ui = ui;
		filter = new ImageQueryFilter();
		filter.setup();
		this.presenter = parent;
	}
	
	@Override
	protected void bindAll() {
		ui.getQueryType().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox queryType = ui.getQueryType();
				Integer i = queryType.getSelectedIndex();
				ui.getDisplayFormula().setText(queryType.getItemText(i));
			}
		});
		ui.getDrawQuery().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(imageSelectionBox != null) {
					imageSelectionBox.center();
					return;
				}
				if(drawP == null) {
					SimilarityPresenter.this.drawP = new SimilarityDrawPresenter(presenter, new SimilarityDrawView());
				}
				SimilarityPresenter.this.drawP.setActive();
				SimilarityPresenter.this.drawP.setQueryId(queryId);
				imageSelectionBox = buildDialogBox(SimilarityPresenter.this.drawP.getDisplay());
				imageSelectionBox.center();
				studyP = null;
			}
		});
		ui.getSelectStudy().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(imageSelectionBox != null) {
					imageSelectionBox.center();
					return;
				}
				if(studyP == null) {
					SimilarityPresenter.this.studyP = new SimilarityStudyPresenter(presenter, new SimilarityStudyView());
				}
				SimilarityPresenter.this.studyP.setActive();
				imageSelectionBox = buildDialogBox(SimilarityPresenter.this.studyP.getDisplay());
				imageSelectionBox.center();
				drawP = null;
			}
		});
	}
	
	private DialogBox buildDialogBox(IView view) {
	    final DialogBox dialogBox = new DialogBox();
	    dialogBox.setText("Image Query");
	    VerticalPanel container = new VerticalPanel();
	    
	    container.add(view.getRootWidget());
	    
	    HorizontalPanel buttonPanelWrapper = new HorizontalPanel();
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    doneButton = new Button("Done");
	    cancelButton = new Button("Cancel");
	    cancelButton.addClickHandler(this);
	    doneButton.addClickHandler(this);
	    
	    buttonPanel.add(doneButton);
	    buttonPanel.add(cancelButton);
	    buttonPanel.setSpacing(5);
	    buttonPanelWrapper.setWidth("100%");
	    buttonPanelWrapper.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
	    buttonPanelWrapper.add(buttonPanel);
	    
	    container.add(buttonPanelWrapper);
	    dialogBox.add(container);
	    dialogBox.getElement().getStyle().setZIndex(3);
		return dialogBox;
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
				SimilarityPresenter.this.setSessionId(result);
			}
			
		});
	}
	
	@Override
	public Filter getFilter() {
		boolean intersect = false;
		boolean overlapquery = false;
		
		filter.setQueryId(this.queryId);
		if (this.sessionId != null) {
			filter.setSessionId(this.sessionId);
		}
		filter.setStudyId(this.studyId);
		filter.setRegionId(ui.getRoiId());
		
		String queryType = ui.getQueryType().getItemText(ui.getQueryType().getSelectedIndex());
		String funcName = "";
		if (queryType.equals(SimilarityPresenter.INTERSECT)) {
			funcName += "intersect_";
			intersect = true;
		} else if (queryType.equals(SimilarityPresenter.QUERYOVERLAP)) {
			funcName += "overlapquery_";
			overlapquery = true;
		} else if (queryType.equals(SimilarityPresenter.PATIENTOVERLAP)) {
			funcName += "overlappatient_";
		} else if (queryType.equals(SimilarityPresenter.JACCARD)) {
			funcName += "jaccard_";
		}
		
		if(drawP != null) {
			funcName += "drawn";
			filter.setDrawn(true);
		} else if (studyP != null) {
			funcName += "study";
			filter.setDrawn(false);
		}
		
		if(!ui.isAllSlices() && drawP != null && !intersect && !overlapquery) {
			funcName += "_dslices";
		}
		
		filter.setFunction(funcName);
		ui.buildFilter(filter);
		
		return filter;
	}

	@Override
	public boolean isType(String val) {
		return val.equals(ImagePresenter.SIMILARITY);
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		((SimilarityView)this.ui).populateSegmentation(segTypes);
	}

	public void floodFill() {
		Timer t = new Timer() {
			@Override
			public void run() {
				if(drawP != null) {
					if(drawP.isDoneFlood()) {
						imageSelectionBox.hide();
						imageSelectionBox = null;
						MainPresenter.get().finished();
					} else {
						this.schedule(500);
					}
				}
			}
		};
		if(drawP != null) {
			drawP.floodFill();
		}
		t.schedule(500);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == doneButton) {
			if(drawP != null) {
				MainPresenter.get().working(true);
				SimilarityPresenter.this.querySessionID();
				Timer t = new Timer() {
					public void run() {
						if(SimilarityPresenter.this.sessionId == null) {
							this.schedule(500);
						} else {
							drawP.setSessionId(SimilarityPresenter.this.sessionId);
							SimilarityPresenter.this.floodFill();
						}
					}
				};
				t.schedule(500);
			} else if (studyP != null){
				studyId = studyP.getStudyId();
				String pid = studyP.getPidInput();
				ui.getSelectStudy().setText("Select Case From Database (PID:" +pid + ", StudyID: " + studyId +")");
				imageSelectionBox.hide();
				imageSelectionBox = null;
			}
		} else if (event.getSource() == cancelButton) {
			imageSelectionBox.hide();
			imageSelectionBox = null;
			if(drawP != null) {
				drawP = null;
			} else if (studyP != null) {
				drawP = null;
			}
		}
	}
	
	public List<Point> getPoints() {
		if(drawP == null) 
			return null;
		return drawP.getPoints();
	}
	
	public List<String> getStudies() {
		if(studyP == null)
			return null;
		return studyP.getStudies();
	}
	
	public String getPidInput() {
		if(studyP == null) 
			return null;
		return studyP.getPidInput();
	}

	public int getRegionTypeIndex() {
		return ui.getRegionTypeIndex();
	}
	public int getSliceIndex() {
		return ui.getSliceIndex();
	}
	public int getSimMeasureIndex() {
		return ui.getSimMeasureIndex();
	}
	public String getLowerThreshold() {
		return ui.getLowerThreshold();
	}
	public String getUpperThreshold() {
		return ui.getUpperThreshold();
	}
	public void setPoints(List<Point> points) {
		if(points == null) {
			return;
		}
		if(drawP == null) {
			SimilarityPresenter.this.drawP = new SimilarityDrawPresenter(presenter, new SimilarityDrawView());
		}
		SimilarityPresenter.this.drawP.setActive();
		SimilarityPresenter.this.drawP.setQueryId(queryId);
		imageSelectionBox = buildDialogBox(SimilarityPresenter.this.drawP.getDisplay());
		studyP = null;
		drawP.setPoints(points);
	}
	public void setStudies(List<String> studies) {
		if(studies == null) {
			return;
		}
		if(studyP == null) {
			SimilarityPresenter.this.studyP = new SimilarityStudyPresenter(presenter, new SimilarityStudyView());
		}
		SimilarityPresenter.this.studyP.setActive();
		imageSelectionBox = buildDialogBox(SimilarityPresenter.this.studyP.getDisplay());
		drawP = null;
		studyP.setStudies(studies);
	}
	public void setPidInput(String s) {
		if (s == null) {
			return;
		}
		if(studyP == null) {
			SimilarityPresenter.this.studyP = new SimilarityStudyPresenter(presenter, new SimilarityStudyView());
		}
		SimilarityPresenter.this.studyP.setActive();
		imageSelectionBox = buildDialogBox(SimilarityPresenter.this.studyP.getDisplay());
		drawP = null;
		studyP.setPidInput(s);
	}
	public void setRegionTypeIndex(int i) {
		ui.setRegionTypeIndex(i);
	}
	public void setSliceIndex(int i) {
		ui.setSliceIndex(i);
	}
	public void setSimMeasureIndex(int i) {
		ui.setSimMeasureIndex(i);
	}
	public void setLowerThreshold(String s) {
		ui.setLowerThreshold(s);
	}
	public void setUpperThresohld(String s) {
		ui.setUpperThresohld(s);
	}
}
