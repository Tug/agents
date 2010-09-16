/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class TaskRefusedSignal extends Signal<Integer> {

	/**
	 * @param sid
	 * @param rid
	 */
	public TaskRefusedSignal(AID sid, AID rid, Integer tid) {
		super(sid, rid, tid);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_TASKREFUSED;
	}

}
