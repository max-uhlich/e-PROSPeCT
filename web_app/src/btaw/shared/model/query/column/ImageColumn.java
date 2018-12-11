package btaw.shared.model.query.column;

import btaw.client.modules.image.ImagePresenter;
import btaw.client.view.filter.FilterPanel;
import btaw.client.view.filter.ImageFilterPanel;

import java.io.Serializable;

public class ImageColumn extends Column {

	private static final long serialVersionUID = -310899220170933119L;
	private String queryType;
	
	public ImageColumn(String queryType){
		this.queryType = queryType;
	}
	@Override
	public String toSQLString() {
		if (queryType == ImagePresenter.DISTANCE) {
			
		}
		return "btaw.person.pid";
	}

	@Override
	public String getName() {
		return this.queryType;
	}

	@Override
	public FilterPanel getFilterPanel() {
		return new ImageFilterPanel(this, this.queryType);
	}
	@Override
	public String getSQLName() {
		return "";
	}

}
