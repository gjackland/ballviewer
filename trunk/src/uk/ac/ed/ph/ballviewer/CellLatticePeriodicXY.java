package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.math.*;

public class CellLatticePeriodicXY extends CellLatticeCore
{

	/**
	 * Creates a new periodic cell lattice with cells of size cs, covering the
	 * 3d range given. This range MUST be the periodic box. cellSize is
	 * increased to
	 */
	public CellLatticePeriodicXY( final SystemCell supercell, final double cs )
	{
		super( supercell, cs );
		System.out.println( "Periodic XY Lattice constructed " + X + " " + Y + " " + Z );
	}

	@Override
	protected void getDimensions()
	{
		X = ( int )Math.ceil( aabb.xRange / cellSize );
		cx = aabb.xRange / X;
		Y = ( int )Math.ceil( aabb.yRange / cellSize );
		cy = aabb.yRange / Y;
		Z = ( int )Math.ceil( aabb.zRange / cellSize );
		cz = aabb.zRange / Z;
	}

	@Override
	protected void setLatticeEdges()
	{
		// Bordering X planes have no objects in them
		for( int i = 0; i <= X + 1; i++ )
		{
			for( int j = 0; j <= Y + 1; j++ )
			{
				list[ i ][ j ][ 0 ] = new ArrayList< Positionable >( 0 );
				list[ i ][ j ][ Z + 1 ] = new ArrayList< Positionable >( 0 );
			}
		}
		// Wrap up Y
		for( int i = 0; i <= X + 1; i++ )
		{
			for( int k = 0; k <= Z + 1; k++ )
			{
				wrapcell( i, 0, k );
				wrapcell( i, Y + 1, k );
			}
		}
		// Wrap up X
		for( int j = 0; j <= Y + 1; j++ )
		{
			for( int k = 0; k <= Z + 1; k++ )
			{
				wrapcell( 0, j, k );
				wrapcell( X + 1, j, k );
			}
		}
	}

	@Override
	public LatticeCoor getLatticeCoor( final double x, final double y, final double z )
	{
		final Vector3 min = aabb.getMin();
		int ix = ( int )( ( x - min.x ) / cx ) + 1;
		int iy = ( int )( ( y - min.y ) / cy ) + 1;
		int iz = ( int )( ( z - min.z ) / cz ) + 1;

		// Fix up for periodic boundary
		if( ix < 1 )
			ix += X;
		else if( ix > X )
			ix -= X;
		if( iy < 1 )
			iy += Y;
		else if( iy > Y )
			iy -= Y;

		return new LatticeCoor( ix, iy, iz );
	}

	private boolean wrapcell( final int i, final int j, final int k )
	{
		int ip = i, jp = j, kp = k;
		// if (list[i][j][k]==null)
		// {
		if( i == 0 )
			ip = X;
		else if( i == X + 1 )
			ip = 1;
		if( j == 0 )
			jp = Y;
		else if( j == Y + 1 )
			jp = 1;
		list[ i ][ j ][ k ] = list[ ip ][ jp ][ kp ];
		return true;
		// }
		// System.out.println( "Didn't wrap " + i + " " + j + " " + k +
		// " was not null" );
		// return false;
	}

}