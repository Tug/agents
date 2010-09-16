package org.logist.centralized;

import java.util.ArrayList;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.Task;

public class World {

	private static World instance;
	
	public ArrayList<AgentProperties> vehicleList;
	public Topology topology;
	public ArrayList<Task> taskList;
	public int Nv;
	public int Nt;
	
	public static World getInstance()
	{
        if (null == instance) {
            instance = new World();
        }
        return instance;
    }
	
	private World(){}
	
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
	
	public AgentProperties getVehiculeProperties(int idv)
	{
		return vehicleList.get(idv);
	}
	
}
