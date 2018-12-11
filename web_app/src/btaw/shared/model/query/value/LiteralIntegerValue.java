package btaw.shared.model.query.value;

public class LiteralIntegerValue extends IntegerValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4202323483357964971L;
	private long value; 
	public LiteralIntegerValue(){
		
	}
	public LiteralIntegerValue(long value) {
		this.value = value;
	}

	@Override
	public String toSQLString() {
		return Long.toString(value);
	}
}