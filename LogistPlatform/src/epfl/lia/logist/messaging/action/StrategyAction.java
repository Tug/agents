/**
 * 
 */
package epfl.lia.logist.messaging.action;

import java.util.HashMap;

import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.tools.AID;

/**
 * @author salves
 *
 */
public class StrategyAction extends Action<HashMap<String,Plan>> {

	/**
	 * @param sid
	 * @param rid
	 * @param msg
	 */
	public StrategyAction(AID sid, AID rid, HashMap<String,Plan> msg) {
		super(sid, rid, msg);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		return ActionTypeEnum.AMT_STRATEGY;
	}

}
