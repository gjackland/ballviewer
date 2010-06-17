package uk.ac.ed.ph.ballviewer.analysis;

import java.awt.Color;
import java.text.DecimalFormat;

import uk.ac.ed.ph.ballviewer.math.*;
import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.util.Options;


public class CrystalAnalyser extends BallAnalyser
{
	public enum StructureType
	{
		UNKNOWN( Color.gray ),
		BCC( Color.blue ),
		FCC( Color.green ),
		HCP( Color.red ),
		ICO( Color.yellow );
	
		private final Color			colour;
		
		public Color	colour() { return colour; }
		
		StructureType( Color colour )
		{
			this.colour		= colour;
		}
	}
	
	// Graphs used to show analysis
	private final RadialHistogram		rad = new RadialHistogram( " big", 0.0, 2.5, 250 );		// for radial test
	private final AngularHistogram		ang = new AngularHistogram( " big", -1.5, 1.5, 150 );	// for angular test
	
	private static final DecimalFormat bigF		= new DecimalFormat(" ######0");
	private static final DecimalFormat smallF	= new DecimalFormat("0");
	private static final DecimalFormat floatF 	= new DecimalFormat("#0.00000");
	
	// Keep a count of the different structure types
	private int[]				count = { 0, 0, 0, 0, 0 };
	// Keep track of the structure type for each ball
	private StructureType[]		ballStructureTypes;
	// Keep track of the last StaticSystem analysed so we only recompute values for a new system
	private StaticSystem		lastSystemAnalysed		= null;
	
	private boolean				printOutput				= false;
	
	public String
	getName()
	{
		return new String( "Crystal Analyser" );
	}
	
	public void
	analyseBalls( StaticSystem sys )
	{
		//if( lastSystemAnalysed != sys )
		//{
			newSystem( sys );
		//}
		
		// Set the color for all the balls
		colorBalls( sys.getBalls() );
	}
	
	private void
	newSystem( StaticSystem newSystem )
	{
		ballStructureTypes	= new StructureType[ newSystem.getBalls().length ];
		for( int i = 0; i < newSystem.getBalls().length; ++i )
		{
			analyseBall( newSystem, i );
		}
		lastSystemAnalysed	= newSystem;
	}
	
	private void
	colorBalls(
		final Ball[]	balls
	)
	{
		for( int i = 0; i < balls.length; ++i )
		{
			balls[ i ].setColour( ballStructureTypes[ i ].colour() );
		}
	}
	
