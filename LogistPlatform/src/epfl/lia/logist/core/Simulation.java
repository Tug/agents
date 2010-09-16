package epfl.lia.logist.core;

/* import table */
import uchicago.src.sim.engine.SimInit;
import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;


/**
 * This object uses a configuration object to launch the simulation. The
 * configuration object should be deleted as soon as it is no more used, since
 * it occupies a great amount of memory
 */
public class Simulation implements Runnable {
	
	/*
	 * Holds the configuration object linked to
	 * the simulation...
	 */
	private Configuration mConfiguration = null;
	
	
	/**
	 * Constructor of the class
	 * 
	 * Initializes a pointer to a configuration object previously loaded
	 * and filled with configuration data
	 */
	public Simulation( Configuration cfg ) {
		mConfiguration = cfg;
	}
	
	
	/**
	 * Run the simulation from loaded configuration
	 * 
	 * This method initializes a Repast Model and then loads the whole 
	 * simulation parameters from the loaded configuration... 
	 */
	public void run() { 
	
		// initializes repast
		SimInit init = new SimInit();
	
		// creates a new model
		SimulationModel model = new SimulationModel();
	
		// creates the model from the configuration
		try {
			
			// another safe check
			if( mConfiguration == null )
				throw new Exception( "No configuration is available." );
			
			// creates the model from a configuration
			model.createFromConfiguration( mConfiguration );
		
			// deletes the configuration object from memory
			mConfiguration = null;
			
			// calls the GC to free memory
			Runtime.getRuntime().freeMemory();
			
			// loads the repast model 
			init.loadModel( model, null, false );
			
		} catch( Exception e ) {
			LogManager.getInstance().log( LogManager.DEFAULT, LogSeverityEnum.LSV_FATAL, 
					"Caught exception: " + e.getMessage() );
			System.err.println( "Caught fatal exception: " + e.getMessage() ) ;
			e.printStackTrace();
			System.exit( -1 );
		}
	}
}
