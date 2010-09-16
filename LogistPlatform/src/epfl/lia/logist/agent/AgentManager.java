package epfl.lia.logist.agent;

/* import list */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.IService;
import epfl.lia.logist.core.listeners.IAgentListener;
import epfl.lia.logist.exception.AgentCreationException;
import epfl.lia.logist.exception.AgentPopulationException;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.AID;
import epfl.lia.logist.tools.LogistGlobals;
import epfl.lia.logist.tools.TimeoutBarrier;


/**
 * The AgentManager class handles the management of agents.
 * 
 * This class is responsible for creating, updating and killing agents.
 */
public class AgentManager implements IService {

	/* Maintains a list of agent profiles */
	private HashMap<AID,AgentProfile> mMapOfProfiles = null;
	
	/* This list holds the ID of agents scheduled to be killed */
	private ArrayList<AID> mListOfKills = null;
	
	/* The single instance of the class */
	private static AgentManager msSingleton = null;
	
	/* A reference to the log manager object */
	private LogManager mLogMgr = null;
	
	/* Agent change listener */
	private ArrayList<IAgentListener> mListeners = null;

	/* The single instance of the barrier */
	private TimeoutBarrier<AID> mBarrier = new TimeoutBarrier<AID>();
	
	/* Keeps the logist globals in one place */
	private LogistGlobals mGlobals = null;
	
	/* The task manager */
	private TaskManager mTaskMgr = null;
	
	
	/**
	 * Default class constructor. 
	 * 
	 * Initializes instance objects that do not need special privileges.
	 */
	public AgentManager() {
		if ( msSingleton==null )
			msSingleton=this;
		mLogMgr = LogManager.getInstance();
		mMapOfProfiles = new HashMap<AID,AgentProfile>();
		mListeners = new ArrayList<IAgentListener>();
		mListOfKills = new ArrayList<AID>();
	}
	
	
	
	//--------------------------------------------------------------------------
	// S E R V I C E   I N T E R F A C E   I M P L E M E N T A T I O N
	//--------------------------------------------------------------------------
	
	/**
	 * Initialize the manager
	 * 
	 * This method is automatically invoked when services
	 * begin starting up...
	 */
	public void init() {
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Initializing the agent management service..." );
		
		// TODO: perform some needed initialization here...
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Initialisation of the agent management is complete." );
	}

	
	/**
	 * Shuts the manager down
	 * 
	 * Deletes all internal data and sends requests to kill agents.
	 */
	public void shutdown() {
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Shutting the agent management service down..." );

		// TODO: perform some needed shutdown here...
		killAllAgents();
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Agent management service is shutting down..." );
	}
	
	
	/**
	 * Sets the manager up.
	 * 
	 * This method asks the creation of agents from the disk (from
	 * descriptor). It starts by creating the root agent first, and then 
	 * it continues with children.
	 * 
	 * @param cfg the global configuration object
	 * @param lg an object holding globals
	 */
	public void setup( Configuration cfg, LogistGlobals lg ) 
		throws Exception {

		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Beginning creation of the individual agents..." );
		
		// store the globals
		mGlobals = lg;
		
		// create the root agent
		createAgentProfile( cfg.Agents.RootAgent, null );
		
		// logs the event to the disk
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG, 
			"All agents were successfully created !" );
		
		// if no profile was created, then we throw and
		if ( mMapOfProfiles.size() == 0 )
			throw new AgentPopulationException( 
					"There exist no agent in simulation." );
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Agent creation and initialization is complete." );
		
