package org.logist.centralized;

import ilog.concert.IloException;
import ilog.solver.IlcGoal;
import ilog.solver.IlcIntExpr;
import ilog.solver.IlcIntVar;
import ilog.solver.IlcNumExpr;
import ilog.solver.IlcSolution;
import ilog.solver.IlcSolver;

import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.plan.GDeliverAction;
import epfl.lia.logist.agent.plan.GMoveAction;
import epfl.lia.logist.agent.plan.GPickupAction;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.task.Task;

public class OptimalCooperativePlanner {

	/* The list of tasks currently in the world */
	private ArrayList<Task> mTaskList = null;

	/* The list of vehicles currently owned by the company */
	private ArrayList<AgentProperties> mVehicleList = null;
	
	/* The topology of the world */
	private Topology mTopology = null;
	
	/* The log manager object */
	private LogManager mLogMgr = null;
		
	/* An array of the capacity of each vehicle */
	private int[] mCapacityArray = null;
	
	/* An array of the cost per km for each vehicle */
	private int[] mCostPerKmArray = null;
	
	/* An array of the weight for every task */
	private int[] mTaskWeightArray = null;
	
	/* An home of home cities */
	private int[] mHomeArray;

	/* The table holding the mappings from a city to an integer code */
	private HashMap<City, Integer> mCityToCodeMapping;

	/* The table holding the mappings from an integer code to a city */
	private HashMap<Integer, City> mCodeToCityMapping;
	
	/* The number of cities in the world */
	private int mNumOfCities = 0;
	
	/* The number of tasks in the world */
	private int mNumOfTasks = 0;
	
	/* The number of vehicles */
	private int mNumOfVehicles = 0;

	/**/
	private int[] mPickupCityOf;
	
	/**/
	private int[] mDeliveryCityOf;
	
	/* The array of distances between cities */
	private int[][] mDistanceTable;
	
	/**/
	private int NULL_CITY;

	
	/**
	 * 
	 */
//Java 1.5 feature
	public OptimalCooperativePlanner( ArrayList<Task> taskList, 
					  ArrayList<AgentProperties> vehicleList, 
					  Topology topology) {
		
		// stores the task list
		this.mTaskList = taskList;
		
		// stores the vehicle list
		this.mVehicleList = vehicleList;
		
		// stores the topology object
		this.mTopology=topology;

		// get an instance of the logmanager
		this.mLogMgr = LogManager.getInstance();
		
		// log the current event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing the optimal cooperative planner..." );
		
		// create integet mapping tables
		mCityToCodeMapping = new HashMap<City, Integer>();
		mCodeToCityMapping = new HashMap<Integer, City>();
		
		// log the current event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Creating city<->integer mapping tables..." );
		
		// initialize the coding for the cities
		initCodingForCities();
		
		mNumOfCities = mCityToCodeMapping.size();
		NULL_CITY = mNumOfCities;
		
		// start by getting the item counts
		mNumOfTasks = getNumOfTasks();
		mNumOfVehicles = getNumOfVehicles();
		
		// then get arrays
		mCapacityArray = getCapacityArray();
		mCostPerKmArray = getCostPerKmArray();
		mHomeArray = getHomeArray();
		mTaskWeightArray = getTaskWeightArray();
		
		//initialize the coding for pickup and delivery places
		mPickupCityOf = new int[ mNumOfTasks + mNumOfVehicles ];
		mDeliveryCityOf = new int[ mNumOfTasks ];
		
		// log the current event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing task tables..." );
		
		// initializes task pickup and delivery
		initTaskCities();

		// log the current event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Initializing distances..." );
		
		//initialize the distances
		mDistanceTable = new int[mNumOfCities][mNumOfCities+1];
		
		// initialize distances
		initDistances();
		
		// log the current event
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Optimal cooperative planner initialization is finished !" );
	}
	
	
	/**
	 * Initializes the maps in which a number corresponds to each city.
	 * @return
	 */
	private void initCodingForCities(){
		
		// get the topological map
		HashMap<String, City> mapOfCities = mTopology.getCities();	
		
		// the index number
		int i=0;

		// for every city in there, assign an integer value
		for ( City c : mapOfCities.values() ){
			
			// create an integer value from 'i'
			Integer _i = new Integer(i);
			
			// adds the mapping to the tables
			mCityToCodeMapping.put( c, _i );
			mCodeToCityMapping.put( _i, c );
			
			// increments i
			i++;
		}
	}
	
