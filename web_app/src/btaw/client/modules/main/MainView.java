package btaw.client.modules.main;

import btaw.client.framework.AView;
import btaw.client.modules.category.CategoryView;
import btaw.client.modules.filter.FilterView;
import btaw.client.modules.table.TableView;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.thirdparty.javascript.jscomp.CssRenamingMap.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MainView extends AView implements MainPresenter.View {
	private final SplitLayoutPanel mainPanel;
	private final PickupDragController dragController;
	private final FilterView filterView;
	private final TableView tableView;
	private final CategoryView categoryView;
	private PopupPanel workingPopup;
	private PopupPanel data_agreement_popup;
	private Boolean finished;
	private Integer waitingDotCnt;
	private Label popupL;
	private Button cancelQuery;
	private Button I_Agree;
	private VerticalPanel popupInnerPanel;
	
	public MainView(){
		
		data_agreement_popup = new PopupPanel(false);
		data_agreement_popup.setGlassEnabled(true);
		//data_agreement_popup.setSize("40%", "40%");
		I_Agree = new Button("I Accept");
		
		Label data_popup_label = new Label("I certify that the data visualized in this platform will remain secure and confidential. I will not make any attempts to re-identify participants in the Alberta Prostate Cancer Registry. I understand that this platform is to be used to easily access and visualize the data in Alberta Prostate Cancer Registry and to generate research questions and hypothesis. For data analysis, reporting and publications, I will apply to the APCaRI Scientific and Data Quality Committee for access to the raw data.");
		data_popup_label.getElement().getStyle().setColor("rgba(255, 0, 0, 1)");
		data_popup_label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		data_popup_label.getElement().getStyle().setBackgroundColor("rgba(255, 255, 0, 1)");
		data_popup_label.setSize("25%", "25%");
		
		VerticalPanel dataPopupInnerPanel = new VerticalPanel();
		dataPopupInnerPanel.setSpacing(5);
		dataPopupInnerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		//dataPopupInnerPanel.setSize("25%", "25%");
		
		HorizontalPanel dataPopuptextPanel = new HorizontalPanel();
		dataPopuptextPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		//dataPopuptextPanel.setSize("30%", "30%");
		dataPopuptextPanel.add(data_popup_label);
		
		HorizontalPanel dataPopuplowerPanel = new HorizontalPanel();
		dataPopuplowerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		//dataPopuplowerPanel.setSize("30%", "30%");
		dataPopuplowerPanel.add(I_Agree);
		
		dataPopupInnerPanel.add(dataPopuptextPanel);
		dataPopupInnerPanel.add(dataPopuplowerPanel);
		
		data_agreement_popup.add(dataPopupInnerPanel);
		
		workingPopup = new PopupPanel(false);
		finished = false;
		popupL = new Label("Working");
		
		cancelQuery = new Button("Cancel Query");
		
		popupInnerPanel = new VerticalPanel();
		popupInnerPanel.setSpacing(5);
		popupInnerPanel.add(popupL);
		popupInnerPanel.add(cancelQuery);
		
		workingPopup.add(popupInnerPanel);
		workingPopup.setGlassEnabled(true);
		waitingDotCnt = 0;
		
		mainPanel = new SplitLayoutPanel(4);

		dragController = new PickupDragController(RootPanel.get(), false);
		
		initWidget(mainPanel);
		categoryView = new CategoryView(dragController);
		categoryView.setWidth("100%");
		categoryView.setHeight("100%");
		categoryView.getElement().setId("catView");
		
		filterView = new FilterView(dragController);
		filterView.getElement().setId("filterView");
		
		tableView = new TableView(dragController);
		tableView.getElement().setId("tableView");
		
		mainPanel.addWest(categoryView, 290);
		mainPanel.addNorth(filterView,300);
		mainPanel.add(tableView);
		mainPanel.getElement().setId("mainPanel");
		
	}

	@Override
	public SplitLayoutPanel getMainPanel() {
		return mainPanel;
	}
	
	@Override
	public FilterView getFilterView() {
		return filterView;
	}

	@Override
	public TableView getTableView() {
		return tableView;
	}
	
	@Override
	public CategoryView getCategoryView() {
		return categoryView;
	}
	
	public void maximizeTableView(boolean val){
		mainPanel.setWidgetHidden(categoryView, val);
		mainPanel.setWidgetHidden(filterView, val);
	}

	public void data_agreement() {
		data_agreement_popup.show();
		data_agreement_popup.center();
	}
	
	public void working(final boolean cancellable) {
		if(!cancellable) {
			popupInnerPanel.remove(cancelQuery);
		}
		workingPopup.show();
		workingPopup.center();
		Timer t = new Timer() {
			public void run() {
				if(!finished) {
					MainView.this.waitingDotCnt = (MainView.this.waitingDotCnt%3) + 1;
					String tmp = "Working";
					for(int i = 0; i < MainView.this.waitingDotCnt; i++) {
						tmp += ".";
					}
					popupL.setText(tmp);
					popupInnerPanel.remove(popupL);
					if(cancellable)
						popupInnerPanel.remove(cancelQuery);
					popupInnerPanel.add(popupL);
					if(cancellable)
						popupInnerPanel.add(cancelQuery);
					this.schedule(1000);
				} else {
					finished = false;
				}
			}
		};
		t.schedule(1000);
	}
	
	public void datafinished() {
		data_agreement_popup.hide();
	}
	
	public void finished() {
		finished = true;
		workingPopup.hide();
	}
	
	public Button getCancelButton() {
		return this.cancelQuery;
	}
	
	public Button getIAgreeButton() {
		return this.I_Agree;
	}
}
