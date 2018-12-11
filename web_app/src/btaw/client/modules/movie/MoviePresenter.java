package btaw.client.modules.movie;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.view.widgets.SliderBar;
import btaw.client.view.widgets.SliderBarMovie;
import btaw.shared.model.Study;

public class MoviePresenter extends Presenter {

	public interface View extends IView {
		void populateStudiesAndShow(List<Study> studies, String pid, String modality, int slice);
		Button getCloseButton();
		ToggleButton getT1Button();
		ToggleButton getT1cButton();
		ToggleButton getT2Button();
		ToggleButton getFlairButton();
		Button getSliceUp();
		Button getSliceDown();
		TextBox getSliceNo();
		TextArea getGlobalComments();
		TextArea getOtherComments();
		SliderBarMovie getStudyDateSlider();
		Button getOptions();
		Button getPrevious();
		Button getPlay();
		Button getNext();
		Button getStop();
		void close();
		void updateImage();
		void decrId();
		void incrId();
		void resetSlice();
		void showT1(boolean hide);
		void showT1c(boolean hide);
		void showT2(boolean hide);
		void showFlair(boolean hide);
		double getStepSize();
		double getMinSize();
		double getMaxSize();
		void drawDates(List<Date> chemoDatesint, int topOffset, String color, String unicodeChar, boolean increaseFontSize);
		void drawStudyDates();
		void setGlobalComment(String s);
		void setUserComments(HashMap<String, HashMap<String, String>> hm);
		void movePreviousStudy();
		void moveNextStudy();
		void moveResetStudy();
		TextBox getPlaySpeedTB();
		ToggleButton getFocusedModeButton();
		void startFocusedMode(boolean fresh);
		void exitFocusedMode();
		CheckBox getCRCheckBox();
		CheckBox getERCheckBox();
		CheckBox getDRCheckBox();
		void setCR(boolean val);
		void setER(boolean val);
		void setDR(boolean val);
		void reSetupImages();
		CheckBox getHistogramCB();
		SliderBar getT1UpperSlider();
		SliderBar getT1LowerSlider();
		SliderBar getT1cUpperSlider();
		SliderBar getT1cLowerSlider();
		SliderBar getT2UpperSlider();
		SliderBar getT2LowerSlider();
		SliderBar getFlairUpperSlider();
		SliderBar getFlairLowerSlider();
		CheckBox getHistEqCheckBox();
		CheckBox getApplyWinLvlCheckBox();
	}
	
	private String pid;
	private final View ui;
	private int currStep = 0;
	private boolean stop = false;
	private List<String> usernames = null;
	private List<String> categories = null;
	private String modality;
	private int slice = 0;
	private int time = 0;
	
	public MoviePresenter(Presenter parent, View ui, String pid, String modality, int slice) {
		super(parent);
		
		this.pid = pid;
		this.ui = ui;
		this.modality = modality;
		this.slice = slice;
		Date date = new Date();
		this.time = (int) (date.getTime() * 0.001);
	}

