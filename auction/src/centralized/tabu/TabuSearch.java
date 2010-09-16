package centralized.tabu;

import java.util.ArrayList;
import java.util.Arrays;

import centralized.*;


public class TabuSearch extends SearchAlgorithm
{
	protected static final int minTenure = 1;
	protected static final int maxTenure = 25;
	protected static final int searchTime = 150;
	protected static final int intensificationTime = 30;
	protected static final int diversificationTime = 70;
	
	protected int[] tabuList;
	protected int currTabuId = 0;
	protected int tenure;
	protected int tenureInit;
	protected ArrayList<Solution> eliteList;
	//protected boolean diversification = false;
	protected int currentIt = 1;
	protected int nbOfIteration;
	protected double maxTime;
	protected Solution best;
	protected int cycleTime = searchTime+intensificationTime+diversificationTime;
	
	public TabuSearch(int tenure, int nbOfIteration)
	{
		this(tenure, nbOfIteration, Integer.MAX_VALUE);
	}
	
	public TabuSearch(int tenure, int nbOfIteration, double maxTime)
	{
		this.tenure = tenure;
		this.tenureInit = tenure;
		this.nbOfIteration = nbOfIteration;
		this.maxTime = maxTime;
		tabuList = new int[tenure];
		Arrays.fill(tabuList, 0);
		eliteList = new ArrayList<Solution>();
	}
	
	@Override public Solution run()
	{
		long start = System.currentTimeMillis();
		Solution A = SelectInitialSolution();
		best = A;
		for(int k=1; k<=nbOfIteration; k++)
		{
			A = applyStrategy(k, A);
			Solution Aold = new Solution(A);
			ArrayList<Move> M = ChooseNeighborMoves(Aold);
			if(M.isEmpty()) break;
			A = LocalChoice(M, Aold);
			if(k % 10 == 0 && System.currentTimeMillis() - start >= maxTime)
			{
				//System.out.println("nb of it : "+k);
				break;
			}
		}
		return best;
	}
	
	protected ArrayList<Move> ChooseNeighborMoves(Solution Aold)
	{
		ArrayList<Move> M = new ArrayList<Move>();
		int vi = getRandomVehicle(Aold);
		for(int vj=0; vj<theWorld.vehicleList.size(); vj++)
		{
			if(vj != vi)
			{
				int t = Aold.taskOrder.get(vi).get(0);
				if(theWorld.taskList.get(t).getWeight()<=theWorld.vehicleList.get(vj).Capacity)
				{
					M.add(new ChangeVehicleMove(vi, vj));
				}
			}
		}
		for(int vj=0; vj<theWorld.vehicleList.size(); vj++)
		{
			int length = Aold.taskOrder.get(vj).size();
			if(length>=4)
			{
				for(int tIdx1=0; tIdx1<length-1; tIdx1++)
				{
					for(int tIdx2=tIdx1+1; tIdx2<length; tIdx2++)
					{
						M.add(new SwapTaskMove(vj, tIdx1, tIdx2));
					}
				}
			}
		}
		return M;
	}
	/*
	private double evaluate(Solution A, Solution Aold)
	{
		double f = A.getCost();
		double diff = Aold.getCost() - f;
		int deltas = 0;
		int prev = A.taskOrder.get(0).size();
		for(int vi=1; vi<A.taskOrder.size(); vi++)
		{
			int curr = A.taskOrder.get(vi).size();
			deltas += Math.abs(prev-curr);
			prev = curr;
		}
		double fDivers = 0;
		double factor = Math.exp(Util.normalizeFloat(currentIt, 100000, 10));
		double factor2 = Math.exp(Util.normalizeFloat(currentIt, 100000, 15));
		if(diversification)
			fDivers = factor * diff + factor2 * deltas;
		return f+fDivers;
	}
	*/
	protected Solution LocalChoice(ArrayList<Move> M, Solution Aold)
	{
		Solution A = Aold;
		double lowerCost = Double.POSITIVE_INFINITY;
		ArrayList<Solution> possibleA = new ArrayList<Solution>();
		ArrayList<Move> possibleM = new ArrayList<Move>();
		int i = 0;
		int[] tabuListCopy = (int[])tabuList.clone();
		Arrays.sort(tabuListCopy);
		while(i < M.size())
        {
			Move m  = M.get(i);
			Solution actual = m.createSolution(Aold);
			// remove forbidden moves
			if(forbiddenMove(m, actual))
			{
				M.remove(i);
				continue;
			}
			double eval = actual.getCost();//evaluate(actual, Aold);
			// remove if tabu and does not satisfy the aspiration criteria
			if(Arrays.binarySearch(tabuListCopy, M.get(i).hashCode()) >= 0)
        	{
				boolean aspire = (eval < best.getCost());
				if(!aspire)
				{
					M.remove(i);
					continue;
				}
        	}
			if(eval < lowerCost)
			{
				possibleA.clear();
				possibleA.add(actual);
				possibleM.clear();
				possibleM.add(m);
				A = actual;
				lowerCost = eval;
			}
			else if(eval == lowerCost)
			{
				possibleA.add(actual);
				possibleM.add(m);
			}
			i++;
		}
		if(possibleA.size() > 0)
		{
			int x = Util.randomInt(0, possibleA.size()-1);
			A = possibleA.get(x);
			Move m = possibleM.get(x);
			if(best.getCost() > A.getCost())
			{
				best = A;
				if(possibleA.size() == 1)
				{
					eliteList.add(A);
				}
				else
				{
					eliteList = possibleA;
				}
				//System.out.println("best : "+best.getCost());
			}
			tabuList[currTabuId++] = m.getOppositeMove().hashCode();
			if(currTabuId >= tenure) currTabuId = 0;
		}
		return A;
	}
	
