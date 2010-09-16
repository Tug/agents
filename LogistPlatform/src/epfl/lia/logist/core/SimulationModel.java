package epfl.lia.logist.core;

/* import table */
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimEventListener;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import epfl.lia.logist.agent.AgentManager;
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.listeners.ITopologyListener;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.core.view.CustomNetworkDisplay;
import epfl.lia.logist.core.view.ModelView;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.MessageDispatcher;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.Convert;
import epfl.lia.logist.tools.LogistConstants;
import epfl.lia.logist.tools.LogistGlobals;


/**
 * The simulation model.
 */
public class SimulationModel extends SimModelImpl implements SimEventListener {

	/* Holds the globals of the simulation */
	private static LogistGlobals mGlobals = new LogistGlobals();
	
	/* The scheduler object */
	private Schedule mSchedule = null;
	
	/* The display surface on which each object draws */
	protected DisplaySurface mDisplay = null;

	/* The views holding displayable data */
	private ModelView mViews = null;
	
	/* The primary instance of the agent manager */
	private AgentManager mAgentMgr = null;
	
	/* The initial instance of the message dispatcher */
	private MessageDispatcher mMsgDisp = null;
	
	/* The initial instance of the task manager */	
	private TaskManager mTaskMgr = null;
	
	/* The primary instance of the log manager */
	private LogManager mLogMgr = null;
	
	/* The primary instance of the topology object */
	private Topology mTopology = null;
	
	/* The number of runs */
	private int mRound = 1;	
	
	/* A list of all services */
	private ArrayList<IService> mServices = null;
	
	/* A list of topology listeners */
	private ArrayList<ITopologyListener> mListeners = null;

	/* This is the first round */
	private boolean mFirstRound = true;
	
