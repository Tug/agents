package org.logist.deliberative;


import java.util.ArrayList;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.plan.GDeliverAction;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskManager;

public class State extends Node
{
	private ArrayList<TaskDescriptor> tasks;
	private ArrayList<TaskDescriptor> assignedTasks;
	
	private double cost;
	
	private AgentProperties agentProp;
	private City currentCity;
	private City destination;
	private Topology topology;
	private ArrayList<IGenericAction> actions;
	
	/**
	 * Default Constructor for a State.
	 * Used to create and compare two states together.
	 * @param tasks
	 * @param assignedTasks
	 */
	public State(ArrayList<TaskDescriptor> tasks, ArrayList<TaskDescriptor> assignedTasks)
	{
		super();
		this.tasks = tasks;
		this.assignedTasks = assignedTasks;
	}
	
	/**
	 * Constructor for the first state of the tree
	 * @param inStateObject
	 * @param agentProp
	 */
	public State(InStateObject inStateObject, AgentProperties agentProp)
	{
		super();
		this.tasks = new ArrayList<TaskDescriptor>();
		this.assignedTasks = new ArrayList<TaskDescriptor>();
		this.currentCity = inStateObject.CurrentCity;
		this.topology = inStateObject.Graph;
		this.agentProp = agentProp;
		this.cost = 0;
		this.actions = new ArrayList<IGenericAction>();
		for(ArrayList<Task> cityTasks : inStateObject.Tasks.values())
		{
			for(Task t : cityTasks)
			{
				tasks.add(t.getDescriptor());
			}
		}
		for(Task t : inStateObject.AssignedTasks)
		{
			assignedTasks.add(t.getDescriptor());
		}
	}
	
	/**
	 * Constructor for the child states of the tree
	 * @param parent
	 */
	public State(State parent)
	{
		super(parent);
		this.currentCity = parent.getLastCity();
		this.topology = parent.getTopology();
		this.agentProp = parent.getAgentProperties();
		this.actions = new ArrayList<IGenericAction>();
	}
	
	public double getAssignedTasksWeight()
	{
		double weight = 0;
		for (TaskDescriptor td : assignedTasks){
				weight += td.Weight;
		}
		return weight;
	}
	
	public void computeCost()
	{
		this.cost = topology.shortestDistanceBetween(currentCity,destination)*agentProp.CostPerKm;
		setTotalCost(cost);
	}
	/**
	 * Create the child states by calling the State(parent) 
	 * contructor with this state in parameter
	 * @return
	 */
	public void createSiblings()
	{
		for(int i=0; i<tasks.size(); i++)
		{
			State child = new State(this);
			ArrayList<TaskDescriptor> childTasks = new ArrayList<TaskDescriptor>();
			ArrayList<TaskDescriptor> childAssignedTasks = (ArrayList<TaskDescriptor>)assignedTasks.clone();
			int j = 0;
			for(TaskDescriptor td : tasks)
			{
				if(j == i) {
					childAssignedTasks.add(td);
					child.setPickupAction(td);
				} else {
					childTasks.add(td);
				}
				j++;
			}
			child.setTasks(childTasks);
			child.setAssignedTasks(childAssignedTasks);
			child.computeCost();
			if(agentProp.Capacity >= child.getAssignedTasksWeight())
				addSibling(child);
		}
		for(int i=0; i<assignedTasks.size(); i++)
		{
			State child = new State(this);
			ArrayList<TaskDescriptor> childTasks = (ArrayList<TaskDescriptor>)tasks.clone();
			ArrayList<TaskDescriptor> childAssignedTasks = new ArrayList<TaskDescriptor>();
			int j = 0;
			for(TaskDescriptor td : assignedTasks)
			{
				if(j == i)
					child.setDeliverAction(td);
				else
					childAssignedTasks.add(td);
				j++;
			}
			child.setTasks(childTasks);
			child.setAssignedTasks(childAssignedTasks);
			child.computeCost();
			if(agentProp.Capacity >= child.getAssignedTasksWeight())
				addSibling(child);
		}
	}
	
	private void setMoveActions(City destination)
	{
		City prevCity = currentCity;
		City nextCity = prevCity;
		while( !nextCity.equals(destination) )
		{
			nextCity = topology.moveOnShortestPath(nextCity,destination);
			double distance = topology.shortestDistanceBetween(prevCity, nextCity);
			actions.add( new GMoveAction(nextCity, distance) );
			prevCity = nextCity;
		}
	}
	
	private void setPickupAction(TaskDescriptor td)
	{
		destination = topology.getCity(td.PickupCity);
		setMoveActions(destination);
		actions.add( new GPickupAction( TaskManager.getInstance().getTaskFromID(td.ID)) );
	}
	
	private void setDeliverAction(TaskDescriptor td)
	{
		destination = topology.getCity(td.DeliveryCity);
		setMoveActions(destination);
		actions.add( new GDeliverAction( TaskManager.getInstance().getTaskFromID(td.ID)) );
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
				last = topology.getCity(cityName);
			}
			else if(action instanceof GDeliverAction)
			{
				String cityName = ((GDeliverAction)action).getTask().getDeliveryCity();
				last = topology.getCity(cityName);
			}
		}
		return last;
	}
		
	@Override
	public boolean equals(Object o)
	{
		State s = (State)o;
		if( tasks.size() != s.getTasks().size() || assignedTasks.size() != s.getAssignedTasks().size() )
			return false;
		
		if(!tasks.isEmpty())
		{
			for(int i=0; i<tasks.size(); i++)
			{
				if(tasks.get(i).ID != s.getTasks().get(i).ID)
					return false;
			}
		}
		
		if(!assignedTasks.isEmpty())
		{
			for(int i=0; i<assignedTasks.size(); i++)
			{
				if(assignedTasks.get(i).ID != s.getAssignedTasks().get(i).ID)
					return false;
			}
		}
		
		return true;
	}
	
	// accessors
	public ArrayList<TaskDescriptor> getTasks() { return tasks; }
	public ArrayList<TaskDescriptor> getAssignedTasks() { return assignedTasks; }
	public void setTasks(ArrayList<TaskDescriptor> tasks) { this.tasks = tasks; }
	public void setAssignedTasks(ArrayList<TaskDescriptor> assignedTasks) { this.assignedTasks = assignedTasks; }
	public City getCurrentCity() { return currentCity; }
	public Topology getTopology() { return topology; }
	public AgentProperties getAgentProperties() { return agentProp; }
	public ArrayList<IGenericAction> getActions() { return actions; }
	public double getCost() { return cost; }
	
}