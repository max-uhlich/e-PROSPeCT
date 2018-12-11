package btaw.client.modules.synth;


import java.util.ArrayList;
import java.util.List;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.FloatTableColumn;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.NoFilter;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

// adapting ImageValuePanel for Synthetic column creation
public class SynthValuePanel extends HorizontalPanel implements ClickHandler{
	
	private String[] labels = {};
	
	//private final ListBox valueEntry;
	private final Button popupButton;
	private DialogBox imageSelectionBox;
	private Button cancelButton;
	private Button doneButton;
	private SynthPresenter activeP;
//	private LinkedHashMap<String, Integer> regionsOfInterest;
	private String name;
	private TextBox tb;
	private Column column;
	private ListBox operator;

	private Presenter presenter;
	public SynthValuePanel(Presenter p)
	{
//		regionsOfInterest = new LinkedHashMap<String, Integer>(); //null pointer protection
//		selection = ImagePresenter.SIMILARITY; //also null pointer protection
		
		this.presenter = p;
		popupButton = new Button("Define");
		popupButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (imageSelectionBox != null)
					return;
				
				if (activeP != null){
					if (!activeP.getName().equals(name)){
						activeP = buildPresenter(name);
						activeP.setActive();
//						((SynthPresenter)activeP).setSegTypes(regionsOfInterest);
					}
				} else {
					activeP = buildPresenter(name);
					activeP.setActive();
//					((SynthPresenter)activeP).setSegTypes(regionsOfInterest);
				}
				
				imageSelectionBox = buildDialogBox(activeP.getDisplay());
				imageSelectionBox.center();
			}
		});
		this.add(popupButton);
	}
/*	
	public void setRegionsOfInterest(LinkedHashMap<String, Integer> regions) {
		regionsOfInterest = regions;
	}
*/
	protected SynthPresenter buildPresenter(String val) {
/*		
		if (val.equals(ImagePresenter.SIMILARITY))
			return new SimilarityPresenter(presenter, new SimilarityView());
		if (val.equals(ImagePresenter.DISTANCE))
			return new DistancePresenter(presenter, new DistanceView());
		if (val.equals(ImagePresenter.RATIOS))
			return new RatioPresenter(presenter, new RatioView());
		return null;
		*/
		if (val.equals("Unnamed"))
    		return new SynthPresenter(FilterPresenter.get(), new SynthView());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Filter getFilter() {
		if (activeP != null) {
			return activeP.getFilter();
		} else {
			return new NoFilter();
		}
	}

/*	
	public LinkedHashMap<String, Integer> getRegionsOfInterest() {
		return regionsOfInterest;
	}
*/
/*	
	public DistancePresenter getDistancePresenter() {
		if(activeP instanceof DistancePresenter) {
			return (DistancePresenter)activeP;
		} else {
			return null;
		}
	}
*/
/*	
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
*/

	protected Panel getDifferentPart() {
		
		HorizontalPanel hp = new HorizontalPanel();
		tb = new TextBox();
		ArrayList<String> ops = new ArrayList<String>(FloatTableColumn.opStringList());
		this.setOperators(ops);
		tb.setWidth("40px");
		
		tb.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				TablePresenter.get().setEdited();
				try 
				{
					Float.parseFloat(tb.getText());
					tb.getElement().getStyle().setBackgroundColor("#FFFFFF");
				}
				catch (NumberFormatException e)
				{
					if(SynthValuePanel.this.getOpIndex() < FloatTableColumn.Op.values().length - 2) {
						tb.getElement().getStyle().setBackgroundColor("#FFAAAA");
						tb.setFocus(true);
					}
				}
			}
		});
		hp.add(tb);
		return hp;
	}
	
	public Column getColumn() {
		return this.column;
	}
	
	public void setColumn(Column column) {
		this.column = column;
	}

	public void setOperators(List<String> ops)
	{
		for (String op : ops){
			this.operator.addItem(op);
		}
	}

	public int getOpIndex()
	{
		return this.operator.getSelectedIndex();
	}
}
