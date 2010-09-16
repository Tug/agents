package epfl.lia.logist.agent;

public enum AgentStateEnum {
	AS_NONE, AS_INIT, AS_SETUP, AS_IDLE, AS_RESET, AS_KILL, AS_MOVETO, AS_PICKUP,
	AS_PHASE1, AS_PHASE2, AS_PHASE3, AS_WAIT, AS_THREADED_WAIT, AS_FINISHED,
	
	AS_AUCTION_START, AS_AUCTION_ITEM, AS_AUCTION_END
}