	/**
	 * Initialize the arrays containing the corresponding codes for
	 * the pickup and delivery cities of each task.
	 */
	private void initTaskCities(){
		
		// prepares the pickup lists
		for (int i=0; i<mTaskList.size(); i++) {
			Task t = (Task)mTaskList.get(i);
			City p = mTopology.getCity( t.getPickupCity() );
			City d = mTopology.getCity( t.getDeliveryCity() );
			mPickupCityOf[i] = mCityToCodeMapping.get(p);
			mDeliveryCityOf[i] = mCityToCodeMapping.get(d);
		}

		// prepares lists
		for (int k=0; k<mNumOfVehicles; k++){
			mPickupCityOf[mNumOfTasks+k] = NULL_CITY;
		}
		
	}
	
	
	/**
	 * Get the number of tasks to be carried out
	 * @return the number of tasks to be carried out
	 */
	private int getNumOfTasks(){
		return mTaskList.size();
	}
	
	/**
	 * Get the number of trucks of the company
	 * @return
	 */
	private int getNumOfVehicles(){
		return mVehicleList.size();
	}
	
	/**
	 * Get an int array containing the capacities of the vehicles owned 
	 * by this company. 
	 * @return an int array containing the capacities of the vehicles owned 
	 * by this company. result[0] is the capacity of the first vehicle, 
	 * result[1] is the capacity of the second vehicle, etc.
	 */
	private int[] getCapacityArray(){
		int[] capacityArray = new int[ mVehicleList.size() ];
		for ( int i=0; i<mVehicleList.size(); i++ ) {
			capacityArray[i] = (int)mVehicleList.get(i).Capacity;
		}
		return capacityArray;
	}

	/**
	 * Get an int array containing the costPerKm of the vehicles owned 
	 * by this company. 
	 * @return an int array containing the capacities of the vehicles owned 
	 * by this company. result[0] is the costPerKm of the first vehicle, 
	 * result[1] is the cost per KM of the second vehicle, etc.
	 */
	private int[] getCostPerKmArray(){
		int[] costPerKmArray = new int[ mVehicleList.size() ];
		for ( int i=0; i<mVehicleList.size(); i++ ) {
			costPerKmArray[i] = (int)mVehicleList.get(i).CostPerKm;
		}
		return costPerKmArray;
	}
	
	/**
	 * Get an int array with the loads of the tasks.
	 * @return an in array such that result[i] equals to the load
	 * of task i
	 */
	private int[] getTaskWeightArray(){
		int[] taskWeightArray = new int[ mTaskList.size() ];
		for ( int i=0; i<mTaskList.size(); i++ ) {
			taskWeightArray[i] = (int)mTaskList.get(i).getWeight();
		}
		return taskWeightArray;
	}

	
	
	
	/**
	 * Return an int array with the home city for each vehicle. The home
	 * city is coded as an integer
	 * @return
	 */
	private int[] getHomeArray(){
		int[] homeArray = new int[ mVehicleList.size() ];
		for ( int i=0; i<mVehicleList.size(); i++ ) {
			City c = mTopology.getCity(mVehicleList.get(i).Home);
			homeArray[i] = mCityToCodeMapping.get(c);
		}
		return homeArray;
	}

	
	/**
	 * Initialize the table with the minimum distance between two cities.
	 * dist[i][j] is the distance of the minimum path between the cities with the
	 * codes i and j.
	 * @return
	 */
	private void initDistances(){

		
		for (int i =0; i<mNumOfCities; i++){
			
			// set this to null
			mDistanceTable[i][i] = 0;
			
			
			for (int j=i+1; j<mNumOfCities; j++){
				
				// get the citis of index i and j
				City ci = (City) mCodeToCityMapping.get(i);
				City cj = (City) mCodeToCityMapping.get(j);
				
				// insert the shortest distance
				mDistanceTable[i][j] = (int)mTopology.shortestDistanceBetween(ci,cj);
			}
			
			// set this to null
			mDistanceTable[i][mNumOfCities] = 0;
		}
	}

