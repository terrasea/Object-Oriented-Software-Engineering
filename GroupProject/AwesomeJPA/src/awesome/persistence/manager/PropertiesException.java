package awesome.persistence.manager;

/**
 * 
 * Exception thrown when an invalid properties file is provided.
 * Properties file is considered valid when it contains the keys
 * "user", "password" and "url".
 *
 */
public class PropertiesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public PropertiesException() {
		super();
	}
	
	/**
	 * 
	 * @param msg The exception message
	 */
	public PropertiesException(String msg) {
		super(msg);
	}
}
