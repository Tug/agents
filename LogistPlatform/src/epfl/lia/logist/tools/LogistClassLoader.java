package epfl.lia.logist.tools;

/* import table */
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import epfl.lia.logist.logging.LogManager;
import epfl.lia.logist.logging.LogSeverityEnum;


/**
 * 
 * @author salves
 *
 */
public class LogistClassLoader {
 	
	/**
	 * Instantiate a class from the disk and from
	 *  
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object instantiateClass( String name, String classpath ) 
		throws Exception {

		// holds the list of urls
		URL[] urls = null;
		
		// retrieves the file path
		ArrayList<URL> urlList = new ArrayList<URL>();
		if ( classpath == null ) {
			urls = new URL[] { null };
		} else {
			StringTokenizer st = new StringTokenizer( classpath, ";" );
			while( st.hasMoreTokens() ) {
				String token = st.nextToken();
				URL url = new URL( "file", null, token );
				urlList.add( url );
			}
			urls = new URL[ urlList.size() ];
			urlList.toArray( urls );	
		}
		

		// creates a new class load
		URLClassLoader cl = URLClassLoader.newInstance( urls );
	
		// loads a new clas from the disk
		Class clMainClass = cl.loadClass( name );

		// returns a new instance
		return clMainClass.newInstance();
	}
	
	
	/**
	 * Instantiate a class from the disk and from
	 *  
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object instantiateClass( String name, String classpath, 
			Class<?>[] aclParams, Object[] aobjArgs ) throws Exception {

		URL[] urls = null;
		
		// retrieves the file path
		ArrayList<URL> urlList = new ArrayList<URL>();
		if ( classpath == null ) {
			urls = new URL[] { null };
		} else {
			StringTokenizer st = new StringTokenizer( classpath, ";" );
			while( st.hasMoreTokens() ) {
				String token = st.nextToken();
				URL url = new URL( "file", null, token );
				urlList.add( url );
			}
			urls = new URL[ urlList.size() ];
			urlList.toArray( urls );	
		}
		

		// creates a new class load
		URLClassLoader cl = URLClassLoader.newInstance( urls );
	
		// loads a new clas from the disk
		Class clMainClass = cl.loadClass( name );
		
		
		// was the class successfully loaded ?
		if ( clMainClass == null )
			throw new ClassNotFoundException( name );
		
		// browses through constructors
		Constructor[] aclConstructors = clMainClass.getDeclaredConstructors();
		for ( int i=0; i<aclConstructors.length; i++ ) {
			
			// retrieves the current constructor
			Constructor clConstructor = aclConstructors[i];
			
			// indicates whether param types match
			boolean bParamsMatch = true;
			
			// should have the same number of params
			Type[] atParams = clConstructor.getParameterTypes();
			if ( atParams.length == aclParams.length ) {	
				for ( int k=0; k<atParams.length; k++ ) {
					if ( atParams[k].getClass() != aclParams[k].getClass() )
						bParamsMatch = false;
				}
			} else {
				bParamsMatch = false;
			}
			
			// if parameter matches
			if ( bParamsMatch ) {
				LogManager.getInstance().log( LogManager.DEFAULT, 
					LogSeverityEnum.LSV_INFO, "Loaded class " + name + "!" );
				return clConstructor.newInstance( aobjArgs );
			}
		}
	
		// is there any match ?
		throw new java.lang.NoSuchMethodException( "constructor" );
	}
}
