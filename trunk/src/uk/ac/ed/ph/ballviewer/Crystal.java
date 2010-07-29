package uk.ac.ed.ph.ballviewer;

import java.awt.*;

import uk.ac.ed.ph.ballviewer.math.*;

/**
 * Produces sets of coordinates of a particular crystal type. <br>
 * The separation is 1.0 .
 */
public final class Crystal
{ // returns datasets of ideal crystals
	static final double	cs	= 1.0;
	static int			X	= 10, Y = 10, Z = 10;

	private Crystal()
	{
	} // This class cannot be instantiated //

	/** Returns a set of 2000 Balls arranged in BCC formation. */
	static public Ball[] BCC()
	{
		double s = cs * 2 / Math.sqrt( 3 );
		int bpc = 2;
		Ball[] p = new Ball[ bpc * X * Y * Z ];

		Vector3 v;
		int i;
		for( i = 0; i < p.length; i++ )
			p[ i ] = new Ball( Color.blue );
		i = 0;
		for( int ix = 0; ix < X; ix++ )
			for( int iy = 0; iy < Y; iy++ )
				for( int iz = 0; iz < Z; iz++ )
				{
					v = new Vector3( ( ix - X / 2 ) * s, ( iy - Y / 2 ) * s, ( iz - Z / 2 ) * s );
					p[ i ].pos = v;
					p[ i + 1 ].pos = v.plus( new Vector3( 0.5 * s, 0.5 * s, 0.5 * s ) );
					i += 2; // =bpc
				}
		return p;
	}

	/** Returns a set of 4000 Balls arranged in FCC formation. */
	static public Ball[] FCC()
	{
		double s = cs * 2 / Math.sqrt( 2 );
		int bpc = 4;
		Ball[] p = new Ball[ bpc * X * Y * Z ];

		Vector3 v;
		int i;
		for( i = 0; i < p.length; i++ )
			p[ i ] = new Ball( Color.blue );
		i = 0;
		for( int ix = 0; ix < X; ix++ )
			for( int iy = 0; iy < Y; iy++ )
				for( int iz = 0; iz < Z; iz++ )
				{
					v = new Vector3( ( ix - X / 2 ) * s, ( iy - Y / 2 ) * s, ( iz - Z / 2 ) * s );
					p[ i ].pos = v;
					p[ i + 1 ].pos = v.plus( new Vector3( 0.5 * s, 0.5 * s, 0.0 * s ) );
					p[ i + 2 ].pos = v.plus( new Vector3( 0.5 * s, 0.0 * s, 0.5 * s ) );
					p[ i + 3 ].pos = v.plus( new Vector3( 0.0 * s, 0.5 * s, 0.5 * s ) );
					i += 4; // =bpc
				}
		return p;
	}

	/** Returns a set of 4000 Balls arranged in HCP formation. */
	static public Ball[] HCP()
	{
		double sx = cs * 1.0; // this crystal has a cuboid cell, not cube
		double sy = cs * Math.sqrt( 3.0 );
		double sz = cs * 1.59;// Math.sqrt(8.0/3);
		int bpc = 4;
		Ball[] p = new Ball[ bpc * X * Y * Z ];

		Vector3 v;
		int i;
		for( i = 0; i < p.length; i++ )
			p[ i ] = new Ball( Color.red );
		i = 0;
		for( int ix = 0; ix < X; ix++ )
			for( int iy = 0; iy < Y; iy++ )
				for( int iz = 0; iz < Z; iz++ )
				{
					v = new Vector3( ( ix - X / 2 ) * sx, ( iy - Y / 2 ) * sy, ( iz - Z / 2 ) * sz );
					p[ i ].pos = v;
					p[ i + 1 ].pos = v.plus( new Vector3( 0.5 * sx, 0.5 * sy, 0.0 * sz ) );
					p[ i + 2 ].pos = v.plus( new Vector3( 0.0 * sx, sy / 3, 0.5 * sz ) );
					p[ i + 3 ].pos = v.plus( new Vector3( 0.5 * sx, 5 * sy / 6, 0.5 * sz ) );
					i += 4; // =bpc
				}
		return p;
	}

	/**
	 * Returns a set of 13 Balls arranged in icosahedral formation around
	 * central Ball.
	 */
	static public Ball[] ICO()
	{
		double s = 3 * cs;
		final double theta = 1.10714871779409; // cos(th)=
												// cos(pi/5)/(cos(pi/5)+1)
		Ball[] p = new Ball[ 13 ];
		double phi;
		int i;
		Vector3 v;

		for( i = 0; i < p.length; i++ )
			p[ i ] = new Ball( Color.red );

		p[ 0 ].pos = new Vector3( 0, 0, 0 );
		p[ 1 ].pos = new Vector3( 0, 0, 1 );
		p[ 2 ].pos = new Vector3( 0, 0, -1 );
		for( i = 0; i < 5; i++ )
		{
			phi = 2 * i * Math.PI / 5; // == i*72 degrees
			v = Vector3.spherical( +1.0, theta, phi );
			p[ 3 + i ].pos = v; // one here
			p[ 3 + i + 5 ].pos = v.times( -1 ); // and one on opposite side
		}
		return p;
	}

}