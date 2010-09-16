
package epfl.lia.logist.task;

/* import table */
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentManager;
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.IService;
import epfl.lia.logist.core.listeners.IAgentListener;
import epfl.lia.logist.core.listeners.ITopologyListener;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.AID;
import epfl.lia.logist.tools.LogistGlobals;


/**
 * The task manager implements management of tasks
 *
 * The task manager in the LogistPlatform application entirely takes care of
 * the management of tasks. Agents do not carry tasks, instead, they ask
 * allocation of tasks, and then, the task manager internally displaces the
 * requested task(if available) to the task list corresponding to the list of
 * tasks owned by a particular agent.
 */
public class TaskManager implements IService, IAgentListener, ITopologyListener {

	/* The ID number identifying the mailer in the messaging subsystem */
	private TaskGenerator mTaskGen = null;

	/* The singleton instance of the class */
	private static TaskManager msSingleton = null;
	
	/* The topology object from which we retrieve topology data */
	private Topology mTopology = null;
	
	/* keeps a private reference to the log manager */
	private LogManager mLogMgr = null;
	
	/* The map holding all created tasks */
	private HashMap<Integer, Task> mMapOfTasks = null;
	
	/* The map holding lists of tasks to pickup for every city in the map */
	private HashMap<String,ArrayList<Task>> mMapOfTasksToPickup = null;
	
	/* The map holding lists of delivered tasks for every city in the map */
	private HashMap<String,ArrayList<Task>> mMapOfDeliveredTasks = null;	
	
	/* The map of all allocations in the simulation and per agent */
	private HashMap<AID,ArrayList<Task>> mMapOfAllocations = null;
	
	/* The number of currently allocated tasks */
	private int mNumOfAllocatedTasks = 0;
	
	/* The current number of free tasks */
	private int mNumOfFreeTasks = 0;
	
	/* The total number of allocated tasks */
	private int mTotalTaskCount = 0;
	
	/* The total number of delivered tasks */
	private int mTotalDeliveredTasks = 0;
	
