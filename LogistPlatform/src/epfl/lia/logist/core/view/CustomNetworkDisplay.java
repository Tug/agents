/**
 * 
 */
package epfl.lia.logist.core.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Iterator;

import uchicago.src.sim.gui.DisplayInfo;
import uchicago.src.sim.gui.Displayable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.gui.ViewEvent;
import uchicago.src.sim.network.Edge;
import uchicago.src.sim.space.VectorSpace;
import epfl.lia.logist.agent.AgentDisplayable;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.LogistGlobals;

/**
 * @author salves
 *
 */
public class CustomNetworkDisplay implements Displayable {

	/* The space holding all agents */
	private VectorSpace mAgentSpace = null;
	
	/* The space holding topology information */
	private VectorSpace mTopoSpace = null;
	
	/* Indicates whether or not agents should be displayed */
	private boolean mbAgents = true;
	
	/* Indicates whether or not cities should be displayed */
	private boolean mbCities = true;
	
	/* Indicates whether or not names should be displayed */
	private boolean mbNames = true;
	
	/* Indicates whether or not routes should be displayed */
	private boolean mbRoutes = true;
	
	/* Indicates whether or not tasks should be displayed */
	private boolean mbTasks = true;
	
	/* Keep a private reference to the globals */
	private LogistGlobals mGlobals = null;
	
	/* An instance of the task manager */
	private TaskManager mTaskMgr = null;
	
	private Font mNameFont = null;
	
