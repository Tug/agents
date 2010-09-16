package epfl.lia.logist.agent.plan;

import epfl.lia.logist.core.topology.City;

public class GMoveAction implements IGenericAction {

	/* The city to move to */
	private City mTarget = null;
	private double mDistance = 0.0;
	
	/**
	 * Default constructor of the class
	 */
	public GMoveAction( City to, double distance ) {
		mTarget = to;
		mDistance = distance;
	}
	
	
	/**
	 * Return the target city..
	 */
	public City getTarget() {
		return mTarget;
	}
	
	
	/**
	 * Returns the distance between the two cities
	 */
	public double getDistance() {
		return mDistance;
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see epfl.lia.logist.agent.plan.IGenericAction#getType()
	 */
	public AgentActionTypeEnum getType() {
		return AgentActionTypeEnum.MOVE;
	}

	public String toString() {
		return "move(" + mTarget.getNodeLabel() + ")";
	}
}
