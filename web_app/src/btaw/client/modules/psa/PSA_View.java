package btaw.client.modules.psa;


import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import btaw.client.framework.AView;
import btaw.client.modules.main.MainPresenter;
import btaw.client.modules.psa.PSA_Presenter.View;
import btaw.shared.model.PSA_Graph;
import btaw.shared.model.Study;

public class PSA_View extends AView implements View {
	
	private DialogBox main;
	private int pid = -1;
	private Button close;
	private Element div;
	
	public PSA_View() {
		
		main = new DialogBox();
		
		HorizontalPanel HP = new HorizontalPanel();

		div = DOM.createDiv();
		HP.getElement().appendChild(div);

		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel bottom = new HorizontalPanel();
		close = new Button("Close");
		bottom.setWidth("100%");
		bottom.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		HorizontalPanel alignPanel = new HorizontalPanel();
		alignPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		bottom.add(alignPanel);
		alignPanel.add(close);

		vp.add(bottom);

		VerticalPanel mainVP = new VerticalPanel();
		mainVP.add(HP);
		mainVP.add(vp);

		main.add(mainVP);
		
	}

	@Override
	public void populateStudiesAndShow(JsArrayString psa_vals, JsArrayString dates, JsArrayInteger size, String pid) {

		this.pid = Integer.parseInt(pid);
		main.setText("Patient " + this.pid + " PSA Trajectory");
		main.setModal(true);
		main.center();
		main.getElement().getStyle().setProperty("width", "auto");
		main.getElement().getStyle().setZIndex(2);

		createPSAGraph(div, psa_vals, dates, size);

		MainPresenter.get().finished();
	}
	
	@Override
	public Button getCloseButton() {
		return close;
	}
	
	@Override
	public void close() {
		main.hide();
	}
	
	private native void createPSAGraph(Element div, JsArrayString jsData, JsArrayString jsDates, JsArrayInteger jsSize)/*-{
		$wnd.d3_psa_graph(div, jsData, jsDates, jsSize);
	}-*/;
	
}
