package uk.ac.ed.ph.ballviewer.analysis;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.EventDispatcher;


public abstract class AnalyserOutput< T extends AnalyserOutputMap >
{	
	// These two lists remain in sync with one another.  For each attached attribute
	// exists a corresponding output map that maps the analyser output onto the class type
	// of the attribute
	protected final	ArrayList< SysObjAttribute >	attachedAttributes =
		new ArrayList< SysObjAttribute >();
	protected final	ArrayList< T >					outputMaps = 
		new ArrayList< T >();
	
	private final	String							name;
	private			EventDispatcher					events;	// Need this so we can send and receive message
	
	AnalyserOutput(
		final String			name
	)
	{
		this.name		= name;
		this.events		= events;
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
				final Constructor< ? extends T > ctor = outputMapClass.getDeclaredConstructor( this.getClass() );
				final T outputMap = ctor.newInstance( this ); // outputMapClass.newInstance();
				
				outputMaps.add( outputMap );
				attachedAttributes.add( attribute );

				if( events != null )
				{
					// Send a message to indicate that an attribute has been attached
					events.notify( new AttributeAttachEvent( true, this, attribute ) );
				}
				
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
		if( attributeIndex != -1 )
		{
			// Remove the output map and the attribute
			outputMaps.remove( attributeIndex );
			attachedAttributes.remove( attributeIndex );
			
			if( events != null )
			{
				// Send a message indicating that the detach event has occured
				events.notify( new AttributeAttachEvent( false, this, attribute ) );
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 *	toString gives the name of the output analyser.
	 *
	 */
	public String
	toString()
	{
		return getName();
	}
	
	public String
	getName()
	{
		return  name;
	}
	
	/**
	 *	Get a set of all the classes that this output can be mapped to.
	 *
	 *
	 */
	abstract Set< Class >
	getSupportedAttributeTypes();
	
	/**
	 *	Set the event dispatcher, used to send attach events.
	 *
	 *	This is typically set by the AnalysisManager when this output is added.
	 */
	final void
	setEventDispatcher(
		final EventDispatcher	events
	)
	{
		this.events	= events;
	}
	
	protected abstract Class< ? extends T >
	getOutputMapForClass(
		final Class sysObjClass
	);
	
}