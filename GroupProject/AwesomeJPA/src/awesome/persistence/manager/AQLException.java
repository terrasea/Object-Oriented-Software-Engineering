package awesome.persistence.manager;

public class AQLException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AQLException() {
		super();
	}
	
	/**
	 * 
	 * @param msg The exception message
	 */
	public AQLException(String msg) {
		super(msg);
	}
}
