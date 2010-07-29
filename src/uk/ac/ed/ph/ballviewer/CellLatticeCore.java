package uk.ac.ed.ph.ballviewer;

import java.util.ArrayList;

import uk.ac.ed.ph.ballviewer.math.*;

abstract class CellLatticeCore implements CellLattice
{
	protected double							cellSize;	// minimum size of a
															// cell
	protected double							cx, cy, cz; // sides of a cell =
															// cellSize for
															// ordinary

	protected int								X, Y, Z;	// no. of cells in
															// each direction
	protected ArrayList< Positionable >[][][]	list;		// flexible
															// (growable) array
															// of Objects
															// (including Balls
															// and Arrows)

	protected Aabb								aabb;

	private final SystemCell					supercell;

	public CellLatticeCore( final SystemCell supercell, final double cellSize )
	{
		this.supercell = supercell;
		this.cellSize = cellSize;

		this.aabb = supercell.getAabb();

		getDimensions();
		System.out.println( "Minm cellsize " + cellSize + " Actual " + cx + " " + cy + " " + cz + " L " + cellSize * Y );

		// Work around for java generics problem
		list = ( ArrayList< Positionable >[][][] )java.lang.reflect.Array.newInstance( ArrayList.class, X + 2, Y + 2, Z + 2 );

		setLattice();
	}

	@Override
	public double cellSize()
	{
		return cellSize;
	}

	@Override
	public double X()
	{
		return X;
	}

	@Override
	public double Y()
	{
		return Y;
	}

	@Override
	public double Z()
	{
		return Z;
	}

	abstract protected void getDimensions();

	abstract protected void setLatticeEdges();

	@Override
	public ArrayList< Positionable > getCellObjectList( final int i, final int j, final int k )
	{
		try
		{
			return list[ i ][ j ][ k ];
		}
		catch( IndexOutOfBoundsException e )
		{
			System.err.println( "Index out of bounds: X = " + i + " Y = " + j + " Z = " + k );
			e.printStackTrace();
			return new ArrayList< Positionable >( 0 );
		}
	}

	@Override
	public void add( final Positionable obj )
	{ // add the object, using the appropriate vector3 coordinates:
		final LatticeCoor l = getLatticeCoor( obj.pos() );
		list[ l.x ][ l.y ][ l.z ].add( obj );
	}

	public void setLattice()
	{
		for( int i = 1; i <= X; i++ )
		{
			for( int j = 1; j <= Y; j++ )
			{
				for( int k = 1; k <= Z; k++ )
				{
					list[ i ][ j ][ k ] = new ArrayList< Positionable >( 5 ); // initially
																				// makes
																				// space
																				// for
																				// 5
																				// objects
																				// only
				}
			}
		}
		setLatticeEdges();
	}

	@Override
	public void resetLattice()
	{
		for( int i = 1; i <= X; i++ )
			for( int j = 1; j <= Y; j++ )
				for( int k = 1; k <= Z; k++ )
				{
					list[ i ][ j ][ k ].clear(); // clears Lattice
				}
	}

	// These convert coordinates to integer indices //
	@Override
	public LatticeCoor getLatticeCoor( final Vector3 v )
	{
		return getLatticeCoor( v.x, v.y, v.z );
	}

	@Override
	public LatticeCoor getLatticeCoor( final double x, final double y, final double z )
	{
		final Vector3 min = aabb.getMin();
		final int ix = ( int )( ( x - min.x ) / cx ) + 1;
		final int iy = ( int )( ( y - min.y ) / cy ) + 1;
		final int iz = ( int )( ( z - min.z ) / cz ) + 1;

		return new LatticeCoor( ix, iy, iz );
	}

	/**
	 * Returns the relative positions of the 'n' nearest neighbours (in nearest
	 * 9 cells).
	 */
	@Override
	public Vector3[] getNearestNeighbours( final Ball b, final int n )
	{ // get positions of n nearest neighbours
		int ix, iy, iz, j;
		final SortTree tr = new SortTree();
		final LatticeCoor l = getLatticeCoor( b.pos );

		// first put all the neighbours into a SortTree
		for( ix = l.x - 1; ix <= l.x + 1; ix++ )
		{
			for( iy = l.y - 1; iy <= l.y + 1; iy++ )
			{
				for( iz = l.z - 1; iz <= l.z + 1; iz++ )
				{
					final ArrayList< Positionable > ls = getCellObjectList( ix, iy, iz );
					for( Positionable p : ls )
					{
						// Is it a ball and not the current ball?
						if( p instanceof Ball && p != b )
						{
							final Vector3 v = supercell.getMinimumVector( b.pos(), ( ( Ball )p ).pos() );
							final double vv = v.dot( v );
							tr.addNode( v, vv );
						}
					}
				}
			}
		}
		// then take out the 'n' smallest
		final Vector3[] nn = new Vector3[ n ];
		tr.resetTraverser();
		for( j = 0; j < n; j++ )
		{
			tr.findNextSmallest();
			nn[ j ] = ( Vector3 )tr.object();
		}
		return nn;
	}

	/**
	 * Returns the relative positions of all the neighbours within rr (radius
	 * squared)
	 */

	@Override
	public Vector3[] getNearestNeighbours( final Ball b, final double rr )
	{
		final ArrayList< Vector3 > nn = new ArrayList< Vector3 >();

		// the coordinates of the cell in the lattice //
		final LatticeCoor l = getLatticeCoor( b.pos );
		try
		{
			// the following works if the cell size (step) is greater than r
			// (otherwise modify 1's)
			for( int ix = l.x - 1; ix <= l.x + 1; ix++ )
			{
				for( int iy = l.y - 1; iy <= l.y + 1; iy++ )
				{
					for( int iz = l.z - 1; iz <= l.z + 1; iz++ )
					{
						ArrayList< Positionable > ls = getCellObjectList( ix, iy, iz );
						for( Positionable p : ls )
						{
							// Is it a ball and not the current ball?
							if( p instanceof Ball && p != b )
							{
								final Vector3 v = supercell.getMinimumVector( b.pos(), ( ( Ball )p ).pos() );
								final double vv = v.dot( v );
								if( vv < rr )
								{
									nn.add( v );
								}
							}
						}

					}
				}
			}
		}
		catch( final Exception e )
		{
			System.out.println( b.pos + "" + l + " " + e );

		}

		final Vector3[] n = new Vector3[ nn.size() ];
		nn.toArray( n );
		return n;
	}

	/**
	 * Returns at most 'n' of the relative positions of nbrs within rr (radius
	 * squared).
	 */
	@Override
	public Vector3[] getNearestNeighbours( Ball b, double rr, int n )
	{
		int i;
		// get the neighbours
		Vector3[] v = getNearestNeighbours( b, rr );
		// put them in a sort tree
		SortTree tr = new SortTree();
		for( i = 0; i < v.length; i++ )
		{
			tr.addNode( v[ i ], v[ i ].dot( v[ i ] ) );
		} // set shouldn't have v=0
		tr.resetTraverser();
		// read out and return at most 'n'
		Vector3[] nn = new Vector3[ ( n < v.length ) ? n : v.length ];
		for( i = 0; i < nn.length; i++ )
		{
			tr.findNextSmallest();
			nn[ i ] = ( Vector3 )tr.object();
		}
		return nn;
	}

	/** Trims any excess space in the CellLattice, implying that it is complete. */
	@Override
	public void trimDown()
	{
		for( int i = 1; i <= X; i++ )
			for( int j = 1; j <= Y; j++ )
				for( int k = 1; k <= Z; k++ )
				{
					list[ i ][ j ][ k ].trimToSize();
				}
	}

}