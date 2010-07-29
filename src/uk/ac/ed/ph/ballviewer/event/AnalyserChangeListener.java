package uk.ac.ed.ph.ballviewer.event;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;

public interface AnalyserChangeListener
{
	public void analyserStateChanged( final Analyser source );
}