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

package epfl.lia.logist.core.topology;

/* importations */
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.Node;


/**
 * This class models a Route between two cities.
 */
public class Route extends DefaultEdge {
	
	boolean mRendered = false;
	
	/**
	 * Generate a Route of a given distance between two cities 
	 * @param city1 The first City
	 * @param city2 The second City
	 * @param distance The distance of the route
	 */
	public Route( Node city1, Node city2, double distance ) {
		super(city1, city2);
		setStrength(distance);
		
		//make the cities aware of the route as well
		if (city1 != null){
			city1.addOutEdge(this);
		}
		if (city2 != null){
			city2.addInEdge(this);
		}
		
	}
	
	
	public boolean getRendered() {
		return mRendered;
	}
	
	public void setRendered( boolean b ) {
		mRendered = b;
	}
	
	
	/**
	 * Get the distance of the Route.
	 * @return The distance of the route.
	 */
	public double getDistance(){
		return getStrength();
	}
	
	/**
	 * Returns a String representation of the Object.
	 */
	public String toString() {
		return "Route from node "
			+ getFrom().getNodeLabel()
			+ " to node "
			+ getTo().getNodeLabel()
			+ ", distance: "
			+ getDistance();
	}
	
}