	/* The global properties of the system*/
	private LogistGlobals mGlobals = null;
	
	
	/**
	 * Default constructor of the class 
	 */
	public TaskManager( Topology tp ) {
		
		// initializes a singleton instance
		if ( msSingleton == null )
			msSingleton = this;
		
		// initializes the tables of tasks
		mMapOfTasksToPickup = new HashMap<String,ArrayList<Task>>();
		mMapOfDeliveredTasks = new HashMap<String,ArrayList<Task>>();
		mMapOfAllocations = new HashMap<AID,ArrayList<Task>>();
		
		// the list of tasks for this round
		mMapOfTasks = new HashMap<Integer, Task>();
		
		// a link to the topology object
		mTopology = tp;
		mLogMgr = LogManager.getInstance();
	}

	
	/**
	 * This method dispatches a given list of tasks through the city. Note that
	 * this operation must not be interrupted, or the cities will have partial
	 * tasks.
	 * 
	 * @param tasks An array of tasks to dispatch.
	 */
	protected synchronized void dispatchTasks( ArrayList<Task> tasks ) {
		
		// increments the number of tasks
		mTotalTaskCount += tasks.size();
		
		// increments the number of free tasks to pick up
		mNumOfFreeTasks += tasks.size();
		
		// if no task list was passed in argument list of the
		// task list is empty, then return
		if ( tasks == null || tasks.isEmpty() ) 
			return;
		
		// for every task, get the source city and dispatch it to the
		// correct list.
		for ( Task t : tasks ) {
			
			// adds the task to the list of tasks
			mMapOfTasks.put( t.getID(), t );
			
			// get the task list associated to the pickup location
			ArrayList<Task> taskList = mMapOfTasksToPickup.get( t.getPickupCity() );
			
			// if the task list exists, then add the task to this
			// list of tasks
			if ( taskList != null)
				taskList.add( t );
		}
	}
	
	
	/**
	 * Allocate one task
	 * 
	 * This method allows a single agent allocating a task. 
	 */
	public synchronized Task allocate( int taskID, City c, AID aid ) {
		
		// a reference to the task to pickup
		Task task = null;
		
		// if the city doesn't exist o
		assert( c != null );
		
		// get the list of tasks remaining to pickup in this city
		ArrayList<Task> taskList  = mMapOfTasksToPickup.get(c.getNodeLabel());
		
		// asserts the task list exists
		assert( taskList != null );

		// for every task in this city, compare ID to the ID passed in
		// argument list.
		for( Task t : taskList ) {
			if ( taskID==t.getID() ) {
				task = t;
				taskList.remove( t );
				break;
			}
		}
		
		// the task was unfortunately not found !
		if ( task == null )
			return null;
		
		// adds it in the list of allocations
		ArrayList<Task> allocationList = mMapOfAllocations.get( aid );
		
		// asserts that allocation list exists
		assert( allocationList != null );
		
		// adds the task to the list of allocations for
		// this agent
		allocationList.add( task );
		
		// increments the stats
		mNumOfFreeTasks--;
		mNumOfAllocatedTasks++;
		
		// are all tasks delivered ?
		return task;
	}

	
	/**
	 * Allocates a certain amount of tasks to an agent from task ID ‘from’ to task ID ‘to’
	 */
	public HashMap<Integer,Boolean> allocateRange( int from, int to, AID agentID ) {
		return null;
	}

	
	/**
	 * Allocates all tasks
	 */
	public HashMap<Integer,Boolean> allocateFull( AID agentID ) {
		return null;
	}
		
	
	/**
	 * Deliver the currently holded task
	 * 
	 * This method allows delivering a particular task held by current agent.
	 * 
	 * @param aid the id of the agent having a task to deliver
	 * 
	 * @return the reward per km associated to this task
	 */
	public double deliverTask( AID aid, String agentName, Integer taskID ) {
		
		// get the list of allocated tasks for this agent
		ArrayList<Task> allocationList = this.getAllocatedTasklist( aid );
		
		// asserts the existence of the list
		assert( allocationList != null );
		
		// the index of allocated task
		int allocatedIndex = -1;
		int currentTaskIndex = 0;
		
		// searches for the allocated index
		for( Task at : allocationList ) {
			
			// tries to find the index
			if ( at != null && at.getID() == taskID ) {
				allocatedIndex = currentTaskIndex;
				break;
			}
			
			// increments the index of taks
			currentTaskIndex++;
		}
		
		// if task was not found, then exit
		if ( allocatedIndex == -1 )
			return -1000.0;
		
		// remove the task from allocation list
		Task task = allocationList.remove( currentTaskIndex );
		
		// asserts the task exists
		assert( task != null );
		
		// change the status of the task to delivered
		task.setDelivered( true, agentName );
		
		// get the list of delivered tasks
		ArrayList<Task> deliveryList = 
						mMapOfDeliveredTasks.get( task.getDeliveryCity() );
		
		// asserts that the list exists
		assert( deliveryList != null );
		
		// adds the task to the delivery list 
		deliveryList.add( task );
		
		// task was delivered
		mTotalDeliveredTasks++;
		mNumOfAllocatedTasks--;
		
		// return the reward per km for this task
		return task.getRewardPerKm();
	}
	
	
	/**
	 * Release tasks for a particular agent
	 * 
	 * This method releases the list of tasks actually owned by a particular
	 * agent. This method is important, since when an agent blocks, it can 
	 * hold some tasks. These tasks must return to the system.
	 * 
	 * @param agentID The ID of the agent for which we want to return tasks
	 */
	public void releaseTasks( AID agentID ) {
		
		// get the list of allocated tasks for a particular agent
		ArrayList<Task> allocationList = this.mMapOfAllocations.get( agentID );

		// if no task list was passed in argument list of the
		// task list is empty, then return
		if ( allocationList == null || allocationList.isEmpty() ) 
			return;
		
		// increments the number of free tasks to pick up
		mNumOfFreeTasks += allocationList.size();

		// for every task, get the source city and dispatch it to the
		// correct list. note that we won't add tasks to the main task list, 
		// since the task has already been added !
		for ( Task t : allocationList ) {
			
			// get the task list associated to the pickup location
			ArrayList<Task> taskList = mMapOfTasksToPickup.get( t.getPickupCity() );
			
			// if the task list exists, then add the task to this
			// list of tasks
			if ( taskList != null)
				taskList.add( t );
		}
		
	}
	
	
	/**
	 * Return a list of all tasks in the system. 
	 * 
	 * This method returns the complete list of tasks in the system. It can
	 * return the list of tasks that have not been delivered, or the complete
	 * list. Tasks in this list are clones.
	 * 
	 * @param everything indicates if even tasks which have been delivered 
	 * should be added to the list
	 * 
	 * @return a list of cloned tasks.
	 */
	public synchronized ArrayList<Task> getTaskList( boolean everything ) {
		
		// creates the target task list
		ArrayList<Task> taskList = new ArrayList<Task>();
		for( Task t : mMapOfTasks.values() ) {
			
			// do not copy if task was delivered, except when asked to
			if ( !t.getDelivered() || everything ) 
				taskList.add( t.clone() );
		}
		
		// return
		return taskList;
	}
	
	
	/**
	 * Return a task object from its ID
	 */
	public Task getTaskFromID( Integer id ) {
		return mMapOfTasks.get( id );
	}
	
	
	/**
	 * Indicates whether all tasks are delivered
	 * 
	 * All tasks are delivered when there
	 */
	public boolean allTasksDelivered() {
		return ( mTotalDeliveredTasks == mTotalTaskCount &&
				mTotalTaskCount>0 );
	}
	

