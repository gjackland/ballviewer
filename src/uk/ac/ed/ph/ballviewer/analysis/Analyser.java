package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.BallViewerFramework;
import uk.ac.ed.ph.ballviewer.StaticSystem;

import uk.ac.ed.ph.ballviewer.event.AnalyserChangeEvent;

import uk.ac.ed.ph.ballviewer.util.Options;
import uk.ac.ed.ph.ballviewer.util.Optionable;


public abstract class Analyser< T extends Options > implements Optionable< T >
{
	protected		T									options		= null;
	
	public abstract String
	getName();
	
	@Override	
	public String
	toString()
	{
		return getName();
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
		// Send out a message to indicate that our state has changed
		BallViewerFramework.eventDispatcher.notify( new AnalyserChangeEvent( this ) );
	}
	
	/**
	 *	This should be used by implementing classes to initialise the analyser and
	 *	make calls to AnalysisManager.attach(BallAnalyser|GraphAnalyser|DrawAnalyser)
	 *	as appropriate to make sure they recieve the correct update calls etc.
	 *
	 */
	public abstract void
	initialise(
		final AnalysisManager		manager
	);
}
