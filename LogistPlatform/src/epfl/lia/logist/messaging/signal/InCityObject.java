package epfl.lia.logist.messaging.signal;

/* import table */
import java.util.ArrayList;
import epfl.lia.logist.task.TaskDescriptor;


/**
 * This object holds information for the <in-city> signal
 * sent to simple agents...
 */
public class InCityObject {
	
	/* The name of the current city */
	public String Name;
	
	/* The list of currently available tasks */
	public ArrayList<TaskDescriptor> Tasks;
	
	/* The list of neighbors of this city */
	public ArrayList<String> Neighbors;
}
