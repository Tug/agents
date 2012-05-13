package centralized;

import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.Task;

public class World {

	//private static World instance;
	
	public ArrayList<AgentProperties> vehicleList;
	public Topology topology;
	public ArrayList<Task> taskList;
	public int Nv = 0;
	public int Nt = 0;
	/*
	public static World getInstance()
	{
        if (null == instance) {
            instance = new World();
        }
        return instance;
    }
	*/
	public World(ArrayList<Task> taskList,
				 ArrayList<AgentProperties> vehicleList,
				 Topology topology)
	{
		setTopology(topology);
		setTaskList(taskList);
		setVehicleList(vehicleList);
	}
	
	public void setTaskList(ArrayList<Task> taskList)
	{
		this.taskList = taskList;
		this.Nt = getTasksNumber();
	}
	
	public void setVehicleList(ArrayList<AgentProperties> vehicleList)
	{
		this.vehicleList = vehicleList;
		this.Nv = getVehiculesNumber();
	}
	
	public void setTopology(Topology topology)
	{
		this.topology = topology;
	}

	public int getTasksNumber()
	{
		return taskList.size();
	}
	
	public int getVehiculesNumber()
	{
		return vehicleList.size();
	}
	
	public AgentProperties getVehicleProperties(int idv)
	{
		return vehicleList.get(idv);
	}
	
	public void addTask(Task t)
	{
		taskList.add(t);
		Nt++;
	}
	
	public void removeTask(Task t)
	{
		taskList.remove(t);
		Nt--;
	}
	
	public void removeLastTask()
	{
		taskList.remove(taskList.size()-1);
		Nt--;
	}
	
	public boolean OverloadPossible()
	{
		double maximalWeight = 0;
		for(int i=0; i<Nt; i++)
		{
			maximalWeight += taskList.get(i).getWeight();
		}
		for(int vi=0; vi<Nv; vi++)
		{
			if(vehicleList.get(vi).Capacity < maximalWeight)
				return true;
		}
		return false;
	}
	
	public double getCost(HashMap<String,Plan> plans)
	{
		double cost=0;
		for(String agentName : plans.keySet())
		{
			Plan p = plans.get(agentName);
			for(AgentProperties ap : vehicleList)
			{
				if(ap.Name.equals(agentName))
				{
					cost += p.getCost(ap.CostPerKm);
				}
			}
		}
		return cost;
	}
	
}
