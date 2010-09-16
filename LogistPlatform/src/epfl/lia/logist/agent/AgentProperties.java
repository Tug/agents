package epfl.lia.logist.agent;

/**
 * Returns the properties of a particular agent. This object is sent to
 * the behavior for own exclusive and personal use...
 */
public class AgentProperties {
	
	/* The capacity of the vehicle */
	public double Capacity = 0.0;

	/* The cost per km of the vehicle */
	public double CostPerKm = 0.0;

	/* The load of the vehicle */
	public double Load = 0.0;

	/* The name of the agent */
	public String Name = null;

	/* The speed of the vehicle */
	public double Speed = 0.0;
	
	/* The home where agent belongs */
	public String Home = null;
}
