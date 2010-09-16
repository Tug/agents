package auction;

// the list of imports
import java.util.ArrayList;
import java.util.HashMap;

import centralized.*;
import centralized.tabu.*;

import epfl.lia.logist.agent.AgentProperties;
import epfl.lia.logist.agent.behavior.Behavior;
import epfl.lia.logist.agent.behavior.response.BidBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.EmptyBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.IBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.ReadyBehaviorResponse;
import epfl.lia.logist.agent.behavior.response.StrategyBehaviorResponse;
import epfl.lia.logist.agent.plan.Plan;
import epfl.lia.logist.core.topology.City;
import epfl.lia.logist.core.topology.Topology;
import epfl.lia.logist.exception.BehaviorExecutionError;
import epfl.lia.logist.exception.BehaviorNotImplementedError;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.messaging.signal.AskBidSignal;
import epfl.lia.logist.messaging.signal.AuctionLostSignal;
import epfl.lia.logist.messaging.signal.AuctionNotificationObject;
import epfl.lia.logist.messaging.signal.AuctionWonSignal;
import epfl.lia.logist.messaging.signal.InStateObject;
import epfl.lia.logist.messaging.signal.InStateSignal;
import epfl.lia.logist.messaging.signal.InitSignal;
import epfl.lia.logist.messaging.signal.ResetSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.messaging.signal.AuctionSetupObject;
import epfl.lia.logist.task.ProbabilityDistribution;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.OptimalCooperativePlanner;


/**
 * 
 * @author salves
 *
 */
public class BidderCompanyBehavior extends Behavior
{
	private static final double maxTimeBidSec = 30;
	protected AgentProperties mAgentProps = null;
	protected ProbabilityDistribution probabilityDistr = null;
	protected Company myCompany;
	
	/**
	 * execute the behavior
	 */
	@Override
	public IBehaviorResponse execute(Signal s) throws BehaviorExecutionError,
			BehaviorNotImplementedError
	{
		
		log( LogSeverityEnum.LSV_DEBUG, "Signal: " + s.getType() );
		
		switch( s.getType() ) {
		
			case SMT_INIT:
				initAgent( ((InitSignal)s).getMessage() );
				return new ReadyBehaviorResponse();
				
			case SMT_RESET:
				resetAgent( ((ResetSignal)s).getMessage() );
				return new ReadyBehaviorResponse();
				
			case SMT_SETUP:
				setupAgent( (AuctionSetupObject)s.getMessage() );
				return new ReadyBehaviorResponse();
				
			case SMT_KILL:
				killAgent();
				return new ReadyBehaviorResponse();
				
			case SMT_AUCTION_START:
				return new ReadyBehaviorResponse();
				
			case SMT_AUCTION_END:	
				handleEndOfAuction();
				return new ReadyBehaviorResponse();
				
			case SMT_ASKBID:
				double bidValue = considerTask( 
						((AskBidSignal)s).getMessage() );
				return new BidBehaviorResponse( bidValue );
				
			case SMT_AUCTION_WON:
				handleAuctionItem( ((AuctionWonSignal)s).getMessage(), true );
				return new EmptyBehaviorResponse();

			case SMT_AUCTION_LOST:
				handleAuctionItem( ((AuctionLostSignal)s).getMessage(), false );
				return new EmptyBehaviorResponse();
				
			case SMT_INSTATE:
				return handleInStateSignal( ((InStateSignal)s).getMessage() );
		}
		
		// all other behaviors are not implemented
		throw new BehaviorNotImplementedError(s.getType());
	}

	protected void initAgent(AgentProperties ap) throws BehaviorExecutionError
	{
		mAgentProps = ap;
		log( LogSeverityEnum.LSV_INFO, "44[" + mAgentProps.Name + "] " +
				"initializing the bidder company agent..." );
	}
	
	protected void killAgent() throws BehaviorExecutionError
	{
		log( LogSeverityEnum.LSV_INFO, "[" + mAgentProps.Name + "] " +
				"killing the bidder company agent..." );		
	}
	
	protected void resetAgent(int round) throws BehaviorExecutionError
	{
		log( LogSeverityEnum.LSV_INFO, "[" + mAgentProps.Name + "] " +
				"entering round #" + round + "..." );
	}
	
	protected void setupAgent( AuctionSetupObject setup ) 
		throws BehaviorExecutionError
	{
		probabilityDistr = setup.Probabilities;
		ArrayList<Task> emptyList = new ArrayList<Task>();
		World myWorld = new World(emptyList, setup.Vehicles, setup.Topology);
		myCompany = new Company(myWorld, probabilityDistr);
		myCompany.setBidderCompanyBehavior(this);
		// logs the current event
		log( LogSeverityEnum.LSV_INFO, "[" + mAgentProps.Name + "] " +
				"seting the centralized agent up..." );
	}
	
	
	private double considerTask( TaskDescriptor tdd )
	{
		Task t = TaskManager.getInstance().getTaskFromID(tdd.ID);
		return myCompany.considerTask(t, maxTimeBidSec);
	}
	
	
	//--------------------------------------------------------------------------
	// Invoked when auction ends...
	//--------------------------------------------------------------------------
	private void handleEndOfAuction()
	{
		log( LogSeverityEnum.LSV_DEBUG, 
				">>> End of the auction! Assigned tasks are: " );
		
		for( Task t : myCompany.world.taskList) {
			log( LogSeverityEnum.LSV_DEBUG, 
					"\t<task from='"+t.getPickupCity()+"' to='"+t.getDeliveryCity()+"' />");
		}
	}
	
	//--------------------------------------------------------------------------
	// Handles winner or looser notifications
	//--------------------------------------------------------------------------
	private void handleAuctionItem(AuctionNotificationObject obj, boolean bWinner)
	{
		myCompany.auctionResults(bWinner, obj.Bids);
	}
	
	
	//--------------------------------------------------------------------------
	// Handles a <in-state> signal
	//--------------------------------------------------------------------------
	protected IBehaviorResponse handleInStateSignal(InStateObject ic) 
		throws BehaviorExecutionError
	{
		//vehicleList = ic.Vehicles;
		//this.stateObject = ic;
		log( LogSeverityEnum.LSV_INFO, "Computing plans..." );
		HashMap<String,Plan> plans = myCompany.getPlans(maxTimeBidSec);
		if(plans == null)
			log( LogSeverityEnum.LSV_WARNING, "No more tasks are available !" );
		double profit = myCompany.getPayAmount() - myCompany.world.getCost(plans);
		log( LogSeverityEnum.LSV_INFO, "profit="+profit );
		return new StrategyBehaviorResponse( plans );
	}

	public void print(String s)
	{
		log( LogSeverityEnum.LSV_DEBUG, ">>> " + s );
	}
	
}
