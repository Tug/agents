package epfl.lia.logist.agent.state.auction;

import java.util.ArrayList;

import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.plan.AgentActionTypeEnum;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.CompanyAgentState;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.action.ActionTypeEnum;
import epfl.lia.logist.messaging.action.BidAction;
import epfl.lia.logist.messaging.signal.AuctionNotificationObject;
import epfl.lia.logist.messaging.signal.AuctionSetupObject;
import epfl.lia.logist.messaging.signal.AuctionWonSignal;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.task.distribution.ITaskDistribution;

public class BidderCompanyState extends CompanyAgentState {

	private ArrayList<Integer> mListOfTasks = null;
	
	
	/**
	 * Constructor of the company state
	 * 
	 * @param ap the company profile object
	 * @param as the parent state object
	 */
	public BidderCompanyState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
	
	
	/**
	 * Plan meets conditions if the picked tasks are included in the 
	 * current list of tasks. 
	 */
	public boolean planMeetsConditions( Plan p ) {
		
		// first, we must clone the plan
		Plan p1 = (Plan)p.clone();
		
		// then, only get the pickup elements
		while( p1.hasMoreElements() ) {
			
			// get the next element
			IGenericAction ga = p1.nextElement();
			if ( ga.getType() == AgentActionTypeEnum.PICKUP ) {
				
				// get the action immediately
				GPickupAction gpa = (GPickupAction)ga;

				// try getting the task
				Task t = gpa.getTask();
				if ( t==null ) return false;
				
				// get the id of the task
				Integer taskID = t.getID();

				// does the list of picked tasks contain the task iD ?
				if ( !mListOfTasks.contains( taskID )  ) {
					warning( "["+mProfile.getName()+"] has picked a task " +
							"which doesn't exist or which belongs to another " +
							"agent ! Releasing tasks !" );
					return false;
				}
			}
		}
		
		// everything seems correct here...
		return true;
	}
	
	
	/**
	 * Notify thing company state that another company has sent a signal on 
	 * behalf of this one !
	 */
	public void notifySignal( Signal<?> signal ) {
		switch( signal.getType()) {
		
			// this signal indicates a task is currently in auction
			case SMT_ASKBID:
				break;
				
			// this signal indicates the auction item was won
			case SMT_AUCTION_WON:
				
				// get the notification object
				AuctionNotificationObject obj = 
					((AuctionWonSignal)signal).getMessage();
				
				// add this task to the list of available tasks
				mListOfTasks.add( obj.Task.ID );
				
				// breaks out of here...
				break;
				
			// this signal indicates the auction item was lost
			case SMT_AUCTION_LOST:
				break;
				
			// this signal indicates the beginning of the auction
			case SMT_AUCTION_START:
				
				// create the list of tasks
				if ( mListOfTasks==null )
					mListOfTasks = new ArrayList<Integer>();
				
				// clears the list out
				mListOfTasks.clear();
				
				// change the current state
				mState = AgentStateEnum.AS_AUCTION_START;
				break;
				
			// this signal indicates the end of the auction
			case SMT_AUCTION_END:
				mState = AgentStateEnum.AS_AUCTION_END;
				break;
		}
	}
	
	@Override
	protected void execute(Action<?> action) {
		
		switch( mState ) {
			
			// starting everything
			case AS_SETUP:
				
				// creates the object holding necessary variables 
				AuctionSetupObject aso = new AuctionSetupObject();
				
				// create the list of vehicles
				aso.Vehicles = new ArrayList<AgentProperties>();
				for( AgentProfile ap : mProfile.getChildren() ) 
					aso.Vehicles.add( ap.getProperties() );
				
				// get an instance of the topology object
				aso.Topology = Topology.getInstance();
				
				// create the list of children
				ITaskDistribution taskDistribution =
					TaskManager.getInstance().getGenerator().getDistribution();
				
				
				// get the current probability distribution
				aso.Probabilities = taskDistribution.getProbabilityDistribution();
				
				// posts the setup signal with the probability distribution
				postSetupSignal( aso );
				
				// changes the state to something new...
				mLastState = AgentStateEnum.AS_SETUP;
				mState = AgentStateEnum.AS_WAIT;
				
				// breaks out...
				break;
	
				
			// invoked when agent is idle
			case AS_IDLE:
				
				// breaks out of here....
				break;
				
			// invoked when the auction starts
			case AS_AUCTION_START:
				
				// must receive the signal to go to the next state
				if ( action != null &&
					 action.getType()==ActionTypeEnum.AMT_READY ) {
					mState = AgentStateEnum.AS_AUCTION_ITEM;
				}
				
				// breaks out of here...
				break;
				
			// while we are in idle state, we receive bids
			case AS_AUCTION_ITEM:
				
				// the action must exist
				if ( action == null ) return;
				
				// is the received action a bid ?
				if ( action.getType() == ActionTypeEnum.AMT_BID ) {
					
						// get the bid action
						BidAction ba = ((BidAction)action);
						
						// builds the new message
						ba = new BidAction( 
								getObjectID(),
								mProfile.getParent().getID(), 
								ba.getMessage());
						
						// forward the message to the auction agent 
						mDispatcher.post( ba );
				}
				
				// breaks out of here...
				break;
				
			// while we are in end mode
			case AS_AUCTION_END:
				
				// if the agent responds correctly, then
				// goes to a stub state
				if ( action != null &&
					 action.getType() == ActionTypeEnum.AMT_READY ) {
					
					// builds the in-city signal
					InStateObject lObject = createInStateObject();
					
					// get an instance of the task manager
					TaskManager taskMgr = TaskManager.getInstance();
					
					lObject.AssignedTasks.clear();
					for ( Integer tid : this.mListOfTasks ) {
						
						// get a task from its ID
						Task t = taskMgr.getTaskFromID( tid );
						
						// if the task exists, then assign it
						if ( t != null ) {
							lObject.AssignedTasks.add( t );
						} else {
							warning( "Task of ID " + tid + " doesn't exist !" );
						}
					}
					// posts the in-city signal
					postInStateSignal( lObject );
					
					// prepares the states
					mLastState = AgentStateEnum.AS_FINISHED;
					mState = AgentStateEnum.AS_WAIT;
					
					// breaks out...
					break;
				}
				
				break;
				
			// this is a stub state for waiting....
			case AS_FINISHED:
				break;
		}
	}

}
