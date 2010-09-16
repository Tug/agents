package org.logist.centralized;


import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.task.Task;

public class Company
{
	public World theWorld;
	
	public Company()
	{
		this.theWorld = World.getInstance();
	}
	
	public double getTotalCost(Solution s)
	{
		double C = 0.0;
		for(int i=0; i<s.nextTask.length; i++)
		{
			if(i<theWorld.Nt) {
				C += (dist(i,s.nextTask[i]) + length(s.nextTask[i])) * cost(s.vehicule[i]);
			} else {
				C += (dist(i,s.nextTask[i]) + length(s.nextTask[i])) * cost(i);
			}
		}
		return C;
	}
	
	public double dist(int idti, int idtj)
	{
		if(idtj == Solution.NULL) return 0;
		String cistr;
		if(idti >= theWorld.Nt)
			cistr = theWorld.getVehiculeProperties(idti-theWorld.Nt).Home;
		else
			cistr = theWorld.taskList.get(idti).getDeliveryCity();
		City ci = theWorld.topology.getCity(cistr);
		City cj = theWorld.topology.getCity(theWorld.taskList.get(idtj).getPickupCity());
		return theWorld.topology.shortestDistanceBetween(ci, cj);
	}
	
	public double length(int idti)
	{
		if(idti == Solution.NULL) return 0;
		Task ti = theWorld.taskList.get(idti);
		City ci = theWorld.topology.getCity(ti.getPickupCity());
		City cj = theWorld.topology.getCity(ti.getDeliveryCity());
		return theWorld.topology.shortestDistanceBetween(ci, cj);
	}
	
	public double cost(int vk)
	{
		vk -= theWorld.Nt;
		return theWorld.getVehiculeProperties(vk).CostPerKm;
	}
	
}
