package auction;

import java.util.ArrayList;

import org.apache.commons.math.random.RandomDataImpl;

import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.distribution.function.IFunction;
import epfl.lia.logist.task.distribution.function.UniformDensityFunction;

/** 
 * Class adapted from the ProbabilisticTaskDistribution class
 * from the logistPlatform library
 */
public class MyTaskGenerator
{
	/* keeps the probability distribution for this task distribution */
	ProbabilityDistribution mProbDistribution = null;
	/* random number generator*/
	protected RandomDataImpl mRandom = null;
	/* array of city names to speed up task generation */
	private String[] mCityArray = null;
	/* the number of tasks that were generated */ 
	protected int mCount = 0;
	/* the density function for the reward */
	protected IFunction mWeightDensity = null;
	
	public MyTaskGenerator(ProbabilityDistribution mProbDistribution)
	{
		this.mProbDistribution = mProbDistribution;
		this.mRandom = new RandomDataImpl();
		// creates the city array
		int i=0;
		mCityArray = new String[ Topology.getInstance().getCities().size() ];
		for( City c : Topology.getInstance().getCities().values() ) {
			mCityArray[i++] = c.getNodeLabel();
		}
		mWeightDensity = new UniformDensityFunction();
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
	
	public ArrayList<Task> generate(int nt)
	{
		ArrayList<Task> taskList = new ArrayList<Task>();
		for(int i=0; i<nt; i++)
			taskList.add(next());
		return taskList;
	}
	
}
