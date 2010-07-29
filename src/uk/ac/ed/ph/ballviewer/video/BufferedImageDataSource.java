package uk.ac.ed.ph.ballviewer.video;

import java.io.IOException;

import javax.media.Time;

import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

public class BufferedImageDataSource extends PushBufferDataSource
{

	private final Object[]					controls	= new Object[ 0 ];

	private final Time						duration;
	private boolean							connected	= false;
	private boolean							running		= false;
	private BufferedImagePushBufferStream	stream		= null;
	private BufferedImagePushBufferStream[]	streams;

	public BufferedImageDataSource( final BufferedImagePushBufferStream stream )
	{
		this.stream = stream;
		duration = stream.getDuration();
	}

	// INTERFACES ///////////////////////////////////////////////////

	@Override
	public String getContentType()
	{
		if( !connected )
		{
			System.err.println( "Error: DataSource not connected" );
			return null;
		}
		return stream.getContentDescriptor().getContentType();
	}

	@Override
	public void connect() throws IOException
	{
		if( connected )
		{
			return;
		}
		connected = true;
	}

	@Override
	public void disconnect()
	{
		try
		{
			if( running )
			{
				stop();
			}
		}
		catch( IOException e )
		{
		}
		connected = false;
	}

	@Override
	public void start() throws IOException
	{
		if( !connected )
		{
			throw new java.lang.Error( "DataSource must be connected before it can be started" );
		}
		if( running )
		{
			return;
		}
		running = true;
		stream.start( true );
	}

	@Override
	public void stop() throws IOException
	{
		if( ( !connected ) || ( !running ) )
		{
			return;
		}
		running = false;
		stream.start( false );
	}

	@Override
	public Object[] getControls()
	{
		return controls;
	}

	@Override
	public Object getControl( String controlType )
	{
		try
		{
			final Class cls = Class.forName( controlType );
			final Object cs[] = getControls();
			for( int i = 0; i < cs.length; i++ )
			{
				if( cls.isInstance( cs[ i ] ) )
					return cs[ i ];
			}
			return null;

		}
		catch( Exception e )
		{ // no such controlType or such control
			return null;
		}
	}

	@Override
	public Time getDuration()
	{
		return duration;
	}

	@Override
	public PushBufferStream[] getStreams()
	{
		if( streams == null )
		{
			streams = new BufferedImagePushBufferStream[] { stream };
		}
		return streams;
	}
}