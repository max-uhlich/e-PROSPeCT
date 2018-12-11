package btaw.client.modules.movie;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.view.widgets.SliderBarMovie;
import btaw.shared.model.Study;

public class MovieLightPresenter extends Presenter {

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
		void movePrevious();
		void moveNext();
		TextBox getPlaySpeedTB();
		CheckBox getCRCheckBox();
		CheckBox getERCheckBox();
		CheckBox getDRCheckBox();
		void setCR(boolean val);
		void setER(boolean val);
		void setDR(boolean val);
		void reSetupImages();
	}
	
	private String pid;
	private final View ui;
	private int currStep = 0;
	private boolean stop = false;
	private String modality;
	private int slice = 0;
	
	public MovieLightPresenter(Presenter parent, View ui, String pid, String modality, int slice) {
		super(parent);
		
		this.pid = pid;
		this.ui = ui;
		this.modality = modality;
		this.slice = slice;
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
				if(event.getValue()) {
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
				if(event.getValue()) {
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
				if(event.getValue()) {
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
				if(event.getValue()) {
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
				Timer t = new Timer() {
					public void run() {
						if(ui.getMaxSize() > ui.getMinSize() + (double)MovieLightPresenter.this.currStep * ui.getStepSize() && !stop) {
							MovieLightPresenter.this.ui.getStudyDateSlider().setCurrentValue(ui.getMinSize() + ui.getStepSize()*(double)++currStep, true);
							double playSpeedInDays = Double.parseDouble(ui.getPlaySpeedTB().getText());
							this.schedule((int)Math.round(1000 * ui.getStepSize()/playSpeedInDays));
						} else {
							currStep = 0;
							stop = false;
						}
					}
				};
				double playSpeedInDays = Double.parseDouble(ui.getPlaySpeedTB().getText());
				t.schedule((int)Math.round(1000 * ui.getStepSize()/playSpeedInDays));
			}
			
		});
		
		ui.getStop().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MovieLightPresenter.this.stop = true;
			}
		
		});
		ui.getPrevious().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.movePrevious();
			}
		});
		ui.getNext().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ui.moveNext();
			}
		});
		ui.getOptions().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				OptionsPresenter op = new OptionsPresenter(MovieLightPresenter.this, new OptionsView());
				op.setActive();
			}
		});
		ui.getPlaySpeedTB().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
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
