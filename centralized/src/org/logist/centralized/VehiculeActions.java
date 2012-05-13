package org.logist.centralized;


import java.util.ArrayList;

import epfl.lia.logist.agent.plan.GDeliverAction;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.task.Task;

public class VehiculeActions
{
	private int id;
	private City currentCity;
	private World theWorld;
	private ArrayList<IGenericAction> actions;
	
	public VehiculeActions(int id, City currentCity)
	{
		this.id = id;
		this.theWorld = World.getInstance();
		this.currentCity = currentCity;
	}
	
	public void computeActions(int[] nextTask)
	{
		actions = new ArrayList<IGenericAction>();
		int t = id;
		while(nextTask[t] != Solution.NULL)
		{
			t = nextTask[t];
			Task task = theWorld.taskList.get(t);
			addActionsFromTask(task);
		}
	}
	
	public Plan getPlan()
	{
		return new Plan(actions);
	}
	
	private void addActionsFromTask(Task t)
	{
		// here the vehicule cannot carry multiple tasks
		// so it delivers as soon as it picks it up
		setPickupAction(t);
		setDeliverAction(t);
	}
	
	private void setPickupAction(Task t)
	{
		currentCity = getLastCity();
		City destination = theWorld.topology.getCity(t.getPickupCity());
		setMoveActions(destination);
		actions.add(new GPickupAction(t));
	}
	
	private void setDeliverAction(Task t)
	{
		currentCity = getLastCity();
		City destination = theWorld.topology.getCity(t.getDeliveryCity());
		setMoveActions(destination);
		actions.add(new GDeliverAction(t));
	}
	
	private void setMoveActions(City destination)
	{
		City prevCity = currentCity;
		City nextCity = currentCity;
		while( !nextCity.match(destination) )
		{
			nextCity = theWorld.topology.moveOnShortestPath(nextCity,destination);
			double distance = theWorld.topology.shortestDistanceBetween(prevCity, nextCity);
			actions.add( new GMoveAction(nextCity, distance) );
			prevCity = nextCity;
		}
	}
	
	private City getLastCity()
	{
		City last = currentCity;
		if(actions != null && !actions.isEmpty())
		{
			IGenericAction action = actions.get(actions.size()-1);
			if(action instanceof GMoveAction)
			{
				last = ((GMoveAction)action).getTarget();
			}
			else if(action instanceof GPickupAction)
			{
				String cityName = ((GPickupAction)action).getTask().getPickupCity();
				last = theWorld.topology.getCity(cityName);
			}
			else if(action instanceof GDeliverAction)
			{
				String cityName = ((GDeliverAction)action).getTask().getDeliveryCity();
				last = theWorld.topology.getCity(cityName);
			}
		}
		return last;
	}
	
	public City getCurrentCity() { return currentCity; }
	
}
