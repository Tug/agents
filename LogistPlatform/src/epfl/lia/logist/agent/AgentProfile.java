package epfl.lia.logist.agent;

/* importation table */
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.BehaviorDescriptor;
import epfl.lia.logist.agent.behavior.DefaultBehavior;
import epfl.lia.logist.agent.entity.Agent;
import epfl.lia.logist.agent.plan.GDeliverAction;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.auction.AuctionCompanyState;
import epfl.lia.logist.agent.state.auction.BidderCompanyState;
import epfl.lia.logist.agent.state.auction.BidderVehicleState;
import epfl.lia.logist.agent.state.centralized.CentralizedCompanyState;
import epfl.lia.logist.agent.state.centralized.CentralizedVehicleState;
import epfl.lia.logist.agent.state.deliberative.DeliberativeCompanyState;
import epfl.lia.logist.agent.state.deliberative.DeliberativeVehicleState;
import epfl.lia.logist.agent.state.reactive.ReactiveCompanyState;
import epfl.lia.logist.agent.state.reactive.ReactiveVehicleState;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.exception.AgentCreationException;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.signal.SignalTypeEnum;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.AID;
import epfl.lia.logist.tools.LogistClassLoader;
import epfl.lia.logist.tools.LogistGlobals;
import epfl.lia.logist.tools.interpolators.PointInterpolator;


/**
 * A  winner agent profile...
 */
public class AgentProfile {

	/* The profile state enum */
	private enum ProfileStateEnum { MOVING, SERVICING, DELIVERING, PLANNING };
	
	/* The agent entity */
	private Agent mAgent = null;

	/* The identifier of the agent */
	private AID mAgentID = null;

	/* The behavior of this agent */
	private AgentBehaviorEnum mBehavior = null;

	/* The capacity of the vehicle */
	protected double mCapacity = 0.0;

	/* The cost per km of the vehicle */
	protected double mCostPerKm = 0.0;

	/* The representation of the display */
	private AgentDisplayable mDisplayable = null;

	/* Keeps the history for this agent */
	private AgentHistory mHistory = null;

	/* The home location of the agent */
	protected City mHome = null;

	/* The load of the vehicle */
	protected double mLoad = 0.0;

	/* The private instance of the log manager */
	private LogManager mLog = null;

	/* The name of the agent */
	public String mName = null;

	/* The parent profile */
	private AgentProfile mParent;

	/* The speed of the vehicle */
	protected double mSpeed = 0.0;

	/* The internal state of the agent */
	private AgentState mState;

	/* The total cost so far */
	private double mTotalCost;

	/* The total driven distance so far */
	private double mTotalDistance;

	/* The total reward so far */
	private double mTotalReward;

	/* The type of this agent */
	private AgentTypeEnum mType;
	
	/* The current city the agent is in */
	private City mCurrentCity;
	
	/* The controller that makes agent move */
	private PointInterpolator mInterpolator;
	
	/* The topology object */
	private Topology mTopology = null;
	
	/* The task manager */
	private TaskManager mTaskMgr = null;

	/* Keeps the current round */
	private int mCurrentRound = 0;
	
	/* Keep a reference to the current plan */
	private Plan mCurrentPlan = null;
	
	/* Keeps a reference to the globals */
	private LogistGlobals mGlobals = null;
	
	/* Keeps a list of all children */
	private ArrayList<AgentProfile> mChildren = null;
	
	/* Time variables for movement */
	private long mStartTime;
	
	/* The current route distance */
	private double mRouteDistance;
	
	/* The city to which we should move to */
	private City mTargetCity;
	
	/* The city where to deliver a task */
	private City mDeliveryCity = null;
	
	/* The current state of the State Machine  */
	private ProfileStateEnum mCurrentState = ProfileStateEnum.SERVICING;
	
	/* The last state of the state machine */
	private ProfileStateEnum mLastState = ProfileStateEnum.SERVICING;
	
	
	/**
	 * The constructor of the agent.
	 * 
	 * Initialize all internal instances. 
	 */
	public AgentProfile( LogistGlobals lg ) {
		mLog = LogManager.getInstance();
		mInterpolator = new PointInterpolator();
		mChildren = new ArrayList<AgentProfile>();
		mTopology = Topology.getInstance();
		mTaskMgr = TaskManager.getInstance();
		mGlobals = lg;
	}


