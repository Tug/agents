package epfl.lia.logist.agent;

/* some useful importations */
import java.awt.Color;
import java.util.ArrayList;

import epfl.lia.logist.agent.behavior.BehaviorDescriptor;


/**
 * 
 * @author malves
 *
 */
public class AgentDescriptor {
	
	/**
	 * The name of the agent
	 */
	public String Name = "Default";
	
	/**
	 * The speed of the vehicle (if type==’VEHICLE’)
	 */
	public double Speed = 120.0;
	
	/**
	 * The load capacity of the vehicle (if type==’VEHICLE’)
	 */
	public double Capacity = 80.0;
	
	/**
	 * The cost per kilometer (if type==’VEHICLE’)
	 */
	public double CostPerKM = 15.0;
	
	/**
	 * Indicates if this agent should be created or not !
	 */
	public boolean Active = true;
	
	/**
	 * The cost per kilometer (if type==’VEHICLE’)
	 */
	public String Home = "random";
	
	/**
	 * The color of the agent (if vehicle) 
	 */
	public java.awt.Color AgentColor = Color.RED;
	
	/**
	 * The type of agent
	 */
	public String Type = "default";
	
	/**
	 * The behavior of the agent (reactive,deliberative,centralized,auction,custom)	
	 */
	public String Behavior = "reactive";
	
	/**
	 * The class for custom agents
	 */
	public String ClassName = null;
	
	/**
	 * A list of childrens (if !=’VEHICLE’)
	 */
	public ArrayList<BehaviorDescriptor> Behaviors = null;
	
	/**
	 * A list of childrens (if !=’VEHICLE’)
	 */
	public ArrayList<AgentDescriptor> Children = null;
	
}