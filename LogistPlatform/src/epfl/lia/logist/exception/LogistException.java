package epfl.lia.logist.exception;

/* import table */
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;


/**
 * The LogistExceeption is the base class for all platform exceptions.
 * 
 * The logist exception is a simple exception. It is used instead of the 
 * Exception class to make clear the fact that it is a platform exception.
 */
public class LogistException extends Exception {
	
	/**
	 * Class constructor
	 * 
	 * Initialize the base class 
	 */
	public LogistException(String msg) {
		super(msg);
		LogManager.getInstance().error("Logist exception: " + msg );
		LogManager.getInstance().flush( LogManager.DEFAULT );
	}
}
