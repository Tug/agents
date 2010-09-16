package auction;

import java.util.ArrayList;
import java.util.HashMap;

import centralized.NonOptimalCooperativePlanner;
import centralized.Solution;
import centralized.Util;
import centralized.World;
import centralized.tabu.TabuSearch;
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskManager;

public class Company
{
	protected static final int minNbOfTasks = 6;
	protected static final int maxNbOfTasks = 10;
	protected static final int nbOfSets = 5;
	
	//protected boolean WINMODE = false;
	
	public World world;
	protected double currentSolCost = 0;
	protected double expectedBenefit = 0;
	protected NonOptimalCooperativePlanner planner;
	protected int Nc; // Number of cities
	protected double lastBid = 0;
	protected ArrayList<Boolean> bidsWon;
	protected Task lastTask;
	protected ArrayList<Opponent> opponents;
	protected BidderCompanyBehavior behavior;
	protected double payAmount = 0;
	protected double lastTotalDistance = 1;
	protected MyTaskGenerator taskGen;
	protected ProbabilityDistribution mProbDistribution;
	protected ArrayList<Task> myTasks;
	protected ArrayList<ArrayList<Task>> taskSets;
	protected double redemption;
	protected double minimumBid;
	
	public Company(World world, ProbabilityDistribution mProbDistribution)
	{
		this.world = world;
		this.Nc = world.topology.getCities().size();
		this.planner = new NonOptimalCooperativePlanner(world);
		this.myTasks = new ArrayList<Task>();
		this.minimumBid = world.getVehicleProperties(0).CostPerKm;
		this.opponents = new ArrayList<Opponent>();
		this.bidsWon = new ArrayList<Boolean>();
		this.mProbDistribution = mProbDistribution;
		this.taskGen = new MyTaskGenerator(mProbDistribution);
		this.taskSets = new ArrayList<ArrayList<Task>>();
		initTaskSets();
	}
	
	public double considerTask(Task t, double maxTimeSec)
	{
		//if(WINMODE) return 1;
		for(Opponent opponent : opponents)
		{
			opponent.considerTask(t, 0);
		}
		City pickupCity = world.topology.getCity( t.getPickupCity() );
		City deliveryCity = world.topology.getCity( t.getDeliveryCity() );
		double totalDistance = world.topology.shortestDistanceBetween( pickupCity, deliveryCity );
		
		double maxTimePerSearch = maxTimeSec*1000/taskSets.size() - 20;
		// if first task
		if(bidsWon.isEmpty())
		{
			maxTimePerSearch /= 2;
			currentSolCost = getAverageCost(maxTimePerSearch);
			redemption = currentSolCost/minNbOfTasks;
		}
		myTasks.add(t);
		checkTaskSets();
		double nextSolCost = getAverageCost(maxTimePerSearch);
		//double nextSolCost = getMaximumCost(maxTimePerSearch);
		//double nextSolCost = getMinimumCost(maxTimePerSearch);
		double marginalCost = nextSolCost - currentSolCost;
		computeExpectedBenefit();
		
		double bid = ((marginalCost + redemption) / totalDistance) * (1 + expectedBenefit);
		
		if(bid < minimumBid) bid = minimumBid;
		
		lastBid = bid;
		currentSolCost = nextSolCost;
		lastTask = t;
		lastTotalDistance = totalDistance;
		
		behavior.print("task reward     = "+(t.getRewardPerKm()*totalDistance));
		behavior.print("marginalCost    = "+marginalCost);
		behavior.print("expectedBenefit = "+expectedBenefit);
		behavior.print("redemption      = "+redemption);
		behavior.print("bid             = "+bid);
		
		return bid;
	}
	
	public void auctionResults(boolean win, Double[] bids)
	{
		//init opponents
		int idMyBid = 0;
		boolean opponentsNonInitialized = (opponents.size() < bids.length-1);
		for(int i=0; i<bids.length; i++)
		{
			if(bids[i] == lastBid)
				idMyBid = i;
			else if(opponentsNonInitialized)
			{
				addOpponents(i);
			}
		}
		if(win)
		{
			payAmount += lastBid * lastTotalDistance; //lastTask.getRewardPerKm() * lastTotalDistance;
		}
		else
		{
			myTasks.remove(myTasks.size()-1);
		}
		for(Opponent opponent : opponents)
		{
			// we suppose that every opponents won/lose
			opponent.auctionResults(!win, bids);
		}
		bidsWon.add(win);
	}
	
	public void computeExpectedBenefit()
	{
		// if first task, we let expectedBenefit = 0
		/*
		if(bidsWon.isEmpty()) return;
		double minOpBenefit = Double.POSITIVE_INFINITY;
		for(Opponent opponent : opponents)
		{
			double opBenefit = lastBid * opponent.lastBid;
			if(minOpBenefit > opBenefit)
				minOpBenefit = opBenefit;
		}
		double deltaBenef = Math.abs(minOpBenefit - expectedBenefit)/2;
		if(lastBidWon())
		{
			expectedBenefit += deltaBenef;
		}
		else
		{
			expectedBenefit -= deltaBenef;
		}
		if(expectedBenefit < 0.75) expectedBenefit = 0.75;
		if(expectedBenefit > 1.25) expectedBenefit = 1.25;
		*/
	}
	
