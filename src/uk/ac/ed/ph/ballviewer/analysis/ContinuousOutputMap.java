package uk.ac.ed.ph.ballviewer.analysis;


abstract class ContinuousOutputMap extends AnalyserOutputMap
{
	abstract Object[]
	mapValues( double[]	inValues );
}
