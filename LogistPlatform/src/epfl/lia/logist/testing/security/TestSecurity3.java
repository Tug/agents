package epfl.lia.logist.testing.security;

/* Importation table */
import epfl.lia.logist.security.SecurityManager;
import epfl.lia.logist.security.*;


/**
 * This test bed creates one thread group belonging to a different thread
 * group than the primary one. It then tries to create the security manager
 * and call it from another thread group, which is theoretically not allowed.
 * 
 * This sample shows one flaw of this security management system. If a thread
 * can get access by any means to the 'main' thread group, then it can
 * create a thread belonging to the 'main' thread group and which would
 * return a pointer to the security manager without passing through the
 * singleton interface.
 * 
 * This however assumes that the user has a means to access the 'main'
 * thread group, which is not possible in LogistAgent due to restriction
 * on the run mode of the behaviors. Moreover, this is a hack, quite difficult
 * to get right.
 */
public class TestSecurity3 {

	/**
	 * This is the thread trying to access the security
	 * manager. It prints YATTA !!!! if it can access it
	 */
	private class AuxiliaryThread extends Thread {
		public AuxiliaryThread( ThreadGroup g, String n ) {
			super(g,n);
		}
		public void run() {
			SecurityManager.getInstance();
			System.out.println( "YATTA !!!!" );
		}
	}
	
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
			
			// initializes thread group
			ThreadGroup loThreadGroup = null;
			
			// enumerates all threads
			Thread[] laThreadArray = new Thread[Thread.activeCount()];
			Thread.enumerate(laThreadArray );
			
			// searches for one thread belonging to thread group 'main'
			for ( int i=0; i<laThreadArray.length; i++ ) {
				System.out.println( "Examining thread '" + laThreadArray[i] + "' ..." );
				if ( laThreadArray[i].getThreadGroup().getName().equals("main") ) {
					loThreadGroup = laThreadArray[i].getThreadGroup();
					System.out.println( "Thread '" + laThreadArray[i] + "' belongs to 'main' threadgroup" );
					break;
				}	
			}
			
			// if the 'main' thread group was found, then continues...
			if ( loThreadGroup != null ) {
				Thread t = new AuxiliaryThread( loThreadGroup, "HelloWorld" );
				t.start();
			} else {
				System.out.println( "No thread belonging to thread group 'main' was found..." );
			}
		}
	}
	

	/**
	 * Empty constructor...
	 */
	public TestSecurity3() {
		new SecurityManager();
	}


	/**
	 * Runs the security test
	 */
	public static void main( String[] args ) {
		new TestSecurity3().run();
	}
	

	/**
	 * Launches the security test.
	 */
	public void run() {

		// creates the agent thread group
		ThreadGroup lsThreadGroup = new ThreadGroup( "agents" );
		
		// creates both threads and starts them
		new SampleWorkerThread( lsThreadGroup, "worker1" ).start();
		new SampleWorkerThread( Thread.currentThread().getThreadGroup(), "worker2" ).start();
	}
}
