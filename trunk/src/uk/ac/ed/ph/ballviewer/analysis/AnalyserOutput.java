package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

abstract class AnalyserOutput< T extends AnalyserOutputMap >
{
	// A list to store all the system object attributes that are attached to this analyser output
	protected final ArrayList< SysObjAttribute >		attachedAttributes =
		new ArrayList< SysObjAttribute >();
		
	protected final ArrayList< T >					outputMaps = 
		new ArrayList< T >();
	
	/**
	 *
	 *	Attach an attribute to this analyser output
	 *
	 *
	 */
	final boolean
	attachAttribute(
		final SysObjAttribute	attribute
	)
	{
		final Class< T > outputMapClass = getOutputMapForClass( attribute.getSysObjClass() );
		
		// Check that we support this attribute type and that it isn't already attached
		if( outputMapClass != null &&
			!attachedAttributes.contains( attribute ) )
		{
			// Let's try making a new instance of the output map for this type of attribute
			try
			{
				final T outputMap = outputMapClass.newInstance();
				
				outputMaps.add( outputMap );
				attachedAttributes.add( attribute );
			}
			catch( Exception e )
			{
				System.out.println( "Failed to create new output map of type " + outputMapClass + " for attribute " + attribute.getName() );
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 *	
	 *	Detach the attribute from this analyser output
	 *
	 *
	 */
	final boolean
	detachAttribute(
		final SysObjAttribute	attribute
	)
	{
		final int attributeIndex = attachedAttributes.indexOf( attribute );
		outputMaps.remove( attributeIndex );
		return attachedAttributes.remove( attributeIndex ) != null;
	}
	
	abstract Set< Class >
	getSupportedAttributeTypes();
	
	protected abstract Class< T >
	getOutputMapForClass(
		final Class sysObjClass
	);
}