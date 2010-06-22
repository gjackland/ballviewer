package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;

import uk.ac.ed.ph.ballviewer.io.ReaderManager;

import uk.ac.ed.ph.ballviewer.event.EventDispatcher;

public final class BallViewerFramework
{
	public static final 	EventDispatcher				eventDispatcher			= new EventDispatcher();
	private final			AnalysisManager				analyser				= new AnalysisManager( this );
	private final			ReaderManager				reader					= new ReaderManager();
	
	
	private					int 						tmpCurrentSample		= 0;
	private 				ExperimentRecord			experimentRecord;
	
	BallViewerFramework()
	{
	}	
	
	/*
	 *	Tell the framework that we're using a new system
	 *
	 */
	void
	newExperimentRecord( ExperimentRecord newExperimentRecord )
	{
		this.experimentRecord		= newExperimentRecord;
		// TODO: Change this so it's on a message rather than direct call
		analyser.reset();
	}
	
	public StaticSystem
	getSystem()
	{
		return experimentRecord.getSample( tmpCurrentSample );
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
	
	void
	tmpSetCurrentSample( final int newSample )
	{
		tmpCurrentSample = newSample;
	}
	
	public ExperimentRecord
	getExperimentRecord()
	{
		return experimentRecord;
	}
}