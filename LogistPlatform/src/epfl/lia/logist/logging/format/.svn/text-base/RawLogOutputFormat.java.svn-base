package epfl.lia.logist.logging.format;

/* importation table */
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import epfl.lia.logist.logging.LogDescriptor;
import epfl.lia.logist.logging.LogEntry;
import epfl.lia.logist.logging.LogOutputFormat;


/**
 * 
 * @author salves
 *
 */
public class RawLogOutputFormat implements LogOutputFormat {
	
	
	/** An instance of a print stream for writing some text out */
	private PrintStream moFileStream = null;
	
	/** Keeps a reference to the descriptor */
	private LogDescriptor moDescriptor = null;
	
	
	/**
	 * Default public constructor
	 */
	public RawLogOutputFormat( LogDescriptor d ) throws Exception {
		
		// keeps a reference on the descriptor
		moDescriptor = d;
		
		// creates the output stream
		moFileStream = new PrintStream( d.File );
	}
	
	/**
	 * Outputs a formatted entry to the stream
	 * @param e
	 */
	public void outputEntry( LogEntry e ) {
		String lsDate = String.format( "%1$tH:%1$tM:%1$tS", new Date() );
		moFileStream.printf( "[%s] %-8s: %s\n", lsDate, e.getSeverity(), e.getText() );
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
	 * <xml> </xml>
	 * Outputs the file header
	 */
	private void outputFileHeader() {
		
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
		moFileStream.println( "Logist Platform RAW Log Output:" );
		moFileStream.println( "===============================\n\n" );
		moFileStream.println( "Starting date : " + String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", new GregorianCalendar() ) );
		moFileStream.println( "Starting time : " + String.format( "%1$tH:%1$tM:%1$tS", new GregorianCalendar() ) );
		moFileStream.println( "\nGeneral environnement info:\n" );
		moFileStream.println( "\t" + lsJVMVendor );
		moFileStream.println( "\t" + lsJVMVendorURL );
		moFileStream.println( "\t" + lsJVMVersion );
		moFileStream.println( "\t" + lsOsArch );
		moFileStream.println( "\t" + lsOsName );
		moFileStream.println( "\t" + lsUsername );
		moFileStream.println( "\t" + lsCacheSize );
		moFileStream.println( "\t" + lsMaxEntries );
		moFileStream.println( "\t" + lsDebugLevel + "\n\n" );
		moFileStream.println( "Log file entries:\n" );
	}
	
	
	/**
	 * Outputs the file footer
	 */
	private void outputFileFooter() {
		moFileStream.println( "\n\n--| End of log |------------------------------------------------------" );
	}
}