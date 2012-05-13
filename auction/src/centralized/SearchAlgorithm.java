package centralized;

import java.util.ArrayList;
import java.util.LinkedList;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.task.Task;

public abstract class SearchAlgorithm {

	protected World theWorld;
	
	public SearchAlgorithm()
	{
		
	}
	
	abstract public Solution run();
	
	public void setWorld(World aWorld)
	{
		this.theWorld = aWorld;
	}
	
	protected Solution SelectInitialSolution()
	{
		Solution S = new Solution(theWorld);
		double max = 0;
		int idvmax = 0;
		for(int i=0; i<theWorld.vehicleList.size(); i++)
		{
			AgentProperties ap = theWorld.vehicleList.get(i);
			if(max < ap.Capacity)
			{
				max = ap.Capacity;
				idvmax = i;
			}
		}
		ArrayList<Integer> list = S.taskOrder.get(idvmax);
		for(int i=0; i<theWorld.Nt; i++)
		{
			list.add(i);
			list.add(i);
		}
		return S;
	}
	
	protected Solution SelectInitialSolution2()
	{
		Solution S = new Solution(theWorld);
		for(int i=0; i<theWorld.Nt; i++)
		{
			Task t = theWorld.taskList.get(i);
			City pickupCity = theWorld.topology.getCity(t.getPickupCity());
			// find closest vehicle
			double min = Double.POSITIVE_INFINITY;
			int bestVehicle = 0;
			for(int j=0; j<theWorld.vehicleList.size(); j++)
			{
				AgentProperties ap = theWorld.vehicleList.get(j);
				City start = theWorld.topology.getCity(ap.Home);
				double dist = theWorld.topology.shortestDistanceBetween(start, pickupCity);
				if(dist < min && ap.Capacity >= t.getWeight())
				{
					min = dist;
					bestVehicle = j;
				}
			}
			S.taskOrder.get(bestVehicle).add(i);
			S.taskOrder.get(bestVehicle).add(i);
		}
		return S;
	}
	
}
