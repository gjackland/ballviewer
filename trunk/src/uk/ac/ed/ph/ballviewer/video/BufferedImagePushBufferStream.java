package uk.ac.ed.ph.ballviewer.video;

import java.awt.Dimension;

import java.awt.image.BufferedImage;

import java.lang.Thread;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.Time;

import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;

public class BufferedImagePushBufferStream implements PushBufferStream, Runnable
{
	private final	ContentDescriptor		descriptor	= new ContentDescriptor( ContentDescriptor.RAW );
	private final	float					frameRate	= 20f;
	private final	Dimension				size;
	private final	RGBFormat				rgbFormat;
	private final	int						maxDataLength;
	private final	BufferedImage[]			images;
	
	private			int 					seqNo		= 0;
	private 		BufferTransferHandler	transferHandler;
	private 		boolean 				running;
	private 		Thread					thread;
	private			Control[]				controls	= new Control[ 0 ];
	private			boolean 				finished	= false;
	private final	long 					contentLength;
	private final 	Time					duration;

	
	public
	BufferedImagePushBufferStream(
		final BufferedImage[]			images
	)
	{
		if( images == null || images.length == 0 ||
			images[ 0 ].getType() != BufferedImage.TYPE_INT_RGB )
		{
			throw new IllegalArgumentException( "Illegal buffered image array passed to BufferedImagePushBufferStream" );
		}
		this.images	= images;
		
		size = new Dimension( images[ 0 ].getWidth(), images[ 0 ].getHeight() );
		// Max number of array elements per frame
		maxDataLength	= size.width * size.height;
		rgbFormat	= new RGBFormat(
			size,					// Frame size
			maxDataLength,			// Max number of array elements per frame
			Format.intArray,		// Data type
			frameRate,				// Frame rate
			24,						// Bits per pixel
			0x00FF0000,				// Red mask
			0x0000FF00,				// Green mask
			0x000000FF,				// Blue mask
			1,						// Pixel stride (number of array elements between adjacent pixels)
			size.width,				// Line stride (number of array elements between video lines)
			VideoFormat.FALSE,		// Flipped - whether video frames are vertically flipped
			Format.NOT_SPECIFIED	// Endian - byte ordering
		);
		
		// The total size (in bytes) of the data contained in this stream
		// i.e. num frames * bytes per frame
		contentLength	= images.length * maxDataLength * 4;
		duration		= new Time( ( double )images.length / ( double )frameRate );
		
		thread = new Thread( this );
	}
	
	void
	start( final boolean start )
	{
		synchronized ( this )
		{
			System.out.println( "start called " + start );
		    this.running 	= start;
		    if( running && !thread.isAlive() )
		    {
				thread = new Thread( this );
				thread.start();
		    }
		    notifyAll();
		}
	}

	public Time
	getDuration()
	{
		return duration;
	}
	
	private void
	reset()
	{
		System.out.println( "Resetting" );
		finished		= false;
		seqNo			= 0;		// Reset the sequence
	}
	
	// INTERFACES ///////////////////////////////////////////////
	
	// SOURCE_STREAM ////////////////////////////////////////////
	
	@Override
	public ContentDescriptor
	getContentDescriptor()
	{
		return descriptor;
	}
	
	@Override
	public long
	getContentLength()
	{
		return contentLength;
	}
	
	@Override
	public boolean
	endOfStream()
	{
		return finished;
	}

	
	// PUSH_BUFFER_STREAM ///////////////////////////////////////
	
	@Override
	public Format
	getFormat()
	{
		return rgbFormat;
	}
		
		
	@Override
	public void
	read( final Buffer buffer )
	{
		synchronized( this )
		{
			System.out.println( "SeqNo " + seqNo );
			Object outdata = buffer.getData();
			
			// Deal with invalid input
			if( outdata == null || !( outdata.getClass() == Format.intArray ) ||
				( ( int[] )outdata ).length < maxDataLength )
			{
				outdata = new int[ maxDataLength ];
				buffer.setData( outdata );
			}
			
			images[ seqNo ].getRGB(
				0,						// Start X
				0,						// Start Y
				size.width,				// Width
				size.height,			// Height
				( int[] )outdata,		// RGB array to write to
				0,						// Offset into the rgb array
				size.width				// Scanline stride of the rgb array
			);
			
			buffer.setFormat( rgbFormat );
			buffer.setTimeStamp( ( long ) ( seqNo * ( 1000 / frameRate ) * 1000000 ) );
			buffer.setSequenceNumber( seqNo );
			buffer.setLength( maxDataLength );
			buffer.setFlags( 0 );
			buffer.setHeader( null );
			seqNo++;
			if( seqNo >= images.length )
			{
				seqNo		= 0;
				finished	= true;
				buffer.setEOM( true );
			}
			
		}
	}

	@Override
	public void
	setTransferHandler( final BufferTransferHandler transferHandler )
	{
		synchronized( this )
		{
			this.transferHandler	= transferHandler;
			notifyAll();
		}
	}
	
	// RUNNABLE //////////////////////////////////////////////////////
	
	@Override
	public void run()
	{
		while( running )
		{
		    synchronized (this)
		    {
		    	// Wait for the transfer handler to be become valid
				while( transferHandler == null && running )
				{
				    try
				    {
						wait( 1000 );
				    } catch (InterruptedException ie) {
				    }
				} // end while
		    }
		
		    if( running && transferHandler != null)
		    {
				transferHandler.transferData(this);
				try
				{
					Thread.currentThread().sleep( 10 );
				} catch (InterruptedException ise) {}
		    }
		} // while( running )
		// Finished running so reset
		reset();
	} // run
	
	// CONTROLS /////////////////////////////////////////////////////
	
	@Override
	public Object[]
	getControls()
	{
		return controls;
	}
	
	@Override
	public Object
	getControl( String controlType )
	{
	   try
	   {
			final Class  cls	= Class.forName( controlType );
			final Object cs[]	= getControls();
			for( int i = 0; i < cs.length; i++ )
			{
				if( cls.isInstance( cs[i] ) )
	            return cs[ i ];
			}
			return null;
	
		} catch (Exception e)
		{   // no such controlType or such control
	     return null;
		}
	}

}