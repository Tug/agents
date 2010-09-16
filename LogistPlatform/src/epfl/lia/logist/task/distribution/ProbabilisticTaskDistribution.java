package epfl.lia.logist.task.distribution;

/* import table */
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.math.random.RandomDataImpl;

import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.task.ProbabilityDescriptor;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskDistributionDescriptor;
import epfl.lia.logist.task.distribution.function.BinaryDensityFunction;
import epfl.lia.logist.task.distribution.function.DiracDensityFunction;
import epfl.lia.logist.task.distribution.function.IFunction;
import epfl.lia.logist.task.distribution.function.NormalDensityFunction;
import epfl.lia.logist.task.distribution.function.UniformDensityFunction;
import epfl.lia.logist.tools.Convert;
import epfl.lia.logist.tools.Pair;


/**
 * The probabilistic distribution creates tasks based on a probabilitistic
 * distribution.
 */
public class ProbabilisticTaskDistribution implements ITaskDistribution {
	
	/* keeps the probability distribution for this task distribution */
	ProbabilityDistribution mProbDistribution = null;

	/* the density function for the reward */
	protected IFunction mRewardDensity = null;
	
	/* the density function for probs */
	protected IFunction mProbDensity = null;
	
	/* the density function for the reward */
	protected IFunction mWeightDensity = null;
	
	/* indicates if we should normalize results */
	protected boolean mNormalize = false;
	
	/* the number of tasks that were generated */ 
	protected int mCount = 0;
	
	/* random number generator*/
	protected RandomDataImpl mRandom = null;
	
