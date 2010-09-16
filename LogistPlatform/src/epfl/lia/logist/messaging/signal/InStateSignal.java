/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import java.util.ArrayList;

import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class InStateSignal extends Signal<InStateObject> {
	
	/**
	 * @param sid
	 * @param rid
	 */
	public InStateSignal(AID sid, AID rid, InStateObject obj) {
		super(sid,rid,obj);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_INSTATE;
	}

}
