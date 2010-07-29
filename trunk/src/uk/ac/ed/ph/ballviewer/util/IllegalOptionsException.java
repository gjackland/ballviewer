package uk.ac.ed.ph.ballviewer.util;

import java.lang.Exception;

public class IllegalOptionsException extends Exception
{
	private final Object[]	illegalOptions;
	private final String[]	optionsMessages;

	public IllegalOptionsException( final Object[] illegalOptions, final String[] optionsMessages )
	{
		if( illegalOptions == null || optionsMessages == null )
		{
			throw new IllegalArgumentException( "Cannot have null illegal options or options message" );
		}
		if( illegalOptions.length != optionsMessages.length )
		{
			throw new IllegalArgumentException( "You must supply a message stating the problem for each illegal option.  " + "illegal options: " + illegalOptions.length
					+ " options messages: " + optionsMessages.length );
		}

		this.illegalOptions = illegalOptions;
		this.optionsMessages = optionsMessages;
	}

	public Object[] getIllegalOptions()
	{
		return illegalOptions;
	}

	public String[] getOptionsMessager()
	{
		return optionsMessages;
	}

}