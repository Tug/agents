package epfl.lia.logist.agent.state.centralized;

/* importation table */
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.VehicleAgentState;
import epfl.lia.logist.messaging.action.Action;


/**
 * 
 * @author malves
 *
 */
public class CentralizedVehicleState extends VehicleAgentState {

	/**
	 * Constructor of the state
	 * @param ap
	 * @param as
	 */
	public CentralizedVehicleState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
			
	
	/**
	 * Executes the behavior of the agent
	 */
	public void execute( Action<?> action ) {
		
		switch( mState ) {
			case AS_SETUP:
				this.mLastState = AgentStateEnum.AS_SETUP;
				this.mState = AgentStateEnum.AS_IDLE;
				break;
				
			case AS_IDLE:
				this.mLastState = AgentStateEnum.AS_IDLE;
				this.mState = AgentStateEnum.AS_WAIT;
				break;
				
			default:
		}
	}
}

