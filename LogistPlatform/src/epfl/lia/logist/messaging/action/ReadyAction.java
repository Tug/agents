/**
 * 
 */
package epfl.lia.logist.messaging.action;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class ReadyAction extends Action {

	/**
	 * @param sid
	 * @param rid
	 */
	public ReadyAction(AID sid, AID rid) {
		super(sid, rid,null);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		// TODO Auto-generated method stub
		return ActionTypeEnum.AMT_READY;
	}

}
