package btaw.shared.model.query.value;

public class LiteralFloatValue extends FloatValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1032058473312588814L;
	private double value; 
	public LiteralFloatValue(){
		
	}
	public LiteralFloatValue(double value) {
		this.value = value;
	}

	@Override
	public String toSQLString() {
		return Double.toString(value);
	}
}