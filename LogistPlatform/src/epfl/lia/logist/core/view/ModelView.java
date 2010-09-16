/* 
 * Created on 13-Oct-2004
 * Author: Radu Jurca and Michael Schumacher
 * Copyright Ecole Polytechnique Fédérale de Lausanne (EPFL)
 * Artificial Intelligence Laboratory
 * Intelligent Agents Course - Winter Semester 2005-2006
 * Version: $Revision: 23 $
 * Last Modified: $Date: 2006-11-03 09:58:10 +0100 (ven, 03 nov 2006) $
 *
 */

package epfl.lia.logist.core.view;

/* import table */
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.IService;
import epfl.lia.logist.tools.LogistGlobals;


/**
 * PDPSpace is the general space of the Pickup and Delivery Problem.
 * It includes a vehicle space, a task space and a topographical space.
 * 
 */
public class ModelView implements IService {
	
	/**
	 * The topological view of the world
	 */
	protected TopologyView mTopoView;
	
	/**
	 * The agent view of the world
	 */
	protected AgentView mAgentView;
	
	/**
	 * The agent view of the world
	 */
	protected TaskView mTaskView;
	
	/**
	 * The width of the space
	 */
	protected int mSpaceWidth;
	
	/**
	 * The height of the space
	 */
	protected int mSpaceHeight;

	
	/**
	 * The constructor of the PdpSpace
	 * @param m The model to which this space belongs
	 * @param xSize The horizontal size of the world
	 * @param ySize The vertical size of the world
	 */
	public ModelView( int width, int height) {
		
		// initializes dimensions
		mSpaceWidth = width;
		mSpaceHeight = height;
		
		// creates the various views
		mTopoView = new TopologyView( width, height );
		mAgentView = new AgentView( width, height );
		mTaskView = new TaskView( width, height );
	}
	
	/**
	 * Initialize the space of the Pdp Proble
	 * @param topology The topology of the environment
	 * @param companyList The list of delivery companies present in the environment
	 */
	public void init() {
		mTopoView.create();
		mTaskView.create();
		mAgentView.create();
	}


	/**
	 * Destroys all views
	 */
	public void shutdown() {
		mTopoView.destroy();
		mTaskView.destroy();
		mAgentView.destroy();
	}
	
	
	/**
	 * Initialize the views
	 */
	public void setup( Configuration cfg, LogistGlobals lg ) {
	}
	
	
	/**
	 * Initializes the new round
	 */
	public void reset( int round ) {
	}
	
	
	/**
	 * Returns the city from the given coordinates
	 * @param posX The X coordinate
	 * @param posY The Y coordinate
	 * @return The City
	 */
	public Object getCityAt(int posX, int posY) {
		return null; //(City)topoSpace.getObjectAt(posX,posY);
	}

	/**
	 * Returns the vehicle from the given coordinates
	 * @param posX The X coordinate
	 * @param posY The Y coordinate
	 * @return The Vehicle
	 */
	public Object getVehicleAt(int posX, int posY) {
		return null; //(Vehicle)vehicleSpace.getObjectAt(posX,posY);
	}
	
	
	/**
	 * Returns the number of tasks at the given coordinates
	 * @param posX The X coordinate 
	 * @param posY The Y coordinate
	 * @return The number of tasks at the given coordinates
	 */
	public int getNumberOfTasksAt(int posX, int posY) {
		return 0; // taskSpace.getObjectsAt(posX, posY).size();
	}

	/**
	 * Returns the number of delivered tasks at the given coordinates
	 * @param posX The X Coordinate
	 * @param posY The Y Coordinate
	 * @return The number of delivered tasks at the given coordinates
	 */
	public int getNumberOfDeliveredTasksAt(int posX, int posY) {
		/*List<Task> tasks = taskSpace.getObjectsAt(posX,posY);
		int n = 0;
		for (Task t : tasks) {
			if (t.isDelivered())
				n++;
		}			
		return n;
		*/
		return 0;
	}

	/**
	 * Returns the number of non delivered tasks at the given coordinates
	 * @param posX The X Coordinate
	 * @param posY The Y Coordinate
	 * @return The number of non delivered tasks at the given coordinates
	 */
	public int getNumberOfNonDeliveredTasksAt(int posX, int posY) {
		return getNumberOfTasksAt(posX, posY) - getNumberOfDeliveredTasksAt(posX, posY);
	}
	

	/**
	 * Returns the topoSpace
	 * @return the topoSpace
	 */
	public TopologyView getTopologyView() {
		return mTopoView;
	}

	/**
	 * Returns the vehicleSpace
	 * @return the vehicleSpace
	 */
	public AgentView getAgentView() {
		return mAgentView;
	}

	/**
	 * Returns the taskSpace
	 * @return the taskSpace
	 */
	public TaskView getTaskView() {
		return mTaskView;
	}
	
	/**
	 * Returns a string representation of the Object.
	 * @return a string representation of the Object.
	 */
	public String toString() {
		return "Viewing service";
	}
	
}