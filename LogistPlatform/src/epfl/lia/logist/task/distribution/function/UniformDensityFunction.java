/**
 * 
 */
package epfl.lia.logist.task.distribution.function;

/* importation table */
import java.util.Properties;

import org.apache.commons.math.random.RandomDataImpl;

import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.Convert;


/**
 * This class is an implementation of an uniform random generator. It
 * is a wrapper for the Apache Commons RandomDataImpl generator method 
 * nextUniform().
 */
public class UniformDensityFunction implements IFunction {

	/* The minimum value which can be generated */
	private double mMin = 0.0;
	
	/* The maximum value which can be generated */
	private double mMax = 1.0;
	
	/* The seed value for the generator */
	private long mSeed = 0;
	
	/* The random generator */
	private RandomDataImpl mRandom = null;
	
	
	/**
	 * Default class constructor
	 * 
	 * Resets class attributes to default values
	 */
	public UniformDensityFunction() {
		mMin = 0.0;
		mMax = 1.0;
		mSeed = System.currentTimeMillis();
		mRandom = new RandomDataImpl();
		mRandom.reSeed( mSeed );

		// prints out the seed used
		LogManager.getInstance().log( LogManager.DEFAULT, 
				LogSeverityEnum.LSV_DEBUG,
				"Uniform density function seed value is " + mSeed + " !" );
	}
	

	/**
	 * Copy constructor of the function
	 * 
	 * This method initializes all the parameters from a property object.
	 * 
	 * @param props a reference to an initialized property object
	 */
	public UniformDensityFunction( Properties props ) {
		
		// initializes values
		mMin = Convert.toDouble( props.getProperty("min"), 0.0 );
		mMax = Convert.toDouble( props.getProperty("max"), 1.0 );
		mSeed = Convert.toLong( props.getProperty("seed"), System.currentTimeMillis() );
		mRandom = new RandomDataImpl();
		mRandom.reSeed( mSeed );
		
		// prints out the seed used
		LogManager.getInstance().log( LogManager.DEFAULT, 
				LogSeverityEnum.LSV_DEBUG,
				"Uniform density function seed value is " + mSeed + " !" );
	}
	
	
	/* (non-Javadoc)
	 * @see epfl.lia.logist.task.IFunction#nextValue()
	 */
	public double nextValue() {
		return mRandom.nextUniform( mMin, mMax );
	}

}
