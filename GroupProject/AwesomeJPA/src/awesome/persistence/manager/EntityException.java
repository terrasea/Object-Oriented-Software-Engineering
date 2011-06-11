package awesome.persistence.manager;

/**
 * 
 * Exception type thrown when the Manager encounters an error when processing 
 * an entity.
 *
 */
public class EntityException extends Exception {

	/**
	 * Constructor
	 * @param msg
	 */
	public EntityException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
