package epfl.lia.logist.agent.state;

/* importation table */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import uchicago.src.sim.network.Node;
import epfl.lia.logist.agent.AgentManager;
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.plan.GDeliverAction;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.agent.plan.PlanVerifier;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.Message;
import epfl.lia.logist.messaging.MessageDispatcher;
import epfl.lia.logist.messaging.MessageHandler;
import epfl.lia.logist.messaging.MessageTypeEnum;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.action.ActionTypeEnum;
import epfl.lia.logist.messaging.action.MoveAction;
import epfl.lia.logist.messaging.action.PickupAction;
import epfl.lia.logist.messaging.action.PlanAction;
import epfl.lia.logist.messaging.action.StrategyAction;
import epfl.lia.logist.messaging.signal.InCityObject;
import epfl.lia.logist.messaging.signal.InCitySignal;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.messaging.signal.InStateSignal;
import epfl.lia.logist.messaging.signal.InitSignal;
import epfl.lia.logist.messaging.signal.KillSignal;
import epfl.lia.logist.messaging.signal.ResetSignal;
import epfl.lia.logist.messaging.signal.SetupSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.messaging.signal.TaskDeliveredSignal;
import epfl.lia.logist.messaging.signal.TaskRefusedSignal;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.AID;


/**
 * 
 * @author malves
 *
 */
public abstract class AgentState extends MessageHandler {

	/* holds the state of the agent */
	protected AgentStateEnum mState = AgentStateEnum.AS_NONE;

	/* holds the state of the agent */
	protected AgentStateEnum mLastState = AgentStateEnum.AS_NONE;
	
	/* holds the current initialization phase */
	protected AgentStateEnum mPhase = AgentStateEnum.AS_PHASE1;
	
	/* The parent profile of this state */
	protected AgentProfile mProfile = null; 
	
	/* The state of parent agent (container) */
	protected AgentState mParent = null; 	
	
	/* A reference to the agent manager */
	protected AgentManager mAgentMgr = null;
	
	/* The destination ID */
	protected AID mRecipientID = null;
	
	/* The message dispatcher */
	protected MessageDispatcher mDispatcher;
	
	/* Indicates whether the agent is alive or not */
	protected boolean mAlive = true;
	
	/* Indicates the current round */
	protected int mCurrentRound = 1;

	/* Indicates the current round */
	private LogManager mLogMgr = null;
	
	
	/**
	 * The constructor of the agent state
	 */
	protected AgentState( AgentProfile ap, AgentState as ) {
		mProfile = ap;
		mParent = as;
		mDispatcher = MessageDispatcher.getInstance();
		mDispatcher.register( this );
		mAgentMgr = AgentManager.getInstance();
		mLogMgr = LogManager.getInstance();
	}

	
	/**
	 * Defines the recipient of this agent
	 */
	public void setRecipient( AID r ) {
		mRecipientID = r;
	}
	