	/**
	 * Generate more tasks in the taskset
	 *
	 * This method is invoked on every turn to generate more tasks. This function
	 * will fail when working with finite task sets, however, it will generate
	 * more tasks when working with infinite sets !
	 */
	public void generateMoreTasks() {
		if ( mTotalTaskCount>0 &&
			(mTotalTaskCount-mTotalDeliveredTasks) <= mGlobals.TaskThreshold ) {
			if (  mTaskGen.hasMoreTasks() ) {
				dispatchTasks( mTaskGen.generate() );
				AgentManager.getInstance().notifyTaskBatchCreation();
				mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					mTaskGen.getBatchSize() + " tasks have been created !" );
			}
		}
	}
	
	
	/**
	 * Allows to define the task generator
	 */
	public void setGenerator( TaskGenerator tg ) {
		mTaskGen = tg;
	}
		

	/**
	 * Returns an instance of the task generator
	 * @return
	 */
	public TaskGenerator getGenerator() {
		return mTaskGen;
	}
		

	/**
	 * Returns the single instance of the manager -> 
	 * protected by the SecurityMgr
	 * @return
	 */
	public static TaskManager getInstance() {
		return msSingleton;
	}
	
	
	/**
	 * Initializes the whole taskset from a single descriptor
	 */
	public void create( TasksetDescriptor tsd ) throws Exception {
	
		LogManager.getInstance().log( LogManager.DEFAULT, LogSeverityEnum.LSV_DEBUG, 
			"Creating the task generator." );
		
		// now, we're going to create the task generator
		mTaskGen = new TaskGenerator( tsd.TaskGeneratorDescriptor );
		
		// now, we can generate the inital amount of tasks
		this.dispatchTasks( mTaskGen.generate() );
	}
	

	/**
	 * Initializes the internal state of the manager
	 */
	public void init() {
		
	}

	
	
	/**
	 * Deletes tasks and resets internal state
	 */
	public void shutdown() {
		LogManager.getInstance().log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, "Destroying the task manager..." );
		writeManagerLog();
	}

	
	/**
	 * Sets the messaging system up
	 */
	public void setup( Configuration cfg, LogistGlobals lg ) throws Exception {
		create( cfg.Tasks );
		mGlobals = lg;
	}

	
	/**
	 * Initializes a new round
	 */
	public void reset( int round ) {
		
		// resets the task generator
		mTaskGen.reset();
		
		// now, we can generate the inital amount of tasks
		this.dispatchTasks( mTaskGen.generate() );
	}
	

	/**
	 * Returns the number of tasks to pickup in this city
	 */
	public int getPickupTaskCount( String city ) {
		return mMapOfTasksToPickup.get(city).size();
	}
	
	
	/**
	 * Returns the number of delivered tasks to this city
	 */
	public int getDeliveredTaskCount( String city ) {
		return mMapOfDeliveredTasks.get(city).size();
	}
	
	
	/**
	 * 
	 */
	public ArrayList<Task> getPickupTasklist( String city ) {
		return mMapOfTasksToPickup.get(city);
	}
	
	
	/**
	 * 
	 */
	public ArrayList<Task> getAllocatedTasklist( AID aid ) {
		return mMapOfAllocations.get(aid);
	}
	
	
	/**
	 * 
	 * @param city
	 * @return
	 */
	public ArrayList<Task> getDeliveredTasklist( String city ) {
		return mMapOfDeliveredTasks.get(city);
	}
		
	
	/**
	 * Automatically invoked everytime a city is added
	 */
	public void onCityAddition( City c ) {
		if ( c==null ) return;
		mMapOfTasksToPickup.put( c.getNodeLabel(), new ArrayList<Task>() );
		mMapOfDeliveredTasks.put( c.getNodeLabel(), new ArrayList<Task>() );
	}
	
	
	/**
	 * Automatically invoked everytime a city is deleted
	 */
	public void onCityDeletion( City c ) {
		if ( c==null ) return;
		ArrayList<Task> lTaskList = mMapOfTasksToPickup.get( c.getNodeLabel() );
		if ( lTaskList != null ) mMapOfTasksToPickup.remove( lTaskList );
		lTaskList = mMapOfDeliveredTasks.get( c.getNodeLabel() );
		if ( lTaskList != null ) mMapOfDeliveredTasks.remove( lTaskList );
	}
	
	
	/**
	 * Automatically invoked everytime an agent is added
	 */
	public void onAgentAddition( AgentProfile ap ) {
		if ( ap==null ) return;
		mMapOfAllocations.put( ap.getID(), new ArrayList<Task>() );
	}
	
	
	/**
	 * Automatically invoked everytime an agent is removed
	 */
	public void onAgentDeletion( AgentProfile ap ) {
		if ( ap==null ) return;
		ArrayList<Task> lTaskList = mMapOfAllocations.get( ap.getID() );
		if ( lTaskList!=null ) mMapOfAllocations.remove( lTaskList );
	}
	
	
	/**
	 * The text for the service management system
	 */
	public String toString() {
		return "Task management service";
	}
	
	
	/**
	 * Writes the log for this manager
	 *
	 * This method writes the tasks and probability distribution to a defined
	 * file on the disk.
	 */
	
	protected void writeManagerLog() {
		
		// opens the file for writing
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream( "logs/task-log.xml" );
		} catch( Exception e ){
			return;
		}
		
		// creates a print stream for ease
		PrintStream out = new PrintStream( fs );
		
		// writes the headers
		out.println( "<xml version=\"1.0\" encoding=\"UTF-8\" />" );
		out.println( "<log>" );
		
		// outputs the probability distribution
		out.println( "\t<probabilities>" );
		ProbabilityDistribution pd = this.getGenerator().getDistribution().
												getProbabilityDistribution();
		if ( pd != null ) {
			for( City c1 : mTopology.getCities().values() ) {
				for( City c2 : mTopology.getCities().values() ) {
					double dProbability = pd.getProbability( c1, c2 );
					double dReward = pd.getRewardPerKm( c1, c2 );
					out.println( "\t\t<probability city1=\"" + 
							c1.getNodeLabel() + "\" city2=\"" + 
							c2.getNodeLabel() + "\" value=\"" + 
							dProbability + "\" reward=\"" + dReward + "\" />");
				}	
			}
		}
		out.println( "\t</probabilities>" );
		
		// displays the complete list of tasks
		out.println( "\t<tasks>" );
		for ( Task t : this.mMapOfTasks.values() ) {
			out.println( "\t\t" + t );
		}
		out.println( "\t</tasks>" );
		
		// displays the list of all delivered tasks
		out.println( "\t<delivered>" );
		for( String cityName : this.mMapOfDeliveredTasks.keySet() ) {
			ArrayList<Task> arrayOfTasks = 
									this.mMapOfDeliveredTasks.get( cityName );
			for ( Task t : arrayOfTasks ) {
				out.println( "\t\t" + t );	
			}
		}
		out.println( "\t</delivered>" );
		
		// displays the list of all undelivered tasks
		out.println( "\t<undelivered>" );
		for( String cityName : this.mMapOfTasksToPickup.keySet() ) {
			ArrayList<Task> arrayOfTasks = 
									this.mMapOfTasksToPickup.get( cityName );
			for ( Task t : arrayOfTasks ) {
				out.println( "\t\t" + t );	
			}
			
		}
		out.println( "\t</undelivered>" );
		
		// ends the log here...
		out.println( "</log>" );
		
		// closes the log file
		out.close();
	}
}