	public HashMap<String, Plan> plan() {
		
		
		// the variables:
		// nextTask[i] = j if task j is carried out by some vehicle after task i
		// How many variables are there?
		// - for each task i: one variable (code: i)
		// - for each vehicle k: one variable representing the home place: (code: mNumOfTasks +k)
		// TOTAL: (mNumOfTasks + mNumOfVehicles) variables
		// 
		// What are the values each variable can take?
		// - a task j (code j)
		// - the stop point: (code: mNumOfTasks +k ) 
		
		// compute the necessary time to compute the plans
		long startTime = System.currentTimeMillis();

		// returns a map of plans, one for each vehicle
		HashMap<String, Plan> plans = new HashMap<String, Plan>();
	
		try{
			
			// create a new solver
			IlcSolver s = new IlcSolver();
			
			//------------------------------------------------------------------
			// V A R I A B L E S
			//------------------------------------------------------------------
			
			// an array of variables nextTask[E1] who takes value E2 if the
			// task E2 is carried out imediatelly after E1
			IlcIntVar[] nextTask = new IlcIntVar[ mNumOfTasks + mNumOfVehicles ];
			for (int i=0; i<nextTask.length; i++)
				nextTask[i] = s.intVar(0, mNumOfTasks+mNumOfVehicles-1, "nextTask["+i+"]");
			
			
			// vehicle[i] = k if task i is assigned to vehicle k
			IlcIntVar[] vehicle = new IlcIntVar[ mNumOfTasks + mNumOfVehicles ];
			for (int i=0; i<vehicle.length; i++)
				vehicle[i] = s.intVar(0, mNumOfVehicles-1, "vehicle["+i+"]");

			// time[i] is the sequence number in which task i is carried out
			// time[mNumOfTasks+k] is 0 (corresponding to the home depot)
			// time[mNumOfTasks+mNumOfVehicles+k] is the time corresponding to the 
			// stoping point of vehicle k
			IlcIntVar[] time = new IlcIntVar[mNumOfTasks+2*mNumOfVehicles];
			for (int i=0; i<time.length; i++)
				time[i] = s.intVar(0,mNumOfTasks+1,"time["+i+"]");


			//------------------------------------------------------------------
			// C O N S T R A I N T S 
			//------------------------------------------------------------------
			
			// Capacity constraints
			for (int i=0; i<mNumOfTasks; i++)
			    for (int k=0; k<mNumOfVehicles; k++)
			        if (mTaskWeightArray[i] > mCapacityArray[k]) 
			            s.add( s.neq(vehicle[i],k) );
			
			// The next task visited cannot be the same task
			for (int i=0; i<mNumOfTasks; i++)
				s.add( s.neq(nextTask[i],i) );
			
			// The next task visited after a home depot of a vehicle,
			// cannot be the stop point corresponding to another vehicle
			for (int k=0; k<mNumOfVehicles; k++){
				for (int j=0; j<mNumOfVehicles; j++){
					if (j != k)
						s.add( s.neq(nextTask[mNumOfTasks+k],mNumOfTasks+j) );
				}
			}
			
			// The array of values nextTask has to contain the following values:
			// - each task can be reached from exactly one other task
			// - each stop point has to be reached once 
			s.add( s.allDiff(nextTask) );
			
			// If E1 comes after E2, E1 and E2 are in the same vehicle
			for (int i=0; i<mNumOfTasks+mNumOfVehicles; i++)
				s.add( s.eq(vehicle[i], s.element(vehicle,nextTask[i])) );
			for (int k=0; k<mNumOfVehicles; k++)
				s.add(s.eq(vehicle[mNumOfTasks+k],k));
	
			// E1 is visited after E2, the time of E1 is the time of E2 +1
			for (int i=0; i<mNumOfTasks+mNumOfVehicles; i++){
				for (int j=0; j<mNumOfTasks; j++)
					s.add(s.imply(s.eq(nextTask[i],j), s.eq(time[j], 
								  s.sum(time[i],1))));
				for (int k=0; k<mNumOfVehicles; k++)
					s.add(s.imply(s.eq(nextTask[i],mNumOfTasks+k), 
								  s.eq(time[mNumOfTasks+mNumOfVehicles+k], 
										  s.sum(time[i],1))));
			}
			
			// The time of starting places is 0
			for (int k=0; k<mNumOfVehicles; k++)
				s.add(s.eq(time[mNumOfTasks+k],0));

			// The time of the stop place of a vehicle is bigger then
			// the time of any task belonging to the vehicle
			for (int i=0; i<mNumOfTasks; i++){
				IlcIntExpr stopInd = s.sum(mNumOfTasks+mNumOfVehicles, vehicle[i]);
				s.add(s.lt(time[i], s.element(time,stopInd)));
			}

			// compute the total distance driven
			/*
			 * Compute the total distance travelled
			 */

			IlcIntExpr totalDist = s.constant(0);
			
			for (int i=0; i < mNumOfTasks; i++){
			    //distance for delivering i: dist[pickupCityOf[i]][deliveryCityOf[i]]
			    //distance to drive to the next task: dist[deliverCityOf[i]][pickupCityOf[nextPlace[i]]
				totalDist = s.sum(totalDist, 
				        s.prod(s.element(mCostPerKmArray, vehicle[i]), 
				             s.sum(mDistanceTable[mPickupCityOf[i]][mDeliveryCityOf[i]],
				                   s.element(mDistanceTable[mDeliveryCityOf[i]], s.element(mPickupCityOf, nextTask[i])))));
			}

			for (int i=0; i < mNumOfVehicles; i++){
				totalDist = s.sum(totalDist, 
				        s.prod(mCostPerKmArray[i], s.element(mDistanceTable[mHomeArray[i]], s.element(mPickupCityOf, nextTask[mNumOfTasks+i]))));
			}

			//add minimization constraint
			s.add(s.minimize(totalDist));
			
			IlcSolution sol = s.solution();
			sol.add(nextTask);
			sol.add(vehicle);
			

			IlcGoal g1 = s.generate(nextTask);
			//IlcGoal g = s.and(s.defaultGoal(),s.storeSolution(sol));
			//IlcGoal g1 = new Generate(vehicle,time,nextPlace, nrTasks, nrVehicles, dist, cityOf, home);
			IlcGoal g = s.and(g1, s.storeSolution(sol));
			
			//s.setTraceVar(true);
			//s.setTraceAll(true);
			
			s.newSearch(g); while (s.next()){
			}

			s.restartSearch();
			boolean solFound = s.next();
			
			
			// we haven't found any solution !
			if ( !solFound ){
				mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_WARNING, 
					"There exists no feasible plan !" );
				mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
					"Planning took " + (System.currentTimeMillis()-startTime) + 
					" milliseconds !" );
			    return null;
			}
			
