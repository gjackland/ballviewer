package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

/*
 *	Cell with free boundaries in all X, Y and Z.
 *
 */
public class CellOrdinary extends SystemCell
{

	public CellOrdinary()
	{}	
	
	public
	CellOrdinary(
		final Aabb	aabb
	)
	{
		super( aabb );
	}
	
	@Override
	public Aabb
	getAabb()
	{
		return aabb;
	}
	
	@Override
	public Vector3
	getMinimumVector(
		final Tuple3		from,
		final Tuple3		to
	)
	{
    	return Vector3.sub( to, from );
	}
	
	@Override
	public Vector3
	getCentre()
	{
		return aabb.getCentre();
	}
	
	@Override
	public CellLattice
	generateCellLattice(
		final double cellsize
	)
	{
		return new CellLatticeOrdinary( this, cellsize );
	}
}