	/* Are agents being killed ? */
	private boolean mKilling = false;
	
	
	/**
	 * Constructs the model and all connected systems
	 * @param cfg
	 */
	public SimulationModel() {
		mLogMgr = LogManager.getInstance();
		mServices = new ArrayList<IService>();
		mListeners = new ArrayList<ITopologyListener>();
	}
		
	
	/**
	 * Creates from a configuration
	 */
	public void createFromConfiguration( Configuration c ) 
		throws Exception {
	
		// parses the properties
		parseGlobals( c.Propset );
		
		// instantiate all services
		instantiateAllServices();	
		
		// register all services
		registerAllServices();
		
		// initialize all services
		initializeAllServices();
		
		// sets every service up
		setupAllServices( c );
	}

	
	/**
	 * Adds a new listener object
	 */
	public void addListener( ITopologyListener tcl ) {
		mListeners.add( tcl );
	}
	
	
	/**
	 * Return the listener list
	 */
	public ArrayList<ITopologyListener> getListeners() {
		return mListeners;
	}
	
	
	/**
	 * Parses the properties of the configuration.
	 */
	public void parseGlobals( Properties propset )  
		throws Exception {
		
		// parse the globals and set to defaults
		mGlobals.WorldXSize = Convert.toInt( (String)propset.get("world-xsize"), mGlobals.WorldXSize );
		mGlobals.WorldXSize = Convert.toInt( (String)propset.get("world-ysize"), mGlobals.WorldYSize );
		mGlobals.Backcolor = Convert.toColor( (String)propset.get("backcolor"), mGlobals.Backcolor );
		mGlobals.Forecolor = Convert.toColor( (String)propset.get("forecolor"), mGlobals.Forecolor );
		mGlobals.Rounds = Convert.toInt( (String)propset.get("num-rounds"), mGlobals.Rounds );
		mGlobals.ResetTimeout = Convert.toLong( (String)propset.get("reset-timeout"), mGlobals.ResetTimeout );
		mGlobals.InitTimeout = Convert.toLong( (String)propset.get("init-timeout"), mGlobals.InitTimeout );
		mGlobals.SetupTimeout = Convert.toLong( (String)propset.get("setup-timeout"), mGlobals.SetupTimeout );
		mGlobals.AuctionTimeout = Convert.toLong( (String)propset.get("auction-timeout"), mGlobals.AuctionTimeout );
		mGlobals.KillTimeout = Convert.toLong( (String)propset.get("kill-timeout"), mGlobals.KillTimeout );
		mGlobals.CityNameColor = Convert.toColor( (String)propset.get("city-name-color"), mGlobals.CityNameColor );
		mGlobals.CityColor = Convert.toColor( (String)propset.get("city-color"), mGlobals.CityColor );
		mGlobals.CityPerimColor = Convert.toColor( (String)propset.get("city-perim-color"), mGlobals.CityPerimColor );
		mGlobals.CityRadius = Convert.toInt( (String)propset.get("city-radius"), mGlobals.CityRadius );
		mGlobals.RouteSize = (float)Convert.toDouble((String)propset.get("route-size"), mGlobals.RouteSize );
		mGlobals.RouteColor = Convert.toColor( (String)propset.get("route-color"), mGlobals.RouteColor );
		mGlobals.TaskIndicatorColor = Convert.toColor( (String)propset.get("task-text-color"), mGlobals.TaskIndicatorColor );
		mGlobals.TaskToPickupColor = Convert.toColor( (String)propset.get("task-pickup-color"), mGlobals.TaskToPickupColor );
		mGlobals.TaskToDeliverColor = Convert.toColor( (String)propset.get("task-deliver-color"), mGlobals.TaskToDeliverColor );
		mGlobals.TaskIndicatorColor = Convert.toColor( (String)propset.get("task-indicator-color"), mGlobals.TaskIndicatorColor );
		mGlobals.TaskThreshold = Convert.toInt( (String)propset.get("task-threshold"), mGlobals.TaskThreshold );
		mGlobals.Antialias = Convert.toBoolean( (String)propset.get("antialias-scene"), mGlobals.Antialias );
		mGlobals.TextAntialias = Convert.toBoolean( (String)propset.get("antialias-text"), mGlobals.TextAntialias );
		mGlobals.ShowLegend = Convert.toBoolean( (String)propset.get("show-legend"), mGlobals.ShowLegend );
		mGlobals.ShowGui = Convert.toBoolean( (String)propset.get("show-gui"), mGlobals.ShowGui );
		mGlobals.ClassPath = Convert.toString( (String)propset.get("classpath"), mGlobals.ClassPath );
		mGlobals.LogPath = Convert.toString( (String)propset.get("log-dir"), System.getProperty("user.dir") );
		mGlobals.HistoryPath = Convert.toString( (String)propset.get("history-dir"), System.getProperty("user.dir") );
		
		// tests for validity
		File logFilepath = new File(mGlobals.LogPath);
		File historyFilepath = new File(mGlobals.HistoryPath);
		
		// if files doesn't exist, fall back to defaults
		if ( !logFilepath.exists() || !logFilepath.isDirectory() )
			mGlobals.LogPath = System.getProperty("user.dir");
		if ( !historyFilepath.exists() || !historyFilepath.isDirectory() )
			mGlobals.HistoryPath = System.getProperty("user.dir");	
		
		// writes some values
		this.mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO,
				"Log file path: " + mGlobals.LogPath );
		this.mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO,
				"History file path: " + mGlobals.HistoryPath );
	}
	
	
	/**
	 * Destroys all the services when simulation ends
	 */
	public void simEventPerformed( SimEvent e ) {
		if ( e.getId() == SimEvent.STOP_EVENT || 
			 e.getId() == SimEvent.END_EVENT )  {
			
			// destroying all services
			this.destroyAllServices();
			
			// flushing all entries in the log manager
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"Flushing entries in " + "the log management subsystem..." );
			
			// simulation ended here...
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
			"Simulation ended successfully..." );
			// flushing all entries
			
			mLogMgr.flush( LogManager.DEFAULT );
			mLogMgr.flush( LogManager.OUT );
			
			// shut everything down
			mLogMgr.shutdown();
			
			// system goes by
			System.exit( 0 );
		}
	}
	
	
	
	//-------------------------------------------------------------------------
	// S E R V I C E   M A N A G E M E N T
	//-------------------------------------------------------------------------
	
	
	/**
	 * Creates a new instance for every service out there
	 */
	public void instantiateAllServices() {
		mViews = new ModelView( mGlobals.WorldXSize, 
								mGlobals.WorldYSize );
		mTopology = new Topology();
		mMsgDisp = new MessageDispatcher();
		mTaskMgr = new TaskManager( mTopology );
		mAgentMgr = new AgentManager();
	}
	
	
	/**
	 * Register all services in the right order
	 */
	public void registerAllServices() {
		
		// adds the services into the list
		mServices.add( mViews );
		mServices.add( mTopology );
		mServices.add( mMsgDisp );
		mServices.add( mTaskMgr );
		mServices.add( mAgentMgr );
		
		// adds the listeners for the topology
		mTopology.addListener( mViews.getTopologyView() );
		mTopology.addListener( mTaskMgr );
		
		// adds the listeners for agents
		mAgentMgr.addListener( mViews.getAgentView() );
		mAgentMgr.addListener( mTaskMgr );
	}
		
	
	/**
	 * Initialize all services
	 */
	public void initializeAllServices() throws Exception {
		for( IService service :  mServices ) {
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"'" + service.toString() + "' is initilizing..." );
			service.init();
		}
	}
	
	
	/**
	 * Sets every registered service up and running
	 */
	public void setupAllServices( Configuration c ) throws Exception {
		for( IService service : mServices ) { 
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"'" + service.toString() + "' is auto-setting up..." );
			service.setup(c,mGlobals);
		}
		mLogMgr.setup(c, mGlobals);
	}
	
	
	/**
	 * Resets all the services by passing them the round number
	 */
	public void resetAllServices( int round ) {
		for ( IService service : mServices ) {
			service.reset( round );
		}
	}
	
	
	/**
	 * Destroys every service out there. For service destruction,
	 * we start by the last service to the first one
	 */
	public void destroyAllServices() {
		
		// destroys everything in the reverse creation order
		for( int i=mServices.size()-1; i>=0; i-- ) {
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"'" + mServices.get(i).toString() + 
					"' is shutting down..." );
			mServices.get(i).shutdown();
		}
	}
	
	
	
	//--------------------------------------------------------------------------
	// S I M U L A T I O N   I N I T I A L I Z E R S 
	//--------------------------------------------------------------------------
	
	/**
	 * Method automatically invoked by the scheduler
	 */
	public void begin() {
    
		// registers this class
		this.addSimEventListener( this );
		
	    // build the model, schedule and display
		initModel();
		
		// the scheduler is also built in the buildModel() function
		initScheduler();
		
		// buildSchedule();
		if ( mGlobals.ShowGui ) initDisplay();
	}

	
	/**
	 * Method automatically invoked by the scheduler
	 */
	public void setup() {
	    
		// suppress this if it causes errors 
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Running setup()..." );
		
		// creates a brand new scheduler
		mSchedule = new Schedule (1);
		
		// should we display the gui
		if ( mGlobals.ShowGui ) {
			
			// create the display surface
			if ( mDisplay != null ) {
				mDisplay.dispose();
				mDisplay = null;
		    }
			
			// creates the default display surface...
			mDisplay = new DisplaySurface( 
						new Dimension(mGlobals.WorldXSize,mGlobals.WorldYSize), 
						this, "PICKUP AND DELIVERY SIMULATION" );
			
			// ... and registers it immediately
			registerDisplaySurface( "PICKUP AND DELIVERY SIMULATION", mDisplay );
		}
	}

	
	/**
	 * Initializes the scheduler
	 */
	protected void initScheduler() {
		
		/* Schedules the execution of the specified method on the 
		 * specified object to start at the specified clock tick 
		 * and continue every tick thereafter.
		 */

		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing the scheduler..." );
		
		// schedules the action
		mSchedule.scheduleActionBeginning( 2, this, "step" );
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing of the scheduler is complete." );
		
	}
		

	/**
	 * Initializes the display
	 */
	protected void initDisplay() {

		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing the display objects..." );
		
		// our custom network display for accelerating the rendering 
		CustomNetworkDisplay networkDisplay = new CustomNetworkDisplay( 
				mViews.getTopologyView().space(), 
				mViews.getAgentView().space(), mGlobals );

		// there's only one top layer
		mDisplay.addDisplayable( networkDisplay, "Topology" );
		mDisplay.setDoubleBuffered(true);
		mDisplay.setMinimumSize( new Dimension(mGlobals.WorldXSize, mGlobals.WorldYSize) );
		mDisplay.setMaximumSize( new Dimension(mGlobals.WorldXSize, mGlobals.WorldYSize) );
		mDisplay.setPreferredSize( new Dimension(mGlobals.WorldXSize, mGlobals.WorldYSize) );
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initialization of the display objects is complete..." );
		
		// sets here generic properties
		mDisplay.setBackground( mGlobals.Backcolor );
		mDisplay.setForeground( mGlobals.Forecolor );
		
		// create the legend
		if ( mGlobals.ShowLegend ) {
			mDisplay.createLegend( "Vehicles" );
			
			for ( AgentProfile ap : mAgentMgr.getProfileList() ) {
				if ( ap.getDisplayable() != null ) {
					mDisplay.addLegendLabel( ap.getName(), 0, ap.getDisplayable().getColor(), false );
				} else {
					mDisplay.addLegendLabel( ap.getName(), 1, Color.BLACK, false );	
				}
			}
		}
	
		// turn the display on
		mDisplay.display();
	}	

	
	/**
	 * Initializes the model and the scheduler, etc�
	 */
	protected void initModel() {
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
		"Initializing the simulation model..." );
		
		// logs the event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing the simulation model is complete." );
		
	}
	
	
	//--------------------------------------------------------------------------
	// S I M U L A T I O N   M E T H O D S
	//--------------------------------------------------------------------------
	
	
	/**
	 * Method automatically invoked by the scheduler
	 */
	public void step() {

		// counts the number of agents alive
		int lAliveCount = 0;
		
		// initializes the first round
		if ( mFirstRound ) {
			initializeRound( true );
			mFirstRound = false;
			mKilling = false;
		}

		// delete all scheduled agents 
		mAgentMgr.verifyScheduledAgents();
		
		// for every agent of the agent manager class, ask the 
		// state for possible work to do. If that�s the case, then 
		// calls the step method in agent sate
		for ( AgentProfile ap : mAgentMgr.getProfileList() ) {
			if ( ap.isAlive() ) { ap.step(); lAliveCount++; }
		}

		// if no more agents are alive
		if ( lAliveCount==0 ) {
			stop();
		}
		
		// generates more tasks
		mTaskMgr.generateMoreTasks();
		
		// the round finishes when all tasks for 
		// the current round are delivered...
		if ( mTaskMgr.allTasksDelivered() && !mKilling ) {
		
			// if no more tasks to deliver, end the simulation
			if ( mRound < mGlobals.Rounds ) {
				initializeRound( false );
				mKilling = false;
			} else {
				mAgentMgr.killAllAgents();
				mKilling = true;
			}
		}
			
		// updates the display
		if ( mGlobals.ShowGui ) mDisplay.updateDisplay();
	}
	
	
	/**
	 * Initializes the first round of the simulation. This round
	 * intializes all agents and prepares all required objects. A 
	 * timeout barrier is placed on the agents in order to wait
	 * for all agents to initialize...
	 */
	protected void initializeRound( boolean bFirst ) {
		
		// this is the very first round 
		if ( bFirst ) {
			
			// log the event
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Lauching agents on first run..." );
			
			// initializes all agents
			mAgentMgr.initAllAgents();	
		} else {
	
			// increments the number of rounds
			mRound++;
			
			// initializes the round - logs the event
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"Initializing round #" + this.mRound );
			
			// resets all services
			resetAllServices( mRound );
		}
	}
		
		

	
	
	//-------------------------------------------------------------------------
	// G E T T E R S   A N D   S E T T E R S
	//-------------------------------------------------------------------------
	
	/**
	 * Returns the parametrable quantities
	 * here..
	 */
	public String[] getInitParam() {
		String[] params = {};
		return params;
	}
	
	//public static LogistGlobals getGlobals() {
	//	return mGlobals;
	//}
	
	
	/**
	 * Returns the name of the application
	 */
	public String getName() {
		return "EPFL-LIA Logist Platform v." 
			+ LogistConstants.VERSION_MAJOR + "." 
		    + LogistConstants.VERSION_MINOR + "."
		    + LogistConstants.VERSION_REVISION;
	}
	
	
	/**
	 * Returns the schedule object
	 */
	public Schedule getSchedule() {
		return mSchedule;
	}
	
}