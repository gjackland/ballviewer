package uk.ac.ed.ph.ballviewer.analysis;

import java.util.Hashtable;
import java.util.Set;

class DiscreteAnalyserOuput extends AnalyserOutput< DiscreteOutputMap >
{
	private static final	Hashtable< Class, Class< DiscreteOutputMap > >	defaultMaps =
		new Hashtable< Class, Class< DiscreteOutputMap > >();
		
	private	final			Hashtable< Class, Class< DiscreteOutputMap > >	customMaps =
		new Hashtable< Class, Class< DiscreteOutputMap > >();


	DiscreteAnalyserOuput(
		final String		name
	)
	{
		super( name );
	}

	void
	updateOutput(
		final int[]		newValues,
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
	
	protected Class< DiscreteOutputMap >
	getOutputMapForClass(
		final Class sysObjClass
	)
	{
		Class< DiscreteOutputMap > map = customMaps.get( sysObjClass );
		
		// If we couldn't find it in the custom maps then use a default one
		if( map == null )
		{
			map = defaultMaps.get( sysObjClass );
		}
		
		return map;
	}
}