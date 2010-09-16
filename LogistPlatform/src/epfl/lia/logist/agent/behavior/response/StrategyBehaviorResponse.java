package epfl.lia.logist.agent.behavior.response;

import java.util.HashMap;

import epfl.lia.logist.agent.plan.Plan;

public class StrategyBehaviorResponse implements IBehaviorResponse {

	/**/
	private HashMap<String,Plan> m_TableOfPlans = null;
	
	
	/**
	 * 
	 * @param plans
	 */
	public StrategyBehaviorResponse( HashMap<String,Plan> plans ) {
		m_TableOfPlans = plans;
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Plan> getPlans() {
		return m_TableOfPlans;
	}
	
	
	/**
	 * 
	 */
	public BehaviorResponseTypeEnum getType() {
		return BehaviorResponseTypeEnum.BRT_STRATEGY;
	}

}
