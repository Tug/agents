package epfl.lia.logist.agent.state.auction;

/* importation table */
import java.util.ArrayList;
import java.util.HashMap;

import epfl.lia.logist.agent.AgentProfile;
import epfl.lia.logist.agent.AgentStateEnum;
import epfl.lia.logist.agent.state.AgentState;
import epfl.lia.logist.agent.state.CompanyAgentState;
import epfl.lia.logist.messaging.action.Action;
import epfl.lia.logist.messaging.action.ActionTypeEnum;
import epfl.lia.logist.messaging.action.BidAction;
import epfl.lia.logist.messaging.signal.AskBidSignal;
import epfl.lia.logist.messaging.signal.AuctionEndSignal;
import epfl.lia.logist.messaging.signal.AuctionLostSignal;
import epfl.lia.logist.messaging.signal.AuctionNotificationObject;
import epfl.lia.logist.messaging.signal.AuctionStartSignal;
import epfl.lia.logist.messaging.signal.AuctionWonSignal;
import epfl.lia.logist.messaging.signal.Signal;
import epfl.lia.logist.task.Task;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskManager;
import epfl.lia.logist.tools.AID;
import epfl.lia.logist.tools.LogistGlobals;
import epfl.lia.logist.tools.TimeoutBarrier;



/**
 * 
 * @author malves
 *
 */
public class AuctionCompanyState extends CompanyAgentState {

	/* The list of auctions for this round */
	private HashMap<AID,Double> mAuctionBids = null;
	
	/* The starting time of the auction */
	private long mStartTime = 0;
	
	/* The current list of tasks for the session */
	private ArrayList<Task> mListOfTasks = null;
	
	/* The current barrier */
	private TimeoutBarrier<AID> mBarrier = null;
	
	/* The current number of registered bids */
	private int mRegisteredBids = 0;
	
	/* The current task descriptor */
	private TaskDescriptor mCurrentTaskDescriptor = null;
	
	/* Keeps the current task */
	private Task mCurrentTask = null;
	
	
	
	/**
	 * Constructor of the company state
	 * 
	 * @param ap the company profile object
	 * @param as the parent state object
	 */
	public AuctionCompanyState( AgentProfile ap, AgentState as ) {
		super(ap,as);
	}
	
	
	/**
	 * Executes the behavior of the agent state
	 */
	public void execute( Action<?> action ) {
		
		// what state are we in
		switch( mState ) {
		
			// setups the agent
			case AS_SETUP: 
				handleSetupState(); 
				break;
				
			// arriving the in the idle state.
			case AS_IDLE: 
				handleIdleState(); 
				break;
				
			// beginning of the auction	
			case AS_AUCTION_START: 
				handleAuctionStartState(); 
				break;
				
			// auctionning an item
			case AS_AUCTION_ITEM: 
				handleAuctionItemState( action ); 
				break;
		
			// end of the auction
			case AS_AUCTION_END: 
				handleAuctionTerminateState(); 
				break;
				
			// the auction is truly finished
			case AS_FINISHED:
				break;
		}
	}
	
	
	/**
	 * Handle the SETUP state.
	 */
	private void handleSetupState() {

		// arriving in the setup state...
		info( "[Auction] System is setting up...." );

		// clears the list of task descriptors
		if ( mListOfTasks == null ) {
			mListOfTasks = new ArrayList<Task>();
		}

		// initialize the list of bids
		if ( mAuctionBids == null ) {
			mAuctionBids = new HashMap<AID,Double>();
		}
		
		// advances to the next state
		mState = AgentStateEnum.AS_IDLE;
	}
	
	
	/**
	 * Handle the IDLE state.
	 */
	private void handleIdleState() {

		// arriving in the idle state...
		info( "[Auction] System is in idle state...." );

		// advances to the next state
		mState = AgentStateEnum.AS_AUCTION_START;
	}
	
	
	/**
	 * Notified the company agent that a task batch was created
	 */
	public void notifyTaskBatchCreation() {
		System.out.println( "->>>>>>> A new batch was created !!!!" );
		if ( mState==AgentStateEnum.AS_FINISHED ) {
			mState = AgentStateEnum.AS_IDLE;
		}
	}
	
