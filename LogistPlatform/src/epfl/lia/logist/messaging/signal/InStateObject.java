package epfl.lia.logist.messaging.signal;

/* import table */
import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.Task;


/**
 * 
 */
public class InStateObject {
	
	/* Holds a reference to the current city object */
	public City CurrentCity;
	
	/* The graph of the world */
	public Topology Graph;
	
	/* A list of tasks per city */
	public HashMap<City,ArrayList<Task>> Tasks;

	/* A list of assigned tasks */
	public ArrayList<Task> AssignedTasks;
	
	/* The list of child vehicles */
	public ArrayList<AgentProperties> Vehicles;

}
