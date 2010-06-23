package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.math.*;

/**  
 * References Objects in a 3d arrangement to allow neighbours to be found quickly. 
 * Contains a 3d array of ObjectLists, corresponding to a specifed 3d range (cuboid) 
 * which is divided into equal cells of a specified size. 
 */ 
public interface CellLattice
{  // all the methods that can be accessed from outside 
	public double cellSize(); 
	public double X(); 
	public double Y(); 
	public double Z(); 
	public void resetLattice(); 
	public void trimDown(); 
	public LatticeCoor getLatticeCoor( final Vector3 v ); 
	public LatticeCoor getLatticeCoor( final double x, final double y, final double z);
	public void add( final Positionable obj ); 
	public ArrayList< Positionable > getCellObjectList(int i,int j,int k); 
	public Vector3[] getNearestNeighbours(Ball b, int n); 
	public Vector3[] getNearestNeighbours(Ball b, double RR, int n); 
	public Vector3[] getNearestNeighbours(Ball b, double RR); 
}


 
class CellLatticePeriodic extends CellLatticeCore
{ 
	 
	/** Creates a new periodic cell lattice with cells of size cs, covering the 3d range given. 
	 * This range MUST be the periodic box. cellSize is increased to  */ 
	public CellLatticePeriodic(
		final SystemCell	supercell,
		final double 		cs
	)
	{ 
		super( supercell, cs );
		System.out.println("Periodic Lattice constructed "+X+" "+Y+" "+Z); 
	} 
	
	protected void
	getDimensions() { 
		X = ( int )Math.ceil( aabb.xRange / cellSize );	cx = aabb.xRange / X; 
		Y = ( int )Math.ceil( aabb.yRange / cellSize );	cy = aabb.yRange / Y; 
		Z = ( int )Math.ceil( aabb.zRange / cellSize );	cz = aabb.zRange / Z; 
	} 
	
	protected void
	setLatticeEdges()
	{ 
		for( int i=0; i<=X+1; i++ )
		{
			for (int j=0; j<=Y+1; j++)
			{ 
				wrapcell(i,j,0); wrapcell(i,j,Z+1);
			}
		} 
		for( int i=0; i<=X+1; i++)
		{
			for (int k=0; k<=Z+1; k++)
			{ 
				wrapcell(i,0,k); wrapcell(i,Y+1,k);
			}
		} 
		for( int j=0; j<=Y+1; j++)
		{
			for (int k=0; k<=Z+1; k++)
			{ 
				wrapcell(0,j,k); wrapcell(X+1,j,k); 
			}
		} 
	} 
	
	public LatticeCoor
	getLatticeCoor(
		final double x,
		final double y,
		final double z
	)
	{ 
		final Vector3 min = aabb.getMin();
		int ix = (int)( ( x - min.x ) / cx ) + 1;
		int iy = (int)( ( y - min.y ) / cy ) + 1;
		int iz = (int)( ( z - min.z ) / cz ) + 1;
		
		// Fix up for periodic boundary
		if( ix < 1 ) ix+= X; else if (ix > X) ix-= X; 
		if( iy < 1 ) iy+= Y; else if (iy > Y) iy-= Y; 
		if( iz < 1 ) iz+= Z; else if (iz > Z) iz-= Z;
		
		return new LatticeCoor( ix,iy,iz );
	} 
 
	private boolean
	wrapcell(
		final int i, 
		final int j, 
		final int k
	)
	{ 
		int ip, jp, kp; 
		//if (list[i][j][k]==null)
		//{
			if (i==0) ip=X; else if (i==X+1) ip=1; else ip = i; 
			if (j==0) jp=Y; else if (j==Y+1) jp=1; else jp = j; 
			if (k==0) kp=Z; else if (k==Z+1) kp=1; else kp = k; 
			list[i][j][k] = list[ip][jp][kp]; 
			return true;
		//}
		//System.out.println( "Didn't wrap " + i + " " + j + " " + k + " was not null" );
		//return false;
	} 
			 
}

/*
 *	Class that represents lattice coordinates - immutable.
 *
 */
class LatticeCoor
{ 
	public final int x,y,z; 
	public LatticeCoor( int ix, int iy, int iz ){ x = ix; y = iy; z = iz; } 
	public String toString() { return "["+x+","+y+","+z+"]"; } 
} 
  
	
