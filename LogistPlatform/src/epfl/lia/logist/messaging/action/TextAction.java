/**
 * 
 */
package epfl.lia.logist.messaging.action;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class TextAction extends Action<String> {

	/**
	 * @param sid
	 * @param rid
	 */
	public TextAction(AID sid, AID rid, String text ) {
		super(sid, rid,text);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		// TODO Auto-generated method stub
		return ActionTypeEnum.AMT_TEXT;
	}

}