	/**
	 * Invoked to update the agent state.
	 * 
	 * This method is really the heart of the agent state. It is responsible
	 * for reading mail and performing requested behaviors.
	 * 
	 * Every agent state comprises a state machine that updates only when a
	 * particular message is received. This allows continuing the simulation
	 * even if one agent blocked.
	 * 
	 * This method handle generic signals like INIT, RESET and KILL. ALl other
	 * signals only depend on the nature of the agent.
	 */
	public void step() {
		
		// retrieve the next action
		Action<?> lAction = getNextAction();
		
		// agent must be alive
		if ( !mAlive ) return;

		// makes decisions according to the different
		// states the agent is in...
		switch( mState ) {
		
			// handle the INIT state
			case AS_INIT:
				
				// there are always three main phases in every initialization
				switch( mPhase ) {
				
					// phase 1: post the initialization signal
					case AS_PHASE1:
						postInitSignal( mProfile.getProperties() );
						mPhase=AgentStateEnum.AS_PHASE2;
						break;
						
					// phase 2: wait for the response and announce to barrier
					case AS_PHASE2:
						if ( lAction != null &&
							 lAction.getType() == ActionTypeEnum.AMT_READY ) {
							mPhase=AgentStateEnum.AS_PHASE3;
							mAgentMgr.getBarrier().announce( mObjectID );
						}
						break;
						
					// phase 3: wait for the barrier to unblock or timeout
					case AS_PHASE3:
						if ( !mAgentMgr.getBarrier().blocked() || 
								mAgentMgr.getBarrier().timeout() ) {
							
							// changes the state to SETUP
							mPhase=AgentStateEnum.AS_PHASE1;
							mState=AgentStateEnum.AS_SETUP;
							
							// if the current AgentState is blocked, then
							// remove the agent from AgentMaanger list...
							if ( mAgentMgr.getBarrier().isBlocked(mObjectID) ) {
								mAgentMgr.removeAgent( mProfile );
							}
						}
						break;
				}
				break;
				
			// the reset phase is like every other phase, it necessitates
			// three phases in order to wait for the reset to occur...
			case AS_RESET:
				
				// there are always three main phases in every reset
				switch( mPhase ) {
				
					// phase 1: post the reset signal
					case AS_PHASE1:
						postResetSignal( mCurrentRound );
						mPhase=AgentStateEnum.AS_PHASE2;
						break;
						
					// phase 2: wait for the response and announce to barrier
					case AS_PHASE2:
						if ( lAction != null &&
							 lAction.getType() == ActionTypeEnum.AMT_READY ) {
							mPhase=AgentStateEnum.AS_PHASE3;
							mAgentMgr.getBarrier().announce( mObjectID );
						}
						break;
						
					// phase 3: wait for the barrier to unblock or timeout
					case AS_PHASE3:
						
						// if the barrier unblocked or timedout, then
						// remove blocking agents an continue
						if ( !mAgentMgr.getBarrier().blocked() || 
							  mAgentMgr.getBarrier().timeout() ) {
							
							// after reset, we immediately go to the
							// IDLE state
							mPhase=AgentStateEnum.AS_PHASE1;
							mState=AgentStateEnum.AS_SETUP;
							
							// if the current AgentState is blocked, then
							// remove the agent from AgentMaanger list...
							if ( mAgentMgr.getBarrier().isBlocked(mObjectID) ) {
								mAgentMgr.removeAgent( mProfile );
							}
						}
						break;
				}
				break;
				
			// handle the `kill´ state
			case AS_KILL:
				
				// the current phase in the killing process
				switch( mPhase ) {
				
					// phase 1: post the reset signal
					case AS_PHASE1:
						postKillSignal( 0 );
						mState = AgentStateEnum.AS_KILL;
						mPhase=AgentStateEnum.AS_PHASE2;
						break;

					// phase 2: wait for the response and announce to barrier
					case AS_PHASE2: // wait for the response
						if ( lAction != null &&
							 lAction.getType() == ActionTypeEnum.AMT_READY ) {
							mState = AgentStateEnum.AS_KILL;
							mPhase=AgentStateEnum.AS_PHASE3;
							mAgentMgr.getBarrier().announce( mObjectID );	
						}
						break;
						
					// phase 3: setting the alive state to false
					case AS_PHASE3:
						mAlive = false;
						break;
				}
				break;
				
			// in wait state, we wait for an incoming action response
			case AS_WAIT:
				
				// wait for an incoming action
				if ( lAction == null )
					return;
				
				// does whatever to do depending on the message type
				switch( lAction.getType() ) {

					// we should get moving...
					case AMT_READY:
						if ( mLastState == AgentStateEnum.AS_SETUP )
							mState = AgentStateEnum.AS_IDLE;
						break;
						
					// we should get moving...
					case AMT_MOVE:
						if ( mLastState == AgentStateEnum.AS_IDLE ) {
							String city = ((MoveAction)lAction).getMessage();
							mProfile.setPlan( createMovePlan(city) );
							mState = mLastState;
						}
						break;
				
					// we should pick the task up...
					case AMT_PICKUP:
					{
						Integer lTaskID = ((PickupAction)lAction).getMessage();
						mProfile.setPlan( createPickupPlan(lTaskID) );
						mState = mLastState; // AgentStateEnum.AS_IDLE;
					}
					break;
					
					// we should pick the plan up...
					case AMT_PLAN:
					{
						System.out.println( "Agent " + mProfile.getName() + 
								" received plan..." );
						
						// get the plan from the message
						Plan plan = ((PlanAction)lAction).getMessage();
						
						// only apply the plan if it is valid
						if ( PlanVerifier.isValid(plan,mProfile) ) {
							mProfile.setPlan( plan );
						} else {
							mProfile.setPlan( null );
						}
						
						// returns to the previous state
						mState = mLastState; // AgentStateEnum.AS_IDLE;
					}
					break;
					
					// agent state received a strategy
					case AMT_STRATEGY:
						
						// get the strategy object
						HashMap<String,Plan> plans = 
							((StrategyAction)lAction).getMessage();
						
						// apply the strategy
						applyStrategy( plans );
						
						// returns to the previous state
						mState = mLastState;
						
						// breaks out of here...
						break;
						
					default:
						execute( lAction );
				}

				break;
				
				
			// for all other states, we let the underlying agent handle 
			// all the necessary.
			default:
				execute( lAction );
		}
	}
	
	
	/**
	 * Return the agent profile from its name
	 * @param name
	 * @return
	 */
	private AgentProfile getProfileByName( String name ) {
		for( AgentProfile ap : mProfile.getChildren() ) {
			if ( ap.getName().equals(name) )
				return ap;
		}
		return null;
	}
	
	
	/**
	 * Indicates whether plan meets conditions or not
	 * @param p
	 * @return
	 */
	public boolean planMeetsConditions( Plan p ) {
		return true;
	}
	
	
	/**
	 * Apply a strategy to a set of agents.
	 * 
	 * A strategy is nothing more than a set of agents along with associated
	 * plans.
	 * 
	 * @param plans the set of plans
	 */
	private void applyStrategy( HashMap<String,Plan> plans ) {
		
		// if no plan is found, then return
		if ( plans == null ) return;
		
		// for every agent, send the associated plan
		for( String agentName : plans.keySet() ) {
			
			// gets the associated plan
			Plan associatedPlan = plans.get( agentName );
			
			// if the plan correct ?
			if ( associatedPlan == null )
				continue;
			
			// writes
			debug( "Plan for [" + agentName + "] " +  associatedPlan  );
			
			// get a profile by name
			AgentProfile profile = getProfileByName( agentName );
			
			// send the plan to an agent
			System.out.println( "Sending plan to agent '" + 
					agentName + "'..." );
			
			// if the profile was found, set the plan
			if ( profile != null ) {
				mDispatcher.post( new PlanAction(
						mObjectID, profile.getID(),plans.get(agentName)) );
			} else {
				warning( "No plan for agent [" + agentName + "]" );
			}
		}	
	}
	
	
	/**
	 * Create a plan asking to move
	 * 
	 * Creates a sequence of actions leading to the target city specified in
	 * the params. This function only accepts moving to neighbourng cities.
	 * 
	 * @param target the name of the city towards which we should go
	 * 
	 * @return a new plan for the AgentProfile to use
	 * 
	 * @note this function is only use with non-planing agents like reactive
	 * agents.
	 */
	protected Plan createMovePlan( String target ) {
		
		// gets the topological object first
		Topology topology = Topology.getInstance();
		
		// get the current and target cities
		City targetCity = topology.getCity( target );
		City currentCity = mProfile.getCurrentCity();
		
		// verify the validity of the action
		if ( !topology.neighbors(currentCity,targetCity) )
			return null;
		
		// if the city exists, then create a sequence of 
		// actions leading to this city
		if ( targetCity != null && !currentCity.match(targetCity) ) {
			
			// thes the distance between cities
			double distance = topology.getDistance( targetCity, currentCity );
			
			// create a new sequence of actions actually consisting in
			// as single action
			IGenericAction[] actionArray = new IGenericAction[] {
					new GMoveAction( targetCity,  distance )
			};
			
			// return the newly created plan
			return new Plan( actionArray );
		}
		
		// nothing to return
		return null;
	}
	
	
	/**
	 * Create a pickup plan
	 * 
	 * Creates a sequence of actions consisting in pickup up current task
	 * and going to the delivery city and deliver the task. 
	 * 
	 * @param target
	 * 
	 * @return a new plan for the AgentProfile to use
	 * 
	 * @note this function is only use with non-planing agents like reactive
	 * agents.
	 */
	protected Plan createPickupPlan( Integer taskID ) {
		
		// retrieve the managers
		TaskManager taskMgr = TaskManager.getInstance();
		Topology topology = Topology.getInstance();
		
		// retrieves the task from its ID
		Task t = taskMgr.getTaskFromID( taskID );
		if ( t == null ) return null;
		
		// retrieve the delivery city
		City deliveryCity = topology.getCity( t.getDeliveryCity() );
		if ( deliveryCity == null )
			return null;
		
		// builds the complete plan
		ArrayList<IGenericAction> lActionList = new ArrayList<IGenericAction>();
		
		// first adds the pickup action
		lActionList.add( new GPickupAction(t) );
		
		// the adds the steps till the destinatoin
		City startCity = mProfile.getCurrentCity();
		City targetCity = deliveryCity;
		while( startCity != targetCity ) {
			startCity = topology.moveOnShortestPath( startCity, targetCity );
			double distance = 0.0;
			try {
				distance = topology.getDistance( mProfile.getCurrentCity(), startCity );
			}catch( Exception e ) {}
			lActionList.add( new GMoveAction(startCity, distance) );
		}
		
		// adds the delivery action 
		lActionList.add( new GDeliverAction(t) );
	
		// returns the new plan
		return new Plan( lActionList );
		
	}
	
