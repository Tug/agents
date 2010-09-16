package centralized;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import epfl.lia.logist.core.topology.City;

public class Solution implements Cloneable
{
	public static final int NULL = -1;
	
	public ArrayList<ArrayList<Integer>> taskOrder;
	public World theWorld;
	private double cost = NULL;
	
	public Solution(World theWorld)
	{
		this.theWorld = theWorld;
		int Nv = theWorld.Nv;
		taskOrder = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<Nv; i++)
		{
			taskOrder.add(new ArrayList<Integer>());
		}
	}
	
	public Solution(Solution A1)
	{
		this.theWorld = A1.theWorld;
		taskOrder = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<A1.taskOrder.size(); i++)
		{
			this.taskOrder.add(new ArrayList<Integer>(A1.taskOrder.get(i)));
		}
	}
	
	public void ChangingVehicle(int v1 ,int v2)
	{
		ArrayList<Integer> v1tasks = taskOrder.get(v1);
		Integer t = v1tasks.get(0);
		v1tasks.remove(0);
		v1tasks.remove(t);
		taskOrder.get(v2).add(t);
		taskOrder.get(v2).add(t);
	}
	
	public void ChangingVehicle2(int v1 ,int v2, int nbTasks)
	{
		ArrayList<Integer> v1tasks = taskOrder.get(v1);
		for(int n=0; n<nbTasks; n++)
		{
			Integer t = v1tasks.get(n);
			v1tasks.remove(n);
			v1tasks.remove(t);
			taskOrder.get(v2).add(0,t);
			taskOrder.get(v2).add(0,t);
		}
	}
	
	public void ChangingTaskOrder(int vi, int tIdx1, int tIdx2)
	{
		swap(taskOrder.get(vi), tIdx1, tIdx2);
	}
	
	public boolean vehicleOverloaded(int vi)
	{
		ArrayList<Integer> tasks = taskOrder.get(vi);
		double currentWeight = 0;
		boolean[] HBP = new boolean[theWorld.Nt];
		Arrays.fill(HBP, false);
		double capacity = theWorld.getVehicleProperties(vi).Capacity;
		for(int i=0; i<tasks.size(); i++)
		{
			Integer t = tasks.get(i);
			double tWeight = theWorld.taskList.get(t).getWeight();
			// if task has been pickup, it means we are delivering it now
			// so we discharge the vehicle
			if(HBP[t])
				currentWeight -= tWeight;
			// else we are picking it up so we charge the vehicle
			else
				currentWeight += tWeight;
			HBP[t] = !HBP[t];
			if(currentWeight > capacity) return true;
		}
		return false;
	}
	
	public boolean overloaded()
	{
		for(int vi=0; vi<taskOrder.size(); vi++)
		{
			if(vehicleOverloaded(vi)) return true;
		}
		return false;
	}
	
	protected Object clone() throws CloneNotSupportedException
	{
		return new Solution(this);
	}
	
	private <T> void swap(List<T> list, int ida, int idb)
	{
		T temp = list.get(ida);
		list.set(ida, list.get(idb));
		list.set(idb, temp);
	}
	
	private void computeCost()
	{
		double C = 0.0;
		for(int vi=0; vi<taskOrder.size(); vi++)
		{
			double dist = 0.0;
			ArrayList<Integer> viList = taskOrder.get(vi);
			String currentCityStr = theWorld.getVehicleProperties(vi).Home;
			City currentCity = theWorld.topology.getCity(currentCityStr);
			// HBP = has been picked up
			boolean[] HBP = new boolean[theWorld.Nt];
			Arrays.fill(HBP, false);
			for(Integer t : viList)
			{
				String city2Str;
				if(HBP[t]) {
					city2Str = theWorld.taskList.get(t).getDeliveryCity();
				} else {
					city2Str = theWorld.taskList.get(t).getPickupCity();
					HBP[t] = true;
				}
				City city2 = theWorld.topology.getCity(city2Str);
				dist += theWorld.topology.shortestDistanceBetween(currentCity, city2);
				currentCity = city2;
			}
			C += dist * theWorld.getVehicleProperties(vi).CostPerKm;
		}
		this.cost = C;
	}
	
	public double getCost()
	{
		if(this.cost == NULL) computeCost();
		return cost;
	}
	
	public int hashCode()
	{
		return taskOrder.hashCode();
	}
	
	public static Solution CreateRandomSolution(World theWorld)
	{
		Solution s = new Solution(theWorld);
		for(int i=0; i<theWorld.Nt; i++)
		{
			int vi = Util.randomInt(0, theWorld.Nv-1);
			ArrayList<Integer> tasksVi = s.taskOrder.get(vi);
			int p = Util.randomInt(0, tasksVi.size());
			tasksVi.add(p,i);
		}
		for(int vi=0; vi<theWorld.Nv; vi++)
		{
			ArrayList<Integer> tasksVi = s.taskOrder.get(vi);
			ArrayList<Integer> tasksViCopy = (ArrayList<Integer>)tasksVi.clone();
			for(int i=0; i<tasksViCopy.size(); i++)
			{
				int p = Util.randomInt(0, tasksVi.size());
				tasksVi.add(p,tasksViCopy.get(i));
			}
		}
		return s;
	}

}
