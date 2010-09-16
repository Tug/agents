package epfl.lia.logist.logging;

import java.util.Date;

public class LogEntry {
	 	
	/**
	 * The cache holding log entries
	 */
	private Date mDate = null;
			

	/**
	 * The file where to put log data
	 */
	private String mText = null;
		

	/**
	 * The output file and formatting class
	 */
	private String mModule = null;
		

	/**
	 * The severity of this entry
	 */
	private LogSeverityEnum mSeverity = LogSeverityEnum.LSV_DEBUG; 
		

	/**
	 * The lowest severity level possible
	 */
	private int mLine = 0;
		

	
	/**
	 * The constructor of the class
	 * @param d
	 * @param t
	 * @param m
	 * @param s
	 * @param l
	 */
	public LogEntry( Date d, String t, String m, LogSeverityEnum s, int l  ) {
		mDate = d;
		mText = m;
		mModule = t;
		mSeverity = s; 
		mLine = l;
	}

	/**
	 * The time at which event occurred
	 * @return
	 */
	public Date getDate() {
		return mDate;
	}
	 
	
	/**
	 * The log entry message
	 * @return
	 */
	public String getText() {
		return mText;
	}

	
	/**
	 * The class in which the error/info happened
	 * @return
	 */
	public String getModule() { 
		return mModule;
	}
	
	
	/**
	 * The severity of the current log entry
	 * @return
	 */
	public LogSeverityEnum getSeverity() {
		return mSeverity;
	}

	
	/**
	 * The line at which event occured
	 * @return
	 */
	public int getLine() {
		return mLine;
	}

}