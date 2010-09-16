package epfl.lia.logist.task;

public class ProbabilityDescriptor {
	
	/**
	 * The name of the city from which we should have a task
	 */
	public String From = null;
	
	/**
	 * The name of the city to which we should deliver
	 */
	public String To = null;
	
	/**
	 * The task probability in this city
	 */
	public Double Task = null;
	
	/**
	 * The random reward 
	 */
	public Double Reward = null;
}
