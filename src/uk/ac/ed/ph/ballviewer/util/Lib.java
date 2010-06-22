package uk.ac.ed.ph.ballviewer.util;


public class Lib
{
	private
	Lib() {};	// Can't instantiate
	
	/**
	 *
	 *	Checks if two classes are compatible.  In the case of a primitive and the corresponding
	 *	java class type (e.g. double and Double) this will return true, false otherwise.  For
	 *	non primitive types a standard equals comparison will be performed.
	 *
	 */
	public static boolean
	areClassesCompatible(
		final Class		a,
		final Class 	b
	)
	{
		// Using exclusive or so if they are both primitive we resort to standard equals method
		if( a.isPrimitive() ^ b.isPrimitive() )
		{
			return a.getSimpleName().equalsIgnoreCase( b.getSimpleName() );
		}
		else
		{
			return a.equals( b );
		}
	}
}