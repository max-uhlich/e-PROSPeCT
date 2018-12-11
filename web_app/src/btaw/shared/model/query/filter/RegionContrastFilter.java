package btaw.shared.model.query.filter;

import btaw.client.modules.table.TablePresenter;

public class RegionContrastFilter extends Filter {

	private static final long serialVersionUID = -8288018313897783036L;

	private String whereClause = "";
	private String fromClause = "";
	private String selectClause = "";
	private String sqlFilter = "";
	
	public void setup() {
		TablePresenter.get().handleRatios(this);
	}
	
	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getFromClause() {
		return fromClause;
	}

	public void setFromClause(String fromClause) {
		this.fromClause = fromClause;
	}

	public String getSelectClause() {
		return selectClause;
	}

	public void setSelectClause(String selectClause) {
		this.selectClause = selectClause;
	}
	
	public void setSqlFilter(String sqlFilter) {
		this.sqlFilter = sqlFilter;
	}
	
	@Override
	public String toSQLString() {
		return this.sqlFilter;
	}

}
