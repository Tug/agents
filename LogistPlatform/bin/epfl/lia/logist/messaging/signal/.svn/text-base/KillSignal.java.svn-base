package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

public class KillSignal extends Signal<Integer> {

	public KillSignal(AID sid, AID rid, Integer reason) {
		super(sid, rid, reason);
	}

	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_KILL;
	}

}