	/**
	 * Constructor of the class. Initializes the class objects from
	 * a topology space and a vehicle space. Theses spaces hold information
	 * about tasks, cities and vehicles in the simulation.
	 */
	public CustomNetworkDisplay( VectorSpace topo, VectorSpace vehicles, 
								 LogistGlobals lg ) {
		this.mTopoSpace = topo;
		this.mAgentSpace = vehicles;
		this.mGlobals = lg;
		mTaskMgr = TaskManager.getInstance();
	}

	
	/* (non-Javadoc)
	 * @see uchicago.src.sim.gui.Displayable#drawDisplay(uchicago.src.sim.gui.SimGraphics)
	 */
	public void drawDisplay(SimGraphics arg0) {
		
		// gets the graphics
		Graphics2D g = arg0.getGraphics();
		
		// enable scene antialiasing
		if ( mGlobals.Antialias ) {
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
								RenderingHints.VALUE_ANTIALIAS_ON );
		} else {
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
								RenderingHints.VALUE_ANTIALIAS_OFF );
		}
		
		// enables antialiased text
		if ( mGlobals.TextAntialias ) {
			g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, 
							    RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		} else {
			g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, 
				    			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
		}
		
		// draws the layers of the graph (drawing position is important)
		if ( this.mbRoutes ) drawRoutes(g);
		if ( this.mbCities ) drawCities(g);
		if ( this.mbNames ) drawNames(g);
		if ( this.mbAgents ) drawAgents(arg0);
		
	}
	
	
	/**
	 * Draws the routes on the graph
	 * 
	 * This method draws the routes according to the colors specified in the
	 * globals.
	 * 
	 * @param g The graphics object allowing to draw on the display
	 */
	private void drawRoutes( Graphics2D g ) {
		
		// defines the color of the route
		g.setPaint( mGlobals.RouteColor );
		
		// stores the old stroke 
		Stroke oldStroke = g.getStroke();
		
		// creates a new stroke
		g.setStroke( new BasicStroke(mGlobals.RouteSize) );
		
		// draw the list of edges
		ArrayList cities = mTopoSpace.getMembers();
		Iterator itor = cities.iterator();
		while( itor.hasNext() ) {
			City c = (City)itor.next();
			ArrayList outEdges = c.getOutEdges();
			Iterator edges = outEdges.iterator();
			while( edges.hasNext() ) {
				Edge e = (Edge)edges.next();
				City c1=(City)e.getFrom();
				City c2=(City)e.getTo();
				g.drawLine( c1.getX(), c1.getY(), c2.getX(), c2.getY() );
			}
		}
		
		// restores the old stroke
		g.setStroke( oldStroke );
	}
	
	
	/**
	 * Draws currently displayed agents
	 * 
	 * This method draws the currently displayed agent. Displays agents are
	 * entities of AgentDisplayable type. They are responsible for their own
	 * redraw.
	 * 
	 * @param g The graphics object
	 */
	private void drawAgents( SimGraphics g ) {
		
		// gets the list of members in the agent space
		ArrayList agents = this.mAgentSpace.getMembers();
		
		// for every agent out there, draw it 
		Iterator itor = agents.iterator();
		while( itor.hasNext() ) {				
			((AgentDisplayable)itor.next()).draw(g);
		}
	}

	
	/**
	 * Draws the city points
	 * 
	 * This method draws the circles corresponding to the spot where the city
	 * lies.
	 * 
	 * @param g The graphics object
	 */
	private void drawCities( Graphics2D g ) {
		
		// get the list of cities
		ArrayList cityList = this.mTopoSpace.getMembers();
		
		// iterates through each of them and draws it !
		Iterator itor = cityList.iterator();
		while( itor.hasNext() ) {
			drawCity( (City)itor.next(), g );
		}
	}
	

	/**
	 * Draws one single city
	 * 
	 * @param c the city to draw
	 * @param g the graphics object
	 */
	private void drawCity( City c, Graphics2D g ) {
		
		// gets the position of the city
		int x = c.getX();
		int y = c.getY();
		
		//draw a circle where the city is supposed to be
		int radius = mGlobals.CityRadius;
		int diameter = 2*mGlobals.CityRadius;
		g.setPaint( mGlobals.CityColor );
		g.fillOval( x-radius, y-radius, diameter, diameter );
		
		// draws a circle in the perimeter of the point
		g.setPaint( mGlobals.CityPerimColor );
		Stroke oldStroke = g.getStroke();
		g.setStroke( new BasicStroke(3.0f) );
		g.drawOval( x-radius, y-radius, diameter, diameter );
		g.setStroke( oldStroke );
		
		// should we display tasks or not ?
		if ( mbTasks ) {
			
			// draws the box indicating tasks delivered
			int xp=x+10, yp=y+10;
			g.setPaint( mGlobals.TaskToDeliverColor );
			g.fillRect( xp, yp, 8, 8 );
			g.setPaint( Color.orange );
			g.drawRect( xp, yp, 8, 8 );
			
			// draws the box indicating tasks to pickup
			g.setPaint( mGlobals.TaskToPickupColor );
			g.fillRect( x+10, y+20, 8, 8 );
			g.setPaint( Color.orange );
			g.drawRect( x+10, y+20, 8, 8 );
			
			// displays some text
			g.setColor( mGlobals.Forecolor );
			int iTasks = mTaskMgr.getPickupTaskCount( c.getNodeLabel() );
			g.drawString( "x" + iTasks, x+20, y+17 );
			iTasks = mTaskMgr.getDeliveredTaskCount( c.getNodeLabel() );
			g.drawString( "x" + iTasks, x+20, y+27 );
		}
	}

	
	/**
	 * Draws the name of the cities
	 * 
	 * This method allows handles the drawing of the names of the cities.
	 * 
	 * @param g the graphics object
	 */
	private void drawNames( Graphics2D g ) {
		
		// get the old font context
		Font currentFont = g.getFont();
		
		if ( mNameFont==null ) {
			mNameFont = Font.getFont( "Courier New-PLAIN-12" );
		}
		
		// change the font
		g.setFont( mNameFont );
		
		// set the color of city names
		g.setColor( mGlobals.CityNameColor );
		
		// for every city in the topological space
		for ( Object o : mTopoSpace.getMembers() ) {
			
			// retrieves a reference to this city
			City c = (City)o;
		
			// draws the city string
			g.drawString( c.getNodeLabel(), c.getX()+5, c.getY()-10 );	
		}
		
		// restore old font context
		g.setFont( currentFont );
	}
	
	
	/* (non-Javadoc)
	 * @see uchicago.src.sim.gui.Displayable#getDisplayableInfo()
	 */
	public ArrayList getDisplayableInfo() {
		ArrayList<DisplayInfo> info = new ArrayList<DisplayInfo>();
		info.add( new DisplayInfo( "Cities", 0, this ) );
		info.add( new DisplayInfo( "Routes", 1, this ) );
		info.add( new DisplayInfo( "Tasks", 2, this ) );
		info.add( new DisplayInfo( "Names", 3, this ) );
		info.add( new DisplayInfo( "Agents", 4, this ) );
		return info;
	}

	
	/* (non-Javadoc)
	 * @see uchicago.src.sim.gui.Probeable#getObjectsAt(int, int)
	 */
	public ArrayList getObjectsAt( int x, int y ) {
		
		// retrieves the list of objects at x and y
		ArrayList obj = new ArrayList();
		for( Object o : mTopoSpace.getMembers() ) {
				obj.add( o );
		}
		return obj;
	}

	
	/* (non-Javadoc)
	 * @see uchicago.src.sim.gui.Displayable#getSize()
	 */
	public Dimension getSize() {
		return new Dimension( mGlobals.WorldXSize, mGlobals.WorldYSize );
	}


	/* (non-Javadoc)
	 * @see uchicago.src.sim.gui.Displayable#viewEventPerformed(uchicago.src.sim.gui.ViewEvent)
	 */
	public void viewEventPerformed(ViewEvent arg0) {
		switch( arg0.getId() ) {
			case 0: this.mbCities = arg0.showView(); break;
			case 1: this.mbRoutes = arg0.showView(); break;
			case 2: this.mbTasks = arg0.showView(); break;
			case 3: this.mbNames = arg0.showView(); break;
			case 4: this.mbAgents = arg0.showView(); break;
		}
	}

}
