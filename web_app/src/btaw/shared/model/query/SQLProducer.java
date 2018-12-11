package btaw.shared.model.query;

/** All implementors of this interface shall be elements of SQL, and produce valid SQL code
 * snippets.
 * 
 * @author Jon VanAlten
 *
 */
public interface SQLProducer {
	
	/** Return a snippet of valid SQL.
	 * 
	 * @return A snippet of valid SQL.
	 */
	public String toSQLString();
	
}