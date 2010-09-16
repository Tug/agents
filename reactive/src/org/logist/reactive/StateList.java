package org.logist.reactive;

import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.messaging.action.ActionTypeEnum;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.TaskDescriptor;

public class StateList {
	private ArrayList<State> states;

	private int[] V;

	private HashMap<String,Double> T;

	private Action[] actions;

	private ProbabilityDistribution probaDist;

	private AgentProperties mProps;

	private Topology topology;

	public StateList(ProbabilityDistribution probaDist, AgentProperties mProps) {
		this.probaDist = probaDist;
		this.mProps = mProps;
		this.topology = Topology.getInstance();
	}

	public void addState(State s) {
		states.add(s);
	}

	public void removeState(State s) {
		states.remove(s);
	}

	public State findState(TaskDescriptor task) {
		return findState(task.PickupCity, task.DeliveryCity);
	}

	public State findState(String cityStart, String cityDestination) {
		City start = topology.getCity(cityStart);
		City destination = topology.getCity(cityDestination);
		return findState(start, destination);
	}

	public State findState(City start, City destination) {
		int i = states.indexOf(new State(start, destination));
		if (i != -1)
			return states.get(i);
		return null;
	}

	public Action findBestAction(TaskDescriptor task) {
		return findBestAction(findState(task));
	}

	public Action findBestAction(State s) {
		return actions[V[s.getId()]];
	}

	public void reinforcementLearning() {
		initStates();
		initActions();
		initTransitions();
		// Reinforcement Learning Algorithm
		double gamma = 0.8;
		// init V
		V = new int[states.size()];
		double[] V1 = new double[states.size()];
		double[] V2 = new double[states.size()];
		for (int i = 0; i < V1.length; i++) {
			V1[i] = 0;
			V2[i] = 0;
		}
		// init Q
		double[][] Q = new double[states.size()][actions.length];
		boolean goodEnough = false;
		while (!goodEnough) {
			for (State s1 : states) {
				int sid1 = s1.getId();
				double max = Double.NEGATIVE_INFINITY;
				int amax = 0;
				for (int aid = 0; aid < actions.length; aid++) {
					double sum = 0.0;
					for (State s2 : states) {
						int sid2 = s2.getId();
						sum += getT(s1,s2,aid) * V1[sid2];
					}
					double R = getExpectedReward(actions[aid], s1);
					Q[sid1][aid] = R + gamma * sum;
					if (Q[sid1][aid] > max) {
						max = Q[sid1][aid];
						amax = aid;
					}
				}
				V2[sid1] = max;
				V[sid1] = amax;
			}
			double totalDiff = 0;
			for (int i = 0; i < V1.length; i++) {
				totalDiff += Math.abs(V1[i] - V2[i]);
				V1[i] = V2[i];
			}
			System.out.println("totalDiff="+totalDiff);
			goodEnough = (totalDiff < 1);
		}
	}

	/**
	 * Init S
	 * 
	 */
	private void initStates() {

		HashMap<String, City> cityMap = topology.getCities();
		states = new ArrayList<State>();
		State.staticid = 0;
		for (City c1 : cityMap.values()) {
			for (City c2 : cityMap.values()) {
				// state that we are in city c1
				// and we have a task to city c2
				if (!c1.equals(c2))
					states.add(new State(c1, c2));
			}
			// state that we are in the city c1 and we don't have any task
			states.add(new State(c1, null));
		}
	}

	/**
	 * Init A
	 * 
	 */
	private void initActions() {
		topology.getCities();
		HashMap<String, City> cityMap = topology.getCities();
		actions = new Action[topology.getCities().size() + 1];
		int i = 0;
		for (City c : cityMap.values()) {
			actions[i] = new Action(ActionTypeEnum.AMT_MOVE, c);
			i++;
		}
		actions[i] = new Action(ActionTypeEnum.AMT_PICKUP);
	}

	/**
	 * Init T
	 * 
	 */
	private void initTransitions() {
		// T as a HashMap otherwise it contains to much 0
		T = new HashMap<String,Double>();
		for (State s1 : states) {
			for (State s2 : states) {
				for (int aid = 0; aid < actions.length; aid++) {
					Action a = actions[aid];
					// p(s2|s1,a)
					double p = 0.0;
					// if we can go from s1 to s2 with action a
					if (s1.isLinkedWith(s2, a)) {
						p = s2.getTaskProbability(probaDist);
						T.put("s"+s1.getId()+"s"+s2.getId()+"a"+aid,new Double(p));
					}
					// T(s1,a,s2) = p(s2|s1,a)
					//T[s1.getId()][s2.getId()][aid] = p;
				}
			}
		}
	}
	
	public double getT(State s1, State s2, int aid) {
		Double ts1s2a = T.get("s"+s1.getId()+"s"+s2.getId()+"a"+aid);
		if(ts1s2a == null) return 0.0;
		else return ts1s2a.doubleValue();
	}

	private double getExpectedReward(Action a, State s) {
		if (a.isPickup()) {
			if(s.hasTask())
				return (s.getRewardPerKm(probaDist) - mProps.CostPerKm) * s.getDistance();
			else 
				return 0.0;
		} else {
			double R = -probaDist.getRewardPerKm(s.getCurrentCity(), a.getDestination());
			R *= topology.shortestDistanceBetween(s.getCurrentCity(), a.getDestination());
			return R;
		}
	}

}
