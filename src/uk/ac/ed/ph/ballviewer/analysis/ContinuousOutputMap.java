package uk.ac.ed.ph.ballviewer.analysis;


abstract class ContinuousOutputMap extends AnalyserOutputMap< ContinuousAnalyserOutput >
{
	ContinuousOutputMap(
		final ContinuousAnalyserOutput	analyserOutput
	)
	{
		super( analyserOutput );
	}
	
	abstract Object[]
	mapValues( double[]	inValues );
}
