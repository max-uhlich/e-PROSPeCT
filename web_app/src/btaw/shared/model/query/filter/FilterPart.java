package btaw.shared.model.query.filter;

import java.io.Serializable;

import btaw.shared.model.query.SQLProducer;
import btaw.shared.model.query.filter.NestedFilter.Op;
/**
 * 
 * Operator-Filter pair in a NestedFilter
 *
 */
public class FilterPart implements SQLProducer,Serializable {
		private static final long serialVersionUID = -4207846488705475706L;
		private Op oper;
		private Filter filter;
		
		public FilterPart(){}
		public FilterPart(Op oper, Filter filter) {
			this.setOper(oper);
			this.setFilter(filter);
		}

		@Override
		public String toSQLString() {
			String ret = new String();
			if (getOper() != null) {
				if (getOper() == Op.AND) {
					ret = ret.concat(" AND");
				} else if (getOper() == Op.OR) {
					ret = ret.concat(" OR");
				}
			}
			ret = ret.concat(" " + getFilter().toSQLString());
			return ret;
		}
		public void setOper(Op oper) {
			this.oper = oper;
		}
		public Op getOper() {
			return oper;
		}
		public void setFilter(Filter filter) {
			this.filter = filter;
		}
		public Filter getFilter() {
			return filter;
		}
	}