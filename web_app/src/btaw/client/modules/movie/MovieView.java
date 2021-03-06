package btaw.client.modules.movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import btaw.client.framework.AView;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.movie.MoviePresenter.View;
import btaw.client.view.widgets.SliderBar;
import btaw.client.view.widgets.SliderBarMovie;
import btaw.client.view.widgets.SliderBarMovie.LabelFormatter;
import btaw.shared.model.Study;

public class MovieView extends AView implements View {
	/* main components */
	private DialogBox main;
	private VerticalPanel containerPanel;
	
	/* modality toggle components */
	private HorizontalPanel modalityTogglePanel;
	private ToggleButton t1Button;
	private ToggleButton t1cButton;
	private ToggleButton t2Button;
	private ToggleButton flairButton;
	private ToggleButton focusedMode;
	
	/* modality view components */
	private HorizontalPanel modalityViewPanel;
	
	private VerticalPanel modalityViewControl;
	private Button sliceUp;
	private Button sliceDown;
	private TextBox sliceNo;
	private CheckBox crCheckBox;
	private CheckBox enforceRegisteredOnly;
	private CheckBox drCheckBox;
	private CheckBox histCheckBox;
	private CheckBox applyWinLvl;
	
	private AbsolutePanel modalityAbsT1;
	private VerticalPanel modalityViewT1;
	private Image t1Image;
	private ArrayList<Image> t1Images;
	
	private AbsolutePanel modalityAbsT1c;
	private VerticalPanel modalityViewT1c;
	private Image t1cImage;
	private ArrayList<Image> t1cImages;
	
	private AbsolutePanel modalityAbsT2;
	private VerticalPanel modalityViewT2;
	private Image t2Image;
	private ArrayList<Image> t2Images;
	
	private AbsolutePanel modalityAbsFlair;
	private VerticalPanel modalityViewFlair;
	private Image flairImage;
	private ArrayList<Image> flairImages;
	
	/* comment components */
	private HorizontalPanel commentViewPanel;
	
	private VerticalPanel globalCommentPanel;
	private VerticalPanel otherCommentPanel;
	private TextArea globalComments;
	private TextArea otherComments;
	
	/* slidebar components */
	private VerticalPanel sliderBarPanel;
	private FocusPanel datePanel;
	private SliderBarMovie studyDateSlider;
	
	/* control panel components */
	private HorizontalPanel controlPanel;
	private Button options;
	private Button previous;
	private Button play;
	private Button next;
	private Button stop;
	private Button close;
	private TextBox sliderSpeed;
	
	private List<Study> studyList;
	private int selectedIndex = 0;
	private double stepSize = 0;
	private double min = 0;
	private double max = 0;
	private int currStudyIndex = 0;
	private boolean cr = false;
	private boolean er = false;
	private boolean dr = false;
	
	/* window level components */
	private SliderBar t1LwrSlider;
	private SliderBar t1UprSlider;
	private SliderBar t1cLwrSlider;
	private SliderBar t1cUprSlider;
	private SliderBar t2LwrSlider;
	private SliderBar t2UprSlider;
	private SliderBar flairLwrSlider;
	private SliderBar flairUprSlider;
	
	
	private int pid = -1;
	private String modality;
	
