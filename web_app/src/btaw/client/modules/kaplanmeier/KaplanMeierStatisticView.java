package btaw.client.modules.kaplanmeier;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.kaplanmeier.KaplanMeierStatisticPresenter.View;

public class KaplanMeierStatisticView extends AView implements View {

	private DialogBox main;
	private VerticalPanel containerPanel;
	private VerticalPanel displayPanel;
	private Button cancel;
	private Button done;
	private Button calculate;
	private ListBox group1;
	private ListBox group2;
	private Label pVal;
	
	public KaplanMeierStatisticView() {
		main = new DialogBox();
		main.getElement().getStyle().setZIndex(3);
		containerPanel = new VerticalPanel();
		containerPanel.setWidth("100%");
		containerPanel.setHeight("100%");
		displayPanel  = new VerticalPanel();
		
		final HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("Cancel");
		done = new Button("Done");
		calculate = new Button("Calculate");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		alignPanel.add(calculate);
		alignPanel.add(done);
		alignPanel.add(cancel);
		bottom.add(alignPanel);
		
		displayPanel.setSpacing(5);
		HorizontalPanel group1HP = new HorizontalPanel();
		HorizontalPanel group2HP = new HorizontalPanel();
		HorizontalPanel statHP = new HorizontalPanel();
		
		Label group1L = new Label("Choose Group One: ");
		group1L.setStyleName("center-Label");
		group1 = new ListBox();
		group1HP.add(group1L);
		group1HP.add(group1);
		
		Label group2L = new Label("Choose Group Two: ");
		group2L.setStyleName("center-Label");
		group2 = new ListBox();
		group2HP.add(group2L);
		group2HP.add(group2);
		
		Label statL = new Label("P-Value: ");
		statL.setStyleName("center-Label");
		pVal = new Label("-");
		pVal.setStyleName("center-Label");
		statHP.add(statL);
		statHP.add(pVal);
		
		displayPanel.add(group1HP);
		displayPanel.add(group2HP);
		displayPanel.add(statHP);
		
		containerPanel.setSpacing(5);
		containerPanel.add(displayPanel);
		containerPanel.add(bottom);
		
		main.add(containerPanel);
		main.setText("K-M Logrank Statistic");
		main.setModal(true);
		main.center();
		
		System.err.println("KM NAMES SIZE AFTER : " + FilterPresenter.get().getKMNames().size());
		System.err.println("KM PIDS SIZE AFTER : " + FilterPresenter.get().getKMPids().size());
		
		if(FilterPresenter.get().getKMNames().isEmpty()) {
			group1.addItem("Error - No Groups Found.");
			group2.addItem("Error - No Groups Found.");
		} else {

			for (int i = 0; i < FilterPresenter.get().getKMNames().size(); i++) {
				group1.addItem(FilterPresenter.get().getKMNames().get(i));
				group2.addItem(FilterPresenter.get().getKMNames().get(i));
			}
			
			if (FilterPresenter.get().getKMNames().size() >= 2) {
				group1.setItemSelected(0, true);
				group2.setItemSelected(1, true);
			}
		}
		
		DOM.setElementProperty(pVal.getElement(), "title", "Probability of obtaining a test statistic at least as extreme as the one that was actually observed, assuming that the null hypothesis is true.");
		DOM.setElementProperty(statL.getElement(), "title", "Probability of obtaining a test statistic at least as extreme as the one that was actually observed, assuming that the null hypothesis is true.");
		
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
	
	public Button getCalculateButton() {
		return this.calculate;
	}
	
	public void setPVal(String p) {
		pVal.setText(p);
	}
	public void setPValCalculating() {
		pVal.setText("Calculating...");
	}
	public ListBox getGroup1() {
		return group1;
	}
	public ListBox getGroup2() {
		return group2;
	}
}
