package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;

import uk.ac.ed.ph.ballviewer.io.ReaderManager;

public class BallViewerFramework
{
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