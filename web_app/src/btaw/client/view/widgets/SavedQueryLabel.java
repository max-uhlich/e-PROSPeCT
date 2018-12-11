package btaw.client.view.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;

public class SavedQueryLabel extends HTML {
	private Integer sqID;
	
	public SavedQueryLabel(String name, Integer savedQueryID) {
		super(name);
		this.sqID = savedQueryID;
		this.getElement().getStyle().setPaddingRight(3, Unit.PX);
		this.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		this.getElement().getStyle().setMarginRight(0, Unit.PX);
		this.getElement().getStyle().setMarginLeft(0, Unit.PX);
	}
	public Integer getSavedQueryId() {
		return this.sqID;
	}
}
