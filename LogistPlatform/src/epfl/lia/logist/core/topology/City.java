package epfl.lia.logist.core.topology;

/* import table */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.network.Node;
import epfl.lia.logist.task.TaskManager;


/**
 * This class models a city.
 */
public class City extends DefaultNode implements Drawable {
	
	/**
	 * The X coordinate of the node.
	 */
	private int x;

	/**
	 * The Y coordinate of the node.
	 */
	private int y;
	
	
	/**
	 * Constructs a City given the coordinates and a label
	 * 
	 * @param space
	 *            The PdpSpace in which the city will be displayed
	 * @param newX
	 *            The X coordinate
	 * @param newY
	 *            The Y coordinate
	 * @param label
	 *            The label of the city
	 */
	public City( int newX, int newY, String label ) {
		super(label);
		x = newX;
		y = newY;
	}
	
	
	// Drawing
	// -------

	// REM See as an example for drawing:
	// method drawDisplay(SimGraphics g) in
	// uchicago.src.sim.gui.Object2DDisplay.java

	/*
	 * (non-Javadoc)
	 * 
	 * @see uchicago.src.sim.gui.Drawable#draw(uchicago.src.sim.gui.SimGraphics)
	 */
	/**
	 * Draws the city.
	 */
	public void draw( SimGraphics g ) {
		Graphics2D g2 = g.getGraphics();
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		drawRoutes( g );
		drawCity( g );
	}
	

	public ArrayList<Node> getDestinations() {
		return this.getOutNodes();
	}
	
	/**
	 * Draw the routes to each connected node
	 */
	private void drawRoutes( SimGraphics g ) {
		
		// for every starting edge, draws the route
		Iterator it = outEdges.iterator();
		
		// finds the scale of the drawing
		int displayScaleWidth = g.getCellWidthScale();
		int displayScaleHeight = g.getCellHeightScale();
		
		Graphics2D g2 = g.getGraphics();
		
		// while there are more routes, continue drawing
		while (it.hasNext()) {
			
			// converts to an edge 
			Route e = (Route)it.next();
			
			Stroke oldStroke = g2.getStroke();
			g2.setStroke( new BasicStroke(3.0f) );
			
			// draw the lines taking into account 
			// the width and heigth of the cells
			g.drawLink( Color.lightGray, 
						displayScaleWidth * getX(), 
						displayScaleWidth * ((City)e.getTo()).getX(), 
						displayScaleHeight * getY(), 
						displayScaleHeight * ((City)e.getTo()).getY());
			
			g2.setStroke( oldStroke );
		}		
	}
	
	
	/**
	 * Draws the city (an oval) and its name
	 * @param g
	 */
	private void drawCity( SimGraphics g ){

		// retrieves the scale of the cells
		int yScale = g.getCellHeightScale();
		int xScale = g.getCellWidthScale();
		
		// get current position in term of cells
		float curX = getX() * xScale;
		float curY = getY() * yScale;

		// prepares the font
		Font currentFont = g.getGraphics().getFont();
		
		
		
		//int fontSize = PdpUtil.MIN_FONT_SIZE + 2* Math.min(xScale,
		//		(PdpUtil.MAX_FONT_SIZE - PdpUtil.MIN_FONT_SIZE)/2);
		//System.out.println(fontSize);
		Font font = new Font( currentFont.getName(), currentFont.getStyle(), 12 ); 
		
		Graphics2D g2 = g.getGraphics();
		String name = getNodeLabel();

		
		FontRenderContext rContext = g2.getFontRenderContext();

		TextLayout layout = new TextLayout(name, font, rContext);
		Rectangle2D bounds = layout.getBounds();
		
		
		//draw the String centered above the city node
		// TODO: change painting
		g2.setPaint( null );
		layout.draw(g2, curX - (float)(bounds.getWidth()/2) ,
                         curY - 2*12);
		
		//draw a circle where the city is supposed to be
		//g2.setPaint(PdpUtil.CITY_OVAL_COLOR);
		int ovalSizeX = 15;
		//PdpUtil.MIN_OVAL_SIZE + 2*Math.min(xScale, 
		//		(PdpUtil.MAX_OVAL_SIZE - PdpUtil.MIN_OVAL_SIZE)/2);
		int ovalSizeY = 15;
		//PdpUtil.MIN_OVAL_SIZE + 2*Math.min(yScale, 
			//	(PdpUtil.MAX_OVAL_SIZE - PdpUtil.MIN_OVAL_SIZE)/2);
		g2.setPaint( Color.red );
		g2.fillOval((int)curX -ovalSizeX/2, (int)curY-ovalSizeY/2, ovalSizeX, ovalSizeY);
		g2.setPaint( Color.white );
		Stroke oldStroke = g2.getStroke();
		g2.setStroke( new BasicStroke(3.0f) );
		g2.drawOval( (int)curX -ovalSizeX/2, (int)curY-ovalSizeY/2, ovalSizeX, ovalSizeY );
		g2.setStroke( oldStroke );
		
		int xp=x+10, yp=y+10;
		g2.setPaint( Color.decode("#a02000") );
		g2.fillRect( xp, yp, 8, 8 );
		g2.setPaint( Color.orange );
		g2.drawRect( xp, yp, 8, 8 );
		g2.drawLine( xp, yp, xp+8, yp+8 );
		g2.drawLine( xp+8, yp, xp, yp+8 );
		
		g2.setPaint( Color.decode("#a02088") );
		g2.fillRect( x+10, y+20, 8, 8 );
		g2.setPaint( Color.orange );
		g2.drawRect( x+10, y+20, 8, 8 );
		g2.drawLine( xp, yp+10, xp+8, yp+18 );
		g2.drawLine( xp+8, yp+10, xp, yp+18 );
		
		// ohow many tasks
		int iTasks = TaskManager.getInstance().getPickupTaskCount( label );
		g2.drawString( "x" + iTasks, x+20, y+17 );
		iTasks = TaskManager.getInstance().getDeliveredTaskCount( label );
		g2.drawString( "x" + iTasks, x+20, y+27 );
		
	}
	

