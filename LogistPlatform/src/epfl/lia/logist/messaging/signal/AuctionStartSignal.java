/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class AuctionStartSignal extends Signal {

	/**
	 * @param sid
	 * @param rid
	 */
	public AuctionStartSignal(AID sid, AID rid) {
		super(sid, rid, null);
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_AUCTION_START;
	}

}