	//--------------------------------------------------------------------------
	// A G E N T   S E R V I C E S
	//--------------------------------------------------------------------------
	
	
	/**
	 * Initialize the agent profile
	 * 
	 * This method initializes initialize the agent. It first starts the 
	 * agent entity thread and sets the state INIT.
	 */
	public void init() {
		mAgent.start( this.mName );
		mState.setState( AgentStateEnum.AS_INIT );
	}

	
	/**
	 * Kill the agent profile
	 * 
	 * This method invokes the state and entity to kill the agent. The history
	 * is flushed to disk.
	 */
	public void kill() {
		mState.setState( AgentStateEnum.AS_KILL );
		if ( mParent != null ) mParent.notifyParentForKilling( this );
		mHistory.flushRound( mCurrentRound );
		mHistory.shutdown();
	}

	
	/**
	 * Reset the agent profile
	 * 
	 * This method resets the internal state of the profile. it first resets
	 * the state of the agent by sending a RESET signal to associated behavior.
	 * It flushes the history and selects a new home for the vehicles.
	 * 
	 * @param round the current round number
	 */
	public void reset( int round ) {
		
		// sets the current state of the agent state
		mState.setState( AgentStateEnum.AS_RESET );
		
		// sets the current round 
		mState.setRound( round );
		
		// flushes the history 
		mHistory.flushRound( round-1 );
		
		/* no displayable? then we have a company agent... */ 
		if ( mDisplayable != null ) {
			
			// chooses a different city 
			City oldCity = mHome;
			while( mHome.match(oldCity) ) { mHome = mTopology.getRandomCity(); }
			
			// defines the current city
			mCurrentCity = mHome;
			mDisplayable.move( new Point(mHome.getX(),mHome.getY()) );
		}
		
		/* sets the current state in servicing */
		mCurrentState = ProfileStateEnum.SERVICING;
	}
	
	/**
	 * Indicate whether agent is alive or not.
	 * 
	 * This method returns true if the agent is still alive. One agent is 
	 * alive only when the state and entity are both alive !
	 * 
	 * @return \b true if the agent is alive, \b false otherwise
	 */
	public boolean isAlive() {
		return mState.isAlive() && mAgent.isActive();
	}
	
	
	/**
	 * Get the type of the agent
	 */
	public AgentTypeEnum getType() {
		return mType;
	}
	
	
	/**
	 * Get the Id of the profile
	 */
	public AID getID() {
		return mAgentID;
	}
	
	
	/**
	 * Returns the globals
	 */
	public LogistGlobals getGlobals() {
		return mGlobals;
	}
	
	
	/**
	 * Return the allocated time to set agent up.
	 * @return
	 */
	public long getSetupTimeout() {
		return mGlobals.SetupTimeout;
	}
	
	
	/**
	 * Return current properties under the form of a single object
	 * 
	 * This method constructs an AgentProperties object that contains the 
	 * properties of the agent.
	 */
	public AgentProperties getProperties() {
		AgentProperties ap = new AgentProperties();
		ap.Capacity = this.mCapacity;
		ap.CostPerKm = this.mCostPerKm;
		ap.Load = this.mLoad;
		ap.Speed = this.mSpeed;
		ap.Name = this.mName;
		ap.Home = this.mCurrentCity.getNodeLabel();
		return ap;
	}
	
	
	//--------------------------------------------------------------------------
	// G E T T E R S   A N D   S E T T E R S
	//--------------------------------------------------------------------------
	
	/**
	 * Return a reference to the agent associated to this profile
	 */
	public Agent getAgent() {
		return mAgent;
	}
	
	
	/**
	 * Return a reference to the current city
	 */
	public City getCurrentCity() {
		return mCurrentCity;
	}
	
	
	/**
	 * Return a reference to the displayable part of this agent
	 */
	public AgentDisplayable getDisplayable() {
		return mDisplayable;
	}
	
	
	/**
	 * Return the name of the agent
	 */
	public String getName() {
		return mName;
	}
	
	
	/**
	 * REturn a reference to the parent
	 */
	public AgentProfile getParent() {
		return this.mParent;
	}
	
	
	/**
	 * Return the state
	 */
	public AgentState getState() {
		return this.mState;
	}
	
	/**
	 * Set a new plan for the agent to follow
	 */
	public void setPlan( Plan plan ) {
		this.mCurrentPlan = plan;
		this.mCurrentState = ProfileStateEnum.PLANNING;
	}
	
	
	
