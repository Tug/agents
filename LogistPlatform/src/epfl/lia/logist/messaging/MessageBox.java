package epfl.lia.logist.messaging;

/* imports the array list class */
import java.util.ArrayList;


/**
 * 
 * @author malves
 *
 */
public class MessageBox {
	
	/* The queue of messages */
	private ArrayList<Message<?>> mMessageBox;

	
	/**
	 * Default constructor of the class. 
	 * 
	 * Initializes the message box.
	 */
	public MessageBox() {
		mMessageBox = new ArrayList<Message<?>>();
	}
	
	/**
	 * Stores a new message in the mailbox
	 * 
	 * This method add the message to the message box.
	 * 
	 * @param m the message to add
	 */
	public void store( Message<?> m ) {
		mMessageBox.add( m );
	}

	/**
	 * Get the number of messages in message box
	 * 
	 * @return the size of mailbox
	 */
	public int count() {
		return mMessageBox.size();
	}

	/**
	 * Inidicates whether the message box is empty or not
	 * 
	 * @return \b true if the message box is empty, \b false otherwise
	 */
	public boolean isEmpty() {
		return mMessageBox.isEmpty();
	}
	
	
	/**
	 * Deletes every pending message in the queue.
	 */
	public void trashAll() {
		mMessageBox.clear();
	}
	
	
	/**
	 * Get the next message.
	 * 
	 * This method returns the next message. Because messages are stored like
	 * in a queue, the first message arrived, is the first to get out.
	 * 
	 * @return the next stores message
	 */
	public Message<?> retrieve() {
		return ( mMessageBox.size()==0 ) ? null : mMessageBox.remove(0);
	}
}