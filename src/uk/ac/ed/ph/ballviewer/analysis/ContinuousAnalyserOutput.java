package uk.ac.ed.ph.ballviewer.analysis;

import java.util.Hashtable;
import java.util.Set;

class ContinuousAnalyserOutput extends AnalyserOutput< ContinuousOutputMap >
{
	private static final	Hashtable< Class, Class< ContinuousOutputMap > >	defaultMaps =
		new Hashtable< Class, Class< ContinuousOutputMap > >();
		
	private	final			Hashtable< Class, Class< ContinuousOutputMap > >	customMaps =
		new Hashtable< Class, Class< ContinuousOutputMap > >();


	void
	updateAttributeValues(
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
	
	protected Class< ContinuousOutputMap >
	getOutputMapForClass(
		final Class sysObjClass
	)
	{
		Class< ContinuousOutputMap > map = customMaps.get( sysObjClass );
		
		// If we couldn't find it in the custom maps then use a default one
		if( map == null )
		{
			map = defaultMaps.get( sysObjClass );
		}
		
		return map;
	}

}