	public MovieView() {
		main = new DialogBox();
		containerPanel = new VerticalPanel();
		main.add(containerPanel);
		main.getElement().getStyle().setZIndex(5);
		
		modalityTogglePanel = new HorizontalPanel();
		t1Button = new ToggleButton("T1");
		t1Button.setDown(false);
		t1cButton = new ToggleButton("T1C");
		t1cButton.setDown(false);
		t2Button = new ToggleButton("T2");
		t2Button.setDown(false);
		flairButton = new ToggleButton("FLAIR");
		flairButton.setDown(false);
		Label toggleL = new Label("Press to toggle modality: ");
		toggleL.setStyleName("center-Label");
		modalityTogglePanel.setSpacing(5);
		modalityTogglePanel.add(toggleL);
		modalityTogglePanel.add(t1Button);
		modalityTogglePanel.add(t1cButton);
		modalityTogglePanel.add(t2Button);
		modalityTogglePanel.add(flairButton);
		
		sliderSpeed = new TextBox();
		crCheckBox = new CheckBox("Chain-Registered");
		crCheckBox.setValue(true);
		drCheckBox = new CheckBox("Directly-Registered");
		drCheckBox.setValue(false);
		enforceRegisteredOnly = new CheckBox("Registered Images Only");
		enforceRegisteredOnly.setValue(true);
		histCheckBox = new CheckBox("Equalize Image Histogram");
		histCheckBox.setValue(false);
		applyWinLvl = new CheckBox("Apply Win/Level");
		applyWinLvl.setValue(false);
		this.cr = false;
		this.er = true;
		this.dr = true;
		sliderSpeed.setWidth("25px");
		Label speedL = new Label("One Second is ");
		speedL.setStyleName("center-Label");
		Label speedL2 = new Label(" Days");
		speedL2.setStyleName("center-Label");
		focusedMode = new ToggleButton("Focused Mode");
		modalityTogglePanel.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"));
		modalityTogglePanel.add(focusedMode);
		//modalityTogglePanel.add(crCheckBox);
		//modalityTogglePanel.add(drCheckBox);
		//modalityTogglePanel.add(enforceRegisteredOnly);
		modalityTogglePanel.add(histCheckBox);
		modalityTogglePanel.add(applyWinLvl);
		
		modalityViewPanel = new HorizontalPanel();

		modalityViewControl = new VerticalPanel();
		sliceUp = new Button("\u21E7");
		sliceDown = new Button("\u21E9");
		sliceNo = new TextBox();
		sliceNo.setValue("1");
		sliceNo.setWidth("25px");
		Label sliceLabel = new Label("Slice:");
		modalityViewControl.setHeight("100%");
		modalityViewControl.setSpacing(5);
		modalityViewControl.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		modalityViewControl.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		modalityViewControl.add(sliceUp);
		modalityViewControl.add(sliceLabel);
		modalityViewControl.add(sliceNo);
		modalityViewControl.add(sliceDown);
		
		t1Images = new ArrayList<Image>();
		t1cImages = new ArrayList<Image>();
		t2Images = new ArrayList<Image>();
		flairImages = new ArrayList<Image>();
		
		modalityAbsT1 = new AbsolutePanel();
		modalityViewT1 = new VerticalPanel();
		t1Image = new Image();
		t1Image.setSize("256px", "256px");
		modalityViewT1.setSize("256px", "256px");
		modalityViewT1.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		modalityViewT1.add(modalityAbsT1);
		modalityAbsT1.setSize("256px", "256px");
		modalityViewT1.add(new Label("T1"));
		t1LwrSlider = new SliderBar(0, 255);
		t1UprSlider = new SliderBar(0.0, 255.0, new btaw.client.view.widgets.SliderBar.LabelFormatter() {
			
			@Override
			public String formatLabel(SliderBar slider, double value) {
				return Long.toString(Math.round(value));
			}
		});
		this.setupSlider(t1LwrSlider, t1UprSlider);
		AbsolutePanel t1SliderBarAbs = new AbsolutePanel();
		t1SliderBarAbs.setWidth(modalityViewT1.getElement().getStyle().getWidth());
		t1SliderBarAbs.add(t1LwrSlider);
		t1SliderBarAbs.add(t1UprSlider);
		modalityViewT1.add(t1SliderBarAbs);
		modalityViewT1.setVisible(false);
		
		modalityAbsT1c = new AbsolutePanel();
		modalityViewT1c = new VerticalPanel();
		t1cImage = new Image();
		t1cImage.setSize("256px", "256px");
		modalityViewT1c.setSize("256px", "256px");
		modalityViewT1c.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		modalityViewT1c.add(modalityAbsT1c);
		modalityAbsT1c.setSize("256px", "256px");
		modalityViewT1c.add(new Label("T1C"));
		t1cLwrSlider = new SliderBar(0, 255);
		t1cUprSlider = new SliderBar(0.0, 255.0, new btaw.client.view.widgets.SliderBar.LabelFormatter() {
			
			@Override
			public String formatLabel(SliderBar slider, double value) {
				return Long.toString(Math.round(value));
			}
		});
		this.setupSlider(t1cLwrSlider, t1cUprSlider);
		AbsolutePanel t1cSliderBarAbs = new AbsolutePanel();
		t1cSliderBarAbs.setWidth(modalityViewT1c.getElement().getStyle().getWidth());
		t1cSliderBarAbs.add(t1cLwrSlider);
		t1cSliderBarAbs.add(t1cUprSlider);
		modalityViewT1c.add(t1cSliderBarAbs);
		modalityViewT1c.setVisible(false);
		
		modalityAbsT2 = new AbsolutePanel();
		modalityViewT2 = new VerticalPanel();
		t2Image = new Image();
		t2Image.setSize("256px", "256px");
		modalityViewT2.setSize("256px", "256px");
		modalityViewT2.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		modalityViewT2.add(modalityAbsT2);
		modalityAbsT2.setSize("256px", "256px");
		modalityViewT2.add(new Label("T2"));
		t2LwrSlider = new SliderBar(0, 255);
		t2UprSlider = new SliderBar(0.0, 255.0, new btaw.client.view.widgets.SliderBar.LabelFormatter() {
			
			@Override
			public String formatLabel(SliderBar slider, double value) {
				return Long.toString(Math.round(value));
			}
		});
		this.setupSlider(t2LwrSlider, t2UprSlider);
		AbsolutePanel t2SliderBarAbs = new AbsolutePanel();
		t2SliderBarAbs.setWidth(modalityViewT2.getElement().getStyle().getWidth());
		t2SliderBarAbs.add(t2LwrSlider);
		t2SliderBarAbs.add(t2UprSlider);
		modalityViewT2.add(t2SliderBarAbs);
		modalityViewT2.setVisible(false);
		
		modalityAbsFlair = new AbsolutePanel();
		modalityViewFlair = new VerticalPanel();
		flairImage = new Image();
		flairImage.setSize("256px", "256px");
		modalityViewFlair.setSize("256px", "256px");
		modalityViewFlair.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		modalityViewFlair.add(modalityAbsFlair);
		modalityAbsFlair.setSize("256px", "256px");
		modalityViewFlair.add(new Label("FLAIR"));
		flairLwrSlider = new SliderBar(0, 255);
		flairUprSlider = new SliderBar(0.0, 255.0, new btaw.client.view.widgets.SliderBar.LabelFormatter() {
			
			@Override
			public String formatLabel(SliderBar slider, double value) {
				return Long.toString(Math.round(value));
			}
		});
		this.setupSlider(flairLwrSlider, flairUprSlider);
		AbsolutePanel flairSliderBarAbs = new AbsolutePanel();
		flairSliderBarAbs.setWidth(modalityViewFlair.getElement().getStyle().getWidth());
		flairSliderBarAbs.add(flairLwrSlider);
		flairSliderBarAbs.add(flairUprSlider);
		modalityViewFlair.add(flairSliderBarAbs);
		modalityViewFlair.setVisible(false);
		
		modalityViewPanel.add(modalityViewControl);
		modalityViewPanel.add(modalityViewT1);
		modalityViewPanel.add(modalityViewT1c);
		modalityViewPanel.add(modalityViewT2);
		modalityViewPanel.add(modalityViewFlair);
		
		commentViewPanel = new HorizontalPanel();
		
		globalCommentPanel = new VerticalPanel();
		globalComments = new TextArea();
		globalComments.setWidth("100%");
		globalComments.setHeight("112px");
		DOM.setStyleAttribute(globalComments.getElement(), "resize", "none");
		globalComments.setReadOnly(true);
		globalComments.getElement().getStyle().setColor("#000000");
		globalCommentPanel.setWidth("425px");
		globalCommentPanel.setHeight("128px");
		globalCommentPanel.add(new Label("Global Comments (Double-Click Text to Edit) :"));
		globalCommentPanel.add(globalComments);
		
		otherCommentPanel = new VerticalPanel();
		otherCommentPanel.setWidth("425px");
		otherCommentPanel.setHeight("128px");
		otherComments = new TextArea();
		otherComments.setWidth("100%");
		otherComments.setHeight("112px");
		otherComments.getElement().getStyle().setColor("#000000");
		DOM.setStyleAttribute(otherComments.getElement(), "resize", "none");
		otherComments.setReadOnly(true);
		otherCommentPanel.add(new Label("Other Comments (Double-Click Text to Edit) : "));
		otherCommentPanel.add(otherComments);
		
		/* LEGEND WITH COLORS FOR DATEPANEL */
		VerticalPanel legendPanel = new VerticalPanel();
		Label legendL = new Label("Legend: ");
		Label legendStudiesL = new Label("\u25BC: Studies");
		Label legendChemoL = new Label("\u25BC: Chemo Start/End");
		DOM.setStyleAttribute(legendChemoL.getElement(), "color", "#FF0000");
		Label legendRTL = new Label("\u25BC: Radiation Treatment Start/End");
		DOM.setStyleAttribute(legendRTL.getElement(), "color", "#0000FF");
		Label legendSecondRTL = new Label("\u25CF: Second Radiation Treatment Start/End");
		DOM.setStyleAttribute(legendSecondRTL.getElement(), "color", "#BB00FF");
		Label legendBiopsyL = new Label("\u25C6: Date Biopsy");
		DOM.setStyleAttribute(legendBiopsyL.getElement(), "color", "#000000");
		Label legendDiagL = new Label("\u25C6: Diagnosis Prelim/Final");
		DOM.setStyleAttribute(legendDiagL.getElement(), "color", "#FF0000");
		Label legendInitialScanL = new Label("\u25C6: Initial Scan");
		DOM.setStyleAttribute(legendInitialScanL.getElement(), "color", "#0000FF");
		//legendPanel.setSpacing(5);
		legendPanel.add(legendL);
		legendPanel.add(legendStudiesL);
		legendPanel.add(legendChemoL);
		legendPanel.add(legendRTL);
		legendPanel.add(legendSecondRTL);
		legendPanel.add(legendBiopsyL);
		legendPanel.add(legendDiagL);
		legendPanel.add(legendInitialScanL);
		
		HorizontalPanel innerLegendAlignmentPanel = new HorizontalPanel();
		innerLegendAlignmentPanel.add(legendPanel);

		commentViewPanel.setWidth("100%");
		commentViewPanel.add(innerLegendAlignmentPanel);
		commentViewPanel.add(globalCommentPanel);
		commentViewPanel.add(otherCommentPanel);
		
		sliderBarPanel = new VerticalPanel();
		sliderBarPanel.setWidth("100%");
		Date max = new Date();
		/* so default label goes from EPOCH -> NOW, label accepts days */
		studyDateSlider = new SliderBarMovie(0, msToDays(max.getTime()), new LabelFormatter() {
			public String formatLabel(SliderBarMovie slider, double value) {
				Date date = new Date(daysToMs(value));
				return DateTimeFormat.getFormat("yyyy/MM/dd").format(date);
			}
		});
		studyDateSlider.setWidth("100%");
		/* 1 month = 30 days */
		studyDateSlider.setStepSize(30);
		studyDateSlider.setCurrentValue(msToDays(max.getTime()));
		studyDateSlider.setNumTicks(200);
		studyDateSlider.setNumLabels(10);
		datePanel = new FocusPanel();
		datePanel.setStyleName("gwt-StudyDate-shell");
		datePanel.setWidth("100%");
		DOM.setStyleAttribute(datePanel.getElement(), "position", "relative");
		
		sliderBarPanel.add(datePanel);
		sliderBarPanel.add(studyDateSlider);
		
		controlPanel = new HorizontalPanel();
		options = new Button("Options");
		previous = new Button("Previous");
		play = new Button("Play");
		next = new Button("Next");
		stop = new Button("Stop");
		close = new Button("Close");
		HorizontalPanel controlLeftAlignPanel = new HorizontalPanel();
		HorizontalPanel controlCenterAlignPanel = new HorizontalPanel();
		HorizontalPanel controlRightAlignPanel = new HorizontalPanel();
		controlLeftAlignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		controlLeftAlignPanel.setSpacing(5);
		controlLeftAlignPanel.add(options);
		controlCenterAlignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		controlCenterAlignPanel.setSpacing(5);
		controlCenterAlignPanel.add(previous);
		controlCenterAlignPanel.add(next);
		controlCenterAlignPanel.add(play);
		controlCenterAlignPanel.add(stop);
		controlCenterAlignPanel.add(speedL);
		controlCenterAlignPanel.add(sliderSpeed);
		controlCenterAlignPanel.add(speedL2);
		controlRightAlignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		controlRightAlignPanel.setSpacing(5);
		controlRightAlignPanel.add(close);
		controlPanel.setWidth("100%");
		controlPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		controlPanel.add(controlLeftAlignPanel);
		controlPanel.add(controlCenterAlignPanel);
		controlPanel.add(controlRightAlignPanel);
		
		containerPanel.add(modalityTogglePanel);
		containerPanel.add(modalityViewPanel);
		containerPanel.add(commentViewPanel);
		containerPanel.add(sliderBarPanel);
		containerPanel.add(controlPanel);
	}
	@Override
	public void populateStudiesAndShow(List<Study> studies, String pid, String modality, int slice) {
		this.min = msToDays(studies.get(0).getDate().getTime());
		studyDateSlider.setMinValue(this.min);
		this.max = msToDays(studies.get(studies.size() - 1).getDate().getTime());
		/* add 5% to the end of max */
		this.max = this.max + Math.round((this.max - this.min)*0.05);
		studyDateSlider.setMaxValue(this.max);
		studyDateSlider.setCurrentValue(this.min, false);
		/* basically subtract the ranges then divide by 100 so that it matches the 100 ticks */
		this.stepSize = (this.max - this.min)/200;
		studyDateSlider.setStepSize(stepSize);
		
		this.sliderSpeed.setText("" + this.stepSize*25);
		
		this.studyList = studies;

		this.pid = Integer.parseInt(pid);
		main.setText("Patient " + this.pid + " Studies");
		main.setModal(true);
		main.center();
		containerPanel.setWidth("1086px");
		
		this.modality = modality;
		System.err.println(modality);
		

		if(modality.equals("T1")) {
			this.t1Button.setValue(true, false);
			this.modalityViewT1.setVisible(true);
		} else if(modality.equals("T1C")) {
			this.t1cButton.setValue(true, false);
			this.modalityViewT1c.setVisible(true);
		} else if(modality.equals("T2")) {
			this.t2Button.setValue(true, false);
			this.modalityViewT2.setVisible(true);
		} else if(modality.equals("FLAIR")) {
			this.flairButton.setValue(true, false);
			this.modalityViewFlair.setVisible(true);
		}
		
		this.sliceNo.setText(""+(slice));
		this.selectedIndex = slice;
		this.focusedMode.setDown(true);
		this.reSetupImages();
		
		MainPresenter.get().finished();
	}
	public void setupSlider(SliderBar lowerSlider, SliderBar upperSlider) {
		lowerSlider.setWidth("100%");
		lowerSlider.setStepSize(1);
		lowerSlider.setNumTicks(0);
		lowerSlider.setNumLabels(0);
		lowerSlider.setPositionStyle("absolute");
		upperSlider.setWidth("100%");
		upperSlider.setStepSize(1);
		upperSlider.setNumTicks(20);
		upperSlider.setNumLabels(5);
		upperSlider.getElement().getStyle().setZIndex(1);
		lowerSlider.setSliderZIndex(2);
		upperSlider.setSliderZIndex(2);
		
		lowerSlider.setCurrentValue(0);
		upperSlider.setCurrentValue(255);
	}
	public void reSetupImages() {
		this.setupImages(selectedIndex);
	}
	private void setupImages(int index) {
		removeCurrentImages();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy/MM/dd");
		for(int i = 0; i < studyList.size(); i++) {
			List<String> t1Ids = null;
			if(studyList.get(i).getType("T1") != null) {
				t1Ids = studyList.get(i).getType("T1").getIds();
			}
			List<String> t1cIds = null;
			if(studyList.get(i).getType("T1C") != null) {
				t1cIds = studyList.get(i).getType("T1C").getIds();
			}
			List<String> t2Ids = null;
			if(studyList.get(i).getType("T2") != null) {
				t2Ids = studyList.get(i).getType("T2").getIds();
			}
			List<String> flairIds = null;
			if(studyList.get(i).getType("FLAIR") != null) {
				flairIds = studyList.get(i).getType("FLAIR").getIds();
			}
			if (t1Ids != null && index < t1Ids.size() && t1Button.isDown()) {
				String t1Id = t1Ids.get(index);
				t1Images.add(new Image(
						"https://ellis-rv.cs.ualberta.ca/btaw/btaw_gui/images?id="+t1Id
						+"&pid="+this.pid
						+"&study_date="+dtf.format(studyList.get(i).getDate())
						+"&modality=T1"
						+"&slicenr="+(index+1)
						+"&cr=false"
						+"&dr=true"
						+"&er=true"
						+"&hist_eq="+this.histCheckBox.getValue()
						+"&apply_wl="+applyWinLvl.getValue()
						+"&lwr="+( t1LwrSlider.getCurrentValue()/255.0 )
						+"&upr="+( t1UprSlider.getCurrentValue()/255.0 )
						+"&time="+this.getUnixTimeStamp()));
			} else {
				t1Images.add(new Image(
						"https://ellis-rv.cs.ualberta.ca/btaw/images/Transparent.png"));
			}

			if (t1cIds != null && index < t1cIds.size() && t1cButton.isDown()) {
				String t1cId = t1cIds.get(index);
				t1cImages.add(new Image(
						"https://ellis-rv.cs.ualberta.ca/btaw/btaw_gui/images?id="+t1cId
								+"&pid="+this.pid
								+"&study_date="+dtf.format(studyList.get(i).getDate())
								+"&modality=T1C"
								+"&slicenr="+(index+1)
								+"&cr=false"
								+"&dr=true"
								+"&er=true"
								+"&hist_eq="+this.histCheckBox.getValue()
								+"&apply_wl="+applyWinLvl.getValue()
								+"&lwr="+( t1cLwrSlider.getCurrentValue()/255.0 )
								+"&upr="+( t1cUprSlider.getCurrentValue()/255.0 )
								+"&time="+this.getUnixTimeStamp()));
			} else {
				t1cImages
						.add(new Image(
								"https://ellis-rv.cs.ualberta.ca/btaw/images/Transparent.png"));
			}

			if (t2Ids != null && index < t2Ids.size() && t2Button.isDown()) {
				String t2Id = t2Ids.get(index);
				t2Images.add(new Image(
						"https://ellis-rv.cs.ualberta.ca/btaw/btaw_gui/images?id="+t2Id
								+"&pid="+this.pid
								+"&study_date="+dtf.format(studyList.get(i).getDate())
								+"&modality=T2"
								+"&slicenr="+(index+1)
								+"&cr=false"
								+"&dr=true"
								+"&er=true"
								+"&hist_eq="+this.histCheckBox.getValue()
								+"&apply_wl="+applyWinLvl.getValue()
								+"&lwr="+( t2LwrSlider.getCurrentValue()/255.0 )
								+"&upr="+( t2UprSlider.getCurrentValue()/255.0 )
								+"&time="+this.getUnixTimeStamp()));
			} else {
				t2Images.add(new Image(
						"https://ellis-rv.cs.ualberta.ca/btaw/images/Transparent.png"));
			}

			if (flairIds != null && index < flairIds.size() && flairButton.isDown()) {
				String flairId = flairIds.get(index);
				flairImages.add(new Image(
						"https://ellis-rv.cs.ualberta.ca/btaw/btaw_gui/images?id="+flairId
								+"&pid="+this.pid
								+"&study_date="+dtf.format(studyList.get(i).getDate())
								+"&modality=FLAIR"
								+"&slicenr="+(index+1)
								+"&cr=false"
								+"&dr=true"
								+"&er=true"
								+"&hist_eq="+this.histCheckBox.getValue()
								+"&apply_wl="+applyWinLvl.getValue()
								+"&lwr="+( flairLwrSlider.getCurrentValue()/255.0 )
								+"&upr="+( flairUprSlider.getCurrentValue()/255.0 )
								+"&time="+this.getUnixTimeStamp()));
			} else {
				flairImages
						.add(new Image(
								"https://ellis-rv.cs.ualberta.ca/btaw/images/Transparent.png"));
			}
		}
		/* add black background */
		t1Images.add(new Image("https://ellis-rv.cs.ualberta.ca/btaw/images/Solid_black.png"));
		t1cImages.add(new Image("https://ellis-rv.cs.ualberta.ca/btaw/images/Solid_black.png"));
		t2Images.add(new Image("https://ellis-rv.cs.ualberta.ca/btaw/images/Solid_black.png"));
		flairImages.add(new Image("https://ellis-rv.cs.ualberta.ca/btaw/images/Solid_black.png"));

		for(int i = 0; i < t1Images.size(); i++) {
			if(t1Button.isDown())
				modalityAbsT1.add(t1Images.get(i), 0, 0);
			if(t1cButton.isDown())
				modalityAbsT1c.add(t1cImages.get(i), 0, 0);
			if(t2Button.isDown())
				modalityAbsT2.add(t2Images.get(i), 0, 0);
			if(flairButton.isDown())
				modalityAbsFlair.add(flairImages.get(i), 0, 0);

			t1Images.get(i).getElement().getStyle().setZIndex(t1Images.size() - i);
			t1cImages.get(i).getElement().getStyle().setZIndex(t1Images.size() - i);
			t2Images.get(i).getElement().getStyle().setZIndex(t1Images.size() - i);
			flairImages.get(i).getElement().getStyle().setZIndex(t1Images.size() - i);

			t1Images.get(i).setPixelSize(256, 256);
			t1cImages.get(i).setPixelSize(256, 256);
			t2Images.get(i).setPixelSize(256, 256);
			flairImages.get(i).setPixelSize(256, 256);
		}
		if(this.focusedMode.isDown()) {
			this.startFocusedMode(false);
		}
	}
	
