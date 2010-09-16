package epfl.lia.logist.agent.state;

/* importation table */
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.messaging.signal.Signal;


/**
 * 
 * @author malves
 *
 */
public abstract class CompanyAgentState extends AgentState {

	/**
	 * Constructor of the state
	 * @param ap
	 * @param as
	 */
	public CompanyAgentState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
	
	/**
	 * NOtify that a signal was sent on behalf of the company.
	 */
	public void notifySignal( Signal<?> signal ) {
	}
	
	/**
	 * Notified the company agent that a task batch was created
	 */
	public void notifyTaskBatchCreation() {
	}
}