	/** 
	 * This method notify agents that a new task batch was created
	 */
	public abstract void notifyTaskBatchCreation();
	
	
	/**
	 * Indicates whether agent is alive or not
	 * 
	 * This method indicates whether an agent is still alive or not. Agents are
	 * not alive anymore when they are asked to kill themselves.
	 */
	public boolean isAlive() {
		return mAlive;
	}
	
	
	/**
	 * Executes some inner behavior
	 * 
	 * This method allows executing behaviors that are not common to the 
	 * different sorts of agents. This is particularly the case if the agent is
	 * in states different from kill, init or reset...
	 * 
	 * @param action The current action generated in this state
	 */
	protected abstract void execute( Action<?> action );
	
	
	/**
	 * Notify an agent that a signal was send towards corresponding entity
	 * 
	 * This method notifies the state of the agent that another agent sent a
	 * message towards the corresponding agent entity.
	 * 
	 * @param signal The signal which was sent
	 */
	public abstract void notifySignal( Signal<?> signal );
	
	
	/**
	 * Set the current state of the agent
	 * 
	 * Change the current state to the new one passed as a parameter. This is a
	 * sometimes desirable behavior to allow external objects to modify the 
	 * current state of this class instance.
	 * 
	 * @param as The new state
	 */
	public void setState( AgentStateEnum as ) {
		mState = as;
		mPhase = AgentStateEnum.AS_PHASE1;
	}
	
	
	/**
	 * Define the current round
	 * 
	 * This method indicates to agent states in which round they currently
	 * are in
	 * 
	 * @param round the round number
	 */
	public void setRound( int round ) {
		this.mCurrentRound = round;
	}
	
	
	/**
	 * Notifies the parent when some action has been received by
	 * the child.
	 * 
	 * @param action The action that parent should be notified
	 */
	protected void notifyParent( Action<?> action ) {	
		// TODO: send notifications to the parent..
	}
	
	
	//--------------------------------------------------------------------------
	// S i g n a l   M a n a g e m e n t   F u n c t i o n s
	//--------------------------------------------------------------------------
	
