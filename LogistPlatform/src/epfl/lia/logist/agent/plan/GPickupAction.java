/**
 * 
 */
package epfl.lia.logist.agent.plan;

import epfl.lia.logist.task.Task;

/**
 * @author salves
 *
 */
public class GPickupAction implements IGenericAction {

	/* Keeps a reference to the task to pick */
	private Task mTask = null;
	
	
	/**
	 * Default constructor of the class.
	 */
	public GPickupAction( Task t ) {
		mTask = t;
	}

	
	/**
	 * Return the reference to the task
	 */
	public Task getTask() {
		return mTask;
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see epfl.lia.logist.agent.plan.IGenericAction#getType()
	 */
	public AgentActionTypeEnum getType() {
		return AgentActionTypeEnum.PICKUP;
	}

	public String toString() {
		return "pickup(" + mTask.getID() + ")";
	}
}
