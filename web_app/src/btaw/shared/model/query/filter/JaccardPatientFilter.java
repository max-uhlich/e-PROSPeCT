package btaw.shared.model.query.filter;

public class JaccardPatientFilter extends ScoredImageFilter {
	private static final long serialVersionUID = 6944927995088848096L;
	
	int roi_id;
	
	public int getRoi_id() {
		return roi_id;
	}

	public void setRoi_id(int roi_id) {
		this.roi_id = roi_id;
	}

	int sid;
	String jaccardFilter;
	String table_alias;

	public String getTable_alias() {
		return table_alias;
	}
	
	public void setup() {
		//TablePresenter.get().handleJaccardPatient(this);
	}
	
	public void setTable_alias(String table_alias) {
		this.table_alias = new String(table_alias);
	}
	
	public String getFromClause() {
			return new String("jaccard_patient_score("+sid+", "+regionId+") AS " + table_alias);
	}

	public String getWhereClause() {
		return new String("btaw.btap_study.study_id = " + table_alias + ".study_id");
	}
	@Override
	public String toSQLString() {
		return jaccardFilter;
	}
	
	public void setSid(int sid) {
		this.sid = sid;
	}
	
	public void setJaccardString(String sqlSnip) {
		this.jaccardFilter = sqlSnip;
	}
	
	public int getSid() {
		return this.sid;
	}
}