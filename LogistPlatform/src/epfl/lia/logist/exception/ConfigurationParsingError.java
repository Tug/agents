package epfl.lia.logist.exception;

/* import table */
import org.jdom.Element;


/**
 * 
 * @author malves
 *
 */
public class ConfigurationParsingError extends LogistException {

	/**
	 * The default constructor of the class
	 * @param ns
	 */
	public ConfigurationParsingError( String msg ) {
		super(msg);
	}

}
