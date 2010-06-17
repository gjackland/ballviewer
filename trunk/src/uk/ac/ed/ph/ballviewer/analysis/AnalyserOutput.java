package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public abstract class AnalyserOutput< T extends AnalyserOutputMap >
{
	// These two lists remain in sync with one another.  For each attached attribute
	// exists a corresponding output map that maps the analyser output onto the class type
	// of the attribute
	protected final ArrayList< SysObjAttribute >	attachedAttributes =
		new ArrayList< SysObjAttribute >();
	protected final ArrayList< T >					outputMaps = 
		new ArrayList< T >();
	
	private final String							name;
	
	
	AnalyserOutput(
		final String	name
	)
	{
		this.name	= name;
	}
	
	/**
	 *
	 *	Attach an attribute to this analyser output
	 *
	 *
	 */
	public final boolean
	attachAttribute(
		final SysObjAttribute	attribute
	)
	{
		final Class< ? extends T > outputMapClass = getOutputMapForClass( attribute.getAttributeClassType() );
		
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
				System.out.println( "Attached " + this + " to " + attribute );
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
	public final boolean
	detachAttribute(
		final SysObjAttribute	attribute
	)
	{
		final int attributeIndex = attachedAttributes.indexOf( attribute );
		outputMaps.remove( attributeIndex );
		return attachedAttributes.remove( attributeIndex ) != null;
	}
	
	/**
	 *	Get a set of all the classes that this output can be mapped to.
	 *
	 *
	 */
	abstract Set< Class >
	getSupportedAttributeTypes();
	
	protected abstract Class< ? extends T >
	getOutputMapForClass(
		final Class sysObjClass
	);
	
	/**
	 *	toString gives the name of the output analyser.
	 *
	 */
	public String
	toString()
	{
		return name;
	}
}