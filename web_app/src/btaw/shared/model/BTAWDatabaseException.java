package btaw.shared.model;

public class BTAWDatabaseException extends Exception {

	public BTAWDatabaseException(){
	}
	public BTAWDatabaseException(String string, Exception ex) {
		super(string, ex);
	}
	
	public BTAWDatabaseException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2899048995656626248L;
	
}
