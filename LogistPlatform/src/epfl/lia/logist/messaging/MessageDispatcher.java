package epfl.lia.logist.messaging;

/* importation table */
import java.util.HashMap;

import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.IService;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.AID;
import epfl.lia.logist.tools.LogistGlobals;


/**
 * 
 * @author malves
 *
 */
public class MessageDispatcher implements IService {
	
	/* The table of message boxes */
	private HashMap<AID,MessageBox> mMessageBoxes = null;
	
	/* The singleton instance of the class */
	private static MessageDispatcher msSingleton = null;
	
	
	/**
	 * Default constructor of the class
	 * 
	 * Does absolutely nothing at all. 
	 */
	public MessageDispatcher() {
		if ( msSingleton == null )
			msSingleton = this;
	}
	
	
	/**
	 * Posts a new message
	 * 
	 * This method allows posting a new message to a known recipient. A message
	 * is given by a sender and a recipient ID which uniquely identify the 
	 * originator and receiver of the message
	 * 
	 * @param m the message to send
	 */
	public void post( Message<?> m ) {
		
		// null messages are discarded
		if ( m==null ) return;
		
		// if the destination address of the message is a 
		// broadcast addres, then post msg to every message box
		if ( m.getRecipientID() == AID.BROADCAST_ADDRESS ) {
			postToEveryone(m);
		
		
		// try to post message to message box
		} else if ( mMessageBoxes.containsKey(m.getRecipientID()) ) {
			
			// get the message box associated with the ID
			MessageBox mBox = mMessageBoxes.get( m.getRecipientID() );
			if ( mBox != null ) {
				mBox.store( m );
			}
		}
	}

	
	/**
	 * Posts the message to every message box
	 * 
	 * This method post the incoming message to every registered box.
	 * 
	 * @param m the message to post to everyone
	 */
	protected void postToEveryone( Message<?> m ) {
		for( MessageBox mBox: mMessageBoxes.values() )
			mBox.store( m );
	}
	
	
	/**
	 * Retrieve next message from a private message box
	 * 
	 * This method checks to see if there is a new message and return it. If no
	 * message is available, null is returned.
	 * 
	 * @param m an object implementing the message handler interface
	 * 
	 * @return \b null if no new message is available, an instance otherwise
	 */
	public Message<?> retrieve( MessageHandler m ) {
		
		// retrieve the message box
		MessageBox mBox = mMessageBoxes.get( m.getObjectID() );
		
		// the message box should not be empty
		if ( mBox != null && !mBox.isEmpty() ) {
			Message<?> mm = mBox.retrieve();
			return mm; 
		}
		
		// return null otherwise
		return null;
	}

	/**
	 * Check if there is any new messages
	 * 
	 * This method allows inspecting the message box for new messages. Another
	 * means of knowing whether there are new messages is to call method
	 * retrieve() and check if returned message is null or not !
	 * 
	 * @param m an object implementing the message handler interface
	 * 
	 * @return \b true if there is a new message, \b false otherwise
	 */
	public boolean check( MessageHandler m ) {
		
		// the message box must exist
		if ( mMessageBoxes == null )
			return false;
		
		// get the message box for handler ID
		MessageBox mBox = mMessageBoxes.get( m.getObjectID() );
		if ( mBox != null )
			return !mBox.isEmpty();
		
		// return false otherwise
		return false;
	}
	

	/**
	 * Get a unique instance of this class
	 */
	public static MessageDispatcher getInstance() {
		return msSingleton;
	}
	
	
	/**
	 * Register an handler within this service
	 * 
	 * This method allows any implementor of the MessageHandler interface to
	 * receive and send messages. For every new handler, it creates a new 
	 * message box. One handler can only have one message box !!!
	 * 
	 * @param m an object implementing the MessageHandler interface
	 */
	public void register( MessageHandler m ) {
		if ( !mMessageBoxes.containsKey(m.getObjectID()) )
			mMessageBoxes.put( m.getObjectID(), 
							   new MessageBox() );
	}

	/**
	 * Unregister a previously registered handler
	 * 
	 * This method allows unregistering a previously registered MessageHandler.
	 * The message handler is passed as a parameter.
	 * 
	 * @param m an object implementing the MessageHandler interface
	 * 
	 * @return \b true if handler was removed, \b false otherwise
	 */
	public boolean unregister( MessageHandler m ) {
		return mMessageBoxes.remove(m.getObjectID()) != null;

	}

	/**
	 * Unregister a previously registered handler
	 * 
	 * This method allows unregistering a previously registered MessageHandler.
	 * We pass the id of the MessageHandler as a parameter.
	 * 
	 * @param mid the ID of the handler instance
	 * 
	 * @return \b true if handler was removed, \b false otherwise
	 */
	public boolean unregister( AID mid ) {
		return mMessageBoxes.remove(mid) != null;
	}

	
	/**
	 * Initializes the message dispatcher service
	 * 
	 * This method initializes the list of message boxes and then calls the
	 * garbage collector to kill pending objects.
	 */
	public void init() {
		
		// creates a new table of messageboxes
		if ( mMessageBoxes == null)
			mMessageBoxes = new HashMap<AID,MessageBox> ();
		else
			mMessageBoxes.clear();
		
		// explicitely calls the Garbage Collector to
		// ensure that unneeded objects are properly
		// destroyed..
		Runtime.getRuntime().gc();
	}


	/**
	 * Shutdown all subsystems linked to this service
	 *
	 * This method trashes all messages stored in the message boxes and then
	 * destroys the message boxes and clear the list.
	 */
	public void shutdown() {

		// creates a new table of messageboxes
		if ( mMessageBoxes == null )
			return;
		else {
			
			// for every message box, trash all pending messages
			for( MessageBox mb : mMessageBoxes.values() ) {
				mb.trashAll();
			}
			
			// clear the list of message boxes
			mMessageBoxes.clear();
		}
		
		// effectively deletes the object
		mMessageBoxes = null;
		
		// explicitely calls the Garbage Collector to
		// ensure that unneeded objects are properly
		// destroyed..
		Runtime.getRuntime().gc();
	}

	
	/**
	 * Set the message dispatcher service up and running
	 * 
	 * @param cfg the global configuration object
	 * @param lg the global system variables
	 */
	public void setup( Configuration cfg, LogistGlobals lg ) {
	}
	
	
	/**
	 * Reset the message dispatcher service
	 * 
	 * @param the current round number
	 */
	public void reset( int round ) {
		
		// log the event
		LogManager.getInstance().log( LogManager.DEFAULT, 
			LogSeverityEnum.LSV_WARNING, "Beginning of round " + round + ": " + 
			"trashing all pending messages..." );
		
		// for every message box, trash all pending messages
		for( MessageBox mb : mMessageBoxes.values() ) {
			mb.trashAll();
		}
	}
	
	
	/**
	 * Get the text display for this service
	 */
	public String toString() {
		return "Inter-object messaging service";
	}
}