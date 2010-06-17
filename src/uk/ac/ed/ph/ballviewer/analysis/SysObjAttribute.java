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
	private final Class			attributeClassType;
	private final String		name;
	
	public SysObjAttribute(
		final Class			attributeClassType,
		final Method		setter,
		final String		name
	) throws IllegalArgumentException
	{
		// Check that we haven't been given null references,
		// and that the setter has only one parameter
		if( attributeClassType == null || setter == null ||
			setter.getParameterTypes().length != 1 )
		{
			throw new IllegalArgumentException( "Can't instantiate SysObjAttribute, invalid parameters." );
		}

		
		this.attributeClassType	= attributeClassType;
		this.setter				= setter;
		this.name				= name;
	}
	
	public String
	getName()
	{
		return name;
	}
	
	/**
	 *	Get the class type of the system object attribute.
	 *
	 *
	 */
	Class
	getAttributeClassType()
	{
		return attributeClassType;
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
	
	public String
	toString()
	{
		return name;
	}
}