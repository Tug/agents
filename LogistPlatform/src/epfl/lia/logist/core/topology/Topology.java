package epfl.lia.logist.core.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import uchicago.src.sim.network.Node;
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.IService;
import epfl.lia.logist.core.listeners.ITopologyListener;
import epfl.lia.logist.exception.TopologyCreationException;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.LogistGlobals;
import epfl.lia.logist.tools.dijkstra.DijkstraAlgorithm;
import epfl.lia.logist.tools.dijkstra.ShortestPath;


/**
 * 
 * @author malves
 *
 */
public class Topology implements IService {
	
	/* A map of all cities in the graph */
	private HashMap<String, City> mMapOfCities = null;
	
	/* A complete list of routes */
	private ArrayList<Route> mListOfRoutes = null;
	
	/* A singleton instance of this class */
	private static Topology msSingleton = null;
	
	/* The list of all listeners associated with this class */
	private ArrayList<ITopologyListener> mListeners = null;

	/* A reference to the log manager */
	private LogManager mLogMgr = null;
	
	/* A reference to the object holding the shortest path info */
	private ShortestPath mShortestPath = null;
	
	/* A list of global system properties */
	private LogistGlobals mGlobals = null;
	
	
	/**
	 * Constructor of the class
	 * 
	 * Initialize the internal state state the topology instance.
	 */
	public Topology(){
		
		// initialize a single instance of this class
		if ( msSingleton == null )
			msSingleton = this;
		
		// creates maps & lists
		mMapOfCities = new HashMap<String, City>();
		mListOfRoutes = new ArrayList<Route>();
		mListeners = new ArrayList<ITopologyListener>();
		mLogMgr = LogManager.getInstance();
	}	
	

	/**
	 * Indicates whether a particular city exists in topology
	 * 
	 * This method returns true when a particular city exists in current 
	 * topology, false otherwise.
	 */
	public boolean exists( String cityName ) {
		if ( cityName==null ) return false;
		return ( mMapOfCities.get(cityName) != null );
	}
	
	
	/**
	 * Indicates whether or not two cities are neighbors
	 * 
	 * This method indicates if two cities are neighbours from inspecting one
	 * of the cities neighbor list. If the second city is present in this list,
	 * then it means to they are neighbors.
	 * 
	 * @param city1Name The name of the first city
	 * @param city2Name The name of the second city
	 * 
	 * @return \b true if cities are neighbors, \b false otherwise
	 */
	public boolean neighbors( String city1Name, String city2Name ) {
		City c1 = mMapOfCities.get( city1Name );
		City c2 = mMapOfCities.get( city2Name );
		return neighbors(c1,c2);
	}
	
	
	/**
	 * Indicates whether or not two cities are neighbors
	 * 
	 * This method indicates if two cities are neighbours from inspecting one
	 * of the cities neighbor list. If the second city is present in this list,
	 * then it means to they are neighbors.
	 * 
	 * @param city1Name The name of the first city
	 * @param city2Name The name of the second city
	 * 
	 * @return \b true if cities are neighbors, \b false otherwise
	 */
	public boolean neighbors( City city1, City city2 ) {
		if ( city1==null || city2 == null ) return false;
		return city1.hasEdgeToOrFrom( city2 );
	}
	
	
	/**
	 * Create the topology from a description
	 * 
	 * This method takes a topology descriptor in input and creates the
	 * topology based on this.
	 * 
	 * @param td the topology descriptor
	 * 
	 * @throws TopologyCreationException when some topological information 
	 * contains errors
	 */
	public void create( TopologyDescriptor td ) 
		throws TopologyCreationException {
	
		// log the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO , 
			"Creating the list of cities from descriptors..." );
		
		// for all city descriptors,
		for ( CityDescriptor cd : td.Cities ) {
			createCityFromDescriptor( cd );
		}
		
