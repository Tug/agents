package epfl.lia.logist.task;


/**
 * 
 * @author malves
 *
 */
public class TasksetDescriptor {


	/**
	 * The descriptor of the generator
	 */
	public TaskgenDescriptor TaskGeneratorDescriptor = null; 


	/**
	 * How many tasks can be allocated? -1 for infinity!!!
	 */
	public int MaxAllocatable = -1;

	
	/**
	 * How many tasks are initially allocated from generator? 
	 * If none or error, choose default (10)
	 */
	public int InitialAmount = 10;

}
