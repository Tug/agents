package epfl.lia.logist.agent.behavior.response;

public class MoveBehaviorResponse implements IBehaviorResponse {

	/* The destination city where to move */
	private String mDestination = null;
	
	
	/**
	 * Copy constructor of the class
	 */
	public MoveBehaviorResponse( String dst ) {
		mDestination = dst;
	}
	
	
	/**
	 * Returns the destination city
	 */
	public String getDestination() {
		return mDestination;
	}
	
	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.agent.behavior.response.BehaviorResponse#getType()
	 */
	public BehaviorResponseTypeEnum getType() {
		return BehaviorResponseTypeEnum.BRT_MOVETO;
	}
}
