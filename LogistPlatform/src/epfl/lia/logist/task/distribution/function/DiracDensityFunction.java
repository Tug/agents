/**
 * 
 */
package epfl.lia.logist.task.distribution.function;

import java.util.Properties;

import epfl.lia.logist.tools.Convert;

/**
 * @author salves
 *
 */
public class DiracDensityFunction implements IFunction {

	/* Defines the constant value */
	private double mConstantValue = 0.0;
	
	
	/**
	 * Default constructor. Initializes the constant value to 0.
	 */
	public DiracDensityFunction() {
		mConstantValue = 0.0;
	}

	/**
	 * Copy constructor of the class. Creates a constant value
	 * from the properties.
	 * 
	 * @param props
	 */
	public DiracDensityFunction( Properties props ) {
		mConstantValue = Convert.toDouble( props.getProperty("constant"), 0.0 );
	}
	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.task.distribution.function.IFunction#nextValue()
	 */
	public double nextValue() {
		return mConstantValue;
	}

}
