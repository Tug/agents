package epfl.lia.logist.logging;

/* Java utility imports */
import java.util.ArrayList;
import java.util.Date;
import epfl.lia.logist.exception.AgentCreationException;
import epfl.lia.logist.logging.format.RawLogOutputFormat;
import epfl.lia.logist.logging.format.RtfLogOutputFormat;
import epfl.lia.logist.logging.format.StreamLogOutputFormat;
import epfl.lia.logist.logging.format.XmlLogOutputFormat;
import epfl.lia.logist.tools.LogistClassLoader;


/**
 * An object representing a log file.
 */
public class LogFile {
		
	
	/**
	 * The cache holding log entries
	 */
	private ArrayList<LogEntry> moCache = null;

	/**
	 * The output file and formatting class
	 */
	private LogOutputFormat moFormat = null;

	/**
	 * The lowest severity level possible
	 */
	private LogSeverityEnum meDebugLevel = null;

	/**
	 * The maximum number of entries for this log
	 */
	private int miMaxEntries = 0;

	/**
	 * The number of entries cached before writing to disk
	 */
	private int miCacheSize = 1; // write cache after each log

	/**
	 * The current number of entries in the log file (<mMaxEntries)
	 */
	private int miNumEntries = 0;

	/**
	 * Determines whether output should also be printed to standard console
	 */
	private boolean mbStdoutput = false;
	
	/**
	 * The classpath were to find classes
	 */
	private String mClassPath = "";
	
	
	/**
	 * Default contructor of the class.
	 */
	public LogFile() {
		
		// creates the deque holding cache entries
		moCache = new ArrayList<LogEntry>( miCacheSize );
	}
	
	
	/**
	 * Copy contructor of the class.
	 */
	public LogFile( String classpath ) {
		this();
		mClassPath = classpath;
	}
	
	
	/**
	 * Creates a log file from a simple descriptor
	 */
	public boolean create( LogDescriptor d ) throws Exception {
		
		// resets the number of entries to 0
		miNumEntries = 0;
		
		// resets the cache entries
		moCache.clear();
		
		// converts to lower case
		String lsFormat = d.Format.toLowerCase();
		
		// for of all, selects the output formatter class
		if ( lsFormat.equals("stream") ) {
			moFormat = new StreamLogOutputFormat( d );
		} else if ( lsFormat.equals("xml") ) {
			d.File += ".xml";
			moFormat = new XmlLogOutputFormat( d );
		} else if ( lsFormat.equals("rtf") ) {
			d.File += ".rtf";
			moFormat = new RtfLogOutputFormat( d );
		} else if ( lsFormat.equals("raw") ) {
			d.File += ".txt";
			moFormat = new RawLogOutputFormat( d );
		} else if ( lsFormat.equals("custom") ) {

			// creates the array of parameters: AgentProfile, AgentState
			Class<?>[] args = { LogDescriptor.class };
			Object[] objs = { d };
			
			// tries creating the class
			try {
				moFormat = (LogOutputFormat)LogistClassLoader.instantiateClass( 
						d.FormatClass, mClassPath, args, objs );
			} catch( Exception e ) {
				throw new AgentCreationException( "Could not create a " +
						"log format instance for log '" + d.ID + 
						"' from class " + d.FormatClass + ". Please " +
						"verify that the global classpath variable is " +
						"correctly set !" );
			}
		}

		// inits the format
		moFormat.init();

		// selects the debug level
		meDebugLevel = null;
		
		// converts the debug level
		String lsDebugLevel = d.DebugLevel.toLowerCase();
		
		// selects the appropriate level
		if ( lsDebugLevel.equals("debug") ) {
			meDebugLevel = LogSeverityEnum.LSV_DEBUG;
		} else if ( lsDebugLevel.equals("info") ) {
			meDebugLevel = LogSeverityEnum.LSV_INFO;
		} else if ( lsDebugLevel.equals("warning") ) {
			meDebugLevel = LogSeverityEnum.LSV_WARNING;
		} else if ( lsDebugLevel.equals("error") ) {
			meDebugLevel = LogSeverityEnum.LSV_ERROR;
		} else if ( lsDebugLevel.equals("fatal") ) {
			meDebugLevel = LogSeverityEnum.LSV_FATAL;
		} else {
			meDebugLevel = LogSeverityEnum.LSV_DEBUG;
		}
		
		// should we put data to output ?
		mbStdoutput = d.ToStdout;

		// how many entries at max ?
		miMaxEntries = d.MaxEntries;
		
		// what is the size of the cache ?
		miCacheSize = d.CacheSize;
		
		// Creates a complete log file from its descriptor
		return true;
	}
	
	
	/**
	 * Destroys the log file.
	 */
	public void destroy() {
		if ( moCache.size() > 0 ) {
			flush();
		}
		moCache.clear();
		moFormat.close();
	}
	
	
	/**
	 * Flushes all elements in the cache to the
	 * output.
	 */
	public synchronized void flush() {
		if ( moFormat==null ) return;
		for ( LogEntry le : moCache ) {
			moFormat.outputEntry( le );
		}
		moCache.clear();
	}
	
	
	/**
	 * Logs one event into the cache.
	 * @param ls
	 * @param msg
	 */
	public synchronized void log( LogSeverityEnum ls, String msg ) {

		// do not log anything
		if ( ls.ordinal()<meDebugLevel.ordinal() )
			return;
	
		// do not add more than allowed
		if ( miMaxEntries>0 && miNumEntries == miMaxEntries )
			return;
		
		// should log to the screen ?
		if ( this.mbStdoutput ) {
			String outmsg = "[" + (new Date()) + "] " + msg;
			if ( ls.compareTo(LogSeverityEnum.LSV_ERROR) >= 0 )
				System.err.println(outmsg);
			else
				System.out.println(outmsg);
		}
		
		// create a new anonymous entry
		moCache.add( new LogEntry(new Date(),"", msg,ls,0) );
		
		// adds a new entry
		miNumEntries++;
		
		// flushes the cache when enough elements are available
		if ( moCache.size() >= miCacheSize )
			flush();
	}

}