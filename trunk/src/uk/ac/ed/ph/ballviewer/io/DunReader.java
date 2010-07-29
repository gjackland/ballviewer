package uk.ac.ed.ph.ballviewer.io;

import java.io.*;
import java.awt.Color;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.math.*;
import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;

class DunReader implements InputReader
{
	@Override
	public String[] getSupportedExtensions()
	{
		return( new String[] { "gdf" } );
	}

	@Override
	public ExperimentRecord getExperimentRecord( final File[] inputFiles, final Collection< Analyser > analysers )
	{
		if( inputFiles == null || inputFiles.length == 0 || inputFiles[ 0 ] == null )
		{
			return null;
		}
		final File inputFile = inputFiles[ 0 ];

		StaticSystem sys = new StaticSystem();

		try
		{
			DataInputStream input = new DataInputStream( new FileInputStream( inputFile ) );
			sys.p = new Ball[ input.readInt() ];
			sys.setR( input.readFloat() ); // (don't need A,AA,RR unless
											// analysing)
			for( int i = 0; i < sys.p.length; i++ )
			{
				final Vector3 pos = new Vector3( input.readFloat(), input.readFloat(), input.readFloat() );
				final Color colour = StaticSystem.StructureType.values()[ input.readInt() ].colour();
				sys.p[ i ] = new Ball( pos, colour );
			}
			input.close();
			System.out.println( "Ready-processed set." );
		}
		catch( Exception e )
		{
			System.out.println( "Error reading file" );
		}

		sys.shouldAnalyse = false;

		// TODO: Deal with system properties somehow
		final ExperimentRecord record = new ExperimentRecord( null );
		record.addSystemSample( sys );

		return record;
	}

}
