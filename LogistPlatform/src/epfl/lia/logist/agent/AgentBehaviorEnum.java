package epfl.lia.logist.agent;


/**
 * 
 * @author salves
 *
 */
public enum AgentBehaviorEnum {

	/**
	 * Represents a reactive agent
	 */
	REACTIVE,
	
	/**
	 * Represents a deliberative agent
	 */
	DELIBERATIVE,
	
	/**
	 * Represents a centralized agent
	 */
	CENTRALIZED,
	
	/**
	 * Represents an auction agent
	 */
	AUCTION,
	
	/**
	 * An agent capable of performing bids
	 */
	AUCTIONEER,
	
	/**
	 * Represents a custom implemented agent
	 */
	CUSTOM
}
