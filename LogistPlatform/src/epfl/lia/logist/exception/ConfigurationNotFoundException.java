package epfl.lia.logist.exception;

/**
 * 
 * @author malves
 *
 */
public class ConfigurationNotFoundException extends LogistException {
	
	/**
	 * Constructor of the exception class
	 * @param name
	 */
	public ConfigurationNotFoundException( String name ) {
		super("The configuration tree named '" + name + "' " +
	       "doesn't exist...");
	}
}
