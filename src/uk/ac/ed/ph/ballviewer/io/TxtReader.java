package uk.ac.ed.ph.ballviewer.io;

import java.io.*;
import java.awt.Color;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.math.*;
import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;

class TxtReader implements InputReader
{
	public String[]
	getSupportedExtensions()
	{
		return ( new String[]{ "txt" } );
	}
	
	public ExperimentRecord
	getExperimentRecord(
		final	File[]					inputFiles,
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
			final int linelength = 36; 
			BufferedReader input = new BufferedReader( new FileReader( inputFile ) );
			sys.p = new Ball[(int)inputFile.length()/linelength];
			for( int i=0; i< sys.p.length; i++ )
			{
				String s = input.readLine();
				double x = Double.parseDouble(s.substring(10,18).trim() );
				double y = Double.parseDouble(s.substring(18,26).trim() );
				double z = Double.parseDouble(s.substring(26).trim() );					
				sys.p[i] = new Ball( new Vector3(x,y,z).times(0.5), Color.gray );
			}
			input.close();
			sys.shouldAnalyse=true;
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
