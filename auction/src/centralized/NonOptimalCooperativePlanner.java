package centralized;

import java.util.ArrayList;
import java.util.HashMap;


import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.Task;

public class NonOptimalCooperativePlanner {

	private World myWorld;
	private SearchAlgorithm search;
	
	public NonOptimalCooperativePlanner(World myWorld)
	{
		this.myWorld = myWorld;
	}
	
	public NonOptimalCooperativePlanner( ArrayList<Task> taskList, 
			  	ArrayList<AgentProperties> vehicleList, 
			  	Topology topology)
	{
		this(new World(taskList, vehicleList, topology));
	}
	
	public void setSearchAlgorithm(SearchAlgorithm search)
	{
		this.search = search;
		search.setWorld(myWorld);
	}
	
	public HashMap<String, Plan> getPlans()
	{
		Solution s = getSolution();
		if(s == null) return null;
		return getPlans(s);
	}
	
	public HashMap<String, Plan> getPlans(Solution s)
	{
		HashMap<String, Plan> plans = new HashMap<String, Plan>();
		for(int idv=0; idv<myWorld.Nv; idv++)
		{
			AgentProperties ap = myWorld.getVehicleProperties(idv);
			VehicleActions va = new VehicleActions(myWorld, idv,myWorld.topology.getCity(ap.Home));
			va.computeActions(s.taskOrder.get(idv));
			plans.put(ap.Name, va.getPlan());
		}
		return plans;
	}
	
	public Solution getSolution()
	{
		if(myWorld.taskList == null || myWorld.taskList.isEmpty())
			return null;
		return search.run();
	}
	

}