	/**
	 * Handle the AUCTION_STATE state.
	 */
	private void handleAuctionStartState() {
		
		// arriving in the idle state...
		info( "[Auction] Initializing the auction..." );

		// does the barrier exist ? if not recreate it
		if ( mBarrier==null ) {
		   mBarrier = new TimeoutBarrier<AID>();			
		}
				
		// reset the barrier
		mBarrier.reset();
				
		// register the participants
		for( AgentProfile ap : mProfile.getChildren() ) {
			
			// informs the user about registering
		   info( "Registering agent " + ap.getName() + "[" + ap.getID() + "] against barrier..." );
		   
		   // register the barrier
		   mBarrier.register( ap.getID() );
		}
		
		// get the task manager
		TaskManager taskMgr = TaskManager.getInstance();
		
		// get the current list of task
		ArrayList<Task> lTaskList = taskMgr.getTaskList( false );
		
		// print some debugging information
		debug( "[Auction] List of tasks to auction:" );
		
		// clears the master list of tasks
		mListOfTasks.clear();
		
		// prints out the list of tasks
		for( Task t : lTaskList ) {
			
			// display the list of tasks
			debug( "\t" + t.toString() );
			
			// adds the task to the list
			mListOfTasks.add( t );
		}

		// clears the list of bids
		mAuctionBids.clear();
		
		// resets the list of bids
		for ( AgentProfile ap : mProfile.getChildren() ) {
			
			// resets the bids
			mAuctionBids.put( ap.getID(), 0.0 );

			// creates the signal
			AuctionStartSignal signal =
				new AuctionStartSignal(getObjectID(),ap.getAgent().getObjectID());
			
			// notifies the child agent about the start of the auction
			ap.getState().notifySignal( signal );
			
			// initializes the auctions
			mDispatcher.post( signal );
				
		}
		
		// prints the auction list
		displayAuctionList();
		
		// change the state
		mState = AgentStateEnum.AS_AUCTION_ITEM;
		mPhase = AgentStateEnum.AS_PHASE1;
	}
	
	
	/**
	 * Handle the AUCTION_ITEM state.
	 */
	private void handleAuctionItemState( Action<?> action ) {

		// consider the current phase
		switch( mPhase ) {
	
			// PHASE 1 -  Posting the AskBid signal
			case AS_PHASE1:

				// auctionning a new task...
				info( "-----------------------------------------------------" );
				
				// propose the next auctionning item
				prepareNextItemToAuction();
				
				// set the barrier timeout
				mBarrier.start( mProfile.getGlobals().AuctionTimeout );
				
				// set the next state
				mPhase = AgentStateEnum.AS_PHASE2;
				
				// breaks out of here
				break;
			
			// PHASE 2 - Wait for the response to the bid
			case AS_PHASE2:
				
				// register the bid from the user
				if ( action!=null && action.getType()==ActionTypeEnum.AMT_BID ) { 
					registerAuctionBid( (BidAction)action );
				}
				
				// goes to the next phase
				if ( mBarrier.timeout() || mRegisteredBids==mAuctionBids.size() ) {

					info( "mRegisteredBids==" + mAuctionBids.size() );
					
					// display the information message
					if ( mRegisteredBids==mAuctionBids.size() )
						info( "All agents finished bidding !!!" );
					else 
						info( "The barrier timed out..." );
					
					// goes to the next pahse
					mPhase = AgentStateEnum.AS_PHASE3;
				}
				
				// breaks out of here
				break;
			
			// PHASE 3 - Send a TaskReceived signal and restarts
			case AS_PHASE3:

				// display the list of auctions
				displayAuctionList();
				
				// barrier has expired or finished, then announce the winners
				// and loosers
				if ( !mBarrier.blocked() || mBarrier.timeout() ||
						mRegisteredBids==mAuctionBids.size() ) {
					assignTaskWinnerAndLoosers();
				}
				break;
		}
	}
	
	
	/**
	 * Handle the AUCTION_TERMINATE state.
	 */
	private void handleAuctionTerminateState() {
		
		// logs the event
		info( "Auction has ended !!!" );

		// ends the auction
		for ( AgentProfile ap : mProfile.getChildren() ) {
		
			// creates the signal
			AuctionEndSignal signal =
				new AuctionEndSignal(getObjectID(),ap.getAgent().getObjectID());
				
			// notifies the agent
			ap.getState().notifySignal( signal );
			
			// initializes the auctions
			mDispatcher.post( signal ); 
		}
		
		// end of the auction
		mState = AgentStateEnum.AS_FINISHED;
	}
	
	
	/**
	 * This class handles the next item to auction. The item is taken 
	 * randomly in the list of items to pickup.
	 */
	private void prepareNextItemToAuction() {

		// retrieve the next item in the list
		mCurrentTask = getNextItemToAuction();
		
		// this item must exist
		assert( mCurrentTask != null );
		
		// creates the task descriptor
		mCurrentTaskDescriptor = mCurrentTask.getDescriptor();
		
		// resets the counter
		this.mRegisteredBids = 0;
		
		// information about auction
		info( "Auctionning task: <task from='"+
				mCurrentTaskDescriptor.PickupCity + "' to='" +
				mCurrentTaskDescriptor.DeliveryCity + "' />" );
		
		// post the auctionning signal
		postAskBidSignalToEveryOne( mCurrentTaskDescriptor );
	}
	
	
	/**
	 * This method returns the next item to auction. The next item
	 * is a random choice from a pool of free tasks.
	 */
	private Task getNextItemToAuction() {
		
		// is there a least one task ?
		if ( mListOfTasks.isEmpty() ) 
			return null;
		
		// choose the next index 
		int iRandomTaskIndex = (int)(Math.random() * mListOfTasks.size());
		
		// remove the task from the list
		return mListOfTasks.remove( iRandomTaskIndex );
	}
	
	
	/**
	 * Post the ask bid signal to agent participating in the auction. 
	 */
	private void postAskBidSignalToEveryOne( TaskDescriptor tdd ) {
		
		// post the signal for all available children
		for( AgentProfile ap : mProfile.getChildren() ) {
			
			// create a new signal 
			AskBidSignal signal =
				new AskBidSignal( getObjectID(), 
								  ap.getAgent().getObjectID(), tdd );
			
			// inform the state agent for the reception emission of
			// a particular signal to the states....
			ap.getState().notifySignal( signal );
			
			// post the new signal
			mDispatcher.post( signal );
		}
	}
	
	
	
