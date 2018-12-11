package btaw.shared.model.query.value;

public class LiteralBooleanValue extends BooleanValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9076725169342602033L;
	public static LiteralBooleanValue TRUE = new LiteralBooleanValue(true);
	public static LiteralBooleanValue FALSE = new LiteralBooleanValue(false);
	
	private boolean value; 
	
	public LiteralBooleanValue(){
		
	}
	public LiteralBooleanValue(boolean value) {
		this.value = value;
	}

	@Override
	public String toSQLString() {
		if (value) {
			return "TRUE";
		} else {
			return "FALSE";
		}
	}
}