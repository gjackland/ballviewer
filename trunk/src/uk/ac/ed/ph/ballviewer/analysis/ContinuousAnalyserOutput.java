package uk.ac.ed.ph.ballviewer.analysis;

import java.util.Hashtable;
import java.util.Set;

class ContinuousAnalyserOutput extends AnalyserOutput< ContinuousOutputMap >
{
	private static final	Hashtable< Class, Class< ? extends ContinuousOutputMap > >	defaultMaps =
		new Hashtable< Class, Class< ? extends ContinuousOutputMap > >();
		
	static
	{
		// Define all the default maps here
		defaultMaps.put( java.awt.Color.class, ContinuousColourMap.class );
	}
		
	// Users can create custom continuous output maps and add them in which case they will be stored here.
	// These take priority over any default maps that may exist.		
	private	final			Hashtable< Class, Class< ? extends ContinuousOutputMap > >	customMaps =
		new Hashtable< Class, Class< ? extends ContinuousOutputMap > >();



	ContinuousAnalyserOutput(
		final String 		name
	)
	{
		super( name );
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