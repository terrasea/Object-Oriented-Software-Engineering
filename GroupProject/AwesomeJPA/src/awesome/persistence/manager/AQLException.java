package awesome.persistence.manager;

/**
 * 
 * Error thrown when an invalid AQL string is provided to query database.
 *
 */
public class AQLException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public AQLException() {
		super();
	}
	
	/**
	 * Constructor
	 * @param msg The exception message
	 */
	public AQLException(String msg) {
		super(msg);
	}
}