	public void removeCurrentImages() {
		modalityAbsT1.clear();
		modalityAbsT1c.clear();
		modalityAbsT2.clear();
		modalityAbsFlair.clear();
		
		t1Images.clear();
		t1cImages.clear();
		t2Images.clear();
		flairImages.clear();
	}
	
	private Study getCurrentSliderDate() {
		Date currSliderDate = new Date(daysToMs(studyDateSlider.getCurrentValue()));
		for(int i = studyList.size() - 1; i >= 0; i--) {
			if(currSliderDate.after(studyList.get(i).getDate())) {
				currStudyIndex = i;
				return studyList.get(i);
			}
		}
		currStudyIndex = 0;
		return studyList.get(0);
	}
	
	private double getProgressToNextStudy() {
		Date currSliderDate = new Date(daysToMs(studyDateSlider.getCurrentValue()));
		if(currSliderDate.after(studyList.get(studyList.size() - 1).getDate())) {
			return 1;
		}
		for(int i = studyList.size() - 2; i >= 0; i--) {
			if(currSliderDate.after(studyList.get(i).getDate())) {
				currStudyIndex = i;
				long totalDiff = studyList.get(i).getDate().getTime() - studyList.get(i + 1).getDate().getTime();
				long currDiff = studyList.get(i).getDate().getTime() - currSliderDate.getTime();
				return 1.0 - ((double)currDiff)/totalDiff;
			}
		}
		return 1;
	}
	
