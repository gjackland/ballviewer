package uk.ac.ed.ph.ballviewer.event;

import java.util.ArrayList;
import java.util.Hashtable;

public final class EventDispatcher
{
	// Mapping of class events to active listeners
	private final Hashtable< Class< ? extends BallViewerEvent< ? > >, ArrayList >		listenersMap =
		new Hashtable< Class< ? extends BallViewerEvent< ? > >, ArrayList >( 10 );
		
	// Add a listener to an event class	
	public < T > void
	listen(
		final Class< ? extends BallViewerEvent< T > >	eventClass,
		final T											listener
	)
	{
		final ArrayList< T >	listeners	= getListenersOf( eventClass );
		
		synchronized( listeners )
		{
			if( !listeners.contains( listener ) )
			{
				listeners.add( listener );
			}
		}
	}
	
	// Notify all listeners of this event
	public < T > void
	notify( final BallViewerEvent< T > event )
	{
		@SuppressWarnings( "unchecked" )
		Class< ? extends BallViewerEvent< T > > eventClass = ( Class< ? extends BallViewerEvent< T > > )event.getClass();
		
		for( T listener : getListenersOf( eventClass ) )
		{
			event.notify( listener );
		}
	}
	
	// Get listeners for a given event class
	private < T > ArrayList< T >
	getListenersOf( Class< ? extends BallViewerEvent< T > > eventClass )
	{
		@SuppressWarnings( "unchecked" )
		final ArrayList< T > existing = listenersMap.get( eventClass );
		if( existing != null )
		{
			return existing;
		}
		
		final ArrayList< T > newList = new ArrayList< T >( 5 );
		listenersMap.put( eventClass, newList );
		return newList;
	}
	
	
}