	//--------------------------------------------------------------------------
	// S T E P   M E T H O D S 
	//--------------------------------------------------------------------------
	
	
	/**
	 * Timestep function
	 * 
	 * This method is automatically invoked at every time step. If verifies in
	 * which state the agent is, and then either serves incoming messages, moves
	 * the agent around or delivers a task in a particular city.
	 */
	public void step() {
		
		// steps according to the current state....
		switch( mCurrentState ) {
		
			// in this state, we are servicing the agent state for
			// next messages...
			case SERVICING: 
				mLastState = ProfileStateEnum.SERVICING;
				mState.step(); 
				break;
				
			// in this state, the agent is moving to the next city...
			case MOVING: 
				stepMove(); 
				break;
				
			// in this state, we are reading actions from the current
			// plan.. this plan will be used for every agent...
			case PLANNING:
				
				// if plan has more actions...
				if ( mCurrentPlan != null &&
					 mCurrentPlan.hasMoreElements() ) {
					
					// get the next action in the list
					IGenericAction nextAction = mCurrentPlan.nextElement();
					
					// normally, the next action should not be null
					if ( nextAction == null ) {
						mLog.info( "[" + this.mName + "] Plan was completely " +
								"carried over..." );
						break;
					}
						
					// calls the appropriate action
					switch( nextAction.getType() ) {
						case MOVE:
							moveAction( (GMoveAction)nextAction ); 
							break;
						case PICKUP: 
							pickupAction( (GPickupAction)nextAction ); 
							break;
						case DELIVER:
							deliverAction( (GDeliverAction)nextAction );
							break;
						default: 
							mLog.log( LogManager.DEFAULT, 
									  LogSeverityEnum.LSV_WARNING, 
									  "A bad action was found in current " +
									  "plan !" );
					}
				}  else {
					mCurrentState = ProfileStateEnum.SERVICING;
				}
		}
	}
	

	/**
	 * Handle a move event
	 * 
	 * Invoked when a move action was found in the plan. The move action 
	 * stipulates where to move next.
	 * 
	 * @param gma a MoveAction type object
	 */
	public void moveAction( GMoveAction gma ) {

		// moves to the target city
		moveToCity( mCurrentCity,  gma.getTarget() );
		
		// sets the actio moving
		mCurrentState = ProfileStateEnum.MOVING;
		mLastState = ProfileStateEnum.PLANNING;
		
		// computes the incurrent cost for moving 
		double incurredCost = mRouteDistance * mCostPerKm;
		
		// the cost of the operations so far
		mTotalCost += incurredCost;
		mTotalDistance += mRouteDistance;
		
		// if the parent agent exists, then notify it
		if ( mParent != null ) 
			mParent.notityParentForMove( incurredCost, mRouteDistance );
		
		// sets the action to move
		mHistory.move( gma.getTarget().getNodeLabel(), incurredCost, 
					   mRouteDistance );
	}
	
	
	/**
	 * Handle a pickup event
	 * 
	 * Invoked when a pickup action was found. The behavior
	 * of the pickup action is modified...
	 * 
	 * @param gpa a PickupAction object
	 */
	public void pickupAction( GPickupAction gpa ) {

		// gets the current task
		Task allocatedTask = gpa.getTask();
	
		// tries to allocate a new task
		Task t = mTaskMgr.allocate( allocatedTask.getID(), 
									mCurrentCity, this.mAgentID );
		if ( t==null || mCapacity<mLoad+t.getWeight() ) {
			mLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
				"Task " + allocatedTask + " was already picked up !" );
			mState.postTaskRefusedSignal( allocatedTask.getID() );
			setPlan( null );
			return;
		} 
		
		// do whatever to do here...
		// TODO: either log or compute something with the task here... 

		// retrieve the delivery city
		mDeliveryCity = mTopology.getCity( allocatedTask.getDeliveryCity() );
			
		// if the parent agent exists, then notify it
		if ( mParent != null ) 
			mParent.notifyParentForPickup( allocatedTask );
		
		//mTaskMgr.allocate( allocatedTask.getID(), mCurrentCity, mAgentID );
		
