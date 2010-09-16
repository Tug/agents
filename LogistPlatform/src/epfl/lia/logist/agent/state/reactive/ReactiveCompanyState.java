package epfl.lia.logist.agent.state.reactive;

/* importation table */
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.CompanyAgentState;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.action.Action;


/**
 * 
 * @author malves
 *
 */
public class ReactiveCompanyState extends CompanyAgentState {

	/**
	 * Constructor of the state
	 * @param ap
	 * @param as
	 */
	public ReactiveCompanyState( AgentProfile ap, AgentState as ) {
		super(ap,as);
		LogManager.getInstance().log( LogManager.DEFAULT, LogSeverityEnum.LSV_FATAL, "Hello World From a Reactive Company Agent !!!" );
	}
			
	/**
	 * Executes the vehavior of the agent state
	 */
	public void execute( Action<?> action ) {
	}
	
	
	/**
	 * Notifies the parent
	 */
	protected void notifyParent( Action<?> action ) {
	}
}

