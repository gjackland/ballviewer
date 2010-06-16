package uk.ac.ed.ph.ballviewer.analysis;

import java.lang.reflect.*;
import java.lang.IllegalArgumentException;

/*
 *	Class to represent an attribute of a system object such as a ball.
 *	This class can be used to set the attribute of a given array of the system
 *	object instances.
 */
public class SysObjAttribute
{
	private final Method		setter;
	private final Class			sysObjClass;
	
	public SysObjAttribute(
		final Class		sysObjClass,
		final Method	setter
	) throws IllegalArgumentException
	{
		// Check that we haven't been given null references,
		// that the setter is indeed from the class that we've been given
		// and that the setter has only one parameter
		try
		{
			if( sysObjClass == null || setter == null ||
				sysObjClass.getMethod( setter.getName(), setter.getParameterTypes() ) == null ||
				setter.getParameterTypes().length != 0 )
			{
				throw new IllegalArgumentException( "Can't instantiate SysObjAttribute, invalid parameters." );
			}
		} catch( NoSuchMethodException e )
		{
			throw new IllegalArgumentException( "Failed to instantiate SysObjAttribute, method passed in does not exist" );
		}
		
		this.sysObjClass	= sysObjClass;
		this.setter			= setter;
	}
	
	public String
	getName()
	{
		return null;
	}
	
	/**
	 *	Get the class type of the system object that this attribute describes.
	 *
	 *
	 */
	public Class
	getSysObjClass()
	{
		return sysObjClass;
	}
	
	/*
	 *	Call the setter on a given array of sys obj instances setting the values
	 *	to those passed in with the values array.
	 */
	boolean
	setValues(
		final Object[]	values,
		final Object[]	objArray
	)
	{
		if( values.length != objArray.length ||
			values.length == 0 ||
			!setter.getParameterTypes()[ 0 ].equals( values[ 0 ].getClass() ) )
		{
			return false;
		}
		for( int i = 0; i < objArray.length; ++i )
		{
			try
			{
				setter.invoke( objArray[ i ], values[ i ] );
			}
			catch( Exception e )
			{
				System.out.println( "Failed to set values for attribute " + getName() );
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}