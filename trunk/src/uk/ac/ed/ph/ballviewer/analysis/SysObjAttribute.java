package uk.ac.ed.ph.ballviewer.analysis;

import java.lang.IllegalArgumentException;

import java.lang.reflect.*;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ed.ph.ballviewer.util.Lib;

/*
 *	Class to represent an attribute of a system object such as a ball.
 *	This class can be used to set the attribute of a given array of the system
 *	object instances.
 */
public final class SysObjAttribute
{
	/**
	 * Helper storage class, used when determine attribute from system object.
	 * 
	 */
	private static final class AttributeDetails
	{
		public Method	setterMethod;
		public Object	defaultValue;
	}

	public static ArrayList< SysObjAttribute > getAttributes( final Class sysObjClass )
	{
		HashMap< String, AttributeDetails > attribDetailsMap = new HashMap< String, AttributeDetails >( 4 );

		// First find the attribute methods
		for( Method m : sysObjClass.getMethods() )
		{
			final AttributeMethod mAnnot = m.getAnnotation( AttributeMethod.class );
			if( mAnnot != null )
			{
				try
				{
					final AttributeDetails attribDetails = new AttributeDetails();
					attribDetails.setterMethod = m;
					attribDetailsMap.put( mAnnot.name(), attribDetails );
				}
				catch( IllegalArgumentException e )
				{
					System.out.println( "Failed to create attribute details from annotated method " + m + "skipping...\n" + e );
				}
			}
		}

		// And now match them up with their default values
		for( Field f : sysObjClass.getFields() )
		{
			// TODO: Check that it's a static field that has a value!
			final AttributeDefault fAnnot = f.getAnnotation( AttributeDefault.class );
			if( fAnnot != null )
			{
				final AttributeDetails attribDetails = attribDetailsMap.get( fAnnot.name() );
				if( attribDetails != null )
				{
					try
					{
						attribDetails.defaultValue = f.get( null );
					}
					catch( IllegalArgumentException e )
					{
						System.out.println( "Unable to set default value for attribute method " + attribDetails.setterMethod + ". Ignoring..." );
					}
					catch( IllegalAccessException e )
					{
						System.out.println( "Unable to get default value for attribute " + fAnnot.name() + " check that it is declared public. Ignoring..." );
					}
				}
				else
				{
					System.out.println( "Class " + sysObjClass + " has attribute default value " + fAnnot.name() + " but no corresponding annotated setter method.  Ignoring..." );
				}
			}
		}

		ArrayList< SysObjAttribute > attributes = new ArrayList< SysObjAttribute >( attribDetailsMap.size() );

		for( java.util.Map.Entry< String, AttributeDetails > entry : attribDetailsMap.entrySet() )
		{
			try
			{
				final AttributeDetails attribDetails = entry.getValue();
				final SysObjAttribute newAttrib = new SysObjAttribute( attribDetails.setterMethod.getParameterTypes()[ 0 ], attribDetails.setterMethod, attribDetails.defaultValue,
						entry.getKey() );
				attributes.add( newAttrib );

			}
			catch( IllegalArgumentException e )
			{
			} // This attribute won't work but we've already told the user the
				// error previously so carry on

		}

		return attributes;
	}

	private final String	name;
	private final Class		sysObjClass;	// The class type of the system
											// object that this attribute is
											// from
	private final Method	setter;		// The setter method used to set the
											// attribute
	private final Class		attributeClass; // The parameter class passed to the
											// setter
	private final Object	defaultValue;	// The default value for the
											// attribute if not being set by
											// analyser output

	public SysObjAttribute( final Class attributeClass, final Method setter, final Object defaultValue, final String name ) throws IllegalArgumentException
	{
		final Class[] setterParamTypes = setter.getParameterTypes();
		// Check that we haven't been given null references,
		// and that the setter has only one parameter
		if( attributeClass == null || setter == null || setterParamTypes.length != 1 || !Lib.areClassesCompatible( setterParamTypes[ 0 ], defaultValue.getClass() ) )
		{
			throw new IllegalArgumentException( "Can't instantiate SysObjAttribute, invalid parameters." );
		}

		this.name = name;
		this.sysObjClass = setter.getDeclaringClass();
		this.attributeClass = attributeClass;
		this.setter = setter;
		this.defaultValue = defaultValue;

	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Get the class type of the system object attribute.
	 * 
	 * 
	 */
	Class getAttributeClass()
	{
		return attributeClass;
	}

	/*
	 * Call the setter on a given array of sys obj instances setting the values
	 * to those passed in with the values array.
	 */
	boolean setValues( final Object[] values, final Object[] objArray )
	{
		if( values.length != objArray.length || values.length == 0 || !Lib.areClassesCompatible( sysObjClass, objArray[ 0 ].getClass() )
				|| !Lib.areClassesCompatible( attributeClass, values[ 0 ].getClass() ) )
		{
			System.out.println( "Not compatible: " + setter.getParameterTypes()[ 0 ] + " and " + values[ 0 ].getClass() );
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

	void resetToDefault( final Object[] objArray )
	{
		for( Object obj : objArray )
		{
			try
			{
				setter.invoke( obj, defaultValue );
			}
			catch( Exception e )
			{
				System.out.println( "Failed to set values to default for attribute " + getName() );
				e.printStackTrace();
			}
		}
	}

	private boolean isInputCompatible( final Class inputClass )
	{
		final Class paramClass = setter.getParameterTypes()[ 0 ];
		if( paramClass.isPrimitive() )
		{
			return paramClass.getSimpleName().equalsIgnoreCase( inputClass.getSimpleName() );
		}
		else
		{
			return paramClass.equals( inputClass );
		}
	}
}