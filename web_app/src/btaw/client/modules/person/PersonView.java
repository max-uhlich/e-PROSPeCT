package btaw.client.modules.person;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import btaw.client.framework.AView;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.person.PersonPresenter.View;
import btaw.client.view.widgets.SliderBar;
import btaw.client.view.widgets.SliderBar.LabelFormatter;
import btaw.shared.model.ImageType;
import btaw.shared.model.Study;
import btaw.shared.model.query.saved.GWTPWorkaround;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

public class PersonView extends AView implements View {
	private static int DEFAULTSLICE = 9;
	private ListBox studies, imageType, segmentation;
	private DialogBox main;
	private ImageElement image;
	private GWTCanvas canvas;

	private Button left, right, close, movie, bLeft, bRight, cLeft, cRight;
        // private Button segOptions;
	private CheckBox eqImHist, applyWinLvl;
	private List<Study> studyList;
	private int selectedIndex;
	private TextBox slice, contrast, brightness;
	private LinkedHashMap<String, Integer> regions;
	
	private LinkedHashMap<Color, List<GWTPWorkaround>> segPointsFill;
	private LinkedHashMap<Color, List<GWTPWorkaround>> segPointsOutline;
	
	private List<GWTPWorkaround> points;
	
	private int pid = -1;
	
	private SliderBar upperSlider, lowerSlider;

	public PersonView() {
		selectedIndex = DEFAULTSLICE;
		segPointsFill = new LinkedHashMap<Color, List<GWTPWorkaround>>();
		segPointsOutline = new LinkedHashMap<Color, List<GWTPWorkaround>>();
		main = new DialogBox();

		slice = new TextBox();
		slice.setText(Integer.toString(DEFAULTSLICE+1));
		slice.setWidth("25px");
		Label sliceL = new Label("Slice");
		sliceL.setStyleName("center-Label");
		
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		HorizontalPanel hp2 = new HorizontalPanel();
		HorizontalPanel hp3 = new HorizontalPanel();
		HorizontalPanel hp4 = new HorizontalPanel();
		HorizontalPanel hp5 = new HorizontalPanel();
		HorizontalPanel segHp = new HorizontalPanel();
		hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		hp.setWidth("100%");
		segHp.setWidth("100%");

		left = new Button("<");
		right = new Button(">");

		studies = new ListBox();
		imageType = new ListBox();
		segmentation = new ListBox();
		
		eqImHist = new CheckBox("Equalize Image Histogram");
		applyWinLvl = new CheckBox("Apply Window Level");

		applyWinLvl.setValue(false);

		hp.add(studies);
		hp.add(imageType);
		hp.add(left);
		hp.add(sliceL);
		hp.add(slice);
		hp.add(right);
		
		hp2.add(eqImHist);
		hp2.add(applyWinLvl);
		
		bLeft = new Button("<");
		bRight = new Button(">");
		cLeft = new Button("<");
		cRight = new Button(">");
		
		contrast = new TextBox();
		contrast.setText("100");
		contrast.setWidth("50px");
		brightness = new TextBox();
		brightness.setText("100");
		brightness.setWidth("50px");
		
		hp3.add(bLeft);
		Label brightL = new Label("Brightness");
		brightL.setStyleName("center-Label");
		Label bPercentL = new Label("%");
		bPercentL.setStyleName("center-Label");
		hp3.add(brightL);
		hp3.add(brightness);
		hp3.add(bPercentL);
		hp3.add(bRight);
		
		hp4.add(cLeft);
		Label contrastL = new Label("Contrast");
		contrastL.setStyleName("center-Label");
		Label cPercentL = new Label("%");
		cPercentL.setStyleName("center-Label");
		hp4.add(contrastL);
		hp4.add(contrast);
		hp4.add(cPercentL);
		hp4.add(cRight);
		
		hp5.setWidth("100%");
		AbsolutePanel sliderBarAbs = new AbsolutePanel();
		sliderBarAbs.setWidth(hp5.getElement().getStyle().getWidth());
		sliderBarAbs.setHeight("100%");
		hp5.add(sliderBarAbs);
		
		lowerSlider = new SliderBar(0, 255);
		upperSlider = new SliderBar(0.0, 255.0, new LabelFormatter() {
			
			@Override
			public String formatLabel(SliderBar slider, double value) {
				return Long.toString(Math.round(value));
			}
		});
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
		
		sliderBarAbs.add(lowerSlider, 0, 0);
		sliderBarAbs.add(upperSlider, 0, 0);
		
		Label segL = new Label("Display Segmentation: ");
		segL.setStyleName("center-Label");
		segHp.add(segL);
		segHp.add(segmentation);

		canvas = new GWTCanvas(256, 256);
		vp.add(hp);
		//vp.add(hp3);
		//vp.add(hp4);
		vp.add(hp2);
		vp.add(hp5);
		//vp.add(segHp);
		vp.add(canvas);

		HorizontalPanel bottom = new HorizontalPanel();
		close = new Button("Close");
		movie = new Button("Movie");
		//segOptions = new Button("Segmentation Options");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		//alignPanel.add(segOptions);
		alignPanel.add(movie);
		alignPanel.add(close);

		vp.add(bottom);

		main.add(vp);
		
	}

