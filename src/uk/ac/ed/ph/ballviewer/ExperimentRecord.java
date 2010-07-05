package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

public final class ExperimentRecord
{
	private final SystemProperties				systemProperties;		// The global properties of the system 
	// Snapshots of the system as it varies with time
	private final ArrayList< StaticSystem >		systemSamples	= new ArrayList< StaticSystem >();
	
	
	public
	ExperimentRecord(
		final SystemProperties		systemProperties
	)
	{
		this.systemProperties	= systemProperties;
	}
	
	public SystemProperties
	getSystemProperties()
	{
		return systemProperties;
	}
	
	public int
	getNumerOfSamples()
	{
		return systemSamples.size();
	}
	
	public StaticSystem
	getSample( final int i )
	throws IndexOutOfBoundsException
	{
		return systemSamples.get( i );
	}
	
	public void
	addSystemSample( final StaticSystem sample )
	{
		System.out.println( "Adding sample to record" );
		systemSamples.add( sample );
		sample.initialise();
	}
}