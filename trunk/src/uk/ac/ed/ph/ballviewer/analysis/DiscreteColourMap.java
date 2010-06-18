package uk.ac.ed.ph.ballviewer.analysis;

import java.awt.Color;

import java.util.HashMap;

public class DiscreteColourMap extends DiscreteOutputMap< Color >
{
	private final HashMap< Integer, Color >	colourMap;

	DiscreteColourMap(
		final DiscreteAnalyserOutput		analyserOutput
	)
	{
		super( analyserOutput );
		
		final int[] possibleValues = analyserOutput.getPossibleValues();
		colourMap = new HashMap< Integer, Color >( possibleValues.length );
		
		Integer min = 0, max = 4;
		//getMinMax( possibleValues, min, max );
		final int range = max - min;
		
		for( int i = 0; i < possibleValues.length; ++i )
		{
			colourMap.put( possibleValues[ i ], getDistributedColour( possibleValues[ i ], min, range ) );
		}
	}
	
	Color[]
	mapValues( final int[] inValues )
	{
		final Color[] outColours = new Color[ inValues.length ];
		
		for( int i = 0; i < inValues.length; ++i )
		{
			outColours[ i ] = colourMap.get( inValues[ i ] );
		}
		
		return outColours;
	}
	
	public void
	showOptionsDialog()
	{
		
	}
	
	private void
	getMinMax(
		final int[]		possibleValues,
		Integer			min,
		Integer			max
	)
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
	
	private Color
	getDistributedColour(
		final int	value,
		final int 	min,
		final int	range
	)
	{
		return new Color(
			( int )( ( ( double )( value - min ) ) / ( double )range * 255d ),
			( int )( ( ( double )( value - min ) ) / ( double )range * 255d ),
			( int )( ( ( double )( value - min ) ) / ( double )range * 255d ),
			255
		);
	}
}