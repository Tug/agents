package epfl.lia.logist.messaging.action;

/* import table */
import epfl.lia.logist.messaging.*;
import epfl.lia.logist.tools.AID;


/**
 * 
 * @author malves
 *
 * @param <E>
 */
public abstract class Action<E> extends Message<E> {
	
	/**
	 * Default constructor of the class.
	 * @param sid
	 * @param rid
	 */
	public Action( AID sid, AID rid, E msg ) {
		super(sid,rid,msg);	
	}

	
	/**
	 * Retrieves the type of the message
	 * @return
	 */
	public MessageTypeEnum getMsgType() {
		return MessageTypeEnum.MGT_ACTION;
	}

	
	/**
	 * Retrieves the type of the signal 
	 * @return
	 */
	public abstract ActionTypeEnum getType();
	
}