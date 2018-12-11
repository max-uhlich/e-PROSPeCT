package btaw.shared.model.query.column;

import java.io.Serializable;

import btaw.client.view.filter.FilterPanel;
import btaw.shared.model.DefinitionData;

public class SavedDefinitionColumn extends Column implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1711368712146301416L;
	private DefinitionData dd;
	
	public SavedDefinitionColumn(){
	}
	
	public SavedDefinitionColumn(DefinitionData dd){
		this.dd = dd;
	}
	
	public DefinitionData getDefinitionData(){
		return this.dd;
	}

	@Override
	public String toSQLString() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return dd.getDefName();
	}

	@Override
	public FilterPanel getFilterPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSQLName() {
		// TODO Auto-generated method stub
		return "";
	}
	
}