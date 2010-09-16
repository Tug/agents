/**
 * 
 */
package epfl.lia.logist.messaging.action;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class BidAction extends Action<Double> {

	/**
	 * @param sid
	 * @param rid
	 */
	public BidAction(AID sid, AID rid, double d ) {
		super(sid, rid, d);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		// TODO Auto-generated method stub
		return ActionTypeEnum.AMT_BID;
	}

}
