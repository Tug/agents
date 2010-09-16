package epfl.lia.logist.messaging.signal;

/* import table */
import epfl.lia.logist.messaging.*;
import epfl.lia.logist.tools.AID;


/**
 * 
 * @author malves
 *
 * @param <E>
 */
public abstract class Signal<E> extends Message<E> {
	
	/**
	 * Default constructor of the class.
	 * @param sid
	 * @param rid
	 */
	public Signal( AID sid, AID rid, E payload ) {
		super(sid,rid,payload);	
	}
	
	
	/**
	 * Retrieves the type of the message
	 * @return
	 */
	public MessageTypeEnum getMsgType() {
		return MessageTypeEnum.MGT_SIGNAL;
	}

	
	/**
	 * Retrieves the type of the signal 
	 * @return
	 */
	public abstract SignalTypeEnum getType();
	
}