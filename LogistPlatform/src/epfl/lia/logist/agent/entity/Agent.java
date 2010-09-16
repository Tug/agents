package epfl.lia.logist.agent.entity;

/* importation table */
import java.util.HashMap;

import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.BehaviorResponseTypeEnum;
import epfl.lia.logist.agent.behavior.response.BidBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.MoveBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.PickupBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.PlanBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.StrategyBehaviorResponse;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.Message;
import epfl.lia.logist.messaging.MessageDispatcher;
import epfl.lia.logist.messaging.MessageHandler;
import epfl.lia.logist.messaging.MessageTypeEnum;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.action.BidAction;
import epfl.lia.logist.messaging.action.MoveAction;
import epfl.lia.logist.messaging.action.PickupAction;
import epfl.lia.logist.messaging.action.PlanAction;
import epfl.lia.logist.messaging.action.ReadyAction;
import epfl.lia.logist.messaging.action.StrategyAction;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.messaging.signal.SignalTypeEnum;
import epfl.lia.logist.tools.AID;



/**
 * An agent represents a single moving entity in the simulation. An agent can either
 * be a vehicle or a company in the context of logistics. It cannot exist alone and
 * is controled by an agent state entity. For all behaviors, an agent state has an
 * internal state machine specifying which moves the agent's behavior can operate. The
 * agent state are drawable entitites.
 *
 */
public class Agent extends MessageHandler 
		implements Runnable {

	/* A map of behaviors */
	private HashMap<SignalTypeEnum,Behavior> mMapOfBehaviors = null; 
	
	/* A private instance of the dispatcher */
	private MessageDispatcher mDispatcher = null;
	
	/* Indicates whether the agent is active */
	private boolean mbActive = true;
	
	/* The ID of the corresponding state */
	private AID mStateID = null;
	
	/* The thread for this object */
	private Thread mThread = null;
	
	
	/**
	 * The constructor of the class
	 * 
	 * Initializes the internal state of the class.
	 */
	public Agent( AID stateID ) {
		super();
		mStateID = stateID;
		mDispatcher = MessageDispatcher.getInstance();
		mMapOfBehaviors = new HashMap<SignalTypeEnum,Behavior>();
		mDispatcher.register(this);
	}
	
	
	/**
	 * The subclassed method of class Runnable 
	 */
	public void run() {
		
		// tries everything
		try {
			
			// tant que l'agent est encore en vie...
			while ( mbActive && mDispatcher != null ) {
			
				// checks to see if there is a message
				if ( mDispatcher.check(this) ) {
				
					// retrieves the next message
					Message msg = mDispatcher.retrieve( this );
					
					// we only care about signals
					if ( msg != null &&
						 msg.getMsgType() == MessageTypeEnum.MGT_SIGNAL ) {
						
						// retrieves the signal
						Signal signal = (Signal)msg;
					
						// dispatches the signal
						dispatchSignal( signal );
						
						// if the message is a kill message
						if ( signal.getType() == SignalTypeEnum.SMT_KILL )
							mbActive = false;
					}
				}
				
				// gives the hand to another thread
				Thread.yield();
			}	
		} catch( Exception e2 ) {
			LogManager.getInstance().log( LogManager.DEFAULT, 
					LogSeverityEnum.LSV_INFO,  "Agent exception: " + 
					e2.getMessage() );
			e2.printStackTrace();
		} finally {
			LogManager.getInstance().log( LogManager.DEFAULT, 
					LogSeverityEnum.LSV_INFO, "Thread has been killed !!!" );
		}
	}
	
	
	/**
	 * Indicates whether agent is active or not
	 */
	public boolean isActive() {
		return mbActive;
	}
	
	
	/**
	 * Starts the thread for this agent
	 */
	public void start( String name ) {
		mThread = new Thread( this, name );
		mThread.start();
	}
	
	
	/**
	 * Dispatches the incoming signal to correct behavior 
	 */
	private void dispatchSignal( Signal m ) {
		
		// retrieves the corresponding behavior
		Behavior behavior = mMapOfBehaviors.get( m.getType() );
		
		// if the behavior exists, then execute it
		if ( behavior != null ) {
			
			// tries executing the behavior
			try {
				
				// executes the behavior
				IBehaviorResponse br = behavior.execute( m );
				
				// handles the response
				if ( br.getType() != BehaviorResponseTypeEnum.BRT_EMPTY )					
					handleResponse( br );
				
			// catches the exceptions, for example, when behaviors are
			// not implemented...
			} catch( BehaviorNotImplementedError err1 ) {
				System.err.println( "Err1: " + err1 );
			} catch( BehaviorExecutionError err2 ) {
				System.err.println( "Err2: " + err2 );
			}
		}
	}
	
	
	/**
	 * Handles the response returned by the behavior
	 *
	 * @param br The response of the behavior
	 */
	private void handleResponse( IBehaviorResponse br ) {
		
		// handles the response according to the type of 
		// the response returned by the behavior
		switch( br.getType() ) {
			
			// if the response is a ready response, then send a ready action
			case BRT_READY:
				postMessage( new ReadyAction(mObjectID,mStateID) );
				break;
				
			// if the responsne if a pickup response, then post a pickup action
			// message. the state should test if the task corresponds to one
			// of the tasks passed in arguments.
			case BRT_PICKUP:
				postMessage( 
					new PickupAction( mObjectID, mStateID,
					new Integer(((PickupBehaviorResponse)br).getTaskID())) );
				break;
				
			// This response contains the destination to move to
			case BRT_MOVETO:
				postMessage(  new MoveAction(
						mObjectID, mStateID,
						((MoveBehaviorResponse)br).getDestination() ) );
				break;
			
			// This response contains a complete plan
			case BRT_PLAN:
				postMessage( new PlanAction(
						mObjectID, mStateID,
						((PlanBehaviorResponse)br).getPlan() ) );
				break;
				
			// This response contains a bid for current task
			case BRT_BID:
				postMessage( new BidAction(
						mObjectID, mStateID, 
						((BidBehaviorResponse)br).getBid()) );
				break;
				
			// This response contains a strategy for sub-agents
			case BRT_STRATEGY:
				postMessage( new StrategyAction(
						mObjectID, mStateID, 
						((StrategyBehaviorResponse)br).getPlans()) );
				break;
				
		}
	}
	
	
	/**
	 * Helper method for posting an action message.
	 */
	private void postMessage( Action<?> action ) {
		MessageDispatcher.getInstance().post( action );
	}
	
	
	/**
	 * Registers a behavior for a particular signal 
	 */
	public void registerBehavior( SignalTypeEnum st, Behavior b ) {
		mMapOfBehaviors.put( st, b );
	}


	/**
	 * Unregisters a behavior for a particular signal 
	 */
	public void unregisterBehavior( SignalTypeEnum st ) {
		if ( mMapOfBehaviors.containsKey(st) )
			mMapOfBehaviors.remove(st);
	}
}