package epfl.lia.logist.testing.logging;

import epfl.lia.logist.logging.LogDescriptor;
import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;


/**
 * This testbed tests several types of log files.
 */
public class TestLogging1 {
	public static void main( String[] args ) {
		new TestLogging1();
	}
	
	public TestLogging1() {
		try {
			
			// creates the log manager
			LogManager lLogMgr = new LogManager();
			
			// initializes the log manager
			lLogMgr.init();
			
			// creates all log files
			createAllLogs();
	
			// prints to all log files
			printToLogs( LogSeverityEnum.LSV_DEBUG, "This is a simple debug message !" );
			printToLogs( LogSeverityEnum.LSV_INFO, "This is a simple piece of information !" );
			printToLogs( LogSeverityEnum.LSV_WARNING, "This is a warning !" );
			printToLogs( LogSeverityEnum.LSV_ERROR, "This is a serious error !" );
			printToLogs( LogSeverityEnum.LSV_FATAL, "This is a fatal error ! Must QUIT !!!" );
			
			// shuts the logs down
			lLogMgr.shutdown();
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void createAllLogs() throws Exception {
		
		// creates the TEXT log
		LogDescriptor lLog1Dsc = new LogDescriptor();
		lLog1Dsc.ID 		   = "log1";
		lLog1Dsc.File 		   = "logfile.log";
		lLog1Dsc.Format 	   = "raw";
		lLog1Dsc.FormatClass   = null;
		lLog1Dsc.DebugLevel    = "debug";
		lLog1Dsc.MaxEntries    = 0; // infinity
		lLog1Dsc.CacheSize     = 5;
		LogManager.getInstance().createLog( lLog1Dsc );
		
		// creates the RTF log
		LogDescriptor lLog2Dsc = new LogDescriptor();
		lLog2Dsc.ID 		   = "log2";
		lLog2Dsc.File 		   = "logfile.rtf";
		lLog2Dsc.Format 	   = "rtf";
		lLog2Dsc.FormatClass   = null;
		lLog2Dsc.DebugLevel    = "debug";
		lLog2Dsc.MaxEntries    = 0; // infinity
		lLog2Dsc.CacheSize     = 5;
		LogManager.getInstance().createLog( lLog2Dsc );
		
		// creates the XML log
		LogDescriptor lLog3Dsc = new LogDescriptor();
		lLog3Dsc.ID 		   = "log3";
		lLog3Dsc.File 		   = "logfile.xml";
		lLog3Dsc.Format 	   = "xml";
		lLog3Dsc.FormatClass   = null;
		lLog3Dsc.DebugLevel    = "debug";
		lLog3Dsc.MaxEntries    = 0; // infinity
		lLog3Dsc.CacheSize     = 5;
		LogManager.getInstance().createLog( lLog3Dsc );
		
		// creates the CUSTOM log
		/*
		LogDescriptor lLog4Dsc = new LogDescriptor();
		lLog4Dsc.ID 		   = "log4";
		lLog4Dsc.File 		   = "logfile.xml";
		lLog4Dsc.Format 	   = "xml";
		lLog4Dsc.FormatClass  = null;
		lLog4Dsc.DebugLevel   = "debug";
		lLog4Dsc.MaxEntries   = 0; // infinity
		lLog4Dsc.CacheSize    = 5;
		LogManager.getInstance().createLog( lLog4Dsc ); */
	}
	
	/**
	 * Writes to the three log files
	 * @param e the severity of the message
	 * @param msg
	 */
	public void printToLogs( LogSeverityEnum e, String msg ) {
		LogManager.getInstance().log( "log1", e, msg );
		LogManager.getInstance().log( "log2", e, msg );
		LogManager.getInstance().log( "log3", e, msg );
		LogManager.getInstance().log( "stdout", e, msg );
	}
}
