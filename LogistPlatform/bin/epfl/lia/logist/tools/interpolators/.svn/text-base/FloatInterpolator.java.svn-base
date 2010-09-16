package epfl.lia.logist.tools.interpolators;

public class FloatInterpolator implements ILinearInterpolator<Double> {

	/* The value from which we interpolate */
	private Double mFromValue = null;
	
	/* The value to which we interpolate */
	private Double mToValue = null;
	
	
	/**
	 * Return the value from which we interpolate. 
	 */
	public Double getFrom() {
		return mFromValue;
	}

	
	/**
	 * Return the value to which we interpolate.
	 */
	public Double getTo() {
		return mToValue;
	}

	
	/**
	 * Linear interpolation between two double values.
	 */
	public Double interpolate(double t) {
		if ( t<0.0 ) t=0.0;
		if ( t>1.0 ) t=1.0;
		return new Double( mFromValue.doubleValue()*(1.0-t) + 
				           mToValue.doubleValue()*t );
	}

	
	/**
	 * Define the value from which we interpolate.
	 */
	public void setFrom(Double value) {
		mFromValue = value;
	}

	
	/**
	 * Define the value to which we interpolate.
	 */
	public void setTo(Double value) {
		mToValue = value;
	}

}
