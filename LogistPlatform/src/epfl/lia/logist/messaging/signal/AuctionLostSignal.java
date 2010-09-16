package epfl.lia.logist.messaging.signal;

import java.util.ArrayList;

import epfl.lia.logist.tools.AID;

public class AuctionLostSignal extends Signal<AuctionNotificationObject> {

	/**
	 * 
	 * @param bids
	 */
	public AuctionLostSignal( AID senderID, AID recipientID, 
							  AuctionNotificationObject obj ) {
		super( senderID, recipientID, obj );
	}
	
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_AUCTION_LOST;
	}

}
