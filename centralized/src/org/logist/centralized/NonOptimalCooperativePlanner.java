package org.logist.centralized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.Task;

public class NonOptimalCooperativePlanner {

	private World theWorld;
	private Company company;
	
	public NonOptimalCooperativePlanner( ArrayList<Task> taskList, 
			  	ArrayList<AgentProperties> vehicleList, 
			  	Topology topology)
	{
		this.theWorld = World.getInstance();
		theWorld.setTaskList(taskList);
		theWorld.setVehicleList(vehicleList);
		theWorld.setTopology(topology);
		company = new Company();
	}
	
	public HashMap<String, Plan> getPlans(Solution s)
	{
		HashMap<String, Plan> plans = new HashMap<String, Plan>();
		for(int i=0; i<theWorld.Nv; i++)
		{
			int idv = i + theWorld.Nt;
			AgentProperties ap = theWorld.getVehiculeProperties(i);
			VehiculeActions va = new VehiculeActions(idv,theWorld.topology.getCity(ap.Home));
			va.computeActions(s.nextTask);
			plans.put(ap.Name, va.getPlan());
		}
		return plans;
	}
	
	public Solution SLS(double p, int nbOfIteration)
	{
		Solution A, Aold;
		A = SelectInitialSolution();
		for(int i=0; i<nbOfIteration; i++)
		{
			Aold = new Solution(A);
			ArrayList<Solution> N = ChooseNeighbours(Aold);
			A = LocalChoice(N, Aold, p);
		}
		return A;
	}
	
	private Solution SelectInitialSolution()
	{
		Solution S = new Solution();
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
		idvmax += theWorld.Nt;
		Arrays.fill(S.vehicule, idvmax);
		for(int i=0; i<theWorld.Nt-1; i++)
			S.nextTask[i] = i+1;
		S.nextTask[idvmax] = 0;
		S.UpdateTime(idvmax);
		return S;
	}
	
	private ArrayList<Solution> ChooseNeighbours(Solution Aold)
	{
		ArrayList<Solution> N = new ArrayList<Solution>();
		int vi = getRandomVehicule(Aold);
		// Applying the changing vehicle operator :
		for(int i=0; i<theWorld.vehicleList.size(); i++)
		{
			int vj = i + theWorld.Nt;
			if(vj != vi)
			{
				int t = Aold.nextTask[vi];
				if(theWorld.taskList.get(t).getWeight()<=theWorld.vehicleList.get(i).Capacity)
				{
					Solution A = new Solution(Aold);
					A.ChangingVehicule(vi,vj);
					N.add(A);
				}
			}
		}
		// Applying the Changing task order operator :
		// compute the number of tasks of the vehicle
		int length = 0;
		int t = vi; // current task in the list
		while(t != Solution.NULL)
		{
		   t = Aold.nextTask[t];
		   length++;
		}
		if(length>=2)
		{
			for(int tIdx1=1; tIdx1<length-1; tIdx1++)
			{
				for(int tIdx2=tIdx1+1; tIdx2<length; tIdx2++)
				{
					Solution A = new Solution(Aold);
					A.ChangingTaskOrder(vi,tIdx1,tIdx2);
					N.add(A);
				}
			}
		}
		return N;
	}
	
	private Solution LocalChoice(ArrayList<Solution> N, Solution Aold, double p)
	{
		//select A from the set of N, the one that improves f
		Solution A = N.get(0);
		double costOfA = company.getTotalCost(A);
		ArrayList<Solution> possibleA = new ArrayList<Solution>();
		for (int i=1; i<N.size() ; i++)
		{
			Solution actual = N.get(i);
			double costOfActual= company.getTotalCost(actual);
			if(costOfActual < costOfA)
			{
				A=actual;
				costOfA = costOfActual;
				possibleA.clear();
			}
			else if(costOfActual == costOfA)
			{
				possibleA.add(actual);
			}
		}
		// if there are more than one with the same value, it choose one A of them randomly
		if(!possibleA.isEmpty())
		{
			A = getRandomElement(possibleA);
		}
		// and then with the new A, return A 	with probability p
		// oooooooooooooorrrrrrrrr, return Aold with probability p-1
		// p is a parameter of the algorithm {0.3..0.5}
		if(Math.random() < p)
			return A;
		else return Aold;
	}
	
	private int getRandomVehicule(Solution A)
	{
		ArrayList<Integer> vs = new ArrayList<Integer>();
		for(int i=0; i<theWorld.vehicleList.size(); i++)
		{
			int vi = i + theWorld.Nt;
			if(A.nextTask[vi] != Solution.NULL) vs.add(vi);
		}
		return getRandomElement(vs);
	}
	
	public static <T> T getRandomElement(ArrayList<T> list)
	{
		int rand = (int)(Math.random() * (list.size()-1));
		return list.get(rand);
	}

}
