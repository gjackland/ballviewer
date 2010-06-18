package uk.ac.ed.ph.ballviewer.analysis;

abstract class AnalyserOutputMap< T extends AnalyserOutput >
{
	private final T		analyserOutput;		// The analyser output that this map is associated with
	
	AnalyserOutputMap(
		final T			analyserOutput
	)
	{
		this.analyserOutput	= analyserOutput;
	}
	
	abstract void
	showOptionsDialog();
}