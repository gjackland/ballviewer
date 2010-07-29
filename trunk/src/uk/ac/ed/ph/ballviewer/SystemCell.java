package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

/*
 *	Abstract class to represent a system supercell.
 *
 */
public abstract class SystemCell
{
	protected final Aabb	aabb;

	public SystemCell()
	{
		// Default cell has object coordinates in range 0->1
		aabb = new Aabb( new Vector3( 0d, 0d, 0d ), new Vector3( 1d, 1d, 1d ) );
	}

	public SystemCell( final Aabb aabb )
	{
		this.aabb = aabb;
	}

	public abstract Aabb getAabb();

	public abstract Vector3 getCentre();

	public abstract Vector3 getMinimumVector( final Tuple3 from, final Tuple3 to );

	public double getMinimumDistance( final Tuple3 from, final Tuple3 to )
	{
		return getMinimumVector( from, to ).modulus();
	}

	public double getMinimumDistanceSq( final Tuple3 from, final Tuple3 to )
	{
		return getMinimumVector( from, to ).modulusSq();
	}

	public abstract CellLattice generateCellLattice( final double cellSize );

}