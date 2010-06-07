package uk.ac.ed.ph.ballviewer;

import uk.ac.ed.ph.ballviewer.math.*;

/**
 * Represents an arrow for 3d drawing. 
 */
public class Arrow implements Positionable {
	public Vector3 v1,v2;
	public Vector3 ctr;
	/** Constructs a new Arrow between the two neighbours of Ball b. */
	public Arrow(Ball b, Vector3 n1, Vector3 n2) {
		Vector3 q = n2.minus(n1).times(0.25);
		v1 = b.pos.plus(n1).plus(q); 
		v2 = b.pos.plus(n2).minus(q);
		ctr = v1.plus(q);
	}
	/** Constructs a new Arrow between the two vectors vec1 and vec2 */
	public Arrow(Vector3 vec1, Vector3 vec2) {
		v1 = vec1;
		v2 = vec2;
		ctr= v1.plus(v2).times(0.5);
	}
	
	public Vector3 pos() { return ctr; }
	public void move(double x, double y, double z) { move(new Vector3(x,y,z)); }
	public void move(Vector3 v) { ctr.add(v) ; v1.add(v); v2.add(v); }
	public Object copy() { return new Arrow ((Vector3)this.v1.clone(), (Vector3)this.v2.clone()); }
}