	/* array of city names to speed up task generation */
	private String[] mCityArray = null;
	
	
	/**
	 * Default constructor
	 * 
	 * The constructor method initializes all default values, or initializes
	 * from values stored in the configuration file.
	 * <br />
	 * The probability distribution for the tasks is a normal distribution. A x
	 * value is choosen in the interval [min;max]. The user can choose all
	 * the parameters of the distribution.
	 */
	public ProbabilisticTaskDistribution( TaskDistributionDescriptor tdd ) 
		throws Exception {
		
		// creates a new distribution
		mRandom = new RandomDataImpl();
		
		// initialize the task distribution
		initDistribution( tdd );
		
		// creates the city array
		int i=0;
		mCityArray = new String[ Topology.getInstance().getCities().size() ];
		for( City c : Topology.getInstance().getCities().values() ) {
			mCityArray[i++] = c.getNodeLabel();
		}
	}
	
	
	/**
	 * Finds a probability function from its name.
	 * 
	 * This method gets a string as input and searches for the corresponding
	 * function object. If the name corresponds to a type, then an instance
	 * with defaulted values is returned. If the name corresponds to an user
	 * defined probability function, then a new probability function is 
	 * created with custom values.
	 * 
	 * @param name the name or type of the probability function
	 * @param props the custom attributies given in the form of a Properties obj
	 * 
	 * @return a function that correponds to the type associated to the name.
	 */
	protected IFunction findFunction( String name, Properties props ) {
		
		// no name ? no function
		if ( name==null ) return null;
		
		// binary distribution ?
		if ( name.equals("binary") ) {
			return new BinaryDensityFunction();
			
		// gaussian distribution ?
		} else if ( name.equals("gaussian") || name.equals("normal") ) {
			return new NormalDensityFunction();
			
		// uniform distirbution ?
		} else if ( name.equals("uniform") ) {
			return new UniformDensityFunction();

		// constant distirbution ?
		} else if ( name.equals("constant") ) {
			return new DiracDensityFunction();
			
		// name does not correspond to a type, but to a user-defined function
		} else if ( props != null && props.containsKey("type") ) {
			
			// first gets the type of the function
			String type = props.getProperty( "type" );
			
			// binary function ?
			if ( type.equals("binary") ) {
				return new BinaryDensityFunction( props );
				
			// normal function ?
			} else if ( type.equals("gaussian") || type.equals("normal") ) {
				return new NormalDensityFunction( props );
				
			// uniform function ?
			} else if ( type.equals("uniform") ) {
				return new UniformDensityFunction( props );
				
			// constant function ?
			} else if ( type.equals("constant") ) {
				return new DiracDensityFunction( props );
				
			// something else ?
			} else {
				return null;
			}
		}
		
		// nothing to return
		return null;
	}

	
	/**
	 * Initializes the probability distribution for this agent
	 */
	protected void initDistribution( TaskDistributionDescriptor tdd ) 
		throws Exception {
		
		// first get the topology manager
		Topology topo = Topology.getInstance();
		LogManager log = LogManager.getInstance();
		Pair<City> pair = null;

		// reseeds from 
		long seed = Convert.toLong( tdd.Props.getProperty("seed"), 12345 );
		mRandom.reSeed( seed );
		
		
		// should results be normalized ?
		mNormalize = Convert.toBoolean( tdd.Props.getProperty("normalize"), true );
		
		// gets the name of both densities
		String probDensityFn =  Convert.toString( tdd.Props.getProperty("probability"), "default" );
		String rewardDensityFn = Convert.toString( tdd.Props.getProperty("reward"), "default" );
		String weightDensityFn = Convert.toString( tdd.Props.getProperty("weight"), "constant" );
		
		// gets the properties objects
		Properties probDensityParams = tdd.DensityFunctions.get( probDensityFn );
		Properties rewardDensityParams = tdd.DensityFunctions.get( rewardDensityFn );
		Properties weightDensityParams = tdd.DensityFunctions.get( weightDensityFn );
		
		// constructs the density function for the probabilities
		mProbDensity = findFunction( probDensityFn, probDensityParams );
		
		// if the density function was not found, then send an error
		if ( mProbDensity == null ) {
			throw new Exception( "Could not found a valid density " +
					"function for probability description !" );
		}
		
		// constructs the density function for the reward
		mRewardDensity = findFunction( rewardDensityFn, rewardDensityParams );
		
		// if the density function was not found, then send an error
		if ( mRewardDensity == null ) {
			throw new Exception( "Could not found a valid density " +
					"function for reward description !" );
		}

		// constructs the density function for the reward
		mWeightDensity = findFunction( weightDensityFn, weightDensityParams );
		
		// if the density function was not found, then send an error
		if ( mWeightDensity == null ) {
			throw new Exception( "Could not found a valid density " +
					"function for weight description !" );
		}
		
		// both pointer should be valid 
		assert( mProbDensity != null && mRewardDensity != null );
		
		
		// creates the distributions
		HashMap<Pair<City>,Double> mProbMap = new HashMap<Pair<City>,Double>();
		HashMap<Pair<City>,Double> mRewardMap = new HashMap<Pair<City>,Double>();
		
		// are there already some probabilities ? if that's the case, then
		// add it to the list of probabilities and rewards..
		if ( tdd.ProbDescriptorList != null &&
			 tdd.ProbDescriptorList.size() > 0 ) {
			for( ProbabilityDescriptor pp : tdd.ProbDescriptorList ) {
				
				// gets both cities
				City cityFrom = topo.getCity( pp.From );
				City cityTo = topo.getCity( pp.To );
				
				// if one of them is invalid, then skip entry
				if ( cityFrom == null || cityTo == null ) {
					log.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
							"A task probability for pair (" + pp.From + "," +
							pp.To + ") was not added. One of cities " +
							"is invalid" );
					continue;
				}
				
				// initializes the numbers
				double dblTaskProb = mProbDensity.nextValue();
				double dblRewardProb = mRewardDensity.nextValue();
				
				// replace them if necessary
				if ( pp.Task != null ) dblTaskProb = pp.Task;
				if ( pp.Reward != null ) dblRewardProb = pp.Reward;
				
				// adds the entry
				pair = new Pair<City>( cityFrom, cityTo );
				mProbMap.put( pair, dblTaskProb );
				mRewardMap.put( pair, dblRewardProb );
			}
		}
	
		// some variables 
		double dblProbability = 0.0;
		double dblReward = 0.0;
		
		// for every entry in the table, add probabilities
		for( City city1 : topo.getCities().values() ) {
			for ( City city2 : topo.getCities().values() ) {
				
				// builds the pair of cities
				pair = new Pair<City>( city1, city2 );
			
				// avoid entries that already have some value
				if ( mProbMap.get(pair) != null ||
					 mRewardMap.get(pair) != null )
					continue;
				
				// we must set a probability of 0.0 for (Ei,Ei) pairs
				if ( city1.match(city2) ) {
					dblProbability = 0.0;
					dblReward = 0.0;
					
				// for all other cities, we take a random value from the
				// distribution function...
				} else {
					dblProbability = mProbDensity.nextValue();
					if(dblProbability < 0) {
						System.out.println("Hmmm ...");
					}
					dblReward = mRewardDensity.nextValue();
				}
	
				// adds the probabilities to the table
				mProbMap.put( pair, dblProbability );
				mRewardMap.put( pair, dblReward );
			}
		}

