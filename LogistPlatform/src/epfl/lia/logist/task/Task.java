package epfl.lia.logist.task;


/**
 * The task structure representing a unit of work for the agents. Agents
 * evaluate their capacity and decide to take or or that task.
 */
public class Task  {

	/**
	 * The ID of the task
	 */
	protected int mID;

	/**
	 * The reward for each KM
	 */
	protected double mRewardPerKm;

	/**
	 * The necessary load to carry the task
	 */
	protected double mWeight;

	/**
	 * The city where task should be picked up
	 */
	protected String mPickupCity;

	/**
	 * The city where task should be delivered
	 */
	protected String mDeliveryCity;
	
	/**
	 * The name of the agent that picked this task
	 */
	protected String mAgent = "None";
	
	/**
	 * Indicates if the task has been delivered
	 */
	protected boolean mDelivered = false;
	
	/**
	 * The current id
	 */
	private static int mNextID = 0;
	
	
	/**
	 * Creates a task with default values
	 */
	public Task() {
	}
	
	/**
	 * Creates the task from a descriptor
	 * @param td
	 */
	public Task( TaskDescriptor td ) {
		mID 	      = mNextID++;
		mRewardPerKm  = td.RewardPerKm;
		mWeight       = td.Weight;
		mPickupCity   = td.PickupCity;
		mDeliveryCity = td.DeliveryCity;
	}
		
	
	/**
	 * Generates a descriptor for this task
	 */
	public TaskDescriptor getDescriptor() {
		
		// initialize the descriptor
		TaskDescriptor td = new TaskDescriptor();
		td.ID			= mID;
		td.RewardPerKm  = mRewardPerKm;
		td.Weight       = mWeight;
		td.PickupCity   = mPickupCity;
		td.DeliveryCity = mDeliveryCity;
		
		// return the descriptor
		return td;
	}
	 
	
	/**
	 * Completely clones a single task
	 */
	public Task clone() {
		Task t = new Task();
		t.mID = mID;
		t.mRewardPerKm = mRewardPerKm;
		t.mWeight = mWeight;
		t.mPickupCity = mPickupCity;
		t.mDeliveryCity = mDeliveryCity;
		t.mAgent = mAgent;
		t.mDelivered = mDelivered;
		return t;
	}
	
	
	/**
	 * The ID of the task
	 */
	public int getID() {
		return mID;
	}

	
	/**
	 * The reward for each KM
	 */
	public double getRewardPerKm() {
		return mRewardPerKm;
	}

	
	/**
	 * Sets the reward per km (for auctions)
	 */
	public void setRewardPerKm( double d ) {
		mRewardPerKm = d;
	}
	
	
	/**
	 * The necessary load to carry the task
	 */
	public double getWeight() {
		return mWeight;
	}

	
	/**
	 * The city where task should be picked up
	 */
	public String getPickupCity() {
		return mPickupCity;
	}

	
	/**
	 * The city where task should be delivered
	 */
	public String getDeliveryCity() {
		return mDeliveryCity;
	}
	
	
	/**
	 * Indicates if the task was delivered or not
	 */
	public boolean getDelivered() {
		return mDelivered;
	}
	
	
	/**
	 * Compares two cities
	 */
	public boolean equals( Object obj ) {
		if ( obj==null || !(obj instanceof Task) )
			return false;
		Task t = (Task)obj;
		return ( mID==t.getID() );
	}
	
	
	/**
	 * Defines if the task is delivered or not.
	 */
	public void setDelivered( boolean status, String vehicle ) {
		mDelivered = status;
		mAgent = vehicle;
	}
	
	
	/**
	 * Returns the name of the agent that delivered this task
	 */
	public String getDeliverAgent() {
		return mAgent;
	}
	
	
	/**
	 * Returns a string representation 
	 */
	public String toString() {
		return "<task id=\"" + this.mID + "\" pickup=\"" + this.mPickupCity + 
			   "\" delivery=\"" + this.mDeliveryCity + "\" owner=\"" + mAgent + 
			   "\" weight=\"" + this.mWeight + "\" reward=\"" + 
			   this.mRewardPerKm + "\" />"; 
	}
}