package epfl.lia.logist.task;


/**
 * 
 *
 */
public class TaskDescriptor {

	/**
	 * The ID of the task
	 */
	public int ID=-1;
	
	/**
	 * The reward for each KM
	 */
	public double RewardPerKm = 15.0;

	/**
	 * The necessary load to carry the task
	 */
	public double Weight = 100.0; 

	/**
	 * The city where task should be picked up
	 */
	public String PickupCity = null;

	/**
	 * The city where task should be delivered
	 */
	public String DeliveryCity = null;
	
	
	/**
	 * CLones the task descriptor
	 */
	public Object clone() {
		TaskDescriptor obj = new TaskDescriptor();
		obj.ID=ID;
		obj.RewardPerKm = RewardPerKm;
		obj.Weight = Weight; 
		obj.PickupCity = PickupCity;
		obj.DeliveryCity = DeliveryCity;
		return obj;
	}
}