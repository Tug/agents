package epfl.lia.logist.config;

/* import table */
import java.io.File;
import epfl.lia.logist.exception.ConfigurationNotFoundException;
import epfl.lia.logist.exception.NoConfigurationException;


/**
 * The ConfigurationManager class manages the access to configuration data
 * stored in configuration files. It provides methods for loading either a 
 * default configuration, if anyone is provided, or a named configuration 
 * object.
 */
public class ConfigurationManager {

	/* The current configuration file object */
	private File mConfigFile = null;
	
	
	/**
	 * Copy constructor of the class. This method stores the file object 
	 * for later use.
	 * 
	 * @param cf a reference to a file object representing the 
	 *           configuration file.
	 */
	public ConfigurationManager( File cf ) {
		mConfigFile = cf;
	}
	
	
	/**
	 * Get a configuration object by its name. This configuration object is 
	 * built from descriptors filled from the configuration file. 
	 * The list of configurations is searched by name. If found, then, a
	 * configuration object is returned, else, the method throws an error
	 * 
	 * @param name the name of the configuration to load 
	 * @return     a configuration object holding simulation descriptors,
	 * @see        Configuration
	 */
	public Configuration getConfigurationByName( String name )
		throws Exception {
		
		// creates a new configuration reader
		ConfigurationReader configurationReader = 
			new ConfigurationReader( mConfigFile );
		
		// retrieves the simulation descriptor
		Configuration configurationObject = 
			configurationReader.load( name );
		
		// if the configuration was not found, throw an exception
		if ( configurationObject == null )
			throw new ConfigurationNotFoundException( name );
		
		//	returns the configuration name
		return configurationObject;

	}
	
	
	/**
	 * Return a configuration previously loaded from the configuration file. 
	 * The configuration is the one specified by the default attribute of the
	 * simulation tag of the configuration file. The configuration must exist
	 * or an exception is thrown
	 * 
	 * @return a configuration object holding simulation descriptors
	 * @see    Configuration
	 */
	public Configuration getConfigurationByDefault() 
		throws Exception {
		
		// creates a new configuration reader
		ConfigurationReader configurationReader = 
				new ConfigurationReader( mConfigFile );
		
		// retrieves the simulation descriptor
		Configuration configurationObject = configurationReader.load( null );
		
		// if the configuration was not found, throw an exception
		if ( configurationObject == null )
			throw new NoConfigurationException();
		
		//	returns the configuration name
		return configurationObject;
	}
}
