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
	
	public StaticSystem
	getStaticSystem(
		final	File								inputFile,
		final	Collection< Analyser >	analysers
	)
	{
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
		
		return sys;
	}
	
}
