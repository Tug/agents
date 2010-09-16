package epfl.lia.logist.testing.security;

/* Importation table */
import epfl.lia.logist.security.SecurityManager;
import epfl.lia.logist.security.*;


/**
 * This test bed creates one thread group belonging to a different thread
 * group than the primary one. It then tries to create the security manager
 * and call it from another thread group, which is not allowed.
 */
public class TestSecurity2 {

	/**
	 * This represents a single worker thread
	 */
	private class SampleWorkerThread extends Thread {
		
		/**
		 * Default constructor of the thread
		 */
		public SampleWorkerThread( ThreadGroup group,
								   String name ) {
			super( group, name );
			System.out.println( "Thread <" + getName() + "> was created..." );
		} 
		
		/**
		 * Default run method
		 */
		public synchronized void run() {
			SecurityManager.getInstance();
			System.out.println( "We'll see this line if nothing bad happens" );
		}
	}
	

	/**
	 * Empty constructor...
	 */
	public TestSecurity2() {
		new SecurityManager();
	}


	/**
	 * Runs the security test
	 */
	public static void main( String[] args ) {
		new TestSecurity2().run();
	}
	

	/**
	 * Launches the security test.
	 */
	public void run() {

		// creates the agent thread group
		ThreadGroup lsThreadGroup = new ThreadGroup( "agents" );
		
		// creates both threads and starts them
		new SampleWorkerThread( lsThreadGroup, "worker1" ).start();
	}
}
