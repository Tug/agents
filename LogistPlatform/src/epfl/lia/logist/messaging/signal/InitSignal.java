/**
 * 
 */
package epfl.lia.logist.messaging.signal;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.tools.AID;

/**
 * @author malves
 *
 */
public class InitSignal extends Signal<AgentProperties> {

	/**
	 * 
	 */
	public InitSignal(AID sid,AID rid, AgentProperties ap ) {
		super(sid,rid,ap);
	}
	
	
	/**
	 * 
	 */
	public SignalTypeEnum getType() {
		return SignalTypeEnum.SMT_INIT;
	}

}
