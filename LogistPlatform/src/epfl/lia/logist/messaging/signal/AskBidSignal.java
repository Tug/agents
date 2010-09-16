/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class AskBidSignal extends Signal<TaskDescriptor> {

	/**
	 * @param sid
	 * @param rid
	 */
	public AskBidSignal(AID sid, AID rid, TaskDescriptor obj) {
		super(sid, rid, obj);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_ASKBID;
	}
}
