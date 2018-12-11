package btaw.client.view.panels;

import java.util.logging.Handler;

import btaw.client.event.HeaderMenuSelectionEvent;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.event.handlers.HeaderMenuSelectionHandler;
import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.view.widgets.ContextMenu;
import btaw.client.view.widgets.WindowSensitiveMenuBar;
import btaw.shared.model.DefinitionData;

import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DefinitionPanel extends HorizontalPanel implements ClickHandler, ContextMenuHandler, HeaderMenuSelectionHandler {

	private final HeaderMenuSelectionController controller;
	private Presenter presenter;
	private HorizontalPanel inner;
	private PushButton delete;
	private Label definitionName;
	private Label definition;
	private DefinitionData data;
	private CheckBox checked;
	
	private ContextMenu contextMenu;
	
	public DefinitionPanel(Presenter p, HeaderMenuSelectionController controller, String defName, String def, DefinitionData data) {
		this.controller = controller;
		this.controller.addHeaderMenuSelectionHandler(this);
		this.presenter = p;
		this.data = data;
		this.buildUI(defName, def);
		
	}
	
	public void buildUI(String defName, String def) {
		inner = new HorizontalPanel();
		inner.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.delete = new PushButton(new Image("images/delete.png"));
		delete.getElement().setClassName("upDownButton");
		delete.addClickHandler(this);
		
		this.setWidth("100%");
		this.definitionName = new Label(defName);
		this.definitionName.setWidth("100%");
		this.definitionName.addClickHandler(this);
		DOM.setStyleAttribute(this.definitionName.getElement(), "cursor", "pointer");
		DOM.setStyleAttribute(this.definitionName.getElement(), "color", "#0000FF");
		this.definitionName.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		this.definition = new Label(def);
		this.definition.setWidth("100%");
		
		inner.setSpacing(5);
		this.checked = new CheckBox();
		inner.add(checked);
		inner.add(definitionName);
		inner.add(new Label(":"));
		inner.add(definition);
		
		delete.addClickHandler(this);
		
		HorizontalPanel rightAlignmentWrapper = new HorizontalPanel();
		rightAlignmentWrapper.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		rightAlignmentWrapper.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		rightAlignmentWrapper.setWidth("100%");
		rightAlignmentWrapper.add(delete);
		
		this.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.add(inner);
		this.add(rightAlignmentWrapper);
		
		this.contextMenu = new ContextMenu(controller,data);
		//Window.alert("returned from context menu constructor");
		//Window.alert("data.getDefName: " + data.getDefName());
	    addDomHandler(this, ContextMenuEvent.getType());
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == delete)
		{
			FilterPresenter.get().removeDefinition(this.definitionName.getText());
		}
		if (event.getSource() == definitionName)
		{
			//Window.alert("YOU JUST CLICKED THE NAME");
			FilterPresenter.get().setFilters(data.getRebuildFilters(), this.definitionName.getText());
		}
	}
	
	public void onContextMenu(ContextMenuEvent event) {
		 // stop the browser from opening the context menu
		//Window.alert("YOU JUST DID A CONTEXT");
		 event.preventDefault();
		 event.stopPropagation();

		 this.contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		 this.contextMenu.show();
	}
	
	@Override
	public void OnHeaderMenuSelectionEvent(HeaderMenuSelectionEvent e) {
		this.contextMenu.hide();
	}
	
	public String getName() {
		return this.definitionName.getText();
	}

	//public void setData(DefinitionData data) {
	//	this.data = data;
	//	this.contextMenu.setDefinitionData(this.data);
	//}

	public DefinitionData getData() {
		return data;
	}
	
	public boolean isChecked() {
		return this.checked.getValue();
	}
	
	public void setChecked() {
		this.checked.setValue(true);
	}
	
	public void setUnchecked() {
		this.checked.setValue(false);
	}
}
