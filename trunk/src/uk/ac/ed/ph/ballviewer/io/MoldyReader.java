package uk.ac.ed.ph.ballviewer.io;

import java.io.*;
import java.util.StringTokenizer;
import java.awt.Color;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;
import uk.ac.ed.ph.ballviewer.analysis.BallAnalyser;
import uk.ac.ed.ph.ballviewer.analysis.SingleValueBallAnalyser;
import uk.ac.ed.ph.ballviewer.math.*;

class MoldyReader implements InputReader
{
	private static final String PARAMS_DEFAULT_FILENAME	= "params.in";	// The default name used for params files as written by MOLDY
	private static final String	LINE_COMMENT_IDENTIFIER	= "#";	// Character used in input to mark the rest of the line as being a comment
	private static final String	PARAMS_VALUE_DELIMITER	= "=";
	
	// If the system is periodic MOLDY may not use 0 as the minimum fractional coordinate
	// and may choose to center around 0 instead giving a range of [-0.5,0.5)
	// NOTE: as these are fractional coordinates the range will always be assumed to span 1.0 in total
	private static final double	PERIODIC_FRACTIONAL_MIN	= -0.5;

	
	/*
	 *	For convenience we enumerate all the prarameters that we will use from the params.in file
	 *
	 */
	private enum Parameters
	{
		IVOL( "ivol" );
		
		private final String paramString;
		
		public String
		toString()
		{
			return paramString;
		}
		
		Parameters( final String paramString )
		{
			this.paramString = paramString;
		}
	}

	public String[]
	getSupportedExtensions()
	{
		return ( new String[]{ "in", "out" } );
	}
	
	public StaticSystem
	getStaticSystem(
		final	File								inputFile,
		final	Collection< Analyser >				analysers
	)
	{
		SystemProperties		properties		= new SystemProperties();
		StaticSystem			sys 			= new StaticSystem();
		SingleValueBallAnalyser energyAnalyser	= null;
		SingleValueBallAnalyser speciesAnalyser	= null;
		
		
		// Try to get the params file
		final File paramsFile = findParamsInInputDir( inputFile );
		if( paramsFile != null )
		{
			System.out.println( "Using found params file: " + paramsFile );
			processParamsFile( paramsFile, properties );
		}
		
		try
		{
			BufferedReader input	= new BufferedReader( new FileReader( inputFile ) );
			sys.p					= new Ball[ Integer.parseInt( input.readLine().trim() ) ];		// Read the number of atoms
			
			// Set up two analysers that are going to take on values from the input file
			energyAnalyser			= new SingleValueBallAnalyser( new String( "Particle Energies" ), sys.p.length );
			speciesAnalyser			= new SingleValueBallAnalyser( new String( "Particle Species" ), sys.p.length );
			
			input.readLine();										// Skip over cell repeat information
			final Matrix3		latticeMatrix = getLatticeMatrix( input );	// Get the lattice cell matrix
			
			// Set the system properties to use as the bounding box
			final Aabb	bb 	= new Aabb( latticeMatrix );
			//final Vector3	min = bb.getMin();
			//final Vector3	max = bb.getMax();

			if( properties.periodic )
			{
				final Vector3	translateBy	= new Vector3( PERIODIC_FRACTIONAL_MIN * bb.xRange, PERIODIC_FRACTIONAL_MIN * bb.yRange, 0d );
				bb.translate( translateBy );
			}
			
			if( properties.periodic )
			{
				properties.supercell	= new CellPeriodicXY( bb );
			}
			
			StringTokenizer stok;
			String			token;
			for( int i=0; i < sys.p.length; i++ )
			{
				stok = new StringTokenizer( input.readLine() );
				Vector3 pos = new Vector3(
					processFractionalCoordinate( Double.parseDouble( stok.nextToken() ) ),
					processFractionalCoordinate( Double.parseDouble( stok.nextToken() ) ),
					Double.parseDouble( stok.nextToken() )
				);
				// Now convert the position from fractional to absoloute values
				pos = latticeMatrix.appliedTo( pos );

				speciesAnalyser.setBallValue( i, Double.parseDouble( stok.nextToken() ) );				// Species

				sys.p[i] = new Ball( pos, Color.gray );
				
				// Now let's get the energy of the atom
				stok.nextToken();		// Atomic mass
				energyAnalyser.setBallValue( i, Double.parseDouble( stok.nextToken() ) );		// and finally the energy
				
			}
			input.close();
		}
		catch( Exception e )
		{
			System.out.println( "Error reading file " );
			e.printStackTrace();
		}
		
		analysers.add( energyAnalyser );
		analysers.add( speciesAnalyser );
		
		sys.setSystemProperties( properties );
		sys.determineDimensions( paramsFile == null );
		sys.shouldAnalyse = true;
		
		
		return sys;
	}
	