	/**
	 * Get the X coordinate of the City.
	 * 
	 * @return the X coordinate of the City.
	 */
	public int getX() {
		return x;
	}
	
	
	/**
	 * Set the value of the X coordinate of the City.
	 * 
	 * @param newX
	 *            The X coordinate of the City.
	 */
	public void setX(int newX) {
		x = newX;
	}
	
	
	/**
	 * Get the Y coordinate of the City.
	 * 
	 * @return the Y coordinate of the City.
	 */
	public int getY() {
		return y;
	}
	
	
	/**
	 * Set the value of the Y coordinate of the City.
	 * 
	 * @param newY
	 *            The Y coordinate of the City.
	 */
	public void setY(int newY) {
		y = newY;
	}

	/**
	 * Set both coordinates of the City
	 * 
	 * @param newX
	 *            The X coordinate of the City.
	 * @param newY
	 *            The Y coordinate of the City.
	 */
	public void setXY(int newX, int newY) {
		x = newX;
		y = newY;
	}

    /**
	 * Compare two cities by the value of the X coordinate.
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 */
    public int compareTo(Object o)
    {
        City c = (City) o;
        return this.getX() - c.getX();
    }
    

    /**
	 * Test if two cities are the same
	 */
    public boolean equals(Object city) {
    		if (city == null)
    			return false;
    		return this.getX() == ((City)city).getX() && this.getY() == ((City)city).getY();
    }

    
    /**
     * Test if two cities have the same name
     */
    public boolean match(City city ) {
    	return this.getNodeLabel().equals( city.getNodeLabel() );
    }
    
    
    /**
     * Hash code
     */
    public int hashCode(){
    	return (new Integer(getX())).hashCode() + (new Integer(getY())).hashCode(); 
    }

	
	/**
	 * Returns a String representation of the Object
	 */
	public String toString() {
		return "city<" + getNodeLabel() + "," + x + "," + y + ">";
	}
	
	/**
	 * Return the route to a neighbouring City
	 */
	public Route getRouteTo(City destination){
		if (this.hasEdgeTo(destination)){
			HashSet h = this.getEdgesTo(destination);
			return (Route) h.iterator().next();
		}
		else return null;
	}
	
}
