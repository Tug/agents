package epfl.lia.logist.agent;

/* importation table */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 * 
 * @author malves
 *
 */
public class AgentHistory {

	/* Constants representing the type of event */
	protected static final int EVT_MOVE = 0;
	protected static final int EVT_PICKUP = 1;
	protected static final int EVT_DELIVER = 2;
	
	
	/**
	 * This object represents an event in the history. This allows
	 * keeping track of the events that led the agent to its final goal.
	 */
	private class HistoryEvent { 
		
		/* The type of history */
		private final int mType;
		
		/* The reward associated to this event */
		private final double mReward;
		
		/* The distance associated to this event */
		private final double mDistance;
		
		/* The id of this event */
		private final int mID;
		
		
		/**
		 * Constructor of the event 
		 * 
		 * @param id The identifier of the event
		 * @param t The type of the event
		 * @param r The reward associated to the event
		 * @param d The distance associated to the event
		 */
		public HistoryEvent( int id, int t, double r, double d ) {
			mID = id;
			mType=t;
			mReward=r;
			mDistance=d;
		}
		
		
		/**
		 * Converts the history event to an XML element.
		 */
		public String toString() {
			String result="<action id=\"" + mID + "\" type=\"";
			switch( mType ) {
				case EVT_MOVE: result += "move"; break;
				case EVT_PICKUP: result += "pickup"; break;
				case EVT_DELIVER: result += "deliver"; break;
			}
			result += "\" reward=\"" + mReward + "\" distance=\"" + 
					  mDistance + "\" />";
			return result;
		}
	}
	
	
	/* The list of history events */
	private ArrayList<HistoryEvent> mEventList = null;

	/* The total cumulated reward for corresponding agent */
	private double mTotalReward = 0;
	
	/* The total cumulation distance for this agent */
	private double mTotalDistance = 0;
	
	/* The total cumulated cost for this agent */
	private double mTotalCost = 0;
	
	/* The total cumulated gain for this agent */
	private double mTotalGain = 0;
	
	/* The current event id */
	private int mID = 0;
	
	/* The output stream associated to the history */
	private PrintStream out = null;
	
	/* The file where to store */
	private String mFilename = "default.xml";
	
	
	/**
	 * Constructor of the class
	 */
	public AgentHistory( String name ) {
		mEventList = new ArrayList<HistoryEvent>();
		mFilename=name + ".xml";
	}

	/**
	 * Appends a new pickup event to the history
	 * 
	 * @param target The target city of the picked task
	 */
	public void pickup( String target ) {
		mEventList.add( new HistoryEvent(mID++,EVT_PICKUP,0,0) );
	}
	
	/**
	 * Appends a new move event to the history
	 * 
	 * @param target The target city to move to
	 * @param cost The cost of this operation (>0)
	 * @param distance The distance 
	 */
	public void move( String target, double cost, double distance ) {
		mEventList.add( new HistoryEvent(mID++,EVT_MOVE,-cost,distance) );
		this.mTotalDistance += distance;
		this.mTotalCost += cost;
		this.mTotalGain -= cost;
	}
	
	
	/**
	 * Appends a new deliver event to the history
	 *  
	 * @param target The target city where to deliver
	 * @param reward The reward of this task
	 */
	public void deliver( String target, double reward ) {
		mEventList.add( new HistoryEvent(mID++,EVT_DELIVER,reward,0.0) );
		this.mTotalReward += reward;
		this.mTotalGain += reward;
	}	
	
	
	/**
	 * Adds some reward to the history stats
	 */
	public void addReward( double reward ) {
		this.mTotalReward += reward;
		this.mTotalGain += reward;
	}
	
	
	/**
	 * Adds some cost to the history stats
	 */
	public void addCost( double cost ) {
		this.mTotalCost += cost;
		this.mTotalGain -= cost;
	}
	
	
	/**
	 * Adds some distance to the history stats
	 */
	public void addDistance( double distance ) {
		this.mTotalDistance += distance;
	}
	
	
	/**
	 * Flushes the events of a particular round to the file. This avoids
	 * having high amounts of data on the memory.
	 */
	public void flushRound( int round ) {
		out.println( "\t<round id=\"" + round + "\">" );
		for( HistoryEvent e : mEventList ) {
			out.println( "\t\t" + e );
		}
		mEventList.clear();
		out.println( "\t</round>" );
		this.mID = 0;
	}
	
	
	/**
	 * Initializes the history event.
	 */
	public void init() throws IOException {
		
		// opens a stream in output mode
		FileOutputStream lOutStream = new FileOutputStream( this.mFilename );
		
		// creates a print stream to write the XML file
		out = new PrintStream( lOutStream );
		
		// outputs the XML tag
		out.println( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" );
		
		// outputs the init takg
		out.println( "<history>" );
	}
	
	
	/**
	 * Closes all streams and outputs the last lines
	 */
	public void shutdown() {
		out.println( "\t<statistics>" );
		out.println( "\t\t<total-cost>" + this.mTotalCost + "</total-cost>" );
		out.println( "\t\t<total-reward>" + this.mTotalReward + "</total-reward>" );
		out.println( "\t\t<total-gain>" + this.mTotalGain + "</total-gain>" );
		out.println( "\t\t<total-distance>" + this.mTotalDistance + "</total-distance>" );
		out.println( "\t</statistics>" );
		out.println( "</history>" );
		out.close();
	}
}
