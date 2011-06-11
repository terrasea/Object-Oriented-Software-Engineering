package awesome.persistence.manager;

/**
 * 
 * Exception type returned when the manager encounters an object that is not
 * an entity.
 *
 */
public class NotAEntity extends Exception {

	public NotAEntity(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5734906945159883989L;

}
