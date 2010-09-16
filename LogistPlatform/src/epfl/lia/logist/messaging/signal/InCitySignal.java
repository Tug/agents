/**
 * 
 */
package epfl.lia.logist.messaging.signal;

/**/
import epfl.lia.logist.tools.AID;


/**
 * This signal is used to specify the agent that it arrived in a
 * particular city.
 */
public class InCitySignal extends Signal<InCityObject> {

	/**
	 * @param sid
	 * @param rid
	 */
	public InCitySignal( AID sid, AID rid, InCityObject obj ) {
		super(sid,rid,obj);
	}

	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.messaging.signal.Signal#getType()
	 */
	@Override
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_INCITY;
	}

	public String toString() {
		return "in-city-signal<" + this.getSenderID() + "," + this.getRecipientID() + "," + mPayload.Name + ">"; 
	}
}
