package epfl.lia.logist.testing.security;

import epfl.lia.logist.security.SecurityManager;
import epfl.lia.logist.security.*;


/**
 * This test bed creates one thread group belonging to a different thread
 * group than the primary one. It then tries to access an unauthorized
 * resource.
 */
public class TestSecurity1 {

	/**
	 * This represents a single worker thread
	 */
	private class SampleWorkerThread extends Thread {
		
		/** The parent object hosting the resource */
		private TestSecurity1 moParent;
		
		
		/**
		 * Default constructor of the thread
		 */
		public SampleWorkerThread( ThreadGroup group,
								   String name, 
								   TestSecurity1 obj ) {
			super( group, name );
			moParent = obj;
			System.out.println( "Thread <" + getName() + "> was created..." );
		} 
		
		/**
		 * Default run method
		 */
		public void run() {
			
			// only one thread at any time
			synchronized(this) {
				System.out.print( "Thread <" + getName() + "> - " );
				moParent.consumeResource1();
				System.out.print( "Thread <" + getName() + "> - " );
				moParent.consumeResource2();
			}
		}
	}
	

	/**
	 * Empty constructor...
	 */
	public TestSecurity1() {
		new SecurityManager();
	}


	/**
	 * Runs the security test
	 */
	public static void main( String[] args ) {
		new TestSecurity1().run();
	}


	/**
	 * This is the first resource that threads try to
	 * access and which is protected.
	 */
	public void consumeResource1() {
		try {
			SecurityManager.getInstance().checkAccess();
		} catch ( Exception e ) {
			System.out.println( "Access to resource 1 is denied to thread..." );
			return;
		}
		System.out.println( "Access to resource 1 was granted !!!" );
	}

	
	/**
	 * This is the second resource that threads try to
	 * access and which is protected.
	 */
	public void consumeResource2() {
		try {
			SecurityManager.getInstance().checkAccess();
		} catch ( Exception e ) {
			System.out.println( "Access to resource 2 is denied to thread..." );
			return;
		}
		System.out.println( "Access to resource 2 was granted !!!" );
	}


	/**
	 * Launches the security test.
	 */
	public void run() {

		// creates the agent thread group
		ThreadGroup l_ThreadGroup = new ThreadGroup( "agents" );

		// creation of the security policy
		// Rule1: TG.Name == 'main' ==> GRANT
		// Rule2: TG.Name == 'any'  ==> DENY
		SecurityPolicy loPolicy = new SecurityPolicy();
		loPolicy.append( SecurityRuleOpEnum.SROP_EQ, Thread.currentThread().getThreadGroup().getName(), SecurityRuleRightsEnum.SRRG_GRANT );
		loPolicy.append( SecurityRuleOpEnum.SROP_ANY, "", SecurityRuleRightsEnum.SRRG_DENY );

		// defines the policy
		SecurityManager.getInstance().definePolicy( loPolicy );

		// now accesses both resources
		System.out.print( "Thread <" + Thread.currentThread().getName() + "> - " );
		consumeResource1();
		System.out.print( "Thread <" + Thread.currentThread().getName() + "> - " );
		consumeResource2();
		
		// creates both threads and starts them
		new SampleWorkerThread( l_ThreadGroup, "worker1", this ).start();
		new SampleWorkerThread( l_ThreadGroup, "worker2", this ).start();
	}
}
