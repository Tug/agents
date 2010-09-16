package epfl.lia.logist.logging.format;

/* importation table */
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
public class RtfLogOutputFormat implements LogOutputFormat {
	
	/** An instance of a print stream for writing some text out */
	private PrintStream moFileStream = null;
	
	/** Keeps a reference to the descriptor */
	private LogDescriptor moDescriptor = null;
	
	
	/**
	 * Default public constructor
	 */
	public RtfLogOutputFormat( LogDescriptor d ) throws Exception {
		
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
		moFileStream.println( "\\par \\tx2500 " );
		switch( e.getSeverity() ) {
			case LSV_WARNING:
				moFileStream.println( "\\cf2 " );
				break;
				
			case LSV_ERROR:
			case LSV_FATAL:
				moFileStream.println( "\\cf1 " );
				break;
		}
		
		String lsDate = String.format( "%1$tH:%1$tM:%1$tS", new Date() );
		moFileStream.printf( "[%s] %-8s \\tab %s \\cf0\n", lsDate, e.getSeverity(), e.getText() );
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
		String lsJVMVendor    = "\\f0 \\b java-vendor     : \\b0 " + System.getProperty( "java.vendor" );
		String lsJVMVendorURL = "\\f0 \\b java-vendor-url : \\b0 " + System.getProperty( "java.vendor.url" );
		String lsJVMVersion   = "\\f0 \\b java-version    : \\b0 " + System.getProperty( "java.version" );
		String lsOsArch       = "\\f0 \\b os-arch         : \\b0 " + System.getProperty( "os.arch" );
		String lsOsName       = "\\f0 \\b os-name         : \\b0 " + System.getProperty( "os.name" );
		String lsUsername     = "\\f0 \\b user-name       : \\b0 " + System.getProperty( "user.name" );
		String lsCacheSize    = "\\f0 \\b cache-size      : \\b0 " + moDescriptor.CacheSize;
		String lsMaxEntries   = "\\f0 \\b max-entries     : \\b0 " + moDescriptor.MaxEntries; 
		String lsDebugLevel   = "\\f0 \\b debug-level     : \\b0 " + moDescriptor.DebugLevel;
		
		
		// some generic information
		moFileStream.println( "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1036" );
		moFileStream.println( "{\\colortbl ;\\red255\\green0\\blue0;\\red255\\green128\\blue0;}" );
		moFileStream.println( "{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}{\\f1\\fmodern\\fprq1\\fcharset0 Courier New;}}" );
		moFileStream.println( "\\f0 \\fs38 \\b Logist Platform RTF Log output \\b0 \\par" );
		moFileStream.println( "\\par \\b \\fs24 Starting date : \\b0 " + String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", new GregorianCalendar() ) );
		moFileStream.println( "\\par \\b Starting time : \\b0 " + String.format( "%1$tH:%1$tM:%1$tS", new GregorianCalendar() ) );
		moFileStream.println( "\\par \\par \\fs28 \\b General environnement info: \\b0 \\par" );
		moFileStream.println( "\\par \\fs24 \\tx750 \\tab " + lsJVMVendor );
		moFileStream.println( "\\par \\tab " + lsJVMVendorURL );
		moFileStream.println( "\\par \\tab " + lsJVMVersion );
		moFileStream.println( "\\par \\tab " + lsOsArch );
		moFileStream.println( "\\par \\tab " + lsOsName );
		moFileStream.println( "\\par \\tab " + lsUsername );
		moFileStream.println( "\\par \\tab " + lsCacheSize );
		moFileStream.println( "\\par \\tab " + lsMaxEntries );
		moFileStream.println( "\\par \\tab " + lsDebugLevel );
		moFileStream.println( "\\par \\par \\fs28 \\b Log file entries: \\b0 \\fs24 \\par" );
		
		/*
		moFileStream.println( "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1036" );
		moFileStream.println( "{\\colortbl ;\\red255\\green0\\blue0;\\red255\\green128\\blue0;}" );
		moFileStream.println( "{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}{\\f1\\fmodern\\fprq1\\fcharset0 Courier New;}}" );
		moFileStream.println( "{\\f0\\fs32 Fichier de log pour LogistPlatform\\b0\\par:" );
		moFileStream.println( "\\par  \\f0\\fs20 \\b Nom\\b0: Logist Plaftorm 1.2" );
		moFileStream.println( "\\par  \\f0\\b Date:\\b0" + new Date() );
		moFileStream.println( "\\par  \\f0\\b Langue:\\b0 Français" );
		moFileStream.println( "\\par\\par\\b Entrées de la base:\\b0\\par" );*/
	}
	
	
	/**
	 * Outputs the file footer
	 */
	private void outputFileFooter() {
	
		moFileStream.println( "\\par \\par --------------------------------------------------------------------------- \\par" );
		moFileStream.println( "}" );
	}
}