	private void setOpacity() {
		Date currSliderDate = new Date(daysToMs(studyDateSlider.getCurrentValue()));
		int max = 0;
		for(int i = 0; i < studyList.size(); i++) {
			max = i;
			if(currSliderDate.after(studyList.get(i).getDate())) {
				t1Images.get(i).getElement().getStyle().setOpacity(0);
				t1cImages.get(i).getElement().getStyle().setOpacity(0);
				t2Images.get(i).getElement().getStyle().setOpacity(0);
				flairImages.get(i).getElement().getStyle().setOpacity(0);
			} else {
				break;
			}
		}
		if(max == 0) {
			max = 1;
		}
		double opacity = getProgressToNextStudy();

		//edge case of last element
		if(currSliderDate.after(studyList.get(studyList.size() - 1).getDate())) {
			t1Images.get(max).getElement().getStyle().setOpacity(opacity);
			t1cImages.get(max).getElement().getStyle().setOpacity(opacity);
			t2Images.get(max).getElement().getStyle().setOpacity(opacity);
			flairImages.get(max).getElement().getStyle().setOpacity(opacity);
			return;
		}
		
		//all other cases
		if(!t1Images.get(max).getUrl().endsWith("/mod_unavail.png"))
			t1Images.get(max - 1).getElement().getStyle().setOpacity(opacity);
		if(!t1cImages.get(max).getUrl().endsWith("/mod_unavail.png"))
			t1cImages.get(max - 1).getElement().getStyle().setOpacity(opacity);
		if(!t2Images.get(max).getUrl().endsWith("/mod_unavail.png"))
			t2Images.get(max - 1).getElement().getStyle().setOpacity(opacity);
		if(!flairImages.get(max).getUrl().endsWith("/mod_unavail.png"))
			flairImages.get(max - 1).getElement().getStyle().setOpacity(opacity);
		
		//sets all future elements to opacity 1 just in case you've been moving the slider around
		for(int i = max; i < studyList.size(); i++) {
			t1Images.get(i).getElement().getStyle().setOpacity(1);
			t1cImages.get(i).getElement().getStyle().setOpacity(1);
			t2Images.get(i).getElement().getStyle().setOpacity(1);
			flairImages.get(i).getElement().getStyle().setOpacity(1);
		}
	}
	