		// adds some reward
		//mTotalReward += 
		//		mTopology.shortestDistanceBetween(mCurrentCity,mDeliveryCity)*
		//					lAllocatedTask.getRewardPerKm();
		mHistory.pickup( mDeliveryCity.getNodeLabel() );
	}
	
	
	/**
	 * Handle a delivery event
	 * 
	 * This method handles a delivery action.
	 */
	public void deliverAction( GDeliverAction action ) {
		
		// actions or tasks should exist !
		if ( action==null || action.getTask()==null )
			return;
		
		// get the task which has been delivered
		Task task = action.getTask();
		
		// is the task null ?
		if ( task == null )
			return;
		
		// send a task delivered signal to agent  
		mState.postTaskDeliveredSignal( task.getID() );
		
		double rewardPerKm = mTaskMgr.deliverTask( mAgentID, mName, task.getID() );
		double absoluteReward = rewardPerKm * mCurrentPlan.getDistanceDriven();
		
		// adds the reward to the total reward
		mTotalReward += absoluteReward;

		// if the parent agent exists, then notify it
		if ( mParent != null ) 
			mParent.notifyParentForDelivery( task, absoluteReward );
		
		// makes up the changes in the history
		mHistory.deliver( mCurrentCity.getNodeLabel(), absoluteReward );
		
	}
	
	
	/**
	 * Step-method for moving the agent displayable
	 * 
	 * This function is invoked when the agent is moving. This function sets
	 * the position up
	 */
	public void stepMove() {

		// computes the elapsed time since start time
		double lElapsedTime = (System.currentTimeMillis() - mStartTime)*0.001;
		
		// computes the distance travelled so far
		double lDistance = lElapsedTime*mSpeed;
		
		// if distance is greater or equal, we
		// do not move anymore...
		if ( lDistance < mRouteDistance ) {
			mDisplayable.move( 
					mInterpolator.interpolate(lDistance/mRouteDistance) );
		} else {
			lDistance = mRouteDistance;
			mCurrentCity = mTargetCity;
			mCurrentState = mLastState;	 
		}
	}
	

	/**
	 * Prepare for moving from one city to another
	 * 
	 * Moves an agent to the requested city. This method initializes the
	 * interpolator object.
	 */
	public void moveToCity( City city1,  City city2 ) {
		
		// computes distances and coordinates
		int x1 = city1.getX();
		int y1 = city1.getY();
		int x2 = city2.getX();
		int y2 = city2.getY();

		// compute the distance between the cities
		System.out.println( "city1 - " + city1 );
		System.out.println( "city2 - " + city2 );
		double distance = 0;
		try {
			distance = mTopology.getDistance(city1,city2);
		} catch( Exception e ) {
		}
		
		// prepares the interpolator
		mInterpolator.setFrom( new Point(x1,y1) );
		mInterpolator.setTo( new Point(x2,y2) );

		// the current distance to follow 
		mRouteDistance = distance;
		
		// defines the direction and angle
		double mx = (double)(x2-x1);
		double my = (double)(y2-y1);
		double L = Math.sqrt( mx*mx + my*my );
		double invL = (L<0.000001)?1.0:1.0/L;
		mDisplayable.setDirection( (double)mx*invL, (double)my*invL );
		
		// defines the starting time
		mStartTime = System.currentTimeMillis();
		
		// defines the target city
		mTargetCity = city2;
	}
	
	
	//--------------------------------------------------------------------------
	// C O M P A N Y   M A N A G E M E N T   F U N C T I O N S
	//--------------------------------------------------------------------------
	
	
	/**
	 * Adds a new child profile to the children list
	 * 
	 * This function is automatically invoked to add a child profile to
	 * a list
	 * 
	 * @param child the child profile to add
	 */
	public void addChild( AgentProfile child ) throws Exception {
		
		// vehicles cannot have children
		if ( mType == AgentTypeEnum.VEHICLE )
			throw new Exception( "Vehicles cannot have any children !");
		
		// adds a new child
		mChildren.add( child );
		
		// log the event
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
				"[" + mName + "] Adding child agent '" + child.getName() 
				+ "'...");
	}
	
	
	/**
	 * Get the ith child of this agent
	 * 
	 * @param i the index of the child to retrieve
	 * 
	 * @return a profile object for agent i
	 */
	public AgentProfile getChild( int i ) {
		return mChildren.get( i );
	}
	
	
	/**
	 * Return the list of all children
	 */
	public ArrayList<AgentProfile> getChildren() {
		return mChildren;
	}
	
	
	/**
	 * Returns the number of children agents
	 * 
	 * @return an integer representing the number of children
	 */
	public int getChildrenCount() {
		return mChildren.size();
	}
	
	
	/**
	 * Notify the parent that a task was delivered
	 * 
	 * @param reward reward associated with the agent
	 */
	private void notifyParentForDelivery( Task t, double reward ) {
		mTotalReward += reward;
		mHistory.addReward( reward );
	}
	
	
	/**
	 * Notify the parent agent that a task has been picked up
	 * 
	 * @param t the task t which was picked up
	 */
	private void notifyParentForPickup( Task t ) {
	}
	
	
	/**
	 * Notify the parent agent every time we move
	 * 
	 * @param cost the cost of moving from one city to another
	 * @param dist the distance separating source and destination citites
	 */
	private void notityParentForMove( double cost, double dist ) {
		this.mTotalCost += cost;
		this.mTotalDistance += dist;
		mHistory.addCost( cost );
		mHistory.addDistance( dist );
	}
	
	
	/**
	 * Notify the agent for killing
	 * 
	 * This method is invoked automatically by the children agent to notify
	 * the parent that child agent
	 * 
	 * @param ap the profile to delete
	 */
	private void notifyParentForKilling( AgentProfile ap ) {
		
		// try deleting the agent 
		if ( mChildren.contains(ap) ) {
			
			// remove the agent from the list
			mChildren.remove(ap);
			
			// log the event
			mLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Child agent '" + ap.getName() + "' was removed from parent '" +
				this.mName + "'" );
			
			// if there is no more children, then kill company agent too...
			if ( mChildren.size()==0 ) {
				mLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"Company agent '" + mName + "' has no more children ! " +
					"Agent will kill himself !" );
				AgentManager.getInstance().removeAgent( this );
			}
		}
	}
	
	
	//--------------------------------------------------------------------------
	// A G E N T   I N I T I A L I Z A T I O N    S E R V I C E S
	//--------------------------------------------------------------------------
	
	/**
	 * Create an agent from a single descriptor
	 * 
	 * This method creates an entire agent from a descriptor. A parent profile 
	 * is also passed in params to build an agent hierarchy.
	 * 
	 * @param ad the descriptor of the agent
	 * @param parent the parent agent (company in general)
	 */
	public void create( AgentDescriptor ad, AgentProfile parent )
			throws Exception {

		// stores the parent agent
		mParent = parent;
		
		// creates the properties from the descriptor
		parseProperties( ad );

		// log the event
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
				"[" + ad.Name + "] Creating agent state...");

		// creates the agent state
		createAgentState(ad);
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
			"[" + ad.Name + "] Agent ID is " + mState.getObjectID() + "...");
		
		// log the event
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
			"[" + ad.Name + "] Creating agent entity...");

		// creates the agent
		createAgentEntity(ad);
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
			"[" + ad.Name + "] Entity ID is " + mAgent.getObjectID() + "...");
		
		// log the event
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
			"[" + ad.Name + "] Creating agent representation...");

		// creates the agent representation
		if (mType == AgentTypeEnum.VEHICLE) {
			createAgentRepresentation(ad);
		}
		
		// set recipient
		mState.setRecipient( mAgent.getObjectID() );
		
		// if agent has one parent...
		if ( mParent != null ) {
			mParent.addChild( this );
		}
	}

	
	/**
	 * Create an agent entity
	 * 
	 * This method creates the entity object for the agent. However, it does not
	 * start its thread. It will start the threads when all agents will be asked
	 * to initialize.
	 * 
	 * @param ad the Agent descriptor linked to this entity.
	 */
	private void createAgentEntity(AgentDescriptor ad)
			throws Exception {

		// creates a brand new agent
		mAgent = new Agent(mState.getObjectID());

		// registers the behaviors for this agent
		registerBehaviors( ad.Behaviors );
	}

	
	/**
	 * Create an agent representation object 
	 * 
	 * Creates a representation of the agent. A representation is a way to
	 * separate the data and the presentation layers.
	 * 
	 * @param ad the descriptor linked to the agent.
	 */
	private void createAgentRepresentation( AgentDescriptor ad ) 
		throws AgentCreationException {

		// Creates the representation of the agent
		mDisplayable = new AgentDisplayable();
		
		// try to set the color of the displayable
		mDisplayable.setColor( ad.AgentColor );
		
		// set the initial vehicle position
		mDisplayable.move( mHome.getX(), mHome.getY() );
		
		// log the event
		mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
				"[" + ad.Name + "] Agent home location is " + mHome + "...");
		
		City mTarget = (City)mTopology.getDestinations(mCurrentCity).get(0);
		double mx = mTarget.getX() - mHome.getX();
		double my = mTarget.getY() - mHome.getY();
		double L = Math.sqrt( mx*mx + my*my );
		double invL = (L<0.000001)?1.0:1.0/L;
		mDisplayable.setDirection( (double)mx*invL, (double)my*invL );	
	}

	
	/**
	 * Create an agent state object
	 * 
	 * Invoked to create the state of the agent. The state of an agent is a
	 * special class holding a finite state machine whose states change
	 * according to actions performed by behaviors.
	 * 
	 * @param ad the descriptor of the current agent.
	 */
	private void createAgentState( AgentDescriptor ad )
		throws AgentCreationException {

		// creates a new behicle state
		switch( mType ) {
			case VEHICLE: createVehicleAgentState(ad, mState); break;
			case COMPANY: createCompanyAgentState(ad); break;
			default: throw new AgentCreationException( "Found an invalid "
				+ "agent type: '" + ad.Type + "'");
		}
		
		// retrieves the agent iD
		mAgentID = mState.getObjectID();
	}

	
	/**
	 * Create a company agent state
	 * 
	 * Creates an agent company state. This state supervises the state of the
	 * children agent state. Every children agent state must report received
	 * actions to its parent to keep them synchronized..
	 * 
	 * @param ad the agent descriptor object.
	 */
	private void createCompanyAgentState( AgentDescriptor ad )
		throws AgentCreationException {

		// retrieves parent state
		AgentState ps = mParent == null ? null : mParent.mState;

		// creates a special agent depending on the case
		switch ( mBehavior ) {
		
			// create a reactive company state
			case REACTIVE: 
				mState = new ReactiveCompanyState(this, ps); 
				break;
				
			// create a deliberative company state
			case DELIBERATIVE: 
				mState = new DeliberativeCompanyState(this, ps); 
				break;
				
			// create a centralized company state
			case CENTRALIZED: 
				mState = new CentralizedCompanyState(this, ps); 
				break;
				
			// create an auction company state
			case AUCTION: 
				mState = new AuctionCompanyState(this, ps); 
				break;
			
			// create an auction company state
			case AUCTIONEER: 
				mState = new BidderCompanyState(this, ps); 
				break;
				
			// create a custom company state
			case CUSTOM:
				
				// logs the event 
				mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
						"[" + ad.Name + "] Creating custom company agent " +
						"from class " + ad.ClassName + "..." ); 
				
				// creates the array of parameters: AgentProfile, AgentState
				Class<?>[] args = { AgentProfile.class, AgentState.class };
				Object[] objs = { this, ps };
				
				// tries creating the class
				try {
					
					// loads the custom agent
					Object objState = LogistClassLoader.instantiateClass( 
							ad.ClassName, mGlobals.ClassPath, args, objs );
					
					// the object must be an instance of AgentState
					if ( objState instanceof AgentState )
						mState = (AgentState)objState;
					else
						throw new AgentCreationException( "The custom company "+
								"agent class has a bad type : " + objState + 
								"!" );
					
				} catch( Exception e ) {
					mLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
							"Could not create a " +
							"vehicle agent instance for agent '" + ad.Name + 
							"' from class " + ad.ClassName + ". Please " +
							"verify that the global classpath variable is " +
							"correctly set !\n\nCurrent classpath is set to:\n"+
							mGlobals.ClassPath );
					throw new AgentCreationException( "Could not create a " +
							"company agent instance for agent '" + ad.Name + 
							"' from class " + ad.ClassName + ". Please " +
							"verify that the global classpath variable is " +
							"correctly set !" );
				}
				
				// breaks out of here...
				break;
		}
	}

	
	/**
	 * Create a new vehicle agent state
	 * 
	 * Creates a vehicle agent state. We do not provide different agent
	 * entities, however, each agent state is different, because it depends on
	 * different states to achieve different behaviors. In this case, the agent
	 * entity is only one container for the behaviors, while an agent state is
	 * what controls it.
	 * 
	 * @param ad the agent descriptor object.
	 */
	private void createVehicleAgentState(AgentDescriptor ad, AgentState parent)
		throws AgentCreationException {

		// retrieves parent state
		AgentState ps = mParent == null ? null : mParent.mState;

		// creates a special agent depending on the case
		switch (mBehavior) {
		
			// create  a new reactive vehicle agent
			case REACTIVE: 
				mState = new ReactiveVehicleState(this, ps); 
				break;
				
			// create a deliberative agent vehicle
			case DELIBERATIVE: 
				mState = new DeliberativeVehicleState(this, ps); 
				break;
				
			// create a centralized agent vehicle
			case CENTRALIZED: 
				mState = new CentralizedVehicleState(this, ps); 
				break;
				
			// create an auctionning agent
			case AUCTIONEER:
				mState = new BidderVehicleState(this, ps);
				break;
				
			// create a user-made agent
			case CUSTOM: 
				
				// logs the event 
				mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
						"[" + ad.Name + "] Creating custom vehicle agent " +
						"from class " + ad.ClassName + "..." ); 
				
				// prepares parameters
				Class<?>[] args = { AgentProfile.class, AgentState.class };
				Object[] objs = { this, ps };

				// tries creating the class
				try {
					
					// get the class as a generic object
					Object objState = LogistClassLoader.instantiateClass( 
							ad.ClassName, mGlobals.ClassPath, args, objs );
					
					// do we have the correct type ?
					if ( objState instanceof AgentState ) 
						mState = (AgentState)objState;
					else
						throw new AgentCreationException( "The custom vehicle " +
								"agent class has a bad type : " + objState + 
								"!" );
				} catch( Exception e ) {
					mLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
							"Could not create a " +
							"company agent instance for agent '" + ad.Name + 
							"' from class " + ad.ClassName + ". Please " +
							"verify that the global classpath variable is " +
							"correctly set !\n\nCurrent classpath is set to:\n"+
							mGlobals.ClassPath );
					throw new AgentCreationException( "Could not create a " +
							"vehicle agent instance for agent '" + ad.Name + 
							"' from class " + ad.ClassName + ". Please " +
							"verify that the global classpath variable is " +
							"correctly set !" );
				}
				
				// breaks out of here...
				break;
		}
	}

	
	/**
	 * Parse the properties of the agent
	 * 
	 * Finds the properties of the agent and parse them
	 */
	public void parseProperties( AgentDescriptor ad ) 
		throws AgentCreationException {
		
		// setting toplevel properties for the agent
		String lStrType = ad.Type.toLowerCase();
		
		// finds the type of agent
		if (lStrType.equals("vehicle")) mType = AgentTypeEnum.VEHICLE;
		else if (lStrType.equals("company")) mType = AgentTypeEnum.COMPANY;
		else throw new AgentCreationException("Agent '" + ad.Name + "' has"
					                     + "an unknown type '" + ad.Type + "'");

		// setting the behavior of the agent
		String lStrBehavior = ad.Behavior.toLowerCase();
		if ( lStrBehavior.equals("reactive") )
			mBehavior = AgentBehaviorEnum.REACTIVE;
		else if ( lStrBehavior.equals("deliberative") ) 
			mBehavior = AgentBehaviorEnum.DELIBERATIVE;
		else if ( lStrBehavior.equals("centralized") ) 
			mBehavior = AgentBehaviorEnum.CENTRALIZED;
		else if ( lStrBehavior.equals("auction") ) 
			mBehavior = AgentBehaviorEnum.AUCTION;
		else if ( lStrBehavior.equals("bidder") ) 
			mBehavior = AgentBehaviorEnum.AUCTIONEER;
		else if ( lStrBehavior.equals("custom") ) 
			mBehavior = AgentBehaviorEnum.CUSTOM;
		else throw new AgentCreationException("Agent '" + ad.Name + "' has "
					+ "an unknown behavior '" + ad.Behavior + "'");

		// did the user selected a random agent ?
		if ( ad.Home.equals("random") ) {
			mHome = Topology.getInstance().getRandomCity();
		} else {
			mHome = Topology.getInstance().getCity( ad.Home );
			if ( mHome == null )
				throw new AgentCreationException( "City '" + ad.Home + "' " +
						"does not exist in current topology ! Invalid home " +
						"location for agent '" + ad.Name + "'");
		}

		// sets the current city
		mCurrentCity = mHome;
		
		// now retrieves the name of the agent
		mName = ad.Name;
		
		// define the speed of the agent
		mSpeed = ad.Speed;

		// define the capacity of the agent
		mCapacity = ad.Capacity;

		// define the cost per km of the agent
		mCostPerKm = ad.CostPerKM;
		
		// creates the history object
		mHistory = new AgentHistory( mGlobals.HistoryPath + File.separator + 
									 this.mName );
		try { 
			mHistory.init(); 
		} catch( Exception e ) { 
			throw new AgentCreationException(
					"The history object for agent '" + mName + "' could not " +
					"be initialized !\nHere is a description of the problem: " + 
					e.getMessage() );
		}
	}

	
	/**
	 * Register the behaviors for the agents
	 * 
	 * This method initialize the behaviors and register them to correct
	 * signals
	 * 
	 * @param behaviors the array of behaviors
	 */
	private void registerBehaviors( ArrayList<BehaviorDescriptor> behaviors )
			throws Exception {

		// if no behavior is defined, then set to default...
		if ( behaviors == null || behaviors.isEmpty() ) {
			
			// logs the event
			mLog.log(LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG,
					"[" + mName + "] No behavior was found. Resetting to" +
					" defaults !" ); 
			
			// creates the default behavior for all
			Behavior rDefBehavior = new DefaultBehavior();
			mAgent.registerBehavior(SignalTypeEnum.SMT_INIT, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_RESET, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_SETUP, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_KILL, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_INCITY, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_INSTATE, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_TASKDELIVERED, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_TASKREFUSED, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_START, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_END, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_WON, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_LOST, rDefBehavior);
			mAgent.registerBehavior(SignalTypeEnum.SMT_ASKBID, rDefBehavior);
			return;
		}
			
		// holds the list of the already loaded classes
		HashMap<String,SignalTypeEnum> lSignals = 
										new HashMap<String,SignalTypeEnum>();
		lSignals.put( "init", SignalTypeEnum.SMT_INIT );
		lSignals.put( "reset", SignalTypeEnum.SMT_RESET );
		lSignals.put( "kill", SignalTypeEnum.SMT_KILL );
		lSignals.put( "setup", SignalTypeEnum.SMT_SETUP );
		lSignals.put( "incity", SignalTypeEnum.SMT_INCITY );
		lSignals.put( "instate", SignalTypeEnum.SMT_INSTATE );
		lSignals.put( "taskdelivered", SignalTypeEnum.SMT_TASKDELIVERED );
		lSignals.put( "taskrefused", SignalTypeEnum.SMT_TASKREFUSED );
		lSignals.put( "auction-start", SignalTypeEnum.SMT_AUCTION_START );
		lSignals.put( "auction-end", SignalTypeEnum.SMT_AUCTION_END );
		lSignals.put( "askbid", SignalTypeEnum.SMT_ASKBID );
		lSignals.put( "auction-won", SignalTypeEnum.SMT_AUCTION_WON );
		lSignals.put( "auction-lost", SignalTypeEnum.SMT_AUCTION_LOST );
		
		// holds a list of loaded classes
		HashMap<String,Behavior> lLoadedClasses = new HashMap<String,Behavior>();
		
		// for every behavior, we must register it
		for ( BehaviorDescriptor bd : behaviors ) {
		
			// behavior
			Behavior bh = null;
			
			// was the behavior already loaded ? user it then...
			if ( lLoadedClasses.get(bd.Handler) != null )
				bh = lLoadedClasses.get( bd.Handler );
			else {
				try {
					
					// loads the object behavior
					Object objBehavior = LogistClassLoader.instantiateClass( 
							bd.Handler, mGlobals.ClassPath );
					
					// is this of correct type ?
					if ( objBehavior instanceof Behavior )
						bh = (Behavior)objBehavior;
					else
						throw new AgentCreationException( "The behavior for " +
								"signal '" + bd.Signal + "' has an incorrect " +
								"class type: " + objBehavior + "!" );
					
				} catch( Exception e ) {
					mLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
							"Class loader could not create a custom instance " +
							"of the behavior corresponding to class '" +  
							bd.Handler + " for agent '" + mName + "' ! " + 
							"Please verify that the global classpath variable " +
							"is correctly set !\n\nCurrent classpath " +
							"is set to:\n" + mGlobals.ClassPath );
					throw new AgentCreationException( "Could not create an " +
							"instance of custom behavior for agent '" + mName + 
							"' from class " + bd.Handler + ". Please " +
							"verify that the global classpath variable is " +
							"correctly set !" );
				}
				
				
			} 
			
			// is the behavior valid ?
			if ( bh == null ) 
				throw new Exception( "Behavior class " + bd.Handler + " could "+
						"not be found !" );
			
			// handle all signals
			if ( bd.Signal.equals("*") ) {
				mAgent.registerBehavior(SignalTypeEnum.SMT_INIT, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_RESET, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_SETUP, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_KILL, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_INCITY, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_INSTATE, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_TASKDELIVERED, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_TASKREFUSED, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_START, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_END, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_ASKBID, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_WON, bh );
				mAgent.registerBehavior(SignalTypeEnum.SMT_AUCTION_LOST, bh );
				lSignals.clear();
				
				// add the class if none registed
				if ( !lLoadedClasses.containsKey(bd.Handler) )
					lLoadedClasses.put( bd.Handler, bh );
				
			// handles the following signals
			} else if ( bd.Signal.equals("$") ) {

				// register the behaviors for the remaining signals
				for ( SignalTypeEnum d : lSignals.values() ) {
					mAgent.registerBehavior( d, bh );
				}
				
				// clears the list
				lSignals.clear();
				
			} else {

				// registers the behavior. first inspects to see if a signal
				// was already assigned. We do not assign twice the same 
				// signal. the second is discarded !
				SignalTypeEnum st = lSignals.get( bd.Signal );
				if ( st != null ) {
					
					// first of all, register the signal
					mAgent.registerBehavior( st, bh );
					
					// then, remove the signal from assignation
					lSignals.remove( st );

					// finally, add the class if not already added
					if ( !lLoadedClasses.containsKey(bd.Handler) )
						lLoadedClasses.put( bd.Handler, bh );
					
				} else {
					throw new AgentCreationException( "Cannot assign a signal "+
							"twice ! Please suppress one of the duplicated " + 
							bd.Signal + " signals !" );
				}
			}
		}
	}
}