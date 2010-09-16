package epfl.lia.logist.agent;

/* import table */
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * 
 * 
 * @author salves
 */
public class AgentDisplayable implements Drawable {

	
	/**
	 * The color of the agent
	 */
	private Color mColor;
	
	/**
	 * The position in Y
	 */
	private double mX;
	
	/**
	 * The position in X
	 */
	private double mY;
	
	/**
	 * This angle is computed from direction
	 */
	private Point[] mArrow = null;
	
	/**
	 * The constructor of the class 
	 * @param state
	 */
	public AgentDisplayable() {
		mArrow = new Point[3];
		mArrow[0] = new Point(0,0);
		mArrow[1] = new Point(0,0);
		mArrow[2] = new Point(0,0);
	}

	public void setDirection( double mx, double my ) {
		double a = 120.0 * Math.PI / 180.0;
		double angleA = Math.atan2(my, mx);
		double angleB = angleA+a;
		double angleC = angleB+a;
		mArrow[0].x = (int)(15.0 * mx);
		mArrow[0].y = (int)(15.0 * my);
		mArrow[1].x = (int)(15.0*Math.cos(angleB));
		mArrow[1].y = (int)(15.0*Math.sin(angleB));
		mArrow[2].x = (int)(15.0*Math.cos(angleC));
		mArrow[2].y = (int)(15.0*Math.sin(angleC));
	}
	
	/* (non-Javadoc)
	 * @see uchicago.src.sim.gui.Drawable#draw(uchicago.src.sim.gui.SimGraphics)
	 */
	public void draw(SimGraphics g) {

		// compute the correct coordinates
		double X = mX; //* g.getCellWidthScale();
		double Y = mY;// * g.getCellHeightScale();

		if ( X<0 && X>500) X=100;
		if ( Y<0 && Y>500 ) Y=100;
		
		// retrieves the ameliored graphics unit
		Graphics2D g2 = g.getGraphics();

		// changes the painting color for the agent
		//g2.setPaint(load != 0 ? color.darker() : color);
		g2.setPaint( mColor );
		
		
		//PdpUtil.MIN_OVAL_SIZE + 2*Math.min(xScale, 
		//		(PdpUtil.MAX_OVAL_SIZE - PdpUtil.MIN_OVAL_SIZE)/2);
		
		//PdpUtil.MIN_OVAL_SIZE + 2*Math.min(yScale, 
		//		(PdpUtil.MAX_OVAL_SIZE - PdpUtil.MIN_OVAL_SIZE)/2);
		
		// chooses the rounding of the rectangle
		
		int rX = 5;
		int rY = 5;
		g2.fillRoundRect( (int)X-rX, (int)Y-rY, rX*2, rY*2, 3, 3 );
		
		
		//double angle = Math.atan2( mY-mOldY, mX-mOldY );
		
		// determines the orientaion of the triangle
		int[] px = new int[3];
		int[] py = new int[3];
		
		px[0] = (int)X+mArrow[0].x;
		py[0] = (int)Y+mArrow[0].y;
		px[1] = (int)(X+mArrow[1].x);
		py[1] = (int)(Y+mArrow[1].y);
		px[2] = (int)(X+mArrow[2].x);
		py[2] = (int)(Y+mArrow[2].y);
		
		
		
		g2.fillPolygon( px, py, 3 );
		
		//if the vehicle is loaded, draw a black wireframe arround
		//if (load != 0) {
		//	g2.setPaint(Color.BLACK);
		//	g2.drawRoundRect((int)curX -rectSizeX/2, (int)curY-rectSizeY/2, rectSizeX, rectSizeY,3,3);
		//}
		//*/
	}
	
	
	/**
	 * 
	 * @param X
	 * @param Y
	 */
	public void move( double X, double Y ) {
		mX = X;
		mY = Y;
	}
	
	/**
	 * 
	 * @param pt
	 */
	public void move( Point pt ) {
		mX = pt.x;
		mY = pt.y;
	}
	
	
	/**
	 * Return the color of the agent.
	 */
	public Color getColor() {
		return mColor;
	}
	
	
	/**
	 * Return the horizontal coordinate
	 */
	public int getX() {
		return (int)mX;
	}

	
	/**
	 * Return the vertical coordinate 
	 */
	public int getY() {
		return (int)mY;
	}
	
	
	/**
	 * Define the color of the agent.
	 */
	public void setColor( Color c ) {
		mColor = c;
	}
	
	
	/**
	 * Set the horizontal coordinate
	 */
	public void setX( int X ) {
		mX=X;
	}
	
	
	/**
	 * Set the vertical coordinate
	 */
	public void setY( int Y ) {
		mY=Y;
	}
}