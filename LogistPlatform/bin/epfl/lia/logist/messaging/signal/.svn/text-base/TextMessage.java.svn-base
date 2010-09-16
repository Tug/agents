package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.tools.AID;

public class TextMessage extends Signal<String> {

	/**
	 * 
	 * @param sid
	 * @param rid
	 */
	public TextMessage(AID sid, AID rid, String msg ) {
		super(sid, rid, msg);
	}

	
	/**
	 * 
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_TEXT;
	}

}
