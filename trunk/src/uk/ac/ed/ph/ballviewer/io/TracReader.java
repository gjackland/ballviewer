package uk.ac.ed.ph.ballviewer.io;

import java.awt.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.Ball;
import uk.ac.ed.ph.ballviewer.ExperimentRecord;
import uk.ac.ed.ph.ballviewer.StaticSystem;
import uk.ac.ed.ph.ballviewer.SystemProperties;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;

import uk.ac.ed.ph.ballviewer.math.Aabb;
import uk.ac.ed.ph.ballviewer.math.Vector3;

public final class TracReader implements InputReader
{
	@Override
	public String[]
	getSupportedExtensions()
	{
		return new String[]{ ".trac" };
	}
	
	@Override
	public ExperimentRecord
	getExperimentRecord(
		final	File[]							inputFiles,
		final	Collection< Analyser >			analysers
	)
	{
		if( inputFiles == null || inputFiles.length == 0 || inputFiles[ 0 ] == null )
		{
			return null;
		}
		final File inputFile = inputFiles[ 0 ];
		
		SystemProperties		properties		= new SystemProperties();
		StaticSystem			sys 			= new StaticSystem();		

		try
		{
			BufferedReader input	= new BufferedReader( new FileReader( inputFile ) );
			sys.p					= new Ball[ Integer.parseInt( input.readLine().trim() ) ];		// Read the number of atoms
			
			input.readLine();										// Skip over cell repeat information
			
			// Set the system properties to use as the bounding box
			//final Aabb	bb 	= new Aabb( latticeMatrix );
			
			StringTokenizer stok;
			String			token;
			for( int i=0; i < sys.p.length; i++ )
			{
				stok = new StringTokenizer( input.readLine() );
				
				// Now let's get the energy of the atom
				stok.nextToken();		// Atomic mass				
			}
			input.close();
		}
		catch( Exception e )
		{
			System.out.println( "Error reading file " );
			e.printStackTrace();
		}

		
		sys.setSystemProperties( properties );
		sys.determineDimensions( true );
		sys.shouldAnalyse = true;
		
		final ExperimentRecord record = new ExperimentRecord( properties );
		record.addSystemSample( sys );
		
		return record;
	}
}