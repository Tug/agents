package org.logist.deliberative;

public class FailureException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FailureException() 
	{
		super("FAILURE");
	}
}