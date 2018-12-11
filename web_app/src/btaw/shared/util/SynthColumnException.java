package btaw.shared.util;


public class SynthColumnException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8967416994042865686L;
	private String message;
	public SynthColumnException(){
		
	}
	public SynthColumnException(String message){
		this.message=message;
	}
	public String getMessage(){
		return this.message;
	}
}
