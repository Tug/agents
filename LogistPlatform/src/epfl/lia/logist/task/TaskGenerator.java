package epfl.lia.logist.task;

/* import table */
import java.util.ArrayList;

import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.task.distribution.DiscreteTaskDistribution;
import epfl.lia.logist.task.distribution.ProbabilisticTaskDistribution;
import epfl.lia.logist.task.distribution.ITaskDistribution;
import epfl.lia.logist.tools.Convert;


/**
 * 
 *
 */
public class TaskGenerator {

	/* Keeps a reference to the current task distribution */
	private ITaskDistribution mTaskDistribution = null; 

	/* How big is one single batch of tasks ? */
	private int mBatchSize = 10;

	/* How many tasks can we allocate ? */
	private int mMaxAllocatable = -1;
	
	/* The number of currently generated tasks */
	private int mGeneratedTasks = 0;
	
	
	/**
	 * Constructor of the class
	 * @param td
	 */
	public TaskGenerator( TaskgenDescriptor td ) throws Exception {
		
		// starts out by creating a task distribution
		createTaskDistribution( td.Distribution );
		
		// defines local variables
		this.mMaxAllocatable = Convert.toInt( (String)td.Props.get("max-allocatable"), -1 );
		this.mBatchSize = Convert.toInt( (String)td.Props.get("batch-size"), 10 );
		
		// sets the default value for the batch size
		if ( this.mMaxAllocatable <= 0 ) this.mMaxAllocatable = -1;
		if ( this.mBatchSize <= 0 ) this.mBatchSize = 1;
		
		// initializes the current amount of generated tasks
		this.mGeneratedTasks = this.mMaxAllocatable;
	}		

	
	/**
	 * Create a new task distribution
	 */
	private void createTaskDistribution( TaskDistributionDescriptor tdd ) 
		throws Exception 
	{
		
		// the name of the distribution
		String lDistributionType = tdd.Type;
		
		// what type of task distribution to create? 
		if ( lDistributionType.equals("discrete") ) {
			createDiscreteDistribution( tdd );
		} else if ( lDistributionType.equals("probabilistic") ) {
			createProbabilisticDistribution( tdd );
		} else {
			throw new Exception( "Distribution type '" + tdd.Type + 
					"' is cannot be handled !!!");
		}
	}
	
	
	/**
	 * Creates a new discrete task distribution
	 */
	private void createDiscreteDistribution( TaskDistributionDescriptor tdd ) 
		throws Exception 
	{
		// log the event
		LogManager.getInstance().log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Creating a discrete task distribution" );
		
		// are there some tasks ?
		if ( tdd.TaskDescriptorList == null || 
			(tdd.TaskDescriptorList!=null && tdd.TaskDescriptorList.size()<=0) ) 
			throw new Exception( "No task was defined for current configuration" );
		
		// now we can create an array of tasks
		ArrayList<Task> lTasks = new ArrayList<Task>();
		
		// for every task descriptor out there, create a task
		for( TaskDescriptor td : tdd.TaskDescriptorList ) 
			lTasks.add( new Task(td) );
		
		// now that we have the array, we can create a task distribution object
		mTaskDistribution = new DiscreteTaskDistribution(lTasks);
	}
	
	
	/**
	 * Creates a new poisson task distribution
	 */
	private void createProbabilisticDistribution( TaskDistributionDescriptor tdd ) 
		throws Exception 
	{
		// log the event
		LogManager.getInstance().log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Creating a probabilistic task distribution" );

		// creates the task distribution
		mTaskDistribution = new ProbabilisticTaskDistribution( tdd );
	}
	
	
	/**
	 * Resets the underlying task distribution
	 */
	public void reset() {
		if ( this.mTaskDistribution != null ) {
			this.mTaskDistribution.reset();
			mGeneratedTasks = this.mMaxAllocatable;
		}
	}
	
	
	/**
	 * Generates a complete batch of tasks
	 */
	public ArrayList<Task> generate() {
		
		// initializes the list of tasks
		ArrayList<Task> lTaskList = new ArrayList<Task>();
		
		// we must generate batches of task
		for ( int i=0; i<mBatchSize && mGeneratedTasks!=0; i++, mGeneratedTasks-- ) {
			if ( mTaskDistribution.hasMoreTasks() ) {
				lTaskList.add( mTaskDistribution.next() );
			} else {
				break;
			}
		}
		
		// returns the list of tasks
		return lTaskList;
	}
		
	
	/**
	 * Retrives the task distribution followed by task generator
	 * @return
	 */
	public ITaskDistribution getDistribution() {
		return mTaskDistribution;
	}

	
	/**
	 * Returns the maximum number of tasks that can be allocated by this
	 * generator per round.
	 */
	public int getMaxAllocatable() {
		return this.mMaxAllocatable;
	}
	
	
	/**
	 * Returns the size of the batch of tasks.
	 * 
	 * This method returns the size of a single batch of tasks. A batch 
	 * determine how many tasks will be created each time the task generator
	 * must generate something.
	 * 
	 * @return the size of the batch of tasks.
	 */
	public int getBatchSize() {
		return this.mBatchSize;
	}
	
	
	/**
	 * Indicates whether this generator has more tasks or not
	 */
	public boolean hasMoreTasks() {
		return ( this.mGeneratedTasks != 0 && 
				 this.mTaskDistribution.hasMoreTasks() );
	}
}