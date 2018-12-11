package btaw.shared.model.query.filter;

/** A convenience class to be used in a Query which intends to return every tuple of the columns
 * selected.
 * 
 * @author Jon VanAlten
 *
 */
public class NoFilter extends Filter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1730099116728549766L;

	public NoFilter() {
		// Just overriding as private to force singleton use.
	}
	
	private static class SingleNoFilter {
		public static final NoFilter INSTANCE = new NoFilter();
	}
	
	public static NoFilter getInstance() {
		return SingleNoFilter.INSTANCE;
	}
	
	@Override
	public String toSQLString() {
		return new String("");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof NoFilter) {
			return true;
		} else {
			return false;
		}
	}
}