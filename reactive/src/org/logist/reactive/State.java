package org.logist.reactive;


import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.ProbabilityDistribution;

public class State
{
	public static int staticid = 0;
	private int id = 0;
	private City currentCity;
	private City destination;
	
	public State(City currentCity, City destination)
	{
		this.id = staticid++;
		this.currentCity = currentCity;
		this.destination = destination;
	}
	
	public double getDistance()
	{
		return Topology.getInstance().shortestDistanceBetween(currentCity, destination);
	}
	
	public double getRewardPerKm(ProbabilityDistribution probaDist) 
	{
		return probaDist.getRewardPerKm(currentCity, destination);
	}
	
	/**
	 * Probability to have a task from city currentCity to city destination
	 */
	public double getTaskProbability(ProbabilityDistribution probaDist)
	{
		if(hasTask())
		{
			return probaDist.getProbability(currentCity, destination);
		}
		else
		{
			return 0;
		}
	}
	
	public boolean isLinkedWith(State s, Action a)
	{
		boolean sameStates = this.equals(s);
		if(sameStates) return false;
		switch(a.getType())
		{
			case AMT_MOVE:
				// if the destination city of the action 
				// equals the start city of the next state
				return a.isPossible(this, s)
				// if the start city of the current state 
				// and the start city of the next state are neigbours
					&& Topology.getInstance().neighbors(currentCity, s.getCurrentCity());
			case AMT_PICKUP:
				// if s1 has a Task and s1.destination == s2.currentCity
				return (hasTask() && destination.equals(s.getCurrentCity()));
		}
		return false;
	}
	
	public boolean hasTask()
	{
		return destination != null;
	}
	
	public int getId() { return id; }
	public City getCurrentCity() { return currentCity; }
	public City getDestination() { return destination; }
	

	
	@Override public boolean equals(Object o)
	{
		State s = (State)o;
		if(s.getDestination() == null && destination == null)
			return currentCity.equals(s.getCurrentCity());
		else if(s.getDestination() == null || destination == null)
			return false;
		else
			return (   currentCity.equals(s.getCurrentCity())
					&& destination.equals(s.getDestination()) );
	}
}