	/**
	 * Get the next action available in associated message box
	 * 
	 * Get the next action from the action list. This function discards all
	 * messages that are not actions. This prevents agents to do things they
	 * are not supposed to do.
	 * 
	 * @return the action which was previously posted by an agent entity.
	 */
	protected Action<?> getNextAction() {

		// stack variables
		Message<?> lNextMsg = null;
		
		// while there are messages
		while( (lNextMsg=mDispatcher.retrieve(this)) != null ) {
			if ( lNextMsg.getMsgType() == MessageTypeEnum.MGT_ACTION ) {
				return (Action<?>)lNextMsg;
			}
		}
		
		// returns nothing
		return null;
	}

	
	/**
	 * Post an initialization message to the recipient agent.
	 * 
	 * This method invite the associated agent to initialize themselves.
	 * 
	 * @param ap an object holding the attributes of the agent, like speed,
	 * cost-per-km, etc...
	 */
	public void postInitSignal( AgentProperties ap ) {
		mDispatcher.post( new InitSignal(mObjectID,mRecipientID,ap) );
	}
	
	
	/**
	 * Post a reset message to the recipient agent.
	 * 
	 * This method asks the agent to prepare itself for a round change. The 
	 * round number is sent as a parameter.
	 * 
	 * @param round the current round number
	 */
	public void postResetSignal( int round ) {
		mDispatcher.post( 
				new ResetSignal(mObjectID, mRecipientID, new Integer(round)) );
	}
	
	
	/**
	 * Post a request for the agent to kill himself. 
	 * 
	 * This method sends a kill signal to recipient. A request to kill is sent
	 * at the end of the simulation or when an agent blocked.
	 * 
	 * @param reason the reason which is invoked to kill the agent
	 */
	public void postKillSignal( int reason ) {
		mDispatcher.post( 
				new KillSignal(mObjectID, mRecipientID, new Integer(reason)) );
	}
	
	
	/**
	 * Post a request for the agent to set its state up.
	 * 
	 * This method posts a signal to ask associated agent to set its state up.
	 * The object which is sent along with the message depends on the type of
	 * agent.
	 * 
	 * @param obj the object sent to set agent up
	 */
	public void postSetupSignal( Object obj ) {
		mDispatcher.post( new SetupSignal(mObjectID,mRecipientID,obj) );
	}
	

