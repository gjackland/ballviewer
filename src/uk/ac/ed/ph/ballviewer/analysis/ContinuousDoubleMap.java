package uk.ac.ed.ph.ballviewer.analysis;

class ContinuousDoubleMap extends ContinuousOutputMap
{
	private double	min	= 0d;
	private double	max	= 10d;

	ContinuousDoubleMap( final ContinuousAnalyserOutput analyserOutput )
	{
		super( analyserOutput );
	}

	@Override
	Object[] mapValues( double[] inValues )
	{
		final Object[] outValues = new Object[ inValues.length ];

		for( int i = 0; i < inValues.length; ++i )
		{
			outValues[ i ] = new Double( ( max - min ) * inValues[ i ] + min );
		}

		return outValues;
	}

	@Override
	public void showOptionsDialog()
	{
	}
}