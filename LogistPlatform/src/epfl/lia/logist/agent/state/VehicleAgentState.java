package epfl.lia.logist.agent.state;


import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.messaging.signal.Signal;

/**
 * 
 * @author malves
 *
 */
public abstract class VehicleAgentState extends AgentState {

	/**
	 * Constructor of the class. Initializes the agent with its
	 * associated profile and the parent state...
	 */
	protected VehicleAgentState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
	
	/**
	 * Notify the sneding of a message on behalf of the vehicle state
	 */
	public void notifySignal( Signal<?> signal ) {	
	}
	
	/**
	 * Notified the company agent that a task batch was created
	 */
	public void notifyTaskBatchCreation() {
	}
}

