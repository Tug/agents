package epfl.lia.logist.task.distribution;

/* import table */
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;


/**
 * 
 * @author malves
 *
 */
public interface ITaskDistribution {
	
	
	/**
	 * Indicates whether or not the distribution has more tasks to
	 * distribute.
	 * 
	 * @return <strong>true</strong> if the distribztion has more tasks, 
	 *         <strong>false</otherwise>.
	 */
	public boolean hasMoreTasks();

	
	/**
	 * Returns the next task
	 * 
	 * @return An object representing the next task to deliver.
	 */
	public Task next();
	
	
	/**
	 * Resets the task distribution
	 */
	public void reset();

	
	/**
	 * Returns the probability distribution for this agent
	 */
	public ProbabilityDistribution getProbabilityDistribution();
}