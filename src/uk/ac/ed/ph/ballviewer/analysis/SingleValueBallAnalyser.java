package uk.ac.ed.ph.ballviewer.analysis;

import java.awt.Color;

import uk.ac.ed.ph.ballviewer.Ball;
import uk.ac.ed.ph.ballviewer.StaticSystem;

public class SingleValueBallAnalyser extends Analyser< SingleValueBallAnalyserOptions > implements BallAnalyser
{
	private final String					name;
	private final double					ballValues[];	// The 'intensity'
															// values assigned
															// for each ball
	private double							min	= Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY; // Min
																											// and
																											// max
																											// values
																											// for
																											// particles

	private final ContinuousAnalyserOutput	valueOutput;

	public SingleValueBallAnalyser( final String name, final int numBalls )
	{
		this.name = name;
		this.ballValues = new double[ numBalls ];
		options = new SingleValueBallAnalyserOptions();
		valueOutput = new ContinuousAnalyserOutput( name, this );
	}

	@Override
	public void initialise( final AnalysisManager manager )
	{
		manager.attachBallAnalyser( this );
	}

	@Override
	public String getName()
	{
		return name;
	}

	private Color getColour( final double normalisedScale )
	{
		final int rMin = options.minColour.getRed();
		final int gMin = options.minColour.getGreen();
		final int bMin = options.minColour.getBlue();
		final int rMax = options.maxColour.getRed();
		final int gMax = options.maxColour.getGreen();
		final int bMax = options.maxColour.getBlue();
		return new Color( ( int )( rMin + ( rMax - rMin ) * normalisedScale ), ( int )( gMin + ( gMax - gMin ) * normalisedScale ), ( int )( bMin + ( bMax - bMin )
				* normalisedScale ), 255 );
	}

	public void setBallValue( final int index, final double value )
	{
		ballValues[ index ] = value;
		// Keep track of the maximum and minimum values
		if( value < min )
		{
			min = value;
		}
		else if( value > max )
		{
			max = value;
		}
	}

	@Override
	public void updateAttributes( StaticSystem sys )
	{
		final Ball[] balls = sys.getBalls();
		final double ballColourValues[] = new double[ balls.length ];

		for( int i = 0; i < ballValues.length; ++i )
		{
			double scaledVal = 0;
			if( options.useLogscale )
			{
				ballColourValues[ i ] = getScaledLogValue( i, 1.0d );
			}
			else
			{
				ballColourValues[ i ] = getScaledValue( i, 1.0d );
			}
		}

		valueOutput.updateOutput( ballColourValues, balls );
	}

	private double getScaledValue( final int index, final double scale )
	{
		final double range = max - min;
		// If they are all the same then always return the maximum
		if( range == 0.0d )
		{
			return scale;
		}
		return ( ( ballValues[ index ] - min ) / range ) * scale;
	}

	private double getScaledLogValue( final int index, final double scale )
	{
		final double scaledRange = ( max - min ) / 9.0d;
		// If they are all the same then always return the maximum
		if( scaledRange == 0.0d )
		{
			return scale;
		}
		final double value = ( ( ballValues[ index ] - min ) / scaledRange ) + 1.0d;
		return Math.log10( value ) * scale;
	}

	@Override
	public AnalyserOutput[] getOutputs()
	{
		AnalyserOutput[] outputs = { valueOutput };

		return outputs;
	}
}