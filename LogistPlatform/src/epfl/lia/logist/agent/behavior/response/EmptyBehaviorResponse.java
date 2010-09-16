package epfl.lia.logist.agent.behavior.response;

public class EmptyBehaviorResponse implements IBehaviorResponse {
	public BehaviorResponseTypeEnum getType() {
		return BehaviorResponseTypeEnum.BRT_EMPTY;
	}

}
