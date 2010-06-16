package uk.ac.ed.ph.ballviewer.analysis;

abstract class DiscreteOutputMap extends AnalyserOutputMap
{
	abstract Object[]
	mapValues( final int[] inValues );
}