	/*
	 *	Helper method to get the lattice matrix given the correct point in the input file
	 *
	 */
	private Matrix3
	getLatticeMatrix( final BufferedReader r )
	throws IOException
	{	
		StringTokenizer stok = new StringTokenizer( r.readLine() );
		double xx = Double.parseDouble( stok.nextToken() );
		double xy = Double.parseDouble( stok.nextToken() );
		double xz = Double.parseDouble( stok.nextToken() );
		stok = new StringTokenizer( r.readLine() );
		double yx = Double.parseDouble( stok.nextToken() );
		double yy = Double.parseDouble( stok.nextToken() );
		double yz = Double.parseDouble( stok.nextToken() );
		stok = new StringTokenizer( r.readLine() );
		double zx = Double.parseDouble( stok.nextToken() );
		double zy = Double.parseDouble( stok.nextToken() );
		double zz = Double.parseDouble( stok.nextToken() );

		return new Matrix3( xx, xy, xz, yx, yy, yz, zx, zy, zz );
	}
	
	/*
	 *	Look for the params file in the input file directory
	 *
	 */
	private File
	findParamsInInputDir(
		final File		inputFile
	)
	{
		if( inputFile.isFile() )
		{
			final File	paramsFile	= new File( inputFile.getParent() + File.separator + PARAMS_DEFAULT_FILENAME );
			
			if( paramsFile.isFile() )
			{
				return paramsFile;
			}
		}
		return null;
	}
	
	/*
	 *	Get a table containing the parameters from the params.in file
	 *
	 *
	 */
	private Hashtable< String, String >
	getParams( final File paramsFile )
	{
		final Hashtable< String, String >	paramsTable	= new Hashtable< String, String >();
		
		try
		{
			BufferedReader	params		= new BufferedReader( new FileReader( paramsFile ) );
			String			paramsLine	= params.readLine();
			while( paramsLine != null )
			{
				paramsLine	= getLineWithoutComment( paramsLine );
				String[]	paramAndValue	= paramsLine.split( PARAMS_VALUE_DELIMITER );
				// We expect to have two values: the parameter name and its value, otherwise something is wrong so ignore
				if( paramAndValue.length == 2 )
				{
					paramsTable.put( paramAndValue[ 0 ].trim(), paramAndValue[ 1 ].trim() );
				}
				
				paramsLine = params.readLine();		// Loop round if there is more
			} 
		}
		catch( Exception e )
		{
			System.out.println( "MoldyReader failed to read params input file" );
		}
		
		
		return paramsTable;
	}
	
	
	/*
	 *	Extract information we want here from the params file and put it into a SystemProperties instance
	 *
	 *
	 */
	private boolean
	processParamsFile(
		final File 				paramsInput,
		final SystemProperties	properties
	)
	{
		Hashtable< String, String> paramsTable = getParams( paramsInput );
		
		// Process ivol
		final String ivol = paramsTable.get( Parameters.IVOL.toString() );
		if( ivol != null && !ivol.isEmpty() )
		{
			if( ivol.equals( "1" ) || ivol.equals( "2" ) )
			{
				properties.periodic	= true;
			}
		}
		
		
		return true;
	}
	
	private double
	processFractionalCoordinate(
		final double x
	)
	{
		return wrapNumber( x - PERIODIC_FRACTIONAL_MIN, 1.0d ) + PERIODIC_FRACTIONAL_MIN;
	}
	
	/*
	 *	Wrap a number, x, in the range 0->range.
	 *
	 */
	private double
	wrapNumber( final double x, final double range )
	{
		double result = x % range;
		if( result < 0.0d )
		{
			result += range;
		}
		return result;
	}
	
	
	/*
	 *	Get the line up to the comment identifier but excluding it
	 *
	 */
	private String
	getLineWithoutComment( final String line )
	{
		final int commentIndex = line.indexOf( LINE_COMMENT_IDENTIFIER );

		if( commentIndex > -1 )		
		{
			return new String( line.substring( 0, commentIndex ) );
		}
		else
		{
			return new String( line );
		}
	}
	
}