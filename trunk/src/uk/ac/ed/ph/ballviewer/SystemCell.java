package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

/*
 *	Abstract class to represent a system supercell.
 *
 */
public abstract class SystemCell
{
	public abstract Aabb
	getAabb();
	
	public abstract Vector3
	getCentre();
	
	public abstract Vector3
	getMinimumVector(
		final Tuple3	from,
		final Tuple3	to
	);
	
	public double
	getMinimumDistance(
		final Tuple3		from,
		final Tuple3		to
	)
	{
		return getMinimumVector( from, to ).modulus();
	}
	
	public double
	getMinimumDistanceSq(
		final Tuple3		from,
		final Tuple3		to
	)
	{
    	return getMinimumVector( from, to ).modulusSq();
	}
	
	public abstract CellLattice
	generateCellLattice( final double cellSize );
	
}