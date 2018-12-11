package btaw.shared.model.query.value;

public class LiteralDateValue extends DateValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5569251415009521627L;
	private String value;
	public LiteralDateValue(){
		
	}
	public LiteralDateValue(String value) {
		this.value = value;
	}

	@Override
	public String toSQLString() {
		if (value != null) {
			return "DATE '"+value+"'";
		} else {
			return "LiteralDateValueNullError";
		}
	}
}