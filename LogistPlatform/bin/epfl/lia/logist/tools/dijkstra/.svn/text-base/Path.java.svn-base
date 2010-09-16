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
import epfl.lia.logist.core.topology.Route;


/**
 * A path is a list of consecutive cities through which a vehicle can travel.
 * If there is a road from A to B, a possible path is (A,B). If the road from A to B
 * passes through C, (A,B) is not a possible path, and only (A,C,B) is a valid path. 
 * So is (B,C,A). The distance of the road connecting the two ends of the path is also 
 * stored. 
 */
public class Path {
	
	/* the list of cities in the path */
	private ArrayList<City> mCityList = null;
	
	/* the distance from the start to the end */
	private double mDistance = 0.0;
	
	
	/**
	 * The class constructor. Initializes the path
	 * as a clone.
	 */
	public Path( ArrayList<City> list, double distance ){
		mCityList = (ArrayList<City>) list.clone();
		mDistance = distance;
	}
	
	
	/**
	 * The class constructor. Initializes the path
	 * from a single city.
	 */
	public Path( City c ){
		mCityList = new ArrayList<City>();
		mCityList.add(c);
	}

	
	/**
	 * Adds a new city to the path.
	 */
	public Path add( City c, double d ){
		mCityList.add(c);
		mDistance += d;
		return (Path)this.clone();
	}
	
	
	/**
	 * Return the length of this path.
	 */
	public double getDistance(){
		return mDistance ;
	}
	
	
	/**
	 * Clones the path
	 */
	public Object clone(){
		return new Path(mCityList,mDistance);
	}

	
	/**
	 * Given a Path, return the next city on the path. 
	 */
	public City getNextCityOnPath( City start ) {
		if ( mCityList.get(0).equals(start) )
			return mCityList.get(1);
		else
			return mCityList.get(mCityList.size()-2);	
	}

	
	/**
	 * Add to the plan the actions that allow a vehicle to move from the start city, 
	 * to the end of the path.
	 * @param start The starting point
	 * @param plan the plan
	 */
	/*
	public void addActions(City start, Plan plan){
		
		City present = start;
		//Java 1.5 feature
		if (list.get(0).equals(start))
			//start from the begining of the list
			for (int i=1; i<list.size(); i++){
				City next = (City) list.get(i);
				Route r = (Route)present.getEdgesTo(next).iterator().next();
				Action a = new Action();
				a.setTakeRoute(r);
				plan.addAction(a);
				present=next;
			}
		else
			//start from the end of the list
			for (int i=list.size()-2; i>-1; i--){
				City next = (City) list.get(i);
				Route r = (Route)present.getEdgesTo(next).iterator().next();
				Action a = new Action();
				a.setTakeRoute(r);
				plan.addAction(a);
				present=next;
			}
		
	} */
		
	
	/**
	 * Return a String representation of this path
	 */
	public String toString(){
		String s = "(";
		for (int i=0; i<mCityList.size();i++)
			s += ((City)mCityList.get(i)).getNodeLabel()+" ";
		s += "):" + mDistance;
		return s;
	}
}
