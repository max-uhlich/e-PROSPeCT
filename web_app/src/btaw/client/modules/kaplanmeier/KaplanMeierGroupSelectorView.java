package btaw.client.modules.kaplanmeier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.kaplanmeier.KaplanMeierGroupSelectorPresenter.View;

public class KaplanMeierGroupSelectorView extends AView implements View {

	private DialogBox main;
	private VerticalPanel containerPanel;
	private VerticalPanel displayPanel;
	private Button cancel;
	private Button done;
	private ArrayList<Integer> toDelete;
	private HashMap<String, Integer> groups;
	
	public KaplanMeierGroupSelectorView() {
		main = new DialogBox();
		main.getElement().getStyle().setZIndex(3);
		containerPanel = new VerticalPanel();
		containerPanel.setWidth("100%");
		containerPanel.setHeight("100%");
		displayPanel  = new VerticalPanel();
		
		final HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("Cancel");
		done = new Button("Done");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		alignPanel.add(done);
		alignPanel.add(cancel);
		bottom.add(alignPanel);
		
		displayPanel.setSpacing(5);
		containerPanel.setSpacing(5);
		containerPanel.add(displayPanel);
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("K-M Group Delete");
		main.setModal(true);
		main.center();
		
		toDelete = new ArrayList<Integer>();
		groups = new HashMap<String, Integer>();
		populateDeleteList();
	}
	
	public void close() {
		main.hide();
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	
	public Button getDoneButton() {
		return this.done;
	}
	
	public void populateDeleteList() {
		ArrayList<ArrayList<String>> pids = FilterPresenter.get().getKMPids();
		
		int currChar = 65;
		for(int i = 0; i < pids.size(); i++) {
			Button currDelButton = new Button ("Delete \""+FilterPresenter.get().getKMNames().get(i)+"\"");
			groups.put("Delete \""+FilterPresenter.get().getKMNames().get(i)+"\"", new Integer(i));
			currDelButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Integer group = groups.get(((Button)event.getSource()).getText());
					toDelete.add(group);
					((Button)event.getSource()).removeFromParent();
				}
				
			});
			displayPanel.add(currDelButton);
		}
	}
	
	public void doDelete() {
		System.err.println("toDelete: " + toDelete);
		System.err.println("groups: " + groups);
		Comparator comparator = Collections.reverseOrder();
		Collections.sort(toDelete, comparator);
		System.err.println("toDelete: " + toDelete);
		
		for(int i = 0; i < toDelete.size(); i++) {
			FilterPresenter.get().getKMPids().remove(toDelete.get(i).intValue());
			FilterPresenter.get().getKMNames().remove(toDelete.get(i).intValue());
		}
		
		System.err.println("PIDS AFTER DELETE : " + FilterPresenter.get().getKMPids().size());
	}
}
