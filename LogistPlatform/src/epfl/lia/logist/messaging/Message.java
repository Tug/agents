package epfl.lia.logist.messaging;

import epfl.lia.logist.tools.AID;


public abstract class Message<E> {
	
	/**
	 * Represents the ID of the recipient
	 */
	private final AID mRID;
	
	/**
	 * Represents the ID of the sender
	 */
	private final AID mSID;
	
	/**
	 * Represents the actual message
	 */
	protected  E mPayload;
	
	
	/**
	 * Copy constructor of the class.
	 * @param sid
	 * @param rid
	 */
	public Message( AID sid, AID rid, E payload ) {
		mSID = sid;
		mRID = rid;
		mPayload = payload;
	}
	
	
	/**
	 * Returns the ID of the recipient.
	 * @return
	 */
	public AID getRecipientID() {
		return mRID;
	}
	
	
	/**
	 * Returns the ID of the sender.
	 * @return
	 */
	public AID getSenderID() {
		return mSID;
	}
	
	
	/**
	 * Returns the message payload.
	 * @return
	 */
	public E getMessage() {
		return mPayload;
	}
	
	
	/**
	 * Returns the type of the message
	 * @return
	 */
	public abstract MessageTypeEnum getMsgType();
}
