package epfl.lia.logist.config;

/* import table */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import epfl.lia.logist.agent.AgentDescriptor;
import epfl.lia.logist.agent.AgentsetDescriptor;
import epfl.lia.logist.agent.behavior.BehaviorDescriptor;
import epfl.lia.logist.core.topology.CityDescriptor;
import epfl.lia.logist.core.topology.RouteDescriptor;
import epfl.lia.logist.core.topology.TopologyDescriptor;
import epfl.lia.logist.exception.ConfigurationParsingError;
import epfl.lia.logist.logging.LogDescriptor;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;
import epfl.lia.logist.logging.LogsetDescriptor;
import epfl.lia.logist.task.ProbabilityDescriptor;
import epfl.lia.logist.task.TaskDescriptor;
import epfl.lia.logist.task.TaskDistributionDescriptor;
import epfl.lia.logist.task.TaskgenDescriptor;
import epfl.lia.logist.task.TasksetDescriptor;
import epfl.lia.logist.tools.Convert;
import epfl.lia.logist.tools.LogistConstants;



/**
 * This class handles the parsing of the configuration file.
 * <p>
 * A configuration file is composed of many configuration elements. Each 
 * top-level element can in turn be specified in a different file provided a
 * link is given to it. Configurations describe every single property about
 * the state of the platform for a single simulation. That means that for 
 * every different simulation (reactive, deliberative, etc...), a configuration
 * must be created.
 */
public class ConfigurationReader {

	/* The name of the main configuration file  */
	private File mFile = null;
	
	/* A reference to the log manager class */
	private LogManager mLogMgr = null;
	
	/* The file path leading to the config file */
	private File mBaseDirectory = null;

	
	/**
	 * Copy constructor of the class. This constructor method stores the 
	 * filename of the configuration file and tests if it exists. If not, a
	 * FileNotFoundException is thrown. This method also constructs a File 
	 * object specifying the base directory of the configuration. It means
	 * that every external file path must be given relative to the path of 
	 * the main configuration file.
	 * 
	 * @param cf the configuration file object
	 * @throws FileNotFoundException if the configuration file was not found
	 */
	public ConfigurationReader( File cf ) throws FileNotFoundException {
		
		// load the file
		mFile = cf;
		
		// the file must exist or an exception is thrown
		if ( !cf.exists() )
			throw new FileNotFoundException( cf.getAbsolutePath() );

		// get the base directory
		mBaseDirectory = cf.getParentFile(); 
			
		// initialize a reference to the log manager object
		mLogMgr = LogManager.getInstance();
	}
	
	
	/**
	 * Loads an entire designated configuration object from the main XML
	 * configuration file. As it is possible to specify as many configuration
	 * elements in the file as desired, this function takes the name of 
	 * the configuration element as a parameter. It avois an unnecessary loading
	 * of every configuration until finding a good one.
	 * <p>
	 * The name parameter has a special meaning. When it has a value, it means
	 * the name of a configuration which exists. If it is null, then the 
	 * platform will return the default configuration specified by the default
	 * attribute of the main <simulation> tag.
	 * 
	 * @param name the name of the configuration (null for the default one)
	 * @return a configuration object specifying the state of the simulation
	 * @throws Exception a generic exception if anything goes wrong
	 */
	public Configuration load( String name ) 
		throws Exception {
		
		// load the root element of the file
		Element rootElement = loadXMLRootFromDisk( mFile.getPath() );
		
		// the root element must exist
		if ( rootElement == null )
			throw new ConfigurationParsingError( "The configuration file " +
					"could not be loaded! Check file syntax !" );
		
		// first thing to verify is that global tag name is 'simulation'
		if ( !rootElement.getName().equals("simulation") ) {
			throw new ConfigurationParsingError( "Malformed configuration " +
				"file. Root element must be <simulation>." );
		}
		
		// load both attributes
		Attribute versionAttribute = rootElement.getAttribute( "version" );
		Attribute defaultAttribute = rootElement.getAttribute( "default" );
		
		// the version attribute is mandatory
		if ( versionAttribute==null ) {
			throw new ConfigurationParsingError( "The version attribute of " +
					"the <simulation> tag is mandatory !" );
		}
		
		// get the platform version under the form of a string
		String platformVersionStr = LogistConstants.VERSION_MAJOR + "." + 
						  			LogistConstants.VERSION_MINOR;
		
		// retrieves the version of the platform
		if ( !versionAttribute.getValue().equals(platformVersionStr) ) {
			throw new ConfigurationParsingError( "Wrong configuration file " +
					"version. Should be " + platformVersionStr );
		}
		
		// we cannot have both the default attribute being null and the name 
		// of the configuration being null !
		if ( (name==null || name.length()==0 ) && 
			 (defaultAttribute==null || 
			  defaultAttribute.getValue().length()==0) ) {
			throw new ConfigurationParsingError( "You must specify a" + 
					"name for the configuration to load, or set the " +
					"default attribute to a correct value !" );
		}
		
		// if no name was specified for the configuration to load, then
		// load the default configuration !
		if ( name==null || name.length()==0 ) {
			name = defaultAttribute.getValue();
		}
		
		// next step is to get the list of the configuration nodes
		List configurationElements = rootElement.getChildren( "configuration" ); 
		
		// now, we must find the configuration by the name 
		Element configurationElement = 
						findConfigurationByName( configurationElements, name );
		
		// the element must exist or nothing will be loaded
		if ( configurationElement==null ) {
			if ( name!=null && name.length()!=0 ) 
				throw new ConfigurationParsingError( "The configuration " 
						+ "named '" + name + "' was not found !" );
		}
		
		// finally loads the configuration object
		Configuration configuration = 
			handleConfiguration( configurationElement );

		// the configuration is loaded, so return it !
		return configuration;
	}
	
	
	/**
	 * Searches a configuration element in the XML stream given by its name. 
	 * For every node, it loads the 'name' attribute and compares to the 
	 * configuration name passed in the arguments list.
	 * 
	 * @param configurationElements the list of configuration elements
	 * @param configurationName the name of the configuration to load
	 * @return a configuration element which name matches the desired 
	 *         configuration name
	 */
	private Element findConfigurationByName( List configurationElements,
											 String configurationName ) {
	
		// get an iterator from the list
		Iterator nodeIterator = configurationElements.iterator();
		
		// for every configuration out there, try finding the requested
		// configuration object...
		while( nodeIterator.hasNext() ) {
			
			// get the next element in the list
			Element nextElement = (Element)nodeIterator.next();
			
			// we try comparing the name passed as an argument and nodes name
			Attribute nameAttribute = nextElement.getAttribute( "name" );
			
			// the name attribute was not found, then continue looping
			if ( nameAttribute == null || nameAttribute.getValue().length()==0 )
				continue;
			
			// compare both names
			if ( nameAttribute.getValue().equals(configurationName) )
				return nextElement;
		}
		
		// no correspondance was found, exit !
		return null;
	}

	
	/**
	 * Parse a configuration element. This method makes a recursive descent
	 * through the configuration element passed in argument list and builds
	 * a configuration descriptor.
	 * 
	 * @param configurationElement the configuration element to explore
	 * @return a configuration object holding the state of the platform
	 * @throws ConfigurationParsingError if the parsing goes badly
	 */
	private Configuration handleConfiguration( Element configurationElement )
		throws ConfigurationParsingError  {

		// create a new configuration object
		Configuration configurationObject = new Configuration();
		
		// try loaded the configuration element from an external file 
		configurationElement = loadXMLExternal( configurationElement );
		
		// get the name attribute
		Attribute nameAttribute = configurationElement.getAttribute( "name" );
		
		// posts the event here
  	  	mLogMgr.info( "New configuration found: '" + 
  	  			nameAttribute.getValue() + "'" );
		
		// stores the value of the name
  	  	configurationObject.Name = nameAttribute.getValue();
		
		// retrieves  property information
  	  	configurationObject.Propset = handlePropertyset( 
  	  			configurationElement, true );
		
		// finds logging information
		configurationObject.Logs = handleLogset( configurationElement );
		
		// finds tasks information
		configurationObject.Tasks = handleTaskset( configurationElement );
		
		// finds topology information
		configurationObject.Topology = handleToposet( configurationElement );
		
		// finds agent information
		configurationObject.Agents = handleAgentset( configurationElement );
		
		// returns the configuration
		return configurationObject;
	}
	

