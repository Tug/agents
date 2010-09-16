/**
 * 
 */
package epfl.lia.logist.tools.interpolators;

import java.awt.Point;

/**
 * @author salves
 *
 */
public class PointInterpolator implements ILinearInterpolator<Point> {

	/* The point from which we start the interpolation */
	private Point mFromValue = null;
	
	/* The point to which we interpolate */
	private Point mToValue = null;
	

	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#getFrom()
	 */
	public Point getFrom() {
		return mFromValue;
	}

	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#getTo()
	 */
	public Point getTo() {
		return mToValue;
	}

	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#interpolate(double)
	 */
	public Point interpolate(double t) {
		if ( t<0.0 ) t=0.0;
		if ( t>1.0 ) t=1.0;
		return new Point ( (int)((1.0-t)*mFromValue.x + t*mToValue.x),
				           (int)((1.0-t)*mFromValue.y + t*mToValue.y) );
	}

	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#setFrom(java.lang.Object)
	 */
	public void setFrom(Point value) {
		mFromValue = value;
	}

	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#setTo(java.lang.Object)
	 */
	public void setTo(Point value) {
		mToValue = value;
	}

}
