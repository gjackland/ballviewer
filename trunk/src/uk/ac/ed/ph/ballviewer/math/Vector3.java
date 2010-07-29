package uk.ac.ed.ph.ballviewer.math;

// Contains Vector3, Matrix3 and Transform classes //
/** Represents a 3d cartesian vector. */
public class Vector3 extends Tuple3 implements Cloneable
{

	public static Vector3 sub( final Tuple3 a, final Tuple3 b )
	{
		final Vector3 ab = new Vector3( b );
		return ( Vector3 )ab.subtract( a );
	}

	/** Creates a new 0 vector. */
	public Vector3()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/** Creates a new vector. */
	public Vector3( double x, double y, double z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3( final double[] coords )
	{
		if( coords.length != 3 )
		{
			throw new IllegalArgumentException( "Vector3 constructor passed double array that does not contain three elements" );
		}
		x = coords[ 0 ];
		y = coords[ 1 ];
		z = coords[ 2 ];
	}

	public Vector3( final Tuple3 tup )
	{
		set( tup );
	}

	/** Returns a string representation in form (x,y,z). */
	@Override
	public String toString()
	{
		return "(" + this.x + "," + this.y + "," + this.z + ")";
	}

	public Vector3( final Vector3 vec )
	{
		set( vec );
	}

	/**
	 * Creates a cartesian vector from spherical polar coordinates.
	 * 
	 * @param r
	 *            radius
	 * @param th
	 *            the polar angle (theta) in range 0.0 through <i>pi</i>
	 * @param phi
	 *            the azimuthal angle in range 0.0 through 2*<i>pi</i>
	 */
	public static Vector3 spherical( double r, double th, double phi )
	{
		return new Vector3( r * Math.sin( th ) * Math.cos( phi ), r * Math.sin( th ) * Math.sin( phi ), Math.cos( th ) );
	}

	/** Sets the vector to equal <code>v</code>. */
	public void set( Vector3 v )
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	/**
	 * Returns the scalar (<code>double</code>) dot product of the vector with
	 * <code>v</code>.
	 */
	public double dot( Vector3 v )
	{
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	/** Returns the vector cross product of the vector with <code>v</code>. */
	public Vector3 cross( Vector3 v )
	{
		return new Vector3( this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x );
	}

	/** Returns the modulus of the vector. */
	public double modulus()
	{
		return Math.sqrt( x * x + y * y + z * z );
	}

	public double modulusSq()
	{
		return x * x + y * y + z * z;
	}

	/** Normalises the vector. */
	public void normalise()
	{
		this.multiply( 1.0 / this.modulus() );
	}

	/** Returns this unit vector in the direction of this vector. */
	public Vector3 normalised()
	{
		return this.times( 1.0 / this.modulus() );
	}

	/** Returns the sum of this vector and vector v. */
	public Vector3 plus( Vector3 v )
	{
		return new Vector3( this.x + v.x, this.y + v.y, this.z + v.z );
	}

	/** Returns the difference vector from <code>v</code> to this vector. */
	public Vector3 minus( Vector3 v )
	{
		return new Vector3( this.x - v.x, this.y - v.y, this.z - v.z );
	}

	/** Returns the vector multiplied by scalar <code>s</code>. */
	public Vector3 times( double s )
	{
		return new Vector3( this.x * s, this.y * s, this.z * s );
	}

	/** Adds vector <code>v</code> to this vector. */
	public void add( Vector3 v )
	{
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}

	/** Subtracts vector <code>v</code> from this vector. */
	public void subtract( Vector3 v )
	{
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}

	/** Multiplies this vector by scalar <code>s</code>. */
	public void multiply( double s )
	{
		this.x *= s;
		this.y *= s;
		this.z *= s;
	}

	/** Returns the rotation of this vector around the x axis. */
	public Vector3 rotatedX( double phi )
	{
		double cos = Math.cos( phi );
		double sin = Math.sin( phi );
		return new Vector3( x, cos * y - sin * z, sin * y + cos * z );
	}

	/** Returns the rotation of this vector around the y axis. */
	public Vector3 rotatedY( double phi )
	{
		double cos = Math.cos( phi );
		double sin = Math.sin( phi );
		return new Vector3( sin * z + cos * x, y, cos * z - sin * x );
	}

	/** Returns the rotation of this vector around the z axis. */
	public Vector3 rotatedZ( double phi )
	{
		double cos = Math.cos( phi );
		double sin = Math.sin( phi );
		return new Vector3( cos * x - sin * y, sin * x + cos * y, z );
	}

	/** Rotates this vector around the x axis. */
	public void rotateX( double phi )
	{
		this.set( this.rotatedX( phi ) );
	}

	/** Rotates this vector around the y axis. */
	public void rotateY( double phi )
	{
		this.set( this.rotatedY( phi ) );
	}

	/** Rotates this vector around the z axis. */
	public void rotateZ( double phi )
	{
		this.set( this.rotatedZ( phi ) );
	}

	/**
	 * Perspectivises this vector as if the camera is at (0,0,
	 * <code>camdepth</code>). The x,y components represent a pespective
	 * projection onto a flat screen. The z component can be used for
	 * z-buffering. At z=0.0, the scale is 1.0 .
	 **/
	public void perspectivise( double camdepth )
	{
		double zsc = -camdepth / ( z - camdepth ); // so that zsc = 1.0 at z=0;
		this.x *= zsc;
		this.y *= zsc;
	}

	/** Returns the perpective projection of this vector, as above. */
	public Vector3 perspectivised( double camdepth )
	{
		double zsc = -camdepth / ( z - camdepth ); // so that zsc = 1.0 at z=0;
		return new Vector3( this.x * zsc, this.y * zsc, this.z ); // just ignore
																	// the z
																	// component
	}

	@Override
	public Object clone()
	{
		return new Vector3( this.x, this.y, this.z );
	}
}
