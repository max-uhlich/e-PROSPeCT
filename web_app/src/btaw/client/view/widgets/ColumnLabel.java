package btaw.client.view.widgets;

import btaw.shared.model.query.column.Column;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;


public class ColumnLabel extends Label {
	Column c;
	public static String defaultColor;
	
	public ColumnLabel(Column c){
		super(c.getName());
		this.c=c;
		this.getElement().getStyle().setPaddingRight(3, Unit.PX);
		this.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		this.getElement().getStyle().setMarginRight(0, Unit.PX);
		this.getElement().getStyle().setMarginLeft(0, Unit.PX);
		if (defaultColor != null)
			defaultColor = this.getElement().getStyle().getBackgroundColor();
	}
	public Column getColumn(){
		return this.c;
	}
}
