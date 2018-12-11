package btaw.client.view.widgets;

import btaw.shared.model.query.column.Column;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class QueryDeleteButton extends PushButton {

	private Column colRef;
	
	public QueryDeleteButton(Image im, Column c){
		super(im);
		this.getElement().getStyle().setFontSize(8, Unit.PT);
		this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		this.setStyleName("gwt-TabButton");
		this.setTitle("Delete");
		this.colRef = c;
	}
	
	public Column getRef(){
		return this.colRef;
	}
	
}
