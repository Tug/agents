package epfl.lia.logist.core.view;

/* import table */
import uchicago.src.sim.space.VectorSpace;
import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.core.listeners.IAgentListener;


/**
 *
 * * This class wraps a Multi2DGrid to represent the space of Vehicles
 * 
 */

public class AgentView extends View<VectorSpace> implements IAgentListener {
	
	/**
	 * Default constructor of the class
	 * @param xSize
	 * @param ySize
	 */
	public AgentView( int width, int height ) {
		this.msSpace = new VectorSpace();
	}
	
	/** 
	 * Invoked once to create the view
	 */
	public void create() {
	}
	
	
	/**
	 * Invoked once to destroy the view
	 */
	public void destroy() {
	}
	
	
	/**
	 * This is automatically invoked by the agent manager
	 * when a new agent is created. This allows depending
	 * systems to update themselves easily.
	 * @param ag a reference 
	 */
	public void onAgentAddition( AgentProfile ap ) {
		if ( ap.getDisplayable() != null )
			msSpace.addMember( ap.getDisplayable() );
	}
	
	
	/**
	 * This is automatically invoked by the agent manager
	 * when an agent is removed. This allows depending systems
	 * to update themselves easily.
	 * @param ag a reference to the agent profile.
	 */
	public void onAgentDeletion( AgentProfile ap ) {
		msSpace.removeMember( ap.getDisplayable() );
	}
	
}
