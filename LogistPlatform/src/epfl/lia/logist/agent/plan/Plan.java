/**
 * 
 */
package epfl.lia.logist.agent.plan;

import java.util.ArrayList;
import java.util.Enumeration;

import epfl.lia.logist.core.topology.Topology;

/**
 * @author salves
 *
 */
public class Plan implements Enumeration<IGenericAction> {

	private ArrayList<IGenericAction> mActionList = null;
	private double mDistanceDriven = 0.0;
	
	
	/**
	 * Default constructor. 
	 * 
	 * Builds a perfectly empty plan..
	 */
	public Plan() {
		mActionList = new ArrayList<IGenericAction>();
	}
	
	
	/**
	 * Copy constructor. 
	 * 
	 * Creates a new plan with entries given in another list.
	 */
	public Plan( ArrayList<IGenericAction> actionList ) {
		mActionList = new ArrayList<IGenericAction>();
		for ( IGenericAction a : actionList )
			addAction( a );
	}
	
	
	/**
	 * Copy constructor.
	 * 
	 * Creates a new plan from entries given in an array of actions.
	 */
	public Plan( IGenericAction[] actionArray ) {
		this();
		for( int i=0; i<actionArray.length; i++ )
			addAction( actionArray[i] );
	}
	
	
	/**
	 * Clones the current plan
	 */
	public Object clone() {
		Plan plan = new Plan();
		for ( IGenericAction a : mActionList ) {
			plan.addAction( a );
		}
		return plan;
	}
	
	
	/**
	 * Clears the plan
	 *
	 * This method clear the plan entries and sets its internal list to no
	 * elements.
	 */
	public void clear() {
		mActionList.clear();
	}
	
	
	/**
	 * Add a new action to the plan
	 * 
	 * This method allows adding a new action into the plan.
	 */
	public void addAction( IGenericAction action ) {
		mActionList.add( action );
		if ( action.getType() == AgentActionTypeEnum.MOVE ) {
			mDistanceDriven += ((GMoveAction)action).getDistance();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return !mActionList.isEmpty();
	}

	
	/* (non-Javadoc)
	 * @see java.util.Enumeration#nextElement()
	 */
	public IGenericAction nextElement() {
		if ( mActionList.isEmpty() )
			return null;
		IGenericAction nextAction = mActionList.remove( 0 );
		return nextAction;
	}
	
	
	/**
	 * Returns the currently driven distance
	 * @return
	 */
	public double getDistanceDriven() {
		return mDistanceDriven;
	}
	
	
	/**
	 * Returns the cost per of achieving this plan
	 */
	public double getCost( double costPerKm ) {
		return mDistanceDriven * costPerKm;
	}
	
	public String toString() {
		String result = "plan<actions:";
		for( IGenericAction a : mActionList ) {
			result += a.toString() + ", ";
		}
		int iPos = result.lastIndexOf( "," );
		if ( iPos > -1 ) {
			result = result.substring( 0,  iPos );
		}
		return result + "; distance: " + mDistanceDriven + ">";
	}
}
