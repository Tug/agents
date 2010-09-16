package epfl.lia.logist.task.distribution.function;

import java.util.Properties;

import org.apache.commons.math.random.RandomDataImpl;

import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.Convert;
import org.apache.commons.math.random.RandomDataImpl;


/**
 * The binary density function
 * 
 *
 */
public class BinaryDensityFunction implements IFunction {

	/* The array of values (2 in fact)*/
	private double[] mValues;
	
	/* The random generator */
	private RandomDataImpl mRandom = null;
	
	/* the seed value */
	private long mSeed = 0;
	
	/* defined which is the bias */
	private double mBias = 0.0;
	
	
	/**
	 * Default class constructor
	 * 
	 * Initializes density function params to defaults
	 */
	public BinaryDensityFunction() {
		mValues = new double[] { 0.0, 1.0 };
		mSeed = System.currentTimeMillis();
		mRandom = new RandomDataImpl();
		mRandom.reSeed( mSeed );
		
		// prints out the seed used
		LogManager.getInstance().log( LogManager.DEFAULT, 
				LogSeverityEnum.LSV_DEBUG,
				"Binary density function seed value is " + mSeed + " !" );
	}
	
	
	/**
	 * Copy constructor 
	 * 
	 * Initializes the binary density function from a property set.
	 */
	public BinaryDensityFunction( Properties props ) {
		mValues = new double[] {
			Convert.toDouble( props.getProperty("min"), 0.0 ),
			Convert.toDouble( props.getProperty("max"), 1.0 )
		};
		mBias = Convert.toDouble( props.getProperty("bias"), 0.0 );
		mSeed = Convert.toLong( props.getProperty("seed"), System.currentTimeMillis() );
		mRandom = new RandomDataImpl();
		mRandom.reSeed( mSeed );
		
		// prints out the seed used
		LogManager.getInstance().log( LogManager.DEFAULT, 
				LogSeverityEnum.LSV_DEBUG,
				"Binary density function seed value is " + mSeed + " !" );
	}
	
	
	/**
	 * Return the  
	 * @param x
	 * @return
	 */
	public double nextValue() {
		return mValues[( mRandom.nextUniform(-1,1)>mBias ) ? 1 : 0 ];
	}

}
