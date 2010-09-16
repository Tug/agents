/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class SetupSignal extends Signal {

	/**
	 * @param sid
	 * @param rid
	 */
	public SetupSignal( AID sid, AID rid, Object obj ) {
		super(sid, rid, obj);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_SETUP;
	}

}
