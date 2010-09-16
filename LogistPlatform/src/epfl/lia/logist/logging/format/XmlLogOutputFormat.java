package epfl.lia.logist.logging.format;

/* importation table */
import java.io.PrintStream;
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
public class XmlLogOutputFormat implements LogOutputFormat {
	
	/** An instance of a print stream for writing some text out */
	private PrintStream moFileStream = null;
	
	/** Keeps a reference to the descriptor */
	private LogDescriptor moDescriptor = null;
	
	/** Keeps a count on the numbber of outputted entries */
	private int miCount = 0;
	
	
	/**
	 * Default public constructor
	 */
	public XmlLogOutputFormat( LogDescriptor d ) throws Exception {
		
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
		
		// information about the entry
		String lsSeverity = "<entry-severity>" + e.getSeverity() + "</entry-severity>";
		String lsModule = "<entry-module>" + e.getModule() + "</entry-module>";
		String lsLine = "<entry-line>" +  e.getLine() + "</entry-line>";
		String lsTime = "<entry-time>" + String.format( "%1$tH:%1$tM:%1$tS", new GregorianCalendar() ) + "</entry-time>";
		String lsMsg = "<entry-message>" + e.getText() + "</entry-message>";
		
		moFileStream.println( "    <log-entry id=\"" + (miCount++) + "\">" );
		moFileStream.println( "      " + lsSeverity );
		moFileStream.println( "      " + lsModule );
		moFileStream.println( "      " + lsLine );
		moFileStream.println( "      " + lsTime );
		moFileStream.println( "      " + lsMsg );
		moFileStream.println( "    </log-entry>" );
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
		
//		 display various information
		String lsStartDate    = "<start-date>" + String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", new GregorianCalendar() ) + "</start-date>";
		String lsStartTime    = "<start-time>" + String.format( "%1$tH:%1$tM:%1$tS", new GregorianCalendar() ) + "</start-time>";
		String lsJVMVendor    = "<java-vendor>" + System.getProperty( "java.vendor" ) + "</java-vendor>";
		String lsJVMVendorURL = "<java-vendor-url>" + System.getProperty( "java.vendor.url" ) + "</java-vendor-url>";
		String lsJVMVersion   = "<java-version>" + System.getProperty( "java.version" ) + "</java-version>";
		String lsOsArch       = "<os-arch>" + System.getProperty( "os.arch" ) + "</os-arch>";
		String lsOsName       = "<os-name>" + System.getProperty( "os.name" ) + "</os-name>";
		String lsUsername     = "<user-name>" + System.getProperty( "user.name" ) + "</user-name>";
		String lsCacheSize    = "<cache-size>" + moDescriptor.CacheSize + "</cache-size>";
		String lsMaxEntries   = "<max-entries>" + moDescriptor.MaxEntries + "</max-entries>"; 
		String lsDebugLevel   = "<debug-level>" + moDescriptor.DebugLevel + "</debug-level>";
		
		
		// some generic information
		moFileStream.println( "<log>" );
		moFileStream.println( "  <general-info>" );
		moFileStream.println( "    " + lsStartDate  );
		moFileStream.println( "    " + lsStartTime );
		moFileStream.println( "    " + lsJVMVendor );
		moFileStream.println( "    " + lsJVMVendorURL );
		moFileStream.println( "    " + lsJVMVersion );
		moFileStream.println( "    " + lsOsArch );
		moFileStream.println( "    " + lsOsName );
		moFileStream.println( "    " + lsUsername );
		moFileStream.println( "    " + lsCacheSize );
		moFileStream.println( "    " + lsMaxEntries );
		moFileStream.println( "    " + lsDebugLevel );
		moFileStream.println( "  </general-info>" );
		moFileStream.println();
		moFileStream.println( "  <log-entries>" );
		
	}
	
	
	/**
	 * Outputs the file footer
	 */
	private void outputFileFooter() {
		moFileStream.println( "  </log-entries>" );
		moFileStream.println( "</log>" );
	}
}