	private int getMaxSliceNo() {
		Study s = getCurrentSliderDate();
		int t1 = 0, t1c = 0, t2 = 0, flair = 0;
		if(s.getType("T1") != null) {
			t1 = s.getType("T1").getIds().size();
		}
		if(s.getType("T1C") != null) {
			t1c = s.getType("T1C").getIds().size();
		}
		if(s.getType("T2") != null) {
			t2 = s.getType("T2").getIds().size();
		}
		if(s.getType("FLAIR") != null) {
			flair = s.getType("FLAIR").getIds().size();
		}
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		list.add(t1);
		list.add(t1c);
		list.add(t2);
		list.add(flair);
		
		return Collections.max(list);
	}
	
	private int msToDays(long miliseconds) {
		final long msInDay = 86400000;
		return (int) (miliseconds/msInDay);
	}
	
	private long daysToMs(double days) {
		final long msInDay = 86400000;
		return Math.round(days) * msInDay;
	}
	public void updateImage() {
		if(studyList == null)
			return;
		setOpacity();
	}
	
	public void incrId(){
		if(selectedIndex >= 0 /*&& 
				selectedIndex < getMaxSliceNo()*/) {
			setupImages(++selectedIndex);
			sliceNo.setText(Integer.toString(selectedIndex + 1));
		}
	}
	public void decrId(){
		if(selectedIndex > 0 /*&&
				selectedIndex <= getMaxSliceNo()*/) {
			setupImages(--selectedIndex);
			sliceNo.setText(Integer.toString(selectedIndex + 1));
		}
	}
	
