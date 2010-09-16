package epfl.lia.logist.task.distribution;

/* import table */
import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.tools.Pair;


/**
 * The DiscreteTaskDistribution class initialized a TaskDistribution object
 * with tasks loaded from the disk. This is typically done for exercices that
 * require a fixed set of well known tasks from the beginning.
 */
public class DiscreteTaskDistribution implements ITaskDistribution {
	
	/* The tasklist that was loaded from the disk */
	private ArrayList<Task> mListOfTasks = null;
	
	/* The list of deleted tasks */
	private ArrayList<Task> mListOfDeletedTasks = null;
	
	/* The associated probability distribution */
	private ProbabilityDistribution mProbDistribution = null;
	
	
	/**
	 * Constructor of the class. Initializes the list of tasks.
	 */
	public DiscreteTaskDistribution( ArrayList<Task> al ) {
		mListOfTasks = al;
		mListOfDeletedTasks = new ArrayList<Task>();
		initializeDistribution();
	}
	
	/**
	 * Initialize the distribution.
	 *
	 * For the discrete case, it is necessary to set the probability to 1, where
	 * there is a task and 0 for all other pair of cities.
	 */
	private void initializeDistribution() {

		// creates the distributions
		HashMap<Pair<City>,Double> mProbMap = new HashMap<Pair<City>,Double>();
		HashMap<Pair<City>,Double> mRewardMap = new HashMap<Pair<City>,Double>();
		
		// retrieve the topology element
		Topology topology = Topology.getInstance();
		
		// for each task, add it...
		for ( Task t : mListOfTasks ) {
			
			// get pickup and delivery cities
			City pickupCity = topology.getCity( t.getPickupCity() );
			City deliveryCity = topology.getCity( t.getDeliveryCity() );
			
			// adds probs and rewards
			Pair<City> pairOfCities =
				new Pair<City>(pickupCity, deliveryCity);
			mProbMap.put( pairOfCities, 1.0 );
			mRewardMap.put( pairOfCities, t.getRewardPerKm() );	
			
		}
		
		for ( City cs: topology.getCities().values() ) {
			for ( City cd: topology.getCities().values() ) {
				
				// create the pair of cities
				Pair<City> pairOfCities = new Pair<City>(cs,cd);
				
				// for all other cities, put at zero ! 
				if ( !mProbMap.containsKey(pairOfCities) ) {
					mProbMap.put( pairOfCities, 0.0 );
					mRewardMap.put( pairOfCities, 0.0 );
				}
			}
		}
		
		// returns the newly created probability distribution
		this.mProbDistribution = 
			new ProbabilityDistribution( mProbMap, mRewardMap );
	}
	
	
	/**
	 * Indicates whether there are more tasks.
	 */
	public boolean hasMoreTasks() {
		return mListOfTasks.size()>0;
	}
	
	
	/**
	 * Return the next task in the list
	 */
	public Task next() {
		Task t = null;
		if ( mListOfTasks.size()>0 ) {
			int iIndex = (int)(Math.random() * (double)mListOfTasks.size());
			t = mListOfTasks.remove(iIndex);
			mListOfDeletedTasks.add( t );
		}
		return t;
	}
	
	
	/**
	 * Resets the task distribution in order to
	 * reset the task lists
	 */
	public void reset() {
		ArrayList<Task> temp = this.mListOfTasks;
		this.mListOfTasks = this.mListOfDeletedTasks;
		this.mListOfDeletedTasks = temp;
	}
	
	
	/**
	 * Retrives a parameter from text distribution
	 * 
	 * @param name The name of the parameter
	 */
	public ProbabilityDistribution getProbabilityDistribution() {
		return mProbDistribution;
	}
}