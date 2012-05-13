package org.logist.reactive;

/* import table */

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.BehaviorResponseTypeEnum;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.MoveBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.PickupBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.agent.entity.Agent;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.IGenericAction;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.action.ActionTypeEnum;
import epfl.lia.logist.messaging.signal.InCityObject;
import epfl.lia.logist.messaging.signal.InCitySignal;
import epfl.lia.logist.messaging.signal.InitSignal;
import epfl.lia.logist.messaging.signal.ResetSignal;
import epfl.lia.logist.messaging.signal.SetupSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.messaging.signal.TaskDeliveredSignal;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.tools.Pair;

/**
 * Implements the same behavior for all signals for the reactive agent.
 */
public class ReactiveBehavior extends Behavior {
	/* the properties of the agent */
	protected AgentProperties mProps = null;

	private StateList states;

	/**
	 * Executes the behavior of the reactive agent.
	 * 
	 * This method holds the logist for responding to messages coming from
	 * corresponding agent state.
	 */
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
			BehaviorNotImplementedError {
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
			return myHandleSetup((ProbabilityDistribution) ((SetupSignal) s)
					.getMessage());

		case SMT_INCITY: // handle the <in-city> signal
			return myHandleInCity(((InCitySignal) s).getMessage());

		case SMT_KILL: // handle the <kill> signal
			return myHandleKill();

		case SMT_TASKDELIVERED:
			return myHandleTaskDelivered(((TaskDeliveredSignal) s).getMessage());

		default: // handles every other signal...
			log(LogSeverityEnum.LSV_INFO, "Agent did not respond to " + "an "
					+ s.getType().toString() + " signal !");
			// throws an exception to signal that this behavior is
			// not implemented
			throw new BehaviorNotImplementedError(s.getType());
		}

	}

	protected IBehaviorResponse myHandleInit(AgentProperties ap)
			throws BehaviorExecutionError {
		
		log(LogSeverityEnum.LSV_INFO, "Agent received " + "an INIT signal !");

		// properties of the agents
		mProps = ap;
		
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleSetup(ProbabilityDistribution probaDist)
			throws BehaviorExecutionError {
		
		log(LogSeverityEnum.LSV_INFO,"Agent received a SETUP signal !");

		states = new StateList(probaDist, mProps);
		states.reinforcementLearning();
		
		log(LogSeverityEnum.LSV_INFO,"reinforcement learning terminated !");
		// returns a ready response
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleReset(int round)
			throws BehaviorExecutionError {
		// logs the event
		log(LogSeverityEnum.LSV_INFO, "Agent received a RESET signal !");

		// logs another event
		log(LogSeverityEnum.LSV_INFO, "Agent "
				+ "acknowledged the beginning of round #" + round);

		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleKill() throws BehaviorExecutionError {
		// logs the event
		log(LogSeverityEnum.LSV_INFO, "Agent received " + "an KILL signal !");

		// returns a ready response
		return new ReadyBehaviorResponse();
	}

	protected IBehaviorResponse myHandleInCity(InCityObject inCity)
			throws BehaviorExecutionError {
		log(LogSeverityEnum.LSV_INFO, "Agent is in city "+inCity.Name);
		// is there any task to pickup ?
		State nextState;
		TaskDescriptor td = null;
		if (inCity.Tasks.size() > 0) {
			td = inCity.Tasks.get(0);
			nextState = states.findState(td);
		} else {
			nextState = states.findState(inCity.Name, null);
		}
		Action action = states.findBestAction(nextState);

		if (action.isMove()) {
			String strDest = action.getDestination().getNodeLabel();
			log(LogSeverityEnum.LSV_INFO, "Agent moves to "+strDest);
			return new MoveBehaviorResponse(strDest);
		} else {
			String strDest = nextState.getDestination().getNodeLabel();
			log(LogSeverityEnum.LSV_INFO, "Agent takes task "+td.ID+" to deliver in "+strDest);
			return new PickupBehaviorResponse(td.ID);
		}

	}

	protected IBehaviorResponse myHandleTaskDelivered(Integer tid)
			throws BehaviorExecutionError {
		// logs the event
		log(LogSeverityEnum.LSV_INFO, "Agent has deliver task "+tid);
		// returns a ready response
		return new ReadyBehaviorResponse();
	}

}
