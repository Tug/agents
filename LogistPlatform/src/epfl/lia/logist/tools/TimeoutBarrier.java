package epfl.lia.logist.tools;

/* importation table */
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;


/**
 * 
 * @author salves
 *
 * @param <E>
 */
public class TimeoutBarrier<E> {

	/**
	 * This class represents a rk
	 *
	 */
	private class BarrierTask extends TimerTask {
		
		/* The associated barrier */
		protected TimeoutBarrier<E> barrier;
		
		/**
		 * Constructor of the task.
		 */
		public BarrierTask( TimeoutBarrier<E> e ) {
			barrier = e;
		}
		
		/**
		 * Invoked after timeout.
		 */
		public void run() {
			barrier.setTimedOut();
		}
	}
	
	/* The rejection list */
	private ArrayList<E> mRejectionList = null;
	
	/* The internal timer  */
	private Timer mInternalTimer = null;
	
	/* Indicates whether barrier has timeout */
	private boolean mHasTimedOut = false;
	
	
	/**
	 * Default constructor of the class. Initializes the 
	 * internal class instances.
	 */
	public TimeoutBarrier() {
		mRejectionList = new ArrayList<E>();
		mInternalTimer = new Timer();
	}
	
	
	/**
	 * Resets the barrier 
	 */
	public void reset() {
		mRejectionList.clear();
		mHasTimedOut = false;
	}
	
	
	/**
	 * Registers an object to the barrier
	 */
	public void register( E object ) {
		mRejectionList.add(object);
	}
	

	/**
	 * Announces arrival to the barrier 
	 */
	public void announce( E object ) {
		if ( mRejectionList.contains(object) )
			mRejectionList.remove(object);
		
	}
	
	
	/**
	 * Indicates whether the barrier is blocked or not
	 * 
	 * @return <strong>true</strong> if the barrier is blocked, 
	 * 		   <strong>false</strong> otherwise.
	 * 
	 * @throws TimeoutException An exception if barrier has timed out
	 *                          while being blocked.
	 */
	public boolean blocked() {
		return mRejectionList.size() > 0;
	}
	
	
	/**
	 * Indicates whether barrier has timed out or not
	 */
	public boolean timeout() {
		return mHasTimedOut;
	}
	
	
	/**
	 * Indicates whether a particular agent is blocked or not
	 */
	public boolean isBlocked( E obj ) {
		return mRejectionList.contains( obj );
	}
	
	
	/**
	 * Puts the barrier in waiting state but only for a certain
	 * amount of time.
	 */
	public void start( long timeout ) {
		mHasTimedOut = false;
		mInternalTimer.schedule( new BarrierTask(this), timeout );
	}
	
	
	/**
	 * TimerTask method which is automatically invoked when barrier 
	 * timeout expires.
	 */
	public void setTimedOut() {
		mHasTimedOut = true;
	}
}
