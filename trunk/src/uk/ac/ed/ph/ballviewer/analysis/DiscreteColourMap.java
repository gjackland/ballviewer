package uk.ac.ed.ph.ballviewer.analysis;

import java.awt.Color;

import java.util.HashMap;

public class DiscreteColourMap extends DiscreteOutputMap< Color >
{
	private final HashMap< Integer, Color >	colourMap;

	DiscreteColourMap( final DiscreteAnalyserOutput analyserOutput )
	{
		super( analyserOutput );

		final int[] possibleValues = analyserOutput.getPossibleValues();
		colourMap = new HashMap< Integer, Color >( possibleValues.length );

		Integer min = 0, max = 4;
		// getMinMax( possibleValues, min, max );
		final int range = max - min;

		for( int i = 0; i < possibleValues.length; ++i )
		{
			colourMap.put( possibleValues[ i ], getDistributedColour( possibleValues[ i ], min, range ) );
		}
	}

	@Override
	Color[] mapValues( final int[] inValues )
	{
		final Color[] outColours = new Color[ inValues.length ];

		for( int i = 0; i < inValues.length; ++i )
		{
			outColours[ i ] = colourMap.get( inValues[ i ] );
		}

		return outColours;
	}

	@Override
	public void showOptionsDialog()
	{

	}

	private void getMinMax( final int[] possibleValues, Integer min, Integer max )
	{
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
		for( int i = 0; i < possibleValues.length; ++i )
		{
			if( possibleValues[ i ] > max )
			{
				max = possibleValues[ i ];
			}
			else if( possibleValues[ i ] < min )
			{
				min = possibleValues[ i ];
			}
		}
	}

	/**
	 * 
	 * Return a colour evenly distributed between the range.
	 * 
	 * 
	 */
	private Color getDistributedColour( final int value, final int min, final int range )
	{
		return Color.getHSBColor( ( ( ( float )( value - min ) ) / ( float )range ), 1.0f, 1.0f );
	}
}