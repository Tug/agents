package epfl.lia.logist.core;

/* import table */
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.tools.LogistGlobals;


/**
 * A service is the common interface shared by
 * all management objects. This single common
 * interface allows for a simpler initialization,
 * setup and destruction.
 */
public interface IService {

	/**
	 * Initializes the service
	 */
	public void init() throws Exception;
	
	
	/**
	 * Shuts the service down
	 */
	public void shutdown();
	
	
	/**
	 * Sets every service up
	 */
	public void setup( Configuration cfg, LogistGlobals lg ) throws Exception;
	
	
	/**
	 * Resets the services
	 */
	public void reset( int round );
}
