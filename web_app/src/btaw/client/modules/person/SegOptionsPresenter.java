package btaw.client.modules.person;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.widgetideas.graphics.client.Color;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;

public class SegOptionsPresenter extends Presenter {

	public interface View extends IView {
		LinkedHashMap<Integer, ListBox> populateSegmentations(LinkedHashMap<String, Integer> segmentations);
		LinkedHashMap<Integer, ListBox> populateUserSegmentations(LinkedHashMap<Date, Integer> userSegmentations);
		Button getCloseButton();
		void close();
	}
	
	private final View ui;
	private LinkedHashMap<Integer, ListBox> segOptions;
	private LinkedHashMap<Integer, ListBox> userSegOptions;
	private LinkedHashMap<Integer, Color> segColors;
	private LinkedHashMap<Integer, Color> userColors;
	public static final Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.PINK};
	private List<Color> availableColors;
	private int numSelected = 0;
	
	public SegOptionsPresenter(Presenter parent, View ui) {
		super(parent);
		
		this.ui = ui;
		this.segColors = new LinkedHashMap<Integer, Color>();
		this.userColors = new LinkedHashMap<Integer, Color>();
		this.availableColors = new ArrayList<Color>();
		for(int i = 0; i < colors.length; i++) {
			availableColors.add(colors[i]);
		}
	}

	@Override
	protected void bindAll() {
		rpcService.getUserSegmentations(new AsyncCallback<LinkedHashMap<Date,Integer>>() {
			
			@Override
			public void onSuccess(LinkedHashMap<Date, Integer> result) {
				LinkedHashMap<Integer, ListBox> segListBox = ui.populateUserSegmentations(result);
				if(segListBox == null)
					return;
				
				segOptions = segListBox;
				
				for(Map.Entry<Integer, ListBox> entry : segListBox.entrySet()) {
					final Integer segId = entry.getKey();
					final ListBox lb = entry.getValue();
					entry.getValue().addChangeHandler(new ChangeHandler() {

						@Override
						public void onChange(ChangeEvent event) {
							if(numSelected >= SegOptionsPresenter.colors.length) {
								return;
							}
							if(lb.getItemText(lb.getSelectedIndex()).equals("Fill")) {
								((PersonPresenter)SegOptionsPresenter.this.parent).addUserPointsFill(availableColors.get(0), segId);
								userColors.put(segId, availableColors.get(0));
								lb.getElement().getStyle().setBackgroundColor(availableColors.get(0).toString());
								availableColors.remove(0);
								numSelected++;
							} else if (lb.getItemText(lb.getSelectedIndex()).equals("Outline")) {
								((PersonPresenter)SegOptionsPresenter.this.parent).addUserPointsOutline(availableColors.get(0), segId);
								userColors.put(segId, availableColors.get(0));
								lb.getElement().getStyle().setBackgroundColor(availableColors.get(0).toString());
								availableColors.remove(0);
								numSelected++;
							} else {
								((PersonPresenter)SegOptionsPresenter.this.parent).removeUserPoints(segId);
								availableColors.add(userColors.get(segId));
								lb.getElement().getStyle().setBackgroundColor("white");
								userColors.remove(segId);
								numSelected--;
							}
						}
						
					});
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		rpcService.getRegionsOfInterest(new AsyncCallback<LinkedHashMap<String,Integer>>() {


			@Override
			public void onSuccess(LinkedHashMap<String, Integer> result) {
				LinkedHashMap<Integer, ListBox> segListBox = ui.populateSegmentations(result);
				if(segListBox == null)
					return;
				
				userSegOptions = segListBox;
				
				for(Map.Entry<Integer, ListBox> entry : segListBox.entrySet()) {
					final Integer segId = entry.getKey();
					final ListBox lb = entry.getValue();
					entry.getValue().addChangeHandler(new ChangeHandler() {

						@Override
						public void onChange(ChangeEvent event) {
							if(numSelected >= SegOptionsPresenter.colors.length) {
								return;
							}
							if(lb.getItemText(lb.getSelectedIndex()).equals("Fill")) {
								((PersonPresenter)SegOptionsPresenter.this.parent).addPatientPointsFill(availableColors.get(0), segId);
								userColors.put(segId, availableColors.get(0));
								lb.getElement().getStyle().setBackgroundColor(availableColors.get(0).toString());
								availableColors.remove(0);
								numSelected++;
							} else if (lb.getItemText(lb.getSelectedIndex()).equals("Outline")) {
								((PersonPresenter)SegOptionsPresenter.this.parent).addPatientPointsOutline(availableColors.get(0), segId);
								userColors.put(segId, availableColors.get(0));
								lb.getElement().getStyle().setBackgroundColor(availableColors.get(0).toString());
								availableColors.remove(0);
								numSelected++;
							} else {
								((PersonPresenter)SegOptionsPresenter.this.parent).removePatientPoints(segId);
								availableColors.add(userColors.get(segId));
								lb.getElement().getStyle().setBackgroundColor("white");
								userColors.remove(segId);
								numSelected--;
							}
						}
						
					});
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		ui.getCloseButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.close();
				((PersonPresenter)parent).closeSegOptiosn();
			}
		});
	}
	
	public LinkedHashMap<Integer, ListBox> getUserSegmentations() {
		return userSegOptions;
	}
	
	public LinkedHashMap<Integer, ListBox> getSegmentations() {
		return segOptions;
	}

	@Override
	protected IView getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void activated(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}

}
