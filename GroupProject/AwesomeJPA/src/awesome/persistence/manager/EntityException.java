package awesome.persistence.manager;

public class EntityException extends Exception{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1762177139258093372L;

	/**
	 * Constructor
	 */
	public EntityException() {
		super();
	}
	
	/**
	 * 
	 * @param msg The exception message
	 */
	public EntityException(String msg) {
		super(msg);
	}
}
