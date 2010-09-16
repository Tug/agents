package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.task.TaskDescriptor;


/**
 * 
 *
 */
public class AuctionNotificationObject {
	
	/*The array of the bids */
	public Double[] Bids = null;
	
	/* A clone of the original task descriptor */
	public TaskDescriptor Task;
	
	
	/**
	 * Clones this object
	 */
	public Object clone() {
		AuctionNotificationObject obj = new AuctionNotificationObject();
		obj.Bids = (Double[])Bids.clone();
		obj.Task = (TaskDescriptor)Task.clone();
		return obj;
	}
}
