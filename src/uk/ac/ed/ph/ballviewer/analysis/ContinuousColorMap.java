package uk.ac.ed.ph.ballviewer.analysis;

import java.awt.Color;

class ContinuousColorMap extends ContinuousOutputMap
{
	private Color	minColour;
	private Color	maxColour;
	
	Object[]
	mapValues( double[]	inValues )
	{
		final Object[] outValues = new Object[ inValues.length ];
		
		final int rMin	= minColour.getRed();
		final int gMin	= minColour.getGreen();
		final int bMin	= minColour.getBlue();
		final int rMax	= maxColour.getRed();
		final int gMax	= maxColour.getGreen();
		final int bMax	= maxColour.getBlue();
		
		for( int i = 0; i < inValues.length; ++i )
		{
			outValues[ i ] = new Color(
				( int )( rMin + ( rMax - rMin ) * inValues[ i ] ),
				( int )( gMin + ( gMax - gMin ) * inValues[ i ] ),
				( int )( bMin + ( bMax - bMin ) * inValues[ i ] ),
				255
			);
		}
		
		return outValues;
	}
	
	public void
	showOptionsDialog()
	{
	}
}