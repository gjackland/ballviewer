package uk.ac.ed.ph.ballviewer.math;

/** 
 * Represent a 3d square matrix. 
 * @see Transform
 */
public class Matrix3 {
	public double[][] r = new double[3][3];
	
	/** Creates a new identity (do nothing) matrix. */
	public Matrix3() {  
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) 
			r[i][j] = (i==j)? 1.0 : 0.0;   // ie R(ij) is delta(ij)
	}
	/** Creates a copy of matrix <code>m</code>. */
	public Matrix3(Matrix3 m) {
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) 
			r[i][j] = m.r[i][j];
	}
	/** Creates a new matrix out of 3 row vectors. */
	public Matrix3(Vector3 row0, Vector3 row1, Vector3 row2) {
		r[0][0]=row0.x; r[0][1]=row0.y; r[0][2]=row0.z;
		r[1][0]=row1.x; r[1][1]=row1.y; r[1][2]=row1.z;
		r[2][0]=row2.x; r[2][1]=row2.y; r[2][2]=row2.z;
	}
	public Matrix3( double r00, double r01, double r02,
						 double r10, double r11, double r12,
						 double r20, double r21, double r22) 
	{
		r[0][0]=r00; r[0][1]=r01; r[0][2]=r02;
		r[1][0]=r10; r[1][1]=r11; r[1][2]=r12;
		r[2][0]=r20; r[2][1]=r21; r[2][2]=r22;
	}
					

	public String toString() { 
		return  "{"+r[0][0]+","+r[0][1]+","+r[0][2]+"}\n{"
					+r[1][0]+","+r[1][1]+","+r[1][2]+"}\n{"
					+r[2][0]+","+r[2][1]+","+r[2][2]+"}\n";
	}
	public static Matrix3 getRotation(Vector3 fwd, Vector3 dn) { 
		Vector3 f = fwd.normalised();        //forward is positive z (crucial for camera)      
		Vector3 r = dn.cross(fwd).normalised(); //rt is positive x
		Vector3 d = fwd.cross(r).normalised();  //dn is positive y
		return new Matrix3(r,d,f).transposed();
	}
	/** 
	 * Returns a matrix that represents a rotation of <code>a</code> radians 
	 * around the vector <code>v</code>.
	 */
	public static Matrix3 rotationAroundVector( Vector3 v, double a ) {
		double cos = Math.cos(a), sin = Math.sin(a);
		v.normalise();
		double[] n = {v.x,v.y,v.z};
		Matrix3 m = new Matrix3();
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			m.r[i][j] = ((i==j)?(cos):0.0) + (1-cos)*n[i]*n[j];
		}
		m.r[0][1] += -n[2]*sin; m.r[1][0] += +n[2]*sin;
		m.r[1][2] += -n[0]*sin; m.r[2][1] += +n[0]*sin;
		m.r[2][0] += -n[1]*sin; m.r[0][2] += +n[1]*sin;
		return m;
	}
	/** Represents a rotation of <code>a</code> radians around z axis */
	public static Matrix3 rotationZ (double a) {
		double cos = Math.cos(a);
		double sin = Math.sin(a);
		Matrix3 m = new Matrix3();
		m.r[0][0]=cos; m.r[0][1]=-sin;
		m.r[1][0]=+sin; m.r[1][1]=cos;
		return m;
	}
	/** Represents a rotation of <code>a</code> radians around y axis */
	public static Matrix3 rotationY (double a) {
		double cos = Math.cos(a);
		double sin = Math.sin(a);
		Matrix3 m = new Matrix3();
		m.r[0][0]=cos; m.r[0][2]=+sin;
		m.r[2][0]=-sin; m.r[2][2]=cos;
		return m; 	}	
	/** Represents a rotation of <code>a</code> radians around x axis */
	public static Matrix3 rotationX (double a) {
		double cos = Math.cos(a);
		double sin = Math.sin(a);
		Matrix3 m = new Matrix3();
		m.r[1][1]=cos; m.r[1][2]=-sin;
		m.r[2][1]=+sin; m.r[2][2]=cos;
		return m;
	}
	
	/** Returns the result of applying this matrix to vector <code>v</code>. */
	public Vector3 appliedTo (Vector3 v) { 
		double x = r[0][0]*v.x + r[0][1]*v.y + r[0][2]*v.z;
		double y = r[1][0]*v.x + r[1][1]*v.y + r[1][2]*v.z;
		double z = r[2][0]*v.x + r[2][1]*v.y + r[2][2]*v.z;
		return new Vector3(x,y,z);
	}		

	/** Returns the product of 2 matrices. */		
	public static Matrix3 mult (Matrix3 m1, Matrix3 m2) { //this means multiply m1 by m2
		Matrix3 out = new Matrix3();
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			out.r[i][j] =0;
			for (int k=0; k<3; k++) { out.r[i][j] += m1.r[i][k]*m2.r[k][j]; }
		}
		return out;
	}
	/** Returns result of multiplying by <code>m</code> in front (<code>mult(m,this)</code>). */
	public Matrix3 fmult(Matrix3 m) { return mult(m,this);	}	// multiply at front
	/** Returns result of multiplying by <code>m</code> behind (<code>mult(this,m)</code>). */
	public Matrix3 bmult(Matrix3 m) { return mult(this,m);	}	// multiply at back
	
	/** Returns the inverse matrix (assumes it is orthonormal). */
	public Matrix3 inverse() { return transposed(); } 
	public double determinant() {
		return r[0][0]*(r[1][1]*r[2][2]-r[1][2]*r[2][1])
			  + r[0][1]*(r[1][2]*r[2][0]-r[1][0]*r[2][2])
			  + r[0][2]*(r[1][0]*r[2][1]-r[1][1]*r[2][0]);
	}
	/** Routine for eliminating cumulative errors (probably unnecessary). */
	public void orthonormalise() {    // this routine should erase any cumulative errors
		Vector3 row1 = new Vector3(r[1][0],r[1][1],r[1][2]);
		Vector3 row2 = new Vector3(r[2][0],r[2][1],r[2][2]); //keeps z direction 
		row1.normalise(); row2.normalise();
		
		Vector3 row0=row1.cross(row2).normalised(); //ensures that row0 orthonormal to row1, row2
		row1=row2.cross(row0).normalised();			  //ensures that row2 is also o/n to row1
		
		r[0][0]=row0.x; r[0][1]=row0.y; r[0][2]=row0.z; // same as for Matrix3(v,v,v)
		r[1][0]=row1.x; r[1][1]=row1.y; r[1][2]=row1.z;
		r[2][0]=row2.x; r[2][1]=row2.y; r[2][2]=row2.z;	
	}
	// Returns transpose matrix. //
	public Matrix3 transposed() {
		Matrix3 t = new Matrix3();
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			t.r[i][j] = r[j][i];
		}
		return t;
	}

}
