package uk.ac.ed.ph.ballviewer.math;

import uk.ac.ed.ph.ballviewer.*;


/*
 *	Represents an Axis Aligned Bounding Box
 *
 */
public class Aabb
{
	private final 	Vector3		min		= new Vector3();
	private final 	Vector3		max		= new Vector3();
	public			double 		xRange;
	public			double 		yRange;
	public 			double 		zRange;
	
	
	public Aabb(
		final Vector3			min,
		final Vector3			max
	) throws IllegalArgumentException
	{
		if( min.x > max.x || min.y > max.y || min.z > max.z )
		{
			throw new IllegalArgumentException( "All components of min have to less than that of max" );
		}
		
		this.min.set( min );
		this.max.set( max );
		
		xRange = max.x - min.x;
		yRange = max.y - min.y;
		zRange = max.z - min.z;
	}
	
	public Aabb(
		final Matrix3			boxMatrix
	)
	{
		final Vector3 e1 = new Vector3( boxMatrix.r[ 0 ] );
		final Vector3 e2 = new Vector3( boxMatrix.r[ 1 ] );
		final Vector3 e3 = new Vector3( boxMatrix.r[ 2 ] );
		
		max.set( e1.plus( e2 ).plus( e3 ) );
		
		xRange = max.x - min.x;
		yRange = max.y - min.y;
		zRange = max.z - min.z;
	}
	
	
	
	public Vector3
	getMin()
	{
		return min;
	}
	
	public Vector3
	getMax()
	{
		return max;
	}
	
	public double
	getXRange()
	{
		return xRange;
	}
	
	public double
	getYRange()
	{
		return yRange;
	}
	
	public double
	getZRange()
	{
		return zRange;
	}
	
	public Vector3
	getCentre()
	{
		return max.minus( min ).times( 0.5d ).plus( min );
	}
	
	
	/*
	 *	Move the whole box by a translation vector.
	 *
	 */
	public void
	translate(
		final Vector3	translateBy
	)
	{
		min.add( translateBy );
		max.add( translateBy );
	}
	
	/*
	 *	Grow the bounding box to encompass the passed in point if it falls outside the current box.
	 *
	 */
	public void
	grow(
		final Vector3	newPoint,
		final double 	epsilon
	)
	{
		// X
		final double minX = newPoint.x - epsilon;
		final double maxX = newPoint.x + epsilon;
		if( minX < min.x )
		{
			xRange += min.x - minX;
			min.x = minX;
		}
		else if( ( newPoint.x + epsilon ) > max.x )
		{
			xRange += maxX - max.x;
			max.x = maxX;
		}
		// Y
		final double minY = newPoint.y - epsilon;
		final double maxY = newPoint.y + epsilon;
		if( newPoint.y < min.y )
		{
			yRange += min.y - minY;
			min.y = minY;
		}
		else if( newPoint.y > max.y )
		{
			yRange += maxY - max.y;
			max.y = maxY;
		}
		// Z
		final double minZ = newPoint.z - epsilon;
		final double maxZ = newPoint.z + epsilon;		
		if( newPoint.z < min.z )
		{
			zRange += min.z - minZ;
			min.z = minZ;
		}
		else if( newPoint.z > max.z )
		{
			zRange += maxZ - max.z;
			max.z = maxZ;
		}

	}
}