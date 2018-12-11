package btaw.client.modules.image;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.shared.model.query.filter.Filter;

import com.google.gwt.user.client.ui.HasWidgets;

public abstract class ImagePresenter extends Presenter {
	public interface View extends IView{
		ArrayList<Circle> getPoints();
	}

	public static final String RATIOS = "Ratios";
	public static final String SIMILARITY = "Venn Diagram";
	public static final String DISTANCE = "Center of Mass";

	private View ui;
	
	public ImagePresenter(Presenter parent, View view) {
		super(parent);
		this.ui = view;
	}

	@Override
	protected void bindAll() {

	}

	@Override
	public IView getDisplay() {
		return ui;
	}

	@Override
	protected void activated(HasWidgets container) {

	}
	
	public abstract Filter getFilter();

	public abstract boolean isType(String val);
	
	public abstract void setSegTypes(LinkedHashMap<String, Integer> segTypes);
}
