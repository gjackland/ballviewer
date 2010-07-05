package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.util.Lib;

/*
 *	Ordinary cell lattice with no boundaries
 *
 */
class CellLatticeOrdinary extends CellLatticeCore
{ 
	/** Creates a new CellLattice with cells of size cs, covering the 3d range given.*/ 
	public CellLatticeOrdinary(
		final SystemCell	supercell,
		final double 		cellSize
	)
	{
		super( supercell, cellSize ); 
		System.out.println("Ordinary Lattice "+X+" "+Y+" "+Z); 
	} 

	@Override	 
	protected void
	getDimensions()
	{ 
		X = ( int )Lib.max( Math.floor( aabb.xRange / cellSize ), 1.0 );	cx = aabb.xRange / X; 
		Y = ( int )Lib.max( Math.floor( aabb.yRange / cellSize ), 1.0 );	cy = aabb.yRange / Y; 
		Z = ( int )Lib.max( Math.floor( aabb.zRange / cellSize ), 1.0 );	cz = aabb.zRange / Z; 
	}
	
	@Override
	protected void
	setLatticeEdges()
	{ 
		for (int i=0; i<=X+1; i++)
		{
			for (int j=0; j<=Y+1; j++)
			{ 
				list[i][j][0]= new ArrayList< Positionable >( 0 ); list[i][j][Z+1]= new ArrayList< Positionable >(0); 
			}
		}
		for (int i=0; i<=X+1; i++)
		{
			for (int k=0; k<=Z+1; k++)
			{ 
				list[i][0][k]= new ArrayList< Positionable >(0); list[i][Y+1][k]= new ArrayList< Positionable >(0); 
			} 
		}
		for (int j=0; j<=Y+1; j++)
		{
			for (int k=0; k<=Z+1; k++)
			{ 
				list[0][j][k]= new ArrayList< Positionable >(0); list[X+1][j][k]= new ArrayList< Positionable >(0);
			}
		}
	} 
}