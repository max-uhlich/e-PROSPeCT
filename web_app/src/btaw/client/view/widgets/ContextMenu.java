package btaw.client.view.widgets;

import btaw.client.event.HeaderMenuSelectionEvent;
import btaw.client.event.controllers.HeaderMenuSelectionController;
import btaw.shared.model.DefinitionData;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class ContextMenu extends PopupPanel{
	private final HeaderMenuSelectionController controller;
	private Command mc;
	public ContextMenu(HeaderMenuSelectionController controller, MagicTextColumn column, ClickableHeader header)
	{
		super(false);
		this.controller=controller;
		MenuBar test = new MenuBar(true);
		MenuBar subTest = new WindowSensitiveMenuBar(true);
		test.addItem(new MenuItem("Remove",new MyCommand("remove",column, controller, header)));
		test.addItem(new MenuItem("Move Column Left", new MyCommand("left", column, controller, header)));
		test.addItem(new MenuItem("Move Column Right", new MyCommand("right", column, controller, header)));
		test.addItem(new MenuItem("Filter", new MyCommand("constrain", column, controller, header)));
		test.addItem(new MenuItem("Ascending", new MyCommand("ASC", column, controller, header)));
		test.addItem(new MenuItem("Descending", new MyCommand("DESC", column, controller, header)));
		test.addItem(new MenuItem("Average", new MyCommand("AVG", column, controller, header)));
		test.addItem(new MenuItem("Count", new MyCommand("COUNT", column, controller, header)));
		test.addItem(new MenuItem("Max", new MyCommand("MAX", column, controller, header)));
		test.addItem(new MenuItem("Min", new MyCommand("MIN", column, controller, header)));
		test.addItem(new MenuItem("Sum", new MyCommand("SUM", column, controller, header)));
		test.addItem(new MenuItem("Count Unique", new MyCommand("UNIQ", column, controller, header)));
		test.addItem(new MenuItem("Histogram", new MyCommand("HIST", column, controller, header)));
		test.addItem(new MenuItem("Find Duplicates", new MyCommand("DUPE", column, controller, header)));
		test.setFocusOnHoverEnabled(true);
		setWidget(test);
	}
	
	public ContextMenu(HeaderMenuSelectionController controller, DefinitionData dd)
	{
		super(true);
		this.controller = controller;
		MenuBar test = new MenuBar(true);
		MenuBar subTest = new WindowSensitiveMenuBar(true);
		mc = new MyCommand("save_def",null,this.controller,null);
		//Window.alert("dd: " + dd.getDefName());
		((MyCommand)mc).setDefinitionData(dd);
		test.addItem(new MenuItem("Save this Definition",mc));
		test.setFocusOnHoverEnabled(true);
		setWidget(test);
	}
	
	public ContextMenu(HeaderMenuSelectionController controller, String command_code, String text, JavaScriptObject chart)
	{
		super(true);
		this.controller = controller;
		MenuBar test = new MenuBar(true);
		//MenuBar subTest = new WindowSensitiveMenuBar(true);
		mc = new Export_Module_Command(command_code,this.controller);
		((Export_Module_Command)mc).setChart(chart);
		test.addItem(new MenuItem(text,mc));
		test.setFocusOnHoverEnabled(true);
		setWidget(test);
	}
	
	//public void setDefinitionData(DefinitionData dd){
	//	mc.setDefinitionData(dd);
	//}
}

class MyCommand implements Command{
	final String itemName;
	final MagicTextColumn column;
	final HeaderMenuSelectionController controller;
	final ClickableHeader header;
	private DefinitionData dd;
	public MyCommand(String itemName, MagicTextColumn column, HeaderMenuSelectionController controller, ClickableHeader header){
		this.itemName=itemName;
		this.controller=controller;
		this.column=column;
		this.header = header;
	}
	
	public void setDefinitionData(DefinitionData dd){
		this.dd = dd;
	}
	
	@Override
	public void execute() {
		if(this.column!=null){
			controller.fireEvent(new HeaderMenuSelectionEvent(itemName,this.column, this.header));
		}
		else{
			controller.fireEvent(new HeaderMenuSelectionEvent(itemName,this.dd));
		}
	}
	
}

class Export_Module_Command implements Command{
	final String itemName;
	final HeaderMenuSelectionController controller;
	private JavaScriptObject chart;
	public Export_Module_Command(String itemName, HeaderMenuSelectionController controller){
		this.itemName=itemName;
		this.controller=controller;
	}
	
	public void setChart(JavaScriptObject chart){
		this.chart = chart;
	}

	@Override
	public void execute() {
		//Window.alert("EXECYTE EXPYRT");
		controller.fireEvent(new HeaderMenuSelectionEvent(itemName,this.chart));
	}
	
}
