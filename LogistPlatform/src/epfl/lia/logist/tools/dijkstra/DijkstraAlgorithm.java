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

/* the import table */
import java.util.ArrayList;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;


public class DijkstraAlgorithm {
	
	/**
	 * This algorithm computes the shortest path between any pair of cities. This
	 * is needed, because when we pick a task, we must deliver it on the shortest
	 * path to the destination.
	 */
	public static ShortestPath computeShortestPath( Topology topo ){
		
		// prepares the map of returned cities
		ShortestPath result = new ShortestPath();
		
		// generate a list of all pairs of cities
		ArrayList<City> cities = new ArrayList<City>(topo.getCities().values());
		ArrayList<PairOfCities> pairs = new ArrayList<PairOfCities>();
		
		// creates all possible paires of cities
		for ( int i=0; i<cities.size(); i++ ) {
			for ( int j=i+1; j<cities.size(); j++ ){
				pairs.add( new PairOfCities((City)cities.get(i),
						                    (City)cities.get(j)) );
			}
		}
		
		// continues looping on all pairs until every one is resolved, i.e
		// we computed a shortest path to the destination..
		while (!pairs.isEmpty()){
			
			// gets the first element
			PairOfCities pair = (PairOfCities)pairs.get(0);
			
			// computes the shortest path
			DijkstraEngine engine = new DijkstraEngine(topo,pairs,result);
			engine.execute( pair.getCity1() );
		}
		
		// returns the result in the form of an hash map
		return result;
	}
	
	
	/**
	 * Obtain the next City on the shortest path towards the desired dest.
	 */
	public static City moveOnShortestPathTowards( ShortestPath shortestPathMap, 
												  City presentLocation, 
												  City destination) {
		PairOfCities pc = new PairOfCities(presentLocation, destination);
		Path path = (Path)shortestPathMap.get(pc);
		City nextCity = path.getNextCityOnPath(presentLocation);	
		return nextCity;
	}

	
	/**
	 * Get the distance of the shortest path between two cities
	 */
	public static double getShortestDistanceBetween( ShortestPath shortestPathMap,
													 City from, City to ){
		
		// handle trivial cases
		if ( from.equals(to) ) return 0.0;
		if ( from==null || to==null ) return Double.POSITIVE_INFINITY;
		
		// builds a pair of cities
		PairOfCities pc = new PairOfCities(from, to);
		
		// get the shortest path
		Path path = (Path)shortestPathMap.get(pc);
		
		// the the distance
		return path.getDistance();
	}
}