	/**
	 * Informs the recipient agent that the task was refused..
	 * 
	 * This method informs the associated agent that the task it asked for is no
	 * longer available.
	 * 
	 * @param tid the identifier of the refused task
	 */
	public void postTaskRefusedSignal( int tid ) {
		mDispatcher.post( new TaskRefusedSignal(mObjectID,mRecipientID,tid) );
	}

	
	/**
	 * Informs the recipient agent that the task was delivered...
	 * 
	 * This method informs the associated agent that the task was correctly
	 * delivered!
	 * 
	 * @param tid a task identifier
	 */
	public void postTaskDeliveredSignal( int tid ) {
		mDispatcher.post( new TaskDeliveredSignal(mObjectID,mRecipientID,tid) );
	}
	
	
	/**
	 * Method overloading for telling an agent it reached a city.
	 * 
	 * This method sends an object which contains necessary data about a
	 * single city. The InCityObject contains the name of the city, the task(s)
	 * in current city as well as neighboring city names.
	 * 
	 * @param obj an object holding the state of a city
	 */
	public void postInCitySignal( InCityObject obj ) {
		mDispatcher.post( new InCitySignal(mObjectID,mRecipientID,obj) );
	}
	
	
	/**
	 * Method overloading for telling an agent it reached a city.
	 * 
	 * This method informs the associated agent that the world is in a 
	 * particular state. The InStateObject is a collection of InCityObject
	 * objects.
	 * 
	 * @param obj an object holding the state of the world
	 */
	public void postInStateSignal( InStateObject obj ) {
		mDispatcher.post( new InStateSignal(mObjectID,mRecipientID,obj) );
	}
	

