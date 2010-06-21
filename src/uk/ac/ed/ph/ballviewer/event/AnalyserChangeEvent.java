package uk.ac.ed.ph.ballviewer.event;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;

public class AnalyserChangeEvent implements BallViewerEvent< AnalyserChangeListener >
{
	private final Analyser		source;		// The analyser whos state has changed
	
	public AnalyserChangeEvent(
		final Analyser		source
	)
	{
		this.source		= source;
	}
	
	
	@Override
	public void
	notify( final AnalyserChangeListener listener )
	{
		listener.analyserStateChanged( source );
	}
}