	@Override
	protected void bindAll() 
	{
		/* MUST DO THIS BEFORE BINDINGS !!! */
		this.rpcService.getStudies(pid, new PresenterCallback<List<Study>>(this) {

			@Override
			protected void success(List<Study> result) {
				ui.populateStudiesAndShow(result, pid, modality, slice);
				setupQueryables();
			}
		});
		
		this.rpcService.getAllUserComments(Integer.parseInt(this.pid), new PresenterCallback<HashMap<String, HashMap<String, String>>>(this) {

			@Override
			protected void success(HashMap<String, HashMap<String, String>> result) {
				ui.setUserComments(result);
			}
		});
		
		ui.getSliceUp().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.incrId();
			}
		});
		
		ui.getSliceDown().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.decrId();
			}
		});
		
		ui.getCloseButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.close();
			}
		});
		
		ui.getStudyDateSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				ui.updateImage();
			}
		});

		ui.getT1Button().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(ui.getFocusedModeButton().isDown() && event.getValue()) {
					ui.showT1c(false);
					ui.getT1cButton().setValue(false, false);
					ui.showT2(false);
					ui.getT2Button().setValue(false, false);
					ui.showFlair(false);
					ui.getFlairButton().setValue(false, false);
				}
				ui.showT1(event.getValue());
				ui.reSetupImages();
			}
		});
		
		ui.getT1cButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(ui.getFocusedModeButton().isDown() && event.getValue()) {
					ui.showFlair(false);
					ui.getFlairButton().setValue(false, false);
					ui.showT2(false);
					ui.getT2Button().setValue(false, false);
					ui.showT1(false);
					ui.getT1Button().setValue(false, false);
				}
				ui.showT1c(event.getValue());
				ui.reSetupImages();
			}
		});
		
		ui.getT2Button().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(ui.getFocusedModeButton().isDown() && event.getValue()) {
					ui.showT1c(false);
					ui.getT1cButton().setValue(false, false);
					ui.showFlair(false);
					ui.getFlairButton().setValue(false, false);
					ui.showT1(false);
					ui.getT1Button().setValue(false, false);
				}
				ui.showT2(event.getValue());
				ui.reSetupImages();
			}
		});
		
		ui.getFlairButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(ui.getFocusedModeButton().isDown() && event.getValue()) {
					ui.showT1c(false);
					ui.getT1cButton().setValue(false, false);
					ui.showT2(false);
					ui.getT2Button().setValue(false, false);
					ui.showT1(false);
					ui.getT1Button().setValue(false, false);
				}
				ui.showFlair(event.getValue());
				ui.reSetupImages();
			}
		});
		
		ui.getPlay().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final double playSpeedInDays = Double.parseDouble(ui.getPlaySpeedTB().getText());
				Timer t = new Timer() {
					public void run() {
						// synchronize current step with slider
						double currPos = MoviePresenter.this.ui.getStudyDateSlider().getCurrentValue();
						if(!stop && currPos < ui.getMaxSize()) {
							currPos += ui.getStepSize();
							MoviePresenter.this.ui.getStudyDateSlider().setCurrentValue(currPos, true);
							// reschedule another run
							this.schedule((int)Math.round(1000.0 * ui.getStepSize()/playSpeedInDays));
						} else {
							// reset slider to start
							MoviePresenter.this.ui.moveResetStudy();
							MoviePresenter.this.ui.getStudyDateSlider().setCurrentValue(ui.getMinSize(), true);
							stop = true;
						}
					}
				};
				stop = false;
				t.schedule((int)Math.round(1000.0 * ui.getStepSize()/playSpeedInDays));
			}
			
		});
		
		ui.getStop().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MoviePresenter.this.stop = true;
			}
		
		});
		ui.getPrevious().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.movePreviousStudy();
			}
		});
		ui.getNext().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.moveNextStudy();
			}
		});
		ui.getOtherComments().addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				OtherCommentEditPresenter op = new OtherCommentEditPresenter(MoviePresenter.this, new OtherCommentEditView(), MoviePresenter.this.pid);
				op.setActive();
			}
			
		});
		ui.getGlobalComments().addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				GlobalCommentEditPresenter gp = new GlobalCommentEditPresenter(MoviePresenter.this, new GlobalCommentEditView(), MoviePresenter.this.pid);
				gp.setActive();
			}
			
		});
		ui.getOptions().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				OptionsPresenter op = new OptionsPresenter(MoviePresenter.this, new OptionsView());
				op.setActive();
			}
		});
		ui.getPlaySpeedTB().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
			}
			
		});
		ui.getFocusedModeButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()) {
					ui.startFocusedMode(true);
				} else {
					ui.exitFocusedMode();
				}
			}
		});
		ui.getCRCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				ui.setCR(event.getValue());
				ui.getDRCheckBox().setValue(false, false);
				ui.setDR(false);
			}
		});
		ui.getERCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				ui.setER(event.getValue());
			}
		});
		ui.getDRCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				ui.setDR(event.getValue());
				ui.getCRCheckBox().setValue(false, false);
				ui.setCR(false);
			}
		});
		ui.getHistogramCB().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				ui.reSetupImages();
			}
		});
		ui.getT1UpperSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getT1UpperSlider().getCurrentValue() <= ui.getT1LowerSlider().getCurrentValue()) {
					ui.getT1UpperSlider().setCurrentValue(ui.getT1LowerSlider().getCurrentValue() + 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getT1LowerSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getT1LowerSlider().getCurrentValue() >= ui.getT1UpperSlider().getCurrentValue()) {
					ui.getT1LowerSlider().setCurrentValue(ui.getT1UpperSlider().getCurrentValue() - 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getT1cUpperSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getT1cUpperSlider().getCurrentValue() <= ui.getT1cLowerSlider().getCurrentValue()) {
					ui.getT1cUpperSlider().setCurrentValue(ui.getT1cLowerSlider().getCurrentValue() + 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getT1cLowerSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getT1cLowerSlider().getCurrentValue() >= ui.getT1cUpperSlider().getCurrentValue()) {
					ui.getT1cLowerSlider().setCurrentValue(ui.getT1cUpperSlider().getCurrentValue() - 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getT2UpperSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getT2UpperSlider().getCurrentValue() <= ui.getT2LowerSlider().getCurrentValue()) {
					ui.getT2UpperSlider().setCurrentValue(ui.getT2LowerSlider().getCurrentValue() + 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getT2LowerSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getT2LowerSlider().getCurrentValue() >= ui.getT2UpperSlider().getCurrentValue()) {
					ui.getT2LowerSlider().setCurrentValue(ui.getT2UpperSlider().getCurrentValue() - 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getFlairUpperSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getFlairUpperSlider().getCurrentValue() <= ui.getFlairLowerSlider().getCurrentValue()) {
					ui.getFlairUpperSlider().setCurrentValue(ui.getFlairLowerSlider().getCurrentValue() + 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getFlairLowerSlider().addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if(ui.getFlairLowerSlider().getCurrentValue() >= ui.getFlairUpperSlider().getCurrentValue()) {
					ui.getFlairLowerSlider().setCurrentValue(ui.getFlairUpperSlider().getCurrentValue() - 1);
				}
				Date date = new Date();
				int currTime = (int) (date.getTime() * 0.001);
				if(currTime - MoviePresenter.this.time > 1) {
					ui.reSetupImages();
					MoviePresenter.this.time = currTime;
				}
			}
		});
		ui.getApplyWinLvlCheckBox().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.reSetupImages();
			}
		});
		ui.getHistEqCheckBox().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.reSetupImages();
			}
		});
	}

	private void setupQueryables() {
		this.rpcService.getDates("SELECT initial_treat_chemo_start_date from btaw.formdata_cns_initial_treatment where pid="+pid+" and initial_treat_chemo_start_date IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 45, "#FF0000", "\u25BC", false);
			}
		});
		this.rpcService.getDates("SELECT initial_treat_chemo_end_date from btaw.formdata_cns_initial_treatment where pid="+pid+" and initial_treat_chemo_end_date IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 45, "#FF0000", "\u25BC", false);
			}
		});
		this.rpcService.getDates("SELECT initial_treat_rt_start_date from btaw.formdata_cns_initial_treatment where pid="+pid+" and initial_treat_rt_start_date IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 40, "#0000FF", "\u25BC", false);
			}
		});
		this.rpcService.getDates("SELECT initial_treat_rt_end_date from btaw.formdata_cns_initial_treatment where pid="+pid+" and initial_treat_rt_end_date IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 40, "#0000FF", "\u25BC", false);
			}
		});
		this.rpcService.getDates("SELECT initial_treat_second_rt_start_date from btaw.formdata_cns_initial_treatment where pid="+pid+" and initial_treat_second_rt_start_date IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 35, "#BB00FF", "\u25CF", true);
			}
		});
		this.rpcService.getDates("SELECT initial_treat_second_rt_end_date from btaw.formdata_cns_initial_treatment where pid="+pid+" and initial_treat_second_rt_end_date IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 35, "#BB00FF", "\u25CF", true);
			}
		});
		this.rpcService.getDates("SELECT date_bx from btaw.formdata_cns_diagnosis_bx where pid="+pid+" and date_bx IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 30, "#000000", "\u25C6", true);
			}
		});
		this.rpcService.getDates("SELECT date_prelim_diag_path_reported from btaw.formdata_cns_diagnosis_bx where pid="+pid+" and date_prelim_diag_path_reported IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 25, "#FF0000", "\u25C6", true);
			}
		});
		this.rpcService.getDates("SELECT date_final_diag_path_reported from btaw.formdata_cns_diagnosis_bx where pid="+pid+" and date_final_diag_path_reported IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 25, "#FF0000", "\u25C6", true);
			}
		});
		this.rpcService.getDates("SELECT date_initial_scan from btaw.formdata_cns_scans where pid="+pid+" and date_initial_scan IS NOT NULL", new PresenterCallback<List<Date>>(this) {

			@Override
			protected void success(List<Date> result) {
				ui.drawDates(result, 20, "#0000FF", "\u25C6", true);
				ui.drawStudyDates();
			}
		});
		this.rpcService.getGlobalComments(Integer.parseInt(this.pid), new PresenterCallback<String>(this) {

			@Override
			protected void success(String result) {
				ui.setGlobalComment(result);
			}
		});
		this.rpcService.getAllUserComments(Integer.parseInt(this.pid), new PresenterCallback<HashMap<String, HashMap<String, String>>>(this) {

			@Override
			protected void success(HashMap<String, HashMap<String, String>> result) {
				ui.setUserComments(result);
			}
		});
	}
	
	public void repopulateComments() {
		this.rpcService.getGlobalComments(Integer.parseInt(this.pid), new PresenterCallback<String>(this) {

			@Override
			protected void success(String result) {
				ui.setGlobalComment(result);
			}
		});
		if (this.usernames == null && this.categories == null) {
			this.rpcService
					.getAllUserComments(
							Integer.parseInt(this.pid),
							new PresenterCallback<HashMap<String, HashMap<String, String>>>(
									this) {

								@Override
								protected void success(
										HashMap<String, HashMap<String, String>> result) {
									ui.setUserComments(result);
								}
							});
		} else {
			this.rpcService
					.getAllUserComments(
							Integer.parseInt(this.pid), this.usernames, this.categories,
							new PresenterCallback<HashMap<String, HashMap<String, String>>>(
									this) {

								@Override
								protected void success(
										HashMap<String, HashMap<String, String>> result) {
									ui.setUserComments(result);
								}
							});
		}
	}
	
	public void setupFilters(List<String> usernames, List<String> categories) {
		this.usernames = usernames;
		this.categories = categories;
		this.repopulateComments();
	}
	
	@Override
	protected IView getDisplay() {
		return ui;
	}

	@Override
	protected void activated(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}

}
