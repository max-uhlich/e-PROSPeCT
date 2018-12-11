package btaw.client.modules.tab;

import btaw.client.event.HeaderMenuSelectionEvent;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.client.event.handlers.HeaderMenuSelectionHandler;
import btaw.client.view.widgets.ContextMenu;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class JS_Module extends HorizontalPanel implements ContextMenuHandler, HeaderMenuSelectionHandler{
	
	private final HeaderMenuSelectionController controller;
	private ContextMenu contextMenu;

	private Element div;
	private String name;
	private JavaScriptObject chart;
	
	public JS_Module(HeaderMenuSelectionController controller, String name){
		super();
		
		this.div = DOM.createDiv();
		this.getElement().appendChild(this.div);
		this.name = name;
		this.controller = controller;
		this.controller.addHeaderMenuSelectionHandler(this);

		//this.contextMenu = new ContextMenu(controller,"export_module","Export this Module",this.chart);
	    //addDomHandler(this, ContextMenuEvent.getType());
	}

	public Element getDiv(){
		return this.div;
	}
	
	public void setChart(JavaScriptObject chart){
		this.chart = chart;
		
		this.contextMenu = new ContextMenu(controller,"export_module","Export this Module",this.chart);
	    addDomHandler(this, ContextMenuEvent.getType());
	}
	
	public void setChart(JavaScriptObject chart, boolean table){
		this.chart = chart;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
		//Window.alert("YOU JUST DID A CONTEXT on: " + this.name);
		event.preventDefault();
		event.stopPropagation();

		this.contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		this.contextMenu.show();
	}

	@Override
	public void OnHeaderMenuSelectionEvent(HeaderMenuSelectionEvent e) {
		// TODO Auto-generated method stub
		this.contextMenu.hide();
	}

}
