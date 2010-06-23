package uk.ac.ed.ph.ballviewer.io;

import java.io.*;
import java.awt.Color;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.math.*;
import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;

class DatReader implements InputReader
{
	public String[]
	getSupportedExtensions()
	{
		return ( new String[]{ "gdf" } );
	}
	
	@Override
	public ExperimentRecord
	getExperimentRecord(
		final	File[]								inputFiles,
		final	Collection< Analyser >	analysers
	)
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
			sys.p = new Ball[input.readInt()];
			for( int i=0; i < sys.p.length; i++ )
			{
				final Vector3 pos = new Vector3((double)input.readFloat(),(double)input.readFloat(), (double)input.readFloat() );
				sys.p[i] = new Ball( pos, Color.gray );
			}
			input.close();
		}
		catch( Exception e )
		{
			System.out.println( "Error reading file" );
		}
		
		sys.shouldAnalyse=true;
		
		// TODO: Deal with system properties somehow
		final ExperimentRecord record = new ExperimentRecord( null );
		record.addSystemSample( sys );
		
		return record;
	}
	
}