	/**
	 * Helper method displaying the list of items.
	 */
	private void displayAuctionList() {
		
		// prints some info
		info( "List of auction bids: " );
		
		// the final string to display
		String strResult = "B: ";
		
		// print the list of bids
		for( AID aid : mAuctionBids.keySet()) {
			if ( mAuctionBids.containsKey(aid) ) 
				strResult = strResult + mAuctionBids.get(aid) + " ";
			else
				strResult = strResult + "#inf ";
		}
		
		// displays the rest of the information
		info( strResult );
	}
	
	
	/**
	 * Assigns the task to the winner and looser
	 */
	private void assignTaskWinnerAndLoosers() {

		// store the maximum value
		ArrayList<Double> arrayOfBids = new ArrayList<Double>();
		double dblMinValue = Double.MAX_VALUE;
		AgentProfile rfWinnerAgent = null;
		
		// for every agent profile, designate winners and loosers
		for( AgentProfile ap : mProfile.getChildren() ) {
		
			// get the bid value
			Double dblBidValue = mAuctionBids.get( ap.getID() );
			
			// if none, set to 0
			if ( dblBidValue==null ) dblBidValue = 0.0;
			
			// if the bid is higher, define as new winner
			if ( dblBidValue < dblMinValue ) {
				dblMinValue = dblBidValue;
				rfWinnerAgent = ap;
			}
			
			// add the array of bids
			arrayOfBids.add( dblBidValue );
		}

		// who is the best ?
		if ( rfWinnerAgent!=null ) {
			info( "[Auction] " + rfWinnerAgent.getName() + " is the winner " +
					"with a bid value of " + dblMinValue + "..." );
		}
		
		// this is the reward of the task
		mCurrentTask.setRewardPerKm( dblMinValue );
		
		// inform about the situation
		info( "[Auction] Assigning winners and loosers..." );

		// creates a signal to send
		Signal<?> signal = null;
		
		// creates a new notification object
		AuctionNotificationObject notificationObject = 
			new AuctionNotificationObject();
		
		// fills the structure
		notificationObject.Bids = new Double[ arrayOfBids.size() ];
		arrayOfBids.toArray( notificationObject.Bids );
		notificationObject.Task = mCurrentTaskDescriptor;
		
		// for every child agent show winners and loosers
		for ( AgentProfile ap : mProfile.getChildren() ) {
			if ( ap == rfWinnerAgent ) {
				info( "[Auction] Dispatching notification to the winner !" );
				signal = new AuctionWonSignal(
						getObjectID(), rfWinnerAgent.getAgent().getObjectID(),
						(AuctionNotificationObject)notificationObject.clone() );
			} else {
				info( "[Auction] Dispatching notification to the looser!" );
				signal = new AuctionLostSignal(
						getObjectID(), ap.getAgent().getObjectID(),
						(AuctionNotificationObject)notificationObject.clone() );
			}
			
			// post the signal
			mDispatcher.post( signal );
			
			// notify the child agent that a particular signal was sent
			// to its corresponding entity and it will soon receive a response...
			ap.getState().notifySignal( signal );
		}
		
		// if there is more tasks to auction ? if that's not the case
		// then
		if ( hasMoreTasksToAuction() ) {
			mPhase=AgentStateEnum.AS_PHASE1;
		} else {
			mPhase=AgentStateEnum.AS_PHASE1;
			mState=AgentStateEnum.AS_AUCTION_END;
		}	
	}
	
	
	/**
	 * This method informs whether there are some tasks left to auction
	 */
	private boolean hasMoreTasksToAuction() {
		return mListOfTasks.size() > 0;
	}
	
	
	/**
	 * This method registers the bid of a particular agent.
	 */
	private void registerAuctionBid( BidAction action ) {
		
		// get message and attribute
		AID bidderID = action.getSenderID();
		Double bidderValue = action.getMessage();
		
		// displays some information
		info( "Registering bid for agent " + action.getSenderID() + ": " 
				+ action.getMessage() );
		
		// if the bid doesn't exist, then put it in the list
		if ( bidderValue==null ) bidderValue=0.0;
		
		// puts the bid
		mAuctionBids.put( bidderID, bidderValue );
		
		// increments the number of registered bids
		this.mRegisteredBids++;
	}
}

