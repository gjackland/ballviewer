package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

/* This class takes as input pairs of vectors that are involved in producing the characteristic
 * angle of HCP. Half of these 6 vectors (and 6 pairs) will be on one side of the plane, 
 * (arbitrarily denoted P for positive in the code) and half will be on the other side (denoted N for
 * negative). Each pair input will have one vector on either side of the plane (see [some kind of 
 * diagram]) and each vector will appear in two pairs. Since each vector can only appear on one side
 * of the plane, it is possible to 'go round the loop' and classify each vector appropriately.
 * Then, all the P-vectors are summed (twice, because it's simpler), and also the N-vectors. 
 * The normal to the plane ideally runs along the difference between these two vector sums, 
 * so that vector difference is returned to the main program.
 */
public class HCPanalyser
{
	static final int	P		= +1;	// positive (arbitrary set)
	static final int	unknown	= 0;	// undefined
	static final int	N		= -1;	// negative (opposite exclusive set)

	public int			vpindex;
	VectorPair[]		vp;
	Vector3				vP, vN;

	/*
	 * Creates a new instance of the HCPanalyser. Makes space for 6 pairs of
	 * vectors.
	 */
	public HCPanalyser()
	{
		vp = new VectorPair[ 6 ];
		vpindex = 0;
		vN = new Vector3();
		vP = new Vector3();
	}

	/*
	 * Adds a new vector pair, if there is enough space. Also keeps track of the
	 * number of attempted entries. Assumes this vector pair are involved in the
	 * characteristic HCP angle around a central particle.
	 */
	public void addVectorPair( Vector3 v1, Vector3 v2 )
	{
		if( vpindex < 6 )
			vp[ vpindex++ ] = new VectorPair( v1, v2 );
		else
			vpindex++; // keep track of too many entries but don't add to full
						// array
	}

	/* Obtains a plane direction from the 6 vector pairs entered. */
	public Vector3 getPlaneDirection()
	{
		if( vpindex != 6 )
			return new Vector3();

		vp[ 0 ].polarity = P; // begin by arbitrarily setting one pair then
								// works to set all the others
		for( int i = 0; i < 6; i++ )
			if( vp[ i ].polarity != unknown )
			{
				for( int j = 0; j < 6; j++ )
					if( i != j )
					{
						if( vp[ i ].v1 == vp[ j ].v1 || vp[ i ].v2 == vp[ j ].v2 )
							vp[ j ].polarity = vp[ i ].polarity;
						else if( vp[ i ].v1 == vp[ j ].v2 || vp[ i ].v2 == vp[ j ].v1 )
							vp[ j ].polarity = -vp[ i ].polarity;
					}
			}
		for( int i = 0; i < 6; i++ )
		{ // then it separates these into two sets of vectors, which get summed
			if( vp[ i ].polarity == P )
			{
				vP.add( vp[ i ].v1 );
				vN.add( vp[ i ].v2 );
			}
			else if( vp[ i ].polarity == N )
			{
				vN.add( vp[ i ].v1 );
				vP.add( vp[ i ].v2 );
			}
			else
				return new Vector3(); // ie something is wrong if neither P nor
										// N
		}

		return vP.minus( vN ); // the difference between the vectors is in the
								// direction of the plane
	}
}

class VectorPair
{
	public int		polarity;
	public Vector3	v1;
	public Vector3	v2;

	public VectorPair( Vector3 in1, Vector3 in2 )
	{
		polarity = HCPanalyser.unknown;
		v1 = in1;
		v2 = in2;
	}
}
