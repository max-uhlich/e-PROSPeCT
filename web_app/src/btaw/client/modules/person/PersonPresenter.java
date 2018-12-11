package btaw.client.modules.person;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.movie.MoviePresenter;
import btaw.client.modules.movie.MovieView;
import btaw.client.view.widgets.SliderBar;
import btaw.shared.model.Study;
import btaw.shared.model.query.saved.GWTPWorkaround;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class PersonPresenter extends Presenter {

	public interface View extends IView {
		void populateStudiesAndShow(List<Study> studies, String pid);

		ListBox getListBox();

		ListBox getImageType();

		Button getLeftButton();

		Button getRightButton();

		Button getCloseButton();

		Button getMovieButton();

		// Button getSegOptionsButton();
		GWTCanvas getCanvas();

		void close();

		void setImage();

		void decrId();

		void incrId();

		void repopulateImageTypes();

		void resetSlice();

		ListBox getSegType();

		void setPoints(List<GWTPWorkaround> pnts);

		int getSegID();

		int getStudyID();

		void redrawPnts();

		int getSlice();

		void setPointsFill(Color color, List<GWTPWorkaround> pnts);

		void setPointsOutline(Color color, List<GWTPWorkaround> pnts);

		void redrawFill();

		void redrawOutline();

		void removePointsOutline(Color color);

		void removePointsFill(Color color);

		CheckBox getEqualizeHistogramCheckBox();

		CheckBox getApplyWinLvlCheckBox();

		Button getCLeftButton();

		Button getCRightButton();

		Button getBLeftButton();

		Button getBRightButton();

		TextBox getContrast();

		TextBox getBrightness();

		SliderBar getUpperSlider();

		SliderBar getLowerSlider();
	}

	private String pid;
	private final View ui;
	private SegOptionsPresenter segOptions;

	private int time = 0;

	private LinkedHashMap<Integer, Color> fillUserSegColors;
	private LinkedHashMap<Integer, Color> outlineUserSegColors;
	private LinkedHashMap<Integer, Color> fillPatientSegColors;
	private LinkedHashMap<Integer, Color> outlinePatientSegColors;

	public PersonPresenter(Presenter parent, View ui, String pid) {
		super(parent);

		this.pid = pid;
		this.ui = ui;
		fillUserSegColors = new LinkedHashMap<Integer, Color>();
		outlineUserSegColors = new LinkedHashMap<Integer, Color>();
		fillPatientSegColors = new LinkedHashMap<Integer, Color>();
		outlinePatientSegColors = new LinkedHashMap<Integer, Color>();
		Date date = new Date();
		this.time = (int) (date.getTime() * 0.001);
	}

	@Override
	protected void bindAll() {
		this.rpcService.getStudies(pid,
				new PresenterCallback<List<Study>>(this) {

					@Override
					protected void success(List<Study> result) {
						ui.populateStudiesAndShow(result, pid);
						MainPresenter.get().finished();
					}
				});

		ui.getListBox().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (segOptions != null) {
					for (Map.Entry<Integer, Color> entry : fillUserSegColors
							.entrySet()) {
						PersonPresenter.this.addUserPointsFill(
								entry.getValue(), entry.getKey());
					}
					for (Map.Entry<Integer, Color> entry : outlineUserSegColors
							.entrySet()) {
						PersonPresenter.this.addUserPointsOutline(
								entry.getValue(), entry.getKey());

					}
					for (Map.Entry<Integer, Color> entry : fillPatientSegColors
							.entrySet()) {
						PersonPresenter.this.addPatientPointsFill(
								entry.getValue(), entry.getKey());

					}
					for (Map.Entry<Integer, Color> entry : outlinePatientSegColors
							.entrySet()) {
						PersonPresenter.this.addPatientPointsOutline(
								entry.getValue(), entry.getKey());

					}
				}
				ui.resetSlice();
				ui.repopulateImageTypes();
				ui.setImage();
				ui.getSegType().setSelectedIndex(0);
			}
		});

		ui.getImageType().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ui.resetSlice();
				ui.setImage();

			}
		});

		ui.getRightButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				ui.incrId();
			}
		});

		ui.getLeftButton().addClickHandler(new ClickHandler() {

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

		ui.getMovieButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MainPresenter.get().working(false);
				MoviePresenter mp = new MoviePresenter(PersonPresenter.this,
						new MovieView(), PersonPresenter.this.pid, ui
								.getImageType().getItemText(
										ui.getImageType().getSelectedIndex()),
						ui.getSlice() - 1);
				mp.setActive();
			}

		});

		// ui.getSegOptionsButton().addClickHandler(new ClickHandler() {
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// if(segOptions != null)
		// return;
		// segOptions = new SegOptionsPresenter(PersonPresenter.this, new
		// SegOptionsView());
		// segOptions.setActive();
		// }
		// });

		ui.getSegType().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (ui.getSegType().getSelectedIndex() == 0) {
					ui.setImage();
					return;
				}
				setPoints(ui.getSegID(), ui.getStudyID());
			}

		});
		ui.getEqualizeHistogramCheckBox().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ui.setImage();
			}
		});
		ui.getApplyWinLvlCheckBox().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ui.setImage();
			}
		});
		ui.getBrightness().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				try {
					int brightness = Integer.parseInt(ui.getBrightness()
							.getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ui.getBrightness().setText("100");
				}
				ui.setImage();
			}
		});
		ui.getContrast().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				try {
					int contrast = Integer.parseInt(ui.getContrast().getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ui.getContrast().setText("100");
				}
				ui.setImage();
			}
		});
		ui.getCLeftButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int contrast = -1;
				try {
					contrast = Integer.parseInt(ui.getContrast().getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ui.getContrast().setText("100");
				}
				if (contrast <= -100)
					return;
				contrast = contrast - 10;
				ui.getContrast().setText(Integer.toString(contrast));
				ui.setImage();
			}
		});
		ui.getCRightButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int contrast = -1;
				try {
					contrast = Integer.parseInt(ui.getContrast().getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ui.getContrast().setText("100");
				}
				if (contrast >= 300)
					return;
				contrast = contrast + 10;
				ui.getContrast().setText(Integer.toString(contrast));
				ui.setImage();
			}
		});
		ui.getBLeftButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int brightness = -1;
				try {
					brightness = Integer.parseInt(ui.getBrightness().getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ui.getBrightness().setText("100");
				}
				if (brightness <= -100)
					return;
				brightness = brightness - 10;
				ui.getBrightness().setText(Integer.toString(brightness));
				ui.setImage();
			}
		});
		ui.getBRightButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int brightness = -1;
				try {
					brightness = Integer.parseInt(ui.getBrightness().getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ui.getBrightness().setText("100");
				}
				if (brightness >= 300)
					return;
				brightness = brightness + 10;
				ui.getBrightness().setText(Integer.toString(brightness));
				ui.setImage();
			}
		});
		ui.getLowerSlider().addValueChangeHandler(
				new ValueChangeHandler<Double>() {

					@Override
					public void onValueChange(ValueChangeEvent<Double> event) {
						if (ui.getLowerSlider().getCurrentValue() >= ui
								.getUpperSlider().getCurrentValue()) {
							ui.getLowerSlider().setCurrentValue(
									ui.getUpperSlider().getCurrentValue() - 1);
						}
						Date date = new Date();
						int currTime = (int) (date.getTime() * 0.001);
						if (currTime - PersonPresenter.this.time > 1) {
							ui.setImage();
							PersonPresenter.this.time = currTime;
						}
					}
				});

		ui.getUpperSlider().addValueChangeHandler(
				new ValueChangeHandler<Double>() {

					@Override
					public void onValueChange(ValueChangeEvent<Double> event) {
						if (ui.getUpperSlider().getCurrentValue() <= ui
								.getLowerSlider().getCurrentValue()) {
							ui.getUpperSlider().setCurrentValue(
									ui.getLowerSlider().getCurrentValue() + 1);
						}
						Date date = new Date();
						int currTime = (int) (date.getTime() * 0.001);
						if (currTime - PersonPresenter.this.time > 1) {
							ui.setImage();
							PersonPresenter.this.time = currTime;
						}
					}
				});
	}

	public void addUserPointsFill(final Color color, int queryId) {
		fillPatientSegColors.put(queryId, color);
		rpcService.getUserSegPoints(queryId,
				new PresenterCallback<List<GWTPWorkaround>>(this) {

					@Override
					protected void success(List<GWTPWorkaround> result) {
						System.err.println("SIZE AFTER : " + result.size());
						ui.setPointsFill(color, result);
					}

				});
	}

	public void addPatientPointsFill(final Color color, int segId) {
		fillUserSegColors.put(segId, color);
		rpcService.getSegPoints(segId, ui.getStudyID(),
				new PresenterCallback<List<GWTPWorkaround>>(this) {

					@Override
					protected void success(List<GWTPWorkaround> result) {
						System.err.println("SIZE AFTER : " + result.size());
						ui.setPointsFill(color, result);
					}

				});
	}

	public void addUserPointsOutline(final Color color, int queryId) {
		outlineUserSegColors.put(queryId, color);
		rpcService.getUserSegPoints(queryId,
				new PresenterCallback<List<GWTPWorkaround>>(this) {

					@Override
					protected void success(List<GWTPWorkaround> result) {
						System.err.println("SIZE AFTER : " + result.size());
						ui.setPointsOutline(color, result);
					}

				});
	}

	public void addPatientPointsOutline(final Color color, int segId) {
		outlinePatientSegColors.put(segId, color);
		rpcService.getSegPoints(segId, ui.getStudyID(),
				new PresenterCallback<List<GWTPWorkaround>>(this) {

					@Override
					protected void success(List<GWTPWorkaround> result) {
						System.err.println("SIZE AFTER : " + result.size());
						ui.setPointsOutline(color, result);
					}

				});
	}

	public void removePatientPoints(int segId) {
		ui.removePointsFill(fillPatientSegColors.remove(segId));
		ui.removePointsOutline(outlinePatientSegColors.remove(segId));
	}

	public void removeUserPoints(int queryId) {
		ui.removePointsOutline(outlineUserSegColors.remove(queryId));
		ui.removePointsFill(fillUserSegColors.remove(queryId));
	}

	public void setPoints(int seg_id, int study_id) {
		rpcService.getSegPoints(seg_id, study_id,
				new PresenterCallback<List<GWTPWorkaround>>(this) {

					@Override
					protected void success(List<GWTPWorkaround> result) {
						System.err.println("SIZE AFTER : " + result.size());
						ui.setPoints(result);
					}

				});
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}

	public void closeSegOptiosn() {
		this.segOptions = null;
	}

	@Override
	protected void activated(HasWidgets container) {
	}

}
