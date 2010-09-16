package epfl.lia.logist.agent.plan;

import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.task.Task;

public class PlanVerifier {

	/**
	 * This method indicates whether a plan is valid or not.
	 * 
	 * This method examinates the input plan. A plan is considered valid only
	 * if there is a deliver action for each pickup and there is at least one
	 * target city.
	 * 
	 * @param plan the plan to examine.
	 * 
	 * @return true if the plan is valid, false otherwise
	 */
	public static boolean isValid( Plan p, AgentProfile ap ) {
		
		// get the log manager
		LogManager log = LogManager.getInstance();
		
		// is there any plan ?
		if ( p == null ) return false;
		
		// the plan doesn't meet the conditions
		if ( !ap.getState().planMeetsConditions( p ) )
			return false;
		
		// clone the plan
		Plan plan = (Plan)p.clone();
		
		// create an empty list of tasks
		ArrayList<Task> pickedTasks = new ArrayList<Task>();
		
		// creates an hash map associating
		HashMap<City,Integer> cityCount = new HashMap<City,Integer>();
		
		// get agent properties linked to the profile
		AgentProperties properties = ap.getProperties();
		
		// verifies that the load is not to high
		double currentLoad = properties.Load;
		
		// goes through the plan actions
		while( plan.hasMoreElements() ) {
			
			// get the next action
			IGenericAction ga = plan.nextElement();
			
			// what type of action ?
			switch( ga.getType() ) {
				case PICKUP:
					{
						// get the task to pickup
						Task t = ((GPickupAction)ga).getTask();
						
						// the task doesn't exist
						if ( t == null ) {
							log.warning( "Plan " + plan + " is invalid !" );
							log.warning( "Found an empty task in plan " );
							return false;
						}
						
						// if the pickup list contains the tasks, then the 
						// plan is not valid
						if ( pickedTasks.contains(t) ) {
							return false;
						}
		
						// adds the task load
						currentLoad += t.getWeight();
		
						// the vehicle has not capacity enough
						if ( currentLoad>properties.Capacity ) {
							log.warning( "Plan " + plan + " is invalid !" );
							log.warning( "The capacity is not sufficient to " +
									" store all tasks." );
							log.warning( "The current load is " + currentLoad );
							log.warning( "The initial load was " + properties.Load );
							log.warning( "The vehicle capacity is " + properties.Capacity );
							return false;
						}
							
						// adds the task to the list
						pickedTasks.add( t );
					}
					
					// breaks out
					break;
					
				case MOVE:
				{
					// gets the target city
					City c = ((GMoveAction)ga).getTarget();
					
					// increments the number of times this city appears
					if ( c == null ) {
						log.warning( "Plan " + plan + " is invalid !" );
						log.warning( "The target city is null !" );
						return false;
					}
					
					// if the city doesn't exist, add it
					if ( !cityCount.containsKey(c) ) 
						cityCount.put( c, new Integer(1) );
					else
					{
						Integer count = cityCount.get(c);
						count = count + 1;
						cityCount.put( c, count );
					}
				}
				
				// breaks out of here
				break;
					
				case DELIVER:
					{
						// get the task to deliver
						Task t = ((GDeliverAction)ga).getTask();
						
						// the task doesn't exist
						if ( t == null ) {
							log.warning( "Plan " + plan + " is invalid !" );
							log.warning( "Delivering a null task !" );
							return false;
						}
						
						// removes the load
						currentLoad -= t.getWeight();
						
						// removes the task
						pickedTasks.remove( t );
					}
					
					// breaks out
					break;
			}
		}
		
		
		// are tasks which are not delivered ?
		if ( pickedTasks.size() > 0 ) {
			log.warning( "Plan " + plan + " is invalid !" );
			log.warning( "Some tasks were not delivered. Still remaining " +
					"to be delivered are:" );
			for( Task t : pickedTasks ) {
				log.warning( ""+t );
			}
			return false;
		}
		
		// there are no actions
		if ( cityCount.size() == 0 ) {
			log.warning( "Plan " + plan + " is invalid !" );
			log.warning( "There are no actions in the plan !" );
			return false;
		}
		
		// it seems the plan is correct
		return true;
	}
}
