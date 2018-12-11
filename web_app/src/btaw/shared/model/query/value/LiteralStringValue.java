package btaw.shared.model.query.value;

public class LiteralStringValue extends StringValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2857621494926672793L;
	private String value; 
	public LiteralStringValue(){
		
	}
	public LiteralStringValue(String value) {
		this.value = new String(value);
	}

	@Override
	public String toSQLString() {
		return "'"+value+"'";
	}
}