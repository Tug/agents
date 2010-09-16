package epfl.lia.logist.task;

import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.tools.Pair;


/**
 * 
 *
 */
public class ProbabilityDistribution {

	/* The probability map holds for each city the probability that there
	   is a task */
	private HashMap<Pair<City>,Double> mProbabilityMap = new HashMap<Pair<City>,Double>();
	private HashMap<Pair<City>,Double> mRewardMap = new HashMap<Pair<City>,Double>();
	
	/**
	 * Class constructor. Stores the probability map
	 */
	public ProbabilityDistribution( HashMap<Pair<City>,Double> mapProbs,
									HashMap<Pair<City>,Double> mapReward ) {
		mProbabilityMap = mapProbs;
		mRewardMap = mapReward;
	}

	
	/**
	 * Return the probability of having a task in city c1 to deliver in city c2
	 */
	public double getProbability( City c1, City c2 ) {
		Pair<City> pair = new Pair<City>( c1, c2 );
		Double prob = mProbabilityMap.get( pair );
		if ( prob != null ) return prob;
		return 0.0;
	}
	
	
	/**
	 * Return the summed probability of all neighbors of given city 
	 */
	public double getSumNeighborsProb( City c  ) {
		double probability = 0.0;
		Pair<City> pair = null;
		ArrayList nodes = c.getOutNodes();
		for( Object node : nodes ) {
			pair = new Pair<City>(c,(City)node);
			Double prob = mProbabilityMap.get( pair );
			if ( prob != null ) 
				probability += prob;
		}
		return probability;
	}
	
	
	/**
	 * Returns the expected reward for delivering a task from city 1 to
	 * city 2.
	 */
	public double getRewardPerKm( City c1, City c2 ) {
		Pair<City> pair = new Pair<City>( c1, c2 );
		Double reward = mRewardMap.get( pair );
		if ( reward != null ) return reward;
		return 0.0;
	}
}
