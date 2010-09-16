/**
 * 
 */
package org.logist.centralized;


import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.messaging.signal.Signal;

/**
 * @author salves
 *
 */
public class CentralizedVehicleBehavior extends Behavior {

    // It does nothing, as Company specifies plans not a Vehicle.

	/**
	 * 
	 */
	public CentralizedVehicleBehavior() {
	    
	}

	//--------------------------------------------------------------------------
	// Executes the behavior
	//--------------------------------------------------------------------------
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
			BehaviorNotImplementedError {
		
		/*
		 * This section handles signal according to the type of signal passed
		 * as an argument.
		 */
		switch( s.getType() ) {
		
			// handle the <init> signal
			case SMT_INIT:
				return new ReadyBehaviorResponse();
			
			// handle the <reset> signal
			case SMT_RESET:
				return new ReadyBehaviorResponse();
				
			// handle the <setup> signal
			case SMT_SETUP:
				return new ReadyBehaviorResponse();

			// handle the <kill> signal
			case SMT_KILL:
				return new ReadyBehaviorResponse();

			// handles every other signal...
			default:
				return new ReadyBehaviorResponse();
		}
	}

}
