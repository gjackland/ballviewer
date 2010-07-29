package uk.ac.ed.ph.ballviewer.event;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;

/**
 * 
 * Event fired when the state of an analyser has changed.
 * 
 */
public class AnalyserChangeEvent implements BallViewerEvent< AnalyserChangeListener >
{
	private final Analyser	source; // The analyser whose state has changed

	public AnalyserChangeEvent( final Analyser source )
	{
		this.source = source;
	}

	@Override
	public void notify( final AnalyserChangeListener listener )
	{
		listener.analyserStateChanged( source );
	}
}