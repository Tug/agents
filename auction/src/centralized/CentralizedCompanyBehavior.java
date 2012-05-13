package centralized;

/* import table */
import java.util.ArrayList;
import java.util.HashMap;

import centralized.tabu.TabuSearch;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.StrategyBehaviorResponse;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.messaging.signal.InStateSignal;
import epfl.lia.logist.messaging.signal.InitSignal;
import epfl.lia.logist.messaging.signal.ResetSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskManager;

/**
 * Implements the same behavior for all signals for the reactive agent.
 */
public class CentralizedCompanyBehavior extends Behavior {
	
	private ArrayList<AgentProperties> aprops = null;
	private boolean initial= true;
	public static final boolean optimal = false;
	public static final int maxSec = 3*60;
	
	//--------------------------------------------------------------------------
	// Executes the behavior
	//--------------------------------------------------------------------------
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
			BehaviorNotImplementedError {
		
		/*
		 * This section handles signal according to the type of signal passed
		 * as an argument.
		 */
		switch( s.getType() ) {
		
			// handle the <init> signal
			case SMT_INIT:
				
				// initializes the agent
				initAgent( ((InitSignal)s).getMessage() );
				
				// returns a ready action
				return new ReadyBehaviorResponse();
			
			// handle the <reset> signal
			case SMT_RESET:
				
				// reset the agent
				resetAgent( ((ResetSignal)s).getMessage() );
				initial=true;
				// return a ready action
				return new ReadyBehaviorResponse();
				
			// handle the <setup> signal
			case SMT_SETUP:
			
				// sets the agent up and running
				setupAgent( (ArrayList<AgentProperties>)s.getMessage() );
				
				// return a ready action
				return new ReadyBehaviorResponse();
				
			// handle the <in-city> signal
			case SMT_INSTATE:
				return handleInStateSignal( ((InStateSignal)s).getMessage() );
				
			// handle the <kill> signal
			case SMT_KILL:
				killAgent();
				return new ReadyBehaviorResponse();

			// handles every other signal...
			default:
				throw new BehaviorNotImplementedError( s.getType() );
		}
	}

	protected IBehaviorResponse initAgent(AgentProperties ap)
	throws BehaviorExecutionError {

		log(LogSeverityEnum.LSV_INFO, "Agent received an INIT signal !");
		
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse setupAgent(ArrayList<AgentProperties> aprops)
	throws BehaviorExecutionError {
		
		log(LogSeverityEnum.LSV_INFO, "Agent received a SETUP signal !");
		// properties of the agents	
		this.aprops = aprops;
		
		return new ReadyBehaviorResponse();		
	}

	protected IBehaviorResponse resetAgent(int round)
	throws BehaviorExecutionError {
		
		log(LogSeverityEnum.LSV_INFO, "Agent received a RESET signal !");
		log(LogSeverityEnum.LSV_INFO, "Agent acknowledged the beginning of round #" + round);

		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse killAgent()
	throws BehaviorExecutionError {
		
		log(LogSeverityEnum.LSV_INFO, "Agent received an KILL signal !");
		
		return new ReadyBehaviorResponse();		
	}
	
	protected IBehaviorResponse handleInStateSignal(InStateObject obj)
	throws BehaviorExecutionError {
		
		if(initial)
		{
			HashMap<String,Plan> plan = null;
			ArrayList<Task> tasks = TaskManager.getInstance().getTaskList(false);
			
			if(optimal)
			{
				long start = System.currentTimeMillis();
				OptimalCooperativePlanner planner = new OptimalCooperativePlanner(tasks, this.aprops, obj.Graph);
				plan = planner.plan();
				long end = System.currentTimeMillis();
				log(LogSeverityEnum.LSV_INFO, "Optimal algorithm ended in : "+(end-start)+" cost : "+getCost(plan));
			}
			else
			{
				NonOptimalCooperativePlanner planner = new NonOptimalCooperativePlanner(tasks, this.aprops, obj.Graph);
				//StaticPlanner planner = new StaticPlanner(tasks, this.aprops, obj.Graph);
				//planner.setSearchAlgorithm(new SLS(0.5, 10000000, 30*1000));
				planner.setSearchAlgorithm(new TabuSearch(15, 100000, 30*1000));
				long start = System.currentTimeMillis();
				plan = planner.getPlans();
				long end = System.currentTimeMillis();
				log(LogSeverityEnum.LSV_INFO, "Non optimal algorithm ended in : "+(end-start)+" cost : "+getCost(plan));
			}
			initial=false;
			return new StrategyBehaviorResponse(plan);
		}
		return new ReadyBehaviorResponse();
	}
	
	public double getCost(HashMap<String,Plan> plans)
	{
		double cost=0;
		for(String agentName : plans.keySet())
		{
			Plan p = plans.get(agentName);
			for(AgentProperties ap : aprops)
			{
				if(ap.Name.equals(agentName))
				{
					cost += p.getCost(ap.CostPerKm);
				}
			}
		}
		return cost;
	}

}
