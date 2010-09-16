package centralized;

import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.plan.GDeliverAction;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.Task;

public class StaticPlanner
{
	private World theWorld;
	private String[][] planStr = planStr2;
	
	private static final String[][] planStr1 = 
	{ 
		{ "Morpheus", "pickup(12), move(Marseille), pickup(5), move(Bordeaux), pickup(18), move(Lyon), deliver(18), move(Genève), deliver(12), deliver(5)" },
		{ "Neo", "move(Le Havre), pickup(6), pickup(1), move(Paris), move(Brest), deliver(1), pickup(2), pickup(7), deliver(6), move(Paris), pickup(17), deliver(2), pickup(14), pickup(0), deliver(7), move(Lyon), move(Genève), deliver(17), deliver(0), deliver(14), pickup(10), pickup(9), pickup(19), move(Strasbourg), deliver(9), deliver(19), pickup(4), deliver(10), pickup(11), pickup(15), pickup(13), move(Genève), deliver(4), deliver(13), deliver(11), deliver(15), move(Monaco), pickup(3), move(Marseille), pickup(8), move(Lyon), move(Genève), deliver(8), pickup(16), move(Strasbourg), deliver(16), deliver(3)"}
	};
	
	private static final String[][] planStr2 = 
	{ 
		{ "Morpheus", "move(Genève), pickup(16), pickup(19), pickup(9), move(Strasbourg), deliver(9), deliver(19), deliver(16), pickup(15), move(Genève), move(Lyon), pickup(12), move(Paris), move(Le Havre), deliver(12), move(Paris), pickup(14), pickup(17), pickup(0), move(Bordeaux), deliver(15), move(Marseille), move(Monaco), pickup(3), deliver(0), deliver(14), deliver(17), move(Marseille), pickup(8), pickup(5), move(Bordeaux), deliver(5), deliver(8), pickup(18), move(Brest), pickup(7), deliver(3), pickup(2), move(Paris), move(Strasbourg), deliver(18), deliver(7), deliver(2)" },
		{ "Neo", "move(Le Havre), pickup(1), pickup(6), move(Paris), move(Lyon), move(Genève), deliver(6), pickup(10), deliver(1), move(Strasbourg), pickup(11), pickup(13), deliver(10), pickup(4), move(Paris), move(Bordeaux), deliver(13), deliver(4), deliver(11)" }
	};
	
	public StaticPlanner( ArrayList<Task> taskList, 
		  	ArrayList<AgentProperties> vehicleList, 
		  	Topology topology)
	{
		this.theWorld = new World(taskList, vehicleList, topology);
	}
	
	
	public HashMap<String, Plan> getPlans()
	{
		HashMap<String, Plan> plans = new HashMap<String, Plan>();
		for(int vi=0; vi<planStr.length; vi++)
		{
			String agentName = planStr[vi][0];
			City currentCity = theWorld.topology.getCity(theWorld.getVehicleProperties(vi).Home);
			String[] actionsStr = planStr[vi][1].split(", ");
			Plan p = new Plan();
			for(int i=0; i<actionsStr.length; i++)
			{
				String[] els = actionsStr[i].split("\\(");
				String actionName = els[0];
				String actionInfo = els[1].substring(0,els[1].length()-1);
				IGenericAction action = null;
				if(actionName.equals("move"))
				{
					City dest = theWorld.topology.getCity(actionInfo);
					double dist = theWorld.topology.shortestDistanceBetween(currentCity, dest);
					currentCity = dest;
					action = new GMoveAction(dest, dist);
				}
				else if(actionName.equals("pickup"))
				{
					int id = Integer.parseInt(actionInfo);
					action = new GPickupAction(theWorld.taskList.get(id));
				}
				else if(actionName.equals("deliver"))
				{
					int id = Integer.parseInt(actionInfo);
					action = new GDeliverAction(theWorld.taskList.get(id));
				}
				p.addAction(action);
			}
			plans.put(agentName, p);
		}
		return plans;
	}
	
}
