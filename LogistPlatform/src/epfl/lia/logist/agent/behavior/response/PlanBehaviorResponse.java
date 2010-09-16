/**
 * 
 */
package epfl.lia.logist.agent.behavior.response;

import epfl.lia.logist.agent.plan.Plan;

/**
 * @author salves
 *
 */
public class PlanBehaviorResponse implements IBehaviorResponse {

	/* the plan that will be communicated to agent */
	private Plan mPlan = null;
	
	
	/**
	 * Constructor of the response 
	 */
	public PlanBehaviorResponse( Plan plan ) {
		mPlan = plan;
	}

	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.agent.behavior.response.IBehaviorResponse#getType()
	 */
	public BehaviorResponseTypeEnum getType() {
		return BehaviorResponseTypeEnum.BRT_PLAN;
	}
	
	
	/**
	 * Return the plan hold by this response
	 */
	public Plan getPlan() {
		return mPlan;
	}

}