	/**
	 * Parses a <logset> element. Logsets hold definitions about the log
	 * files used in the simulation. Three log file names are reserved:
	 * main (for the system log), stderr and stdout (for the screen).
	 * 
	 * @param configurationElement the parent <configuration> element
	 * @return a descriptor holding individual log descriptors 
	 */
	private LogsetDescriptor handleLogset( Element configurationElement )
		throws ConfigurationParsingError {
		
		// retrieve the first logset element
		Element logsetElement = getFirstElement( configurationElement, 
												 "logset", true );
		
		// tries to load the logset externally
		logsetElement = loadXMLExternal( logsetElement );
		
		// asserts that the logset element still exists
		assert( logsetElement != null );
		
		// tell what we've found
  	  	mLogMgr.info( "Found a new logset: '" + 
  	  		logsetElement.getAttributeValue("name") + "'" );
  	  	
  	  	// creates a new descriptor
		LogsetDescriptor descriptor = new LogsetDescriptor();
		
		// is there any log file to take into account ?
		List logElements = logsetElement.getChildren( "log" );
		if ( logElements != null && !logElements.isEmpty() ) {
			descriptor.Logs = handleLogElements( logElements );
		}
		
		// finally return the descriptor
		return descriptor;
	}
	
	
	/**
	 * Get a list of <log> descriptors and invoked for each the parsing method.
	 * 
	 * @param logElements the list of <log> elements 
	 * @return a map of log descriptors indexed by name (ID)
	 */
	private HashMap<String,LogDescriptor> handleLogElements( 
			List logElements ) throws ConfigurationParsingError {
		
		// create a new table of descriptors
		HashMap<String,LogDescriptor> logDescriptorTable = 
			new HashMap<String,LogDescriptor>();
		
		// iterate through every node
		Iterator logIterator = logElements.iterator();
		while( logIterator.hasNext() ) {
			
			// get the next <log> element
			Element logElement = (Element)logIterator.next();
			
			// get the next log descriptor
			LogDescriptor logDescriptor = handleLogElement( logElement );
			
			// if the descriptor exists, then add it if not already present
			if ( logDescriptor != null ) {
				if ( !logDescriptorTable.containsKey(logDescriptor.ID) ) {
					logDescriptorTable.put( logDescriptor.ID, logDescriptor );
				} else {
					mLogMgr.warning( "Log file '" + logDescriptor.ID + 
							"' already exists! Preserving existing one !" );
				}
			}
		}
		
		// return the table of descriptors
		return logDescriptorTable;
	}
	
	
	/**
	 * Parses a single <log> element. This method gets the defined properties
	 * of a log file and puts it together in a single descriptor. The properties
	 * which can be defined for a log file are:
	 * <ul>
	 * <li><em>debug-level</em> for minimum level which will be outputted</li>
	 * <li><em>file</em> for the name of the file to produce </li>
	 * <li><em>format</em> for the format of the file (xml,rtf,raw,custom)</li>
	 * <li><em>cache-size</em> for the number of entries before flushing</li>
	 * <li><em>max-entries</em> for the max. number of entries</li>
	 * <li><em>stdout-dup</em> for duplicating entry to stdout</li> 
	 * </ul>
	 * 
	 * @param logElement the <log> node element 
	 * @return a descriptor specifying log file options
	 */
	private LogDescriptor handleLogElement( Element logElement )
		throws ConfigurationParsingError {
		
		// creates a log descriptor
		LogDescriptor logDescriptor = new LogDescriptor();
		
		// gets the properties
		Properties logProperties = handlePropertysetElement( logElement );
		
		// gets the id attribute
		Attribute nameAttribute = logElement.getAttribute( "name" );
		if ( nameAttribute == null || nameAttribute.getValue().length()==0 )
			throw new ConfigurationParsingError( "The name attribute is " +
					"mandatory for log elements." );
		
		// store the name of the log file
		logDescriptor.ID = nameAttribute.getValue();
		
		// indicate we found a new log file
		mLogMgr.info( "Found a new log file: '" + logDescriptor.ID + "'" );
		
		// store the value of the debug level (default: debug)
		logDescriptor.DebugLevel =
			Convert.toString( logProperties.getProperty("debug-level"), 
					"debug" );
		
		// store the name of the file (default: name)
		logDescriptor.File = 
			Convert.toString( logProperties.getProperty( "file" ), 
					logDescriptor.ID );
		
		// store the format to use (default:raw)
		logDescriptor.Format = 
			Convert.toString( logProperties.getProperty("format"), "raw" );
		
		// store the class of the custom format
		logDescriptor.FormatClass = logProperties.getProperty("class");
		if ( logDescriptor.Format.equals("custom") && 
			 logDescriptor.FormatClass == null )
			throw new ConfigurationParsingError( "The class property is " +
				"mandatory for custom log classes." );
		
		// get the size of the cache (default:1)
		logDescriptor.CacheSize =
			Convert.toInt( logProperties.getProperty("cache-size"), 1 );
		
		// get the maximum number of entries (default:infinity)
		logDescriptor.MaxEntries = 
			Convert.toInt( logProperties.getProperty("max-entries"), -1 );
		
		// should log file also output to stdout ?
		logDescriptor.ToStdout = 
			Convert.toBoolean( logProperties.getProperty("stdout-dup"), false );
		
		// returns the descriptor
		return logDescriptor;
	}
	
	
	/**
	 * Parses a complete <taskset>. Tasksets have options determining the way
	 * the platform will generate tasks.
	 * 
	 * @param configurationElement the parent <configuration> element
	 * @return a descriptor of the complete taskset
	 */
	private TasksetDescriptor handleTaskset( Element configurationElement )
		throws ConfigurationParsingError {

		// retrieve the first logset element
		Element tasksetElement = getFirstElement( configurationElement, 
				 								  "taskset", true );
		
		// tries to load the logset externally
		tasksetElement = loadXMLExternal( tasksetElement );
		
		// asserts that the taskset element still exists
		assert( tasksetElement != null );
		
		// tell what we've found
  	  	mLogMgr.info( "Found a new taskset: '" + 
  	  		tasksetElement.getAttributeValue("name") + "'" );

  	  	// creates a new taskset descriptor
		TasksetDescriptor descriptor = new TasksetDescriptor();
				
		// retrieves the task generator
		descriptor.TaskGeneratorDescriptor = 
			handleTaskgenElement( tasksetElement );
		
		// returns the taskset
		return descriptor;
	}
	
	
	/**
	 * Parses a task generator element. Task generators control and manage the
	 * flow of tasks. They rely on the task distributions to generate one task
	 * after another and they organized many tasks into a single batch. These
	 * quantities are controlled by the user in the configuration file.
	 * 
	 * @param tasksetElement the parent <taskset> element node
	 * @return a descriptor for a task generator
	 */
	private TaskgenDescriptor handleTaskgenElement( Element tasksetElement )
		throws ConfigurationParsingError {
		
		// retrieves the first on
		Element generatorElement = getFirstElement( tasksetElement, 
				 								  "generator", true );
		
		// handles the generator node and returns the result
		TaskgenDescriptor descriptor = 
			handleGeneratorElement( generatorElement );
		
		// returns the newly created descriptor
		return descriptor;
	}
	
	
	/**
	 * Parses a task generator element. Task generators control and manage the
	 * flow of tasks. They rely on the task distributions to generate one task
	 * after another and they organized many tasks into a single batch. These
	 * quantities are controlled by the user in the configuration file.
	 * 
	 * @param generatorElement the <generator> element node
	 * @return a descriptor for a task generator
	 */
	private TaskgenDescriptor 
		handleGeneratorElement( Element generatorElement ) 
			throws ConfigurationParsingError {
		
		// creates the generator node
		TaskgenDescriptor descriptor = new TaskgenDescriptor();
	
		// handle the property set for this parent node
		descriptor.Props = handlePropertyset( generatorElement, true );
		
		// tries to find the correct configuration
		if ( descriptor.Props.getProperty("distribution") == null )
			throw new ConfigurationParsingError( "No task distribution was " +
					"specified for the current task generator !" );
		
		// then, we search for every distribution set
		List distributionElements = 
			generatorElement.getChildren( "distribution" );
		
		// if there is at least one distribution...
		if ( distributionElements != null && distributionElements.size()>0 ) {
			
			// get a table with all distributions which were found
			Hashtable<String, TaskDistributionDescriptor> distributionTable = 
				handleDistributionElements( distributionElements );
		
			// tries to find the correct distribution
			String distributionName = 
				descriptor.Props.getProperty( "distribution" );
			descriptor.Distribution = distributionTable.get( distributionName );
			if ( descriptor.Distribution == null )
				throw new ConfigurationParsingError( "The task distribution " +
						"'" + distributionName + "' does not exist !" );
		} else {
			throw new ConfigurationParsingError( "No task distribution " +
				"could be found for current task generator!" );
		}
		
		// returns the generator descriptor
		return descriptor; 
	}
	
	
	/**
	 * Get a table of descriptors for the declared task distributions. Each 
	 * distribution has options to specify how tasks should be generated.
	 * 
	 * @param distributionElements a list of <distribution> element nodes
	 * @return a table holding descriptors for the distributions indexed by name
	 */
	private Hashtable<String, TaskDistributionDescriptor> 
		handleDistributionElements( List distributionElements ) 
			throws ConfigurationParsingError {
		
		// creates an array list of task distributions
		Hashtable<String, TaskDistributionDescriptor> distributionTable = 
			new Hashtable<String, TaskDistributionDescriptor>();

		// loads every node out there...
		Iterator distributionIterator = distributionElements.iterator();
		
		// for every distribution, loads it into the table
		while( distributionIterator.hasNext() ) {
			
			// get the next element from the list
			Element distributionElement= (Element)distributionIterator.next();
			
			// get the distribution descriptor from the corresponding element
			TaskDistributionDescriptor descriptor = 
				handleDistributionElement( distributionElement);
			
			// asserts the descriptor exists
			assert( descriptor != null );
			
			// if the descriptor exists, then put it in the table
			distributionTable.put( descriptor.Name, descriptor );
		}
		
		// return the array list of task distributions
		return distributionTable;
	}
	
	
	/**
	 * Parses a single <distribution> element. A distribution is in fact a
	 * collection of <property> elements which specify internal attribute
	 * values for this distribution. Those elements are dependant on the
	 * type of distribution. For example, discrete distributions will have
	 * tasks specified, but probabilitistic distributions will have not.
	 * 
	 * @param distributionElement the <distribution> element node to parse
	 * @return a descriptor for a single task distribution
	 */
	private TaskDistributionDescriptor 
		handleDistributionElement( Element distributionElement )
			throws ConfigurationParsingError {
		
		// creates a new task distribution descriptor
		TaskDistributionDescriptor descriptor = 
			new TaskDistributionDescriptor();
		
		// retrieve the attributes
		Attribute nameAttribute = distributionElement.getAttribute( "name" );
		Attribute typeAttribute = distributionElement.getAttribute( "type" );
		
		// the 'name' attribute is mandatory
		if ( nameAttribute == null )
			throw new ConfigurationParsingError( "The 'name' attribute is " +
					"mandatory for <distribution> elements" );

		// the 'type' attribute is mandatory
		if ( typeAttribute == null )
			throw new ConfigurationParsingError( "The 'type' attribute is " +
				"mandatory for <distribution> elements" );

		// save the name of the distribution
		descriptor.Name = nameAttribute.getValue();
		descriptor.Type = typeAttribute.getValue();
		
		// handles properties for the dist
		descriptor.Props = handlePropertyset( distributionElement, true );
		
		// if type is discrete
		if ( typeAttribute.getValue().equals("discrete") ) {
			Element tasksElement = distributionElement.getChild( "tasks" );
			if ( tasksElement != null ) {
				List taskElements = tasksElement.getChildren( "task" );
				descriptor.TaskDescriptorList = 
					handleTaskElements( taskElements );
			}
		}
		
		// handles the probabilities section
		List probabilitiesElements = 
				distributionElement.getChildren( "probabilities" );
		
		// how many probabilities element is there ?
		if ( probabilitiesElements != null && 
			 probabilitiesElements.size() > 0 ) {
			
			if ( probabilitiesElements.size() > 1 ) {
				mLogMgr.warning( "More than one <probabilities> element " +
						"was found ! System will only consider the first!" );
			}
			
			// get the probabilities element
			Element probabilitiesElement = 
				(Element)probabilitiesElements.get(0);
			
			// asserts this element exists
			assert( probabilitiesElement != null );
			
			// use the probabilities element
			List probabilityElements = 
				probabilitiesElement.getChildren( "probability" );
			
			// does the probability elements exist ?
			if ( probabilityElements != null && probabilityElements.size()>0 ) {
				descriptor.ProbDescriptorList = 
					handleProbabilityElements( probabilityElements );
			}
		}
		
		
		// get the density-function elements
		Element densityFunctionsElement = 
			distributionElement.getChild( "density-functions" );
		
		// does the element exist? if yes the parse it
		if ( densityFunctionsElement != null ) {
			
			// get every <function> elements
			List functionElements = 
				densityFunctionsElement.getChildren( "function" );
			
			// is there any element in the list ?
			if ( functionElements != null && functionElements.size() > 0 ) {
				descriptor.DensityFunctions = 
					handleDensityFunctionElements( functionElements );
			}
		}
		
		// returns the descriptor
		return descriptor;
	}
	
	
	/**
	 * Parses a list of <task> elements. Each task must specify the pickup
	 * and delivery city names, along width the reward per km and the weight
	 * of that task.
	 * 
	 * @param taskElements a list of <task> element nodes
	 * @return a list of task descriptors
	 */
	private ArrayList<TaskDescriptor> handleTaskElements( List taskElements )
		throws ConfigurationParsingError {
		
		// the list of task descriptors
		ArrayList<TaskDescriptor> descriptorList = 
			new ArrayList<TaskDescriptor>();
		
		// loads every task node out of here..
		Iterator taskIterator = taskElements.iterator();
		
		// loop for every task in the set
		while( taskIterator.hasNext() ) {
			
			// get the next task element 
			Element taskElement = (Element)taskIterator.next();
			
			// get the descriptor
			TaskDescriptor descriptor = handleTaskElement( taskElement );
			
			// asserts the descriptor exists
			assert( descriptor != null );
			
			// add the descriptor if not null
			descriptorList.add( descriptor );
		}
		
		// return the descriptor
		return descriptorList;
	}
	
	
	/**
	 * Pases a single <task> element.
	 * 
	 * @param taskElement the <task> element node to parse
	 * @return a task descriptor for current element
	 */
	private TaskDescriptor handleTaskElement( Element taskElement )
		throws ConfigurationParsingError {
		
		// the descriptor
		TaskDescriptor descriptor = new TaskDescriptor();

		// tries to parse the task
		try {
		
			// does the node element exist ?
			if ( taskElement == null ) {
				throw new ConfigurationParsingError( "A task node could " +
						"not be found." );
			}
			
			// retrieves the reward of the task
			if ( taskElement.getAttribute("reward") != null )
				descriptor.RewardPerKm = 
					taskElement.getAttribute("reward").getDoubleValue(); 

			// retrieves the weight of the task
			if ( taskElement.getAttribute("weight") != null )
				descriptor.Weight = 
					taskElement.getAttribute("weight").getDoubleValue();
		
			// retrieves the name of the pickup city
			if ( taskElement.getAttribute("pickup") != null )
				descriptor.PickupCity = 
					taskElement.getAttributeValue("pickup");

			// retrieves the name of the delivery city
			if ( taskElement.getAttribute("delivery") != null )
				descriptor.DeliveryCity = 
					taskElement.getAttributeValue("delivery");
			
		} catch( Exception e ) {
			mLogMgr.warning( "One of the tasks could not be loaded !" );
			mLogMgr.debug( 
					"Reward: " + taskElement.getAttribute("reward") + "\n" +
					"Weight: " + taskElement.getAttribute("weight") + "\n" + 
					"Pickup: " + taskElement.getAttribute("pickup") + "\n" + 
					"Delivery: " + taskElement.getAttribute("delivery") + "\n" 
			);
			throw new ConfigurationParsingError( "Last task could not be " +
					"loaded! One of the values may be wrong !");
		}
		
		// logs the event
		mLogMgr.info( "Found a new task from '" + descriptor.PickupCity + 
				"' to '" + descriptor.DeliveryCity + "'" );
		
		// return the descriptor
		return descriptor;
	}
	
	
	/**
	 * A list of <probability> elements. Probabilitistic task distributions
	 * create a ProbabilityDistribution object which specify for every pair
	 * of cities the probability that there is a task in the first city leading
	 * to the second. The RLA algorithm uses this information to build an
	 * internal state representation of the world in order to choose the next
	 * path to take.
	 * 
	 * @param probabilityElements a list of <probability> elements
	 * @return an array of probability descriptors
	 */
	private ArrayList<ProbabilityDescriptor> 
		handleProbabilityElements( List probabilityElements )
			throws ConfigurationParsingError {
		
		// creates a new array of probability descriptors 
		ArrayList<ProbabilityDescriptor> descriptorList = 
			new ArrayList<ProbabilityDescriptor>();
		
		// get the descriptor of the probability list
		Iterator probabilityIterator = probabilityElements.iterator();
		
		// while there are more probabilities in the list
		while( probabilityIterator.hasNext() ) {
			
			// get the next <probability> element
			Element probabilityElement = (Element)probabilityIterator.next();
			
			// get the descriptor of this node
			ProbabilityDescriptor descriptor = 
				handleProbabilityElement( probabilityElement );
			
			// asserts the descriptor exists
			assert( descriptor != null );
			
			// add it to the list
			descriptorList.add( descriptor );
		}
		
		// return the descriptor
		return descriptorList;
	}
	
	
	/**
	 * Parses a single <probability> element. 
	 * 
	 * @param probabilityElement a <probability> element node
	 * @return a probability descriptor
	 */
	private ProbabilityDescriptor 
		handleProbabilityElement( Element probabilityElement )
			throws ConfigurationParsingError {
		
		// creates a new probability descriptor
		ProbabilityDescriptor descriptor = new ProbabilityDescriptor();
		
		// loads every attribute out there
		Attribute fromAttribute = probabilityElement.getAttribute( "from" );
		Attribute toAttribute = probabilityElement.getAttribute( "to" );
		Attribute taskAttribute = probabilityElement.getAttribute( "task" );
		Attribute rewardAttribute = probabilityElement.getAttribute( "reward" );
		
		// 'from' attribute is mandatory
		if ( fromAttribute == null )
			throw new ConfigurationParsingError( "The <probability> node must" +
					" define the 'from' attribute !" );
		
		// 'to' attribute is mandatory
		if ( toAttribute == null )
			throw new ConfigurationParsingError( "The <probability> node must" +
					" define the 'to' attribute !" );
		
		// build descriptor
		descriptor.From = fromAttribute.getValue();
		descriptor.To = toAttribute.getValue();
		
		// tries performing some changes
		try { 
			descriptor.Task =  ( taskAttribute != null ) ? 
								 taskAttribute.getDoubleValue() : 
								 null;
			descriptor.Reward =  ( rewardAttribute != null ) ? 
					 			   rewardAttribute.getDoubleValue() : 
					 			   null;
					 			   
		} catch( DataConversionException e ) {
			throw new ConfigurationParsingError(
				"Either the task probability or reward for probability pair " +
				"going from '" + fromAttribute.getValue() + "' to '" + 
				toAttribute.getValue() + "' has a bad format !"
			);
		}
		
		// returns the probability descriptor
		return descriptor;
	}
	
	
	/**
	 * Return a table of density function descriptors indexed by name. Density
	 * functions determine how the probabilities are generated according to 
	 * an internal probability distribution.
	 * 
	 * @param functionElements a list of <function> nodes.
	 * @return a table of density function descriptors
	 */
	private HashMap<String,Properties> 
		handleDensityFunctionElements( List functionElements ) 
			throws ConfigurationParsingError {
		
		// creates the density function
		HashMap<String,Properties> functionTable = 
			new HashMap<String,Properties>();

		// loads every node out there...
		Iterator functionIterator = functionElements.iterator();
		
		// while more functions are found
		while( functionIterator.hasNext() ) {
			
			// get element from the iterator
			Element functionElement = (Element)functionIterator.next();
			
			// handle the element node
			Properties functionProperties = 
				handleDensityFunctionElement( functionElement );
			
			// add it to the table, if it exists
			if ( functionProperties != null ) {
				functionTable.put(
				   functionProperties.getProperty("name"), functionProperties );
			}
		}
		
		// return the list of property objects
		return functionTable;
	}

	
	/**
	 * 
	 * @param functionElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private Properties handleDensityFunctionElement( Element functionElement ) 
		throws ConfigurationParsingError {
		
		// creates the density function
		Properties properties = handlePropertysetElement( functionElement );

		// no property set ?
		if ( properties == null || properties.size() == 0 )
			throw new ConfigurationParsingError( "Density function node" +
					" does not define any property !" );
		
		// the name attribute is mandatory
		Attribute nameAttribute = functionElement.getAttribute( "name" );
		if ( nameAttribute == null )
			throw new ConfigurationParsingError( "Density function node " +
					"must have a name attribute!" );
		
		// the property set must have a type
		Attribute typeAttribute = functionElement.getAttribute( "type" );
		if ( typeAttribute == null ) {
			String typeAttr = properties.getProperty( "type" );
			if ( typeAttr == null ) {
				throw new ConfigurationParsingError( "Density function '" + 
						nameAttribute.getValue() + "' must have " +
								"a type attribute!" );
			}
		} else {
			properties.setProperty( "type", typeAttribute.getValue() );
		}
		
		// because the name attribute is present
		properties.setProperty( "name", nameAttribute.getValue() );
		
		// return the properties
		return properties;
	}
	
	
	/**
	 * 
	 * @param configurationElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private TopologyDescriptor handleToposet( Element configurationElement )
		throws ConfigurationParsingError {
		
		// retrieve the first toposet element
		Element toposetElement = getFirstElement( configurationElement, 
				  								  "toposet", true );
		
		// tries to load the toposet externally
		toposetElement = loadXMLExternal( toposetElement );
		
		// asserts that the toposet element still exists
		assert( toposetElement != null );
		
		// tell what we've found
  	  	mLogMgr.info( "Found a new toposet: '" + 
  	  		toposetElement.getAttributeValue("name") + "'" );
		
  	  	// create a new descriptor
  	  	TopologyDescriptor descriptor = new TopologyDescriptor();
  	  
		// handle the properties
  	  	descriptor.Props = handlePropertyset( toposetElement, false );
		
		// handle city information
		descriptor.Cities = handleCityset( toposetElement );
		
		// handles route information
  	  	descriptor.Routes = handleRouteset( toposetElement );
		
		// returns the newly created descriptor
		return descriptor;
	}
	
	
	/**
	 * 
	 * @param toposetElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private ArrayList<CityDescriptor> handleCityset( Element toposetElement )
		throws ConfigurationParsingError {
		
		// retrieve the first cities element
		Element citiesElement = getFirstElement( toposetElement, 
				  								 "cities", true );
		
		// constructs an array of descriptors
		ArrayList<CityDescriptor> descriptorList = 
			new ArrayList<CityDescriptor>();
		
		// loads the list of city elements
		List cityElements = citiesElement.getChildren( "city" );
		
		// finds all routes
		Iterator cityIterator = cityElements.iterator();
		while( cityIterator.hasNext() ) {
			
			// retrieves the current element
			Element cityElement = (Element)cityIterator.next();
			
			// retreives the current descriptor
			CityDescriptor descriptor = handleCityElement( cityElement );
			
			// asserts the descriptor exists
			assert( descriptor != null );

			// adds the descriptor
			mLogMgr.info( "New city found: '" + descriptor.Name + "'" );
			descriptorList.add( descriptor );
		}
		
		// returns the previously created descriptor
		return descriptorList;
	}
	

	/**
	 * 
	 * @param toposetElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private ArrayList<RouteDescriptor> handleRouteset( Element toposetElement )
		throws ConfigurationParsingError  {
		
		// retrieve the first routes element
		Element routesElement = getFirstElement( toposetElement, 
												 "routes", true );
		
		// constructs an array of route descriptors
		ArrayList<RouteDescriptor> descriptorList = 
			new ArrayList<RouteDescriptor>();
		
		// loads the list of route elements
		List routeElements = routesElement.getChildren( "route" );

		// finds all routes
		Iterator routeIterator = routeElements.iterator();
		
		// iterates through all route elements
		while( routeIterator.hasNext() ) {
			
			// retrieves the current element
			Element routeElement = (Element)routeIterator.next();
			
			// retreives the current descriptor
			RouteDescriptor descriptor = handleRouteElement( routeElement );
			
			// adds the descriptor to the list of descriptors
			if ( descriptor != null ) {
				mLogMgr.info( "Found new route from '" +  descriptor.Source + 
						"' to '" + descriptor.Destination + "'" );
				descriptorList.add( descriptor );
			}
		}
		
		// returns the previously created descriptor
		return descriptorList;
	}
	
	
	/**
	 * 
	 * @param citiesElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private CityDescriptor handleCityElement( Element citiesElement )
		throws ConfigurationParsingError 
	{	
		
		// creates a brand new descriptor
		CityDescriptor descriptor = new CityDescriptor();

		// retrieves the attribute list
		Attribute nameAttribute = citiesElement.getAttribute( "name" );
		Attribute xAttribute = citiesElement.getAttribute( "x" );
		Attribute yAttribute = citiesElement.getAttribute( "y" );
		
		// if a single route is missing
		if ( nameAttribute==null || nameAttribute.getValue().length()==0 ||
			 xAttribute==null || xAttribute.getValue().length()==0 ||
			 yAttribute==null || yAttribute.getValue().length()==0 ) {
			throw new ConfigurationParsingError( "All attributes of a " +
					"city element are mandatory." );
		}

		// stores the results
		descriptor.Name = nameAttribute.getValue();
		
		try {
			descriptor.X = xAttribute.getIntValue();
		} catch( Exception e ) {
			throw new ConfigurationParsingError( "The X position of city " +
					descriptor.Name + " has a bad format !" );
		} 
		
		try {
			descriptor.Y = yAttribute.getIntValue();
		} catch( Exception e )  {
			throw new ConfigurationParsingError( "The Y position of city " +
					descriptor.Name + " has a bad format !" );
		}
		
		// returns the descriptor
		return descriptor;
	}
	
	
	/**
	 * 
	 * @param routeElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private RouteDescriptor handleRouteElement( Element routeElement )
		throws ConfigurationParsingError 
	{	
		// creates a brand new descriptor
		RouteDescriptor descriptor = new RouteDescriptor();
	
		// retrieves the attribute list
		Attribute distanceAttribute = routeElement.getAttribute( "distance" );
		Attribute fromAttribute = routeElement.getAttribute( "from" );
		Attribute toAttribute = routeElement.getAttribute( "to" );
		
		// if a single route is missing
		if ( distanceAttribute==null || distanceAttribute.getValue().length()==0 ||
			 fromAttribute==null || fromAttribute.getValue().length()==0 ||
			 toAttribute==null || toAttribute.getValue().length()==0 ) {
			throw new ConfigurationParsingError( "All attributes of a route " +
					"are mandatory." );
		}

		// stores the results
		descriptor.Source = fromAttribute.getValue();
		descriptor.Destination = toAttribute.getValue();
		
		try {
			descriptor.Distance = distanceAttribute.getDoubleValue();
		} catch( Exception e ) {
			throw new ConfigurationParsingError( "The distance attribute for " +
					"route going from '" + descriptor.Source + "' to '" +
					descriptor.Destination + "' has a bad format !" );
		}
		
		// returns the newly created descriptor
		return descriptor;
	}
		
	
	/**
	 * 
	 * @param configurationElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private AgentsetDescriptor handleAgentset( Element configurationElement )
		throws ConfigurationParsingError {
		
		// creates a new log descriptor
		AgentsetDescriptor descriptor = new AgentsetDescriptor();

		// retrieves only the first logset element
		Element agentsetElement = getFirstElement( configurationElement, 
													"agentset", true );
		
		// tries to load from an external file
		agentsetElement = loadXMLExternal( agentsetElement );
		
		// store the name of the agent set
		String agentsetName = agentsetElement.getAttributeValue( "name" );
			
		// posts the event here
  	  	mLogMgr.info( "Found a new agentset: '" + agentsetName + "'" );
  	  	
		// handles the properties for this object
  	  	descriptor.Props = handlePropertyset( agentsetElement, false );
			
		// now, searches for the first agent
		List rootAgentElements = agentsetElement.getChildren( "agent" );
		
		// if no element is found, then throw an error
		if ( rootAgentElements==null || rootAgentElements.size() == 0 ) 
			throw new ConfigurationParsingError( "No root agent was found " +
					"for agent set '" + agentsetName + "'." );
		
		// if more than one is present, warn the user
		if ( rootAgentElements.size()> 1 ) {
			mLogMgr.warning( "More than one root agent was found for current " +
					"agent set. System will only consider the first one." );
		}
		
		// get the root element
		Element agentElement = (Element)rootAgentElements.get( 0 );
		
		// get the descriptor for the root agent
		descriptor.RootAgent = handleAgentElement( agentElement );
		
		// returns the newly created descriptor
		return descriptor;
	}
	
	
	/**
	 * 
	 * @param agentElements
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private ArrayList<AgentDescriptor> 
		handleAgentElements( List agentElements )
			throws ConfigurationParsingError {

		// creates a new array of probability descriptors 
		ArrayList<AgentDescriptor> descriptorList = 
			new ArrayList<AgentDescriptor>();
		
		// loads every node out there...
		Iterator agentIterator = agentElements.iterator();
		
		// while there are agents
		while( agentIterator.hasNext() ) {
			
			// get the current agent element
			Element agentElement = (Element)agentIterator.next();
			
			// get the descriptor from the element
			AgentDescriptor descriptor = handleAgentElement( agentElement );
			
			// asserts the descriptor exists
			assert( descriptor != null );
			
			// add the descriptor to the list
			descriptorList.add( descriptor );
		}
		
		// return the descriptor
		return descriptorList;
	}
	
	
	/**
	 * 
	 * @param agentElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private AgentDescriptor handleAgentElement( Element agentElement )
		throws ConfigurationParsingError {
		
		// gets the name attribute
		Attribute nameAttribute = agentElement.getAttribute( "name" );
		
		// the name attribute is mandatory
		if ( nameAttribute == null )
			throw new ConfigurationParsingError( "The name attribute of " +
					"the agent element is mandatory !" );
		
		// finds the name attribute
		String name = nameAttribute.getValue();
		
		// logs the event
		mLogMgr.info( "Found a new agent named '" + name + "' !" );
		
		// retrieve both elements
		Element definitionElement = getFirstElement( agentElement, "definition",
													 true ); 
		// creates the agent descriptor
		AgentDescriptor descriptor = 
			handleAgentDefinitionElement( definitionElement, name );
		
		// stores the name
		descriptor.Name = name;
		
		// get the children element (this element is not mandatory)
		Element childrenElement = getFirstElement( agentElement, "children",
				 								   false ); 
		
		// do we have children ?
		if ( childrenElement == null ) {
			mLogMgr.info( "Agent '" + descriptor.Name + "' has no children !" );
		} else {
			
			// seek for children elements
			List agentElements = childrenElement.getChildren( "agent" );
			
			// is there any children here ?
			if ( agentElements != null && agentElements.size()>0 ) {
				descriptor.Children = handleAgentElements( agentElements );
			} else {
				mLogMgr.info( "Agent '" + descriptor.Name + 
						"' has no children !" );
			}
		}

		// returns the descriptor
		return descriptor;
	}
	

	/**
	 * 
	 * @param definitionElement
	 * @param name
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private AgentDescriptor 
		handleAgentDefinitionElement( Element definitionElement, String name )
			throws ConfigurationParsingError {
		
		// creates the agent descriptor
		AgentDescriptor descriptor = new AgentDescriptor();
		
		// finds the property map
		Properties properties = handlePropertyset( definitionElement, true );
		
		// retrievs the properties of the agent
		descriptor.Speed = 
			Convert.toDouble( properties.getProperty("speed"), 
					descriptor.Speed );
		descriptor.Capacity = 
			Convert.toDouble( properties.getProperty("capacity"), 
					descriptor.Capacity );
		descriptor.CostPerKM = 
			Convert.toDouble( properties.getProperty("costperkm"), 
					descriptor.CostPerKM );
		descriptor.AgentColor = 
			Convert.toColor( properties.getProperty("color"), 
					descriptor.AgentColor );
		descriptor.Home = 
			Convert.toString( properties.getProperty("home"), descriptor.Home );
		descriptor.Active = 
			Convert.toBoolean( properties.getProperty("active"), descriptor.Active );
		
		// displays information about agent
		mLogMgr.debug( "Agent '" + name + "' properties:" );
		mLogMgr.debug( "   --> speed: " + descriptor.Speed );
		mLogMgr.debug( "   --> capacity: " + descriptor.Capacity );
		mLogMgr.debug( "   --> costperkm: " + descriptor.CostPerKM );
		mLogMgr.debug( "   --> color: " + descriptor.AgentColor );
		mLogMgr.debug( "   --> home: " + descriptor.Home );	
		
		// the type must be present
		descriptor.Type = properties.getProperty( "type" );
		if ( descriptor.Type == null ) {
			throw new ConfigurationParsingError( "A type property must be " +
					"defined for agent '" + name + "'!" );
		}
		
		// the class must be present for custom agnet
		descriptor.ClassName = properties.getProperty( "class" );
		if ( descriptor.ClassName==null && descriptor.Type.equals("custom") ) {
			throw new ConfigurationParsingError( "A class property must be " +
					"defined for the custom agent '" + name + "'!" );
		}

		// gets the behavior property
		descriptor.Behavior = properties.getProperty( "behavior" );
		if ( descriptor.Behavior==null ) {
			throw new ConfigurationParsingError( "No behavior was defined " +
					"for agent '" + name + "' !" );
		}
		
		// handle the behavior node
		Element behaviorsetElement = 
				definitionElement.getChild( "behaviorset" );
		
		// there should be at least one behaviorset
		if ( behaviorsetElement == null )
			throw new ConfigurationParsingError( "No behaviorset was defined " +
					"for agent '" + name + "'!" );
		
		//  lists the behavior nodes
		List behaviorElements = behaviorsetElement.getChildren( "behavior" );
		if ( behaviorElements != null && behaviorElements.size()>0 ) {
			descriptor.Behaviors = handleBehaviorElements( behaviorElements );
		}
		
		// return the descriptor
		return descriptor;
	}
	
	
	/**
	 * 
	 * @param behaviorElements
	 * @return
	 */
	private ArrayList<BehaviorDescriptor> 
		handleBehaviorElements( List behaviorElements ) 
			throws ConfigurationParsingError {

		// creates a new array of probability descriptors 
		ArrayList<BehaviorDescriptor> descriptorList = 
			new ArrayList<BehaviorDescriptor>();
		
		// loads every node out there...
		Iterator behaviorIterator = behaviorElements.iterator();
		
		// while behaviors exists...
		while( behaviorIterator.hasNext() ) {
			
			// get the next behavior
			Element behaviorElement = (Element)behaviorIterator.next();
			
			// squeeze the descriptor out of the element
			BehaviorDescriptor descriptor = 
				handleBehaviorElement( behaviorElement );
			
			// asserts that the descriptor exists
			assert( descriptor != null );
			
			// add the descriptor to the list
			descriptorList.add( descriptor );
		}
		
		// return the list of descriptors
		return descriptorList;
	}
	
	
	/**
	 * 
	 * @param behaviorElement
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private BehaviorDescriptor 
		handleBehaviorElement( Element behaviorElement )
			throws ConfigurationParsingError {

		// creates a brand new descriptor
		BehaviorDescriptor descriptor = new BehaviorDescriptor();
		
		// retrieves the signal attribute
		Attribute signalAttribute = behaviorElement.getAttribute( "signal" );
		if ( signalAttribute==null ) {
			throw new ConfigurationParsingError( "The signal attribute of " +
					"the behavior is mandatory." );
		}
		
		// retrieve the handler attribute
		Attribute handlerAttribute = behaviorElement.getAttribute( "handler" );
		if ( handlerAttribute==null ) {
			throw new ConfigurationParsingError( "The handler attribute of " +
					"the behavior is mandatory." );
		}
		
		// retrieve the shared attribute
		Attribute sharedAttribute = behaviorElement.getAttribute( "shared" );
		
		// stores the value in the descriptor
		descriptor.Signal = signalAttribute.getValue();
		descriptor.Handler = handlerAttribute.getValue();
		descriptor.Shared = ( sharedAttribute==null ? 
							  "False" : sharedAttribute.getValue() );
		
		// returns the descriptor
		return descriptor;
	}
	
	
	/**
	 * 
	 * @param parent
	 * @param mandatory
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private Properties 
		handlePropertyset( Element parentElement, boolean isMandatory )
			throws ConfigurationParsingError {
		
		// retrieves the first propset element
		Element propsetElement = getFirstElement( parentElement, "propset", 
												  isMandatory );
		
		// returns the property set
		return handlePropertysetElement( propsetElement );
	}
	
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public Properties handlePropertysetElement( Element propsetElement ) 
		throws ConfigurationParsingError {
		
		// creates a new properties object
		Properties properties = new Properties();
		
		// retrieves a list of all properties
		List propertyElements = propsetElement.getChildren( "property" );
		
		// we create an iterator on the list
		Iterator propertyIterator = propertyElements.iterator();
		while( propertyIterator.hasNext() ) {
			
		      // get the next element in the list
		      Element propertyElement = (Element)propertyIterator.next();
		      
		      // retrieve the name attribute
		      Attribute nameAttribute = propertyElement.getAttribute( "name" );
		      
		      // the name attribute is mandatory
		      if ( nameAttribute==null )
		    	 throw new ConfigurationParsingError( "Missing 'name' " +
		    	 		"attribute in property." );
		      
		      // retrieves the value attribute
		      Attribute valueAttribute = propertyElement.getAttribute("value");
		      
		      // the value attribute is mandatory
		      if ( valueAttribute==null )
			    	 throw new ConfigurationParsingError( "Missing 'value' " +
			    	 		"attribute in property." );
			      
		      // does the property already exist ?
		      if ( properties.containsKey(nameAttribute.getValue()) ) {
		    	  mLogMgr.warning( "Property '" + nameAttribute.getValue() + 
		    			  "' already exists. Overwriting." );
		      }
		      
		      // sets or overwrites the value
		      properties.setProperty( nameAttribute.getValue(), 
		    		  valueAttribute.getValue() );
		}
		
		// returns the property set
		return properties;
	}


	/**
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	private Element loadXMLRootFromDisk( String filename ) 
		throws Exception {

		// loads the stream from a file
		FileInputStream stream = new FileInputStream( filename );
		
		// creates a builder
		SAXBuilder documentBuilder = new SAXBuilder();
		
		// loads the document from the builder
		Document document = documentBuilder.build( new InputSource(stream) );
		
		// return the top level element
		return document.getRootElement();
	}
	
	
	/**
	 * Tries to load an external set.
	 * @param file
	 * @param parent
	 * @param name
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private Element loadXMLExternal( Element parentElement ) 
		throws ConfigurationParsingError 
	{
		// stack variable
		Element rootElement = null;

		// retrieves the top attribute list
		Attribute nameAttribute = parentElement.getAttribute( "name" );
		Attribute fileAttribute = parentElement.getAttribute( "file" );
		
		// if no file is present, then return immediately the parent node
		if ( fileAttribute==null )
			return parentElement;
		
		// the name attribute is mandatory
		if ( nameAttribute==null || nameAttribute.getValue().length()==0 ) {
			throw new ConfigurationParsingError( "The name attribute of the " +
					"<" + parentElement.getName() + "> is mandatory !" );
		}
		
		// get the xml file name
		File xmlFile = new File( mBaseDirectory, fileAttribute.getValue() );
		
		// does this file exists ?
		if ( !xmlFile.exists() ) {
			mLogMgr.error( "The external filepath '" + xmlFile + 
					"' was not found !" );
			throw new ConfigurationParsingError( "The external configuration" +
					"file '" + fileAttribute.getValue() + "' does not exist!" );
		}
		
		// load the root element from disk
		try {
			rootElement = loadXMLRootFromDisk( xmlFile.getAbsolutePath() );
		} catch( Exception e ) {
			throw new ConfigurationParsingError( "The external " + 
					parentElement.getName() + " element could not be loaded. " +
					"XML data in this file is invalid. ( JDOM error: " + e + 
					")" );
		}

		// the names must correspond !
		if ( !rootElement.getName().equals(parentElement.getName()) ) {
			throw new ConfigurationParsingError( "No root element '" + 
					parentElement.getName() + "' was found in the external " +
					"file." );
		}

		// get the name attribute of the external element
		Attribute externalNameAttribute = rootElement.getAttribute( "name" );
			
		// the child attribute must be present
		if ( externalNameAttribute == null ) {
			throw new ConfigurationParsingError( "The external '" + 
					parentElement.getName() + "' " + "must define a " +
					"'name' attribute." );
		}
			
		// the names must match
		if ( !externalNameAttribute.getValue().equals(
				nameAttribute.getValue()) ) {
			throw new ConfigurationParsingError( "The name attribute of the " +
					"external element '" + parentElement.getName() + "' does " +
					"not match the name of the local element! "  );
		}
			
		// the external file must not define any other attribute
		if ( rootElement.getAttributes().size() > 1 ) {
			throw new ConfigurationParsingError( "The external element '" + 
					parentElement.getName() + "' *must* not define more than " +
							"one attribute !" );
		}
		
		// returns the root element found
		return rootElement;
	}
	
	
	/**
	 * 
	 * @param parentElement
	 * @param elementName
	 * @param isElementMandatory
	 * @return
	 * @throws ConfigurationParsingError
	 */
	private Element getFirstElement( Element parentElement, String elementName, 
			boolean isElementMandatory ) throws ConfigurationParsingError {

		// get the list of elements
		List elementList = parentElement.getChildren( elementName );
		
		// is the element list empty ?
		if ( elementList==null || elementList.isEmpty() ) {
			if ( isElementMandatory ) {
				throw new ConfigurationParsingError( "No " + elementName +
						" element was found !" );
			} else {
				return null;
			}
		}
	
		// if the list has more that one element, then skip the others
		if ( elementList.size() > 1 ) {
			mLogMgr.warning( "More than one <" + elementName + "> element " +
					"was found. System will only consider the first !" );
		}
		
		// retrieve the first cities element
		Element singleElement = (Element)elementList.get( 0 );
		
		// retrieves the list
		return singleElement;
	}
}
