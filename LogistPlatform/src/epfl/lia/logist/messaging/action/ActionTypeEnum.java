package epfl.lia.logist.messaging.action;


/**
 * 
 * 
 * @author malves
 */
public enum ActionTypeEnum {

	/**
	 * Tells associated agent state that agent is ready
	 */
	AMT_READY,

	/**
	 * Tells associated agent state that agent wants to move
	 */
	AMT_MOVE,
	
	/**
	 * Tells associated agent state that agent wants to pick a particular task
	 */
	AMT_PICKUP,

	/**
	 * Tells associated agent state that agent wants to submit a plan
	 */
	AMT_PLAN,
	
	/**
	 * 
	 */
	AMT_STRATEGY,
	
	/**
	 * The log entry contains an fatal error description. Platform exits in this case.
	 */
	AMT_BID,
	
	/**
	 * There is nothing here...
	 */
	AMT_TEXT,
	
	
	/**
	 * There is nothing here...
	 */
	AMT_NONE
	
}
