package uk.ac.ed.ph.ballviewer;

import java.io.*;
import java.awt.*;
import java.text.*;
import java.util.*;

import uk.ac.ed.ph.ballviewer.math.*;

/** Representation of a static arrangement balls. */
public class StaticSystem
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
	
	/** Array containing the complete set of balls/particles. */
	public			Ball[]				p;
	/** Arrangement of objects (including balls) into 3d lattice of cells. */
	private			CellLattice			cells;
	/** Indicates whether the data is raw (true) or processed (false). */
	public			boolean				shouldAnalyse;
	private			boolean				determineDimensions	= true;
	
	private			SystemProperties	properties;
	
	public BufferedWriter analysisOutput;
	public double		A,AA	= 1.0;   // will be the best estimate of angular separation
	public double		R		= 1.27;  // these may change when we process the input
	public double		RR		= R * R;

	
	public StaticSystem()
	{
		properties	= new SystemProperties();
	}
	
	public StaticSystem(
		final SystemProperties	properties
	)
	{
		this.properties = properties;
	}
	
	public void
	setSystemProperties(
		final SystemProperties	newProperties
	)
	{
		properties	= newProperties;
	}
	
	public void
	setR( final double newR )
	{
		R = newR;
	}
	
	public boolean
	isDetermineDeimensions()
	{
		return determineDimensions;
	}
	
	public void
	determineDimensions( final boolean shouldDetermine )
	{
		determineDimensions = shouldDetermine;
	}
	
	public CellLattice
	getCellLattice()
	{
		return cells;
	}
	
	public Ball[]
	getBalls()
	{
		return p;
	}
	
	
	public void
	initialise()
	{
		final SystemCell	supercell	= properties.getSupercell();
		final Aabb			aabb		= supercell.getAabb();

		// Should we determine the dimensions of the system or have they already been caluclated
		if( determineDimensions )
		{
			for( Ball b : p )
			{  
				final Vector3 v = b.pos();
				aabb.grow( v );
			}
			System.out.println("min,max done "+ new Date());
		}

		final Vector3 min = aabb.getMin();
		final Vector3 max = aabb.getMax();		
		System.out.println( min.x + " " + max.x );
		System.out.println( min.y + " " + max.y );
		System.out.println( min.z + " " + max.z );
		
		if( shouldAnalyse )
		{  // that is, if the colour-coding isnt already done
			final double n = 10; // avg ptcls/cell - bigger is safer (varied density?) but slower
			double cs = Math.pow( n * aabb.xRange * aabb.yRange * aabb.zRange / p.length, 1.0 / 3 ); // corresponding cell size
			// this rough lattice is need to support the analysis of the standard radius,
			//  but it isn't efficient enough to use for the full analysis (millions of balls?)
			cells = supercell.generateCellLattice( cs );
			
			for (int i=0;i<p.length;i++) cells.add(p[i]); //add all the ptcls
			cells.trimDown(); // and tidy up the lattice - it's ready for use
			System.out.println("temporary lattice constructed: "+new Date());
			analyseStandardRadius(); // see below for method
			System.out.println("radius estimated: "+new Date());
			cells = null; System.gc(); // this is to dump the temporary cell Lattice
			System.out.println("lattice recycled: "+new Date());
			
			try { analysisOutput = new BufferedWriter(new FileWriter("analysis.out")); } 
			catch (Exception e) {}
		}

		// below is the real thing - cells are cubes of length R, where R is the cut-off radius
		//  so all neighbours must be in same cell or neighbouring cell (+-1 for x,y,z)
		//	 also note for same reason there is an extra empty cell thickness at each edge 
		//  for periodic bc's the opposite edges could reference the same ObjectList
		cells = supercell.generateCellLattice( R );
		
		for (int i=0;i<p.length;i++) cells.add(p[i]);
		cells.trimDown();
	}
	
	
	private void messEmUp( double r )
	{  // this jostles the balls by a random amount
		Vector3 d;
		for (int i=0;i<p.length;i++) {
			d = new Vector3 ((Math.random()-0.5)*r,(Math.random()-0.5)*r,(Math.random()-0.5)*r);
			while (d.modulus()>r/2) 
				d = new Vector3 ((Math.random()-0.5)*r,(Math.random()-0.5)*r,(Math.random()-0.5)*r);
			p[i].pos.add(d);
			p[i].setColour( Color.gray );
		}
	}	
		
	// This estimates the standard separation A; bcc is most likely to mess this up, 
	//  so we get a ballpark radius, then use it to get a more precise one
	private void analyseStandardRadius()
	{  
		double sum=0;
		int total=0;
		Vector3 v[];
		int i,t,j;
		
		for( t=0; t<100; t++ )
		{  // 100 samples for rough one (old version only used this)
			i = (int)( Math.random() * p.length );
			v = cells.getNearestNeighbours( p[i], 8 ); // get 8 closest neighbours (should be ok for bcc)
			for (j=0;j<v.length;j++)
			{
				sum+=v[j].modulus(); total++;
			}
		}		
		double tempR = 1.15*sum/total; // give 1.15 leeway; this should help filter nasty edge results
		double tempRR = tempR*tempR;
		System.out.println("Rough radius estimate: "+tempR);
		
		sum=0.0; total =0;
		for (t=0;t<p.length/10;t++) { // 10% samples for good one
			i = (int) (Math.random()*p.length);
			v = cells.getNearestNeighbours(p[i],tempRR,12); 			// up to 12 nbrs within tempR 
			for (j=0;j<v.length;j++) { sum+=v[j].modulus(); total++; }	// (should give 8 for bcc)
		}
		A = sum/total; AA=A*A;
		R *= A;		// ie R has units of A
		RR	*= AA;	// and R.R has units of A.A
		System.out.println("Standard radius estimate: "+A+" averaged over "+total);
	}

	
	/** 
	 * Saves the results of the analysis.
	 * Creates a .dun file. Assumes the analysis has actually been done.
	 * @see #StaticSystem(java.lang.String)
	 */
	public void storeResults(String filename) { // Stores the ball positions and colours
		DataOutputStream output;						// may one day do arrows also
		try {
			output = new DataOutputStream(new FileOutputStream(filename));
			output.writeInt(p.length);
			output.writeFloat((float)R);
			for (int i=0;i<p.length;i++) {
				output.writeFloat((float)p[i].pos.x);
				output.writeFloat((float)p[i].pos.y);
				output.writeFloat((float)p[i].pos.z);
				StructureType tp = StructureType.UNKNOWN;
				if( p[i].getColour() == Color.blue) tp = StructureType.BCC; 
				else if ( p[i].getColour() == Color.green) tp = StructureType.FCC;
				else if ( p[i].getColour() == Color.red) tp = StructureType.HCP; 
				else if ( p[i].getColour() == Color.yellow) tp = StructureType.ICO;
				output.writeInt( tp.ordinal() );
			}
			output.close();
		}
		catch (IOException io) {
			System.err.println("Error saving results:\n" + io.toString() ); 
		}
	}
	
	/*
	 *	Get the properties of the system.
	 *
	 *
	 */
	public SystemProperties
	getSystemProperties()
	{
		return properties;
	}
	
	/*
	 *	Return the center of the system
	 *
	 */
	public Vector3
	getCentre()
	{
		return properties.getSupercell().getCentre();
	}
	
}
	
            