			//------------------------------------------------------------------
			// C R E A T E   T H E   P L A N S
			//------------------------------------------------------------------
			
			for ( int k=0; k<mNumOfVehicles; k++ ) {
			
				// gets the properties of the agent
				AgentProperties ap = mVehicleList.get( k );
				
				// creates a brand new plan
				Plan plan = new Plan();
				
				// get the current location
				City currentCity = mTopology.getCity( ap.Home );

				// temporary variables
				double distance = 0.0;
				City nextCity;
				
				int codeNextTask = mNumOfTasks+k;
				while( (codeNextTask=sol.getValue(nextTask[codeNextTask])) < mNumOfTasks ) {
					
					// get the next destination 
					City nextDestination = mCodeToCityMapping.get(mPickupCityOf[codeNextTask]);

					// move till the pickup location
					while ( !currentCity.match(nextDestination) ) {
						
						// moves on the shortest path
						nextCity = mTopology.moveOnShortestPath( currentCity, nextDestination );

						// computes the distance till the next city
						distance = mTopology.shortestDistanceBetween( currentCity, nextCity );
						
						// adds the action
						plan.addAction( new GMoveAction(nextCity,distance) );
						
						// stores the next city
						currentCity = nextCity;
					}
					
					// get the next task to pickup
					Task t = mTaskList.get( codeNextTask );
					
					// adds a pickup action
					plan.addAction( new GPickupAction(t) );
					
					// now, add the sequence of moves until delivering the
					// task...
					nextDestination = mCodeToCityMapping.get( mDeliveryCityOf[codeNextTask] );
					
					// continues looping while not found 
					while( !currentCity.match(nextDestination) ) {
					
						// moves on the shortest path
						nextCity = mTopology.moveOnShortestPath( currentCity, nextDestination );
						
						// computes the distance till the next city
						distance = mTopology.shortestDistanceBetween( currentCity, nextCity );
						
						// adds the action
						plan.addAction( new GMoveAction(nextCity,distance) );
						
						// stores the next city
						currentCity = nextCity;
						
					}
					
					// adds the deliver action to the list of actions
					plan.addAction( new GDeliverAction(t) );
				}
				
				// shows the plan for this agent
				mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
						"[" + ap.Name + "] " + plan  );
						
				// adds the plan
				plans.put( ap.Name, plan );
			}
			 
			
		} catch( IloException error ){
			mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_ERROR, 
					"CooperativePlanner: plan(): Error occured: " + error  );
		}
		
		// tell how many time has passed
		mLogMgr.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Planning took " + (System.currentTimeMillis()-startTime) + 
				" milliseconds !" );
		
		// return the list of plans
		return plans;
	}
	

/*	
	private static String toString(IlcIntVar[] array){
		String s="[";
		for (int i=0; i<array.length; i++){
			String str = array[i].toString().substring(array[i].getName().length());
			s += str;
		}
			//s += "["+array[i].toString()+"]";
		s += "]";
		return s;
	}

	private String toString(IlcIntVar[][][] array){
		String s="";
		for (int i = 0; i<array.length; i++){
			for (int k=0; k<mNumOfVehicles; k++){

				for (int j=0; j<array[i].length; j++){
					s += array[i][j][k]+ " ";
				}
				s += "\t";
			}
			s += "\n";
		}
		return s;
	}
*/	
}