		// sets the task manager
		mTaskMgr = TaskManager.getInstance();
	}
	
	
	/**
	 * Create an agent profile from a descriptor
	 * 
	 * This method takes a descriptor as input and then creates a new
	 * agent profile. We also pass the parent reference to setup an agent
	 * hierarchy.
	 * 
	 * @param ad the descriptor serving to initialize the agent
	 * @param parent the parent profile to build a hierarchy
	 */
	private void createAgentProfile( AgentDescriptor ad, AgentProfile parent ) 
		throws Exception {
		
		// the agent descriptor should exist
		if ( ad == null ) 
			throw new AgentCreationException( "No agent descriptor found." );
		
		// the agent is not active
		if ( !ad.Active ) {
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
				"Agent '" + ad.Name + "' is not active. Not creating it !" );
			return;
		}
		
		// create a new profile
		AgentProfile ap = new AgentProfile( mGlobals );

		// log the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG, 
			"Spawning named agent '" + ad.Name + "'..." );

		// do we have enough memory for a new profile ?
		if ( ap == null )
			throw new AgentCreationException( "No profile could be created. " +
					"Please raise the VM memory amount!" );
		
		// can we further initialize the agent from 
		// the descriptor ?
		ap.create( ad, parent );
		
		// logs the event to the disk
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG, 
			"Spawning children agents for '" + ad.Name + "'..." );

		// invokes the listeners to add the agent profile in their 
		// lists...
		if ( ap.getType() == AgentTypeEnum.VEHICLE ) {
			for ( IAgentListener acl : mListeners ) {
				acl.onAgentAddition( ap );
			}
		}
		
		// if this is a parent agent, then we should create the children
		// elements too...
		if ( ad.Children != null ) {
			for( AgentDescriptor adc: ad.Children ) {
				createAgentProfile( adc, ap );
			}
		}
		
		// put this agent in the map of agents...
		mMapOfProfiles.put( ap.getID(), ap );
	}

	
	/**
	 * Reset all agents
	 * 
	 * This method calls the respective reset() method for all agents. Agents
	 * will then be sent a RESET message.
	 * 
	 * @param round the current round number
	 */
	public void reset( int round ) {
		resetAllAgents( round );
	}
	
	
	/**
	 * Add a new listener.
	 * 
	 * Adds a new listener. A listener is invoked when an agent is created, 
	 * deleted or changed. This allows classes depending on agents to use 
	 * only a list, thus having no need to duplicate data. For example, the
	 * service responsible for displaying agents will register as a listener.
	 * When a new agent is created, this service is immediately informed !
	 * 
	 * @param acl the listener object
	 */
	public void addListener( IAgentListener acl ) {
		mListeners.add( acl );
	}
	
	
	//--------------------------------------------------------------------------
	// A G E N T   M A N A G E M E N T  F U N C T I O N S 
	//--------------------------------------------------------------------------
	
	
	/**
	 * Initializes all agents in the simulation
	 * 
	 * This method sends a INIT message to all agents. When the agent receives
	 * the message, it has then a certain amount of time to initialize whatever
	 * it has to initialize.
	 * 
	 * @note If the agent is not ready after this delay, it will automatically
	 * be deleted from the simulation
	 */
	public void initAllAgents() {
		
		// reset tbe barrier
		mBarrier.reset();
		
		// init all agent
		for ( AgentProfile ap : mMapOfProfiles.values() ) {
			ap.init();
			mBarrier.register( ap.getID() );
		}
		
		// start the barrier
		mBarrier.start(mGlobals.InitTimeout );
	}	
	
	
	/**
	 * Notifies the batch creation. Agents are notified everytime
	 * a task batch is happened to the system. 
	 */
	public void notifyTaskBatchCreation() {
		for( AgentProfile ap : mMapOfProfiles.values() ) 
			ap.getState().notifyTaskBatchCreation();
	}

	
	/**
	 * Kills all agents in the simulation
	 * 
	 * This method sends a KILL message to all agents. When the agent receives
	 * the message, it has then a certain amount of time before simulation
	 * ends. This little delay allows writing files to disk, etc.. etc...
	 */
	public void killAllAgents() {
		
		// reset the barrier
		mBarrier.reset();
		
		// kill all agents
		for ( AgentProfile ap : mMapOfProfiles.values() ) {
			ap.kill();
			mBarrier.register( ap.getID() );
		}
		
		// start the barrier
		mBarrier.start( mGlobals.KillTimeout );
	}
	
	
	/**
	 * Send a global request to reset
	 * 
	 * This method is invoked at the end of a round in a multi-round simulation.
	 * If tells agents which round is starting.
	 */
	public void resetAllAgents( int round ) {
		
		// reset the barrier 
		mBarrier.reset();
		
		// reset agents and register them
		for ( AgentProfile ap : mMapOfProfiles.values() ) {
			ap.reset( round );
			mBarrier.register( ap.getID() );
		}
		
		// start the barrier
		mBarrier.start( mGlobals.ResetTimeout );
	}


	/**
	 * Remove an agent from the disk
	 * 
	 * This method removes an agent from the manager's lists. It is killed
	 * and everything it has done is written to disk( history, etc...). The
	 * agent is first put in a schedule list and is killed in next time step.
	 * 
	 * @param ap the profile object to kill
	 */
	public void removeAgent( AgentProfile ap ) {
		
		// sends a request to kill
		ap.kill();
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Agent '" + ap.getName() + "' was scheduled for killing !" );
		
		// removes the agent from the list
		mListOfKills.add( ap.getID() );
	}
	
	
	/**
	 * Verify if there is any agent which should be killed
	 * 
	 * Once agent manager is requested to remove a particular agent, this
	 * agent is put in a scheduled list which elements will be discarded on
	 * the next time step. This is necessary in order to avoid concurrent
	 * list modificated.
	 */
	public void verifyScheduledAgents() {
		
		// is there any agent to kill
		if ( mListOfKills.size()>0 ) {
			
			// delete all agents
			for( AID aid: mListOfKills ) {
				
				// delete the profile
				mMapOfProfiles.remove( aid );
				
				// throw away tasks
				mTaskMgr.releaseTasks( aid );
				
				// log the event
				mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"Agent scheduled to kill has been deleted !" );
			}
		
			// clears the agents that should be clear 
			mListOfKills.clear();
		}
	}
	
	
	//--------------------------------------------------------------------------
	// I N S T A N C E   S E R V I C E S
	//--------------------------------------------------------------------------
	
	
	/**
	 * Get an instance of the manager
	 */
	public static AgentManager getInstance() {
		return msSingleton;
	}
	
	
	/**
	 * Returns a reference to the barrier
	 */
	public TimeoutBarrier<AID> getBarrier() {
		return mBarrier; 	
	}
	
	
	/**
	 * Returns the profile for a single agent 
	 * @return
	 */
	public AgentProfile getProfile( AID agentID ) {
		return mMapOfProfiles.get(agentID) ;
	}
	
	
	/**
	 * Returns a collection of profiles
	 * @return
	 */
	public Collection<AgentProfile> getProfileList() {
		return mMapOfProfiles.values();
	}


	public int getAgentCount() {
		return mMapOfProfiles.size();
	}
	
	/**
	 * The text for the service management system
	 */
	public String toString() {
		return "Agent management service";
	}
}
