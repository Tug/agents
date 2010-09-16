package centralized;

import java.util.ArrayList;
import java.util.LinkedList;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.task.Task;

public class SLS extends SearchAlgorithm {
	
	protected double p;
	protected int nbOfIteration;
	protected int maxTime;
	protected Solution best;
	
	public SLS(double p, int nbOfIteration)
	{
		this(p, nbOfIteration, Integer.MAX_VALUE);
	}
	
	public SLS(double p, int nbOfIteration, int maxTime)
	{
		super();
		this.p = p;
		this.nbOfIteration = nbOfIteration;
		this.maxTime = maxTime;
	}

	public Solution run()
	{
		long start = System.currentTimeMillis();
		Solution A, Aold;
		A = SelectInitialSolution();
		best = A;
		for(int i=0; i<nbOfIteration; i++)
		{
			Aold = new Solution(A);
			ArrayList<Solution> N = ChooseNeighbours(Aold);
			A = LocalChoice(N, Aold, p);
			if(i % 10 == 0 && System.currentTimeMillis() - start >= maxTime)
			{
				System.out.println("nb of it : "+i);
				break;
			}
		}
		return best;
	}
	
	protected ArrayList<Solution> ChooseNeighbours(Solution Aold)
	{
		ArrayList<Solution> N = new ArrayList<Solution>();
		int vi = getRandomVehicle(Aold);
		// Applying the changing vehicle operator :
		for(int vj=0; vj<theWorld.vehicleList.size(); vj++)
		{
			if(vj != vi)
			{
				int t = Aold.taskOrder.get(vi).get(0);
				if(theWorld.taskList.get(t).getWeight()<=theWorld.vehicleList.get(vj).Capacity)
				{
					Solution A = new Solution(Aold);
					A.ChangingVehicle(vi,vj);
					//A.ChangingVehicleIntelligent(vi,vj);
					N.add(A);
				}
				//int r = Util.randomInt(0, Aold.taskOrder.get(vi).size()-1);
				/*
				for(int r=0; r<Aold.taskOrder.get(vi).size(); r++)
				{
					Integer t = Aold.taskOrder.get(vi).get(r);
					if(theWorld.taskList.get(t).getWeight()<=theWorld.vehicleList.get(vj).Capacity)
					{
						Solution A = new Solution(Aold);
						//A.ChangingVehicle(vi,vj);
						A.ChangingVehicle(vi,vj,r);
						N.add(A);
					}
				}//*/
			}
		}
		// Applying the Changing task order operator :
		// compute the number of tasks of the vehicle
		
		for(int vj=0; vj<theWorld.vehicleList.size(); vj++)
		{
			int length = Aold.taskOrder.get(vj).size();
			if(length>=4)
			{
				for(int tIdx1=0; tIdx1<length-1; tIdx1++)
				{
					for(int tIdx2=tIdx1+1; tIdx2<length; tIdx2++)
					{
						Solution A = new Solution(Aold);
						A.ChangingTaskOrder(vj,tIdx1,tIdx2);
						if(!A.vehicleOverloaded(vj))
							N.add(A);
					}
				}
			}
		}
		return N;
	}
	
	protected Solution LocalChoice(ArrayList<Solution> N, Solution Aold, double p)
	{
		//select A from the set of N, the one that improves f
		Solution A = N.get(0);
		ArrayList<Solution> possibleA = new ArrayList<Solution>();
		for(int i=0; i<N.size() ; i++)
		{
			Solution actual = N.get(i);
			if(actual.getCost() < A.getCost())
			{
				possibleA.clear();
				possibleA.add(actual);
				A = actual;
			}
			else if(actual.getCost() == A.getCost())
			{
				possibleA.add(actual);
			}
		}
		// if there are more than one with the same value, it choose one A of them randomly
		if(!possibleA.isEmpty())
		{
			A = Util.getRandomElement(possibleA);
			if(best.getCost() > A.getCost())
				best = A;
		}
		// and then with the new A, return A 	with probability p
		// oooooooooooooorrrrrrrrr, return Aold with probability p-1
		// p is a parameter of the algorithm {0.3..0.5}
		if(Math.random() < p)
			return A;
		else return Aold;
	}
	
	/*
	protected Solution updateBestSolution(ArrayList<Solution> N, Solution best)
	{
		for (int i=1; i<N.size() ; i++)
		{
			Solution Si = N.get(i);
			if(Si.getCost() < best.getCost())
				best = Si;
		}
		return best;
	}
	*/
	
	protected int getRandomVehicle(Solution A)
	{
		ArrayList<Integer> vs = new ArrayList<Integer>();
		for(int vi=0; vi<theWorld.vehicleList.size(); vi++)
		{
			if(!A.taskOrder.get(vi).isEmpty()) vs.add(vi);
		}
		return Util.getRandomElement(vs);
	}
	
	
}
