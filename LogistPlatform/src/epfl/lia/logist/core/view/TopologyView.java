package epfl.lia.logist.core.view;

/* import table */
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.VectorSpace;
import epfl.lia.logist.core.listeners.ITopologyListener;
import epfl.lia.logist.core.topology.City;

/**
 * This class wraps a Object2DGrid to represent a topological space
 */
public class TopologyView extends View<VectorSpace> implements ITopologyListener {
	
	/**
	 * Default constructor of the class
	 * @param width
	 * @param height
	 */
	public TopologyView( int width, int height ) {
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
	 * Invoked when city is added
	 * @param city
	 */
	public void onCityAddition( City city ) {
		this.msSpace.addMember( city );
		//space().putObjectAt( city.getX(), city.getY(), city );
	}
	
	
	/**
	 * Invoked when city is removed
	 * @param city
	 */
	public void onCityDeletion( City city ) {
	}
	
	/**
	 * Invoked when city situation changes
	 * @param city
	 */
	public void onCityChange( City city ) {
	}
}
