/**
 * 
 */
package epfl.lia.logist.agent.plan;

import epfl.lia.logist.task.Task;

/**
 * @author salves
 *
 */
public class GDeliverAction implements IGenericAction {

	private Task mTask = null;
	
	
	/**
	 * 
	 */
	public GDeliverAction( Task t ) {
		mTask = t;
	}

	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.agent.plan.IGenericAction#getType()
	 */
	public AgentActionTypeEnum getType() {
		return AgentActionTypeEnum.DELIVER;
	}

	
	/**
	 * Returns the currently delivered task
	 */
	public Task getTask() {
		return mTask;
	}
	
	public String toString() {
		return "deliver(" + mTask.getID() + ")";
	}
}
