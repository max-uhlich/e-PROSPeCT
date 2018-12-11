package btaw.client.modules.image;


import java.util.LinkedHashMap;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.image.distance.DistancePresenter;
import btaw.client.modules.image.distance.DistanceView;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.NoFilter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ImageValuePanel extends HorizontalPanel implements ClickHandler{
	
	private String[] labels = {};
	
	//private final ListBox valueEntry;
	private final Button popupButton;
	private DialogBox imageSelectionBox;
	private Button cancelButton;
	private Button doneButton;
	private ImagePresenter activeP;
	private LinkedHashMap<String, Integer> regionsOfInterest;
	private String selection;

	private Presenter presenter;
	public ImageValuePanel(Presenter p)
	{
		regionsOfInterest = new LinkedHashMap<String, Integer>(); //null pointer protection
		selection = ImagePresenter.DISTANCE; //also null pointer protection
		
		this.presenter = p;
		popupButton = new Button("Specify Query");
		popupButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (imageSelectionBox != null)
					return;
				
				if (activeP != null){
					if (!activeP.isType(selection)){
						activeP = buildPresenter(selection);
						activeP.setActive();
						((ImagePresenter)activeP).setSegTypes(regionsOfInterest);
					}
				} else {
					activeP = buildPresenter(selection);
					activeP.setActive();
					((ImagePresenter)activeP).setSegTypes(regionsOfInterest);
				}
				
				imageSelectionBox = buildDialogBox(activeP.getDisplay());
				imageSelectionBox.center();
			}
		});
		this.add(popupButton);
	}
	
	public void setRegionsOfInterest(LinkedHashMap<String, Integer> regions) {
		regionsOfInterest = regions;
	}

	protected ImagePresenter buildPresenter(String val) {
		if (val.equals(ImagePresenter.SIMILARITY))
			return new SimilarityPresenter(presenter, new SimilarityView());
		if (val.equals(ImagePresenter.DISTANCE))
			return new DistancePresenter(presenter, new DistanceView());
		if (val.equals(ImagePresenter.RATIOS))
			return new RatioPresenter(presenter, new RatioView());
		return null;
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
	
	public void onClick(ClickEvent event) {
		if (event.getSource() == doneButton) {
			imageSelectionBox.hide();
			imageSelectionBox = null;
		} else if (event.getSource() == cancelButton) {
			imageSelectionBox.hide();
			imageSelectionBox = null;
			activeP = null;
		}
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	public Filter getFilter() {
		if (activeP != null) {
			return activeP.getFilter();
		} else {
			return new NoFilter();
		}
	}
	
	public LinkedHashMap<String, Integer> getRegionsOfInterest() {
		return regionsOfInterest;
	}
	
	public DistancePresenter getDistancePresenter() {
		if(activeP instanceof DistancePresenter) {
			return (DistancePresenter)activeP;
		} else {
			return null;
		}
	}
	
	public SimilarityPresenter getSimilarityPresenter() {
		if(activeP instanceof SimilarityPresenter) {
			return (SimilarityPresenter)activeP;
		} else {
			return null;
		}
	}
	
	public RatioPresenter getRatioPresenter() {
		if(activeP instanceof RatioPresenter) {
			return (RatioPresenter)activeP;
		} else {
			return null;
		}
	}
	public void setupRatioPresenter() {
		activeP = new RatioPresenter(presenter, new RatioView());
		activeP.setActive();
		((ImagePresenter)activeP).setSegTypes(regionsOfInterest);
	}
	public void setupDistancePresenter() {
		activeP = new DistancePresenter(presenter, new DistanceView());
		activeP.setActive();
		((ImagePresenter)activeP).setSegTypes(regionsOfInterest);
	}
	public void setupSimilarityPresenter() {
		activeP = new SimilarityPresenter(presenter, new SimilarityView());	
		activeP.setActive();
		((ImagePresenter)activeP).setSegTypes(regionsOfInterest);
	}
}
