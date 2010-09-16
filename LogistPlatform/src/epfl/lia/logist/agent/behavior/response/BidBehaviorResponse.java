package epfl.lia.logist.agent.behavior.response;

public class BidBehaviorResponse implements IBehaviorResponse {

	private double mPrivateBid = 0;
	
	public BidBehaviorResponse( double bid ) {
		mPrivateBid = bid;
	}
	
	public double getBid() {
		return mPrivateBid;
	}
	
	public BehaviorResponseTypeEnum getType() {
		return BehaviorResponseTypeEnum.BRT_BID;
	}

}
