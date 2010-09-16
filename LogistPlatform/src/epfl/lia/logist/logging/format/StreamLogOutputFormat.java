package epfl.lia.logist.logging.format;

/* ... */
import java.io.PrintStream;
import java.util.Date;
import java.util.GregorianCalendar;

import epfl.lia.logist.logging.LogDescriptor;
import epfl.lia.logist.logging.LogEntry;
import epfl.lia.logist.logging.LogOutputFormat;
import epfl.lia.logist.logging.LogSeverityEnum;


/**
 * 
 * @author salves
 *
 */
public class StreamLogOutputFormat implements LogOutputFormat {
	
	/** An instance of a print stream for writing some text out */
	private PrintStream moOutStream = null;
	private PrintStream moErrStream = null;
	
	
	/**
	 * Default public constructor
	 */
	public StreamLogOutputFormat( LogDescriptor d ) throws Exception {

		// if File is "stdout", then write to output
		if ( d.File.toLowerCase().equals("stdout") ) {
			moOutStream = System.out;
			moErrStream = System.err;
		} else {
			
			// TODO: change to take into account other streams
			moOutStream = System.out;
			moErrStream = System.err;
		}
	}
	
	
	/**
	 * Outputs a formatted entry to the stream
	 * @param e
	 */
	public void outputEntry( LogEntry e ) {
		String lsDate = String.format( "%1$tH:%1$tM:%1$tS", new Date() );
		if ( e.getSeverity() == LogSeverityEnum.LSV_WARNING ||
			 e.getSeverity() == LogSeverityEnum.LSV_ERROR ||
			 e.getSeverity() == LogSeverityEnum.LSV_FATAL )
			moErrStream.printf( "[%s] %-8s: %s\n", lsDate, e.getSeverity(), e.getText() );
		else
			moOutStream.printf( "[%s] %-8s: %s\n", lsDate, e.getSeverity(), e.getText() );
	}
		
	
	/**
	 * Initiatializes the output 
	 */
	public void init() {
		outputFileHeader();
	}
	
	
	/**
	 * Closes the output stream
	 */
	public void close() {
		outputFileFooter();
	}
	
	
	/**
	 * Outputs the file header
	 */
	private void outputFileHeader() {
		/*
		// display various information
		String lsJVMVendor    = "java-vendor     : " + System.getProperty( "java.vendor" );
		String lsJVMVendorURL = "java-vendor-url : " + System.getProperty( "java.vendor.url" );
		String lsJVMVersion   = "java-version    : " + System.getProperty( "java.version" );
		String lsOsArch       = "os-arch         : " + System.getProperty( "os.arch" );
		String lsOsName       = "os-name         : " + System.getProperty( "os.name" );
		String lsUsername     = "user-name       : " + System.getProperty( "user.name" );
		String lsCacheSize    = "cache-size      : " + moDescriptor.CacheSize;
		String lsMaxEntries   = "max-entries     : " + moDescriptor.MaxEntries; 
		String lsDebugLevel   = "debug-level     : " + moDescriptor.DebugLevel;;
		
		
		// some generic information
		moOutStream.println( "Logist Platform STDOUT Log Output:" );
		moOutStream.println( "===============================\n\n" );
		moOutStream.println( "Starting date : " + String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", new GregorianCalendar() ) );
		moOutStream.println( "Starting time : " + String.format( "%1$tH:%1$tM:%1$tS", new GregorianCalendar() ) );
		moOutStream.println( "\nGeneral environnement info:\n" );
		moOutStream.println( "\t" + lsJVMVendor );
		moOutStream.println( "\t" + lsJVMVendorURL );
		moOutStream.println( "\t" + lsJVMVersion );
		moOutStream.println( "\t" + lsOsArch );
		moOutStream.println( "\t" + lsOsName );
		moOutStream.println( "\t" + lsUsername );
		moOutStream.println( "\t" + lsCacheSize );
		moOutStream.println( "\t" + lsMaxEntries );
		moOutStream.println( "\t" + lsDebugLevel + "\n\n" );
		moOutStream.println( "Log file entries:\n" );*/
	}
	
	
	/**
	 * Outputs the file footer
	 */
	private void outputFileFooter() {
		//moOutStream.println( "\n\n--| End of log |------------------------------------------------------" );
	}

}
