package auction;

import java.util.ArrayList;
import java.util.HashMap;

import centralized.NonOptimalCooperativePlanner;
import centralized.Solution;
import centralized.World;
import centralized.tabu.TabuSearch;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;

public class Opponent extends Company
{
	public static final int Nv = 2;
	public static final boolean INTELLIGENT = false;
	
	protected int idOpponent;
	
	public Opponent(int idOpponent, World world, ProbabilityDistribution mProbDistribution)
	{
		super(world, mProbDistribution);
		this.idOpponent = idOpponent;
	}
	
	@Override public double considerTask(Task t, double maxTimeSec)
	{
		if(!INTELLIGENT)
		{
			City pickupCity = world.topology.getCity( t.getPickupCity() );
			City deliveryCity = world.topology.getCity( t.getDeliveryCity() );
			double totalDistance = world.topology.shortestDistanceBetween( pickupCity, deliveryCity );
			myTasks.add(t);
			lastTask = t;
			lastTotalDistance = totalDistance;
			return 0;
		}
		return super.considerTask(t, maxTimeSec);
	}
	
	@Override public void auctionResults(boolean win, Double[] bids)
	{
		lastBid = bids[idOpponent];
		if(win)
		{
			payAmount += lastBid * lastTotalDistance;
		}
		else
		{
			myTasks.remove(myTasks.size()-1);
		}
		bidsWon.add(win);
	}
	
	@Override public void addOpponents(int id)
	{
		// do nothing
	}

}
