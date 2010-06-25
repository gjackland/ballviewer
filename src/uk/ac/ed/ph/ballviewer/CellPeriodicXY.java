package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

/*
 *	Cell periodic in X and Y with 'free' boundary in z directions
 *
 */
public class CellPeriodicXY extends SystemCell
{
	public
	CellPeriodicXY()
	{
		super();
	}
	
	public CellPeriodicXY(
		final Aabb	aabb
	)
	{
		super( aabb );
	}
	
	public Aabb
	getAabb()
	{
		return aabb;
	}
	
	public Vector3
	getMinimumVector(
		final Tuple3		from,
		final Tuple3		to
	)
	{
    	final Vector3 ab = Vector3.sub( to, from );

       	ab.x = MathUtil.wrapLength( ab.x, aabb.xRange );
       	ab.y = MathUtil.wrapLength( ab.y, aabb.yRange );

       	return ab;
	}
	
	public Vector3
	getCentre()
	{
		return aabb.getCentre();
	}
	
	public CellLattice
	generateCellLattice(
		final double cellsize
	)
	{
		return new CellLatticePeriodicXY( this, cellsize );
	}
}