package btaw.shared.util;

import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.NestedFilter;

public class F_UtilE extends NestedFilter {

	/**
	 * 
	 */
	private int bcount;
	private boolean error=false;
	private String sql="";
	public F_UtilE(){
		
	}
	private static final long serialVersionUID = -1080688684479220987L;

	@Override
	public String toSQLString() {
		return sql;
	}
	public void addFilter(Filter f){
		sql+=f.toSQLString();
	}
	public void lbrack(){
		sql+="(";
		bcount++;
	}
	public void rbrack(){
		sql+=")";
		bcount--;
		if(bcount<0)error=true;
	}
	public void And(){
		sql+=" AND ";
	}
	public void Or(){
		sql+=" OR ";
	}
	public boolean error(){
		return error||bcount!=0;
	}
}
