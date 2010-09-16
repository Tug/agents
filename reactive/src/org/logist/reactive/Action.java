package org.logist.reactive;

import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.messaging.action.ActionTypeEnum;

public class Action {
	private ActionTypeEnum type;

	private City destination;

	public Action(ActionTypeEnum type, City destination) {
		this.type = type;
		this.destination = destination;
	}

	public Action(ActionTypeEnum type) {
		this(type, null);
	}

	public ActionTypeEnum getType() {
		return type;
	}

	public City getDestination() {
		return destination;
	}

	public boolean isPickup() {
		return type == ActionTypeEnum.AMT_PICKUP;
	}

	public boolean isMove() {
		return type == ActionTypeEnum.AMT_MOVE;
	}

	public boolean isPossible(State state1, State state2) {
		return destination.equals(state2.getCurrentCity());
	}
}