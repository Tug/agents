package epfl.lia.logist.core.listeners;

/* a profile agent */
import epfl.lia.logist.agent.AgentProfile;


/**
 * 
 * @author salves
 *
 */
public interface IAgentListener {

	/**
	 * This is automatically invoked by the agent manager
	 * when a new agent is created. This allows depending
	 * systems to update themselves easily.
	 * @param ag a reference 
	 */
	public void onAgentAddition( AgentProfile ap );
	
	/**
	 * This is automatically invoked by the agent manager
	 * when an agent is removed. This allows depending systems
	 * to update themselves easily.
	 * @param ag a reference to the agent profile.
	 */
	public void onAgentDeletion( AgentProfile ap );

}
