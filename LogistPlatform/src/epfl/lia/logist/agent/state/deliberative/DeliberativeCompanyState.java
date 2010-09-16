package epfl.lia.logist.agent.state.deliberative;

/* importation table */
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.CompanyAgentState;
import epfl.lia.logist.messaging.action.Action;


/**
 * 
 * @author malves
 *
 */
public class DeliberativeCompanyState extends CompanyAgentState {

	/**
	 * Constructor of the state
	 * @param ap
	 * @param as
	 */
	public DeliberativeCompanyState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
			
	
	/**
	 * Executes the vehavior of the agent state
	 */
	public void execute( Action<?> action ) {
	}
}

