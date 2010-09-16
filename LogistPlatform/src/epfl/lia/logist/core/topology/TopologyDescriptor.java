package epfl.lia.logist.core.topology;

/* import table */
import java.util.ArrayList;
import java.util.Properties;


/**
 * 
 * @author salves
 *
 */
public class TopologyDescriptor {

	/**
	 * The set of all properties in the system
	 */
	public Properties Props = null;
	
	/**
	 * An array containing all routes
	 */
	public ArrayList<RouteDescriptor> Routes = null;
	
	/**
	 * An array containing all cities
	 */
	public ArrayList<CityDescriptor> Cities = null;
}
