package btaw.shared.model.query.filter;

import btaw.client.modules.table.TablePresenter;

public class ImageQueryFilter extends ScoredImageFilter {
	private static final long serialVersionUID = 3088704204828533190L;

	String filterString = null;
	String tableAlias = null;
	String function = null;
	int sessionId = -1;
	int queryId = -1;
	int regionId = -1;
	int studyId = -1;
	boolean drawn;
	
	public boolean isDrawn() {
		return drawn;
	}
	public void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}
	public void setFunction(String func) {
		this.function = func;
	}
	public int getStudyId() {
		return this.studyId;
	}
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
	public int getRegionId() {
		return regionId;
	}
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	public int getQueryId() {
		return queryId;
	}
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}

	public String getFilterString() {
		return filterString;
	}
	public void setFilterString(String filter) {
		this.filterString = filter;
	}
	public void setup() {
		TablePresenter.get().handleSimilarity(this);
	}
	public String getFromClause() {
		if(drawn) {
			return new String(function + "("+sessionId+", "+queryId+", "+regionId+") AS " + tableAlias);
		} else {
			return new String(function + "("+studyId+", "+regionId+") AS " + tableAlias);
		}
	}
	
	public String getWhereClause() {
		return new String("btaw.btap_study.study_id = " + tableAlias + ".study_id");
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(String tableAlias) {
		this.tableAlias = new String(tableAlias);
	}

	@Override
	public String toSQLString() {
		return filterString;
	}
	
	
}