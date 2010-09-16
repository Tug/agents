package epfl.lia.logist.agent.state.deliberative;

/* importation table */
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.VehicleAgentState;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.TaskGenerator;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.task.distribution.ITaskDistribution;


/**
 * 
 * @author malves
 *
 */
public class DeliberativeVehicleState extends VehicleAgentState {

	/**
	 * Constructor of the state
	 * @param ap
	 * @param as
	 */
	public DeliberativeVehicleState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
			
	
	/**
	 * Executes the behavior of the agent
	 */
	public void execute( Action<?> action ) {

		// for the states, ...
		switch( mState ) {
				
			// sends a setup signal with the probability distribution for every
			// city, so, it can initialize its tables...
			case AS_SETUP:
				
				// retrieves the probability distribution
				TaskManager lTaskMgr = TaskManager.getInstance();
				TaskGenerator lTaskGen = lTaskMgr.getGenerator();
				ITaskDistribution lTaskDistr = lTaskGen.getDistribution();
				ProbabilityDistribution lProbDistr = lTaskDistr.getProbabilityDistribution();
				
				// posts the setup signal with the probability distribution
				postSetupSignal( lProbDistr );
				
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
				
		}
	}
}

