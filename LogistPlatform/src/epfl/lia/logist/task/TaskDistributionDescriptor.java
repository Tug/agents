package epfl.lia.logist.task;

/* import table */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


/**
 * Holds the parameters for the creation of a task distribution
 * object.
 */
public class TaskDistributionDescriptor {
	
	/* The name of the task distribution */
	public String Name;
	
	/* can be anything in { discrete, probabilistic } */
	public String Type;

	/* a set of parameters for the distribution */
	public Properties Props;

	/* the list of task descriptors */
	public ArrayList<TaskDescriptor> TaskDescriptorList = null;
	
	/* a map of density functions to use */
	public HashMap<String,Properties> DensityFunctions = null;

	/* the list of probability descriptors */
	public ArrayList<ProbabilityDescriptor> ProbDescriptorList = null;
}