	public void addOpponents(int id)
	{
		ArrayList<Task> emptyTaskList = new ArrayList<Task>();
		World opponentWorld = new World(emptyTaskList, world.vehicleList, world.topology);
		Opponent o = new Opponent(id, opponentWorld, mProbDistribution);
		o.considerTask(lastTask, 1000);
		opponents.add(o);
	}
	
	public void initTaskSets()
	{
		for(int i=0; i<nbOfSets; i++)
		{
			taskSets.add(taskGen.generate(minNbOfTasks));
		}
	}
	
	public void checkTaskSets()
	{
		if(myTasks.size() < maxNbOfTasks)
		{
			if(myTasks.size() >= maxNbOfTasks-minNbOfTasks)
			{
				for(int i=0; i<taskSets.size(); i++)
				{
					int r = Util.randomInt(taskSets.get(i).size()-1);
					taskSets.get(i).remove(r);
				}
			}
			/*
			int nbOfTasks = minNbOfTasks;
			while(nbOfTasks < myTasks.size())
			{
				nbOfTasks += minNbOfTasks;
			}
			int nbOfTaskToGen = nbOfTasks - myTasks.size();
			if(nbOfTaskToGen > taskSets.get(0).size())
			{
				for(int i=0; i<taskSets.size(); i++)
				{
					taskSets.get(i).addAll(taskGen.generate(nbOfTaskToGen));
				}
			}
			*/
		}
		else if(myTasks.size() == maxNbOfTasks)
		{
			realMode();
		}
	}
	
	public void checkTime(double maxTimeSec)
	{
		int Nt = myTasks.size() + taskSets.get(0).size();
		while(maxTimeSec < Nt*taskSets.size())
		{
			taskSets.remove(taskSets.size()-1);
			behavior.print("time too short ! Reducing taskSet to "+taskSets.size());
		}
		if(taskSets.size() == 0) 
		{
			realMode();
			//WINMODE = true;
		}
	}
	
	public HashMap<String,Plan> getPlans(double maxTimeSec)
	{
		if(world.taskList.isEmpty())
			return null;
		else
		{
			world.setTaskList(myTasks);
			planner.setSearchAlgorithm(new TabuSearch(15, 300000, maxTimeSec*1000-20));
			return planner.getPlans();
		}
	}
	
	public void setBidderCompanyBehavior(BidderCompanyBehavior behavior)
	{
		this.behavior = behavior;
	}
	
	public boolean lastBidWon()
	{
		return bidsWon.get(bidsWon.size()-1).booleanValue();
	}
	
	public double getWinningRatio()
	{
		int wins = 0;
		for(int i=0; i<bidsWon.size(); i++)
		{
			if(bidsWon.get(i)) wins++;
		}
		return (double)wins/bidsWon.size();
	}
	
	public double getAverageCost(double maxTimePerSearch)
	{
		double marginalCostSum = 0;
		for(int i=0;i <taskSets.size(); i++)
		{
			ArrayList<Task> tasks = new ArrayList<Task>(taskSets.get(i));
			tasks.addAll(myTasks);
			world.setTaskList(taskSets.get(i));
			planner.setSearchAlgorithm(new TabuSearch(15, 300000, maxTimePerSearch));
			marginalCostSum += planner.getSolution().getCost();
		}
		return marginalCostSum/taskSets.size();
	}
	
	public double getMaximumCost(double maxTimePerSearch)
	{
		double marginalCostMax = 0;
		for(int i=0;i <taskSets.size(); i++)
		{
			ArrayList<Task> tasks = new ArrayList<Task>(taskSets.get(i));
			tasks.addAll(myTasks);
			world.setTaskList(taskSets.get(i));
			planner.setSearchAlgorithm(new TabuSearch(15, 300000, maxTimePerSearch));
			double marginalCost = planner.getSolution().getCost();
			if(marginalCostMax < marginalCost) marginalCostMax = marginalCost;
		}
		return marginalCostMax;
	}
	
	public double getMinimumCost(double maxTimePerSearch)
	{
		double marginalCostMin = 0;
		for(int i=0;i <taskSets.size(); i++)
		{
			ArrayList<Task> tasks = new ArrayList<Task>(taskSets.get(i));
			tasks.addAll(myTasks);
			world.setTaskList(taskSets.get(i));
			planner.setSearchAlgorithm(new TabuSearch(15, 300000, maxTimePerSearch));
			double marginalCost = planner.getSolution().getCost();
			if(marginalCostMin > marginalCost) marginalCostMin = marginalCost;
		}
		return marginalCostMin;
	}
	
	public double getPayAmount()
	{
		return payAmount;
	}
	
	public void realMode()
	{
		taskSets.clear();
		taskSets.add(new ArrayList<Task>());
	}
	
}
