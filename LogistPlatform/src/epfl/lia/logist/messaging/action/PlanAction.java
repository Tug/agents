/**
 * 
 */
package epfl.lia.logist.messaging.action;

import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class PlanAction extends Action<Plan> {

	/**
	 * @param sid
	 * @param rid
	 */
	public PlanAction(AID sid, AID rid, Plan plan ) {
		super( sid, rid, plan );
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		// TODO Auto-generated method stub
		return ActionTypeEnum.AMT_PLAN;
	}

}
