package epfl.lia.logist.agent.state.centralized;

/* importation table */
import java.util.ArrayList;

import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.CompanyAgentState;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.signal.InStateObject;


/**
 * 
 * @author malves
 *
 */
public class CentralizedCompanyState extends CompanyAgentState {

	/**
	 * Constructor of the state
	 * @param ap
	 * @param as
	 */
	public CentralizedCompanyState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
			
	
	/**
	 * Generates a descriptor for this task
	 */
	public void execute( Action<?> action ) {

		// for the states, ...
		switch( mState ) {
				
			// sends a setup signal with the probability distribution for every
			// city, so, it can initialize its tables...
			case AS_SETUP:
				
				// create the list of children
				ArrayList<AgentProperties> children = 
								new ArrayList<AgentProperties>();
				
				// get children
				for( AgentProfile ap : mProfile.getChildren()  ) {
					children.add( ap.getProperties() );
				}
				
				// posts the setup signal with the probability distribution
				postSetupSignal( children );
				
				// changes the state to something new...
				mLastState = AgentStateEnum.AS_SETUP;
				mState = AgentStateEnum.AS_WAIT;
				
				// breaks out...
				break;
				
			// the agent is currently idle. Its not doing anything and should be currently
			// in a city. If this is the case, then inform the agent that it arrived in
			// a city, what to do next ?
			case AS_IDLE:
				
				// builds the in-city signal
				InStateObject lObject = createInStateObject();
				
				// posts the in-city signal
				postInStateSignal( lObject );
				
				// prepares the states
				mLastState = AgentStateEnum.AS_IDLE;
				mState = AgentStateEnum.AS_WAIT;
				
				// breaks out...
				break;
				
			case AS_FINISHED:
				break;
				
		}
	}
}

