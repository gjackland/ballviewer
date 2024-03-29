package uk.ac.ed.ph.ballviewer.io;

import java.util.Hashtable;
import java.io.File;
import java.util.Date;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.ExperimentRecord;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;

public final class ReaderManager
{
	private static final Hashtable< String, InputReader >	readerRegistry	= new Hashtable< String, InputReader >();

	static enum InputReaders
	{
		// A list of all known input readers
		MOLDY( new MoldyReader() ), OLI( new OliReader() ), GDF( new GdfReader() ), TXT( new TxtReader() ), DAT( new DatReader() ), LAT( new LatReader() ), DUN( new DunReader() ), TRAC(
				new TracReader() );

		private final InputReader	reader;

		InputReaders( final InputReader reader )
		{
			this.reader = reader;
		}
	}

	/**
	 * 
	 * 
	 * 
	 * Constructor that will build up our mapping of supported extensions to the
	 * correct reader instance.
	 */
	public ReaderManager()
	{
		for( InputReaders r : InputReaders.values() )
		{
			for( String ext : r.reader.getSupportedExtensions() )
			{
				readerRegistry.put( ext, r.reader );
			}
		}
	}

	public ExperimentRecord getStaticSystem( final String[] inputFilenames, final Collection< Analyser > analysers ) throws IllegalArgumentException
	{
		if( analysers == null )
		{
			throw new IllegalArgumentException( "ReaderManager::getStaticSystem illegal argument: passed null analysers collection" );
		}
		final File inputFiles[] = new File[ inputFilenames.length ];
		for( int i = 0; i < inputFilenames.length; ++i )
		{
			inputFiles[ i ] = new File( inputFilenames[ i ] );
		}
		return getStaticSystem( inputFiles, analysers );
	}

	/**
	 * Creates a new <code>StaticSystem</code> from the file specified.
	 * <p>
	 * Currently 6 types of input supported:
	 * <ul>
	 * <li>.gdf (Matt) and .txt (Graeme) files are ascii.
	 * <li>.dat files are binary containing floats.
	 * <li>.dun (done) files are binary and contain the results so they are are
	 * ready for immediate viewing.
	 * <li>.lat files give position and three integers for use in colouring
	 * <li>.xyz files are ascii free format .txt
	 * </ul>
	 */
	public ExperimentRecord getStaticSystem( final File[] inputFiles, final Collection< Analyser > analysers ) throws IllegalArgumentException
	{
		if( analysers == null )
		{
			throw new IllegalArgumentException( "ReaderManager::getStaticSystem illegal argument: passed null analysers collection" );
		}
		if( inputFiles[ 0 ].isFile() )
		{
			final InputReader r = readerRegistry.get( getFileExtensions( inputFiles[ 0 ] ) );
			if( r != null )
			{
				ExperimentRecord expRec = null;
				try
				{
					expRec = r.getExperimentRecord( inputFiles, analysers );
				}
				catch( Exception e )
				{
					System.err.println( "Error loading experiment: " + e.toString() );
					e.printStackTrace();
					return null;
				}

				System.out.println( "Balls all read: " + new Date() );

				return expRec;
			}
			else
			{
				System.out.println( "File type not supported" );
			}
		}

		return null;
	}

	private String getFileExtensions( final File inputFile )
	{
		final String filename = inputFile.getName();
		return filename.substring( filename.lastIndexOf( '.' ) + 1, filename.length() );
	}
}