package awesome.persistence.annotations;

/**
 * 
 * Enumeration to represent the loading type for field
 *
 */
public enum FetchType {
	LAZY,		// Lazily load the field when the object is requested
	EAGER		// Imediatly load the field when the object is requested
}
