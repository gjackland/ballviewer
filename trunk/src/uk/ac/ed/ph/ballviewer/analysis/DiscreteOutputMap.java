package uk.ac.ed.ph.ballviewer.analysis;

abstract class DiscreteOutputMap< T > extends AnalyserOutputMap< DiscreteAnalyserOutput >
{
	DiscreteOutputMap(
		final DiscreteAnalyserOutput		analyserOutput
	)
	{
		super( analyserOutput );
	}
	
	abstract T[]
	mapValues( final int[] inValues );
}