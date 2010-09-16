package epfl.lia.logist.messaging.signal;

/**
 * 
 * @author malves
 *
 */
public enum SignalTypeEnum {
	
	/**
	 * Commands an agent to initialize himself
	 * 
	 * (synchronous) à response: AMT_READY
	 */
	SMT_INIT,
	
	/**
	 * Commands an agent to reset its internal state
	 * 
	 * (synchronous) à response: AMT_READY
	 */
	SMT_RESET,

	/**
	 * Commands an agent to perform some cleaning before being killed
	 * 
	 * (synchronous) à response: AMT_READY
	 */
	SMT_KILL,

	/**
	 * Allows an agent to perform the initial needed setup
	 * (synchronous) à response: AMT_READY
	 */
	SMT_SETUP,

	/**
	 * Tells an agent that it is currently in city X
	 */
	SMT_INCITY,

	/**
	 * Tells an agent that world is currently in state Y
	 */
	SMT_INSTATE,

	/**
	 * Tells an agent that the task it picked up before has been delivered
	 */
	SMT_TASKDELIVERED,

	/**
	 * Informs an agent that the task it asked for is unavailable.
	 */
	SMT_TASKREFUSED,

	/**
	 * Asks agents for bids for current item
	 * 
	 * (synchronous) à response: AMT_BID after at most T seconds
	 */
	SMT_ASKBID,

	/**
	 * A generic message, mosly for text messages
	 *  
	 * (synchronous) à response: AMT_READY
	 */
	SMT_TEXT,
	
	/**
	 * 
	 */
	SMT_AUCTION_WON,
	
	/**
	 * 
	 */
	SMT_AUCTION_LOST,
	
	/**
	 * 
	 */
	SMT_AUCTION_START,
	
	/**
	 * 
	 */
	SMT_AUCTION_END
}
