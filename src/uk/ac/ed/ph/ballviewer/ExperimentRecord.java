package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

public final class ExperimentRecord
{
	private final SystemProperties								systemProperties;																// The
																																				// global
																																				// properties
																																				// of
																																				// the
																																				// system
	// Snapshots of the system as it varies with time
	private final ArrayList< StaticSystem >						systemSamples		= new ArrayList< StaticSystem >();

	// A list of all the classes of system object stored in this experiment
	// record e.g. balls, defects etc.
	private final ArrayList< Class< ? extends SystemObject > >	experimentObjects	= new ArrayList< Class< ? extends SystemObject > >( 1 );

	public ExperimentRecord( final SystemProperties systemProperties )
	{
		this.systemProperties = systemProperties;
	}

	public boolean registerSysObjType( final Class< ? extends SystemObject > objClass )
	{
		if( !experimentObjects.contains( objClass ) )
		{
			// experimentObjects.
		}
		return false;
	}

	public SystemProperties getSystemProperties()
	{
		return systemProperties;
	}

	public int getNumerOfSamples()
	{
		return systemSamples.size();
	}

	public StaticSystem getSample( final int i ) throws IndexOutOfBoundsException
	{
		return systemSamples.get( i );
	}

	public StaticSystem newSample()
	{
		final StaticSystem newSample = new StaticSystem( experimentObjects );
		systemSamples.add( newSample );
		return newSample;
	}

	public void addSystemSample( final StaticSystem sample )
	{
		System.out.println( "Adding sample to record" );
		systemSamples.add( sample );
		sample.initialise();
	}
}