	@Override
	public void populateStudiesAndShow(List<Study> studies, String pid) {
		
		for (Study study : studies) {
			this.studies.addItem(DateTimeFormat.getFormat("yyyy/MM/dd").format(
					study.getDate()));
		}
		
		Study s = studies.get(this.studies.getSelectedIndex());
		this.studyList = studies;
		for (ImageType type : s.getTypes()) {
			this.imageType.addItem(type.getDescription());
			//System.err.println(type.getDescription());
		}

		regions = FilterPresenter.get().getRegionsOfInterest();
		
		segmentation.addItem("Please Select");
		for(Map.Entry<String, Integer> entry : regions.entrySet()) {
			segmentation.addItem(entry.getKey());
		}
		
		this.pid = Integer.parseInt(pid);
		main.setText("Patient " + this.pid + " Studies");
		main.setModal(true);
		main.center();
		main.getElement().getStyle().setProperty("width", "auto");
		main.getElement().getStyle().setZIndex(2);
		
		setImage();
		
		MainPresenter.get().finished();
	}
	
	@Override
	public void setImage() {
		setImage(selectedIndex);
	}
	
	public void setImage(int index) {
		if(this.imageType.getItemCount() == 0) {
			System.err.println("NO IMAGES IN IMAGETYPE");
		}
		List<String> ids = studyList.get(this.studies.getSelectedIndex())
			.getType(this.imageType.getItemText(this.imageType.getSelectedIndex())).getIds();
		if (index >= 0 && index < ids.size()){
			String id = ids.get(index);
			String[] urls = new String[]{"https://ellis-rv.cs.ualberta.ca/btaw/btaw_gui/images?id="+id
					+"&pid="+pid
					+"&study_date="+this.studies.getItemText(this.studies.getSelectedIndex())
					+"&modality="+this.imageType.getItemText(this.imageType.getSelectedIndex())
					+"&slicenr="+(index+1)
					+"&cr=false"
					+"&dr=true"
					+"&er=true"
					+"&hist_eq="+eqImHist.getValue()
					+"&apply_wl="+applyWinLvl.getValue()
					+"&lwr="+( lowerSlider.getCurrentValue()/255.0 )
					+"&upr="+( upperSlider.getCurrentValue()/255.0 )};
			ImageLoader.loadImages(urls, new ImageLoader.CallBack() {

				public void onImagesLoaded(ImageElement[] imageHandles) {
					canvas.clear();
					ImageElement img = imageHandles[0];
					image = img;
					canvas.drawImage(image, 0, 0, 256, 256);
					PersonView.this.redrawPnts();
					PersonView.this.redrawFill();
					PersonView.this.redrawOutline();
				}
			});
		} else {
			System.err.println("PersonView attempting to access index out of bounds.");
		}
	}
	
	public void repopulateImageTypes() {
		this.imageType.clear();
		for(ImageType t : studyList.get(this.studies.getSelectedIndex()).getTypes()) {
			this.imageType.addItem(t.getDescription());
			System.err.println("WTF : " + t.getDescription());
		}
	}
	
	@Override
	public void incrId(){
		if(selectedIndex + 1 < studyList.get(this.studies.getSelectedIndex())
									.getType(this.imageType.getItemText(this.imageType.getSelectedIndex()))
									.getIds().size()) {
			System.err.println("MAX : " + studyList.get(this.studies.getSelectedIndex())
									.getType(this.imageType.getItemText(this.imageType.getSelectedIndex()))
									.getIds().size());
			++selectedIndex;
			setImage(selectedIndex);
			slice.setText(Integer.toString(selectedIndex + 1));
			System.err.println("INCR CALLED : " + selectedIndex);
		}
	}
	@Override
	public void decrId(){
		if(selectedIndex > 0) {
			--selectedIndex;
			setImage(selectedIndex);
			slice.setText(Integer.toString(selectedIndex + 1));
			System.err.println("DECR CALLED : " + selectedIndex);
		}
	}

	@Override
	public ListBox getListBox() {
		return studies;
	}

	@Override
	public ListBox getImageType() {
		return imageType;
	}

	@Override
	public GWTCanvas getCanvas() {
		return canvas;
	}

	@Override
	public Button getLeftButton() {
		return left;
	}

	@Override
	public Button getRightButton() {
		return right;
	}

	public Button getCLeftButton() {
		return cLeft;
	}
	public Button getCRightButton() {
		return cRight;
	}
	public Button getBLeftButton() {
		return bLeft;
	}
	public Button getBRightButton() {
		return bRight;
	}
	public TextBox getContrast() {
		return this.contrast;
	}
	public TextBox getBrightness() {
		return this.brightness;
	}

