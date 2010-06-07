package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

public abstract class SystemCell
{
	public abstract Aabb
	getAabb();
	
	public abstract Vector3
	getCentre();
	
	public abstract Vector3
	getMinimumVector( final Tuple3 from, final Tuple3 to );
	
	public abstract double
	getMinimumDistance( final Tuple3 from, final Tuple3 to );
	
	public abstract double
	getMinimumDistanceSq( final Tuple3 from, final Tuple3 to );
	
	public abstract CellLattice
	generateCellLattice( final double cellSize );
	
}