	/** 
	 * Analyses the <code>Ball</code> at the specified index.
	 * Assigns a colour with which it will be displayed next time.
	 */
	private void analyseBall(
		final StaticSystem				sys,
		final int						index
	)
	{
		final Ball[]		p		= sys.getBalls();
		final CellLattice	cells	= sys.getCellLattice();
		
		int nbrs = 0;													// this is the radial part
		LightHistogram ha = new LightHistogram(-1.5,1.5,150);			// for angular distribution
		// we start with a set of neighbours within the cutoff radius
		Vector3[] n = cells.getNearestNeighbours( p[index], sys.RR ); // get neighbours
		
		// this part looks at radii (radius squared)
		if( printOutput )
		{
			System.out.println( index+" "+n.length );
		}
		for (int i=0;i<n.length;i++)
		{ 
			double rr = n[i].dot(n[i]) / sys.AA; // standardise it with AA (A is standard radius - see above)
			rad.add( rr ); 
			if( rr<=1.55 ) nbrs++;			// 1.55 is a 'magic' number - chosen cutoff
		} 
		// the following could possibly be done more efficiently using a smaller no. of bins
		// this part looks at angles (cosine)
		HCPanalyser hcpa = new HCPanalyser();
		for (int i=0;i<n.length;i++)
		{
			Vector3 vi=n[i];
			double ri=vi.modulus();
			if( ri > 0.0d )
			{
				for( int j=0; j<i; j++ )
				{
					Vector3 vj=n[j];
					double rj=vj.modulus();
					if( rj > 0 )
					{
						double cos = vi.dot(vj)/(ri*rj);
						if (-0.90<cos && cos<-0.75) { hcpa.addVectorPair(vi,vj); }	
						ha.add(cos);
					}					
				}
			}
		}
		
		// now for the nasty looking analysis
		int pr	= ha.sum(-1.01,-0.95);  	// the main part of (opposite) "pairs" peak 
		int c1  = ha.sum(-0.94,-0.92);  	// overlap of peak "pairs" with peak "hcp"
		int c2  = ha.sum(-0.91,-0.76);  	// (hcp) only - peak "hcp"
		int c3  = ha.sum(-0.75,-0.71);	// overlap of peak "hcp" with next peak
												// gap in the distribution - too messy to use
		int ctr 	= ha.sum(-0.19,+0.19);	// central peak - 12 or  8 or  5 for bcc; 12 for fcc/hcp
		int xt  	= ha.sum(+0.20,+0.24);	// extra catchment for big (bcc)
		int big 	= ha.sum(+0.25,+0.79);	// near lump - 36 or 32 or 28 for bcc (2peaks); 24 for fcc/hcp 
		int layer2	= ha.sum(+0.80,+1.01);	// very acute angles are assumed to be due to the 
													//  next layer of neighbours sneaking in
		
		int pr1		= pr+c1;				// (bcc) pairs peak is wider to ensure all are caught
		int chcp	= c1+c2+c3;  		// characteristic hcp peak
		int bigx	= big+xt;	
		
		
		// ico has no right angles: sml peak should be empty; sml should be small :) //		
		double icoscore = ctr;
		// bcc expects ratio = 3.0, fcc/hcp expect 2.0 - invert ratio to make bccscore smallest for bcc
		// extra factor to encourage bcc: if 7 pairs of opposites (cos~-1) then add 1.5 to ratio
		double ratio = (double)(bigx)/ctr;
		double bccscore = 0.35/(ratio-1.0); // 0.35 is 'magic' number, 1.0 is minm ratio
		// both fcc&hcp expect 24 in the "acute lump"
		double fcchcp		= discrepancy( new int[]{24},  new int[]{big}	)/24;
		// fcc has a six pairs of opposites, then a gap
		final int[] int_6_0 = new int[]{6,0};
		final int[] int_3_9 = new int[]{3,9};
		double fccscore = 0.61*discrepancy( int_6_0, new int[]{pr1,c2} )/6; // //
		// hcp has 3 pairs of opposites, and 9 when include characteristic peak (0.61 is 'magic' no.) 
		double hcpscore = discrepancy( int_3_9, new int[]{pr,pr+chcp} )/12;
		//special cases: (low score is good) //
		if (pr==7) bccscore =0;   // fairly safe bet that 7 pairs = BCC
		else if (pr==6) fccscore =0; // only come into play if not bcc
		else if (pr<=3) hcpscore =0; //
		
		StructureType decision;
		if( layer2 > 0 )
		{
			decision = StructureType.UNKNOWN;
		}
		else if( icoscore < 3 )
		{
			if( nbrs>13 || nbrs<11 )
			{
				decision = StructureType.UNKNOWN;
			}
			else
			{
				decision = StructureType.ICO;
			}
		}
		else if( bccscore <= fcchcp )
		{
		if( nbrs < 11 ) decision = StructureType.UNKNOWN; 
		else decision = StructureType.BCC;
		}
		else
		{
			if( nbrs>12 || nbrs<11 ) decision = StructureType.UNKNOWN;
			else if( fccscore < hcpscore ) decision = StructureType.FCC;
			else	decision = StructureType.HCP;
		}
		
		// Save the structure type for this ball
		ballStructureTypes[ index ] = decision;
		count[ decision.ordinal() ]++;
		
		Vector3 planeV = hcpa.getPlaneDirection();
		if( decision == StructureType.HCP )
		{ 
			if (planeV.modulus()>0 && Math.random()<0.5)
			{
				planeV.normalise(); planeV.multiply( 0.3 * sys.A );
				cells.add( new Arrow( p[index].getPosition().plus( planeV ), p[index].getPosition().minus( planeV ) ) );
			}
		}

		String outputStr = bigF.format(index)+" "+smallF.format(pr)+" "
		+floatF.format(bccscore)+" "+floatF.format(fcchcp)+" "
		+floatF.format(fccscore)+" "+floatF.format(hcpscore)+" "+ decision;
		if( decision == StructureType.HCP )
		{
			outputStr += " "+planeV;
		}
		outputStr += "\n";
		try { sys.analysisOutput.write(outputStr,0,outputStr.length()); } catch (Exception e) {}
		
		// sum up all the histograms and if the last ball has just been done, show them
		if( nbrs>=12 )
		{ 
			ang.addData(ha); 
		}
		if( index == p.length-1 )
		{
			if( printOutput )
			{
				for( StructureType struct : StructureType.values() )
				{
					System.out.println( struct + " count: " + count[ struct.ordinal() ] );
				}
			}
			rad.draw(); ang.draw(); 
		}
		
	}
	
	AnalyserOutput[]
	getOutputs()
	{
		return null;
	}
	
	void
	updateAttributes( StaticSystem sys )
	{
	}
	
	/*
	 *
	 * This returns the total of differences between expected and observed results 
	 * - small values indicate close correlation, meaning a good score
	*/
	private double
	discrepancy( int[] e, int b[] )
	{
		double sum = 0; 
		int diff;
		for( int i=0; i<e.length; i++ )
		{
			diff = ( e[i]>b[i] ) ? ( e[i]-b[i] ) : ( b[i]-e[i] );
			sum += diff;
		}
		return sum;
	}
	
}