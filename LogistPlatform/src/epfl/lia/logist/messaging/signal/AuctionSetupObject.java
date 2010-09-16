package epfl.lia.logist.messaging.signal;

import java.util.ArrayList;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.ProbabilityDistribution;

/**
 * This object contains everything necessary to launch the simulation
 */
public class AuctionSetupObject {

	/* The list of participating vehicles */
	public ArrayList<AgentProperties> Vehicles;
	
	/* The probabilities object */
	public ProbabilityDistribution Probabilities;
	
	/* The current topology object */
	public Topology Topology;
}