	@Override
	public Button getCloseButton() {
		return close;
	}

	@Override
	public void close() {
		main.hide();
	}
	
	public void resetSlice() {
		selectedIndex = DEFAULTSLICE;
		slice.setText(Integer.toString(DEFAULTSLICE+1));
	}
	
	public Button getMovieButton() {
		return this.movie;
	}
	
	//public Button getSegOptionsButton() {
	//	return this.segOptions;
	//}
	
	public CheckBox getEqualizeHistogramCheckBox() {
		return this.eqImHist;
	}
	public CheckBox getApplyWinLvlCheckBox() {
		return this.applyWinLvl;
	}
	@Override
	public ListBox getSegType() {
		return this.segmentation;
	}
	
	@Override
	public int getSegID() {
		return regions.get(segmentation.getValue(segmentation.getSelectedIndex()));
	}
	
	@Override
	public int getStudyID() {
		return Integer.parseInt(studyList.get(this.studies.getSelectedIndex()).getId());
	}
	
	@Override
	public void setPoints(List<GWTPWorkaround> pnts) {
		this.points = pnts;
		redrawPnts();
	}
	
	public int getSlice() {
		return Integer.parseInt(this.slice.getText());
	}
	
	public void redrawPnts() {
		if(this.points == null || segmentation.getSelectedIndex() == 0) {
			return;
		}
		
		int currZ = Integer.parseInt(slice.getText());

		canvas.setLineWidth(1);
		canvas.setStrokeStyle(Color.RED);
		
		for(int i = 0; i < this.points.size(); i++) {
			GWTPWorkaround p = this.points.get(i);
			if(Integer.parseInt(p.getZ_value()) != currZ) {
				continue;
			}
			canvas.strokeRect(Math.round(Integer.parseInt(p.getX_value())/2.0), Math.round(Integer.parseInt(p.getY1_value())/2.0), 0.5, 0.5);
			canvas.strokeRect(Math.round(Integer.parseInt(p.getX_value())/2.0), Math.round(Integer.parseInt(p.getY2_value())/2.0), 0.5, 0.5);
		}
	}
	
	public void redrawFill() {
		if(this.segPointsFill.isEmpty())
			return;
		
		int currZ = Integer.parseInt(slice.getText());
		
		canvas.setLineWidth(1);
		
		for(Map.Entry<Color, List<GWTPWorkaround>> entry : segPointsFill.entrySet()) {
			canvas.setStrokeStyle(entry.getKey());
			
			for(int i = 0; i < entry.getValue().size(); i++) {
				GWTPWorkaround p = entry.getValue().get(i);
				if(Integer.parseInt(p.getZ_value()) != currZ) {
					continue;
				}
				canvas.beginPath();
				canvas.moveTo(Math.round(Integer.parseInt(p.getX_value())/2.0), Math.round(Integer.parseInt(p.getY1_value())/2.0));
				canvas.lineTo(Math.round(Integer.parseInt(p.getX_value())/2.0), Math.round(Integer.parseInt(p.getY2_value())/2.0));
				canvas.stroke();
			}
		}
	}
	
	public void redrawOutline() {
		if(this.segPointsOutline.isEmpty())
			return;
		
		int currZ = Integer.parseInt(slice.getText());
		
		canvas.setLineWidth(1);
		for(Map.Entry<Color, List<GWTPWorkaround>> entry : segPointsOutline.entrySet()) {
			canvas.setStrokeStyle(entry.getKey());
			
			for(int i = 0; i < entry.getValue().size(); i++) {
				GWTPWorkaround p = entry.getValue().get(i);
				if(Integer.parseInt(p.getZ_value()) != currZ) {
					continue;
				}
				canvas.strokeRect(Math.round(Integer.parseInt(p.getX_value())/2.0), Math.round(Integer.parseInt(p.getY1_value())/2.0), 0.5, 0.5);
				canvas.strokeRect(Math.round(Integer.parseInt(p.getX_value())/2.0), Math.round(Integer.parseInt(p.getY2_value())/2.0), 0.5, 0.5);

			}
		}
	}
	
	public void setPointsFill(Color color, List<GWTPWorkaround> pnts) {
		segPointsFill.put(color, pnts);
		this.setImage();
	}
	
	public void removePointsFill(Color color) {
		if(segPointsFill.containsKey(color))
			segPointsFill.remove(color);

		this.setImage();
	}
	
	public void setPointsOutline(Color color, List<GWTPWorkaround> pnts) {
		segPointsOutline.put(color, pnts);
		this.setImage();
	}
	
	public void removePointsOutline(Color color) {
		if(segPointsOutline.containsKey(color))
			segPointsOutline.remove(color);

		this.setImage();
	}
	
	public SliderBar getUpperSlider() {
		return this.upperSlider;
	}
	
	public SliderBar getLowerSlider() {
		return this.lowerSlider;
	}

}
