package btaw.shared.model.query.filter;

public class JaccardImageFilter extends ScoredImageFilter {
	private static final long serialVersionUID = 3088704204828533190L;

	int session_id = -1;
	String table_alias;
	int query_id = -1;
	String jaccardFilter;
	
	public int getQuery_id() {
		return query_id;
	}
	public void setQuery_id(int query_id) {
		this.query_id = query_id;
	}

	public String getJaccardFilter() {
		return jaccardFilter;
	}
	public void setJaccardFilter(String jaccardFilter) {
		this.jaccardFilter = jaccardFilter;
	}
	public void setup() {
		//TablePresenter.get().handleJaccardDrawn(this);
	}
	public String getFromClause() {
		return new String("jaccard_drawn_score("+session_id+", "+regionId+", "+query_id+") AS " + table_alias);
	}
	
	public String getWhereClause() {
		return new String("btaw.btap_study.study_id = " + table_alias + ".study_id");
	}

	public int getSession_id() {
		return session_id;
	}

	public void setSession_id(int session_id) {
		this.session_id = session_id;
	}

	public String getTable_alias() {
		return table_alias;
	}

	public void setTable_alias(String table_alias) {
		this.table_alias = new String(table_alias);
	}

	@Override
	public String toSQLString() {
		return jaccardFilter;
	}
	
	
}