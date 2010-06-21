package uk.ac.ed.ph.ballviewer.analysis;

import java.lang.NoSuchMethodException;

import java.util.Hashtable;
import java.util.Set;

import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.EventDispatcher;

class ContinuousAnalyserOutput extends AnalyserOutput< ContinuousOutputMap >
{
	private static final	Hashtable< Class, Class< ? extends ContinuousOutputMap > >	defaultMaps =
		new Hashtable< Class, Class< ? extends ContinuousOutputMap > >();
		
	static
	{
		// Define all the default maps here
		registerDefaultMap( ContinuousColourMap.class, java.awt.Color.class );
		registerDefaultMap( ContinuousDoubleMap.class, Double.class );
		registerDefaultMap( ContinuousDoubleMap.class, double.class );
	}
	
	private static boolean
	registerDefaultMap(
		final Class< ? extends ContinuousOutputMap >	mapperClass,
		final Class										outputClass
	)
	{
		// Check that the class has the required constructor i.e. one parameter of type ContinuousAnalyserOutput
		try
		{
			// This will throw exception if it can't find the method
			mapperClass.getDeclaredConstructor( ContinuousAnalyserOutput.class );
			defaultMaps.put( outputClass, mapperClass );
			return true;
		}
		catch( NoSuchMethodException e )
		{
			return false;
		}
	}
		
	// Users can create custom continuous output maps and add them in which case they will be stored here.
	// These take priority over any default maps that may exist.		
	private	final			Hashtable< Class, Class< ? extends ContinuousOutputMap > >	customMaps =
		new Hashtable< Class, Class< ? extends ContinuousOutputMap > >();



	ContinuousAnalyserOutput(
		final String 			name,
		final BallAnalyser		parentAnalyser
	)
	{
		super( name, parentAnalyser );
	}

	void
	updateOutput(
		final double[]	newValues,
		final Object[]	objArray
	)
	{
		for( int i = 0; i < attachedAttributes.size(); ++i )
		{
			attachedAttributes.get( i ).setValues( outputMaps.get( i ).mapValues( newValues ), objArray );
		}
	}
	
	Set< Class >
	getSupportedAttributeTypes()
	{
		final Set< Class > supportedTypes = defaultMaps.keySet();
		supportedTypes.addAll( customMaps.keySet() );
		
		return supportedTypes;
	}
	
	protected Class< ? extends ContinuousOutputMap >
	getOutputMapForClass(
		final Class sysObjClass
	)
	{
		Class< ? extends ContinuousOutputMap > map = customMaps.get( sysObjClass );
		
		// If we couldn't find it in the custom maps then use a default one
		if( map == null )
		{
			map = defaultMaps.get( sysObjClass );
		}
		
		return map;
	}

}