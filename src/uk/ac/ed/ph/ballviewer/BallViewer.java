package uk.ac.ed.ph.ballviewer;

import java.util.Date;

import java.io.File;

import javax.swing.UIManager;

import uk.ac.ed.ph.ballviewer.gui.BallViewerGUI;

public final class BallViewer
{
	public static void main( String arg[] )
	{
		System.out.println( "\nStarting App: " + new Date() );

		// Set the look and feel to the native for that system
		try
		{
			// Set System L&F
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch( Exception e )
		{
		}

		String inputname = "", outputname = "";
		if( arg.length == 0 )
		{
			new BallViewerGUI();
		}
		else if( arg.length == 1 )
		{
			inputname = arg[ 0 ];
			outputname = changeFileExtension( arg[ 0 ], ".dun" );
			final File[] inputFiles = { new File( inputname ) };
			new BallViewerGUI( inputFiles );
		}
		else if( arg.length >= 2 )
		{
			final File[] inputFiles = new File[ arg.length ];
			for( int i = 0; i < arg.length; ++i )
			{
				inputFiles[ i ] = new File( arg[ i ] );
			}
			new BallViewerGUI( inputFiles );
		}
	}

	private static String changeFileExtension( String fnm, String ext )
	{ // fnm = filename, ext = extension
		int di = fnm.lastIndexOf( "." ); // dot index
		if( di == -1 || di < fnm.lastIndexOf( "/" ) )
		{ // if there is no dot in the name //
			return fnm + ext; // or if last dot isn't in a directory name //
		}
		else
		{
			return fnm.substring( 0, di ) + ext;
		}
	}
}