		// log the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO , 
			"Creating the list of routes from descriptors." );
		
		// for every route descriptor...
		for ( RouteDescriptor rd : td.Routes ) {
			createRouteFromDescriptor( rd );
		}
		
		// log the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO , 
			"Computing the shortest path for all possible " +
			"pairs of cities." );
		
		// computes the shortest path
		mShortestPath = DijkstraAlgorithm.computeShortestPath( this );
	}
	
	
	/**
	 * Moves on the shortest path between two cities. 
	 *
	 * This method returns the shortest path between two cities. It does so
	 * by giving the name of the next city in the path !
	 */
	public City moveOnShortestPath( City from, City to ) {
		return DijkstraAlgorithm.
			moveOnShortestPathTowards( mShortestPath, from, to );
	}
	

	/**
	 * Get the shortest distance between cities from source to destination.
	 */
	public double shortestDistanceBetween( City from, City to ) {
		return DijkstraAlgorithm.
						getShortestDistanceBetween( mShortestPath, from, to );
	}


	/**
	 * Create a city from a descriptor.
	 */
	private void createCityFromDescriptor( CityDescriptor cd ) 
		throws TopologyCreationException {
		
		// we only support distinct entries
		if ( mMapOfCities.containsKey(cd.Name) )
			throw new TopologyCreationException( "A city" +
					" named '" + cd.Name + "' already exists in topology.");
		
		// creates a new city entry
		City city = new City( cd.X, cd.Y, cd.Name );
		
		// adds the city into the table
		mMapOfCities.put( cd.Name, city );
		
		// propagates the changes to all listeners
		for ( ITopologyListener listener : mListeners ) {
			listener.onCityAddition( city );
		}
		
		// log the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO , 
			"Created new city " + city + "." );
	}
	
	
	/**
	 * Creates a new route from a descriptor.
	 */
	private void createRouteFromDescriptor( RouteDescriptor cd ) 
		throws TopologyCreationException {
		
		// gets the two involed citites
		City cityFrom = mMapOfCities.get( cd.Source );
		City cityTo = mMapOfCities.get( cd.Destination );
		
		// creates a new route
		Route routeFrom = new Route( cityFrom, cityTo, cd.Distance );
		Route routeTo = new Route( cityTo, cityFrom, cd.Distance );
		
		// adds a route to the array
		mListOfRoutes.add( routeFrom );
		mListOfRoutes.add( routeTo );
		
		// log the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO , 
			"Creating new route between " + cityFrom + " and " + cityTo + "." );
	}	
	
	
	/**
	 * Returns a map of Cities in the topology, indexed by their name.
	 * @return The map of cities.
	 */
	public HashMap<String, City> getCities() {
		return mMapOfCities;
	}
	
	
	/**
	 * Get the distance between two cities.
	 * @param start The starting city
	 * @param end The destination city
	 * @return the distance
	 */
	public int getDistance( City start, City end ) {
		
		// if start city matches the end city, then don't move
		if ( start.match(end) )
			return 0;
		
		// if one of the cities doesn't exist, then return INFINITY as the
		// distance separating them...
		if ( start==null || end==null ||
			 !exists(start.getNodeLabel()) || !exists(end.getNodeLabel()) )
			return Integer.MAX_VALUE;
		
		// get the route going to this city
		Route route = (Route)start.getEdgesTo(end).iterator().next();
		
		// this city cannot be reached 
		if ( route == null )
			return Integer.MAX_VALUE;
		
		// return the distance
		return (int)route.getDistance();
	}

	
	/**
	 * Return all the cities that can be reached from the city
	 * @param city
	 * @return a list of cities that can be reached from the city given
	 * as argument
	 */
	public ArrayList<?> getDestinations( City city ){
		return city.getOutNodes();
	}

	
	/**
	 * Return the singleton instance of this class
	 */
	public static Topology getInstance() {
		return msSingleton;
	}
	
	
	/**
	 * This function specifies if the requested city is accessible from
	 * a particular city.
	 */
	public boolean isAccessible( City from, City to ) {
		HashSet setOfEdges = from.getEdgesTo(to);
		return ( setOfEdges!=null );
	}
	
	
	/**
	 * Return a city from its name
	 * 
	 * @param city the name of the city
	 * 
	 * @return a City object with the same name
	 */
	public City getCity( String city ) {
		return mMapOfCities.get(city);
	}
	
	
	/**
	 * Get a random city from the current topology
	 */
	public City getRandomCity() {
		int lIndex = (int)((double)mMapOfCities.size() * Math.random());
		Node theCity = (Node)(mMapOfCities.values().toArray()[lIndex]);
		return (City)theCity;
	}
	
	/**
	 * Adds a new topology change listener
	 */
	public void addListener( ITopologyListener tcl ) {
		mListeners.add( tcl );
	}
	
	
	/**
	 * Initializes the class instance
	 */
	public void init() {
	}
	
	
	/**
	 * Destroys the class instance
	 */
	public void shutdown() {
	}
	
	
	/**
	 * Sets the topology up
	 */
	public void setup( Configuration cfg, LogistGlobals lg ) throws Exception {
		create( cfg.Topology );
		mGlobals = lg;
	}

	
	/**
	 * (non-Javadoc)
	 * @see epfl.lia.logist.core.IService#reset(int)
	 */
	public void reset( int round ) {
	}

	
	/**
	 * The text for the service management system
	 */
	public String toString() {
		return "Topology indexing service";
	}
}