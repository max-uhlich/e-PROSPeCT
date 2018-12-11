package btaw.client.modules.kaplanmeier;

import btaw.client.framework.AView;
import btaw.client.modules.kaplanmeier.KaplanMeierPresenter.View;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KaplanMeierView extends AView implements View {
	private DialogBox main;
	private VerticalPanel containerPanel;
	
	private Button cancel;
	private Button done;
	private Button delete;
	private Button statistic;
	
	Image chart = null;
	
	public KaplanMeierView() {
		main = new DialogBox();
		main.getElement().getStyle().setZIndex(2);
		containerPanel = new VerticalPanel();
		containerPanel.setWidth("100%");
		containerPanel.setHeight("100%");
		
		final HorizontalPanel bottom = new HorizontalPanel();
		cancel = new Button("Cancel");
		done = new Button("Done");
		delete = new Button("Delete Groups");
		statistic = new Button("Statistical Significance");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(delete);
		alignPanel.add(statistic);
		alignPanel.add(done);
		alignPanel.add(cancel);
		
		chart = new Image();
		containerPanel.add(chart);
		containerPanel.add(bottom);
		
		main.add(new ScrollPanel(containerPanel));
		main.setText("Kaplan-Meier Estimator");
		main.setModal(true);
		main.center();
		
	}
	@Override
	public void close() {
		main.hide();
	}
	
	public Button getCancelButton() {
		return this.cancel;
	}
	
	public Button getDoneButton() {
		return this.done;
	}
	
	public Button getDeleteButton() {
		return this.delete;
	}
	
	public void addKMUrl (String url) {
		chart.setUrl(url);
	}
	
	public Button getStatButton() {
		return this.statistic;
	}
}
