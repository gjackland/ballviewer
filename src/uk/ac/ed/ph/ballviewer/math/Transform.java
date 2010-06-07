package uk.ac.ed.ph.ballviewer.math;

/** General 3d transform: rotation and translation combined. */
public class Transform {
	/** Rotation matrix. */
	public Matrix3 m;
	/** Translation vector. */
	public Vector3 t;
	
	/** Creates a 'do nothing' transform. */
	public Transform() { 
		m = new Matrix3();  //identity matrix
		t = new Vector3();  //zero vector
	}
	/** Creates a transform from a rotation matrix and a translation vector. */
	public Transform( Matrix3 ma, Vector3 tr) {
		m = ma; t = tr;
	}
	/** Represents a rotation by <code>a</code> around <code>axis</code> at the origin. */
	public static Transform rotation(Vector3 axis, double a) {
		return new Transform ( Matrix3.rotationAroundVector(axis,a), new Vector3());
	}
	/** Represents a translation by vector <code>tr</code>. */
	public static Transform translation(Vector3 tr) {
		return new Transform (new Matrix3(), tr);
	}
	/** Returns the inverse transform. */
	public Transform inverse() {
		Matrix3 inv = m.inverse();
		return new Transform ( inv, inv.appliedTo(this.t) );
	}
	/** Returns the result of applying this transform to vector <code>v</code>. */
	public Vector3 appliedTo (Vector3 v) {
		return this.m.appliedTo(v).plus(this.t);
	}
	/** Returns the result of applying 2 transforms in order. */
	public static Transform join (Transform t1, Transform t2) { 
		return new Transform ( Matrix3.mult(t1.m,t2.m), t1.appliedTo(t2.t) );
	}
}



