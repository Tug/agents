/**
 * 
 */
package epfl.lia.logist.agent.behavior;

/* import table */
import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.messaging.signal.Signal;


/**
 * @author salves
 *
 */
public class DefaultBehavior extends Behavior {

	/**
	 * 
	 */
	public DefaultBehavior()  {
	}

	/* (non-Javadoc)
	 * @see epfl.lia.logist.agent.behavior.Behavior#execute(epfl.lia.logist.messaging.signal.Signal)
	 */
	@Override
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
			BehaviorNotImplementedError {
		return new ReadyBehaviorResponse();
	}

}