	//--------------------------------------------------------------------------
	// L o g   M a n a g e m e n t   F u n c t i o n s
	//--------------------------------------------------------------------------
	
	
	/**
	 * Use the log manager to display a debug message
	 * 
	 * @param msg the message string
	 */
	public void debug( String msg ) {
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG, msg );
	}
	
	
	/**
	 * Use the log manager to display a informative message
	 * 
	 * @param msg the message string
	 */
	public void info( String msg ) {
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, msg );
	}
	
	
	/**
	 * Use the log manager to display a warning message
	 * 
	 * @param msg the message string
	 */
	public void warning( String msg ) {
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, msg );
	}
	
	
	/**
	 * Use the log manager to display an eror message
	 * 
	 * @param msg the message string
	 */
	public void error( String msg ) {
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_ERROR, msg );
	}
	
	
	//--------------------------------------------------------------------------
	// S i g n a l   C r e a t i o n   M e t h o d s
	//--------------------------------------------------------------------------
	
	/**
	 * Helper function that creates an InState object to pass
	 * as an argument for the <in-state> signal
	 */
	protected InStateObject createInStateObject() {
		
		Topology topo = Topology.getInstance();
		TaskManager taskMgr = TaskManager.getInstance();
		
		// create a new object
		InStateObject iso = new InStateObject();
		
		// first of all, set current city
		iso.CurrentCity = mProfile.getCurrentCity();
		
		// them, sets a pointer to the topology object
		iso.Graph = topo;
		
		// then, copies the map of tasks
		iso.Tasks = new HashMap<City,ArrayList<Task>>();
		for( City c : topo.getCities().values() ) {
			
			// create a new task list
			ArrayList<Task> taskList = new ArrayList<Task>();
			
			// get the original task list
			ArrayList<Task> originalTaskList = 
					taskMgr.getPickupTasklist( c.getNodeLabel() );
			
			// for every task in that list, copies the items
			for ( Task t : originalTaskList ) {
				taskList.add( t.clone() );
			}
			
			// puts the new task list associated to the current city
			iso.Tasks.put( c, taskList );
		}
		
		// sends the list of assigned tasks
		iso.AssignedTasks = new ArrayList<Task>();
		ArrayList<Task> allocatedTaskList = taskMgr.getAllocatedTasklist(this.mObjectID);
		if ( allocatedTaskList !=null ) {
			for ( Task t : allocatedTaskList ) {
				if ( t != null )
					iso.AssignedTasks.add( t.clone() );
			}
		}
		
		// adds the vehicles to the object
		iso.Vehicles = new ArrayList<AgentProperties>();
		for( AgentProfile ap : mProfile.getChildren() ) {
			iso.Vehicles.add( ap.getProperties() );
		}
		
		// returns the iso object
		return iso;
	}
	
	
	/**
	 * Helper function that creates an InCity object to pass
	 * as an argument for the <in-city> signal
	 */
	protected InCityObject createInCityObject( boolean oneTask ) {
		
		// retrieves the current city
		City currentCity = mProfile.getCurrentCity();
		
		// creates a new object
		InCityObject lObj = new InCityObject();
		
		// initializes all entries of the signal
		lObj.Name = currentCity.getNodeLabel();
		lObj.Neighbors = new ArrayList<String>();
		lObj.Tasks = new ArrayList<TaskDescriptor>();
		
		// adds the neighbours of this city
		ArrayList<?> lAvailableDestinations = 
						Topology.getInstance().getDestinations( currentCity );
		for( Object c : lAvailableDestinations ) {
			lObj.Neighbors.add( ((Node)c).getNodeLabel() );
		}
		
		// adds a task ...
		Iterator lItor = TaskManager.getInstance().getPickupTasklist(
					currentCity.getNodeLabel()).iterator();

		// are we in one task mode ?
		if ( oneTask ) {
			if ( lItor.hasNext() ) 
				lObj.Tasks.add( ((Task)lItor.next()).getDescriptor() );
		} else {
			while( lItor.hasNext() ) {
				lObj.Tasks.add( ((Task)lItor.next()).getDescriptor() );
			}
		}
		
		// sends a signal towards the agent entity
		return lObj;
	}
}

