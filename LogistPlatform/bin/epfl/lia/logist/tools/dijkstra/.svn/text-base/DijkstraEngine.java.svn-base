/* 
 * Created on 13-Oct-2004
 * Author: Radu Jurca and Michael Schumacher
 * Copyright Ecole Polytechnique Fédérale de Lausanne (EPFL)
 * Artificial Intelligence Laboratory
 * Intelligent Agents Course - Winter Semester 2005-2006
 * Version: $Revision: 23 $
 * Last Modified: $Date: 2006-11-03 09:58:10 +0100 (ven, 03 nov 2006) $
 *
 */

package epfl.lia.logist.tools.dijkstra;

/* the importation table */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;


/**
 * The Dijkstra Engine algorithm
 */
public class DijkstraEngine
{
    /**
     * Infinity value for distances.
     */
    public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;
    
    
    /**
     * This comparator orders cities according to their shortest distances,
     * in ascending fashion. If two cities have the same shortest distance,
     * we compare the cities themselves.
     */
    private final Comparator shortestDistanceComparator = new Comparator()
        {
            public int compare(Object left, Object right)
            {
                if((left instanceof City) && (right instanceof City))
                	return compare((City) left, (City) right);
                else throw new ClassCastException("Not comparable");
            }
            
            private int compare(City left, City right)
            {
                // note that this trick doesn't work for huge distances, close to Integer.MAX_VALUE
                int result = getShortestDistance(left) - getShortestDistance(right);
                
                return (result == 0) ? left.compareTo(right) : result;
            }
        };
    
    /**
     * The graph.
     */
    private final Topology mTopology;
    
    /**
     * The working set of cities, kept ordered by shortest distance.
     */
    private final SortedSet unsettledNodes = new TreeSet(shortestDistanceComparator);
    
    /**
     * The set of cities for which the shortest distance to the source
     * has been found.
     */
    private final Set settledNodes = new HashSet();
    
    /**
     * The currently known shortest distance for all cities.
     */
//Java 1.5 feature
    private final Map<City, Integer> shortestDistances = new HashMap<City, Integer>();

    /**
     * Predecessors list: maps a city to its predecessor in the spanning tree of
     * shortest paths.
     */
    private final Map<City, City> predecessors = new HashMap<City, City>();
    
    /**
     * The list of all pairs of cities
     */
    private ArrayList<PairOfCities> pairs;
    
    /**
     * The HashMap in which the result is returned
     */
//  Java 1.5 feature
    private ShortestPath result;
    /**
     * Constructor.
     */
//Java 1.5 feature
    public DijkstraEngine(Topology topo, ArrayList<PairOfCities> pairs,ShortestPath result)
    {
    	this.mTopology = topo;
    	this.pairs = pairs;
    	this.result = result;
    }

    /**
     * Initialize all data structures used by the algorithm.
     * 
     * @param start the source node
     */
    private void init(City start)
    {
        settledNodes.clear();
        unsettledNodes.clear();
        
        shortestDistances.clear();
        predecessors.clear();
        
        // add source
        setShortestDistance(start, 0);
        unsettledNodes.add(start);
    }
    
    /**
     * Run Dijkstra's shortest path algorithm on the graph.
     * The results of the algorithm are available through
     * {@link #getPredecessor(City)}
     * and 
     * {@link #getShortestDistance(City)}
     * upon completion of this method.
     * 
     * @param start the starting city
     */
    public void execute(City start)
    {
        init(start);
        
        // the current node
        City u;
        
        // extract the node with the shortest distance
        while ((u = extractMin()) != null)
        {
            //assert !isSettled(u);
            
            markSettled(u);
            
            //add the minimum distances to the result
            City current = u;
            City currentPred = null;
            Path path = new Path(u);
            while ((currentPred = getPredecessor(current)) != null){
            	int dist = mTopology.getDistance(current,currentPred);
            	Path p = path.add(currentPred, dist);
            	PairOfCities pair = new PairOfCities(u,currentPred);
            	result.put(pair, p);
            	pairs.remove(pair);
            	current = currentPred;
            }
            
            relaxNeighbors(u);
        }
    }

    /**
     * Extract the city with the currently shortest distance, and remove it from
     * the priority queue.
     * 
     * @return the minimum city, or <code>null</code> if the queue is empty.
     */
    private City extractMin()
    {
    	if (unsettledNodes.isEmpty()) return null;
    	
        City min = (City) unsettledNodes.first();
        unsettledNodes.remove(min);
        
        return min;
    }
    
	/**
	 * Compute new shortest distance for neighboring nodes and update if a better
	 * distance is found.
	 * 
	 * @param u the node
	 */
    private void relaxNeighbors(City u)
    {
        for (Iterator i = mTopology.getDestinations(u).iterator(); i.hasNext(); )
        {
            City v = (City) i.next();
            
            // skip node already settled
            if (isSettled(v)) continue;
            
            if (getShortestDistance(v) > getShortestDistance(u) + mTopology.getDistance(u, v))
            {
            	// assign new shortest distance and mark unsettled
                setShortestDistance(v, getShortestDistance(u) + mTopology.getDistance(u, v));
                                
                // assign predecessor in shortest path
                setPredecessor(v, u);
            }
        }        
    }

	/**
	 * Mark a node as settled.
	 * 
	 * @param u the node
	 */
	private void markSettled(City u)
	{
		settledNodes.add(u);    
	}

	/**
	 * Test a node.
	 * 
     * @param v the node to consider
     * 
     * @return whether the node is settled, ie. its shortest distance
     * has been found.
     */
    private boolean isSettled(City v)
    {
        return settledNodes.contains(v);
    }

    /**
     * @return the shortest distance from the source to the given city, or
     * {@link DijkstraEngine#INFINITE_DISTANCE} if there is no route to the destination.
     */    
    public int getShortestDistance(City city)
    {
        Integer d = (Integer) shortestDistances.get(city);
        return (d == null) ? INFINITE_DISTANCE : d.intValue();
    }

	/**
	 * Set the new shortest distance for the given node,
	 * and re-balance the set according to new shortest distances.
	 * 
	 * @param city the node to set
	 * @param distance new shortest distance value
	 */        
    private void setShortestDistance(City city, int distance)
    {
        // this crucial step ensure no duplicates will be created in the queue
        // when an existing unsettled node is updated with a new shortest distance
        unsettledNodes.remove(city);

        shortestDistances.put(city, new Integer(distance));
        
		// re-balance the sorted set according to the new shortest distance found
		// (see the comparator the set was initialized with)
		unsettledNodes.add(city);        
    }
    
    /**
     * @return the city leading to the given city on the shortest path, or
     * <code>null</code> if there is no route to the destination.
     */
    public City getPredecessor(City city)
    {
        return (City) predecessors.get(city);
    }
    
    private void setPredecessor(City a, City b)
    {
        predecessors.put(a, b);
    }

}
