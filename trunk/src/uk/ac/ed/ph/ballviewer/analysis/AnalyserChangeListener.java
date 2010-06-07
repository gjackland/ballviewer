package uk.ac.ed.ph.ballviewer.analysis;

import java.util.EventListener;


public interface AnalyserChangeListener extends EventListener
{
	public void
	analyserStateChanged( AnalyserChangeEvent e );
}