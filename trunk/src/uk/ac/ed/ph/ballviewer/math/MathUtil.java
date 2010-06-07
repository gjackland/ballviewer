package uk.ac.ed.ph.ballviewer.math;


public class MathUtil
{
	private MathUtil() {}		// Can't construct
	
	
	final static double
	min(
		final double a,
		final double b
	)
	{
		return a < b ? a : b;
	}
	
	final static double
	max(
		final double a,
		final double b
	)
	{
		return a > b ? a : b;
	}
	
	/*
	 *	Wrap a length, l, to give the shortest distance on the
	 *	number line between 0 and l given that wrapping is allowed
	 *	in both directions at the boundaries 0->range
	 *
	 */
	public static 		double
	wrapLength( final double l, final double range )
	{
		double result = l % range;
		final double halfRange = range / 2.0;
		if( result > halfRange )
		{
			result -= range;
		}
		else if( result <= -halfRange )
		{
			result += range;
		}
		return result;


//		//double result = Math.abs( l );
//		final double 	halfRange		= range / 2.0;
//		final double	lIntoHalfRange	= Math.floor( l / halfRange );
//		return /*Math.signum( l ) */ ( l - halfRange * ( lIntoHalfRange + ( lIntoHalfRange % 2 ) ) );

//		double numToSubtract = Math.rint( l / range );
//		return l - numToSubtract * range;
		
	}
}