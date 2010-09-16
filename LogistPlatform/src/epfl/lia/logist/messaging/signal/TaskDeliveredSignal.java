/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class TaskDeliveredSignal extends Signal<Integer> {

	/**
	 * @param sid
	 * @param rid
	 */
	public TaskDeliveredSignal(AID sid, AID rid, Integer tid) {
		super(sid, rid, tid);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_TASKDELIVERED;
	}

}
