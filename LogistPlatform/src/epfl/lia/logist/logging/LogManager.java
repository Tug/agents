package epfl.lia.logist.logging;

import java.io.File;
import java.util.HashMap;

import epfl.lia.logist.config.Configuration;
import epfl.lia.logist.core.IService;
import epfl.lia.logist.tools.LogistGlobals;


/**
 * 
 * @author salves
 *
 */
public class LogManager implements IService {
	
		/* logs into the default log file */
		public static String DEFAULT	= "main";
		
		/* logs into the stardard stream log */
		public static String ERR		= "stdout";
		public static String OUT		= "stdout";
		
		/* A map of log files */
		protected HashMap<String,LogFile> mMapOfLogs = null;
		
		/* A static instance of ourselves */
		private static LogManager msSingleton = null;
		
		/* Tells whether the log manager was already initialized or not */
		private boolean mbIsInitialized = false ;
		
		/* The globals for the platform */
		private LogistGlobals mGlobals = null;
		
		
		/**
		 * The constructor of the class. This method is responsible
		 * for initializing the state of the class as well as the
		 * singleton instance.
		 */
		public LogManager() {
			if ( msSingleton==null )
				msSingleton = this;
			mMapOfLogs = new HashMap<String,LogFile>();
		}
		
		
		/**
		 * Creates a new log file from a log file descriptor.
		 * @param lfd a descriptor object for the log file.
		 * @return the index of the created log file.
		 */
		public void createLog( LogDescriptor lfd ) throws Exception {

			// creates a new log file
			LogFile lf = null;
			
			// creates the log file
			if ( mGlobals == null )
				lf = new LogFile();
			else
				lf = new LogFile( mGlobals.ClassPath );
			
			// if the log file already contains an entry,
			// then do not create a new log file
			if ( mMapOfLogs.containsKey(lfd.ID) )
				return;
			
			// creates the log file from a descriptor and
			// returns the log index.
			if ( lf.create( lfd ) ) {
				mMapOfLogs.put( lfd.ID, lf );
			}
		}
		
		
		/**
		 * Deletes the log file from the system.
		 * @param id
		 */
		public LogFile deleteLog( String ID ) {
			
			// retrieves the log file
			LogFile lLog = mMapOfLogs.remove(ID);
			
			// ensures that log file exists
			if ( lLog == null )
				return null;
			
			// closes this log file
			lLog.destroy();
			
			// returns the log file
			return lLog;
		}

		
		/**
		 * Returns an instance of this class.
		 * @return a singleton value
		 */
		public static synchronized LogManager getInstance() {
			return msSingleton;
		}

		
		/**
		 * Initializes the log file manager. Initializes the
		 * log map and creates three primary log files "stdout", "stderr" and
		 * "main".
		 */
		public void init() throws Exception {
			
			if ( mbIsInitialized ) return;
			
			// creates the first log
			LogDescriptor lOutLogDsc = new LogDescriptor();
			lOutLogDsc.ID 			 = "stdout";
			lOutLogDsc.File 		 = "stdout";
			lOutLogDsc.Format 		 = "stream";
			lOutLogDsc.FormatClass   = null;
			lOutLogDsc.DebugLevel	 = "debug";
			lOutLogDsc.MaxEntries    = 0; // infinity
			lOutLogDsc.CacheSize     = 1;
			lOutLogDsc.ToStdout      = false;
			createLog( lOutLogDsc );
			
			// creates the main log file
			LogDescriptor lMainLogDsc = new LogDescriptor();
			lMainLogDsc.ID 		      = "main";
			lMainLogDsc.File 		  = "logist";
			lMainLogDsc.Format 	      = "raw";
			lMainLogDsc.FormatClass   = null;
			lMainLogDsc.DebugLevel    = "debug";
			lMainLogDsc.MaxEntries    = 0; // infinity
			lMainLogDsc.CacheSize     = 5; // caches 5 entries in queue
			lMainLogDsc.ToStdout      = true;
			createLog( lMainLogDsc );
		}

		
		/**
		 * Destroys the log file manager
		 */
		public void shutdown() {
			String[] lStringArray = new String[ mMapOfLogs.keySet().size() ];
			mMapOfLogs.keySet().toArray( lStringArray );
			for( int i=0; i<lStringArray.length; i++ ) {
				deleteLog( lStringArray[i] );
			}
		}

		
		/**
		 * Sets up
		 */
		public void setup( Configuration cfg, LogistGlobals lg ) 
			throws Exception {
			
			// stores the globals
			mGlobals = lg;
			
			// is there any log to create ?
			if ( cfg.Logs.Logs == null || cfg.Logs.Logs.isEmpty() )
				return;
			
			// get the list of logs
			HashMap<String,LogDescriptor> logs = cfg.Logs.Logs;
			
			// sets the ld file to reflect a special structure
			File logDir = new File( lg.LogPath );
			
			// for every log descriptor, create the corresponding
			// log file
			for( LogDescriptor ld : logs.values() ) {
				
				// if the directory exists, then...
				if ( logDir.exists() ) {
					File logFile = new File( logDir, ld.File );
					ld.File = logFile.getAbsolutePath();
				}
				
				// creates a new log
				createLog( ld );
				
				// logs the event
				log( DEFAULT, LogSeverityEnum.LSV_INFO, 
					 "Created log file named '" + ld.ID + "' !"	);
			}
		}
		
		
		/**
		 * Resets the log manager
		 */
		public void reset( int round ) {
			for ( LogFile l : mMapOfLogs.values() )
				l.flush();
		}
		
		
		/**
		 * Logs a message to the output
		 * @param ID the identifier of the log file to use
		 * @param s the severity constant for this log
		 * @param msg the message to log
		 */
		public void log( String ID, LogSeverityEnum s, String msg ) {
			
			// retrieves the log file
			LogFile lf = mMapOfLogs.get(ID);
			
			// writes the entry
			if ( lf != null ) {
				lf.log( s, msg );
			} else { 
				System.out.println ("Log error: " + msg + "(" + 
						mMapOfLogs.size() +")"); 
			}
		}

		
		/**
		 * Flushes the requested log file. The effect of this command is
		 * to flush any pending log transaction. 
		 * @param ID
		 */
		public void flush( String ID ) {
			
			// retrieves the log file
			LogFile lf = mMapOfLogs.get(ID);
			
			// writes the entry
			if ( lf != null ) {
				lf.flush();
			}
		}
		
		
		/**
		 * The text for the service management system
		 */
		public String toString() {
			return "Log management service";
		}
		
		public void debug( String message ) {
			log( DEFAULT, LogSeverityEnum.LSV_DEBUG, message );
		}
		
		public void info( String message ) {
			log( DEFAULT, LogSeverityEnum.LSV_INFO, message );
		}
		
		public void warning( String message ) {
			log( DEFAULT, LogSeverityEnum.LSV_WARNING, message );
		}
		
		public void error( String message ) {
			log( DEFAULT, LogSeverityEnum.LSV_ERROR, message );
		}

}