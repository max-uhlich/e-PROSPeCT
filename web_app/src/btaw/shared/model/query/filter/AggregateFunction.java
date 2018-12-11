package btaw.shared.model.query.filter;

public class AggregateFunction extends TableColumnFilter {

	private static final long serialVersionUID = -2258984362968667236L;

	private String function;
	
	public AggregateFunction () {
	}
	
	public String getFunction() {
		return function;
	}

	public void setFunction(String funcArg) {
		this.function = funcArg;
	}
	
	@Override
	public String toSQLString() {
		// TODO Auto-generated method stub
		return null;
	}

}
