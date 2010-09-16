/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class ResetSignal extends Signal<Integer> {

	/**
	 * @param sid
	 * @param rid
	 */
	public ResetSignal(AID sid, AID rid, Integer data) {
		super(sid,rid,data);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_RESET;
	}

}
