package epfl.lia.logist.logging;

public interface LogOutputFormat {


	/**
	 * Outputs a formatted entry to the stream
	 * @param e
	 */
	public void outputEntry( LogEntry e ); 
		
	
	/**
	 * Initiatializes the output 
	 */
	public void init();
	
	
	/**
	 * Closes the output stream
	 */
	public void close();
	
}
