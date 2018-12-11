package btaw.client.view.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;

public class SavedFeatureLabel extends HTML {
	private String sfID;
	
	public SavedFeatureLabel(String name, String savedFeatureID) {
		super(name);
		this.sfID = savedFeatureID;
		this.getElement().getStyle().setPaddingRight(3, Unit.PX);
		this.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		this.getElement().getStyle().setMarginRight(0, Unit.PX);
		this.getElement().getStyle().setMarginLeft(0, Unit.PX);
	}
	public String getSavedFeatureId() {
		return this.sfID;
	}
}
