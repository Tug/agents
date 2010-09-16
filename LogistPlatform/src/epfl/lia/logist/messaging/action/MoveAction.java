/**
 * 
 */
package epfl.lia.logist.messaging.action;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class MoveAction extends Action<String> {

	/**
	 * @param sid
	 * @param rid
	 */
	public MoveAction(AID sid, AID rid, String city ) {
		super(sid, rid, city);
		//System.out.println( "MoveAction::payload = " + city );
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.action.Action#getType()
	 */
	@Override
	public ActionTypeEnum getType() {
		return ActionTypeEnum.AMT_MOVE;
	}

	public String toString() {
		return "move-action<" + getSenderID() + "," + getRecipientID() + "," + mPayload + ">";
	}
}
