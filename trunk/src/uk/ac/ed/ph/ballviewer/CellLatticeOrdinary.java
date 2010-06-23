package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

/*
 *	Ordinary cell lattice with no boundaries
 *
 */
class CellLatticeOrdinary extends CellLatticeCore
{ 
	/** Creates a new CellLattice with cells of size cs, covering the 3d range given.*/ 
	public CellLatticeOrdinary(
		final SystemCell	supercell,
		final double 		cs
	)
	{
		super( supercell, cs ); 
		System.out.println("Ordinary Lattice "+X+" "+Y+" "+Z); 
	} 

	@Override	 
	protected void
	getDimensions()
	{ 
		X = (int)Math.ceil( aabb.xRange / cellSize );	 
		Y = (int)Math.ceil( aabb.yRange / cellSize );	 
		Z = (int)Math.ceil( aabb.zRange / cellSize );
		cx = cy = cz = cellSize; 
	}
	
	@Override
	protected void
	setLatticeEdges()
	{ 
		for (int i=0; i<=X+1; i++) for (int j=0; j<=Y+1; j++) { 
			list[i][j][0]= new ArrayList< Positionable >( 0 ); list[i][j][Z+1]= new ArrayList< Positionable >(0); 
		} 
		for (int i=0; i<=X+1; i++) for (int k=0; k<=Z+1; k++) { 
			list[i][0][k]= new ArrayList< Positionable >(0); list[i][Y+1][k]= new ArrayList< Positionable >(0); 
		} 
		for (int j=0; j<=Y+1; j++) for (int k=0; k<=Z+1; k++) { 
			list[0][j][k]= new ArrayList< Positionable >(0); list[X+1][j][k]= new ArrayList< Positionable >(0); 
		} 
	} 
}