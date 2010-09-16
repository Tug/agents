package org.logist.behavior;

/* import table */
import java.util.ArrayList;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.MoveBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.PickupBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.signal.InCityObject;
import epfl.lia.logist.messaging.signal.InCitySignal;
import epfl.lia.logist.messaging.signal.InitSignal;
import epfl.lia.logist.messaging.signal.ResetSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.task.TaskDescriptor;


/**
 * Behavior class of the test agent
 * 
 * This class represents a behavior that is shared for all signals addressed
 * to this agent. It handles almost all signals that should be handled by the
 * reactive agent, and throws an exception for signals it doesn't understand.
 */
public class TestBehavior extends Behavior {

	/* the properties of the agent */
	protected AgentProperties mProps = null;
	
	
	/**
	 * Executes the behavior of the test agent
	 */
	@Override
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
			BehaviorNotImplementedError {
		
		switch( s.getType() ) {
		
			// handles init signals
			case SMT_INIT:
				return handleInit( ((InitSignal)s).getMessage() );

			// handles reset signals
			case SMT_RESET:
				return handleReset( ((ResetSignal)s).getMessage() );
				
			// handles kill signals
			case SMT_KILL:
				return handleKill();
				
			// handles setup signals
			case SMT_SETUP:
				return handleSetup();
	
			// handles incity signals
			case SMT_INCITY:
				return handleInCity( ((InCitySignal)s).getMessage() );
				
			case SMT_TASKDELIVERED:
				return handleTaskDelivered();
				
		}
		
		log( LogSeverityEnum.LSV_INFO, "Agent did not respond to " +
		"an " + s.getType().toString() + " signal !" );
		// throws an exception to signal that this behavior is
		// not implemented
		throw new BehaviorNotImplementedError(s.getType());
	}
	
	
	/**
	 * Handles the INIT signals
	 * 
	 * This method handles INIT-type signals. This signal is sent at the
	 * beginning of the simulation to ask agents to initialize. This message
	 * always carries the properties of the agent. This means that in current
	 * version of the platform, properties of the agent cannot change between
	 * rounds (except for Home)
	 * 
	 * @param ap The properties of the agent.
	 */
	protected IBehaviorResponse handleInit( AgentProperties ap ) 
		throws BehaviorExecutionError {
		
		// properties of the agents
		mProps = ap;
		
		// logs the event
		log( LogSeverityEnum.LSV_INFO, "Agent received " +
				"an INIT signal !" );
		
		// returns a ready response
		return new ReadyBehaviorResponse();
	}
	
	
	/**
	 * Handles the SETUP signals
	 * 
	 * This method handles the SETUP signals. This signal is one of the only
	 * type of signals that carry different objects depending on the signal.
	 */
	protected IBehaviorResponse handleSetup() 
		throws BehaviorExecutionError {

		// logs the event
		log( LogSeverityEnum.LSV_INFO, "Agent received " +
				"a SETUP signal !" );
		
		// returns a ready response
		return new ReadyBehaviorResponse();
	}
	
	
	/**
	 * Handles the RESET signals.
	 * 
	 * This method handles the reset signal. This type of signal is sent 
	 * at the beginning of each round except the first one. The RESET signal
	 * carries the current round number.
	 */
	protected IBehaviorResponse handleReset( int round ) 
		throws BehaviorExecutionError {

		// logs the event
		log( LogSeverityEnum.LSV_INFO, "Agent received " +
				"an RESET signal. !" );
		
		// logs another  event
		log( LogSeverityEnum.LSV_INFO, "Agent " +
				"acknowledged the beginning of round #" + round );
		
		// returns a ready response
		return new ReadyBehaviorResponse();
	}
	
	
	/**
	 * Handles KILL signals
	 * 
	 * This method handles incoming kill signals. This signal is sent by
	 * corresponding agent state to ask the agent to perform shutdown tasks.
	 * However, if agent blocks, application exits even if agent has not 
	 * finished.
	 */
	protected IBehaviorResponse handleKill()
		throws BehaviorExecutionError {

		// logs the event
		log( LogSeverityEnum.LSV_INFO, "Agent received " +
				"an KILL signal !" );
		
		// returns a ready response
		return new ReadyBehaviorResponse();
	}
	
	
	/**
	 * Handles incoming INCITY messages.
	 * 
	 * INCITY messages are sent towards agents when a reactive agent type
	 * arrives in a city. This messages holds the name of the city, the name
	 * of the neighbor cities as well as the tasks.
	 * 
	 * @param obj The object holding the city name, tasks and neighbors
	 */
	protected IBehaviorResponse handleInCity( InCityObject obj ) 
		throws BehaviorExecutionError {

		// is there any task to pickup ?
		if ( obj.Tasks.size() > 0 ) {
			
			// gets the index of the best task
			int taskIndex = considerTasks( obj.Tasks );
			
			// returns a pickup action
			if ( taskIndex >= 0 )
				return new PickupBehaviorResponse( obj.Tasks.get(taskIndex).ID ); 
		}
		
		// there is no task, we choose a random city from here
		int cityIndex = (int)( Math.random() * (double)obj.Neighbors.size() );
		
		// gets the name of the target city
		String cityName = obj.Neighbors.get( cityIndex );
		
		// returns a move to signal
		return new MoveBehaviorResponse( cityName );
	}
	
	
	/**
	 * Considers a list of tasks
	 * 
	 * This function considers the list of tasks passed as an argument
	 * and returns the index of the task which presents the best immediate 
	 * benefit.
	 * 
	 * @param tasks The list of tasks
	 * 
	 * @return An integer value representing the index of the task with 
	 *         highest immediate value
	 */
	protected int considerTasks( ArrayList<TaskDescriptor> tasks )
		throws BehaviorExecutionError {
		
		// creates a consideration array
		double highestConsideration = Double.NEGATIVE_INFINITY;
		int highestConsiderationIndex = -1;
		
		// for every task, decide which one is the best . The best task
		// is the one that maximized the profit and minimizes the weight.
		for( int i=0; i<tasks.size(); i++ ) {
			
			// gets the ith task descriptor
			TaskDescriptor td = tasks.get(i);
			
			// avoids a bad division by zero error
			if ( td.Weight < 0.0001 ) td.Weight = 1.0;
			
			// chooses a factor between 85% and 125%
			double factor = 0.85 + Math.random() * 0.5;
			
			// gets the consideration factor
			double consideration = factor * ( td.RewardPerKm / td.Weight );
			
			// is there a better task ?
			if ( consideration > highestConsideration ) {
				highestConsideration = consideration;
				highestConsiderationIndex = i;
			}
		}
		
		// returns the best index
		return highestConsiderationIndex;
	}
	
	protected IBehaviorResponse handleTaskDelivered()
	throws BehaviorExecutionError {

	// logs the event
	log( LogSeverityEnum.LSV_INFO, "Agent received " +
			"a TaskDelieveredSignal signal !" );
	
	// returns a ready response
	return new ReadyBehaviorResponse();
	}
	
}
