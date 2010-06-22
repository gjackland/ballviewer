package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.Ball;
import uk.ac.ed.ph.ballviewer.StaticSystem;

final class DefaultAnalyser extends Analyser implements BallAnalyser
{
	private final ArrayList< ContinuousAnalyserOutput >	continuousOutputs =
		new ArrayList< ContinuousAnalyserOutput >();

	private final ArrayList< DiscreteAnalyserOutput >	discreteOutputs =
		new ArrayList< DiscreteAnalyserOutput >();		
		
	public void
	addAnalyserOutput(
		final ContinuousAnalyserOutput	newOutput
	)
	{
		continuousOutputs.add( newOutput );
	}
	
	public void
	addAnalyserOutput(
		final DiscreteAnalyserOutput	newOutput
	)
	{
		discreteOutputs.add( newOutput );
	}
	
	@Override
	public String
	getName()
	{
		return "Default Analyser";
	}
	
	// INTERFACES ///////////////////////////////////////////////////////
	
	// ANALYSER /////////////////////////////////////////////////////////
	
	public void
	initialise(
		final AnalysisManager		manager
	)
	{
		manager.attachBallAnalyser( this );
	}
	
	
	// BALL ANALYSER ////////////////////////////////////////////////////
	
	/**
	 *
	 *	Called by the Analysis Manager to tell the analyser to update 
	 *
	 *
	 */
	@Override
	public void
	updateAttributes( final StaticSystem system )
	{
		final Ball[] balls = system.getBalls();
		
		for( ContinuousAnalyserOutput out : continuousOutputs )
		{
			//out.updateOutput()
		}
	}
	
	/**
	 *	The analyser should provide an array of outputs that can be mapped onto
	 *	attributes of a ball e.g. ball energy can be mapped onto the ball colour
	 *
	 */
	@Override
	public AnalyserOutput[]
	getOutputs()
	{
		final AnalyserOutput[] outputsArray =
			new AnalyserOutput[ continuousOutputs.size() + discreteOutputs.size() ];
			
		for( int i = 0; i < continuousOutputs.size(); ++i )
		{
			outputsArray[ i ] = continuousOutputs.get( i );
		}
		for( int i = 0; i < discreteOutputs.size(); ++i )
		{
			outputsArray[ i + discreteOutputs.size() ] = discreteOutputs.get( i );
		}
		
		return outputsArray;
	}
	
	// END INTERFACES ///////////////////////////////////////////////////
}