		// if the distribution should be normalized, then we
		// normalize it ...
		if ( mNormalize ) {
			
			// holds some values here...
			double dPositiveSum;
			double dProb;
			double dNoTaskProb;
			
			// should we normalize ?
			for( City city1 : topo.getCities().values() ) {
				
				// resets the positive sum and the probability
				// of having no task...
				dPositiveSum = 0.0;
				dNoTaskProb = 1.0;
				
				// for each city, add the task probability to the sum and
				// multiply the probability of not having a task going to city2...
				for ( City city2 : topo.getCities().values() ) {
					pair = new Pair<City>( city1, city2 );
					dProb = mProbMap.get( pair );
					dPositiveSum += dProb;
					dNoTaskProb *= 1-dProb;
				}
				
				// computes the final sum and adds the probability of not
				// having any task in this city. this results in the final
				// probability...
				dPositiveSum += dNoTaskProb;
				if ( dPositiveSum < 0.0001 ) dPositiveSum = 1.0;
				
				// now, the probability is updated again
				for ( City city2 : topo.getCities().values() ) {
					pair = new Pair<City>( city1, city2 );
					dProb = mProbMap.get( pair );
					mProbMap.put( pair, dProb / dPositiveSum );
				}
			}
		}
		
		// retrieves the probability distribution
		this.mProbDistribution = new ProbabilityDistribution(mProbMap,mRewardMap);
	}
	
	
	/**
	 * Indicates whether there exist more tasks.
	 * 
	 * For probabilistic distribution, there are an infinite amount of tasks
	 * available. For this reason, this method should always return true.
	 */
	public boolean hasMoreTasks() {
		return true;
	}
	
	
	/**
	 * Return the next task in the set.
	 */
	public Task next() {

		// get the topology object
		Topology topo = Topology.getInstance();
		
		// choose one random city
		int cityNum = mRandom.nextInt( 0, mCityArray.length-1 );
	
		// returns the choose city
		City cSourceCity = topo.getCity( this.mCityArray[cityNum] );
		
		// now, we choose one random probabilitiy
		double dProbability = mRandom.nextUniform( 0.0, 0.1 );
		double dStockedProb = 0.0;
		
		// creates a new task descriptor
		TaskDescriptor tdd = new TaskDescriptor();
		
		// selects one city among neighbor cities. The choice is done as follows:
		//
		// 1. we select a probability P following our distribution
		// 2. for every neighbor city:
		//	2.a	get p the stocked probability for pair (city,neighbor)
		//	2.b if ( P<p ) then select this city if random()>0.5
		// 3. if no city was found, we take by default the pair with the highest
		//	  probability
		for ( City cDestCity : topo.getCities().values() ) {
		
			// avoid tasks from a city to the same city
			if ( cDestCity.match(cSourceCity) ) continue;
			
			// finds the stocked probability
			dStockedProb = mProbDistribution.getProbability( cSourceCity, cDestCity );
			
			// if the random prob is smaller, then create a task here...
			if ( dProbability < dStockedProb ) {
				tdd.ID = mCount++;
				tdd.DeliveryCity = cDestCity.getNodeLabel();
				tdd.PickupCity = cSourceCity.getNodeLabel();
				tdd.RewardPerKm = mProbDistribution.getRewardPerKm( cSourceCity, cDestCity );
				tdd.Weight = mWeightDensity.nextValue();
				break;
			}
		}
		
		// if no task was defined, defined it here...
		if ( tdd == null ) {
			
			// gets a random city while cities match
			cityNum = mRandom.nextInt( 0, mCityArray.length-1 );
			City cDestCity = topo.getCity( mCityArray[cityNum] );
			while( cDestCity.match(cSourceCity) ) {
				cityNum = mRandom.nextInt( 0, mCityArray.length-1 );
				cDestCity = topo.getCity( mCityArray[cityNum] );
			}
			
			// builds the task descriptor
			tdd = new TaskDescriptor();
			tdd.ID = mCount++;
			tdd.DeliveryCity = cDestCity.getNodeLabel();
			tdd.PickupCity = cSourceCity.getNodeLabel();
			tdd.RewardPerKm = mProbDistribution.getRewardPerKm( cSourceCity, cDestCity );
			tdd.Weight = 0;
		}
		
		// returns the new task
		return new Task( tdd );
	}

	
	/**
	 * Resets the task distribution
	 */
	public void reset() {
		// does absolutely nothing at all !
	}
	
	
	/**
	 * Returns the probability distribution for this agent
	 */
	public ProbabilityDistribution getProbabilityDistribution() {
		return mProbDistribution;
	}
	
}