package epfl.lia.logist.tools.interpolators;

public class IntegerInterpolator implements ILinearInterpolator<Integer> {

	/* the value from which we interpolate */
	public Integer mFromValue = null;
	
	/* the value to which we interpolate */
	public Integer mToValue = null;
	
	
	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#getFrom(java.lang.Object)
	 */
	public Integer getFrom() {
		return mFromValue;
	}

	
	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#getTo(java.lang.Object)
	 */
	public Integer getTo() {
		return mToValue;
	}

	
	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#interpolate(double)
	 */
	public Integer interpolate(double t) {
		if ( t<0.0 ) t=0.0;
		if ( t>1.0 ) t=1.0;
		return new Integer( (int)((1.0-t)*mFromValue.doubleValue() + 
								 t*mToValue.doubleValue()) ) ;
	}

	
	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#setFrom(java.lang.Object)
	 */
	public void setFrom(Integer value) {
		mFromValue = value;
	}


	/* (non-Javadoc)
	 * @see interpolators.IInterpolator#setTo(java.lang.Object)
	 */
	public void setTo(Integer value) {
		mToValue = value;
	}
}