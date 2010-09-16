/**
 * 
 */
package epfl.lia.logist.agent.state.auction;

import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.VehicleAgentState;
import epfl.lia.logist.messaging.action.Action;

/**
 * @author salves
 *
 */
public class BidderVehicleState extends VehicleAgentState {

	/**
	 * The constructor of the class
	 * 
	 * @param ap The associated profile 
	 * @param as The associated parent state (company agent)
	 */
	public BidderVehicleState(AgentProfile ap, AgentState as) {
		super(ap, as);
	}

	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.agent.state.AgentState#execute(epfl.lia.logist.messaging.action.Action)
	 */
	@Override
	protected void execute( Action<?> action ) {
		
		// reacts upon reception of an action
		switch( mState ) {
		
			// vehicles are not initialized, since they only follow plans, the
			// agent entities have nothing to do !
			case AS_SETUP:
				mState = AgentStateEnum.AS_IDLE;
				break;
				
			// when sent in the idle state, the vehicle agent is kept busy 
			// waiting for a plan to execute...
			case AS_IDLE:
				mState = AgentStateEnum.AS_WAIT;
				mLastState = AgentStateEnum.AS_IDLE;
				break;
		}
	}

}
