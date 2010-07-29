package uk.ac.ed.ph.ballviewer.analysis;

import java.util.Hashtable;
import java.util.Set;

class DiscreteAnalyserOutput extends AnalyserOutput< DiscreteOutputMap >
{
	private static final Hashtable< Class, Class< ? extends DiscreteOutputMap > >	defaultMaps	= new Hashtable< Class, Class< ? extends DiscreteOutputMap > >();

	static
	{
		// Define all the default maps here
		registerDefaultMap( DiscreteColourMap.class, java.awt.Color.class );
	}

	private static boolean registerDefaultMap( final Class< ? extends DiscreteOutputMap > mapperClass, final Class outputClass )
	{
		// Check that the class has the required constructor i.e. one parameter
		// of type ContinuousAnalyserOutput
		try
		{
			// This will throw exception if it can't find the method
			mapperClass.getDeclaredConstructor( DiscreteAnalyserOutput.class );
			defaultMaps.put( outputClass, mapperClass );
			return true;
		}
		catch( NoSuchMethodException e )
		{
			return false;
		}
	}

	private final Hashtable< Class, Class< DiscreteOutputMap > >	customMaps	= new Hashtable< Class, Class< DiscreteOutputMap > >();

	private final int[]												possibleValues;

	DiscreteAnalyserOutput( final String name, final BallAnalyser parentAnalyser, final int[] possibleValues )
	{
		super( name, parentAnalyser );

		this.possibleValues = possibleValues;
	}

	int[] getPossibleValues()
	{
		return possibleValues;
	}

	void updateOutput( final int[] newValues, final Object[] objArray )
	{
		for( int i = 0; i < attachedAttributes.size(); ++i )
		{
			attachedAttributes.get( i ).setValues( outputMaps.get( i ).mapValues( newValues ), objArray );
		}
	}

	@Override
	Set< Class > getSupportedAttributeTypes()
	{
		final Set< Class > supportedTypes = defaultMaps.keySet();
		supportedTypes.addAll( customMaps.keySet() );

		return supportedTypes;
	}

	@Override
	protected Class< ? extends DiscreteOutputMap > getOutputMapForClass( final Class sysObjClass )
	{
		Class< ? extends DiscreteOutputMap > map = customMaps.get( sysObjClass );

		// If we couldn't find it in the custom maps then use a default one
		if( map == null )
		{
			map = defaultMaps.get( sysObjClass );
		}

		return map;
	}
}