	/* drawing date arrows on datepanel */
	public void drawStudyDates() {
		Element dateIndicator = null;
		int panelHeight = DOM.getElementPropertyInt(datePanel.getElement(), "offsetHeight");
		int lineWidth = studyDateSlider.getLineWidth();
		for(Study s : studyList) {
			if(this.max < msToDays(s.getDate().getTime())) {
				this.setupMax(s.getDate());
			} else if (this.min > msToDays(s.getDate().getTime())) {
				this.setupMin(s.getDate());
			}
			dateIndicator = DOM.createDiv();
			DOM.setElementProperty(dateIndicator, "title", DateTimeFormat.getFormat("yyyy/MM/dd").format(s.getDate()));
			DOM.setStyleAttribute(dateIndicator, "position", "absolute");
			DOM.setStyleAttribute(dateIndicator, "display", "");
			DOM.setElementProperty(dateIndicator, "className", "gwt-StudyDate-label");
			DOM.setElementProperty(dateIndicator, "innerHTML", "\u25BC");
			DOM.setStyleAttribute(dateIndicator, "top", 50 + "px");
			DOM.setStyleAttribute(dateIndicator, "left", 26 + ((msToDays(s.getDate().getTime()) - this.min)/(this.max - this.min))*lineWidth + "px"  );
			DOM.setStyleAttribute(dateIndicator, "visibility", "visible");
			DOM.appendChild(datePanel.getElement(), dateIndicator);
		}
	}
	public void drawDates(List<Date> dates, int topOffset, String color, String unicodeChar, boolean increaseFontSize) {
		if(dates == null) {
			return;
		}
		Element dateIndicator = null;
		int lineWidth = studyDateSlider.getLineWidth();
		for(Date d : dates) {
			if(this.max < msToDays(d.getTime())) {
				this.setupMax(d);
			} else if (this.min > msToDays(d.getTime())) {
				this.setupMin(d);
			}
			dateIndicator = DOM.createDiv();
			DOM.setElementProperty(dateIndicator, "title", DateTimeFormat.getFormat("yyyy/MM/dd").format(d));
			DOM.setStyleAttribute(dateIndicator, "position", "absolute");
			DOM.setStyleAttribute(dateIndicator, "display", "");
			DOM.setElementProperty(dateIndicator, "className", "gwt-StudyDate-label");
			DOM.setElementProperty(dateIndicator, "innerHTML", unicodeChar);
			DOM.setStyleAttribute(dateIndicator, "top", topOffset + "px");
			DOM.setStyleAttribute(dateIndicator, "left", 26 + ((msToDays(d.getTime()) - this.min)/(this.max - this.min))*lineWidth + "px"  );
			DOM.setStyleAttribute(dateIndicator, "visibility", "visible");
			DOM.setStyleAttribute(dateIndicator, "color", color);
			if(increaseFontSize) {
				DOM.setStyleAttribute(dateIndicator, "fontSize", "14pt");
			}
			DOM.appendChild(datePanel.getElement(), dateIndicator);
		}
	}
	private void setupMin(Date min) {
		this.min = msToDays(min.getTime());
		studyDateSlider.setMinValue(this.min);
		studyDateSlider.setCurrentValue(this.min, false);
		this.stepSize = (this.max - this.min)/200;
		studyDateSlider.setStepSize(stepSize);
	}
	private void setupMax(Date max) {
		this.max = msToDays(max.getTime());
		/* add 5% to the end of max */
		this.max = this.max + Math.round((this.max - this.min)*0.05);
		studyDateSlider.setMaxValue(this.max);
		studyDateSlider.setCurrentValue(this.min, false);
		this.stepSize = (this.max - this.min)/200;
		studyDateSlider.setStepSize(stepSize);
		
		this.sliderSpeed.setText("" + this.stepSize*25);
		System.err.println("SETUP MAX!");
	}
	public void showT1(boolean hide) {
		modalityViewT1.setVisible(hide);
	}
	public void showT1c(boolean hide) {
		modalityViewT1c.setVisible(hide);
	}
	public void showT2(boolean hide) {
		modalityViewT2.setVisible(hide);
	}
	public void showFlair(boolean hide) {
		modalityViewFlair.setVisible(hide);
	}
	public Button getCloseButton() {
		return close;
	}
	public void close() {
		main.hide();
	}
	public void resetSlice() {
		selectedIndex = 0;
		sliceNo.setText("1");
	}
	public ToggleButton getT1Button() {
		return t1Button;
	}
	public ToggleButton getT1cButton() {
		return t1cButton;
	}
	public ToggleButton getT2Button() {
		return t2Button;
	}
	public ToggleButton getFlairButton() {
		return flairButton;
	}
	public Button getSliceUp() {
		return sliceUp;
	}
	public Button getSliceDown() {
		return sliceDown;
	}
	public TextBox getSliceNo() {
		return sliceNo;
	}
	public TextArea getGlobalComments() {
		return globalComments;
	}
	public TextArea getOtherComments() {
		return otherComments;
	}
	public SliderBarMovie getStudyDateSlider() {
		return studyDateSlider;
	}
	public Button getOptions() {
		return options;
	}
	public Button getPrevious() {
		return previous;
	}
	public Button getPlay() {
		return play;
	}
	public Button getNext() {
		return next;
	}
	public Button getStop() {
		return stop;
	}
	public double getStepSize() {
		return this.stepSize;
	}
	public double getMinSize() {
		return this.min;
	}
	public double getMaxSize() {
		return this.max;
	}
	public void setGlobalComment(String s) {
		if(s == null) {
			this.globalComments.setText("No comments found in database.");
			return;
		}
		this.globalComments.setText(s);
	}
	public void setUserComments(HashMap<String, HashMap<String, String>> hm) {
		String otherComments = "";
		for(Map.Entry<String, HashMap<String, String>> entry : hm.entrySet()) {
			otherComments += "User " + entry.getKey() + " Comments:\n";
			for(Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
				otherComments += " Category \'" + innerEntry.getKey() + "\':\n";
				otherComments += "  "+innerEntry.getValue().replaceAll("\\r\\n|\\r|\\n", " ") + "\n\n";
			}
		}
		if(otherComments.length() == 0) {
			otherComments += "No comments found.";
		}
		this.otherComments.setText(otherComments);
	}
	public void moveNextStudy() {
		if(currStudyIndex == studyList.size() - 1) {
			studyDateSlider.setCurrentValue(msToDays(studyList.get(studyList.size() - 1).getDate().getTime()), true);
			return;
		}
		studyDateSlider.setCurrentValue(msToDays(studyList.get(++currStudyIndex).getDate().getTime()) + Math.round((this.max - this.min)*0.01), true);
	}
	public void movePreviousStudy() {
		if(currStudyIndex == 0) {
			studyDateSlider.setCurrentValue(msToDays(studyList.get(0).getDate().getTime()), true);
			return;
		}
		studyDateSlider.setCurrentValue(msToDays(studyList.get(--currStudyIndex).getDate().getTime()), true);
	}
	public void moveResetStudy() {
		currStudyIndex = 0;
		studyDateSlider.setCurrentValue(msToDays(studyList.get(0).getDate().getTime()), true);
	}
	public TextBox getPlaySpeedTB() {
		return this.sliderSpeed;
	}
	public ToggleButton getFocusedModeButton() {
		return this.focusedMode;
	}
	public CheckBox getCRCheckBox() {
		return this.crCheckBox;
	}
	public CheckBox getDRCheckBox() {
		return this.drCheckBox;
	}
	public CheckBox getERCheckBox() {
		return this.enforceRegisteredOnly;
	}
	public void setCR(boolean val) {
		this.cr = val;
		this.setupImages(selectedIndex);
	}
	public void setER(boolean val) {
		this.er = val;
		this.setupImages(selectedIndex);
	}
	public void setDR(boolean val) {
		this.dr = val;
		this.setupImages(selectedIndex);
	}
	public void startFocusedMode(boolean fresh) {
		if(!containerPanel.remove(commentViewPanel)) {
			System.err.println("MovieView could not detach commentPanel from containerPanel");
		}
		if (fresh) {
			if (modality.equals("T1")) {
				this.t1Button.setValue(true, false);
				this.modalityViewT1.setVisible(true);
				this.t1cButton.setValue(false, false);
				this.modalityViewT1c.setVisible(false);
				this.t2Button.setValue(false, false);
				this.modalityViewT2.setVisible(false);
				this.flairButton.setValue(false, false);
				this.modalityViewFlair.setVisible(false);
			} else if (modality.equals("T1C")) {
				this.t1Button.setValue(false, false);
				this.modalityViewT1.setVisible(false);
				this.t1cButton.setValue(true, false);
				this.modalityViewT1c.setVisible(true);
				this.t2Button.setValue(false, false);
				this.modalityViewT2.setVisible(false);
				this.flairButton.setValue(false, false);
				this.modalityViewFlair.setVisible(false);
			} else if (modality.equals("T2")) {
				this.t1Button.setValue(false, false);
				this.modalityViewT1.setVisible(false);
				this.t1cButton.setValue(false, false);
				this.modalityViewT1c.setVisible(false);
				this.t2Button.setValue(true, false);
				this.modalityViewT2.setVisible(true);
				this.flairButton.setValue(false, false);
				this.modalityViewFlair.setVisible(false);
			} else if (modality.equals("FLAIR")) {
				this.t1Button.setValue(false, false);
				this.modalityViewT1.setVisible(false);
				this.t1cButton.setValue(false, false);
				this.modalityViewT1c.setVisible(false);
				this.t2Button.setValue(false, false);
				this.modalityViewT2.setVisible(false);
				this.flairButton.setValue(true, false);
				this.modalityViewFlair.setVisible(true);
			}
		}
		int height = Window.getClientHeight() - 256;
		modalityViewT1.setPixelSize(height,  height);
		modalityAbsT1.setPixelSize(height,  height);
		for(Image t1Image : t1Images)
			t1Image.setPixelSize(height,  height);
		modalityViewT2.setPixelSize(height,  height);
		modalityAbsT2.setPixelSize(height,  height);
		for(Image t2Image : t2Images)
			t2Image.setPixelSize(height,  height);
		modalityViewT1c.setPixelSize(height,  height);
		modalityAbsT1c.setPixelSize(height,  height);
		for(Image t1cImage : t1cImages)
			t1cImage.setPixelSize(height,  height);
		modalityViewFlair.setPixelSize(height,  height);
		modalityAbsFlair.setPixelSize(height,  height);
		for(Image flairImage : flairImages)
			flairImage.setPixelSize(height,  height);
	}
	public void exitFocusedMode() {
		containerPanel.remove(sliderBarPanel);
		containerPanel.remove(controlPanel);
		containerPanel.add(commentViewPanel);
		containerPanel.add(sliderBarPanel);
		containerPanel.add(controlPanel);
		int height = 256;
		modalityViewT1.setPixelSize(height,  height);
		modalityAbsT1.setPixelSize(height,  height);
		for(Image t1Image : t1Images)
			t1Image.setPixelSize(height,  height);
		modalityViewT2.setPixelSize(height,  height);
		modalityAbsT2.setPixelSize(height,  height);
		for(Image t2Image : t2Images)
			t2Image.setPixelSize(height,  height);
		modalityViewT1c.setPixelSize(height,  height);
		modalityAbsT1c.setPixelSize(height,  height);
		for(Image t1cImage : t1cImages)
			t1cImage.setPixelSize(height,  height);
		modalityViewFlair.setPixelSize(height,  height);
		modalityAbsFlair.setPixelSize(height,  height);
		for(Image flairImage : flairImages)
			flairImage.setPixelSize(height,  height);
	}
	private int getUnixTimeStamp() {
        Date date = new Date();
        int iTimeStamp = (int) (date.getTime() * .001);
        return iTimeStamp;
	}
	public CheckBox getHistogramCB() {
		return this.histCheckBox;
	}
	public SliderBar getT1UpperSlider() {
		return this.t1UprSlider;
	}
	public SliderBar getT1LowerSlider() {
		return this.t1LwrSlider;
	}
	public SliderBar getT1cUpperSlider() {
		return this.t1cUprSlider;
	}
	public SliderBar getT1cLowerSlider() {
		return this.t1cLwrSlider;
	}
	public SliderBar getT2UpperSlider() {
		return this.t2UprSlider;
	}
	public SliderBar getT2LowerSlider() {
		return this.t2LwrSlider;
	}
	public SliderBar getFlairUpperSlider() {
		return this.flairUprSlider;
	}
	public SliderBar getFlairLowerSlider() {
		return this.flairLwrSlider;
	}
	public CheckBox getHistEqCheckBox() {
		return this.histCheckBox;
	}
	public CheckBox getApplyWinLvlCheckBox() {
		return this.applyWinLvl;
	}
}
