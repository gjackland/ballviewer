package uk.ac.ed.ph.ballviewer.math;

public class Point3 extends Tuple3
{

	public double	x;
	public double	y;
	public double	z;

	/** Creates a new 0 vector. */
	public Point3()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/** Creates a new vector. */
	public Point3( double x, double y, double z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** Returns a string representation in form (x,y,z). */
	@Override
	public String toString()
	{
		return "(" + this.x + "," + this.y + "," + this.z + ")";
	}

	@Override
	public Object clone()
	{
		return new Point3( this.x, this.y, this.z );
	}
}
