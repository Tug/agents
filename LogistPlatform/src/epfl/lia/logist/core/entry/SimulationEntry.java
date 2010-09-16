package epfl.lia.logist.core.entry;

/* import table */
import java.io.File;
import java.io.FileNotFoundException;
import epfl.lia.logist.core.Simulation;
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.config.ConfigurationManager;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.tools.LogistConstants;


/**
 * The SimulationEntry object parses the command line arguments. It is
 * also responsible for creating a reader to create the initial configuration
 * from which we can set all simulation parameters
 */
public class SimulationEntry  {
	
	/* Holds the array of arguments passed to the  application. */
	private String[] mArgList = null;
	
	/* Holds the default configuration name */
	private String mConfigName = null;
	
	/* Holds the configuration filename */
	private String mConfigFile = null;
	
	
	/**
	 * Default constructor of the class. Passes the
	 * array of arguments passed to the application
	 * for further parsing.
	 */
	public SimulationEntry( String[] args ) {
		mArgList = args;
	}


	/**
	 * Starts the platform 
	 */
	public void start() throws Exception {
		
		// writes to the log manager
		LogManager lLog = LogManager.getInstance();
		
		// prepares the configuration object
		Configuration lConfig = null;
		
		// logs the starting event
		lLog.log( LogManager.DEFAULT, LogSeverityEnum.LSV_INFO, 
				"Platform started..." );
		
		// first of all parses application argument
		if ( !parseArguments(mArgList) )
			return;
		
		// the configuration file must be present
		if ( mConfigFile==null )
			throw new Exception( "A configuration file must be defined !" );
		
		// ensures that the configuration file exists
		File lCfgFile = new File( mConfigFile );
		if ( !lCfgFile.exists() )
			throw new FileNotFoundException( mConfigFile );
		
		// logs the event
		lLog.log( "main", LogSeverityEnum.LSV_INFO, 
				"Reading the configuration file ..." );
		
		// creates a new configuration manager object
		ConfigurationManager lConfigMgr = new ConfigurationManager( lCfgFile );
		
		// retrieves the complete configuration from the file
		if ( mConfigName == null ) {
			lConfig = lConfigMgr.getConfigurationByDefault();
		} else {
			lConfig = lConfigMgr.getConfigurationByName( mConfigName );
		}
		
		// is there any configuraiton ?
		if ( lConfig == null ) 
			throw new Exception( "No suitable configuration could be found !" );
		
		// now, we can start the platform
		Simulation lSimulation = new Simulation( lConfig );
	
		// starts the simulation
		lSimulation.run();
	}

	
	/**
	 * Parses the arguments
	 * 
	 * This method parses the command line arguments. At this time, the
	 * platform only supports a limited set of arguments.
	 */
	private boolean parseArguments( String[] args ) {
		
		// if there is not enough arguments, display usage
		if ( args.length == 0 ) {
			displayUsage();
			return false;
		}
		
		// parses every argument
		for ( int i=0; i<args.length; i++ ) {
			
			// a version flag
			if ( args[i].equals("--version") ) {
				displayVersion();
				return false;
			} else if ( args[i].equals("--help") ) {
				displayUsage();
				return false;
			} else if ( !args[i].startsWith("--") ) {
				if ( mConfigFile==null )
					mConfigFile=args[i];
				else
					if ( mConfigName==null )
						mConfigName=args[i];
			}
		}
		
		// returns something
		return true;
	}


	/**
	 * Displays the version of the platform
	 * 
	 * This method displays the current version of the platform. This number 
	 * is defined in the LogistConstants source file.
	 */
	private void displayVersion() {
		System.out.println( "EPFL-LIA Logist Simulation Platform" );
		System.out.println( "Copyright(C) 2007 by EPFL-LIA" );
		System.out.println( "Version " + LogistConstants.VERSION_MAJOR + "." +
				LogistConstants.VERSION_MINOR + "." +
				LogistConstants.VERSION_REVISION + ", running on " +
				System.getProperty("os.name") + " - " + 
				System.getProperty("os.arch") );
	}
	
	
	/**
	 * Displays the command line usage
	 */
	private void displayUsage() {
		System.out.println( "Usage: logist [-options] config default" );
		System.out.println( "\nwhere options include:" );
		System.out.println( "\t--version\n\t\tprint the current version " +
				"of the platform" );
		System.out.println( "\t--help\n\t\tdisplay this message" );
		System.out.println( "\tconfig\n\t\tthe name of the configuration file" );
		System.out.println( "\tdefault\n\t\tthe name of the configuration" );
	}	
}
