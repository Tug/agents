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

/* importation table */
import epfl.lia.logist.core.topology.City;


/**
 * This class represents a pair of cities. (City1, City2) is equivalent to
 * (City2, City1).
 */
public class PairOfCities {

	/* The first city in the pair */
	protected City city1;
	
	/* The second city in the pair */
	protected City city2;
	
	
	
	/**
	 * Constructs a pair of cities.
	 */
	public PairOfCities(City city1, City city2){
		this.city1 = city1;
		this.city2 = city2;
	}
	
	
	/**
	 * Compares the specified object with this pair of cities for equality. 
	 * Returns true iff the object is a pair of cities, and the the cities 
	 * in the pair are the same, irrespective of the order.
	 */
	public boolean equals( Object obj ) {
		if (obj == this)
			return true;
		if (!(obj instanceof PairOfCities))
			return false;
		PairOfCities pair = (PairOfCities) obj;
		return (
		((city1.equals(pair.getCity1())) && (city2.equals(pair.getCity2()))) ||
		((city1.equals(pair.getCity2())) && (city2.equals(pair.getCity1()))));
	}
	
	
	/**
	 * Returns the hashCode value for this state. The hash code is equal 
	 * to the sum of the hash codes for the elements of the state (i.e. 
	 * the location, the list of loaded tasks and the list of available 
	 * tasks).
	 */
	public int hashCode(){
		return city1.hashCode() + city2.hashCode();
	}
	

	/**
	 * Return the first city.
	 */
	public City getCity1() {
		return city1;
	}
	
	
	/**
	 * Return the second city.
	 */
	public City getCity2() {
		return city2;
	}
	
	
	/**
	 * Displays the string formatted version of the class
	 * contents.
	 */
	public String toString(){
		return "("+city1.getNodeLabel()+" - "+city2.getNodeLabel()+")";
	}
}
