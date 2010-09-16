package org.logist.deliberative;

/* import table */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.MoveBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.PickupBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.PlanBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.config.ConfigurationManager;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.signal.InCityObject;
import epfl.lia.logist.messaging.signal.InCitySignal;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.messaging.signal.InStateSignal;
import epfl.lia.logist.messaging.signal.InitSignal;
import epfl.lia.logist.messaging.signal.ResetSignal;
import epfl.lia.logist.messaging.signal.SetupSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.messaging.signal.TaskDeliveredSignal;
import epfl.lia.logist.messaging.signal.TaskRefusedSignal;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskManager;


/**
 * Implements the same behavior for all signals for the reactive agent.
 */
public class DeliberativeBehavior extends Behavior {
	
	protected AgentProperties mProps = null;
	private TreeSearchStrategy searchAlgo;
	private Plan plan;
	//private int previousNbOfTasks = -1;
	
	public static final String algoName = "Astar"; // "Astar" "BFS"
	
	//--------------------------------------------------------------------------
	// Executes the behavior
	//--------------------------------------------------------------------------
	@Override
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
		BehaviorNotImplementedError 
	{
		/*
		 * This section handles signal according to the type of signal passed as
		 * an argument.
		 */
		switch (s.getType()) {
		case SMT_INIT: // handle the <init> signal
			return myHandleInit(((InitSignal) s).getMessage());

		case SMT_RESET: // handle the <reset> signal
			return myHandleReset(((ResetSignal) s).getMessage());

		case SMT_SETUP: // handle the <setup> signal
			return myHandleSetup((ProbabilityDistribution) ((SetupSignal) s).getMessage());
				
		case SMT_INSTATE: // handle the <in-city> signal
			return myHandleInCity(((InStateSignal) s).getMessage());
		
		case SMT_KILL: // handle the <kill> signal
			return myHandleKill();
			
		case SMT_TASKREFUSED: // handle <task-refused signals>
			return myHandleTaskRefused(((TaskRefusedSignal) s).getMessage());
			
		case SMT_TASKDELIVERED:
			return myHandleTaskDelivered(((TaskDeliveredSignal) s).getMessage());
		
		default: // handles every other signal...
			log(LogSeverityEnum.LSV_INFO, "Agent did not respond to a "
									+ s.getType().toString() + " signal !");
			// throws an exception to signal that this behavior is
			// not implemented
			throw new BehaviorNotImplementedError(s.getType());
		}
	}

	protected IBehaviorResponse myHandleInit(AgentProperties ap)
		throws BehaviorExecutionError
	{
		log(LogSeverityEnum.LSV_INFO, "Agent received an INIT signal !");
		this.mProps = ap;
		plan = new Plan();
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleSetup(ProbabilityDistribution probaDist)
		throws BehaviorExecutionError 
	{
		log(LogSeverityEnum.LSV_INFO,"Agent received a SETUP signal !");
		if(algoName.equals("Astar"))
		{
			searchAlgo = new AStar();
			log(LogSeverityEnum.LSV_INFO,"AStar algorithm chosen !");
		}
		else if(algoName.equals("BFS"))
		{
			searchAlgo = new BreadthFirst();
			log(LogSeverityEnum.LSV_INFO,"BFS algorithm chosen !");
		}
		else
		{
			searchAlgo = new AStar();
			log(LogSeverityEnum.LSV_INFO,"No algorithm chosen, AStar algorithm chosen !");
		}
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleReset(int round) throws BehaviorExecutionError
	{
		log(LogSeverityEnum.LSV_INFO, "Agent received a RESET signal !");
		log(LogSeverityEnum.LSV_INFO, "Agent acknowledged the beginning of round #" + round);
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleKill() throws BehaviorExecutionError
	{
		log(LogSeverityEnum.LSV_INFO, "Agent received a KILL signal !");
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleInCity(InStateObject inStateObject)
		throws BehaviorExecutionError
	{
		int nbOfTaskNow = getNbOfAvailableTasks(inStateObject.Tasks);
		log(LogSeverityEnum.LSV_INFO, "Agent sees "+nbOfTaskNow+" tasks ! computing new plan...");
		try {
			State currentState = new State(inStateObject, mProps);
			ArrayList<TaskDescriptor> emptyList = new ArrayList<TaskDescriptor>();
			State goalState = new State(emptyList, emptyList);
			long start = System.currentTimeMillis();
			State state = (State) searchAlgo.search(currentState, goalState);
			long end = System.currentTimeMillis();
			log(LogSeverityEnum.LSV_INFO, searchAlgo.getClass().getSimpleName()+" algorithm ended in : "+(end-start));
			LinkedList<IGenericAction> actionList = new LinkedList<IGenericAction>();
			while(!state.getId().equals("0"))
			{
				ArrayList<IGenericAction> actions = state.getActions();
				for(int i=actions.size()-1; i>=0; i--)
					actionList.addFirst(actions.get(i));
				state = (State) state.getParent();
			}
			if(actionList.isEmpty()) return new ReadyBehaviorResponse();
			plan = new Plan(actionList.toArray(new IGenericAction[0]));
			log(LogSeverityEnum.LSV_INFO, "new plan : "+plan);
		} catch (FailureException e) {
			log(LogSeverityEnum.LSV_INFO, "FailureException during "+searchAlgo.getClass().getSimpleName()+" search !");
			e.printStackTrace();
		}
		return new PlanBehaviorResponse(plan);
	}

	protected IBehaviorResponse myHandleTaskDelivered(Integer tid)
		throws BehaviorExecutionError
	{
		log(LogSeverityEnum.LSV_INFO, "Agent has delivered task "+tid);
		return new ReadyBehaviorResponse();
	}
	
	protected IBehaviorResponse myHandleTaskRefused(Integer tid)
		throws BehaviorExecutionError
	{
		log(LogSeverityEnum.LSV_INFO, "Agent has refused task "+tid);
		return new ReadyBehaviorResponse();
	}
	
	private int getNbOfAvailableTasks(HashMap<City,ArrayList<Task>> taskDistribution)
	{
		int nbTask = 0;
		for(ArrayList<Task> tasks : taskDistribution.values())
			nbTask += tasks.size();
		return nbTask;
	}

}

