package btaw.shared.model.query.filter;

public class OverlapImageFilter extends ScoredImageFilter {
	private static final long serialVersionUID = -6312354714377024654L;

	int session_id = -1;
	String table_alias = new String("olp_1");
	int query_id = -1;
	String overlapFilter;
	
	@Override
	public String toSQLString() {
		return overlapFilter;
	}

	public void setQuery_id(int query_id) {
		this.query_id = query_id;
	}

	public void setup() {
		//TablePresenter.get().handleOverlap(this);
	}
	
	public void setSession_id(Integer session_id) {
		this.session_id = session_id;
	}

	public void setOverlapFilter(String string) {
		this.overlapFilter = string;
	}
	
	public String getOverlapFilter() {
		return overlapFilter;
	}
	
	public int getQueryId() {
		return query_id;
	}
	
	public int getSessionId() {
		return session_id;
	}
	
	public String getTable_alias() {
		return table_alias;
	}
	
	public void setTable_alias(String table_alias) {
		this.table_alias = new String(table_alias);
	}
	
	public String getFromClause() {
		return new String("overlap("+session_id+", "+regionId+", "+query_id+") AS " + table_alias);
	}
	
	public String getWhereClause() {
		return new String("btaw.btap_study.study_id = " + table_alias + ".study_id");
	}
}