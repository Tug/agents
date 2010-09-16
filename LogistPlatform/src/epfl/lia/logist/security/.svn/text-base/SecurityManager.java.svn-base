package epfl.lia.logist.security;

/**
 * The security manager is responsible for managing access to
 * singleton resources. It checks access and denies to objects
 * that are not listed in current policy.
 */
public class SecurityManager
{
	/**
	 * Keeps a single instance of the class alive
	 */
	private static SecurityManager msSingleton = null;


	/**
	 * The set of rules applying at the instant
	 */
	private SecurityPolicy moPolicy = null;


	/** 
	 * Default constructor of the class. Initializes the
	 * singleton, and the whole manager.
	 */
	public SecurityManager() {
		
		// initialize the singleton
		if ( msSingleton == null )
			msSingleton = this;
		
		// initializes the manager
		init();
	}

	
	/** 
	 * Checks if current thread has access to resource
	 */
	public void checkAccess() throws Exception {
		if ( moPolicy != null )
			moPolicy.eval();
	}


	/**
	 * Defines the set of rules applying from now on
	 */
	public void definePolicy( SecurityPolicy policy ) {
		moPolicy = policy;	
	}


	/** 
	 * Retrieves the singleton instance of this class
	 */
	public static synchronized SecurityManager getInstance() {
		
		// only the main thread group as the possibility to access this
		if ( !Thread.currentThread().getThreadGroup().getName().equals("main") )
			throw new SecurityException( "Unauthorized access to Security Manager");
		
		// returns the singleton
		return msSingleton;
	}


	/**
	 * Initializes the internal state of the class
	 */
	public void init() {
		moPolicy = null;
	}


	/**
	 * Clears out the state of the manager
	 */
	public void shutdown() {
		moPolicy = null;
	}
}
