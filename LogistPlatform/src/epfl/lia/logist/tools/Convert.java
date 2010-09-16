package epfl.lia.logist.tools;

import java.awt.Color;

/**
 * Simple conversion utility
 */
public class Convert {
	
	/**
	 * Convert the input value to an int value.
	 * 
	 * @param obj
	 * @param def
	 * @return
	 */
	public static int toInt( String obj, int def ) {
		if ( obj == null ) return def;
		try { 
			return Integer.parseInt( obj ); 
		} catch( Exception e ) { 
			return def; 
		}
	}
	
	
	/**
	 * Convert the input value to a long value.
	 * 
	 * @param obj
	 * @param def
	 * @return
	 */
	public static long toLong( String obj, long def ) {
		if ( obj == null ) return def;
		try { 
			return Long.parseLong( obj ); 
		} catch( Exception e ) { 
			return def; 
		}
	}
	
	
	/**
	 * Convert the input value to a long value.
	 * 
	 * @param obj
	 * @param def
	 * @return
	 */
	public static double toDouble( String obj, double def ) {
		if ( obj == null ) return def;
		try { 
			return Double.parseDouble( obj ); 
		} catch( Exception e ) { 
			return def; 
		}
	}
	
	
	/**
	 * Convert the input value to a string value.
	 * 
	 * @param obj
	 * @param def
	 * @return
	 */
	public static String toString( String obj, String def ) {
		if ( obj == null ) return def;
		return obj;
	}
	
	
	/**
	 * Convert the input value to a boolean value.
	 * 
	 * @param obj
	 * @param def
	 * @return
	 */
	public static boolean toBoolean( String obj, boolean def ) {
		if ( obj == null ) return def;
		try { 
			if ( obj.equals("yes") ||
				 obj.equals("true") ) return true; 
			if ( obj.equals("no") ||
				 obj.equals("false")) return false;
			return def;
		} catch( Exception e ) { 
			return def; 
		}
	}
	
	
	/**
	 * Converts the input value to a color.
	 * 
	 * This method converts the string passed in input as a color. If one
	 * error occurs during the decoding, then the default value is returned.
	 * 
	 * @param obj
	 * @param def
	 * @return
	 */
	public static Color toColor( String obj, Color def ) {
		if ( obj == null ) return def;
		try { 
			return Color.decode(obj);
		} catch( Exception e ) { 
			return def; 
		}
	}
}
