package uk.ac.ed.ph.ballviewer.io;

import java.awt.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

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
		return new String[]{ "trac" };
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
		
		SystemProperties			properties		= new SystemProperties();
		ArrayList< ArrayList< Ball > >	allBalls	= new ArrayList< ArrayList< Ball > >( 30 );

		try
		{
			BufferedReader input	= new BufferedReader( new FileReader( inputFile ) );
			
			StringTokenizer stok;
			String			token;
			while( true )
			{
				final String line = input.readLine();
				if( line == null )
				{
					break;
				}
				
				stok = new StringTokenizer( line );
				final Vector3	pos = new Vector3();
				
				pos.x	= Double.valueOf( stok.nextToken() );
				pos.y	= Double.valueOf( stok.nextToken() );
				pos.z	= Double.valueOf( stok.nextToken() );
				
				final int frameNo	= ( int )( double )Double.valueOf( stok.nextToken() );
				final int ballId	= ( int )( double )Double.valueOf( stok.nextToken() );
				
				ArrayList< Ball > balls;
				try
				{
					balls = allBalls.get( frameNo );
				}
				catch( Exception e )
				{
					balls = new ArrayList< Ball >( 5 );
					allBalls.add( balls );
				}
				
				balls.add( new Ball( pos, Color.gray ) );
			}
			input.close();
		}
		catch( Exception e )
		{
			System.out.println( "Error reading file " );
			e.printStackTrace();
		}
		
		ArrayList< StaticSystem >	systems 	= new ArrayList< StaticSystem >( 30 );
		final ExperimentRecord		record		= new ExperimentRecord( properties );
		for( int i = 0; i < allBalls.size(); ++i )
		{
			final ArrayList< Ball > balls = allBalls.get( i );
			if( balls != null )
			{	
				StaticSystem sys;
				try
				{
					sys = systems.get( i );
				}
				catch( final Exception e )
				{
					sys = new StaticSystem();
					sys.setSystemProperties( new SystemProperties() );
					sys.determineDimensions( true );
					sys.shouldAnalyse = false;
				}
				sys.p = new Ball[ balls.size() ];
				balls.toArray( sys.p );
				record.addSystemSample( sys );
			}
		}
		
		return record;
	}
}