package epfl.lia.logist.task.distribution.function;

import java.util.Properties;

import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.Convert;
import org.apache.commons.math.random.RandomDataImpl;


public class NormalDensityFunction implements IFunction {

	/* the center of the distribution */
	private double mMean = 0.0;
	
	/* the square root of the variance */
	private double mDeviation = 0.0;
	
	/* vertical bounds for the distribution */
	private double[] mYB;
	
	/* sets the random generator */
	private RandomDataImpl mRandom = null;
	
	/* the seed value */
	private long mSeed = 0;
	
	
	/**
	 * Default class constructor
	 * 
	 * Initializes class interval values to defaults.
	 */
	public NormalDensityFunction() {
		
		// initializes bounds
		mYB = new double[] { 0.0, 1.0 };
		
		// finds the mean and variance
		mMean = 0.0;
		mDeviation = 0.44721359549995793928183473374626;
		
		// defines the seed value
		mSeed = System.currentTimeMillis();
		
		// creates the random generator
		mRandom = new RandomDataImpl();
		mRandom.reSeed( mSeed );	
		
		// logs the seed to use
		LogManager.getInstance().log( LogManager.DEFAULT, 
				LogSeverityEnum.LSV_DEBUG,
				"Normal density function seed value is " + mSeed + " !" );
	}
	
	
	/**
	 * Constructor of the class
	 * 
	 * Initializes all the parameters of the distribution. These parameters
	 * are:
	 * <br />
	 * <ul>
	 * 	<li>mean</li>
	 *  <li>variance</li>
	 *  <li>deviation</li>
	 *  <li>x-min</li>
	 *  <li>x-max</li>
	 *  <li>y-min</li>
	 *  <li>y-max</li>
	 * </ul>
	 */
	public NormalDensityFunction( Properties props ) {
		
		// initializes bounds 
		mYB = new double[] {
			Convert.toDouble( props.getProperty("y-min"), 0.0 ),
			Convert.toDouble( props.getProperty("y-max"), 1.0 )
		};
		
		// finds the mean and variance
		mMean = Convert.toDouble( props.getProperty("mean"), 0.0 );
		double variance = Convert.toDouble( props.getProperty("variance"), -1.0 );
		if ( variance < 0.0 ) {
			mDeviation = Convert.toDouble( props.getProperty("deviation"), -1.0 );
			if ( mDeviation <= 0.0 ) {
				mDeviation = 0.44721359549995793928183473374626;
			}
		} else {
			mDeviation = Math.sqrt( variance );
		}
		
		// finds the bounds of the distribution
		;
		;
		
		// loads the seed value
		mSeed = Convert.toLong( props.getProperty("seed"), System.currentTimeMillis() );
		
		// creates the random generator
		mRandom = new RandomDataImpl();
		mRandom.reSeed( mSeed );
		
		// logs the seed to use
		LogManager.getInstance().log( LogManager.DEFAULT, 
				LogSeverityEnum.LSV_DEBUG,
				"Normal density function seed value is " + mSeed + " !" );
	}
	

	/**
	 * Generates the next value in the distribution
	 */
	public double nextValue() {
		double y = mRandom.nextGaussian( mMean, mDeviation );
		if(y < mYB[0]) 
			return mYB[0];
		if(y > mYB[1])
			return mYB[1];
			          
		return mYB[0] + (mYB[1]-mYB[0]) * y;
	}
}
