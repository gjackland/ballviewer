package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;

import uk.ac.ed.ph.ballviewer.io.ReaderManager;

import uk.ac.ed.ph.ballviewer.event.EventDispatcher;

public final class BallViewerFramework
{
	public static final 	EventDispatcher				eventDispatcher			= new EventDispatcher();
	private 				StaticSystem				system;
	private final			AnalysisManager				analyser				= new AnalysisManager( this );
	private final			ReaderManager				reader					= new ReaderManager();
	
	BallViewerFramework()
	{
	}	
	
	/*
	 *	Tell the framework that we're using a new system
	 *
	 */
	void
	newSystem( StaticSystem newSystem )
	{
		this.system		= newSystem;
		// TODO: Change this so it's on a message rather than direct call
		analyser.reset();
	}
	
	public StaticSystem
	getSystem()
	{
		return system;
	}
	
	public AnalysisManager
	getAnalysisManager()
	{
		return analyser;
	}
	
	public ReaderManager
	getReaderManager()
	{
		return reader;
	}
}