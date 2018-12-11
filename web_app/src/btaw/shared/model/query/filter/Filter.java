package btaw.shared.model.query.filter;

import java.io.Serializable;

import btaw.shared.model.query.SQLProducer;

/** Top level Filter class to be used in Query.
 * 
 * @author Jon VanAlten
 *
 */
public abstract class Filter implements SQLProducer, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1502033432102054978L;

	public Filter(){
		
	}
	
}