package uk.ac.ed.ph.ballviewer;

import java.awt.image.*;
import java.io.*;
import java.text.*;
import com.sun.image.codec.jpeg.*;

/**
 * Class for creating and saving jpeg images. <br>
 * Uses package <code>com.sun.image.codec.jpeg</code> . <br>
 * Each jpeg is encoded from an input {@link java.awt.image.BufferedImage} <br>
 * and is placed in the directory <code>c:/javaprog/images/</code> .
 */
public final class JpegCreator
{

	private JpegCreator()
	{
	} // This class cannot be instantiated. //

	static int				setNo	= 1;
	static int				imgNo	= 1;
	static DecimalFormat	nf		= new DecimalFormat( "00" );	// fills up
																	// space
																	// with 0's
																	// if
																	// necessary

	static
	{
		while( true )
		{
			File f = new File( fileName( setNo, imgNo ) );
			if( f.exists() )
				setNo++;
			else
				break; // if it doesn't exist then we can use this set number
		}
	}

	/**
	 * Encodes <code>img</code> into a new jpeg file. The name of the file
	 * produced contains 2 numbers: firstly the run number allows the files to
	 * be grouped by the instance of the program that created them, and secondly
	 * the image number to distiguish them individually within the run.
	 */
	public static void saveImage( BufferedImage img )
	{
		try
		{
			OutputStream os = new FileOutputStream( fileName( setNo, imgNo ) );
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( os );
			encoder.encode( img );
			os.close();
			imgNo++;
		}
		catch( Exception e )
		{
			System.out.println( "Save Image failed:\n" + e.toString() );
		}
	}

	// Write jpeg to current directory by default
	static final String	dir	= "";

	static String fileName( int n1, int n2 )
	{
		return dir + nf.format( n1 ) + nf.format( n2 ) + ".jpg";
	}
}
