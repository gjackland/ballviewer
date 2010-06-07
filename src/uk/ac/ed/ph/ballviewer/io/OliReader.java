package uk.ac.ed.ph.ballviewer.io;

import java.io.*;
import java.awt.Color;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.math.*;
import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;

class OliReader implements InputReader
{
	public String[]
	getSupportedExtensions()
	{
		return ( new String[]{ "oli" } );
	}
	
	public StaticSystem
	getStaticSystem(
		final	File								inputFile,
		final	Collection< Analyser >	analysers
	)
	{
		StaticSystem sys = new StaticSystem();
		
		System.out.println("  Assuming 3 x Coordinates + Species input" );
		final int linelength = 72; 
			
		try
		{
			LineNumberReader lnr = new LineNumberReader( new FileReader( inputFile ) );
			lnr.setLineNumber(1);
			StreamTokenizer stok = new StreamTokenizer(lnr);
			stok.parseNumbers();
			stok.eolIsSignificant( true );
			stok.nextToken();
			while( stok.ttype != StreamTokenizer.TT_EOL )
			{
				int lineno = lnr.getLineNumber();
				int sizlat =(int)stok.nval;
				sys.p = new Ball[sizlat];
				System.out.println(sizlat);
				stok.nextToken();
			}
			
			for( int i=0; i < sys.p.length; i++ )
			{
				double x=0,y=0,z=0,mass=0,energy=0;
				int spec=0,spin=0,sigma=0;
				stok.nextToken();
				while( stok.ttype != StreamTokenizer.TT_EOL )
				{
					if (stok.ttype == StreamTokenizer.TT_NUMBER)
					x  = stok.nval;
					stok.nextToken();
					if (stok.ttype == StreamTokenizer.TT_NUMBER)  
					y  = stok.nval;
					stok.nextToken();
					if (stok.ttype == StreamTokenizer.TT_NUMBER)
					z  = stok.nval;
					stok.nextToken();        
					if (stok.ttype == StreamTokenizer.TT_NUMBER)
					spec  = (int)stok.nval;
					stok.nextToken();      
				} 
				
				if( spec == 1 )
				{
					sys.p[ i ] = new Ball( new Vector3(x,y,z), 10.0d, Color.red );
				}
				else
				{
					sys.p[ i ] = new Ball( new Vector3(x,y,z), Color.yellow );
				}
	
			}
		}
		catch( Exception e )
		{
			System.out.println( "Error loading file" );
		}
		
		sys.shouldAnalyse = true;
		
		return sys;
	}
	
}
