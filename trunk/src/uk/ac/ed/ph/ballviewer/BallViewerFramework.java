package uk.ac.ed.ph.ballviewer;

import java.io.File;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;
import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;

import uk.ac.ed.ph.ballviewer.io.ReaderManager;

import uk.ac.ed.ph.ballviewer.event.EventDispatcher;
import uk.ac.ed.ph.ballviewer.event.NewExperimentEvent;
import uk.ac.ed.ph.ballviewer.event.TimelineEvent;

/*
 *	The 'Model' for our ballviewer application, represents all the data.
 *
 *
 */
public final class BallViewerFramework
{

	public static final EventDispatcher	eventDispatcher		= new EventDispatcher();
	private final AnalysisManager		analyser			= new AnalysisManager( this );
	private final ReaderManager			reader				= new ReaderManager();

	private int							tmpCurrentSample	= 0;
	// The current experiment record
	private ExperimentRecord			experimentRecord;

	public BallViewerFramework()
	{
	}

	public StaticSystem getSystem()
	{
		return experimentRecord != null ? experimentRecord.getSample( tmpCurrentSample ) : null;
	}

	public AnalysisManager getAnalysisManager()
	{
		return analyser;
	}

	public void tmpSetCurrentSample( final int newSample )
	{
		tmpCurrentSample = newSample;

		if( experimentRecord != null )
		{
			// Fire a timeline changed event
			eventDispatcher.notify( new TimelineEvent( newSample ) );
		}
	}

	public ExperimentRecord getExperimentRecord()
	{
		return experimentRecord;
	}

	public boolean loadExperimentRecord( final File[] inputFiles )
	{
		if( inputFiles == null )
		{
			experimentRecord = null;
			eventDispatcher.notify( new NewExperimentEvent( experimentRecord ) );
			return false;
		}

		final ArrayList< Analyser > analysers = new ArrayList< Analyser >();
		experimentRecord = reader.getStaticSystem( inputFiles, analysers );

		analyser.reset();

		// Update any system specific analysers
		analyser.addAnalysers( analysers );

		eventDispatcher.notify( new NewExperimentEvent( experimentRecord ) );

		return true;
	}
}