package epfl.lia.logist.messaging.signal;


import epfl.lia.logist.tools.AID;

public class AuctionWonSignal extends Signal<AuctionNotificationObject> {

	/**
	 * 
	 * @param bids
	 */
	public AuctionWonSignal( AID senderID, AID recipientID, 
							 AuctionNotificationObject obj ) {
		super( senderID, recipientID, obj );
	}
	

	/**
	 * 
	 */
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_AUCTION_WON;
	}

}
