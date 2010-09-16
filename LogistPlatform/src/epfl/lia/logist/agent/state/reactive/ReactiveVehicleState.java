package epfl.lia.logist.agent.state.reactive;

/* import table */
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.VehicleAgentState;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.action.ActionTypeEnum;
import epfl.lia.logist.messaging.signal.InCityObject;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.TaskGenerator;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.task.distribution.ITaskDistribution;


/**
 * This class represents the state of a reactive vehicle agent. It controls
 * the position of the agents as well as all properties linked to it. It holds
 * the state of a single agent and communicates with the entity.
 */
public class ReactiveVehicleState extends VehicleAgentState {	
	
	/* This variable is a reference */
	private long mStartTime = 0;
	
	/**
	 * Constructor of the reactive agent state.
	 */
	public ReactiveVehicleState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
			
	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.agent.state.AgentState#execute(epfl.lia.logist.messaging.action.Action)
	 */
	@Override
	protected void execute(Action<?> action) {
		
		// for the states, ...
		switch( mState ) {
				
			// sends a setup signal with the probability distribution for every
			// city, so, it can initialize its tables...
			case AS_SETUP:
			{
				// retrieves the probability distribution
				TaskManager lTaskMgr = TaskManager.getInstance();
				TaskGenerator lTaskGen = lTaskMgr.getGenerator();
				ITaskDistribution lTaskDistr = lTaskGen.getDistribution();
				ProbabilityDistribution lProbDistr = lTaskDistr.getProbabilityDistribution();
				
				// posts the setup signal with the probability distribution
				postSetupSignal( lProbDistr );

				// changes the state to something new...
				mLastState = AgentStateEnum.AS_SETUP;
				mState = AgentStateEnum.AS_THREADED_WAIT;
				
				// sets the starting time
				mStartTime = System.currentTimeMillis();
			}	
			
			// breaks out of here...
			break;
				
			// put ourselves in threaded wait
			case AS_THREADED_WAIT:
				
				// if we receive the correct message ...
				if ( action!=null && action.getType() == ActionTypeEnum.AMT_READY ) {
					mState = AgentStateEnum.AS_IDLE;
				} else if ( System.currentTimeMillis()-mStartTime >= mProfile.getSetupTimeout() ) {
					warning( "The agent has passed the setup time !" );
					mAgentMgr.removeAgent( mProfile );
				}
				// breaks out...
				break;
				
			// the agent is currently idle. Its not doing anything and should be currently
			// in a city. If this is the case, then inform the agent that it arrived in
			// a city, what to do next ?
			case AS_IDLE:
			{
				// builds the in-city signal
				InCityObject lObject = createInCityObject( true );
				
				// posts the in-city signal
				postInCitySignal( lObject );
				
				// prepares the states
				mLastState = AgentStateEnum.AS_IDLE;
				mState = AgentStateEnum.AS_WAIT;
			}	

			// breaks out...
			break;	
		}
	}
}