	protected Solution Intensification()
	{
		int r = Util.randomInt(0,eliteList.size()-1);
		if(eliteList.size() == 0) return best;
		Solution s = eliteList.get(r);
		eliteList.remove(r);
		return s;
	}
	
	protected Solution Diversification()
	{
		Solution s = Solution.CreateRandomSolution(theWorld);
		if(theWorld.OverloadPossible() && !s.overloaded())
		{
			int i=0;
			while(!s.overloaded() && i < 60)
			{
				s = Solution.CreateRandomSolution(theWorld);
				//System.out.println("rdddd="+i);
				i++;
			}
		}
		return s;
	}
	
	private Solution applyStrategy(int it, Solution A)
	{
		currentIt = it;
		if(it % cycleTime == 0)
		{
			//System.out.println("search "+it);
			setTenure(tenureInit);
			clearTabuList();
			return best;
		}
		else if(it % cycleTime == searchTime)
		{
			//System.out.println("intensification "+it);
			setTenure(minTenure);
			clearTabuList();
			return Intensification();
		}
		else if(it % cycleTime == searchTime+intensificationTime)
		{
			//System.out.println("diversification "+it);
			//diversification = true;
			setTenure(maxTenure);
			clearTabuList();
			return Diversification();
		}
		return A;
	}
	
	private boolean forbiddenMove(Move m, Solution A)
	{
		if(m instanceof SwapTaskMove)
		{
			SwapTaskMove stm = (SwapTaskMove)m;
			if(A.vehicleOverloaded(stm.vid))
			{
				return true;
			}
		}
		return false;
	}
	
	protected int getRandomVehicle(Solution A)
	{
		ArrayList<Integer> vs = new ArrayList<Integer>();
		for(int vi=0; vi<theWorld.vehicleList.size(); vi++)
		{
			if(!A.taskOrder.get(vi).isEmpty()) vs.add(vi);
		}
		return Util.getRandomElement(vs);
	}
	
	private void setTenure(int newVal)
	{
		if(newVal > tenure)
			tabuList = Arrays.copyOf(tabuList, newVal+10);
		else
			Arrays.fill(tabuList, newVal, tenure, 0);
		this.tenure = newVal;
	}
	
	private void clearTabuList()
	{
		Arrays.fill(tabuList, 0);
		currTabuId = 0;
	}
	
}
