/**
 * 
 */
package epfl.lia.logist.messaging.action;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class PickupAction extends Action<Integer> {

	/**
	 * @param sid
	 * @param rid
	 */
	public PickupAction(AID sid, AID rid, Integer tid ) {
		super(sid, rid, tid);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		return ActionTypeEnum.AMT_PICKUP;
	}

}
