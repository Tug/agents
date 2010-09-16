package epfl.lia.logist.agent.behavior.response;

public class PickupBehaviorResponse implements IBehaviorResponse {

	/* The ID of the task to pickup */
	private int mTaskID = -1;
	
	
	/**
	 * The constructor of the response
	 */
	public PickupBehaviorResponse( int tid ) {
		mTaskID = tid;
	}
	
	
	/**
	 * Returns the type of the response
	 */
	public BehaviorResponseTypeEnum getType() {
		return BehaviorResponseTypeEnum.BRT_PICKUP;
	}
	
	
	/**
	 * Returns the ID of the task
	 */
	public int getTaskID() {
		return mTaskID;
	}

}
