package uk.ac.ed.ph.ballviewer.io;

import java.io.*;
import java.awt.Color;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.math.*;
import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;

class GdfReader implements InputReader
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
			BufferedReader input = new BufferedReader( new FileReader( inputFile ) );
			input.readLine(); // useless title
			input.readLine(); // mysterious but useless number
			sys.p = new Ball[ Integer.parseInt( input.readLine().substring( 12, 24 ).trim() ) ];
			for( int i = 0; i < sys.p.length; i++ )
			{
				String s = input.readLine();
				double x = Double.parseDouble( s.substring( 0, 13 ).trim() );
				double y = Double.parseDouble( s.substring( 13, 26 ).trim() );
				double z = Double.parseDouble( s.substring( 26, 39 ).trim() );
				final Vector3 pos = new Vector3( x * 1.6, y * 1.6, z * 2.0 ).times( 0.05 ); // position
																							// in
																							// microns
				sys.p[ i ] = new Ball( pos, Color.gray );
			}
			input.close();
		}
		catch( Exception e )
		{
			System.out.println( "Error reading file" );
		}

		sys.shouldAnalyse = true;

		// TODO: Deal with system properties somehow
		final ExperimentRecord record = new ExperimentRecord( null );
		record.addSystemSample( sys );

		return record;
	}

}
