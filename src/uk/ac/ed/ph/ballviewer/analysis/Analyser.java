package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.StaticSystem;

import uk.ac.ed.ph.ballviewer.util.Options;
import uk.ac.ed.ph.ballviewer.util.Optionable;


public abstract class Analyser< T extends Options > implements Optionable< T >
{
	private final	ArrayList< AnalyserChangeListener >	listeners	= new ArrayList< AnalyserChangeListener >();
	protected		T									options		= null;
	
	public abstract String
	getName();
	
	public void
	addAnalyserChangeListener(
		final AnalyserChangeListener listener
	)
	{
		listeners.add( listener );
	}
	
	protected void
	fireAnalyserEvent(
		final AnalyserChangeEvent event
	)
	{
		for( AnalyserChangeListener listener : listeners )
		{
			listener.analyserStateChanged( event );
		}
	}
	
	public final T
	getOptions()
	{
		if( options != null )
		{
			return ( T )options.clone();
		}
		return null;
	}
	
	public void
	setOptions( final T newOptions )
	{
		options	= ( T )newOptions.clone();
		optionsChanged();
	}
	
	protected void
	optionsChanged()
	{
		fireAnalyserEvent( new AnalyserChangeEvent( this ) );
	}
}
